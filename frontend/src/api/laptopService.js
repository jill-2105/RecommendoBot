const API_URL = `${process.env.REACT_APP_API_BASE_URL}/WebApi`;

// Utility delay for simulating real-world delay (optional)
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

// 🔍Fetch laptops using API

export const fetchLaptopsFromApi = async (query = "") => {
  query = (query ?? "").toString();
  const settings = {
    method: "POST",
    headers: {
      "Content-Type": "text/plain",
    },
    body: JSON.stringify({
      spelling: `${query}`, // Send dynamic query here
      method: "SearchProduct",
    }),
  };

  try {
    const response = await fetch(API_URL, settings);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const data = await response.json();

    console.log("Laptop API response:", data); // Debug log

    // Check for ["error: message"] format
    if (
      Array.isArray(data) &&
      data.length === 1 &&
      typeof data[0] === "string" &&
      data[0].toLowerCase().startsWith("error:")
    ) {
      console.warn("API error:", data[0]);
      return [];
    }

    return data;
  } catch (error) {
    console.error("Failed to fetch laptops from API:", error);
    return [];
  }
};

// Word Completion API call
export const getAutocompleteSuggestions = async (prefix) => {
  const settings = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      method: "WordCompletion",
      prefix,
    }),
  };

  try {
    const response = await fetch(API_URL, settings);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const data = await response.json();
    return data.result || [];
  } catch (error) {
    console.error("WordCompletion failed:", error);
    return [];
  }
};


// Spell Check API call
export const spellCheckQuery = async (spelling) => {
  const settings = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      method: "spellCheck",
      spelling,
    }),
  };

  try {
    const response = await fetch(API_URL, settings);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const data = await response.json();

    // Return empty array if result is empty, but do NOT treat as error
    if (Array.isArray(data.result)) {
      return data.result;
    }

    // Fallback: if data.result is not an array
    return ["error: Invalid response format"];
  } catch (error) {
    console.error("Spell check failed:", error);
    return ["error: Request failed"];
  }
};

// Main Search Logic (with filters, sorting, pagination)
export const searchLaptops = async (
  query,
  filters,
  sortBy,
  page = 1,
  limit = 12
) => {
  await delay(500); // Optional simulated delay

  console.log("Searching laptops with query:", query);

  const allLaptops = await fetchLaptopsFromApi(query);
  let results = [...allLaptops];

  // Apply filters
  if (filters.brands?.length) {
    results = results.filter((laptop) => {
      const brand = (laptop.brandName ?? laptop.brand ?? "")
        .toString()
        .trim();
      return filters.brands.includes(brand);
    });
  }

  if (filters.ram?.length) {
    results = results.filter((laptop) => {
      const mem = (laptop.memory ?? "").toString().toLowerCase();
      return filters.ram.some((r) => mem.includes(r.toLowerCase()));
    });
  }

  if (filters.storage?.length) {
    results = results.filter((laptop) => {
      const storage = (laptop.storage ?? "").toString().toLowerCase();
      return filters.storage.some((s) => {
        const token = s.toString().toLowerCase();
        return storage.includes(token);
      });
    });
  }

  if (filters.display?.length) {
    results = results.filter((laptop) => {
      const display = (laptop.display ?? "").toString();
      const match = display.match(/(\d+(?:\.\d+)?)/);
      if (!match) return false;
      const inches = Number.parseFloat(match[1]);
      return filters.display.some((d) => {
        const selected = Number.parseFloat(d.replace(/[^0-9.]/g, ""));
        return (
          Number.isFinite(selected) && Math.abs(inches - selected) < 0.6
        );
      });
    });
  }

  if (filters.graphics?.length) {
    results = results.filter((laptop) =>
      filters.graphics.some((g) =>
        laptop.graphics?.toLowerCase().includes(g.toLowerCase())
      )
    );
  }

  if (filters.priceRange?.length === 2) {
    results = results.filter((laptop) => {
      const price = Number.parseInt(
        laptop.price.replace("$", "").replace(",", "")
      );
      return (
        price >= filters.priceRange[0] && price <= filters.priceRange[1]
      );
    });
  }

  // Sorting
  if (sortBy === "price-low-high") {
    results.sort(
      (a, b) =>
        parseInt(a.price.replace("$", "").replace(",", "")) -
        parseInt(b.price.replace("$", "").replace(",", ""))
    );
  } else if (sortBy === "price-high-low") {
    results.sort(
      (a, b) =>
        parseInt(b.price.replace("$", "").replace(",", "")) -
        parseInt(a.price.replace("$", "").replace(",", ""))
    );
  }

  // Pagination
  const totalItems = results.length;
  const totalPages = Math.ceil(totalItems / limit);
  const startIndex = (page - 1) * limit;
  const endIndex = startIndex + limit;
  const paginatedResults = results.slice(startIndex, endIndex);

  return {
    laptops: paginatedResults,
    pagination: {
      currentPage: page,
      totalPages,
      totalItems,
      itemsPerPage: limit,
      hasNextPage: page < totalPages,
      hasPrevPage: page > 1,
    },
  };
};

//Search frequency helpers
export const increaseSearchFrequencyCount = async (word) => {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      method: "increaseSearchFrequencyCount",
      word,
    }),
  });

  if (!response.ok) throw new Error("Failed to fetch search frequency");
  const data = await response.json();
  return data.result; // Adjust depending on your backend response structure
};

export const getSearchFrequency = async () => {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ method: "getTop5SearchedWords" }),
  });

  if (!response.ok) throw new Error("Failed to fetch word frequency");
  const data = await response.json();

  const freqMap = {};
  if (Array.isArray(data.result)) {
    data.result.forEach(({ word, count }) => {
      freqMap[word] = Number(count);
    });
  }
  return freqMap;
};

export const getWordFrequency = async (spelling) => {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ method: "getWordFrequency", spelling }),
  });

  if (!response.ok) throw new Error("Failed to fetch word frequency");

  const data = await response.json();

  if (data.word && data.occurrence !== undefined) {
    return { [data.word]: Number(data.occurrence) }; // return as object
  } else {
    throw new Error("Invalid response format");
  }
};
