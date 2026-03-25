import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductFormComponent } from './product-form.component';
import { ProductService } from '../../Services/product.service';
import { StoreService } from '../../Services/store-service';
import { CategoryService } from '../../Services/category.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

// ── Fausses données ──────────────────────────────────────
const fakeProduct = {
  id: 1, name: 'MacBook Pro', description: 'Laptop Apple',
  price: 2500, stock: 10, active: true,
  storeId: 1, categoryId: 1, createdAt: new Date()
};

const fakeStores     = [{ id: 1, name: 'Tech Store' }];
const fakeCategories = [{ id: 1, name: 'Laptops', type: 'TECH' }];

// ── Faux services ────────────────────────────────────────
const fakeProductService = {
  getProductById: (id: number) => of(fakeProduct),
  addProduct:     (p: any)     => of(null as any),
  updateProduct:  (p: any, id: number) => of(null as any)
};

const fakeStoreService = {
  getAllStores: () => of(fakeStores)
};

const fakeCategoryService = {
  getAllCategories: () => of(fakeCategories)
};

const fakeRouter = {
  navigate:      jasmine.createSpy('navigate'),
  navigateByUrl: jasmine.createSpy('navigateByUrl')
};

// ════════════════════════════════════════════════════════
// SUITE 1 — Mode AJOUT (pas d'id)
// ════════════════════════════════════════════════════════
describe('ProductFormComponent — Mode Ajout', () => {

  let component: ProductFormComponent;
  let fixture: ComponentFixture<ProductFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductFormComponent, RouterTestingModule],
      providers: [
        { provide: ProductService,      useValue: fakeProductService  },
        { provide: StoreService, useValue: fakeStoreService    },
        { provide: CategoryService,     useValue: fakeCategoryService },
        { provide: Router,              useValue: fakeRouter          },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: {} } } // pas d'id = mode ajout
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Test 1 ──────────────────────────────────────────
  it('le composant doit être créé', () => {
    expect(component).toBeTruthy();
  });

  // ── Test 2 ──────────────────────────────────────────
  it('le formulaire doit être invalide si vide', () => {
    expect(component.productForm.valid).toBeFalse();
  });

  // ── Test 3 ──────────────────────────────────────────
  it('doit charger la liste des stores au démarrage', () => {
    expect(component.stores.length).toBe(1);
  });

  // ── Test 4 ──────────────────────────────────────────
  it('doit charger la liste des catégories au démarrage', () => {
    expect(component.categories.length).toBe(1);
  });

  // ── Test 5 ──────────────────────────────────────────
  it('le formulaire doit être valide avec toutes les données correctes', () => {
    component.productForm.patchValue({
      name: 'MacBook Pro', price: 2500,
      stock: 10, storeId: 1, categoryId: 1
    });
    expect(component.productForm.valid).toBeTrue();
  });

  // ── Test 6 ──────────────────────────────────────────
  it('le nom doit avoir au moins 2 caractères', () => {
    component.productForm.patchValue({ name: 'A' });
    expect(component.name?.errors?.['minlength']).toBeTruthy();
  });

  // ── Test 7 ──────────────────────────────────────────
  it('le prix ne peut pas être négatif', () => {
    component.productForm.patchValue({ price: -10 });
    expect(component.price?.errors?.['min']).toBeTruthy();
  });

  // ── Test 8 ──────────────────────────────────────────
  it('le stock ne peut pas être négatif', () => {
    component.productForm.patchValue({ stock: -5 });
    expect(component.stock?.errors?.['min']).toBeTruthy();
  });

  // ── Test 9 ──────────────────────────────────────────
  it('le store est obligatoire', () => {
    component.productForm.patchValue({ storeId: null });
    expect(component.storeId?.errors?.['required']).toBeTruthy();
  });

  // ── Test 10 ─────────────────────────────────────────
  it('la catégorie est obligatoire', () => {
    component.productForm.patchValue({ categoryId: null });
    expect(component.categoryId?.errors?.['required']).toBeTruthy();
  });

  // ── Test 11 ─────────────────────────────────────────
  it('doit appeler addProduct si le formulaire est valide', () => {
    spyOn(window, 'alert').and.stub();
    const spyAdd = spyOn(fakeProductService, 'addProduct').and.returnValue(of(null as any));

    component.productForm.patchValue({
      name: 'MacBook Pro', price: 2500,
      stock: 10, storeId: 1, categoryId: 1
    });
    component.onSubmit();

    expect(spyAdd).toHaveBeenCalledTimes(1);
  });

  // ── Test 12 ─────────────────────────────────────────
  it('ne doit PAS appeler addProduct si le formulaire est invalide', () => {
    const spyAdd = spyOn(fakeProductService, 'addProduct').and.callThrough();
    component.productForm.patchValue({ name: '', storeId: null, categoryId: null });

    component.onSubmit();

    expect(spyAdd).not.toHaveBeenCalled();
  });

  // ── Test 13 ─────────────────────────────────────────
  it('goBack doit naviguer vers /admin/products', () => {
    component.goBack();
    expect(fakeRouter.navigate).toHaveBeenCalledWith(['/admin/products']);
  });

});

// ════════════════════════════════════════════════════════
// SUITE 2 — Mode MODIFICATION (avec id)
// ════════════════════════════════════════════════════════
describe('ProductFormComponent — Mode Modification', () => {

  let component: ProductFormComponent;
  let fixture: ComponentFixture<ProductFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductFormComponent, RouterTestingModule],
      providers: [
        { provide: ProductService,      useValue: fakeProductService  },
        { provide: StoreService, useValue: fakeStoreService    },
        { provide: CategoryService,     useValue: fakeCategoryService },
        { provide: Router,              useValue: fakeRouter          },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { id: 1 } } } // id = 1 → mode modification
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Test 14 ─────────────────────────────────────────
  it('doit charger les données du produit à modifier', () => {
    expect(component.productForm.get('name')?.value).toBe('MacBook Pro');
    expect(component.productForm.get('price')?.value).toBe(2500);
  });

  // ── Test 15 ─────────────────────────────────────────
  it('doit appeler updateProduct lors de la soumission', () => {
    spyOn(window, 'alert').and.stub();
    const spyUpdate = spyOn(fakeProductService, 'updateProduct').and.returnValue(of(null as any));

    component.onSubmit();

    expect(spyUpdate).toHaveBeenCalledTimes(1);
  });

  // ── Test 16 ─────────────────────────────────────────
  it('doit naviguer vers /user/products après modification', () => {
    spyOn(window, 'alert').and.stub();
    spyOn(fakeProductService, 'updateProduct').and.returnValue(of(null as any));

    component.onSubmit();

    expect(fakeRouter.navigateByUrl).toHaveBeenCalledWith('/user/products');
  });

});