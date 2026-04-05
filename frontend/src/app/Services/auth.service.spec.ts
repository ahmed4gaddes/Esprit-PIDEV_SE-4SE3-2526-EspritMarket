import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService (legacy token helper)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.removeItem('token');
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.removeItem('token');
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('login should POST credentials to api auth', () => {
    service.login('a@b.c', 'secret').subscribe();
    const req = httpMock.expectOne('http://localhost:8081/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: 'a@b.c', password: 'secret' });
    req.flush({ token: 't' });
  });

  it('saveToken and getToken should round-trip', () => {
    service.saveToken('abc');
    expect(service.getToken()).toBe('abc');
  });

  it('logout should remove token', () => {
    service.saveToken('x');
    service.logout();
    expect(service.getToken()).toBeNull();
  });
});
