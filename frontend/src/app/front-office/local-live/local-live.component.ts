import { Component, OnInit, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

// Services
import { LiveSessionService } from '../../core/services/live-session.service';
import { ChatService } from '../../core/services/chat.service';
import { AuthService } from '../../auth/auth.service';

// Models
import { LiveSession } from '../../core/models/live-session.model';
import { ChatMessage, ChatMessageRequest } from '../../core/models/chat-message.model';

@Component({
    selector: 'app-local-live',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './local-live.component.html',
    styleUrls: ['./local-live.component.css']
})
export class LocalLiveComponent implements OnInit, OnDestroy {
    @ViewChild('chatScroll') chatScroll!: ElementRef;
    
    @ViewChild('localVideo') localVideo!: ElementRef<HTMLVideoElement>;
    
    liveSession: LiveSession | null = null;
    messages: ChatMessage[] = [];
    newMessageInput = '';
    
    currentUserId = 1; // Assuming default demo customer ID
    currentUserName = 'Participant';

    chatInterval: any;
    loading = true;

    // WebRTC Camera Logic
    mediaStream: MediaStream | null = null;
    isCameraOn = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private liveSessionService: LiveSessionService,
        private chatService: ChatService,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
        this.authService.currentName$.subscribe(name => {
            if (name) this.currentUserName = name;
        });

        const idParam = this.route.snapshot.paramMap.get('id');
        if (idParam) {
            const liveId = Number(idParam);
            this.loadLiveDetails(liveId);
        } else {
            this.router.navigate(['/customer/dashboard']);
        }
    }

    // Toggle Camera and Microphone
    async toggleCamera() {
        if (this.isCameraOn) {
            // Stop the camera
            if (this.mediaStream) {
                this.mediaStream.getTracks().forEach(track => track.stop());
            }
            this.mediaStream = null;
            this.isCameraOn = false;
            if (this.localVideo && this.localVideo.nativeElement) {
                this.localVideo.nativeElement.srcObject = null;
            }
        } else {
            // Start the camera
            try {
                this.mediaStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
                this.isCameraOn = true;
                
                // Wait for Angular to render the video element if it was hidden by *ngIf
                setTimeout(() => {
                    if (this.localVideo && this.localVideo.nativeElement) {
                        this.localVideo.nativeElement.srcObject = this.mediaStream;
                    }
                }, 100);
            } catch (err) {
                console.error("Error accessing camera:", err);
                alert("Impossible d'accéder à la caméra ou au micro. Veuillez vérifier vos permissions de navigateur.");
            }
        }
    }

    loadLiveDetails(id: number): void {
        this.liveSessionService.getById(id).subscribe({
            next: (data) => {
                this.liveSession = data;
                this.loading = false;
                // Once live is loaded, initialize chat
                this.initChat();
            },
            error: (err) => {
                console.error('Error fetching live session', err);
                this.router.navigate(['/customer/dashboard']);
            }
        });
    }

    // Optional: Let the seller start the live from within the chat room!
    startLive(): void {
        if (!this.liveSession || this.liveSession.status !== 'SCHEDULED') return;
        this.liveSessionService.updateStatus(this.liveSession.id!, 'LIVE' as any).subscribe({
            next: (updatedLive) => {
                this.liveSession!.status = updatedLive.status;
            }
        });
    }

    endLive(): void {
        if (!this.liveSession || this.liveSession.status !== 'LIVE') return;
        this.liveSessionService.updateStatus(this.liveSession.id!, 'ENDED' as any).subscribe({
            next: (updatedLive) => {
                this.liveSession!.status = updatedLive.status;
            }
        });
    }

    initChat(): void {
        if (!this.liveSession?.id) return;

        // Fetch initial messages
        this.fetchMessages();

        // Start polling every 3 seconds
        this.chatInterval = setInterval(() => {
            this.fetchMessages();
        }, 3000);
    }

    fetchMessages(): void {
        if (!this.liveSession?.id) return;
        this.chatService.getMessages(this.liveSession.id).subscribe({
            next: (data) => {
                this.messages = data;
                this.scrollToBottom();
            },
            error: (err) => console.error('Error fetching messages', err)
        });
    }

    sendMessage(): void {
        if (!this.newMessageInput.trim() || !this.liveSession?.id) return;

        const request: ChatMessageRequest = {
            content: this.newMessageInput.trim()
        };

        const currentMsg = this.newMessageInput;
        this.newMessageInput = ''; // clear input early

        this.chatService.sendMessage(this.liveSession.id, request).subscribe({
            next: () => {
                this.fetchMessages();
            },
            error: (err) => {
                console.error("Failed to send message", err);
                this.newMessageInput = currentMsg; // restore
            }
        });
    }

    goBack(): void {
        this.router.navigate(['/customer/dashboard']);
    }

    scrollToBottom(): void {
        setTimeout(() => {
            if (this.chatScroll && this.chatScroll.nativeElement) {
                this.chatScroll.nativeElement.scrollTop = this.chatScroll.nativeElement.scrollHeight;
            }
        }, 100);
    }

    ngOnDestroy(): void {
        if (this.chatInterval) {
            clearInterval(this.chatInterval);
        }
        
        // Stop the camera when leaving the component
        if (this.mediaStream) {
            this.mediaStream.getTracks().forEach(track => track.stop());
        }
    }
}
