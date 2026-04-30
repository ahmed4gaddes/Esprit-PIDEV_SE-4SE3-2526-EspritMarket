# Implémentation des Fonctionnalités Avancées (F1, F2, F6, F7)

Ce document détaille le plan d'implémentation pour les quatre fonctionnalités demandées. Ces ajouts vont nécessiter des modifications à la fois sur le backend (Spring Boot) et sur le frontend (Angular).

## User Review Required

> [!IMPORTANT]
> L'implémentation touche à la logique de création de commande (OrderService) pour appliquer les réductions (Promotions F2 et Fidélité F7).
> Veuillez valider la règle de calcul suivante :
> - **Fidélité (F7)** : 1 TND dépensé = 1 Point gagné. 100 Points utilisables = 5 TND de réduction.
> - **Priorité des réductions** : Le code promo s'applique d'abord, puis les points de fidélité s'appliquent sur le reste.

## Proposed Changes

### F1: Alerte Retour en Stock

Permet aux utilisateurs de s'abonner pour recevoir un email quand un produit en rupture revient en stock.

#### [NEW] Backend Entities & Logic
- Création de l'entité `StockAlert` (userId, productId, isNotified).
- Création du `StockAlertRepository` et du `StockAlertController` (`POST /Product/{id}/notify-me`).
- Modification de `StockMovementService` : lors d'un incrément de stock (`type == IN`), si le stock passe de 0 à un nombre positif, récupérer les `StockAlert` non notifiées, envoyer un email via `JavaMailSender` et marquer comme `notified = true`.

#### [MODIFY] Frontend UI
- Mise à jour de `product.service.ts` pour inclure la méthode `notifyMe(productId)`.
- Ajout d'un bouton "M'alerter du retour en stock" sur les produits affichés avec le label "RUPTURE DE STOCK" (dans le composant des produits).

---

### F2: Promotions & Codes Promo

Permet aux vendeurs de créer des codes promo applicables lors du paiement.

#### [NEW] Backend Entities & Logic
- Création de l'enum `DiscountType` (`PERCENTAGE`, `FIXED`).
- Création de l'entité `Promotion` (code, type, value, expiresAt, usageLimit, usedCount, store).
- Création du `PromotionRepository` et `PromotionController` (CRUD vendeur).

#### [MODIFY] Checkout Logic (Backend)
- Mise à jour de `OrderRequestDTO` pour inclure le champ `promoCode`.
- Modification de `OrderServiceImpl.createOrderFromCart()` pour valider le code promo, réduire le montant total de la commande, et incrémenter le compteur d'utilisation (`usedCount`).

#### [MODIFY] Frontend UI
- Frontend Acheteur : Ajout d'un champ "Code Promo" dans la modale/section de checkout (panier) dans `customer-dashboard.component.html`.

---

### F6: Analytics Vendeur Avancées

Tableau de bord pour les vendeurs fournissant des KPIs de leurs ventes.

#### [NEW] Backend Analytics Logic
- Création du DTO `SellerAnalyticsDTO` (revenu total, nombre de commandes, note moyenne, nombre de produits).
- Ajout d'une route `GET /Store/{storeId}/analytics` dans `RestControllerStore`.
- Implémentation du calcul dans `ServiceStore.java` :
  - Calculer les revenus en itérant sur les `OrderItem` liés aux produits de la boutique (en ignorant les commandes annulées).
  - Récupérer la note moyenne du vendeur via `RateService`.

#### [MODIFY] Frontend Seller Dashboard
- Connexion du composant `seller-analytics.component.ts` (déjà existant mais avec données mockées) à la nouvelle API `/analytics`.

---

### F7: Programme de Fidélité (Points & Récompenses)

Récompense les achats par des points convertibles en réduction.

#### [NEW] Backend Entities & Logic
- Création de l'entité `LoyaltyPoints` (userId, points).
- Création du `LoyaltyController` (`GET /loyalty/my`).

#### [MODIFY] Order Checkout Logic
- Mise à jour de `OrderRequestDTO` pour inclure `boolean useLoyaltyPoints`.
- Modification de `OrderServiceImpl.createOrderFromCart()` :
  - Si `useLoyaltyPoints` est vrai, convertir les points disponibles en réduction (ex: 100 points = 5 TND) et déduire du total.
  - À la fin de la commande, générer de nouveaux points de fidélité pour le client (Total de la commande = nombre de points gagnés).

#### [MODIFY] Frontend Checkout UI
- Frontend Acheteur : Affichage du solde de points actuel au checkout et case à cocher "Utiliser mes points pour X TND de réduction" dans le panier (`customer-dashboard.component.html`).

## Verification Plan

### Automated Tests
1. Compilation Maven du backend.
2. Build Angular du frontend (`npm run build`).

### Manual Verification
1. Créer une commande avec un code promo pour vérifier la réduction.
2. Tester le checkout en cochant "Utiliser mes points de fidélité".
3. Mettre un produit à 0 stock, s'inscrire à l'alerte, incrémenter le stock et vérifier l'envoi de l'email dans les logs.
4. Se connecter avec un compte Vendeur et consulter le tableau de bord Analytics pour valider que les chiffres remontent bien depuis la base de données.
