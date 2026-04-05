import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  const base = 'http://localhost:8081/api/admin';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAllUsers should send withCredentials and map roles', () => {
    let page: any;
    service.getAllUsers({ page: 0, size: 5 }).subscribe((p) => (page = p));

    const req = httpMock.expectOne(
      (r) => r.url === `${base}/users` && r.params.get('page') === '0' && r.params.get('size') === '5'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({
      items: [
        {
          id: 1,
          name: 'U',
          email: 'u@x.com',
          role: 'ROLE_CUSTOMER',
          storeActive: false,
          isActive: true
        }
      ],
      page: 0,
      size: 5,
      totalElements: 1,
      totalPages: 1,
      hasNext: false,
      hasPrevious: false
    });

    expect(page.items.length).toBe(1);
    expect(page.items[0].role).toBe('CUSTOMER');
  });

  it('getAllUsers should omit role param when ALL', () => {
    service.getAllUsers({ role: 'ALL' }).subscribe();
    const req = httpMock.expectOne(
      (r) => r.url === `${base}/users` && !r.params.has('role')
    );
    expect(req.request.params.get('role')).toBeNull();
    req.flush({ items: [], page: 0, size: 10, totalElements: 0, totalPages: 0, hasNext: false, hasPrevious: false });
  });

  it('toggleUserStatus should PUT with withCredentials', () => {
    let u: any;
    service.toggleUserStatus(7).subscribe((x) => (u = x));
    const req = httpMock.expectOne(`${base}/users/7/toggle-status`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({
      id: 7,
      name: 'U',
      email: 'u@x.com',
      role: 'ADMIN',
      storeActive: true,
      isActive: false
    });
    expect(u.role).toBe('ADMIN');
  });

  it('deleteUser should DELETE with withCredentials', () => {
    let done = false;
    service.deleteUser(3).subscribe(() => (done = true));
    const req = httpMock.expectOne(`${base}/users/3`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.withCredentials).toBeTrue();
    req.flush(null);
    expect(done).toBeTrue();
  });

  it('updateUserRole should PUT body and map response', () => {
    let u: any;
    service.updateUserRole(2, 'ADMIN').subscribe((x) => (u = x));
    const req = httpMock.expectOne(`${base}/users/2/role`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ role: 'ADMIN' });
    expect(req.request.withCredentials).toBeTrue();
    req.flush({
      id: 2,
      name: 'U',
      email: 'u@x.com',
      role: 'ADMIN',
      storeActive: false,
      isActive: true
    });
    expect(u.role).toBe('ADMIN');
  });
});
