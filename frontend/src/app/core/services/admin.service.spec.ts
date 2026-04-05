import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AdminService } from './admin.service';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;
  const base = 'http://localhost:8081/api/admin';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AdminService]
    });
    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getDashboard should GET with days param', () => {
    let res: any;
    service.getDashboard(14).subscribe((r) => (res = r));
    const req = httpMock.expectOne(
      (r) => r.url === `${base}/dashboard` && r.params.get('days') === '14'
    );
    expect(req.request.method).toBe('GET');
    req.flush({ kpis: {} as any, activity: [] });
    expect(res).toBeDefined();
  });

  it('getUsers should build query params', () => {
    let done = false;
    service.getUsers({ role: 'CUSTOMER', active: 'ACTIVE', q: 'ab', page: 1, size: 20 }).subscribe(() => (done = true));
    const req = httpMock.expectOne(
      (r) =>
        r.url === `${base}/users` &&
        r.params.get('role') === 'CUSTOMER' &&
        r.params.get('active') === 'true' &&
        r.params.get('q') === 'ab' &&
        r.params.get('page') === '1' &&
        r.params.get('size') === '20'
    );
    req.flush({ items: [], page: 1, size: 20, totalElements: 0, totalPages: 0, hasNext: false, hasPrevious: false });
    expect(done).toBeTrue();
  });

  it('toggleUserStatus should PUT', () => {
    service.toggleUserStatus(1).subscribe();
    const req = httpMock.expectOne(`${base}/users/1/toggle-status`);
    expect(req.request.method).toBe('PUT');
    req.flush({} as any);
  });

  it('updateUserRole should PUT role body', () => {
    service.updateUserRole(1, 'SELLER').subscribe();
    const req = httpMock.expectOne(`${base}/users/1/role`);
    expect(req.request.body).toEqual({ role: 'SELLER' });
    req.flush({} as any);
  });

  it('deleteUser should DELETE', () => {
    service.deleteUser(9).subscribe();
    const req = httpMock.expectOne(`${base}/users/9`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('getInternshipApplications should pass filters', () => {
    let done = false;
    service
      .getInternshipApplications({ status: 'PENDING', internshipId: 2, companyId: 3, q: 'x', page: 0, size: 10 })
      .subscribe(() => (done = true));
    const req = httpMock.expectOne(
      (r) =>
        r.url === `${base}/internship-applications` &&
        r.params.get('status') === 'PENDING' &&
        r.params.get('internshipId') === '2' &&
        r.params.get('companyId') === '3' &&
        r.params.get('q') === 'x'
    );
    req.flush({ items: [], page: 0, size: 10, totalElements: 0, totalPages: 0, hasNext: false, hasPrevious: false });
    expect(done).toBeTrue();
  });

  it('getAuditLogs should pass action and q', () => {
    let done = false;
    service.getAuditLogs({ action: 'ROLE', q: 'admin', page: 0, size: 20 }).subscribe(() => (done = true));
    const req = httpMock.expectOne(
      (r) =>
        r.url === `${base}/audit-logs` &&
        r.params.get('action') === 'ROLE' &&
        r.params.get('q') === 'admin' &&
        r.params.get('size') === '20'
    );
    req.flush({ items: [], page: 0, size: 20, totalElements: 0, totalPages: 0, hasNext: false, hasPrevious: false });
    expect(done).toBeTrue();
  });
});
