from flask import Flask, request, jsonify
from flask_cors import CORS
import pandas as pd
import numpy as np
import re
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

app  = Flask(__name__)
CORS(app)

# ── Charger au démarrage ────────────────────────────────────────
model      = SentenceTransformer("paraphrase-multilingual-MiniLM-L12-v2")
embeddings = np.load("product_embeddings (1).npy")
df         = pd.read_csv("products_clean (1).csv")

# ── Preprocessing ───────────────────────────────────────────────
def extract_ram(desc):
    m = re.search(r'(\d+)\s*Go\s*RAM', str(desc), re.IGNORECASE)
    return int(m.group(1)) if m else 0

if "ram_gb" not in df.columns:
    df["ram_gb"] = df["description"].apply(extract_ram)

if "stock" not in df.columns:
    import random
    random.seed(42)
    df["stock"] = [random.randint(0, 50) for _ in range(len(df))]

# ── Profils ─────────────────────────────────────────────────────
PROFILES = {
    "developpeur": {
        "query_boost": "RAM 32Go 16Go SSD 512Go i7 i9 Ryzen 7 Ryzen 9 performance professionnel",
        "min_ram": 16, "categories": ["informatique"], "prix_max": 9999
    },
    "etudiant": {
        "query_boost": "leger etudiant 8Go RAM economique autonomie batterie 14 pouces",
        "min_ram": 4, "categories": ["informatique"], "prix_max": 1000
    },
    "gaming": {
        "query_boost": "gaming GPU RTX RAM 16Go 32Go 144Hz haute performance",
        "min_ram": 16, "categories": ["informatique"], "prix_max": 9999
    },
    "general": {
        "query_boost": "", "min_ram": 0, "categories": [], "prix_max": 9999
    }
}

CATEGORY_KEYWORDS = {
    "telephone portable": ["telephone", "smartphone", "mobile", "iphone",
                           "android", "samsung", "xiaomi", "realme", "oppo",
                           "nokia", "poco", "galaxy", "tel", "gsm"],
    "informatique":       ["pc", "ordinateur", "laptop", "portable", "ordi", "mac",
                           "asus", "lenovo", "dell", "hp", "acer", "msi"],
    "stockage":           ["stockage", "disque", "ssd", "nas", "microsd",
                           "cle usb", "sauvegarde", "rapide", "hdd", "usb"],
    "electronique":       ["ecouteur", "casque", "enceinte", "tablette",
                           "television", "tv", "bluetooth", "audio", "ecran"],
    "electromenager":     ["aspirateur", "lave", "four", "friteuse", "machine a laver",
                           "refrigerateur", "climatiseur", "vaisselle", "linge", "frigo"],
    "reseau":             ["wifi", "routeur", "switch", "reseau", "gigabit",
                           "point acces", "repeteur", "internet", "connexion"]
}

BRANDS = ["asus", "lenovo", "dell", "hp", "acer", "microsoft", "apple",
          "samsung", "huawei", "xiaomi", "realme", "sony", "lg", "msi",
          "intel", "kingston", "seagate", "oppo", "nokia", "vivo", "cisco",
          "ubiquiti", "beko", "panasonic", "hisense", "bosch"]

def detect_profile(query):
    q = query.lower()
    if any(w in q for w in ["developpeur", "dev", "coder"]): return "developpeur"
    if any(w in q for w in ["etudiant", "ecole", "universite"]): return "etudiant"
    if any(w in q for w in ["gaming", "jeu", "game", "gamer"]): return "gaming"
    return "general"

def detect_category(query):
    q = query.lower()
    for cat, keywords in CATEGORY_KEYWORDS.items():
        if any(k in q for k in keywords):
            return cat
    return None

def detect_brand(query):
    q = query.lower()
    for brand in BRANDS:
        if brand in q:
            return brand.capitalize()
    return None

def extract_budget(query):
    q = query.lower()
    m = re.search(r'(moins de|max|budget|jusqu|environ)\s*(\d+)', q)
    if m: return int(m.group(2))
    m2 = re.search(r'(\d{3,4})\s*(tnd|dt|dinar)?', q)
    if m2: return int(m2.group(1))
    return None

# ── Smart search ────────────────────────────────────────────────
def smart_search(query, top_k=5):
    profile_name = detect_profile(query)
    profile      = PROFILES[profile_name]
    category     = detect_category(query)
    brand        = detect_brand(query)
    budget       = extract_budget(query)

    enriched = query + " " + profile["query_boost"]
    q_vec    = model.encode([enriched])
    scores   = cosine_similarity(q_vec, embeddings)[0]

    result          = df.copy()
    result["score"] = scores

    if category:
        result = result[result["category_name"] == category]
    elif profile["categories"]:
        result = result[result["category_name"].isin(profile["categories"])]

    if brand:
        result = result[result["name"].str.contains(brand, case=False, na=False)]

    if profile["min_ram"] > 0:
        result = result[result["ram_gb"] >= profile["min_ram"]]

    if budget:
        result = result[result["price"] <= budget]
    else:
        result = result[result["price"] <= profile["prix_max"]]

    # Produits disponibles en priorité
    available = result[result["stock"] > 0]
    if not available.empty:
        result = available

    if result.empty:
        result          = df.copy()
        result["score"] = scores
        if category:
            result = result[result["category_name"] == category]

    # threshold to prevent irrelevant fallback results for typos
    min_score = 0.15
    if not category and not brand:
        result = result[result["score"] >= min_score]

    k    = top_k if top_k is not None else len(result)
    cols = ["name", "price", "description", "category_name", "score", "ram_gb", "stock"]
    cols = [c for c in cols if c in result.columns]
    
    if result.empty:
        return pd.DataFrame(columns=cols)
        
    return result.nlargest(k, "score")[cols]

# ── Comparaison ─────────────────────────────────────────────────
def compare_products(products, query):
    profile_name = detect_profile(query)
    category     = detect_category(query)

    weights = {
        "developpeur": {"ram": 0.4, "score": 0.3, "price": 0.3},
        "etudiant":    {"ram": 0.2, "score": 0.3, "price": 0.5},
        "gaming":      {"ram": 0.4, "score": 0.4, "price": 0.2},
        "smartphone":  {"ram": 0.3, "score": 0.4, "price": 0.3},
        "general":     {"ram": 0.2, "score": 0.5, "price": 0.3},
    }
    if category == "telephone portable":
        profile_name = "smartphone"

    w         = weights.get(profile_name, weights["general"])
    prices    = [p["price"]         for p in products]
    rams      = [p.get("ram_gb", 0) for p in products]
    scores    = [p["score"]         for p in products]
    max_price = max(prices) if prices else 1
    max_ram   = max(rams)   if max(rams) > 0 else 1
    max_score = max(scores) if scores else 1

    for p in products:
        p["total_score"] = round(
            w["score"] * (p["score"]         / max_score) +
            w["ram"]   * (p.get("ram_gb", 0) / max_ram)   +
            w["price"] * (1 - p["price"]      / max_price), 4
        )

    ranked = sorted(products,
        key=lambda x: (x.get("stock", 1) == 0, -x["total_score"]))

    best    = ranked[0]
    reasons = []
    reasons.append(f"Score similarite : {round(best['score'], 3)}")
    if best.get("ram_gb", 0) == max(rams):
        reasons.append(f"Plus grande RAM : {best.get('ram_gb')}Go")
    if best["price"] == min(prices):
        reasons.append(f"Meilleur prix : {best['price']} TND")
    if best.get("stock", 1) > 0:
        reasons.append(f"Disponible en stock ({int(best.get('stock', 0))} unites)")

    return {"best": best, "ranked": ranked, "reasons": reasons, "profile": profile_name}

# ── Routes Flask ────────────────────────────────────────────────
@app.route("/predict", methods=["POST"])
def predict():
    data   = request.get_json()
    query  = data.get("query", "")
    top_k  = data.get("top_k", 5)

    results = smart_search(query, top_k=top_k)
    cols    = ["name", "price", "description", "category_name", "score", "ram_gb", "stock"]
    cols    = [c for c in cols if c in results.columns]
    return jsonify(results[cols].to_dict("records"))

@app.route("/compare", methods=["POST"])
def compare():
    data     = request.get_json()
    query    = data.get("query", "")
    top_k    = data.get("top_k", 5)

    results  = smart_search(query, top_k=top_k)
    cols     = ["name", "price", "description", "category_name", "score", "ram_gb", "stock"]
    cols     = [c for c in cols if c in results.columns]
    products = results[cols].to_dict("records")

    recommendation = compare_products(products, query)
    return jsonify(recommendation)

@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status":   "ok",
        "products": len(df),
        "model":    "paraphrase-multilingual-MiniLM-L12-v2"
    })

if __name__ == "__main__":
    app.run(port=5000, debug=True)