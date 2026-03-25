import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StoreComponent } from './store.component';
import { StoreService } from '../../Services/store-service';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Store } from '../../models/store';

// ── Données de test ────────────────────────────────────────
const mockStores: Store[] = [
  {
    id: 1, name: 'Tech Store', description: 'Electronics',
    active: true, createdAt: new Date(),
    productIds: [], productNames: [],
    advertisementIds: [], advertisementTitles: [],
    commissionIds: [], ruleIds: [], ruleTitles: []
  },
  {
    id: 2, name: 'Art Studio', description: 'Art supplies',
    active: false, createdAt: new Date(),
    productIds: [], productNames: [],
    advertisementIds: [], advertisementTitles: [],
    commissionIds: [], ruleIds: [], ruleTitles: []
  }
];

// ── Faux service ───────────────────────────────────────────
const fakeStoreService = {
  getAllStores: () => of(mockStores),
  deleteStore: (id: number) => of(null)
};

describe('StoreComponent', () => {
  let component: StoreComponent;
  let fixture: ComponentFixture<StoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StoreComponent, RouterTestingModule],
      providers: [
        { provide: StoreService, useValue: fakeStoreService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(StoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // déclenche ngOnInit
  });

  // ── Test 1 : le composant se crée ─────────────────────
  it('devrait créer le composant', () => {
    expect(component).toBeTruthy();
  });

  // ── Test 2 : les stores sont chargés ──────────────────
  it('devrait charger la liste des stores', () => {
    expect(component.stores.length).toBe(2);
  });

  // ── Test 3 : compter les stores actifs ────────────────
  it('devrait retourner 1 store actif', () => {
    expect(component.getActiveCount()).toBe(1);
  });

  // ── Test 4 : compter les stores inactifs ──────────────
  it('devrait retourner 1 store inactif', () => {
    expect(component.getInactiveCount()).toBe(1);
  });

  // ── Test 5 : couleur avatar ───────────────────────────
  it('devrait retourner une couleur hex pour getAvatarColor', () => {
    const color = component.getAvatarColor('Tech Store');
    expect(color).toMatch(/^#[0-9a-f]{6}$/i);
  });

  // ── Test 6 : supprimer un store si confirmé ───────────
  it('devrait supprimer un store si confirmé', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert').and.stub();
    component.deleteStore(mockStores[0]);
    expect(component).toBeTruthy();
  });

  // ── Test 7 : annuler la suppression ───────────────────
  it('ne devrait pas supprimer si annulé', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    const before = component.stores.length;
    component.deleteStore(mockStores[0]);
    expect(component.stores.length).toBe(before);
  });

});