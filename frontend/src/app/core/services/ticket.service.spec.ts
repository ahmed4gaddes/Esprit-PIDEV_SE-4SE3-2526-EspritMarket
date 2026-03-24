import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TicketService } from './ticket.service';
import { Ticket, TicketStatus } from '../models/ticket.model';

describe('TicketService', () => {
    let service: TicketService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/api/tickets';

    const fakeTicket: Ticket = {
        id: 1,
        price: 50.0,
        qrCode: 'uuid-1234',
        checkedIn: false,
        status: TicketStatus.VALID,
        purchaseDate: new Date(),
        eventId: 1,
        eventTitle: 'Tech Conference',
        userId: 1,
        userName: 'John Doe'
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [TicketService]
        });
        service = TestBed.inject(TicketService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== getById ====================
    it('should get a single ticket by ID via GET', () => {
        let result: Ticket | undefined;

        service.getById(1).subscribe(ticket => result = ticket);

        const req = httpMock.expectOne(`${API_URL}/1`);
        expect(req.request.method).toBe('GET');
        req.flush(fakeTicket);

        expect(result).toBeDefined();
        expect(result!.id).toBe(1);
        expect(result!.qrCode).toBe('uuid-1234');
    });

    // ==================== getByEvent ====================
    it('should get tickets by event ID via GET', () => {
        let result: Ticket[] | undefined;

        service.getByEvent(1).subscribe(tickets => result = tickets);

        const req = httpMock.expectOne(`http://localhost:8081/api/events/1/tickets`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeTicket]);

        expect(result).toBeDefined();
        expect(result!.length).toBe(1);
    });

    // ==================== getByUser ====================
    it('should get tickets by user ID via GET', () => {
        let result: Ticket[] | undefined;

        service.getByUser(1).subscribe(tickets => result = tickets);

        const req = httpMock.expectOne(`http://localhost:8081/api/users/1/tickets`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeTicket]);

        expect(result).toBeDefined();
        expect(result!.length).toBe(1);
    });

    // ==================== create ====================
    it('should create a ticket via POST', () => {
        const ticketRequest = { price: 50.0, userId: 1 };
        let result: Ticket | undefined;

        service.create(1, ticketRequest).subscribe(ticket => result = ticket);

        const req = httpMock.expectOne(`http://localhost:8081/api/events/1/tickets`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(ticketRequest);
        req.flush({ ...fakeTicket });

        expect(result).toBeDefined();
        expect(result!.id).toBe(1);
    });

    // ==================== checkIn ====================
    it('should check in a ticket via PUT', () => {
        let result: Ticket | undefined;

        service.checkIn(1).subscribe(ticket => result = ticket);

        const req = httpMock.expectOne(`${API_URL}/1/check-in`);
        expect(req.request.method).toBe('PUT');
        req.flush({ ...fakeTicket, checkedIn: true });

        expect(result).toBeDefined();
        expect(result!.checkedIn).toBeTrue();
    });

    // ==================== delete ====================
    it('should delete a ticket via DELETE', () => {
        let deleteCompleted = false;

        service.delete(1).subscribe(() => deleteCompleted = true);

        const req = httpMock.expectOne(`${API_URL}/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);

        expect(deleteCompleted).toBeTrue();
    });
});
