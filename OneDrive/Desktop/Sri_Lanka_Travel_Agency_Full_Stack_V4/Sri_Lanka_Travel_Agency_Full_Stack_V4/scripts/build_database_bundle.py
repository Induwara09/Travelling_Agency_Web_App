#!/usr/bin/env python3
"""Merge the generated V3 catalogue into the reusable MySQL schema."""

from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
full_sql = ROOT / "database" / "travel_db_full.sql"
seed_sql = ROOT / "database" / "destinations_seed_v3.sql"

current = full_sql.read_text(encoding="utf-8")
markers = [
    "-- Copyright-safe destination images are served by Wikimedia Commons redirects.",
    "-- Serendib Trails V3: 25-district destination seed",
]
marker = next((value for value in markers if value in current), None)
if marker is None:
    raise SystemExit("destination seed marker missing from travel_db_full.sql")

head = current.split(marker, 1)[0].rstrip()
seed = seed_sql.read_text(encoding="utf-8").strip()
packages = r"""

-- Optional destination-based tour package examples.
SET @ella_id = (SELECT id FROM destinations WHERE LOWER(name) = 'ella' ORDER BY id LIMIT 1);
SET @sigiriya_id = (SELECT id FROM destinations WHERE LOWER(name) = 'sigiriya rock fortress' ORDER BY id LIMIT 1);
SET @galle_id = (SELECT id FROM destinations WHERE LOWER(name) = 'galle dutch fort' ORDER BY id LIMIT 1);

INSERT INTO packages (name, description, price, duration_days, image_url, category, status, destination_id, created_at)
SELECT 'Highland Rails & Tea Trails',
       'A four-day Ella escape featuring tea country, scenic walks and the famous highland railway landscape.',
       89000.00, 4, NULL, 'Adventure', 'ACTIVE', @ella_id, CURRENT_TIMESTAMP(6)
WHERE @ella_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packages WHERE name = 'Highland Rails & Tea Trails');

INSERT INTO packages (name, description, price, duration_days, image_url, category, status, destination_id, created_at)
SELECT 'Cultural Triangle Discovery',
       'A three-day journey through Sigiriya, ancient kingdoms and Sri Lanka''s living cultural heritage.',
       76000.00, 3, NULL, 'Cultural', 'ACTIVE', @sigiriya_id, CURRENT_TIMESTAMP(6)
WHERE @sigiriya_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packages WHERE name = 'Cultural Triangle Discovery');

INSERT INTO packages (name, description, price, duration_days, image_url, category, status, destination_id, created_at)
SELECT 'Southern Coast & Heritage',
       'A five-day coastal journey combining Galle Fort, relaxed beaches, local food and ocean sunsets.',
       118000.00, 5, NULL, 'Beach', 'ACTIVE', @galle_id, CURRENT_TIMESTAMP(6)
WHERE @galle_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM packages WHERE name = 'Southern Coast & Heritage');

-- Admin users are intentionally not inserted here because passwords must be BCrypt hashes.
-- Create the first admin securely with ADMIN_NAME, ADMIN_EMAIL and ADMIN_PASSWORD environment variables.

SELECT 'travel_db V3 schema and 25-district destination catalogue are ready' AS result;
""".strip()

full_sql.write_text(f"{head}\n\n{seed}\n\n{packages}\n", encoding="utf-8")
print("rebuilt travel_db_full.sql with the V3 destination catalogue")
