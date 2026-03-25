import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CategoryFormComponent } from './category-form.component';
import { CategoryService } from '../../Services/category.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

// ── Fausse catégorie existante ───────────────────────────
const fakeCategory = {
  id: 1, name: 'Laptops', type: 'TECH', description: 'Tech products'
};

// ── Faux service ─────────────────────────────────────────
const fakeCategoryService = {
  getCategoryById: (id: number) => of(fakeCategory),
  addCategory:     (cat: any)   => of(null as any),
  updateCategory:  (cat: any, id: number) => of(null as any)
};

const fakeRouter = {
  navigate:      jasmine.createSpy('navigate'),
  navigateByUrl: jasmine.createSpy('navigateByUrl')
};

// ════════════════════════════════════════════════════════
// SUITE 1 — Mode AJOUT (pas d'id)
// ════════════════════════════════════════════════════════
describe('CategoryFormComponent — Mode Ajout', () => {

  let component: CategoryFormComponent;
  let fixture: ComponentFixture<CategoryFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryFormComponent, RouterTestingModule],
      providers: [
        { provide: CategoryService, useValue: fakeCategoryService },
        { provide: Router,          useValue: fakeRouter },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: {} } } // pas d'id = mode ajout
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CategoryFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Test 1 ──────────────────────────────────────────
  it('le composant doit être créé', () => {
    expect(component).toBeTruthy();
  });

  // ── Test 2 ──────────────────────────────────────────
  it('le formulaire doit être vide au départ', () => {
    expect(component.categoryForm.get('name')?.value).toBe('');
    expect(component.categoryForm.get('type')?.value).toBeNull();
  });

  // ── Test 3 ──────────────────────────────────────────
  it('le formulaire doit être invalide si vide', () => {
    expect(component.categoryForm.valid).toBeFalse();
  });

  // ── Test 4 ──────────────────────────────────────────
  it('le formulaire doit être valide avec des données correctes', () => {
    component.categoryForm.patchValue({ name: 'Laptops', type: 'TECH' });
    expect(component.categoryForm.valid).toBeTrue();
  });

  // ── Test 5 ──────────────────────────────────────────
  it('le nom doit avoir au moins 2 caractères', () => {
    component.categoryForm.patchValue({ name: 'A' }); // trop court
    expect(component.name?.errors?.['minlength']).toBeTruthy();
  });

  // ── Test 6 ──────────────────────────────────────────
  it('le type est obligatoire', () => {
    component.categoryForm.patchValue({ name: 'Laptops', type: null });
    expect(component.type?.errors?.['required']).toBeTruthy();
  });

  // ── Test 7 ──────────────────────────────────────────
  it('doit appeler addCategory si le formulaire est valide', () => {
    spyOn(window, 'alert').and.stub();
    const spyAdd = spyOn(fakeCategoryService, 'addCategory').and.returnValue(of(null as any));

    component.categoryForm.patchValue({ name: 'Laptops', type: 'TECH' });
    component.onSubmit();

    expect(spyAdd).toHaveBeenCalledTimes(1);
  });

  // ── Test 8 ──────────────────────────────────────────
  it('ne doit PAS appeler addCategory si le formulaire est invalide', () => {
    const spyAdd = spyOn(fakeCategoryService, 'addCategory').and.callThrough();
    component.categoryForm.patchValue({ name: '', type: null });

    component.onSubmit();

    expect(spyAdd).not.toHaveBeenCalled();
  });

  // ── Test 9 ──────────────────────────────────────────
  it('goBack doit naviguer vers /admin/categories', () => {
    component.goBack();
    expect(fakeRouter.navigate).toHaveBeenCalledWith(['/admin/categories']);
  });

  // ── Test 10 ─────────────────────────────────────────
  it('doit contenir les 6 types disponibles', () => {
    expect(component.categoryTypes.length).toBe(6);
    expect(component.categoryTypes).toContain('DIGITAL');
    expect(component.categoryTypes).toContain('EDUCATION');
  });

});

// ════════════════════════════════════════════════════════
// SUITE 2 — Mode MODIFICATION (avec id)
// ════════════════════════════════════════════════════════
describe('CategoryFormComponent — Mode Modification', () => {

  let component: CategoryFormComponent;
  let fixture: ComponentFixture<CategoryFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryFormComponent, RouterTestingModule],
      providers: [
        { provide: CategoryService, useValue: fakeCategoryService },
        { provide: Router,          useValue: fakeRouter },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { id: 1 } } } // id = 1 → mode modification
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CategoryFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Test 11 ─────────────────────────────────────────
  it('doit charger les données de la catégorie à modifier', () => {
    expect(component.categoryForm.get('name')?.value).toBe('Laptops');
    expect(component.categoryForm.get('type')?.value).toBe('TECH');
  });

  // ── Test 12 ─────────────────────────────────────────
  it('doit appeler updateCategory lors de la soumission', () => {
    spyOn(window, 'alert').and.stub();
    const spyUpdate = spyOn(fakeCategoryService, 'updateCategory').and.returnValue(of(null as any));

    component.onSubmit();

    expect(spyUpdate).toHaveBeenCalledTimes(1);
  });

  // ── Test 13 ─────────────────────────────────────────
  it('doit naviguer vers /user/category après modification', () => {
    spyOn(window, 'alert').and.stub();
    spyOn(fakeCategoryService, 'updateCategory').and.returnValue(of(null as any));

    component.onSubmit();

    expect(fakeRouter.navigateByUrl).toHaveBeenCalledWith('/user/category');
  });

});