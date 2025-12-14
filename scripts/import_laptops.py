import pandas as pd
import mysql.connector
from datetime import datetime

# ========= CONFIG =========
CSV_FILE = r"D:\Projects\RecommendoBot\backend\src\main\resources\data\all_laptops_data.csv"

DB_CONFIG = {
    "host": "localhost",
    "user": "jill",
    "password": "jill",
    "database": "laptops_data",
}
BATCH_SIZE = 500
# ==========================

print("=" * 60)
print("IMPORTING LAPTOP CSV INTO MySQL (laptops table)")
print("=" * 60)

# 1) Read CSV
print(f"\n[1/4] Reading CSV: {CSV_FILE}")
df = pd.read_csv(CSV_FILE)
print(f"      ✓ Loaded {len(df):,} rows")
print(f"      Columns: {list(df.columns)}")

# Normalize column names (strip spaces, case)
df.columns = [c.strip() for c in df.columns]

required_cols = ["Brand Name", "Product", "Price", "Processor",
                 "Memory", "Storage", "Graphics", "Display", "OS", "Image"]
missing = [c for c in required_cols if c not in df.columns]
if missing:
    raise ValueError(f"Missing required columns in CSV: {missing}")

# 2) Connect to MySQL
print(f"\n[2/4] Connecting to MySQL...")
conn = mysql.connector.connect(**DB_CONFIG)
cursor = conn.cursor()
print(f"      ✓ Connected to DB: {DB_CONFIG['database']}")

# 3) Clear existing data
print(f"\n[3/4] Clearing existing data from laptops table...")
cursor.execute("DELETE FROM laptops")
cursor.execute("ALTER TABLE laptops AUTO_INCREMENT = 1")
conn.commit()
print("      ✓ Table cleared and AUTO_INCREMENT reset")

# 4) Insert in batches
print(f"\n[4/4] Inserting rows in batches of {BATCH_SIZE}...")

insert_sql = """
INSERT INTO laptops
(brand_name, product, price, processor, memory, storage, graphics,
 display, os, image_url, created_at, updated_at)
VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
"""

now = datetime.now()
total = len(df)
inserted = 0

def clean_price(p):
    if pd.isna(p):
        return None
    s = str(p).strip()
    if not s:
        return None
    s = s.replace('$', '').replace(',', '')
    try:
        return float(s)
    except ValueError:
        return None

batch = []
for idx, row in df.iterrows():
    vals = (
        row["Brand Name"],
        row["Product"],
        clean_price(row["Price"]),
        row.get("Processor"),
        row.get("Memory"),
        row.get("Storage"),
        row.get("Graphics"),
        row.get("Display"),
        row.get("OS"),
        row.get("Image"),
        now,
        now,
    )
    batch.append(vals)

    if len(batch) >= BATCH_SIZE:
        cursor.executemany(insert_sql, batch)
        conn.commit()
        inserted += len(batch)
        print(f"      Inserted {inserted:,}/{total:,} rows", end="\r")
        batch = []

# remaining
if batch:
    cursor.executemany(insert_sql, batch)
    conn.commit()
    inserted += len(batch)

print(f"\n      ✓ Finished inserting {inserted:,} rows")

# verify
cursor.execute("SELECT COUNT(*) FROM laptops")
count = cursor.fetchone()[0]
print(f"\n      ✓ laptops table now has {count:,} rows")

cursor.close()
conn.close()

print("\n" + "=" * 60)
print("✓ IMPORT COMPLETE")
print("=" * 60)
