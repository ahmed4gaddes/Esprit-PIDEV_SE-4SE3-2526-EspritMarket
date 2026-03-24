import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GamificationListComponent } from './gamification-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('GamificationListComponent', () => {
    let component: GamificationListComponent;
    let fixture: ComponentFixture<GamificationListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', ['getGamifications', 'createGamification', 'updateGamification', 'deleteGamification']);
        mockService.getGamifications.and.returnValue(of([{ id: 1, points: 50, level: 3 }]));
        mockService.deleteGamification.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [GamificationListComponent, CommonModule, FormsModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(GamificationListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the gamification list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load gamifications on init', () => {
        expect(component.gamifications.length).toBe(1);
        expect(mockService.getGamifications).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
    });
});
