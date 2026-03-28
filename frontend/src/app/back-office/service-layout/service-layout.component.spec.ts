import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServiceLayoutComponent } from './service-layout.component';
import { AuthService } from '../../auth/auth.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('ServiceLayoutComponent', () => {
    let component: ServiceLayoutComponent;
    let fixture: ComponentFixture<ServiceLayoutComponent>;

    const mockAuthService = {
        getUserRole: jasmine.createSpy('getUserRole').and.returnValue('EXPERT'),
        getUserName: jasmine.createSpy('getUserName').and.returnValue('Jane Doe'),
        logout: jasmine.createSpy('logout')
    };

    beforeEach(async () => {
        mockAuthService.getUserRole.and.returnValue('EXPERT');
        mockAuthService.getUserName.and.returnValue('Jane Doe');
        await TestBed.configureTestingModule({
            imports: [ServiceLayoutComponent, RouterTestingModule],
            providers: [
                { provide: AuthService, useValue: mockAuthService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(ServiceLayoutComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the layout', () => {
        expect(component).toBeTruthy();
    });

    it('should fetch the role and name from AuthService on init', () => {
        expect(component.userName).toBe('Jane Doe');
        expect(mockAuthService.getUserRole).toHaveBeenCalled();
        expect(mockAuthService.getUserName).toHaveBeenCalled();
    });

    it('should compute the correct panel title for EXPERT', () => {
        expect(component.panelTitle).toBe('Expert Panel');
    });

    it('should compute the correct panel title for COMPANY', () => {
        mockAuthService.getUserRole.and.returnValue('COMPANY');
        component.ngOnInit(); // Refresh role
        expect(component.panelTitle).toBe('Company Panel');
    });

    it('should call authService.logout() when logout is triggered', () => {
        component.logout();
        expect(mockAuthService.logout).toHaveBeenCalled();
    });
});
