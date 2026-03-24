import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ChatService } from './chat.service';
import { ChatMessage } from '../models/chat-message.model';

describe('ChatService', () => {
    let service: ChatService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8081/api/live-sessions';

    const fakeMessage: ChatMessage = {
        id: 1,
        content: 'Hello World',
        senderName: 'Alice',
        sentAt: new Date()
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ChatService]
        });
        service = TestBed.inject(ChatService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    // ==================== sendMessage ====================
    it('should send a message via POST', () => {
        const messageRequest = { content: 'Hello World' };
        let result: ChatMessage | undefined;

        service.sendMessage(1, messageRequest).subscribe(msg => result = msg);

        const req = httpMock.expectOne(`${API_URL}/1/chat`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(messageRequest);
        req.flush({ ...fakeMessage });

        expect(result).toBeDefined();
        expect(result!.content).toBe('Hello World');
        expect(result!.senderName).toBe('Alice');
    });

    // ==================== getMessages ====================
    it('should get messages by live session ID via GET', () => {
        let result: ChatMessage[] | undefined;

        service.getMessages(1).subscribe(messages => result = messages);

        const req = httpMock.expectOne(`${API_URL}/1/chat`);
        expect(req.request.method).toBe('GET');
        req.flush([fakeMessage]);

        expect(result).toBeDefined();
        expect(result!.length).toBe(1);
    });
});
