const { test: base, expect } = require("@playwright/test")

const laptops = Array.from({ length: 13 }, (_, index) => {
  const price = [799, 999, 1199, 1399, 1599, 1899, 2199, 2499, 699, 899, 1099, 1299, 1499][index]
  const brands = ["Acer", "Apple", "Asus", "Dell", "HP"]
  const brand = brands[index % brands.length]
  return {
    brand,
    brandName: brand,
    product: `${brand} Laptop ${index + 1}`,
    price: `$${price}`,
    processor: index % 2 ? "Intel Core i7" : "Apple M2",
    memory: index % 3 ? "16 GB" : "8 GB",
    storage: index % 2 ? "512 GB SSD" : "256 GB SSD",
    graphics: index % 2 ? "Intel UHD" : "NVIDIA RTX",
    display: index % 2 ? '15"' : '14"',
    os: index % 2 ? "Windows 11" : "macOS",
    image: "/placeholder.svg",
  }
})

async function mockApi(page, options = {}) {
  await page.route("**/WebApi", async (route) => {
    const request = route.request()
    let body = {}
    try {
      body = JSON.parse(request.postData() || "{}")
    } catch {
      body = {}
    }

    if (options.failSearch && body.method === "SearchProduct") {
      await route.fulfill({ status: 503, contentType: "application/json", body: JSON.stringify({ error: "Service unavailable" }) })
      return
    }

    let response
    switch (body.method) {
      case "SearchProduct":
        response = options.emptySearch ? ["error: no laptops found"] : laptops
        break
      case "WordCompletion":
        response = { result: ["apple", "apple macbook", "apple laptop"] }
        break
      case "spellCheck":
        response = { result: body.spelling === "appl" ? ["appl: apple"] : [] }
        break
      case "getTop5SearchedWords":
        response = { result: [{ word: "apple", count: 12 }, { word: "dell", count: 8 }] }
        break
      case "getWordFrequency":
        response = { word: body.spelling || "apple", occurrence: 3 }
        break
      case "increaseSearchFrequencyCount":
        response = { result: true }
        break
      default:
        response = { result: {} }
    }

    await route.fulfill({ status: 200, contentType: "application/json", body: JSON.stringify(response) })
  })
}

const test = base.extend({
  mockApi: async ({ page }, use) => {
    await mockApi(page)
    await use(page)
  },
})

module.exports = { test, expect, laptops, mockApi }
