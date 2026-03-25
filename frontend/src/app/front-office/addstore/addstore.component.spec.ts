import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AddstoreComponent } from './addstore.component';
import { StoreServiceService } from '../../Services/store-service';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

// ── Faux service ───────────────────────────────────────────
const fakeStoreService = {
  getStoreById: () => of(null),
  addStore: () => of({ id: 1, name: 'New Store' }),
  updateStore: () => of({ id: 1, name: 'Updated Store' })
};

const fakeRouter = {
  navigate: jasmine.createSpy('navigate'),
  navigateByUrl: jasmine.createSpy('navigateByUrl')
};

// ══════════════════════════════════════════════════════════
// MODE AJOUT — pas d'id dans l'URL
// ══════════════════════════════════════════════════════════
describe('AddstoreComponent — Mode Ajout', () => {
  let component: AddstoreComponent;
  let fixture: ComponentFixture<AddstoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddstoreComponent, RouterTestingModule],
      providers: [
        { provide: StoreServiceService, useValue: fakeStoreService },
        { provide: Router, useValue: fakeRouter },
        { provide: ActivatedRoute, useValue: { snapshot: { params: {} } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AddstoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Test 1 : le composant se crée ─────────────────────
  it('devrait créer le composant', () => {
    expect(component).toBeTruthy();
  });

  // ── Test 2 : le formulaire démarre vide ───────────────
  it('le formulaire devrait être vide au départ', () => {
    expect(component.storeForm.get('name')?.value).toBe('');
    expect(component.storeForm.get('description')?.value).toBe('');
  });

  // ── Test 3 : le formulaire est invalide si vide ───────
  it('le formulaire devrait être invalide si vide', () => {
    expect(component.storeForm.valid).toBeFalse();
  });

  // ── Test 4 : le formulaire est valide avec bonnes valeurs
  it('le formulaire devrait être valide avec les bonnes valeurs', () => {
    component.storeForm.patchValue({
      name: 'Mon Store',
      description: 'Une description suffisamment longue'
    });
    expect(component.storeForm.valid).toBeTrue();
  });

  // ── Test 5 : erreur si nom trop court ─────────────────
  it('devrait avoir une erreur si le nom est trop court', () => {
    component.storeForm.patchValue({ name: 'AB' });
    expect(component.name?.errors?.['minlength']).toBeTruthy();
  });

  // ── Test 6 : erreur si description trop courte ────────
  it('devrait avoir une erreur si la description est trop courte', () => {
    component.storeForm.patchValue({ description: 'Court' });
    expect(component.description?.errors?.['minlength']).toBeTruthy();
  });

  // ── Test 7 : toggle actif par défaut ──────────────────
  it('le store devrait être actif par défaut', () => {
    expect(component.storeForm.get('active')?.value).toBeTrue();
  });

  // ── Test 8 : addStore appelé au submit ────────────────
  it('devrait appeler addStore quand le formulaire est valide', () => {
    spyOn(window, 'alert').and.stub();
    spyOn(fakeStoreService, 'addStore').and.returnValue(of(null as any));

    component.storeForm.patchValue({
      name: 'Mon Store',
      description: 'Une description suffisamment longue'
    });
    component.onSubmit();

    expect(fakeStoreService.addStore).toHaveBeenCalled();
  });

  // ── Test 9 : pas d'appel si formulaire invalide ───────
  it('ne devrait pas appeler addStore si le formulaire est invalide', () => {
    spyOn(fakeStoreService, 'addStore').and.returnValue(of(null as any));
    component.onSubmit();
    expect(fakeStoreService.addStore).not.toHaveBeenCalled();
  });

});

// ══════════════════════════════════════════════════════════
// MODE MODIFICATION — id présent dans l'URL
// ══════════════════════════════════════════════════════════
describe('AddstoreComponent — Mode Modification', () => {
  let component: AddstoreComponent;
  let fixture: ComponentFixture<AddstoreComponent>;

  const existingStore = {
    id: 1,
    name: 'Tech Store',
    description: 'Un magasin de technologie',
    active: true,
    createdAt: new Date()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddstoreComponent, RouterTestingModule],
      providers: [
        {
          provide: StoreServiceService,
          useValue: {
            getStoreById: () => of(existingStore),
            addStore: () => of(null as any),
            updateStore: () => of(null as any)
          }
        },
        { provide: Router, useValue: fakeRouter },
        { provide: ActivatedRoute, useValue: { snapshot: { params: { id: 1 } } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AddstoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Test 10 : le composant se crée ────────────────────
  it('devrait créer le composant', () => {
    expect(component).toBeTruthy();
  });

  // ── Test 11 : le formulaire est pré-rempli ────────────
  it('le formulaire devrait être pré-rempli avec les données du store', () => {
    expect(component.storeForm.get('name')?.value).toBe('Tech Store');
    expect(component.storeForm.get('description')?.value).toBe('Un magasin de technologie');
  });

  // ── Test 12 : l'id est bien récupéré ──────────────────
  it('devrait avoir l\'id = 1', () => {
    expect(component.id).toBe(1);
  });

  // ── Test 13 : navigation après modification ───────────
  it('devrait naviguer vers /store après modification', () => {
    spyOn(window, 'alert').and.stub();
    component.onSubmit();
    expect(fakeRouter.navigateByUrl).toHaveBeenCalledWith('/store');
  });

  // ── Test 14 : goBack navigue vers /stores ─────────────
  it('goBack() devrait naviguer vers /stores', () => {
    component.goBack();
    expect(fakeRouter.navigate).toHaveBeenCalledWith(['/stores']);
  });

});