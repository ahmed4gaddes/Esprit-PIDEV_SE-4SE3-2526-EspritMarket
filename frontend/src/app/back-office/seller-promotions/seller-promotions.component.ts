import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PromotionService, Promotion } from '../../Services/promotion.service';
import { StoreService } from '../../Services/store-service';
import { Store } from '../../models/store';

@Component({
  selector: 'app-seller-promotions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-fluid py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>🏷️ Gestion des Codes Promo</h2>
      </div>

      <!-- Store Selector -->
      <div class="mb-4" *ngIf="stores.length > 0">
        <label class="form-label fw-bold">Sélectionner une boutique :</label>
        <select class="form-select w-auto" (change)="onStoreChange($event)" [value]="selectedStoreId">
           <option *ngFor="let store of stores" [value]="store.id">{{ store.name }}</option>
        </select>
      </div>

      <div *ngIf="stores.length === 0" class="alert alert-warning">
         Vous ne possédez aucune boutique.
      </div>

      <div class="row" *ngIf="selectedStoreId">
        <!-- Create Form -->
        <div class="col-md-4">
          <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom">
              <h5 class="mb-0">Créer un Code Promo</h5>
            </div>
            <div class="card-body">
              <form (ngSubmit)="onSubmit()" #promoForm="ngForm">
                <div class="mb-3">
                  <label class="form-label">Code</label>
                  <input type="text" class="form-control" name="code" [(ngModel)]="newPromo.code" required placeholder="Ex: SUMMER24" style="text-transform: uppercase;">
                </div>
                
                <div class="mb-3">
                  <label class="form-label">Type de réduction</label>
                  <select class="form-select" name="type" [(ngModel)]="newPromo.type" required>
                    <option value="PERCENTAGE">Pourcentage (%)</option>
                    <option value="FIXED_AMOUNT">Montant Fixe (TND)</option>
                  </select>
                </div>

                <div class="mb-3">
                  <label class="form-label">Valeur de la réduction</label>
                  <input type="number" class="form-control" name="discountValue" [(ngModel)]="newPromo.discountValue" required min="1">
                </div>

                <div class="mb-3">
                  <label class="form-label">Date d'expiration</label>
                  <input type="datetime-local" class="form-control" name="expiresAt" [(ngModel)]="newPromo.expiresAt" required>
                </div>

                <div class="mb-3">
                  <label class="form-label">Limite d'utilisation</label>
                  <input type="number" class="form-control" name="usageLimit" [(ngModel)]="newPromo.usageLimit" placeholder="0 pour illimité" min="0">
                  <small class="text-muted">Laissez 0 pour une utilisation illimitée</small>
                </div>

                <button type="submit" class="btn btn-primary w-100" [disabled]="!promoForm.valid || isSubmitting">
                  <span *ngIf="isSubmitting" class="spinner-border spinner-border-sm me-2"></span>
                  Créer le code
                </button>
              </form>
            </div>
          </div>
        </div>

        <!-- List -->
        <div class="col-md-8">
          <div class="card shadow-sm border-0">
            <div class="card-header bg-white border-bottom">
              <h5 class="mb-0">Vos Codes Promo</h5>
            </div>
            <div class="card-body p-0">
              <div class="table-responsive">
                <table class="table table-hover mb-0 align-middle">
                  <thead class="table-light">
                    <tr>
                      <th>Code</th>
                      <th>Réduction</th>
                      <th>Expiration</th>
                      <th>Utilisations</th>
                      <th>Statut</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr *ngFor="let promo of promotions">
                      <td><span class="badge bg-dark fs-6">{{ promo.code }}</span></td>
                      <td>
                        <span class="text-success fw-bold">
                          {{ promo.discountValue }} {{ promo.type === 'PERCENTAGE' ? '%' : 'TND' }}
                        </span>
                      </td>
                      <td>{{ promo.expiresAt | date:'medium' }}</td>
                      <td>{{ promo.usedCount || 0 }} / {{ promo.usageLimit === 0 ? '∞' : promo.usageLimit }}</td>
                      <td>
                        <span class="badge bg-success" *ngIf="isPromoActive(promo)">Actif</span>
                        <span class="badge bg-danger" *ngIf="!isPromoActive(promo)">Expiré/Épuisé</span>
                      </td>
                    </tr>
                    <tr *ngIf="promotions.length === 0">
                      <td colspan="5" class="text-center py-4 text-muted">Aucun code promo trouvé pour cette boutique.</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SellerPromotionsComponent implements OnInit {
  stores: Store[] = [];
  selectedStoreId: number | null = null;
  promotions: Promotion[] = [];
  
  newPromo: Promotion = {
    code: '',
    type: 'PERCENTAGE',
    discountValue: 10,
    expiresAt: '',
    usageLimit: 0
  };
  
  isSubmitting = false;

  constructor(
    private storeService: StoreService,
    private promotionService: PromotionService
  ) {}

  ngOnInit(): void {
    this.storeService.getMyStores().subscribe({
      next: (stores) => {
        this.stores = stores || [];
        if (this.stores.length > 0) {
           this.selectedStoreId = this.stores[0].id || null;
           this.loadPromotions();
        }
      }
    });
  }

  onStoreChange(event: any): void {
     this.selectedStoreId = Number(event.target.value);
     this.loadPromotions();
  }

  loadPromotions(): void {
    if (!this.selectedStoreId) return;
    this.promotionService.getPromotionsByStore(this.selectedStoreId).subscribe({
      next: (promos) => this.promotions = promos,
      error: (err) => console.error(err)
    });
  }

  onSubmit(): void {
    if (!this.selectedStoreId) return;
    this.isSubmitting = true;
    
    // Convert to uppercase
    this.newPromo.code = this.newPromo.code.toUpperCase().trim();
    
    this.promotionService.createPromotion(this.newPromo, this.selectedStoreId).subscribe({
      next: (promo) => {
        this.promotions.unshift(promo);
        this.isSubmitting = false;
        alert('Code promo créé avec succès !');
        // Reset form
        this.newPromo = {
          code: '',
          type: 'PERCENTAGE',
          discountValue: 10,
          expiresAt: '',
          usageLimit: 0
        };
      },
      error: (err) => {
        console.error(err);
        this.isSubmitting = false;
        alert('Erreur lors de la création du code promo (Le code existe peut-être déjà).');
      }
    });
  }
  
  isPromoActive(promo: Promotion): boolean {
    const isExpired = new Date(promo.expiresAt).getTime() < new Date().getTime();
    const isExhausted = promo.usageLimit > 0 && (promo.usedCount || 0) >= promo.usageLimit;
    return !isExpired && !isExhausted;
  }
}
