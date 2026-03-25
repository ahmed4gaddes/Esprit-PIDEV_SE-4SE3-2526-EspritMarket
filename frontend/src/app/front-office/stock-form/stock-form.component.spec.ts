import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StockFormComponent } from './stock-form.component';
import { StockService } from '../../Services/stock.service';
import { ProductService } from '../../Services/product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

// ── Fausses données ──────────────────────────────────────
const fakeMovement = {
  id: 1, quantity: 50, type: 'IN',
  productId: 1, productName: 'MacBook', date: new Date()
};

const fakeProducts = [
  { id: 1, name: 'MacBook Pro' },
  { id: 2, name: 'iPhone 15'  }
];

// ── Faux services ────────────────────────────────────────
const fakeMovementService = {
  getMovementById: (id: number) => of(fakeMovement),
  addMovement:     (m: any)     => of(null as any),
  updateMovement:  (m: any, id: number) => of(null as any)
};

const fakeProductService = {
  getAllProducts: () => of(fakeProducts)
};

const fakeRouter = {
  navigate:      jasmine.createSpy('navigate'),
  navigateByUrl: jasmine.createSpy('navigateByUrl')
};

// ════════════════════════════════════════════════════════
// SUITE 1 — Mode AJOUT (pas d'id)
// ════════════════════════════════════════════════════════
describe('StockFormComponent — Mode Ajout', () => {

  let component: StockFormComponent;
  let fixture: ComponentFixture<StockFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StockFormComponent, RouterTestingModule],
      providers: [
        { provide: StockService, useValue: fakeMovementService },
        { provide: ProductService,       useValue: fakeProductService  },
        { provide: Router,               useValue: fakeRouter          },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: {} } } // pas d'id = mode ajout
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(StockFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Test 1 ──────────────────────────────────────────
  it('le composant doit être créé', () => {
    expect(component).toBeTruthy();
  });

  // ── Test 2 ──────────────────────────────────────────
  it('le formulaire doit être invalide si vide', () => {
    expect(component.stockForm.valid).toBeFalse();
  });

  // ── Test 3 ──────────────────────────────────────────
  it('doit charger la liste des produits au démarrage', () => {
    expect(component.products.length).toBe(2);
  });

  // ── Test 4 ──────────────────────────────────────────
  it('doit contenir les 3 types de mouvements', () => {
    expect(component.movementTypes).toContain('IN');
    expect(component.movementTypes).toContain('OUT');
    expect(component.movementTypes).toContain('ADJUSTMENT');
  });

  // ── Test 5 ──────────────────────────────────────────
  it('le formulaire doit être valide avec les données correctes', () => {
    component.stockForm.patchValue({ quantity: 10, type: 'IN', productId: 1 });
    expect(component.stockForm.valid).toBeTrue();
  });

  // ── Test 6 ──────────────────────────────────────────
  it('la quantité doit être au minimum 1', () => {
    component.stockForm.patchValue({ quantity: 0 });
    expect(component.quantity?.errors?.['min']).toBeTruthy();
  });

  // ── Test 7 ──────────────────────────────────────────
  it('le type est obligatoire', () => {
    component.stockForm.patchValue({ type: null });
    expect(component.type?.errors?.['required']).toBeTruthy();
  });

  // ── Test 8 ──────────────────────────────────────────
  it('le produit est obligatoire', () => {
    component.stockForm.patchValue({ productId: null });
    expect(component.productId?.errors?.['required']).toBeTruthy();
  });

  // ── Test 9 ──────────────────────────────────────────
  it('doit appeler addMovement si le formulaire est valide', () => {
    spyOn(window, 'alert').and.stub();
    const spyAdd = spyOn(fakeMovementService, 'addMovement').and.returnValue(of(null as any));

    component.stockForm.patchValue({ quantity: 10, type: 'IN', productId: 1 });
    component.onSubmit();

    expect(spyAdd).toHaveBeenCalledTimes(1);
  });

  // ── Test 10 ─────────────────────────────────────────
  it('ne doit PAS appeler addMovement si le formulaire est invalide', () => {
    const spyAdd = spyOn(fakeMovementService, 'addMovement').and.callThrough();
    component.stockForm.patchValue({ quantity: 0, type: null, productId: null });

    component.onSubmit();

    expect(spyAdd).not.toHaveBeenCalled();
  });

  // ── Test 11 ─────────────────────────────────────────
  it('goBack doit naviguer vers /admin/stock-movements', () => {
    component.goBack();
    expect(fakeRouter.navigate).toHaveBeenCalledWith(['/admin/stock-movements']);
  });

});

// ════════════════════════════════════════════════════════
// SUITE 2 — Mode MODIFICATION (avec id)
// ════════════════════════════════════════════════════════
describe('StockMovementFormComponent — Mode Modification', () => {

  let component: StockFormComponent;
  let fixture: ComponentFixture<StockFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StockFormComponent, RouterTestingModule],
      providers: [
        { provide: StockService, useValue: fakeMovementService },
        { provide: ProductService,       useValue: fakeProductService  },
        { provide: Router,               useValue: fakeRouter          },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { id: 1 } } } // id = 1 → mode modification
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(StockFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Test 12 ─────────────────────────────────────────
  it('doit charger les données du mouvement à modifier', () => {
    expect(component.stockForm.get('quantity')?.value).toBe(50);
    expect(component.stockForm.get('type')?.value).toBe('IN');
  });

  // ── Test 13 ─────────────────────────────────────────
  it('doit appeler updateMovement lors de la soumission', () => {
    spyOn(window, 'alert').and.stub();
    const spyUpdate = spyOn(fakeMovementService, 'updateMovement').and.returnValue(of(null as any));

    component.onSubmit();

    expect(spyUpdate).toHaveBeenCalledTimes(1);
  });

  // ── Test 14 ─────────────────────────────────────────
  it('doit naviguer vers /user/stock-movements après modification', () => {
    spyOn(window, 'alert').and.stub();
    spyOn(fakeMovementService, 'updateMovement').and.returnValue(of(null as any));

    component.onSubmit();

    expect(fakeRouter.navigateByUrl).toHaveBeenCalledWith('/user/stock-movements');
  });

});