import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService (app auth)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: jasmine.SpyObj<Router>;
  const apiUrl = 'http://localhost:8081/api/auth';

  beforeEach(() => {
    localStorage.clear();
    router = jasmine.createSpyObj('Router', ['navigate']);
    router.navigate.and.returnValue(Promise.resolve(true));
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: Router, useValue: router },
        AuthService
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('login should POST withCredentials and store user fields', () => {
    service.login({ email: 'a@b.c', password: 'x' }).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({ role: 'CUSTOMER', name: 'N', email: 'a@b.c' });
    expect(service.getUserRole()).toBe('CUSTOMER');
    expect(service.getUserName()).toBe('N');
    expect(service.getUserEmail()).toBe('a@b.c');
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('register should persist session like login', () => {
    service.register({ email: 'a@b.c' }).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/register`);
    expect(req.request.withCredentials).toBeTrue();
    req.flush({ role: 'SELLER', name: 'S', email: 'a@b.c' });
    expect(service.getUserRole()).toBe('SELLER');
  });

  it('logout should POST logout, clear storage, navigate', () => {
    localStorage.setItem('user_role', 'ADMIN');
    localStorage.setItem('user_name', 'A');
    localStorage.setItem('user_email', 'a@b.c');
    service.logout();
    const req = httpMock.expectOne(`${apiUrl}/logout`);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({});
    expect(localStorage.getItem('user_role')).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('forgotPassword should POST email', () => {
    service.forgotPassword('x@y.z').subscribe();
    const req = httpMock.expectOne(`${apiUrl}/forgot-password`);
    expect(req.request.body).toEqual({ email: 'x@y.z' });
    req.flush({});
  });

  it('resetPassword should POST body', () => {
    service.resetPassword({ token: 't', password: 'p' }).subscribe();
    const req = httpMock.expectOne(`${apiUrl}/reset-password`);
    expect(req.request.body).toEqual({ token: 't', password: 'p' });
    req.flush({});
  });

  it('socialLogin should not store when newUser', () => {
    service.socialLogin('google', 'tok').subscribe();
    const req = httpMock.expectOne(`${apiUrl}/social-login`);
    req.flush({ newUser: true, role: 'CUSTOMER' });
    expect(service.isLoggedIn()).toBeFalse();
  });

  it('socialLogin should store when existing user', () => {
    service.socialLogin('google', 'tok').subscribe();
    const req = httpMock.expectOne(`${apiUrl}/social-login`);
    req.flush({ newUser: false, role: 'EXPERT', name: 'E', email: 'e@x.com' });
    expect(service.getUserRole()).toBe('EXPERT');
  });

  it('completeSocialLogin should store when role returned', () => {
    service.completeSocialLogin('google', 'tok', 'CUSTOMER').subscribe();
    const req = httpMock.expectOne(`${apiUrl}/social-login/complete`);
    req.flush({ role: 'CUSTOMER', name: 'U', email: 'u@x.com' });
    expect(service.getUserRole()).toBe('CUSTOMER');
  });

  it('getCurrentUser should GET /api/users/me withCredentials', () => {
    service.getCurrentUser().subscribe();
    const req = httpMock.expectOne('http://localhost:8081/api/users/me');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({ id: 1, name: 'U', email: 'u@x.com', role: 'CUSTOMER', storeActive: false, isActive: true });
  });

  it('getToken should return null', () => {
    expect(service.getToken()).toBeNull();
  });
});
