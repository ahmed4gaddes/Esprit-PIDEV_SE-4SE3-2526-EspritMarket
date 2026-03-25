import { ComponentFixture, TestBed } from '@angular/core/testing';
//import { StockComponent } from './stock.component';
import { StockComponent } from './stock.component';



import { StockService } from '../../Services/stock.service';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { Stock} from '../../models/stock';

// ── Données de test ──────────────────────────────────────
const fakeMovements: Stock[] = [
  { id: 1, quantity: 50,   type: 'IN',         productId: 1, productName: 'MacBook', date: new Date() },
  { id: 2, quantity: 10,   type: 'OUT',        productId: 2, productName: 'iPhone',  date: new Date() },
  { id: 3, quantity: 1717, type: 'IN',         productId: 1, productName: 'MacBook', date: new Date() },
  { id: 4, quantity: 5,    type: 'ADJUSTMENT', productId: 3, productName: 'AirPods', date: new Date() }
];

// ── Faux service ─────────────────────────────────────────
const fakeMovementService = {
  getAllMovements: () => of(fakeMovements),
  deleteMovement:  (id: number) => of(null)
};

const fakeRouter = { navigate: jasmine.createSpy('navigate') };

// ════════════════════════════════════════════════════════
describe('StockComponent', () => {

  let component: StockComponent;
  let fixture: ComponentFixture<StockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StockComponent, RouterTestingModule],
      providers: [
        { provide: StockService, useValue: fakeMovementService },
        { provide: Router,               useValue: fakeRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(StockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // déclenche ngOnInit
  });

  // ── Test 1 ────────────────────────────────────────────
  it('le composant doit être créé', () => {
    expect(component).toBeTruthy();
  });

  // ── Test 2 ────────────────────────────────────────────
  it('doit charger les mouvements au démarrage', () => {
    expect(component.stock.length).toBe(4);
  });

  // ── Test 3 ────────────────────────────────────────────
  it('doit compter correctement les mouvements IN', () => {
    expect(component.getInCount()).toBe(2);
  });

  // ── Test 4 ────────────────────────────────────────────
  it('doit compter correctement les mouvements OUT', () => {
    expect(component.getOutCount()).toBe(1);
  });

  // ── Test 5 ────────────────────────────────────────────
  it('ne doit PAS supprimer si on clique Annuler', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    const spyDelete = spyOn(fakeMovementService, 'deleteMovement').and.callThrough();

    component.deleteMovement(1);

    expect(spyDelete).not.toHaveBeenCalled();
  });

  // ── Test 6 ────────────────────────────────────────────
  it('doit supprimer le mouvement si on clique OK', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(fakeMovementService, 'deleteMovement').and.returnValue(of(null as any));

    component.deleteMovement(1);

    expect(component.stock.find(m => m.id === 1)).toBeUndefined();
    expect(component.stock.length).toBe(3);
  });

  // ── Test 7 ────────────────────────────────────────────
  it('doit gérer une erreur de chargement sans planter', () => {
    const consoleSpy = spyOn(console, 'error').and.stub();
    spyOn(fakeMovementService, 'getAllMovements').and.returnValue(
      throwError(() => ({ status: 500 }))
    );

    component.loadStock();

    expect(consoleSpy).toHaveBeenCalled();
  });

  // ── Test 8 ────────────────────────────────────────────
  it('editMovement doit naviguer vers la page édition', () => {
    component.editMovement(2);
    expect(fakeRouter.navigate).toHaveBeenCalledWith(['/admin/stock-movements/edit', 2]);
  });

});