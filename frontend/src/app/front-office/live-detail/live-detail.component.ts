import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';
import { LiveSessionService } from '../../core/services/live-session.service';
import { ChatService, ChatBanStatus } from '../../core/services/chat.service';
import { LiveSession, LiveSessionStatus } from '../../core/models/live-session.model';
import { ChatMessage } from '../../core/models/chat-message.model';
import { TimesService } from '../../Services/times.service';
import { Times } from '../../models/times';
import { Subscription, interval } from 'rxjs';

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

    // 🛡️ Ban state
    isBanned = false;
    banReason = '';
    banSecondsRemaining = 0;
    private banCountdownInterval?: any;

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
        if (this.banCountdownInterval) {
            clearInterval(this.banCountdownInterval);
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
                    this.checkBanStatus(data.id); // 🛡️ Check ban on load
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
        this.chatSubscription = this.chatService.pollMessages(id, 3000).subscribe({
            next: (msgs) => {
                this.messages = msgs;
            },
            error: (err) => console.error('Chat polling error', err)
        });
    }

    sendMessage() {
        if (!this.newMessage.trim() || !this.liveSession?.id || this.isBanned) return;

        this.chatService.sendMessage(this.liveSession.id, { content: this.newMessage }).subscribe({
            next: (msg) => {
                this.messages.push(msg);
                this.newMessage = '';
                this.scrollToBottom();
            },
            error: (err) => {
                // 🛡️ Handle ban response from backend (403)
                if (err.status === 403) {
                    const errorMsg: string = err.error?.message || err.error || '';
                    if (errorMsg.startsWith('BANNED:')) {
                        const parts = errorMsg.split(':');
                        this.banReason = parts[1] || 'VIOLATION';
                        this.banSecondsRemaining = parseInt(parts[2], 10) || 300;
                        this.activateBanUI();
                    }
                } else {
                    console.error('Failed to send message', err);
                }
            }
        });
    }

    // 🛡️ Check ban status on load (in case user refreshes the page while banned)
    private checkBanStatus(liveSessionId: number): void {
        this.chatService.getBanStatus(liveSessionId).subscribe({
            next: (status: ChatBanStatus) => {
                if (status.banned && status.secondsRemaining && status.secondsRemaining > 0) {
                    this.banReason = status.reason || 'VIOLATION';
                    this.banSecondsRemaining = status.secondsRemaining;
                    this.activateBanUI();
                }
            },
            error: () => { /* ignore if not logged in */ }
        });
    }

    // 🛡️ Start the ban countdown UI
    private activateBanUI(): void {
        this.isBanned = true;
        if (this.banCountdownInterval) clearInterval(this.banCountdownInterval);

        this.banCountdownInterval = setInterval(() => {
            this.banSecondsRemaining--;
            if (this.banSecondsRemaining <= 0) {
                this.isBanned = false;
                this.banReason = '';
                clearInterval(this.banCountdownInterval);
            }
        }, 1000);
    }

    // 🛡️ Format seconds to MM:SS for display
    get banCountdownDisplay(): string {
        const m = Math.floor(this.banSecondsRemaining / 60);
        const s = this.banSecondsRemaining % 60;
        return `${m}:${s.toString().padStart(2, '0')}`;
    }

    get banMessage(): string {
        if (this.banReason === 'BAD_WORD') {
            return '🚫 Vous avez utilisé un mot interdit.';
        } else if (this.banReason === 'SPAM') {
            return '⚠️ Vous avez envoyé trop de messages rapidement.';
        }
        return '🚫 Vous êtes banni du chat.';
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
