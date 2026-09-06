#!/usr/bin/env python3
"""Build the V3 destination JSON and MySQL seed from the supplied district list."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from urllib.parse import quote


DISTRICT_IMAGES = {
    "Colombo": "Galle Face, Colombo, Sri Lanka - panoramio.jpg",
    "Gampaha": "Negombo Beach, Sri Lanka.jpg",
    "Kalutara": "Kalutara Bodhiya from an Architectural eye.jpg",
    "Kandy": "Sri Dalada Maligawa or the Temple of the Sacred Tooth Relic.jpg",
    "Matale": "Sigiriya Sri lanka.jpg",
    "Nuwara Eliya": "Horton Plains Sri Lanka 1.jpg",
    "Galle": "Galle Fort -Srilanka.jpg",
    "Matara": "Mirissa Beach Sri Lanka.jpg",
    "Hambantota": "Safari in Yala National park Sri Lanka (29446546593).jpg",
    "Jaffna": "Jaffna fort-5-jaffna-Sri Lanka.jpg",
    "Kilinochchi": "Iranamadu Tank2.jpg",
    "Mannar": "Mannar Dutch Fort.jpg",
    "Batticaloa": "Batticaloa Portuguese (dutch) fort.jpg",
    "Ampara": "Arugam Bay, Sri Lanka - panoramio (1).jpg",
    "Trincomalee": "Nilaveli Beach Sri Lanka.jpg",
    "Puttalam": "Kalpitiya Beach.jpg",
    "Anuradhapura": "Ruwanwelisaya Stupa Anuradhapura.jpg",
    "Polonnaruwa": "Gal Vihara Polonnaruwa 1.jpg",
    "Badulla": "Nine Arches Bridge in Ella.jpg",
    "Monaragala": "Buduruwagala-site.jpg",
    "Ratnapura": "Sri Pada (Adam's peak) view. Sri Lanka.jpg",
    "Kegalle": "Sri Lanka Pinnawala Elephant Orphanage.jpg",
}

PLACE_IMAGES = {
    "Galle Face Green": "Galle Face Green (5439939147).jpg",
    "Colombo Lotus Tower": "Lotus Tower Floating Market-Colombo Srilanka-Andres Larin.jpg",
    "Negombo Beach": "Negombo Beach, Sri Lanka.jpg",
    "Kalutara Bodhiya": "Kalutara Bodhiya from an Architectural eye.jpg",
    "Sri Dalada Maligawa / Temple of the Tooth": "Sri Dalada Maligawa or the Temple of the Sacred Tooth Relic.jpg",
    "Sigiriya Rock Fortress": "Sigiriya Sri lanka.jpg",
    "Horton Plains National Park": "Horton Plains Sri Lanka 1.jpg",
    "Galle Dutch Fort": "Galle Dutch Fort, Sri Lanka.jpg",
    "Mirissa Beach": "Mirissa Beach Sri Lanka.jpg",
    "Yala National Park – main southern access": "Safari in Yala National park Sri Lanka (29446546593).jpg",
    "Jaffna Fort": "Jaffna fort-5-jaffna-Sri Lanka.jpg",
    "Iranamadu Tank / Reservoir": "Iranamadu Tank2.jpg",
    "Mannar Fort": "Mannar Dutch Fort.jpg",
    "Batticaloa Dutch Fort": "Batticaloa Portuguese (dutch) fort.jpg",
    "Arugam Bay": "Arugam Bay, Sri Lanka - panoramio (1).jpg",
    "Nilaveli Beach": "Nilaveli Beach Sri Lanka.jpg",
    "Kalpitiya Beach": "Kalpitiya Beach.jpg",
    "Ruwanwelisaya": "Ruwanwelisaya Stupa Anuradhapura.jpg",
    "Gal Viharaya": "Gal Vihara Polonnaruwa 1.jpg",
    "Nine Arches Bridge": "Nine Arches Bridge in Ella.jpg",
    "Buduruwagala": "Buduruwagala-site.jpg",
    "Sri Pada / Adam's Peak": "Sri Pada (Adam's peak) view. Sri Lanka.jpg",
    "Pinnawala Elephant Orphanage": "Sri Lanka Pinnawala Elephant Orphanage.jpg",
}


def commons_urls(filename: str | None) -> tuple[str | None, str | None]:
    if not filename:
        return None, None
    encoded = quote(filename, safe="()_',-")
    file_page = "https://commons.wikimedia.org/wiki/File:" + encoded.replace("%20", "_")
    image = "https://commons.wikimedia.org/wiki/Special:Redirect/file/" + encoded + "?width=1600"
    return image, file_page


def category_for(name: str, icons: str) -> str:
    value = name.lower()
    if value == "ella":
        return "City & Culture"
    if any(word in value for word in ("beach", "bay", "coast", "island", "lighthouse", "surf", "whale watching")):
        return "Beaches & Coast"
    if any(word in value for word in ("temple", "vihar", "kovil", "church", "cathedral", "mosque", "bodhi", "stupa", "pagoda", "shrine", "maligawa", "mihintale", "sri pada", "devalaya")):
        return "Sacred Places"
    if any(word in value for word in ("national park", "sanctuary", "forest", "wetland", "rainforest", "bird", "elephant", "zoo", "mangrove", "wildlife")):
        return "Wildlife & Nature"
    if any(word in value for word in ("mountain", "range", "rock", "pathana", "cave", "hill", "hiking", "world's end", "riverston", "knuckles", "meemure")):
        return "Hiking & Adventure"
    if any(word in value for word in ("falls", "waterfall")) or re.search(r"(^|\s)ella($|\s)", value):
        return "Waterfalls"
    if any(word in value for word in ("lake", "lagoon", "tank", "wewa", "reservoir", "springs", "canal", "harbour", "samudraya")):
        return "Lakes & Waterways"
    if any(word in value for word in ("fort", "museum", "ancient", "archaeological", "palace", "ruins", "gedige", "bridge", "colonial", "public library", "memorial")):
        return "Heritage & History"
    if "🏛️" in icons:
        return "Heritage & History"
    if any(symbol in icons for symbol in ("🛕", "🕌", "⛪")):
        return "Sacred Places"
    if any(symbol in icons for symbol in ("🌿", "🐘", "🐆", "🐦", "🦇", "🐠")):
        return "Wildlife & Nature"
    return "City & Culture"


def tags_for(name: str, icons: str, category: str) -> str:
    tags = [category]
    mappings = {
        "⭐": "Popular", "📸": "Photography", "🥾": "Hiking", "🏖️": "Beach",
        "🌊": "Water", "🌿": "Nature", "🏛️": "History", "🛕": "Religious",
        "🕌": "Religious", "⛪": "Religious", "🐘": "Wildlife", "🐆": "Wildlife",
        "🐦": "Birdwatching", "🐋": "Whale Watching", "🏄": "Surfing", "🏝️": "Island",
        "🐠": "Marine Life", "🚣": "Adventure", "💎": "Gem Culture", "🕊️": "Calm",
    }
    for symbol, label in mappings.items():
        if symbol in icons and label not in tags:
            tags.append(label)
    if "tea" in name.lower() and "Tea Country" not in tags:
        tags.append("Tea Country")
    return ", ".join(tags[:6])


def descriptions(name: str, district: str, category: str, note: str) -> tuple[str, str]:
    templates = {
        "Waterfalls": f"Discover the natural beauty of {name}, a scenic waterfall highlight in {district} District.",
        "Beaches & Coast": f"Experience {name}, a memorable coastal attraction in {district} District with tropical scenery and ocean atmosphere.",
        "Sacred Places": f"Visit {name}, an important spiritual and cultural landmark in {district} District.",
        "Wildlife & Nature": f"Explore {name}, a nature-rich attraction in {district} District known for landscapes, biodiversity and peaceful surroundings.",
        "Hiking & Adventure": f"Explore {name}, an adventurous outdoor highlight in {district} District with rewarding scenery and walking opportunities.",
        "Lakes & Waterways": f"Enjoy the calm waters and surrounding scenery of {name} in {district} District.",
        "Heritage & History": f"Step into the history of {name}, a notable heritage attraction in {district} District.",
        "City & Culture": f"Discover {name}, a distinctive cultural and local-life attraction in {district} District.",
    }
    short = templates[category]
    if note:
        short = f"{short[:-1]} — {note}."
    full = (
        f"{short} This destination offers travellers a meaningful way to experience the character of "
        f"{district}, whether through scenery, history, local traditions or quiet exploration. "
        "Check local opening times, weather and access conditions before travelling, and visit natural and sacred places respectfully."
    )
    return short[:500], full[:2000]


def parse(markdown: str) -> list[dict]:
    district = None
    records = []
    for raw in markdown.splitlines():
        heading = re.match(r"^#{1,2}\s+(?:\d+\.\s+)?([A-Z ]+) DISTRICT$", raw.strip())
        if heading:
            district = heading.group(1).title()
            continue
        if not district or not raw.lstrip().startswith("-"):
            continue
        match = re.search(r"\*\*(.+?)\*\*", raw)
        if not match:
            continue
        name = match.group(1).strip()
        icons = raw[raw.find("-") + 1:match.start()].strip()
        tail = raw[match.end():].strip().lstrip("–-").strip()
        category = category_for(name, icons)
        short, description = descriptions(name, district, category, tail)
        filename = PLACE_IMAGES.get(name) or DISTRICT_IMAGES.get(district)
        image_url, source_url = commons_urls(filename)
        records.append({
            "name": name,
            "location": f"{district} District, Sri Lanka",
            "district": district,
            "category": category,
            "shortDescription": short,
            "description": description,
            "imageUrl": image_url,
            "imageSourceUrl": source_url,
            "tags": tags_for(name, icons, category),
            "featured": "⭐" in icons,
        })
    return records


def sql_escape(value: str | None) -> str:
    return "NULL" if value is None else "'" + value.replace("'", "''") + "'"


def write_sql(records: list[dict], output: Path) -> None:
    lines = [
        "-- Serendib Trails V3: 25-district destination seed",
        "-- Idempotent: existing destination names are preserved.",
        "USE travel_db;",
        "",
    ]
    for row in records:
        columns = "name, location, district, category, short_description, description, image_url, image_source_url, tags, featured, created_at"
        values = [
            row["name"], row["location"], row["district"], row["category"],
            row["shortDescription"], row["description"], row["imageUrl"],
            row["imageSourceUrl"], row["tags"], 1 if row["featured"] else 0,
        ]
        value_sql = ", ".join(sql_escape(v) if not isinstance(v, int) else f"b'{v}'" for v in values)
        name = row["name"].replace("'", "''").lower()
        lines.extend([
            f"INSERT INTO destinations ({columns})",
            f"SELECT {value_sql}, CURRENT_TIMESTAMP(6)",
            f"WHERE NOT EXISTS (SELECT 1 FROM destinations WHERE LOWER(name) = '{name}');",
            "",
        ])
    lines.append(f"SELECT COUNT(*) AS destination_count FROM destinations; -- supplied list contains {len(records)} records")
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> None:
    if len(sys.argv) != 4:
        raise SystemExit("usage: generate_destination_seed.py SOURCE.md OUTPUT.json OUTPUT.sql")
    source, json_output, sql_output = map(Path, sys.argv[1:])
    records = parse(source.read_text(encoding="utf-8"))
    districts = {row["district"] for row in records}
    if len(districts) != 25 or len(records) != 371:
        raise SystemExit(f"unexpected parsed data: {len(districts)} districts, {len(records)} destinations")
    json_output.parent.mkdir(parents=True, exist_ok=True)
    sql_output.parent.mkdir(parents=True, exist_ok=True)
    json_output.write_text(json.dumps(records, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    write_sql(records, sql_output)
    print(f"generated {len(records)} destinations across {len(districts)} districts")


if __name__ == "__main__":
    main()
