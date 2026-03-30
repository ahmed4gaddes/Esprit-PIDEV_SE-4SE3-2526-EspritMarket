import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    mockAuthSpy = jasmine.createSpyObj('AuthService', ['login', 'socialLogin']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: mockAuthSpy }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error message if login fails (403)', () => {
    component.email = 'test@gmail.com';
    component.password = 'wrongpass';
    mockAuthSpy.login.and.returnValue(throwError(() => ({ status: 403 })));

    component.onSubmit();

    expect(mockAuthSpy.login).toHaveBeenCalledWith({ email: 'test@gmail.com', password: 'wrongpass' });
    expect(component.errorMessage).toBe('Access denied. Please verify your account status.');
  });

  it('should show generic error for normal login failure', () => {
    component.email = 'test@gmail.com';
    component.password = 'wrongpass';
    mockAuthSpy.login.and.returnValue(throwError(() => ({ error: { error: 'Invalid credentials' } })));

    component.onSubmit();

    expect(component.errorMessage).toBe('Invalid credentials');
  });

  it('should redirect CUSTOMER to dashboard on success', fakeAsync(() => {
    component.email = 'cust@gmail.com';
    component.password = 'password123';
    mockAuthSpy.login.and.returnValue(of({ role: 'CUSTOMER' }));

    component.onSubmit();
    tick(50); // Advance timer for setTimeout

    expect(mockAuthSpy.login).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/customer/dashboard']);
  }));

  it('should redirect SELLER to seller dashboard on success', fakeAsync(() => {
    component.email = 'seller@esprit.tn';
    component.password = 'password123';
    mockAuthSpy.login.and.returnValue(of({ role: 'SELLER' }));

    component.onSubmit();
    tick(50); // Advance timer for setTimeout

    expect(router.navigate).toHaveBeenCalledWith(['/seller/dashboard']);
  }));

  it('should handle google callback for new user correctly', () => {
    mockAuthSpy.socialLogin.and.returnValue(of({ 
        newUser: true, 
        name: 'Google User', 
        email: 'g@google.com', 
        picture: 'pic.png' 
    }));

    component.handleGoogleCallback({ credential: 'fake-token' });

    expect(mockAuthSpy.socialLogin).toHaveBeenCalledWith('GOOGLE', 'fake-token');
    expect(router.navigate).toHaveBeenCalledWith(['/complete-profile'], {
         state: { googleToken: 'fake-token', name: 'Google User', email: 'g@google.com', picture: 'pic.png' }
    });
  });

  it('should handle google callback for existing user correctly', fakeAsync(() => {
    mockAuthSpy.socialLogin.and.returnValue(of({ 
        newUser: false, 
        role: 'COMPANY'
    }));

    component.handleGoogleCallback({ credential: 'fake-token' });
    tick(50);

    expect(mockAuthSpy.socialLogin).toHaveBeenCalledWith('GOOGLE', 'fake-token');
    expect(router.navigate).toHaveBeenCalledWith(['/company/dashboard']);
  }));
});
