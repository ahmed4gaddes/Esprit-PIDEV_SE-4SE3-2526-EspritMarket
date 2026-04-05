import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EventService } from './event.service';
import { Event as AppEvent, EventStatus, EventType } from '../models/event.model';

describe('EventService', () => {
    let service: EventService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/api/events';

    // Faux événement de test réutilisé dans tous les tests
    const fakeEvent: AppEvent = {
        id: 1,
        title: 'Festival ESPRIT',
        description: 'Grand festival annuel',
        date: new Date(),
        location: 'Campus ESPRIT',
        imageUrl: '',
        capacity: 200,
        ticketPrice: 15.0,
        type: EventType.PRODUCT_LAUNCH_EVENT,
        status: EventStatus.UPCOMING,
        ticketCount: 0
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [EventService]
        });
        service = TestBed.inject(EventService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== getAll ====================
    it('should get all events via GET', () => {
        let result: AppEvent[] | undefined;

        service.getAll().subscribe(events => result = events);

        const req = httpMock.expectOne(API_URL);
        expect(req.request.method).toBe('GET');
        req.flush([fakeEvent]);

        expect(result).toBeDefined();
        expect(result!.length).toBe(1);
        expect(result![0].title).toBe('Festival ESPRIT');
    });

    // ==================== getById ====================
    it('should get a single event by ID via GET', () => {
        let result: AppEvent | undefined;

        service.getById(1).subscribe(event => result = event);

        const req = httpMock.expectOne(`${API_URL}/1`);
        expect(req.request.method).toBe('GET');
        req.flush(fakeEvent);

        expect(result).toBeDefined();
        expect(result!.id).toBe(1);
        expect(result!.title).toBe('Festival ESPRIT');
    });

    // ==================== create ====================
    it('should create an event via POST', () => {
        const newEvent: Partial<AppEvent> = {
            title: 'Nouveau Concours',
            type: EventType.PRODUCT_LAUNCH_EVENT,
            capacity: 100
        };
        let result: AppEvent | undefined;

        service.create(newEvent).subscribe(event => result = event);

        const req = httpMock.expectOne(API_URL);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(newEvent);
        req.flush({ ...fakeEvent, title: 'Nouveau Concours' });

        expect(result).toBeDefined();
        expect(result!.title).toBe('Nouveau Concours');
    });

    // ==================== update ====================
    it('should update an event via PUT', () => {
        const updatedData: Partial<AppEvent> = { title: 'Festival ESPRIT 2026' };
        let result: AppEvent | undefined;

        service.update(1, updatedData).subscribe(event => result = event);

        const req = httpMock.expectOne(`${API_URL}/1`);
        expect(req.request.method).toBe('PUT');
        req.flush({ ...fakeEvent, ...updatedData });

        expect(result).toBeDefined();
        expect(result!.title).toBe('Festival ESPRIT 2026');
    });

    // ==================== updateStatus ====================
    it('should update event status via PUT', () => {
        let result: AppEvent | undefined;

        service.updateStatus(1, EventStatus.ONGOING).subscribe(event => result = event);

        const req = httpMock.expectOne(`${API_URL}/1/status?status=ONGOING`);
        expect(req.request.method).toBe('PUT');
        req.flush({ ...fakeEvent, status: EventStatus.ONGOING });

        expect(result).toBeDefined();
        expect(result!.status).toBe(EventStatus.ONGOING);
    });

    // ==================== delete ====================
    it('should delete an event via DELETE', () => {
        let deleteCompleted = false;

        service.delete(1).subscribe(() => deleteCompleted = true);

        const req = httpMock.expectOne(`${API_URL}/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);

        expect(deleteCompleted).toBeTrue();
    });

    // ==================== getByStore ====================
    it('should get events by store ID via GET', () => {
        let result: AppEvent[] | undefined;

        service.getByStore(5).subscribe(events => result = events);

        const req = httpMock.expectOne(`${API_URL}/store/5`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeEvent]);

        expect(result).toBeDefined();
        expect(result!.length).toBe(1);
    });
});
