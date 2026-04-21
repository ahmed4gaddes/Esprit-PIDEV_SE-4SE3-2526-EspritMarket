# ============================================================
#  app.py  —  EspritMarket AI Recommendation API
#  Flask server exposant /recommend et /chat
#  Basé sur le modèle entraîné dans Google Colab
# ============================================================

from flask import Flask, request, jsonify
from flask_cors import CORS
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import pandas as pd
import re
import os

app = Flask(__name__)
CORS(app)

# ─────────────────────────────────────────────────────────────
#  1. CHARGEMENT DES DONNÉES ET DU MODÈLE (au démarrage)
# ─────────────────────────────────────────────────────────────
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

print("⏳ Chargement du dataset et des embeddings...")
df                 = pd.read_csv(os.path.join(BASE_DIR, "products_clean (1).csv"))
product_embeddings = np.load(os.path.join(BASE_DIR, "product_embeddings (1).npy"))
model              = SentenceTransformer("paraphrase-multilingual-MiniLM-L12-v2")
print(f"✅ Prêt ! {len(df)} produits chargés.")


# ─────────────────────────────────────────────────────────────
#  2. FEATURE ENGINEERING
# ─────────────────────────────────────────────────────────────
def extract_ram(desc):
    m = re.search(r'(\d+)\s*Go\s*RAM', str(desc), re.IGNORECASE)
    return int(m.group(1)) if m else 0

if "ram_gb" not in df.columns:
    df["ram_gb"] = df["description"].apply(extract_ram)

if "text" not in df.columns:
    df["text"] = df["name"] + " " + df["description"]

if "stock" not in df.columns:
    df["stock"] = 10  # valeur par défaut si la colonne n'existe pas


# ─────────────────────────────────────────────────────────────
#  3. PROFILS UTILISATEURS
# ─────────────────────────────────────────────────────────────
PROFILES = {
    "developpeur": {
        "query_boost": "RAM 32Go 16Go SSD 512Go i7 i9 Ryzen 7 Ryzen 9 performance professionnel",
        "min_ram": 16, "categories": ["informatique"], "prix_max": 9999
    },
    "etudiant": {
        "query_boost": "léger étudiant 8Go RAM économique autonomie batterie 14 pouces",
        "min_ram": 4, "categories": ["informatique"], "prix_max": 900
    },
    "gaming": {
        "query_boost": "gaming GPU RTX RAM 16Go 32Go 144Hz",
        "min_ram": 16, "categories": ["informatique"], "prix_max": 9999
    },
    "general": {
        "query_boost": "", "min_ram": 2, "categories": [], "prix_max": 9999
    }
}


# ─────────────────────────────────────────────────────────────
#  4. DÉTECTION : profil / catégorie / marque / budget
# ─────────────────────────────────────────────────────────────
BRANDS = [
    "asus", "lenovo", "dell", "hp", "acer", "microsoft", "apple",
    "samsung", "huawei", "xiaomi", "realme", "sony", "lg", "msi",
    "intel", "kingston", "seagate", "cisco", "ubiquiti", "beko",
    "ariston", "panasonic", "hisense", "bosch", "poco", "iphone",
    "oppo", "nokia", "vivo"
]

CATEGORY_KEYWORDS = {
    "telephone portable": [
        "telephone", "smartphone", "mobile", "gsm", "iphone", "android",
        "samsung", "xiaomi", "realme", "poco", "galaxy", "oppo", "nokia",
        "vivo", "camera", "batterie longue"
    ],
    "informatique": [
        "pc", "ordinateur", "laptop", "notebook", "portable", "computer",
        "processeur", "ram", "asus", "lenovo", "dell", "hp", "acer",
        "microsoft", "msi", "intel"
    ],
    "stockage": [
        "stockage", "disque", "ssd", "nas", "cle usb", "carte sd",
        "microsd", "sauvegarde", "stockage rapide", "memoire externe"
    ],
    "electronique": [
        "ecouteur", "écouteur", "casque", "enceinte", "tablette",
        "television", "tv", "bluetooth", "audio", "headphone", "airpod"
    ],
    "electromenager": [
        "aspirateur", "lave", "four", "friteuse", "refrigerateur", "climatiseur"
    ],
    "reseau": [
        "wifi", "routeur", "switch", "reseau", "point acces", "gigabit"
    ]
}

COMPARE_KEYWORDS = [
    "meilleur", "optimal", "lequel", "quel", "choisir", "recommande",
    "conseille", "prefer", "entre", "compare", "mieux", "top", "winner",
    "parmi", "superieur", "privilegie", "favoris", "ideal", "parfait",
    "adapte", "convient", "suggere", "propose", "selectionne", "retiens",
    "prends", "achete", "opte", "decides",
    "bchah", "ahsen", "ahsni", "anho", "l'ahsni", "afdhal", "wesh",
    "chwiya", "barcha", "behi", "mrigel", "najah", "zeda", "3la",
    "enti", "bihi", "yektfi", "yesleh", "yaaser", "nheb", "nakhtar",
    "nakhou", "najjem", "winou",
    "افضل", "الأفضل", "أنسب", "أحسن", "اختار", "انصح", "ارشح", "ماذا",
    "best", "which", "recommend", "suggest", "pick", "choose", "select",
    "versus", "vs", "better",
    "c'est quoi", "c'est lequel", "tu conseilles", "tu recommandes",
    "que prendre", "que choisir", "lequel prendre", "lequel acheter",
    "dis moi", "aide moi", "help me", "guide me",
    "contre", "ou bien", "difference", "comparer", "comparaison"
]


def detect_profile(query):
    q = query.lower()
    if any(w in q for w in ["developpeur", "développeur", "dev", "coder"]):
        return "developpeur"
    if any(w in q for w in ["etudiant", "étudiant", "ecole", "universite"]):
        return "etudiant"
    if any(w in q for w in ["gaming", "jeu", "game", "gamer"]):
        return "gaming"
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
    if m:
        return int(m.group(2))
    m2 = re.search(r'(\d{3,4})\s*(tnd|dt|dinar)?', q)
    if m2:
        return int(m2.group(1))
    return None


def is_compare_request(query):
    return any(w in query.lower() for w in COMPARE_KEYWORDS)


def stock_label(stock):
    if stock == 0:
        return "RUPTURE DE STOCK"
    elif stock <= 5:
        return f"Stock faible ({int(stock)})"
    else:
        return f"En stock ({int(stock)})"


# ─────────────────────────────────────────────────────────────
#  5. SMART SEARCH (avec stock, budget, marque, catégorie)
# ─────────────────────────────────────────────────────────────
def smart_search(query, top_k=5, include_out_of_stock=False):
    profile_name = detect_profile(query)
    profile      = PROFILES[profile_name]
    category     = detect_category(query)
    brand        = detect_brand(query)
    budget       = extract_budget(query)

    enriched        = query + " " + profile["query_boost"]
    q_vec           = model.encode([enriched])
    scores          = cosine_similarity(q_vec, product_embeddings)[0]

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

    available = result[result["stock"] > 0]

    if available.empty:
        include_out_of_stock = True

    final = result if include_out_of_stock else available

    if final.empty:
        final          = df[df["stock"] > 0].copy()
        final["score"] = scores[:len(final)]
        if category:
            final = final[final["category_name"] == category]

    k    = top_k if top_k is not None else len(final)
    cols = ["name", "price", "stock", "description", "category_name", "score", "ram_gb"]
    return final.nlargest(k, "score")[cols]


# ─────────────────────────────────────────────────────────────
#  6. COMPARAISON INTELLIGENTE (mode chat)
# ─────────────────────────────────────────────────────────────
def compare_products(products, orig_query):
    profile_name = detect_profile(orig_query)
    category     = detect_category(orig_query)

    if category == "telephone portable":
        profile_name = "smartphone"

    weights = {
        "developpeur": {"ram": 0.4, "score": 0.3, "price": 0.3},
        "etudiant":    {"ram": 0.2, "score": 0.3, "price": 0.5},
        "gaming":      {"ram": 0.4, "score": 0.4, "price": 0.2},
        "smartphone":  {"ram": 0.3, "score": 0.4, "price": 0.3},
        "general":     {"ram": 0.2, "score": 0.5, "price": 0.3},
    }
    w = weights.get(profile_name, weights["general"])

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

    ranked = sorted(products, key=lambda x: (x.get("stock", 1) == 0, -x["total_score"]))
    best   = ranked[0]

    reasons = [f"Score similarité : {round(best['score'], 3)}"]
    if best.get("ram_gb", 0) == max(rams):
        reasons.append(f"Plus grande RAM : {best.get('ram_gb')}Go")
    if best["price"] == min(prices):
        reasons.append(f"Meilleur prix : {best['price']} TND")
    if best.get("stock", 1) > 0:
        reasons.append(f"Disponible en stock ({int(best.get('stock', 0))} unités)")

    return {
        "profile":  profile_name,
        "best":     best,
        "ranked":   ranked,
        "reasons":  reasons
    }


# ─────────────────────────────────────────────────────────────
#  7. SESSION (en mémoire — reset à chaque redémarrage)
# ─────────────────────────────────────────────────────────────
session = {"last_results": None, "last_query": None}


# ─────────────────────────────────────────────────────────────
#  8. ROUTES FLASK
# ─────────────────────────────────────────────────────────────

@app.route("/recommend", methods=["POST"])
def recommend():
    """
    Body JSON : { "query": "pc pour developpeur" }
    Retourne  : liste de produits recommandés
    """
    data  = request.get_json(force=True)
    query = data.get("query", "").strip()

    if not query:
        return jsonify({"error": "Le champ 'query' est requis"}), 400

    results = smart_search(query, top_k=5)

    # Mettre à jour la session pour le /chat
    session["last_results"] = results
    session["last_query"]   = query

    products = []
    for _, row in results.iterrows():
        products.append({
            "name":          row["name"],
            "price":         float(row["price"]),
            "stock":         int(row.get("stock", 0)),
            "stockLabel":    stock_label(row.get("stock", 0)),
            "description":   row.get("description", ""),
            "categoryName":  row.get("category_name", ""),
            "ramGb":         int(row.get("ram_gb", 0)),
            "score":         round(float(row["score"]), 4),
            "profile":       detect_profile(query),
            "brand":         detect_brand(query),
            "budget":        extract_budget(query),
        })

    return jsonify(products)


@app.route("/chat", methods=["POST"])
def chat():
    """
    Body JSON : { "query": "anho ahsen wahid ?" }
    Si c'est une demande de comparaison ET qu'il y a des résultats en session →
    retourne le meilleur produit avec explication.
    Sinon → nouvelle recherche.
    """
    data  = request.get_json(force=True)
    query = data.get("query", "").strip()

    if not query:
        return jsonify({"error": "Le champ 'query' est requis"}), 400

    # ── Comparaison sur les derniers résultats ──────────────────
    if is_compare_request(query) and session["last_results"] is not None:
        products = session["last_results"].to_dict("records")
        result   = compare_products(products, session["last_query"])

        return jsonify({
            "type":    "comparison",
            "profile": result["profile"],
            "best":    {
                "name":        result["best"]["name"],
                "price":       float(result["best"]["price"]),
                "stock":       int(result["best"].get("stock", 0)),
                "stockLabel":  stock_label(result["best"].get("stock", 0)),
                "description": result["best"].get("description", ""),
                "ramGb":       int(result["best"].get("ram_gb", 0)),
                "totalScore":  result["best"]["total_score"],
            },
            "ranked":  [
                {
                    "name":       p["name"],
                    "price":      float(p["price"]),
                    "stock":      int(p.get("stock", 0)),
                    "stockLabel": stock_label(p.get("stock", 0)),
                    "ramGb":      int(p.get("ram_gb", 0)),
                    "totalScore": p["total_score"],
                }
                for p in result["ranked"]
            ],
            "reasons": result["reasons"],
        })

    # ── Nouvelle recherche ──────────────────────────────────────
    results = smart_search(query, top_k=5)
    session["last_results"] = results
    session["last_query"]   = query

    products = []
    for _, row in results.iterrows():
        products.append({
            "name":         row["name"],
            "price":        float(row["price"]),
            "stock":        int(row.get("stock", 0)),
            "stockLabel":   stock_label(row.get("stock", 0)),
            "description":  row.get("description", ""),
            "categoryName": row.get("category_name", ""),
            "ramGb":        int(row.get("ram_gb", 0)),
            "score":        round(float(row["score"]), 4),
        })

    return jsonify({
        "type":     "search",
        "profile":  detect_profile(query),
        "category": detect_category(query),
        "brand":    detect_brand(query),
        "budget":   extract_budget(query),
        "products": products,
    })


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "products": len(df)})


# ─────────────────────────────────────────────────────────────
#  9. DÉMARRAGE
# ─────────────────────────────────────────────────────────────
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)