import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ChatMessage, ChatMessageRequest } from '../models/chat-message.model';

@Injectable({
    providedIn: 'root'
})
export class ChatService {

    private apiUrl = 'http://localhost:8081/api/live-sessions';

    constructor(private http: HttpClient) { }

    getMessages(liveSessionId: number): Observable<ChatMessage[]> {
        return this.http.get<ChatMessage[]>(`${this.apiUrl}/${liveSessionId}/chat`);
    }

    sendMessage(liveSessionId: number, request: ChatMessageRequest): Observable<ChatMessage> {
        return this.http.post<ChatMessage>(`${this.apiUrl}/${liveSessionId}/chat`, request);
    }
  unsendMessage(liveSessionId: number, messageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${liveSessionId}/chat/${messageId}`);
  }

    /**
     * Start polling for messages every N milliseconds.
     * Useful for a simple pseudo-real-time chat implementation without WebSockets.
     */
    pollMessages(liveSessionId: number, intervalMs: number = 3000): Observable<ChatMessage[]> {
        return timer(0, intervalMs).pipe(
            switchMap(() => this.getMessages(liveSessionId))
        );
    }
}
