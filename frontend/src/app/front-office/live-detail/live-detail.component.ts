import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LiveSessionService } from '../../core/services/live-session.service';
import { ChatService } from '../../core/services/chat.service';
import { LiveSession, LiveSessionStatus } from '../../core/models/live-session.model';
import { ChatMessage } from '../../core/models/chat-message.model';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-live-detail',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './live-detail.component.html',
    styleUrls: ['./live-detail.component.css']
})
export class LiveDetailComponent implements OnInit, OnDestroy, AfterViewChecked {
    liveSession: LiveSession | null = null;
    messages: ChatMessage[] = [];
    newMessage: string = '';
    loading = true;
    chatSubscription?: Subscription;

    @ViewChild('chatContainer') private chatContainer!: ElementRef;

    constructor(
        private route: ActivatedRoute,
        private liveSessionService: LiveSessionService,
        private chatService: ChatService
    ) { }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.loadLiveSession(Number(id));
            this.startChatPolling(Number(id));
        }
    }

    ngOnDestroy(): void {
        if (this.chatSubscription) {
            this.chatSubscription.unsubscribe();
        }
    }

    ngAfterViewChecked(): void {
        this.scrollToBottom();
    }

    loadLiveSession(id: number) {
        this.liveSessionService.getById(id).subscribe({
            next: (data) => {
                this.liveSession = data;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading live session', err);
                this.loading = false;
            }
        });
    }

    startChatPolling(id: number) {
        // Poll every 3 seconds
        this.chatSubscription = this.chatService.pollMessages(id, 3000).subscribe({
            next: (msgs) => {
                // Prevent jarring UI jumps if old messages haven't changed much
                this.messages = msgs;
            },
            error: (err) => console.error('Chat polling error', err)
        });
    }

    sendMessage() {
        if (!this.newMessage.trim() || !this.liveSession?.id) return;

        this.chatService.sendMessage(this.liveSession.id, { content: this.newMessage }).subscribe({
            next: (msg) => {
                this.messages.push(msg); // Add optimistically
                this.newMessage = '';
                this.scrollToBottom();
            },
            error: (err) => console.error('Failed to send message', err)
        });
    }

    private scrollToBottom(): void {
        try {
            this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
        } catch (err) { }
    }

    isLive(status?: LiveSessionStatus): boolean {
        return status === LiveSessionStatus.LIVE;
    }
}
