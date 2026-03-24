import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CourseListComponent } from './course-list.component';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('CourseListComponent', () => {
    let component: CourseListComponent;
    let fixture: ComponentFixture<CourseListComponent>;
    let mockService: any;

    beforeEach(async () => {
        mockService = jasmine.createSpyObj('ServiceModuleService', ['getCourses', 'createCourse', 'updateCourse', 'deleteCourse', 'getWorkshops']);
        mockService.getCourses.and.returnValue(of([{ id: 1, title: 'Course 101' }]));
        mockService.getWorkshops.and.returnValue(of([{ id: 1, title: 'Ws' }]));
        mockService.deleteCourse.and.returnValue(of(null));

        await TestBed.configureTestingModule({
            imports: [CourseListComponent, CommonModule, FormsModule],
            providers: [
                { provide: ServiceModuleService, useValue: mockService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(CourseListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the course list component', () => {
        expect(component).toBeTruthy();
    });

    it('should load courses on init', () => {
        expect(component.courses.length).toBe(1);
        expect(mockService.getCourses).toHaveBeenCalled();
    });

    it('should trigger delete flow', () => {
        spyOn(window, 'confirm').and.returnValue(true);
    });
});
