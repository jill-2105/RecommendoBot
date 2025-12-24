# RecommendoBot – Laptop Recommendation Web App

**A full‑stack laptop recommendation system combining classical IR algorithms with a lightweight Java HTTP API and a React frontend.**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![React 19](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![Vercel](https://img.shields.io/badge/Frontend-Vercel-black.svg)](https://vercel.com/)
[![Railway](https://img.shields.io/badge/Backend-Railway-purple.svg)](https://railway.app/)

---

## 💻 Project Overview

RecommendoBot lets users search and filter laptops across brands, specs, and price, backed by classic IR components like inverted index, ranking, spell‑checking, autocomplete, and query‑frequency tracking.

## 🚀 Key Features

*   **🔍 Advanced Search & Filtering**: Powered by custom **Inverted Index** and **PageRank** algorithms to rank laptops by relevance, featuring granular filters (Brand, RAM, Price) and pagination.
*   **✨ Smart Spell Checking**: A **Trie-based** spell checker that automatically corrects typos (e.g., "dell" instead of "del") to ensure accurate results even with misspelled queries.
*   **⌨️ Real-Time Autocomplete**: Instant word completion suggestions using optimized **Trie** data structures for a seamless search experience.
*   **📊 Search Analytics**: Tracks real-time search trends and word frequency using **AVL Trees** to display the most popular keywords and user insights.
*   **🕷️ Intelligent Web Crawler**: Scrapes manufacturer support pages to extract contact details (Emails, Phones, URLs) using **Jsoup** and **Regex** pattern matching.
*   **⚔️ Laptop Comparison Tool**: Side-by-side specification comparison for up to 3 laptops to help users make informed decisions.
*   **⚡ Lightweight Architecture**: A high-performance system built with a raw **Java HTTP API** (no Spring overhead) and a modern **React SPA** frontend.
.

### 🌐 Quick Links

- **Live Demo:** [Click Here](https://recommendo-bot.vercel.app/)

- **Dataset:** `all_laptops_data.csv` in data folder.

---

## 🏗️ Architecture

### High‑Level Flow

```text
React Frontend (Vercel) ──▶ Java WebInterface API (Railway) 
                                   
              ──▶ Algorithms / Data
                ├── Inverted Index + Ranking
                ├── Spell Checking (Trie)
                ├── Word Completion (Trie)
                ├── Search Frequency (AVL + file)
                └── Crawler (Jsoup + regex)
```

**Components:**
- **Frontend:** React (Create React App), calling a single RPC‑style endpoint `/WebApi`.
- **Backend:** Java `HttpServer` on port `8080`, dispatching by `method` field in JSON body.
- **Algorithms layer:** Plain Java packages for ranking, crawling, spell check, word completion, frequency.

---

## 📁 Repository Layout

- `src/`  
  - `laptoprecommendation/WebInterface.java` – main HTTP server (`POST /WebApi`).  
  - `laptoprecommendation/Features.java` – routes methods to underlying algorithms.  
  - `Crawler/` – Jsoup‑based crawler + HTML→text + regex extraction.  
  - `InvertedIndex/` – builds/searches inverted index from CSV data.  
  - `FrequencyFinder/` – frequency and occurrence utilities.  
  - `pageRanking/` – ranking logic + `Laptop` DTO.  
  - `spellcheckingusingtrie/` – trie‑based spell checker.  
  - `Wordcompletion/` – trie‑based autocomplete.  
  - `SearchFrequency/` – AVL‑tree based search‑frequency tracker.
- `frontend/` – React SPA (search UI, filters, crawler page, charts).  
- `lib/` – external jars (`gson`, `jsoup`, etc.) required at runtime.  
- `data/` – supporting input data (if any).  
- `all_laptops_data.csv` – core laptop dataset loaded at startup.  
- `saved_pages/`, `text_pages/` – crawler outputs (gitignored).

---

## ⚙️ Technology Stack

### Backend

- **Language:** Java 17  
- **HTTP Server:** `com.sun.net.httpserver.HttpServer`  
- **Algorithms:** Custom implementations (Trie, AVL tree, inverted index, PageRank‑style ranking).  
- **Libraries:** Gson for JSON, Jsoup for HTML fetching/parsing.

### Frontend

- **Framework:** React (Create React App)  
- **State & Data:** Fetch API + custom service layer (`laptopService`, crawler page).  
- **Styling:** CSS modules / component‑level styles.  
- **Deployment:** Vercel, configured with `REACT_APP_API_BASE_URL` pointing to Railway.

### Infrastructure

- **Backend Hosting:** Railway (Java Nixpack, custom start command).  
- **Frontend Hosting:** Vercel (connected to `main` branch).  
- **Environment Variables:**  
  - `REACT_APP_API_BASE_URL` – base URL for backend (`https://...up.railway.app`).

---

## 🚀 Running the Project

### 1️⃣ Backend – Local (port 8080)

```bash
# From repo root

# Compile to ./bin with external jars
mkdir -p bin
javac -cp ".:lib/*" -d bin $(find src -name "*.java")

# Run the API server
java -cp ".:bin:lib/*" laptoprecommendation.WebInterface
# Available at: http://localhost:8080/WebApi
```

### 2️⃣ Frontend – Local (port 3000)

```bash
cd frontend
npm install

# Ensure .env.development has:
# REACT_APP_API_BASE_URL=http://localhost:8080
npm start
# Open http://localhost:3000
```

### 3️⃣ Production Configuration

- **Railway:** `PORT=8080`, start command compiles and runs `laptoprecommendation.WebInterface`.  
- **Vercel:** environment variable `REACT_APP_API_BASE_URL=https://<railway-service>.up.railway.app`.

---

## 🔌 API Surface (`POST /WebApi`)

All requests use JSON and a `method` field:

- `SearchProduct` – search laptops by spelling/query.  
- `spellCheck` – return spelling suggestions.  
- `WordCompletion` – autocomplete suggestions.  
- `crawlAndExtract` – crawl contact pages and extract phone/email/URLs.  
- `increaseSearchFrequencyCount` – increment search term counts.  
- `getTop5SearchedWords` – top N searched keywords.  
- `getWordFrequency` – frequency of a term in the dataset.

Example:

```json
{ "method": "SearchProduct", "spelling": "dell" }
```

Response: JSON array of laptops.

---

## 🧪 Testing & Troubleshooting

- Verify backend: `curl -X POST http://localhost:8080/WebApi -H "Content-Type: application/json" -d '{"method":"SearchProduct","spelling":"dell"}'`.  
- If frontend fails in production:
  - Check Network tab for calls to the Railway URL.  
  - Confirm `REACT_APP_API_BASE_URL` is set correctly in Vercel.
---