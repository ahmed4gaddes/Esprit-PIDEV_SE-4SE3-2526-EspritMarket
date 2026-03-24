import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class UploadService {

    private apiUrl = 'http://localhost:8081/api/upload';

    constructor(private http: HttpClient) { }

    /**
     * Upload a file and return the URL
     */
    uploadImage(file: File): Observable<string> {
        const formData = new FormData();
        formData.append('file', file);

        return this.http.post<{ url: string; filename: string }>(this.apiUrl, formData)
            .pipe(map(response => response.url));
    }
}
