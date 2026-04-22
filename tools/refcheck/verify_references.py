#!/usr/bin/env python3
"""
verify_references.py
====================
Reference-verification script for SoftManage project.

Accepts a plain-text list of references (one per line) or a JSON file with
structured entries and attempts to verify each reference via:
  1. CrossRef REST API (DOI / metadata lookup)
  2. Bing Web Search v7 (optional, requires BING_API_KEY env var)

Produces:
  • report.md   — Markdown summary
  • results.json — machine-readable JSON
  • results.tsv  — tab-separated for spreadsheet import

Usage:
  python verify_references.py --input refs.txt  --format text
  python verify_references.py --input refs.json --format json [--output ./out]

Environment variables:
  CROSSREF_EMAIL  — polite-pool e-mail for CrossRef (recommended)
  BING_API_KEY    — Azure Bing Search v7 key (optional)
"""

from __future__ import annotations

import argparse
import json
import os
import sys
import time
import urllib.parse
from dataclasses import dataclass, field, asdict
from pathlib import Path
from typing import Any

import requests

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

CROSSREF_BASE = "https://api.crossref.org/works"
BING_SEARCH_BASE = "https://api.bing.microsoft.com/v7.0/search"
REQUEST_DELAY = 1.0  # seconds between CrossRef requests (polite pool)
HTTP_TIMEOUT = 15    # seconds

CROSSREF_EMAIL = os.environ.get("CROSSREF_EMAIL", "")
BING_API_KEY = os.environ.get("BING_API_KEY", "")


# ---------------------------------------------------------------------------
# Data models
# ---------------------------------------------------------------------------

@dataclass
class Reference:
    id: int
    raw: str = ""
    authors: str = ""
    title: str = ""
    venue: str = ""
    year: int | None = None
    volume: str = ""
    pages: str = ""
    doi: str = ""


@dataclass
class FindingMatch:
    title: str = ""
    authors: str = ""
    year: int | None = None
    doi: str = ""
    url: str = ""


@dataclass
class Finding:
    ref_id: int
    found: bool = False
    confidence: str = "low"
    match: FindingMatch = field(default_factory=FindingMatch)
    evidence_links: list[str] = field(default_factory=list)
    notes: str = ""
    crossref_status: str = "not_checked"
    bing_status: str = "not_checked"


# ---------------------------------------------------------------------------
# Input parsing
# ---------------------------------------------------------------------------

def parse_text_input(path: Path) -> list[Reference]:
    refs: list[Reference] = []
    lines = path.read_text(encoding="utf-8").splitlines()
    idx = 1
    for line in lines:
        line = line.strip()
        if not line:
            continue
        # Strip leading "N." numbering if present
        if line[0].isdigit():
            dot = line.find(".")
            if dot != -1 and dot < 4:
                line = line[dot + 1:].strip()
        refs.append(Reference(id=idx, raw=line, title=line))
        idx += 1
    return refs


def parse_json_input(path: Path) -> list[Reference]:
    data: list[dict[str, Any]] = json.loads(path.read_text(encoding="utf-8"))
    refs: list[Reference] = []
    for item in data:
        ref = Reference(
            id=item.get("id", len(refs) + 1),
            raw=json.dumps(item, ensure_ascii=False),
            authors=item.get("authors", ""),
            title=item.get("title", ""),
            venue=item.get("venue", ""),
            year=item.get("year"),
            volume=str(item.get("volume", "") or ""),
            pages=item.get("pages", ""),
            doi=item.get("doi", ""),
        )
        refs.append(ref)
    return refs


# ---------------------------------------------------------------------------
# CrossRef checker
# ---------------------------------------------------------------------------

def _crossref_headers() -> dict[str, str]:
    headers = {"Accept": "application/json"}
    if CROSSREF_EMAIL:
        headers["User-Agent"] = f"SoftManage-RefCheck/1.0 (mailto:{CROSSREF_EMAIL})"
    return headers


def check_crossref(ref: Reference, verbose: bool = False) -> tuple[bool, FindingMatch, list[str], str]:
    """Query CrossRef for the reference.  Returns (found, match, links, note)."""
    links: list[str] = []
    note = ""

    # If we have a DOI, try a direct DOI lookup first.
    if ref.doi:
        url = f"{CROSSREF_BASE}/{urllib.parse.quote(ref.doi, safe='')}"
        try:
            resp = requests.get(url, headers=_crossref_headers(), timeout=HTTP_TIMEOUT)
            if resp.status_code == 200:
                msg = resp.json().get("message", {})
                return True, _extract_match(msg), [url], "DOI lookup succeeded."
        except requests.RequestException as exc:
            note = f"DOI lookup error: {exc}"

    # Fall back to title query.
    if not ref.title:
        return False, FindingMatch(), links, "No title available for query."

    params: dict[str, Any] = {
        "query.title": ref.title,
        "rows": 3,
        "select": "DOI,title,author,published,container-title,URL",
    }
    if ref.authors:
        params["query.author"] = ref.authors
    query_url = f"{CROSSREF_BASE}?{urllib.parse.urlencode(params)}"
    links.append(query_url)

    try:
        resp = requests.get(query_url, headers=_crossref_headers(), timeout=HTTP_TIMEOUT)
        resp.raise_for_status()
    except requests.RequestException as exc:
        return False, FindingMatch(), links, f"CrossRef request failed: {exc}"

    items = resp.json().get("message", {}).get("items", [])
    if not items:
        return False, FindingMatch(), links, "No CrossRef results returned."

    best = items[0]
    match = _extract_match(best)

    # Simple confidence check: title overlap ≥ 50%
    result_title = match.title.lower()
    query_title = ref.title.lower()
    overlap = _title_overlap(query_title, result_title)

    if overlap >= 0.5:
        return True, match, links, f"Title overlap {overlap:.0%} (CrossRef)."
    else:
        note = (
            f"Best CrossRef result title overlap only {overlap:.0%}; "
            "may be a different work. Manual verification recommended."
        )
        return False, match, links, note


def _extract_match(msg: dict[str, Any]) -> FindingMatch:
    title_list = msg.get("title", [])
    title = title_list[0] if title_list else ""
    authors_raw = msg.get("author", [])
    authors = "; ".join(
        f"{a.get('family', '')} {a.get('given', '')}".strip() for a in authors_raw
    )
    pub = msg.get("published", {})
    date_parts = pub.get("date-parts", [[None]])
    year = date_parts[0][0] if date_parts and date_parts[0] else None
    doi = msg.get("DOI", "")
    url = msg.get("URL", f"https://doi.org/{doi}" if doi else "")
    return FindingMatch(title=title, authors=authors, year=year, doi=doi, url=url)


def _title_overlap(a: str, b: str) -> float:
    words_a = set(a.split())
    words_b = set(b.split())
    if not words_a:
        return 0.0
    return len(words_a & words_b) / len(words_a)


# ---------------------------------------------------------------------------
# Bing Web Search checker
# ---------------------------------------------------------------------------

def check_bing(ref: Reference, verbose: bool = False) -> tuple[list[str], str]:
    """Return (evidence_links, note). Skipped when BING_API_KEY is unset."""
    if not BING_API_KEY:
        return [], "Bing search skipped (BING_API_KEY not set)."

    query_parts = []
    if ref.title:
        query_parts.append(f'"{ref.title}"')
    if ref.authors:
        query_parts.append(ref.authors.split(",")[0].strip())
    if ref.year:
        query_parts.append(str(ref.year))
    query = " ".join(query_parts)

    params = {"q": query, "count": 3, "mkt": "en-US"}
    headers = {"Ocp-Apim-Subscription-Key": BING_API_KEY}

    try:
        resp = requests.get(
            BING_SEARCH_BASE, params=params, headers=headers, timeout=HTTP_TIMEOUT
        )
        resp.raise_for_status()
    except requests.RequestException as exc:
        return [], f"Bing request failed: {exc}"

    web_pages = resp.json().get("webPages", {}).get("value", [])
    links = [page.get("url", "") for page in web_pages if page.get("url")]
    note = f"Bing returned {len(links)} result(s)." if links else "Bing returned no results."
    return links, note


# ---------------------------------------------------------------------------
# Verification orchestration
# ---------------------------------------------------------------------------

def verify_reference(ref: Reference, verbose: bool = False) -> Finding:
    finding = Finding(ref_id=ref.id)

    if verbose:
        print(f"  [CrossRef] id={ref.id} title={ref.title[:60]!r}")

    found, match, cr_links, cr_note = check_crossref(ref, verbose)
    finding.found = found
    finding.match = match
    finding.evidence_links.extend(l for l in cr_links if l)
    finding.crossref_status = "found" if found else "not_found"
    finding.notes += cr_note + " "

    time.sleep(REQUEST_DELAY)

    if verbose:
        print(f"  [Bing]     id={ref.id}")

    bing_links, bing_note = check_bing(ref, verbose)
    finding.bing_status = "found" if bing_links else "no_results"
    finding.evidence_links.extend(bing_links)
    finding.notes += bing_note

    # Determine overall confidence
    if finding.found and bing_links:
        finding.confidence = "high"
    elif finding.found:
        finding.confidence = "medium"
    elif bing_links:
        finding.confidence = "low"
    else:
        finding.confidence = "none"

    # Add notes for Chinese references
    if any(0x4E00 <= ord(c) <= 0x9FFF for c in ref.title):
        finding.notes += (
            " Note: Chinese-language reference — recommend verifying via "
            "CNKI/知网 (requires institutional credentials)."
        )

    finding.notes = finding.notes.strip()
    return finding


def verify_all(refs: list[Reference], verbose: bool = False) -> list[Finding]:
    findings: list[Finding] = []
    total = len(refs)
    for i, ref in enumerate(refs, 1):
        if verbose:
            print(f"\n[{i}/{total}] Checking reference #{ref.id} …")
        finding = verify_reference(ref, verbose)
        findings.append(finding)
    return findings


# ---------------------------------------------------------------------------
# Report generation
# ---------------------------------------------------------------------------

STATUS_EMOJI = {True: "✅", False: "❌"}
CONFIDENCE_LABEL = {
    "high": "🟢 High",
    "medium": "🟡 Medium",
    "low": "🟠 Low",
    "none": "🔴 None",
}


def generate_markdown(refs: list[Reference], findings: list[Finding]) -> str:
    ref_map = {r.id: r for r in refs}
    lines: list[str] = [
        "# Reference Verification Report",
        "",
        "Generated by `tools/refcheck/verify_references.py`.",
        "",
        "## Summary",
        "",
    ]

    found_count = sum(1 for f in findings if f.found)
    lines += [
        f"- **Total references checked:** {len(findings)}",
        f"- **Found / confirmed:** {found_count}",
        f"- **Not found / needs follow-up:** {len(findings) - found_count}",
        "",
        "---",
        "",
        "## Per-Reference Results",
        "",
    ]

    for finding in findings:
        ref = ref_map.get(finding.ref_id)
        raw = ref.raw if ref else f"Reference #{finding.ref_id}"
        lines += [
            f"### [{finding.ref_id}] {STATUS_EMOJI[finding.found]} "
            f"Confidence: {CONFIDENCE_LABEL.get(finding.confidence, finding.confidence)}",
            "",
            f"**Input:** {raw}",
            "",
        ]
        if finding.match and (finding.match.title or finding.match.doi):
            m = finding.match
            lines += [
                "**Best match found:**",
                "",
                f"| Field | Value |",
                f"|-------|-------|",
                f"| Title | {m.title or '—'} |",
                f"| Authors | {m.authors or '—'} |",
                f"| Year | {m.year or '—'} |",
                f"| DOI | {m.doi or '—'} |",
                f"| URL | {m.url or '—'} |",
                "",
            ]
        if finding.evidence_links:
            lines.append("**Evidence links:**")
            lines.append("")
            for lnk in finding.evidence_links:
                lines.append(f"- {lnk}")
            lines.append("")
        lines += [
            f"**Notes:** {finding.notes}",
            "",
            "---",
            "",
        ]

    lines += [
        "## Further Steps",
        "",
        "1. **CNKI / 知网**: References 1 and 4 are Chinese-language journal articles not "
        "indexed by CrossRef. Verify via [CNKI](https://www.cnki.net/) using institutional credentials "
        "(set `CNKI_TOKEN` env var once the API is integrated).",
        "2. **Scopus / Web of Science**: Add `ScopusChecker` class using the "
        "[Elsevier Scopus API](https://dev.elsevier.com/) (`SCOPUS_API_KEY` required, subscription needed).",
        "3. **Manual PDF verification**: For references still marked _not found_, "
        "download PDFs and place them in `docs/pdfs/` (gitignored). Attach to the GitHub issue for "
        "team review.",
        "4. **Publisher site search**: Add targeted checks against Springer "
        "(`https://link.springer.com`), ScienceDirect (`https://www.sciencedirect.com`), and "
        "IEEE Xplore (`https://ieeexplore.ieee.org`) using their public search APIs.",
        "",
    ]
    return "\n".join(lines)


def generate_tsv(refs: list[Reference], findings: list[Finding]) -> str:
    ref_map = {r.id: r for r in refs}
    header = "\t".join([
        "id", "found", "confidence",
        "input_title", "matched_title", "matched_authors",
        "matched_year", "matched_doi", "matched_url",
        "crossref_status", "bing_status", "notes",
    ])
    rows = [header]
    for f in findings:
        ref = ref_map.get(f.ref_id)
        input_title = ref.title if ref else ""
        m = f.match
        row = "\t".join([
            str(f.ref_id),
            str(f.found),
            f.confidence,
            input_title,
            m.title if m else "",
            m.authors if m else "",
            str(m.year) if m and m.year else "",
            m.doi if m else "",
            m.url if m else "",
            f.crossref_status,
            f.bing_status,
            f.notes.replace("\t", " "),
        ])
        rows.append(row)
    return "\n".join(rows)


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(
        description="Verify bibliographic references against CrossRef and Bing."
    )
    p.add_argument("--input", required=True, help="Path to input file (text or JSON).")
    p.add_argument(
        "--format",
        choices=["text", "json"],
        default="text",
        help="Input format: 'text' (one ref per line) or 'json'.",
    )
    p.add_argument(
        "--output",
        default="./refcheck_output",
        help="Output directory for report files (default: ./refcheck_output).",
    )
    p.add_argument("--verbose", action="store_true", help="Print progress to stdout.")
    return p


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()

    input_path = Path(args.input)
    if not input_path.exists():
        print(f"ERROR: Input file not found: {input_path}", file=sys.stderr)
        sys.exit(1)

    output_dir = Path(args.output)
    output_dir.mkdir(parents=True, exist_ok=True)

    # Parse input
    if args.format == "json":
        refs = parse_json_input(input_path)
    else:
        refs = parse_text_input(input_path)

    if not refs:
        print("No references found in input file.", file=sys.stderr)
        sys.exit(1)

    print(f"Loaded {len(refs)} reference(s).  Starting verification …")
    if not CROSSREF_EMAIL:
        print("  ⚠  CROSSREF_EMAIL not set — using anonymous CrossRef rate limit (3 req/s).")
    if not BING_API_KEY:
        print("  ⚠  BING_API_KEY not set — Bing evidence links will be skipped.")

    findings = verify_all(refs, verbose=args.verbose)

    # Write outputs
    md_path = output_dir / "report.md"
    md_path.write_text(generate_markdown(refs, findings), encoding="utf-8")
    print(f"✅ Markdown report: {md_path}")

    json_path = output_dir / "results.json"
    json_path.write_text(
        json.dumps([asdict(f) for f in findings], ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    print(f"✅ JSON results:    {json_path}")

    tsv_path = output_dir / "results.tsv"
    tsv_path.write_text(generate_tsv(refs, findings), encoding="utf-8")
    print(f"✅ TSV results:     {tsv_path}")

    found = sum(1 for f in findings if f.found)
    print(f"\nDone. {found}/{len(findings)} reference(s) confirmed.")


if __name__ == "__main__":
    main()
