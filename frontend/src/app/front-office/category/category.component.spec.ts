import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CategoryComponent } from './category.component';
import { CategoryService } from '../../Services/category.service';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { Category } from '../../models/category';

// ── Données de test ──────────────────────────────────────
const fakeCategories: Category[] = [
  { id: 1, name: 'Laptops',   type: 'TECH',      description: 'Tech products' },
  { id: 2, name: 'Paintings', type: 'ART',       description: 'Art products'  },
  { id: 3, name: 'Courses',   type: 'EDUCATION', description: 'Edu products'  }
];

// ── Faux service ─────────────────────────────────────────
const fakeCategoryService = {
  getAllCategories: () => of(fakeCategories),
  deleteCategory:  (id: number) => of(null)
};

const fakeRouter = { navigate: jasmine.createSpy('navigate') };

// ════════════════════════════════════════════════════════
describe('CategoryComponent', () => {

  let component: CategoryComponent;
  let fixture: ComponentFixture<CategoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryComponent, RouterTestingModule],
      providers: [
        { provide: CategoryService, useValue: fakeCategoryService },
        { provide: Router,          useValue: fakeRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // déclenche ngOnInit
  });

  // ── Test 1 ────────────────────────────────────────────
  it('le composant doit être créé', () => {
    expect(component).toBeTruthy();
  });

  // ── Test 2 ────────────────────────────────────────────
  it('doit charger les catégories au démarrage', () => {
    expect(component.categories.length).toBe(3);
  });

  // ── Test 3 ────────────────────────────────────────────
  it('doit contenir les 6 types de catégories', () => {
    expect(component.categoryTypes.length).toBe(6);
    expect(component.categoryTypes).toContain('TECH');
    expect(component.categoryTypes).toContain('ART');
  });

  // ── Test 4 ────────────────────────────────────────────
  it('getTypeCount doit compter correctement par type', () => {
    expect(component.getTypeCount('TECH')).toBe(1);
    expect(component.getTypeCount('ART')).toBe(1);
    expect(component.getTypeCount('DIGITAL')).toBe(0); // aucun
  });

  // ── Test 5 ────────────────────────────────────────────
  it('ne doit PAS supprimer si on clique Annuler', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    const spyDelete = spyOn(fakeCategoryService, 'deleteCategory').and.callThrough();

    component.deleteCategory(1);

    expect(spyDelete).not.toHaveBeenCalled();
  });

  // ── Test 6 ────────────────────────────────────────────
  it('doit supprimer la catégorie si on clique OK', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(fakeCategoryService, 'deleteCategory').and.returnValue(of(null as any));

    component.deleteCategory(1);

    // la catégorie id=1 doit être retirée de la liste
    expect(component.categories.find(c => c.id === 1)).toBeUndefined();
    expect(component.categories.length).toBe(2);
  });

  // ── Test 7 ────────────────────────────────────────────
  it('doit gérer une erreur de chargement sans planter', () => {
    const consoleSpy = spyOn(console, 'error').and.stub();
    spyOn(fakeCategoryService, 'getAllCategories').and.returnValue(
      throwError(() => ({ status: 500, url: '/categories' }))
    );
    component.loadCategories();
    expect(consoleSpy).toHaveBeenCalled();
  });

  // ── Test 8 ────────────────────────────────────────────
  it('editCategory doit naviguer vers la page édition', () => {
    component.editCategory(2);
    expect(fakeRouter.navigate).toHaveBeenCalledWith(['/admin/categories/edit', 2]);
  });

});