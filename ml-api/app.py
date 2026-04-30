# ============================================================
#  app.py  —  EspritMarket AI Recommendation API
#  ✅ Modèle : intfloat/multilingual-e5-base (plus performant)
#  ✅ Correction automatique des fautes de frappe (rapidfuzz)
#  ✅ Préfixes query:/passage: obligatoires pour e5
#  ✅ Gestion précise multi-filtres
#  ✅ Message si filtre non disponible
#  ✅ RAM la plus proche si exacte indisponible
#  ✅ Cache embeddings
#  ✅ CORS corrigé
# ============================================================

from flask import Flask, request, jsonify
from flask_cors import CORS
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
from rapidfuzz import process, fuzz
import numpy as np
import pandas as pd
import re
import requests
import time
import threading

app = Flask(__name__)
CORS(app,
     resources={r"/*": {"origins": "*"}},
     methods=["GET", "POST", "OPTIONS"],
     allow_headers=["Content-Type", "Authorization"])

# ─────────────────────────────────────────────────────────────
#  1. MODÈLE IA  — multilingual-e5-base
# ─────────────────────────────────────────────────────────────
print("⏳ Chargement du modèle IA (multilingual-e5-base)...")
model = SentenceTransformer("intfloat/multilingual-e5-base")
print("✅ Modèle prêt.")

SPRING_BOOT_URL = "http://localhost:8081/Product/getall"
SCORE_THRESHOLD = 0.20
CACHE_TTL       = 300

_cache = {
    "df":         None,
    "embeddings": None,
    "last_load":  0,
    "lock":       threading.Lock(),
}


# ─────────────────────────────────────────────────────────────
#  2. CORRECTION AUTOMATIQUE DES FAUTES DE FRAPPE
# ─────────────────────────────────────────────────────────────
VOCABULARY = [
    # Catégories produits
    "ordinateur", "telephone", "smartphone", "laptop", "notebook",
    "tablette", "ecouteur", "casque", "enceinte", "television",
    "routeur", "switch", "climatiseur", "refrigerateur", "aspirateur",
    "disque", "stockage", "memoire", "clavier", "souris", "ecran",
    # Profils utilisateur
    "gaming", "etudiant", "developpeur", "programmeur", "ingenieur",
    "gamer", "streamer", "informaticien",
    # Specs techniques
    "processeur", "performance", "batterie", "autonomie", "resolution",
    "graphique", "leger", "portable", "professionnel", "economique",
    # Marques
    "samsung", "lenovo", "asus", "xiaomi", "dell", "acer",
    "apple", "huawei", "realme", "sony", "msi", "microsoft",
    "intel", "kingston", "seagate", "cisco", "bosch", "panasonic",
    "hisense", "beko", "poco", "oppo", "nokia", "vivo", "redmi",
    # Processeurs
    "ryzen", "core",
    # Darija courant — pas de correction
    "nichri", "bghit", "nheb", "inhib", "inhich",
]

# Dictionnaire de corrections forcées (fautes connues → mot correct)
FORCED_CORRECTIONS = {
    # Ordinateur
    "ordinater": "ordinateur", "ordinatuer": "ordinateur", "ordianteur": "ordinateur",
    "ordinatear": "ordinateur", "ordiateur": "ordinateur", "ordinateurr": "ordinateur",
    "ordiantuer": "ordinateur", "ordinatuer": "ordinateur", "ordonateur": "ordinateur",
    "ordinatuer": "ordinateur", "ordin": "ordinateur",
    # Etudiant
    "etudian": "etudiant", "etudiants": "etudiant", "etudien": "etudiant",
    "etudant": "etudiant", "etudinat": "etudiant", "étudian": "etudiant",
    "etudiann": "etudiant", "etudient": "etudiant", "etuiant": "etudiant",
    # Laptop / PC
    "lapotop": "laptop", "lapotp": "laptop", "latop": "laptop",
    "lpatop": "laptop", "laptops": "laptop", "lapop": "laptop",
    # Téléphone
    "telephon": "telephone", "telphone": "telephone", "teléphone": "telephone",
    "telehone": "telephone", "telephonne": "telephone",
    # Gaming
    "gamin": "gaming", "gamming": "gaming", "gaiming": "gaming",
    # Développeur
    "devellopeur": "developpeur", "devloppeur": "developpeur",
    "developeur": "developpeur", "developper": "developpeur",
    # Marques
    "samsoung": "samsung", "samsong": "samsung", "samsubg": "samsung",
    "lenovoo": "lenovo", "lenvo": "lenovo",
    "assus": "asus", "azus": "asus",
    "xiaomii": "xiaomi", "xaomi": "xiaomi",
    "appel": "apple", "aple": "apple",
    "huawai": "huawei", "houawei": "huawei",
    # Processeur
    "proceseur": "processeur", "prcesseur": "processeur",
    "razen": "ryzen", "rizen": "ryzen", "rayzen": "ryzen",
    # Autres
    "tabletes": "tablette", "tablete": "tablette",
    "ecouteurs": "ecouteur", "ecoutuer": "ecouteur",
    "routuer": "routeur", "routuer": "routeur",
}

IGNORE_WORDS = {
    "pc", "ram", "ssd", "hdd", "go", "to", "tnd", "dt", "dinar", "budget",
    "max", "min", "moins", "environ", "achat", "acheter", "veux", "bghit",
    "nheb", "nichri", "inhib", "je", "de", "du", "le", "la", "les", "un",
    "une", "pour", "avec", "et", "en", "sur", "par", "que", "qui", "dont",
    "comme", "aussi", "des", "i3", "i5", "i7", "i9", "hp", "lg",
}

def correct_query(query: str) -> str:
    """Corrige automatiquement les fautes de frappe dans la requête utilisateur."""
    words = query.lower().split()
    corrected = []
    changed   = False

    for word in words:
        # 1️⃣ Correction forcée (dictionnaire exact — priorité maximale)
        if word in FORCED_CORRECTIONS:
            fixed = FORCED_CORRECTIONS[word]
            print(f"✏️  Correction forcée : '{word}' → '{fixed}'")
            corrected.append(fixed)
            changed = True
            continue

        # 2️⃣ Ne pas corriger : mots ignorés, trop courts ou chiffres
        if word in IGNORE_WORDS or len(word) <= 2:
            corrected.append(word)
            continue
        if re.match(r'^\d+', word):
            corrected.append(word)
            continue

        # 3️⃣ Correction floue via rapidfuzz (seuil abaissé à 75%)
        result = process.extractOne(word, VOCABULARY, scorer=fuzz.ratio)
        if result:
            match, score, _ = result
            if score >= 75 and match != word:
                print(f"✏️  Correction floue  : '{word}' → '{match}' (score={score})")
                corrected.append(match)
                changed = True
            else:
                corrected.append(word)
        else:
            corrected.append(word)

    final = " ".join(corrected)
    if changed:
        print(f"📝 Query finale : '{query}' → '{final}'")
    return final


# ─────────────────────────────────────────────────────────────
#  3. ENCODAGE e5 — PRÉFIXES OBLIGATOIRES
# ─────────────────────────────────────────────────────────────
def encode_query(text: str):
    """Encode une requête utilisateur avec le préfixe 'query:' requis par e5."""
    return model.encode([f"query: {text}"], normalize_embeddings=True)

def encode_passages(texts: list):
    """Encode les descriptions produits avec le préfixe 'passage:' requis par e5."""
    prefixed = [f"passage: {t}" for t in texts]
    return model.encode(prefixed, show_progress_bar=False, normalize_embeddings=True)


# ─────────────────────────────────────────────────────────────
#  4. CACHE & DATAFRAME
# ─────────────────────────────────────────────────────────────
def get_products_from_db():
    try:
        response = requests.get(SPRING_BOOT_URL, timeout=5)
        data = response.json()
        if isinstance(data, list) and len(data) > 0:
            print(f"✅ {len(data)} produits récupérés")
            return data
        return []
    except Exception as e:
        print(f"❌ Erreur Spring Boot : {e}")
        return []


def build_dataframe():
    products = get_products_from_db()
    if not products:
        return pd.DataFrame()

    df = pd.DataFrame(products)

    rename_map = {}
    if "categoryName" in df.columns: rename_map["categoryName"] = "category_name"
    if "ramGb"        in df.columns: rename_map["ramGb"]        = "ram_gb"
    if rename_map: df = df.rename(columns=rename_map)

    for col, default in [("name",""),("price",0.0),("stock",0),
                          ("description",""),("category_name",""),
                          ("ram_gb",0),("id",0),("imageUrl","")]:
        if col not in df.columns: df[col] = default

    df["name"]          = df["name"].fillna("").astype(str)
    df["price"]         = pd.to_numeric(df["price"],  errors="coerce").fillna(0.0)
    df["stock"]         = pd.to_numeric(df["stock"],  errors="coerce").fillna(0).astype(int)
    df["ram_gb"]        = pd.to_numeric(df["ram_gb"], errors="coerce").fillna(0).astype(int)
    df["description"]   = df["description"].fillna("").astype(str)
    df["category_name"] = df["category_name"].fillna("").astype(str)

    def extract_ram(desc):
        m = re.search(r'(\d+)\s*Go\s*RAM', str(desc), re.IGNORECASE)
        return int(m.group(1)) if m else 0

    df.loc[df["ram_gb"] == 0, "ram_gb"] = df.loc[df["ram_gb"] == 0, "description"].apply(extract_ram)
    df["text"] = df["name"] + " " + df["description"] + " " + df["category_name"]
    return df


def get_cached_data():
    with _cache["lock"]:
        now     = time.time()
        expired = (now - _cache["last_load"]) > CACHE_TTL
        empty   = _cache["df"] is None or _cache["df"].empty

        if empty or expired:
            print("🔄 Rechargement cache...")
            df = build_dataframe()
            if not df.empty:
                # ✅ Utilise encode_passages() pour les produits (préfixe passage:)
                embeddings           = encode_passages(df["text"].tolist())
                _cache["df"]         = df
                _cache["embeddings"] = embeddings
                _cache["last_load"]  = now
                print(f"✅ Cache prêt : {len(df)} produits")

        return _cache["df"], _cache["embeddings"]


def warmup():
    print("🔥 Préchauffage...")
    get_cached_data()
    print("✅ Préchauffage terminé.")

threading.Thread(target=warmup, daemon=True).start()


# ─────────────────────────────────────────────────────────────
#  5. PROFILS
# ─────────────────────────────────────────────────────────────
PROFILES = {
    "developpeur": {"query_boost": "RAM 32Go 16Go SSD 512Go i7 i9 Ryzen 7 performance professionnel",
                    "min_ram": 16, "categories": ["informatique"], "prix_max": 9999},
    "etudiant":    {"query_boost": "léger étudiant 8Go RAM économique autonomie batterie",
                    "min_ram": 4,  "categories": ["informatique"], "prix_max": 9000},
    "gaming":      {"query_boost": "gaming GPU RTX RAM 16Go 32Go 144Hz",
                    "min_ram": 16, "categories": ["informatique"], "prix_max": 9999},
    "general":     {"query_boost": "", "min_ram": 0, "categories": [], "prix_max": 9999},
}

BRANDS = [
    "asus","lenovo","dell","hp","acer","microsoft","apple","samsung","huawei",
    "xiaomi","realme","sony","lg","msi","intel","kingston","seagate","cisco",
    "ubiquiti","beko","ariston","panasonic","hisense","bosch","poco","iphone",
    "oppo","nokia","vivo","redmi","galaxy",
]

BRAND_ALIASES = {
    "apple":     ["apple","appel","aple","appl","mac","macbook"],
    "samsung":   ["samsung","samson","samsng","samsun","galaxy"],
    "xiaomi":    ["xiaomi","xiomi","xaomi","shiaomi"],
    "lenovo":    ["lenovo","lenovoo","lenvo","lnovo"],
    "asus":      ["asus","asuss","azus","assus"],
    "dell":      ["dell","del"],
    "hp":        ["hp","hewlett"],
    "acer":      ["acer","accer","asar"],
    "huawei":    ["huawei","huawai","hwuawei","houawei"],
    "realme":    ["realme","realmi","realmy"],
    "sony":      ["sony","sonny","soni"],
    "lg":        ["lg"],
    "msi":       ["msi"],
    "microsoft": ["microsoft","microsft","micorsoft"],
    "intel":     ["intel","intal"],
    "kingston":  ["kingston","kingstone"],
    "seagate":   ["seagate","seaget"],
    "cisco":     ["cisco","sisco"],
    "beko":      ["beko","becko"],
    "panasonic": ["panasonic","panasoni"],
    "hisense":   ["hisense","hisens"],
    "bosch":     ["bosch","bosh"],
    "poco":      ["poco","pocco"],
    "oppo":      ["oppo","opoo"],
    "nokia":     ["nokia","nokya","nokoia"],
    "vivo":      ["vivo","vibo"],
    "iphone":    ["iphone","iphonn"],
    "redmi":     ["redmi","redmy"],
}

BRAND_TO_CATEGORY = {
    "samsung":"telephone portable","xiaomi":"telephone portable",
    "realme":"telephone portable","poco":"telephone portable",
    "oppo":"telephone portable","nokia":"telephone portable",
    "vivo":"telephone portable","redmi":"telephone portable",
    "iphone":"telephone portable","apple":"telephone portable",
    "huawei":"telephone portable","galaxy":"telephone portable",
    "asus":"informatique","lenovo":"informatique","dell":"informatique",
    "hp":"informatique","acer":"informatique","msi":"informatique",
    "cisco":"reseau","ubiquiti":"reseau",
}

CATEGORY_KEYWORDS = {
    "telephone portable": [
        "telephone","smartphone","mobile","gsm","iphone","android",
        "samsung","xiaomi","realme","poco","galaxy","oppo","nokia","vivo",
        "camera","batterie longue","redmi",
        "nichri telephone","inhib nichri telephone","nichri smartphone",
        "bghit telephone","nheb telephone","nichri redmi","nichri samsung",
        "nichri xiaomi","inhib nichri samsung","inhib nichri xiaomi",
        "inhib nichri redmi","inhib samsung","inhib xiaomi","inhib telephone",
        "inhib smartphone","inhib iphone","inhib redmi","inhib poco",
        "inhib galaxy","bghit samsung","bghit xiaomi","nheb samsung","nheb xiaomi",
    ],
    "informatique": [
        "pc","ordinateur","laptop","notebook","portable","computer",
        "processeur","asus","lenovo","dell","hp","acer","microsoft","msi","intel",
        "nichri pc","bghit pc","nheb pc","achat pc","je veux pc","acheter pc",
        "bghit ordinateur","nheb ordinateur","nichri ordinateur",
        "inhib nichri pc","inhib nichri ordinateur","achat ordinateur",
        "je veux ordinateur","acheter ordinateur","inhib pc","inhib laptop",
        "inhib ordinateur","inhich nichri pc","inhich nichri laptop","je veux achat pc",
        # ✅ Ajout : variantes après correction
        "ordinateur etudiant","laptop etudiant","pc etudiant",
        "ordinateur gaming","laptop gaming","pc gaming",
        "ordinateur developpeur","laptop developpeur",
    ],
    "stockage": [
        "stockage","disque dur","nas","cle usb","carte sd",
        "microsd","sauvegarde","memoire externe",
    ],
    "electronique": [
        "ecouteur","écouteur","casque","enceinte","tablette",
        "television","tv","bluetooth","audio","headphone","airpod",
        "inhib casque","inhib ecouteur","nichri casque",
    ],
    "electromenager": [
        "aspirateur","lave","four","friteuse","refrigerateur","climatiseur",
        "inhib machine","inhib frigo","nichri climatiseur",
    ],
    "reseau": [
        "wifi","routeur","switch","reseau","point acces","gigabit","router",
        "achat routeur","je veux routeur","acheter routeur",
        "nichri routeur","bghit routeur","nheb routeur",
        "inhib routeur","je veux achat routeur","achat router",
    ],
}

COMPARE_KEYWORDS = [
    "meilleur","optimal","lequel","quel","choisir","recommande","compare",
    "mieux","top","parmi","superieur","ideal","bchah","ahsen","ahsni",
    "anho","l'ahsni","afdhal","best","which","recommend","versus","vs",
    "better","c'est lequel","tu conseilles","que prendre","que choisir",
]

BUNDLE_KEYWORDS = [
    "accessoire","complement","bundle","pack",
    "acheter avec","souvent avec","va avec","compatible",
    "quoi d'autre","autre produit","produit complementaire",
]

PROCESSOR_ALIASES = {
    "ryzen 9": ["ryzen 9","razen 9","rayzen 9","rizen 9","ryzen9"],
    "ryzen 7": ["ryzen 7","razen 7","rayzen 7","rizen 7","ryzen7","razen7"],
    "ryzen 5": ["ryzen 5","razen 5","rayzen 5","rizen 5","ryzen5"],
    "ryzen 3": ["ryzen 3","razen 3","rayzen 3","rizen 3","ryzen3"],
    "i9":      ["i9","core i9"],
    "i7":      ["i7","core i7"],
    "i5":      ["i5","core i5"],
    "i3":      ["i3","core i3"],
    "m3":      ["m3","apple m3"],
    "m2":      ["m2","apple m2"],
    "m1":      ["m1","apple m1"],
}


# ─────────────────────────────────────────────────────────────
#  6. DÉTECTION NLP
# ─────────────────────────────────────────────────────────────
def detect_profile(query):
    q = query.lower()
    if any(w in q for w in [
        "developpeur","développeur","devloppeur","dev","déve","coder",
        "programmeur","informaticien","software","backend","frontend",
        "fullstack","data scientist","ingenieur","devops","python",
        "java","javascript","react","angular","machine learning","deep learning",
        "virtualisation","virtual machine","vm","vmware","docker",
    ]): return "developpeur"
    if any(w in q for w in [
        "etudiant","étudiant","etudiannt","eleve","élève","lyceen",
        "lycéen","lycee","univ","universite","université","ecole",
        "école","faculte","fac","campus","bac","licence","master",
        "cours","etude","étude","etudes","études","scolaire","classe",
        "student","school","university","college","studying",
    ]): return "etudiant"
    if any(w in q for w in [
        "gaming","gamm","game","gamer","jeu","jeux","jouer","fps",
        "mmorpg","rpg","esport","stream","streamer","fortnite",
        "minecraft","valorant","lol","fifa","cod","pubg","rtx","gtx",
        "gpu","carte graphique","144hz","240hz","ray tracing",
        "play","games","pc game","laptop game","gaming laptop",
        "useful for gaming","good for gaming","for gaming","gaming pc",
    ]): return "gaming"
    return "general"


def detect_category(query):
    q = query.lower()
    for cat, keywords in CATEGORY_KEYWORDS.items():
        if any(k in q for k in keywords): return cat
    for brand, cat in BRAND_TO_CATEGORY.items():
        if brand in q: return cat
    return None


def detect_brand(query):
    q     = query.lower()
    words = set(re.findall(r'\w+', q)) - IGNORE_WORDS
    for brand, aliases in BRAND_ALIASES.items():
        for alias in aliases:
            if alias in words or alias in q:
                return brand.capitalize()
    for brand in BRANDS:
        if brand in words:
            return brand.capitalize()
    return None


def extract_budget(query):
    q = query.lower()
    for pat in [r'\d+\s*go\s*ram',r'ram\s*\d+\s*go',r'\d+\s*go\s*ssd',
                r'ssd\s*\d+\s*(go|to)',r'\d+\s*to\s*(ssd|hdd|disque)',
                r'\d+\s*go\b',r'\d+\s*to\b',r'\d+\s*pouces',
                r'i[3579]\b',r'ryzen\s*\d+',r'\d+\s*mhz',
                r'\d+\s*mp\b',r'\d+\s*mah\b',r'ddr\d*',r'core\s*i\d']:
        q = re.sub(pat, '', q)
    m = re.search(
        r'(moins de|max|budget|jusqu|environ|pas plus|maxi|inferieur|'
        r'lower than|less than|under|below|cheaper than|no more than|price under)\s*(\d+)', q)
    if m: return int(m.group(2))
    m2 = re.search(r'(\d{3,5})\s*(tnd|dt|dinar|euro|eur)', q)
    if m2: return int(m2.group(1))
    return None


def extract_ram_query(query):
    q = query.lower()
    m = re.search(r'(\d+)\s*go\s*(of\s*)?ram', q)
    if m: return int(m.group(1))
    m2 = re.search(r'ram\s*(\d+)\s*go', q)
    if m2: return int(m2.group(1))
    m3 = re.search(r'(?:no less than|at least|minimum|min)\s*(\d+)\s*(?:go|gb)?', q)
    if m3: return int(m3.group(1))
    m4 = re.search(r'(\d+)\s*(?:go|gb)\s*(?:of\s*)?ram', q)
    if m4: return int(m4.group(1))
    m5 = re.search(r'avec\s*(\d+)\s*go', q)
    if m5: return int(m5.group(1))
    m6 = re.search(r'ram\s*(\d+)', q)
    if m6: return int(m6.group(1))
    return None


def extract_storage(query):
    q = query.lower()
    q_no_ram = re.sub(r'\d+\s*go\s*ram', '', q)
    q_no_ram = re.sub(r'ram\s*\d+\s*go', '', q_no_ram)
    q_no_ram = re.sub(r'ram\s*\d+',      '', q_no_ram)

    m = re.search(r'(ssd|hdd|disque|stockage)\s*(\d+)\s*(to|go)', q_no_ram)
    if m:
        val, unit = int(m.group(2)), m.group(3)
        return val * 1000 if unit == "to" else val

    m2 = re.search(r'(\d+)\s*(to|go)\s*(ssd|hdd|disque|stockage)', q_no_ram)
    if m2:
        val, unit = int(m2.group(1)), m2.group(2)
        return val * 1000 if unit == "to" else val

    m3 = re.search(r'(\d+)\s*to\b', q_no_ram)
    if m3: return int(m3.group(1)) * 1000

    m4 = re.search(r'(\d+)\s*go\b', q_no_ram)
    if m4:
        val = int(m4.group(1))
        if val >= 128: return val
    return None


def extract_processor(query):
    q = query.lower()
    for proc, aliases in PROCESSOR_ALIASES.items():
        if any(a in q for a in aliases): return proc
    return None


def is_compare_request(query):
    return any(w in query.lower() for w in COMPARE_KEYWORDS)


def is_bundle_request(query):
    return any(w in query.lower() for w in BUNDLE_KEYWORDS)


def stock_label(stock):
    if stock == 0:  return "RUPTURE DE STOCK"
    if stock <= 5:  return f"Stock faible ({int(stock)})"
    return f"En stock ({int(stock)})"


def get_bundle_from_spring(product_name):
    try:
        r = requests.get("http://localhost:8081/bundle/search",
                         params={"name": product_name}, timeout=5)
        return r.json()
    except Exception as e:
        print(f"❌ Erreur bundle : {e}")
        return []


# ─────────────────────────────────────────────────────────────
#  7. VÉRIFICATION DISPONIBILITÉ DES FILTRES
# ─────────────────────────────────────────────────────────────
def check_filters_availability(df, category, brand, ram_query, processor, storage, budget):
    missing = []
    relaxed = set()

    base = df[df["stock"] > 0].copy()

    if category:
        cat_base = base[base["category_name"].str.lower() == category.lower()]
        if cat_base.empty:
            missing.append(f"❌ Aucun produit en stock dans la catégorie **{category}**.")
            return "\n".join(missing), {"all"}
        base = cat_base

    # Vérif marque
    if brand:
        brand_base = base[base["name"].str.contains(brand, case=False, na=False)]
        if brand_base.empty:
            missing.append(f"❌ La marque **{brand}** n'est pas disponible dans notre catalogue.")
            relaxed.add("brand")
        else:
            base = brand_base

    # Vérif processeur
    if processor:
        proc_base = base[
            base["name"].str.lower().str.contains(processor, na=False) |
            base["description"].str.lower().str.contains(processor, na=False)
        ]
        if proc_base.empty:
            missing.append(f"⚠️ Aucun produit avec processeur **{processor}** disponible.")
            relaxed.add("processor")

    # Vérif RAM avec RAM la plus proche
    if ram_query:
        ram_base = base[base["ram_gb"] >= ram_query]
        if ram_base.empty:
            max_ram = int(base["ram_gb"].max()) if not base.empty else 0
            missing.append(f"⚠️ Aucun produit avec **{ram_query}Go RAM** disponible. Maximum disponible : {max_ram}Go.")
            relaxed.add("ram")
        else:
            rams_available = sorted(base["ram_gb"].unique().tolist())
            if ram_query not in rams_available:
                closest = min(rams_available, key=lambda x: abs(x - ram_query))
                missing.append(f"💡 RAM **{ram_query}Go** non disponible exactement. RAM la plus proche : **{closest}Go**.")

    # Vérif stockage
    if storage:
        if storage >= 1000:
            tb = storage // 1000
            patterns = [f"{tb}to", f"{tb} to", f"ssd {tb}to", f"{storage}go"]
        else:
            patterns = [f"{storage}go", f"{storage} go", f"ssd {storage}"]
        stor_base = base[base["description"].str.lower().apply(
            lambda d: any(p in d for p in patterns)
        )]
        if stor_base.empty:
            def get_storage_from_desc(desc):
                m = re.search(r'ssd\s*(\d+)\s*(go|to)', desc.lower())
                if m:
                    v, u = int(m.group(1)), m.group(2)
                    return v * 1000 if u == "to" else v
                return 0
            base_copy = base.copy()
            base_copy["storage_val"] = base_copy["description"].apply(get_storage_from_desc)
            available_storage = sorted(base_copy[base_copy["storage_val"] > 0]["storage_val"].unique().tolist())
            if available_storage:
                closest_stor = min(available_storage, key=lambda x: abs(x - storage))
                missing.append(f"⚠️ Stockage **{storage}Go** non disponible. Stockage le plus proche : **{closest_stor}Go**.")
            else:
                missing.append(f"⚠️ Aucun produit avec **{storage}Go de stockage** disponible.")
            relaxed.add("storage")

    # Vérif budget
    if budget:
        budget_base = base[base["price"] <= budget]
        if budget_base.empty:
            missing.append(f"⚠️ Budget **{budget} TND** insuffisant.")
            relaxed.add("budget")

    msg = "\n".join(missing) if missing else None
    return msg, relaxed


# ─────────────────────────────────────────────────────────────
#  8. SMART SEARCH
# ─────────────────────────────────────────────────────────────
def smart_search(query, top_k=10):
    print(f"🚀 smart_search appelée avec query='{query}'")

    # ✅ Étape 1 : Correction des fautes de frappe
    corrected_query = correct_query(query)
    if corrected_query != query.lower():
        print(f"✏️  Query corrigée : '{query}' → '{corrected_query}'")
    query = corrected_query

    df, embeddings = get_cached_data()
    if df is None or df.empty:
        return pd.DataFrame(), pd.DataFrame(), None, None, None

    profile_name = detect_profile(query)
    profile      = PROFILES[profile_name]
    category     = detect_category(query)
    brand        = detect_brand(query)
    budget       = extract_budget(query)
    processor    = extract_processor(query)
    ram_query    = extract_ram_query(query)
    storage      = extract_storage(query)

    # ✅ Si catégorie non détectée mais profil informatique → forcer catégorie
    if category is None and profile_name in ["etudiant", "developpeur", "gaming"]:
        category = "informatique"
        print(f"🔧 Catégorie forcée via profil : {profile_name} → informatique")

    print(f"🔍 cat={category} brand={brand} ram={ram_query} proc={processor} storage={storage} budget={budget} profile={profile_name}")

    # Vérification disponibilité des filtres
    missing_msg, relaxed = check_filters_availability(
        df, category, brand, ram_query, processor, storage, budget
    )

    if "all" in relaxed:
        return pd.DataFrame(), df, brand, "not_found", missing_msg

    # ── Embeddings avec préfixe query: ──────────────────────
    enriched = " ".join(filter(None, [
        query, profile["query_boost"],
        category or "", processor or "", brand or "",
        f"{ram_query}Go RAM" if ram_query else "",
    ]))

    # ✅ Encode avec préfixe "query:" obligatoire pour e5
    q_vec  = encode_query(enriched)
    scores = cosine_similarity(q_vec, embeddings)[0]

    result          = df.copy()
    result["score"] = scores

    # FILTRE 1 : Catégorie
    if category:
        result = result[result["category_name"].str.lower() == category.lower()]
    elif profile["categories"]:
        result = result[result["category_name"].str.lower().isin(
            [c.lower() for c in profile["categories"]])]

    # FILTRE 2 : Marque
    if brand and "brand" not in relaxed:
        brand_filtered = result[result["name"].str.contains(brand, case=False, na=False)]
        if not brand_filtered.empty:
            result = brand_filtered

    # FILTRE 3 : RAM profil
    if profile["min_ram"] > 0 and not ram_query:
        ram_filtered = result[result["ram_gb"] >= profile["min_ram"]]
        if not ram_filtered.empty:
            result = ram_filtered

    # FILTRE 4 : RAM — exact d'abord, puis plus proche
    if ram_query and "ram" not in relaxed:
        exact = result[result["ram_gb"] == ram_query]
        if not exact.empty:
            result = exact
        else:
            available_rams = sorted(result[result["ram_gb"] > 0]["ram_gb"].unique().tolist())
            if available_rams:
                closest_ram = min(available_rams, key=lambda x: abs(x - ram_query))
                result = result[result["ram_gb"] == closest_ram]
            else:
                gte = result[result["ram_gb"] >= ram_query]
                if not gte.empty: result = gte

    # FILTRE 5 : Processeur
    if processor and "processor" not in relaxed:
        proc_f = result[
            result["name"].str.lower().str.contains(processor, na=False) |
            result["description"].str.lower().str.contains(processor, na=False)
        ]
        if not proc_f.empty:
            result = proc_f
        else:
            cat_base = df.copy()
            if category:
                cat_base = cat_base[cat_base["category_name"].str.lower() == category.lower()]
            proc_cat = cat_base[
                cat_base["name"].str.lower().str.contains(processor, na=False) |
                cat_base["description"].str.lower().str.contains(processor, na=False)
            ]
            if not proc_cat.empty:
                proc_cat = proc_cat.copy()
                proc_cat["score"] = scores[proc_cat.index]
                result = proc_cat

    # FILTRE 6 : Stockage
    if storage and "storage" not in relaxed:
        if storage >= 1000:
            tb = storage // 1000
            patterns = [f"{tb}to", f"{tb} to", f"ssd {tb}to", f"ssd {tb} to", f"{storage}go"]
        else:
            patterns = [f"{storage}go", f"{storage} go", f"ssd {storage}", f"ssd{storage}"]
        stor_f = result[result["description"].str.lower().apply(
            lambda d: any(p in d for p in patterns))]
        if not stor_f.empty: result = stor_f

    # FILTRE 7 : Budget
    print(f"💰 budget={budget} | relaxed={relaxed} | prix_max={profile['prix_max']}")
    if budget and "budget" not in relaxed:
        result = result[result["price"] <= budget]
    else:
        result = result[result["price"] <= profile["prix_max"]]

    available = result[result["stock"] > 0]
    final     = available if not available.empty else result

    if final.empty:
        return pd.DataFrame(), df, brand, "not_found", missing_msg or "❌ Aucun produit ne correspond à vos critères."

    filtered = final[final["score"] >= SCORE_THRESHOLD]

    # Fallback si score trop bas
    if filtered.empty:
        base = df.copy()
        if category:
            base = base[base["category_name"].str.lower() == category.lower()]
        if brand and "brand" not in relaxed:
            bf = base[base["name"].str.contains(brand, case=False, na=False)]
            if not bf.empty: base = bf
        if budget and "budget" not in relaxed:
            base = base[base["price"] <= budget]
        base = base[base["stock"] > 0]
        if not base.empty:
            base = base.copy()
            base["score"] = scores[base.index]
            filtered = base

    if filtered.empty:
        return pd.DataFrame(), df, brand, "not_found", missing_msg or "❌ Aucun produit trouvé."

    cols = ["id","name","price","stock","description","category_name","score","ram_gb","imageUrl"]
    existing = [c for c in cols if c in filtered.columns]
    return filtered.nlargest(top_k, "score")[existing], df, brand, None, missing_msg


# ─────────────────────────────────────────────────────────────
#  9. COMPARAISON
# ─────────────────────────────────────────────────────────────
def compare_products(products, orig_query):
    profile_name = detect_profile(orig_query)
    category     = detect_category(orig_query)
    if category == "telephone portable": profile_name = "smartphone"

    weights = {
        "developpeur": {"ram":0.4,"score":0.3,"price":0.3},
        "etudiant":    {"ram":0.2,"score":0.3,"price":0.5},
        "gaming":      {"ram":0.4,"score":0.4,"price":0.2},
        "smartphone":  {"ram":0.3,"score":0.4,"price":0.3},
        "general":     {"ram":0.2,"score":0.5,"price":0.3},
    }
    w = weights.get(profile_name, weights["general"])

    prices = [p["price"] for p in products]
    rams   = [p.get("ram_gb",0) for p in products]
    scores = [p["score"] for p in products]
    max_price = max(prices) if prices else 1
    max_ram   = max(rams)   if max(rams) > 0 else 1
    max_score = max(scores) if scores else 1

    for p in products:
        p["total_score"] = round(
            w["score"] * (p["score"]        / max_score) +
            w["ram"]   * (p.get("ram_gb",0) / max_ram)   +
            w["price"] * (1 - p["price"]    / max_price), 4
        )

    ranked = sorted(products, key=lambda x: (x.get("stock",1)==0, -x["total_score"]))
    best   = ranked[0]
    reasons = [f"Score IA : {round(best['score'],3)}"]
    if best.get("ram_gb",0) == max(rams) and max(rams)>0:
        reasons.append(f"Plus grande RAM : {best['ram_gb']}Go")
    if best["price"] == min(prices):
        reasons.append(f"Meilleur prix : {best['price']} TND")
    if best.get("stock",1) > 0:
        reasons.append(f"Disponible ({int(best.get('stock',0))} unités)")
    return {"profile":profile_name,"best":best,"ranked":ranked,"reasons":reasons}


# ─────────────────────────────────────────────────────────────
#  10. SESSION
# ─────────────────────────────────────────────────────────────
session = {"last_results": None, "last_query": None}


# ─────────────────────────────────────────────────────────────
#  11. ROUTES
# ─────────────────────────────────────────────────────────────
@app.route("/recommend", methods=["POST"])
def recommend():
    data  = request.json
    query = data.get("query","")
    results, _, brand, error, _ = smart_search(query, top_k=5)
    if error:
        return jsonify([])
    products = []
    for _, row in results.iterrows():
        products.append({
            "id":          int(row["id"]),
            "name":        row["name"],
            "price":       float(row["price"]),
            "stock":       int(row.get("stock",0)),
            "stockLabel":  stock_label(row.get("stock",0)),
            "description": row.get("description",""),
            "categoryName":row.get("category_name",""),
            "ramGb":       int(row.get("ram_gb",0)),
            "score":       round(float(row["score"]),4),
            "imageUrl":    row.get("imageUrl",""),
        })
    return jsonify(products)


@app.route("/chat", methods=["POST"])
def chat():
    data         = request.json
    raw_query    = data.get("query","")
    # ✅ Correction des fautes AVANT tout traitement
    query        = correct_query(raw_query)

    # ── Comparaison ─────────────────────────────────────────
    if is_compare_request(query) and session["last_results"] is not None:
        products = session["last_results"].to_dict("records")
        result   = compare_products(products, session["last_query"])
        return jsonify({
            "type":    "comparison",
            "profile": result["profile"],
            "best": {
                "id":          int(result["best"]["id"]),
                "name":        result["best"]["name"],
                "price":       float(result["best"]["price"]),
                "stock":       int(result["best"].get("stock",0)),
                "stockLabel":  stock_label(result["best"].get("stock",0)),
                "description": result["best"].get("description",""),
                "ramGb":       int(result["best"].get("ram_gb",0)),
                "totalScore":  result["best"]["total_score"],
                "imageUrl":    result["best"].get("imageUrl",""),
            },
            "ranked": [{
                "id":         int(p["id"]),
                "name":       p["name"],
                "price":      float(p["price"]),
                "stock":      int(p.get("stock",0)),
                "stockLabel": stock_label(p.get("stock",0)),
                "ramGb":      int(p.get("ram_gb",0)),
                "totalScore": p["total_score"],
                "imageUrl":   p.get("imageUrl",""),
            } for p in result["ranked"]],
            "reasons": result["reasons"],
        })

    # ── Bundle ──────────────────────────────────────────────
    if is_bundle_request(query) and session["last_results"] is not None:
        last = session["last_results"]
        if not last.empty:
            product_name = str(last.iloc[0]["name"])
            bundles      = get_bundle_from_spring(product_name)
            if bundles:
                return jsonify({"type":"bundle","productName":product_name,"bundles":bundles})
            return jsonify({"type":"not_found","message":"❌ Aucun accessoire trouvé.","products":[]})

    # ── Recherche principale ─────────────────────────────────
    results, df_all, brand, error, missing_msg = smart_search(query, top_k=10)

    if error == "brand_not_found":
        return jsonify({
            "type":     "brand_not_found",
            "brand":    brand,
            "message":  f"❌ Aucun produit de la marque **{brand}** dans notre catalogue.\n\n💡 Marques disponibles : Asus, Lenovo, Dell, HP, Samsung, Xiaomi...",
            "products": []
        })

    if error == "not_found":
        return jsonify({
            "type":     "not_found",
            "message":  missing_msg or "❌ Aucun produit trouvé. Essayez avec d'autres critères.",
            "products": []
        })

    if results.empty or results["score"].max() < SCORE_THRESHOLD:
        return jsonify({
            "type":    "not_found",
            "message": "❌ Aucun produit trouvé. Essayez avec d'autres mots-clés.",
            "products":[]
        })

    products_out = []
    for _, row in results.iterrows():
        products_out.append({
            "id":          int(row["id"]),
            "name":        str(row["name"]),
            "price":       float(row["price"]),
            "stock":       int(row.get("stock",0)),
            "stockLabel":  stock_label(row.get("stock",0)),
            "description": str(row.get("description","")),
            "categoryName":str(row.get("category_name","")),
            "ramGb":       int(row.get("ram_gb",0)),
            "score":       round(float(row["score"]),4),
            "imageUrl":    str(row.get("imageUrl","")),
        })
    products_out = products_out[:5]

    session["last_results"] = results.head(5)
    session["last_query"]   = query

    # Résumé filtres actifs
    tags = []
    if detect_category(query):   tags.append(f"📂 {detect_category(query)}")
    if detect_brand(query):      tags.append(f"🏷️ {detect_brand(query)}")
    if extract_ram_query(query): tags.append(f"💾 {extract_ram_query(query)}Go RAM")
    if extract_processor(query): tags.append(f"⚙️ {extract_processor(query)}")
    if extract_storage(query):   tags.append(f"💿 {extract_storage(query)}Go SSD")
    if extract_budget(query):    tags.append(f"💰 max {extract_budget(query)} TND")
    filters_summary = " • ".join(tags) if tags else None

    return jsonify({
        "type":     "search",
        "profile":  detect_profile(query),
        "category": detect_category(query),
        "brand":    detect_brand(query),
        "budget":   extract_budget(query),
        "filters":  filters_summary,
        "warning":  missing_msg,
        "products": products_out,
    })


@app.route("/cache/refresh", methods=["POST"])
def refresh_cache():
    with _cache["lock"]:
        _cache["last_load"] = 0
    get_cached_data()
    return jsonify({"status":"ok","message":"Cache rechargé."})


@app.route("/health", methods=["GET"])
def health():
    df, _ = get_cached_data()
    return jsonify({
        "status":      "ok",
        "model":       "intfloat/multilingual-e5-base",
        "products":    len(df) if df is not None else 0,
        "cache_age_s": round(time.time() - _cache["last_load"]),
    })


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)