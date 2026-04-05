import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';
import { LiveSessionService } from '../../core/services/live-session.service';
import { ChatService } from '../../core/services/chat.service';
import { LiveSession, LiveSessionStatus } from '../../core/models/live-session.model';
import { ChatMessage } from '../../core/models/chat-message.model';
import { TimesService } from '../../Services/times.service';
import { Times } from '../../models/times';
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
    times: Times[] = [];
    loadingTimes = false;
    newMessage: string = '';
    loading = true;
    chatSubscription?: Subscription;

    @ViewChild('chatContainer') private chatContainer!: ElementRef;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private liveSessionService: LiveSessionService,
        private chatService: ChatService,
        private timesService: TimesService
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
                if (data?.id) {
                    this.loadTimes(data.id);
                }
            },
            error: (err) => {
                console.error('Error loading live session', err);
                this.loading = false;
            }
        });
    }

    private loadTimes(liveSessionId: number): void {
        this.loadingTimes = true;
        this.timesService.getByLiveSession(liveSessionId).subscribe({
            next: (data) => {
                this.times = data;
                this.loadingTimes = false;
            },
            error: (err) => {
                console.error('Failed to load times', err);
                this.times = [];
                this.loadingTimes = false;
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

    goBackToLives(): void {
        if (window.history.length > 1) {
            this.location.back();
            return;
        }
        this.router.navigate(['/lives']);
    }
}
