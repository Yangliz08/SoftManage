# Reference Verification Tool — `verify_references.py`

A lightweight CLI script that checks a list of bibliographic references against
public data sources and produces a Markdown report, a JSON summary, and a TSV
export.

---

## Requirements

- Python ≥ 3.9
- Install dependencies:

```bash
pip install -r requirements.txt
```

---

## Input formats

### 1. Plain-text file (one reference per line)

```
Smith, J. (2023). My paper title. Journal Name, 10(2), 100-120.
孙明. 面向中小企业的轻量化协作平台的设计与实现. 电子技术与软件工程，2024(3)：55-62.
```

### 2. JSON file

```json
[
  {
    "id": 1,
    "authors": "孙明",
    "title": "面向中小企业的轻量化协作平台的设计与实现",
    "venue": "电子技术与软件工程",
    "year": 2024,
    "volume": null,
    "pages": "55-62",
    "doi": null
  }
]
```

Required fields: `id`, `title`.  
Optional fields: `authors`, `venue`, `year`, `volume`, `pages`, `doi`.

---

## Usage

```bash
# From a plain-text list
python verify_references.py --input refs.txt --format text

# From a JSON file
python verify_references.py --input refs.json --format json

# Specify output directory (default: ./refcheck_output)
python verify_references.py --input refs.json --format json --output ./my_output

# Increase verbosity
python verify_references.py --input refs.json --format json --verbose
```

Output files are written to the `--output` directory:

| File | Description |
|------|-------------|
| `report.md` | Human-readable Markdown report |
| `results.json` | Machine-readable JSON with all findings |
| `results.tsv` | TSV for easy import into Excel / Google Sheets |

---

## Environment variables

| Variable | Required | Description |
|----------|----------|-------------|
| `CROSSREF_EMAIL` | Recommended | Polite pool e-mail for CrossRef API (higher rate limits). Set to your institutional e-mail. |
| `BING_API_KEY` | Optional | Azure Cognitive Services Bing Search v7 API key. When set, web-search evidence links are fetched; when absent the Bing step is skipped with a warning. |

```bash
export CROSSREF_EMAIL="researcher@university.edu"
export BING_API_KEY="<your-bing-key>"
python verify_references.py --input refs.json --format json
```

---

## Data sources & rate-limiting

| Source | Endpoint | Rate limit |
|--------|----------|------------|
| CrossRef REST API | `https://api.crossref.org/works` | ~50 req/s polite pool, 3 req/s anonymous |
| Bing Web Search v7 | `https://api.bing.microsoft.com/v7.0/search` | Depends on subscription tier |

The script inserts a **1-second delay** between CrossRef requests to stay within
the anonymous rate limit.  Set `CROSSREF_EMAIL` to use the polite pool (50 req/s).

---

## Further steps / TODOs

1. **CNKI / 知网 checks** — Chinese journals (refs 1 & 4 in the sample list) are
   not indexed by CrossRef or Bing adequately.  CNKI offers an official API for
   institutional subscribers; add a `CnkiChecker` class that queries
   `https://kns.cnki.net/kns8/defaultresult/index` with the article title.
   Credentials (institution token) must be stored in a `CNKI_TOKEN` env var.

2. **Scopus / Web of Science checks** — Add a `ScopusChecker` that uses the
   Elsevier Scopus API (`https://api.elsevier.com/content/search/scopus`).
   Requires a `SCOPUS_API_KEY` env var (subscription required).

3. **Manual PDF attachment** — For references marked `found: false`, export a
   `needs_manual_check.txt` file containing DOI/title and a suggested Google
   Scholar or Sci-Hub query.  Attach PDFs to the GitHub issue or store them in
   `docs/pdfs/` (add `docs/pdfs/` to `.gitignore` to keep the repo clean).

4. **Institutional proxy** — Some publisher pages (Springer, ScienceDirect, IEEE
   Xplore) require login.  Integrate EZproxy URL rewriting or set a
   `PROXY_URL` env var to route requests through an institutional proxy.
