import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/auth.service';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ServiceModuleService {
    private apiUrl = `${environment.apiUrl}/api`;

    constructor(private http: HttpClient, private authService: AuthService) { }

    private getHeaders(): HttpHeaders {
        const token = this.authService.getToken();
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });
    }

    // --- Workshops ---
    getWorkshops(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/workshops`); }
    getWorkshopById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/workshops/${id}`); }
    createWorkshop(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/workshops`, data, { headers: this.getHeaders() }); }
    updateWorkshop(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/workshops/${id}`, data, { headers: this.getHeaders() }); }
    deleteWorkshop(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/workshops/${id}`, { headers: this.getHeaders() }); }

    // --- Certificates ---
    getCertificates(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/certificates`); }
    getCertificateById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/certificates/${id}`); }
    createCertificate(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/certificates`, data, { headers: this.getHeaders() }); }
    updateCertificate(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/certificates/${id}`, data, { headers: this.getHeaders() }); }
    deleteCertificate(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/certificates/${id}`, { headers: this.getHeaders() }); }
    getCertificateEligibility(certificateId: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/certificates/${certificateId}/eligibility`, { headers: this.getHeaders() });
    }

    /** Complétion d'un cours par l'utilisateur connecté (JWT). */
    markCourseComplete(courseId: number): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/user-course-completions`, { courseId }, { headers: this.getHeaders() });
    }
    getMyCourseCompletions(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/user-course-completions/me`, { headers: this.getHeaders() });
    }

    // --- Internships ---
    getInternships(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/internships`); }
    getInternshipById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/internships/${id}`); }
    createInternship(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/internships`, data, { headers: this.getHeaders() }); }
    updateInternship(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/internships/${id}`, data, { headers: this.getHeaders() }); }
    deleteInternship(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/internships/${id}`, { headers: this.getHeaders() }); }
    applyToInternship(internshipId: number, data: { cvUrl: string; coverLetter?: string }): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/internship-applications/internships/${internshipId}/apply`, data, { headers: this.getHeaders() });
    }
    getMyInternshipApplications(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/internship-applications/me`, { headers: this.getHeaders() });
    }
    getInternshipApplicationsByInternship(internshipId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/internship-applications/internships/${internshipId}`, { headers: this.getHeaders() });
    }
    decideInternshipApplication(applicationId: number, status: 'ACCEPTED' | 'REJECTED', reviewerComment?: string): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/internship-applications/${applicationId}/status`, { status, reviewerComment }, { headers: this.getHeaders() });
    }
    getMyNotifications(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/notifications/me`, { headers: this.getHeaders() });
    }
    markNotificationAsRead(id: number): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/notifications/${id}/read`, {}, { headers: this.getHeaders() });
    }

    // --- Courses ---
    getCourses(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/courses`); }
    getCourseById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/courses/${id}`); }
    createCourse(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/courses`, data, { headers: this.getHeaders() }); }
    updateCourse(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/courses/${id}`, data, { headers: this.getHeaders() }); }
    deleteCourse(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/courses/${id}`, { headers: this.getHeaders() }); }

    // --- Registrations ---
    getRegistrations(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/registrations`); }
    getMyWorkshopRegistrations(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/registrations/me`, { headers: this.getHeaders() });
    }
    getRegistrationById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/registrations/${id}`); }
    createRegistration(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/registrations`, data, { headers: this.getHeaders() }); }
    updateRegistration(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/registrations/${id}`, data, { headers: this.getHeaders() }); }
    deleteRegistration(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/registrations/${id}`, { headers: this.getHeaders() }); }

    // --- Gamifications ---
    getGamifications(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/gamifications`); }
    getGamificationById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/gamifications/user/${id}`); }
    createGamification(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/gamifications`, data, { headers: this.getHeaders() }); }
    updateGamification(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/gamifications/${id}`, data, { headers: this.getHeaders() }); }
    deleteGamification(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/gamifications/${id}`, { headers: this.getHeaders() }); }

    // --- Service Calendars ---
    getCalendars(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/service-calendars`); }
    getCalendarById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/service-calendars/${id}`); }
    createCalendar(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/service-calendars`, data, { headers: this.getHeaders() }); }
    updateCalendar(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/service-calendars/${id}`, data, { headers: this.getHeaders() }); }
    deleteCalendar(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/service-calendars/${id}`, { headers: this.getHeaders() }); }

    // --- Certificate Validations ---
    getCertValidations(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/certificate-validations`); }
    getCertValidationById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/certificate-validations/${id}`); }
    createCertValidation(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/certificate-validations`, data, { headers: this.getHeaders() }); }
    updateCertValidation(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/certificate-validations/${id}`, data, { headers: this.getHeaders() }); }
    deleteCertValidation(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/certificate-validations/${id}`, { headers: this.getHeaders() }); }

    // --- Supporting Documents ---
    getSupportingDocuments(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/supporting-documents`); }
    getSupportingDocumentById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/supporting-documents/${id}`); }
    createSupportingDocument(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/supporting-documents`, data, { headers: this.getHeaders() }); }
    updateSupportingDocument(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/supporting-documents/${id}`, data, { headers: this.getHeaders() }); }
    deleteSupportingDocument(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/supporting-documents/${id}`, { headers: this.getHeaders() }); }

    // --- Workshop Evaluations ---
    getWorkshopEvaluations(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/workshop-evaluations`); }
    getWorkshopEvaluationById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/workshop-evaluations/${id}`); }
    createWorkshopEvaluation(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/workshop-evaluations`, data, { headers: this.getHeaders() }); }
    updateWorkshopEvaluation(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/workshop-evaluations/${id}`, data, { headers: this.getHeaders() }); }
    deleteWorkshopEvaluation(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/workshop-evaluations/${id}`, { headers: this.getHeaders() }); }

    // --- Services (Base) ---
    getMyServices(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/services/my-services`, { headers: this.getHeaders() }); }
    getServices(): Observable<any[]> { return this.http.get<any[]>(`${this.apiUrl}/services`); }
    getServiceById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/services/${id}`); }
    updateService(id: number, data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/services/${id}`, data, { headers: this.getHeaders() }); }
    deleteService(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/services/${id}`, { headers: this.getHeaders() }); }
}
