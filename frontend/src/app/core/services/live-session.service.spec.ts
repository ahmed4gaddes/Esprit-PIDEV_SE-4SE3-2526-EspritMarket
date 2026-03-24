import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LiveSessionService } from './live-session.service';
import { LiveSession, LivePlatform, LiveSessionStatus } from '../models/live-session.model';

describe('LiveSessionService', () => {
    let service: LiveSessionService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/api/live-sessions';

    const fakeSession: LiveSession = {
        id: 1,
        title: 'Spring Boot Q&A',
        description: 'Live session about Spring',
        link: 'http://zoom.us/j/123456',
        platform: LivePlatform.ZOOM,
        status: LiveSessionStatus.SCHEDULED,
        scheduledAt: new Date(),
        eventId: 1,
        creatorId: 1
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [LiveSessionService]
        });
        service = TestBed.inject(LiveSessionService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== getAll ====================
    it('should get all live sessions via GET', () => {
        let result: LiveSession[] | undefined;

        service.getAll().subscribe(sessions => result = sessions);

        const req = httpMock.expectOne(API_URL);
        expect(req.request.method).toBe('GET');
        req.flush([fakeSession]);

        expect(result).toBeDefined();
        expect(result!.length).toBe(1);
        expect(result![0].title).toBe('Spring Boot Q&A');
    });

    // ==================== getById ====================
    it('should get a single live session by ID via GET', () => {
        let result: LiveSession | undefined;

        service.getById(1).subscribe(session => result = session);

        const req = httpMock.expectOne(`${API_URL}/1`);
        expect(req.request.method).toBe('GET');
        req.flush(fakeSession);

        expect(result).toBeDefined();
        expect(result!.id).toBe(1);
    });

    // ==================== create ====================
    it('should create a live session via POST', () => {
        const newSession: Partial<LiveSession> = {
            title: 'Angular Workshop',
            platform: LivePlatform.ZOOM
        };
        let result: LiveSession | undefined;

        service.create(newSession, 1).subscribe(session => result = session);

        const req = httpMock.expectOne(`${API_URL}?eventId=1`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(newSession);
        req.flush({ ...fakeSession, title: 'Angular Workshop' });

        expect(result).toBeDefined();
        expect(result!.title).toBe('Angular Workshop');
    });

    // ==================== update ====================
    it('should update a live session via PUT', () => {
        const updatedData: Partial<LiveSession> = { title: 'Updated Title' };
        let result: LiveSession | undefined;

        service.update(1, updatedData).subscribe(session => result = session);

        const req = httpMock.expectOne(`${API_URL}/1`);
        expect(req.request.method).toBe('PUT');
        req.flush({ ...fakeSession, ...updatedData });

        expect(result).toBeDefined();
        expect(result!.title).toBe('Updated Title');
    });

    // ==================== updateStatus ====================
    it('should update live session status via PUT', () => {
        let result: LiveSession | undefined;

        service.updateStatus(1, LiveSessionStatus.LIVE).subscribe(session => result = session);

        const req = httpMock.expectOne(`${API_URL}/1/status?status=LIVE`);
        expect(req.request.method).toBe('PUT');
        req.flush({ ...fakeSession, status: LiveSessionStatus.LIVE });

        expect(result).toBeDefined();
        expect(result!.status).toBe(LiveSessionStatus.LIVE);
    });

    // ==================== delete ====================
    it('should delete a live session via DELETE', () => {
        let deleteCompleted = false;

        service.delete(1).subscribe(() => deleteCompleted = true);

        const req = httpMock.expectOne(`${API_URL}/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);

        expect(deleteCompleted).toBeTrue();
    });

    // ==================== getByEvent ====================
    it('should get live sessions by event ID via GET', () => {
        let result: LiveSession[] | undefined;

        service.getByEvent(1).subscribe(sessions => result = sessions);

        const req = httpMock.expectOne(`${API_URL}/event/1`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeSession]);

        expect(result).toBeDefined();
        expect(result!.length).toBe(1);
    });
});
