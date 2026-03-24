import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CalendarListComponent } from './calendar-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('CalendarListComponent', () => {
    let component: CalendarListComponent;
    let fixture: ComponentFixture<CalendarListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', ['getCalendars', 'createCalendar', 'updateCalendar', 'deleteCalendar', 'getServices']);
        mockService.getCalendars.and.returnValue(of([{ id: 1, available: true }]));
        mockService.getServices.and.returnValue(of([]));
        mockService.deleteCalendar.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [CalendarListComponent, CommonModule, FormsModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(CalendarListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the calendar list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load calendars on init', () => {
        expect(component.events.length).toBe(1);
        expect(mockService.getCalendars).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
        component.deleteData(1);
        expect(mockService.deleteCalendar).toHaveBeenCalledWith(1);
    });
});
