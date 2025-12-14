// src/api/laptopService.js
import axios from "axios"

const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080"

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
})

// ---- MAIN SEARCH ----
export const searchLaptops = async (query, filters, sortBy, page = 1, pageSize = 12) => {
  const payload = {
    query,
    filters,
    sortBy,
    page,
    pageSize,
  }

  const res = await api.post("/api/laptops/search", payload)
  // backend should return { laptops: [...], pagination: {...} }
  return res.data
}

// ---- SEARCH / WORD FREQUENCY ----
export const getSearchFrequency = async () => {
  const res = await api.get("/api/analytics/search-frequency")
  return res.data
}

export const getWordFrequency = async (query) => {
  const res = await api.get("/api/analytics/word-frequency", {
    params: { q: query },
  })
  return res.data
}

export const increaseSearchFrequencyCount = async (query) => {
  try {
    await api.post("/api/analytics/search-frequency", { query })
  } catch (e) {
    // non‑critical; ignore failure
    console.warn("Failed to increase search frequency", e)
  }
}

// ---- AUTOCOMPLETE & SPELLCHECK ----
export const getAutocompleteSuggestions = async (partial) => {
  const res = await api.get("/api/laptops/autocomplete", {
    params: { q: partial },
  })
  return res.data
}

export const spellCheckQuery = async (query) => {
  const res = await api.get("/api/laptops/spellcheck", {
    params: { q: query },
  })
  return res.data
}

// optional default export for legacy imports
const laptopService = {
  searchLaptops,
  getSearchFrequency,
  getWordFrequency,
  increaseSearchFrequencyCount,
  getAutocompleteSuggestions,
  spellCheckQuery,
}

export default laptopService
