import { Component, OnInit, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { switchMap, catchError, finalize, from, of } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

// Services
import { LiveSessionService } from '../../core/services/live-session.service';
import { ChatService } from '../../core/services/chat.service';
import { AuthService } from '../../auth/auth.service';
import { ObjectDetectionService, Detection } from '../../core/services/object-detection.service';
import { ProductService } from '../../Services/product.service';
import { ProductImageService } from '../../Services/product-image.service';
import { StoreServiceService } from '../../Services/store-service.service';
import { CategoryService } from '../../Services/category.service';
import { UploadService } from '../../core/services/upload.service';

// Models
import { LiveSession } from '../../core/models/live-session.model';
import { ChatMessage, ChatMessageRequest } from '../../core/models/chat-message.model';
import { Product } from '../../models/product';

@Component({
    selector: 'app-local-live',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    templateUrl: './local-live.component.html',
    styleUrls: ['./local-live.component.css']
})
export class LocalLiveComponent implements OnInit, OnDestroy {
    @ViewChild('chatScroll') chatScroll!: ElementRef;
    
    @ViewChild('localVideo') localVideo!: ElementRef<HTMLVideoElement>;
    @ViewChild('overlayCanvas') overlayCanvas!: ElementRef<HTMLCanvasElement>;
    
    liveSession: LiveSession | null = null;
    messages: ChatMessage[] = [];
    newMessageInput = '';
    
    currentUserId = 1; // fallback
    currentUserName = 'Participant';
    currentUserRole: string | null = null;
    isSeller = false;

    chatInterval: any;
    loading = true;

    // WebRTC Camera Logic
    mediaStream: MediaStream | null = null;
    isCameraOn = false;

    // Object detection (seller only)
    private detectInterval: any;
    private detectionBusy = false;
    private lastCandidateAtMs = 0;
    private lastCandidateLabel: string | null = null;
    private overlayRaf: number | null = null;
    latestDetections: Detection[] = [];
    latestFrameSize: { w: number; h: number } | null = null;
    latestDetectionsAtMs = 0;

    pendingDetections: Array<{
        label: string;
        confidence: number;
        snapshotUrl: string;
        existsInDb: boolean;
        suggestedChatMessage: string;
        createdAtMs: number;
    }> = [];
    private recentlyQueued: Record<string, number> = {};

    showAddProductModal = false;
    detectedLabel: string | null = null;
    detectedConfidence: number | null = null;
    detectedSnapshotUrl: string | null = null;
    /** Optional: upload the webcam snapshot as product image (same flow as seller product form). */
    attachSnapshotAsImage = true;
    removeBackgroundBeforeUpload = false;
    uploadingSnapshot = false;

    productForm = new FormGroup({
        name: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]),
        description: new FormControl(''),
        price: new FormControl(0, [Validators.required, Validators.min(0)]),
        stock: new FormControl(0, [Validators.required, Validators.min(0)]),
        active: new FormControl(true),
        storeId: new FormControl<number | null>(null, [Validators.required]),
        // Category is optional at DB level; service now tolerates null
        categoryId: new FormControl<number | null>(null),
    });

    stores: any[] = [];
    categories: any[] = [];

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private liveSessionService: LiveSessionService,
        private chatService: ChatService,
        private authService: AuthService,
        private odService: ObjectDetectionService,
        private productService: ProductService,
        private uploadService: UploadService,
        private productImageService: ProductImageService,
        private storeService: StoreServiceService,
        private categoryService: CategoryService,
    ) {}

    ngOnInit(): void {
        this.authService.currentName$.subscribe(name => {
            if (name) this.currentUserName = name;
        });
        this.authService.currentRole$.subscribe(role => {
            this.currentUserRole = role;
            this.isSeller = (role || '').toUpperCase() === 'SELLER';
            this.tryStartSellerDetection();
        });
        // best-effort: fetch real user id (if endpoint works)
        this.authService.getCurrentUser().subscribe({
            next: (u) => (this.currentUserId = u.id),
            error: () => {},
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
            this.stopDetectionLoop();
            this.stopOverlayLoop();
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

                // Start detection only for seller/creator
                this.startDetectionLoopIfSeller();
                this.startOverlayLoop();
            } catch (err) {
                console.error("Error accessing camera:", err);
                alert("Impossible d'accéder à la caméra ou au micro. Veuillez vérifier vos permissions de navigateur.");
            }
        }
    }

    /** If role loads after the camera is on, start the detection interval (otherwise it never starts). */
    private tryStartSellerDetection(): void {
        if (!this.isSeller || !this.isCameraOn || !this.liveSession) return;
        this.startDetectionLoopIfSeller();
    }

    private startDetectionLoopIfSeller() {
        if (!this.isCameraOn || !this.liveSession) return;
        if (!this.isSeller) return;
        if (this.detectInterval) return;

        // load stores/categories once for modal selects
        this.storeService.getAllStores().subscribe({
            next: (data) => (this.stores = data),
            error: (err) => console.error('Stores error', err),
        });
        this.categoryService.getAllCategories().subscribe({
            next: (data) => (this.categories = data),
            error: (err) => console.error('Categories error', err),
        });

        // prefill storeId if available from liveSession
        if (this.liveSession.storeId != null) {
            this.productForm.patchValue({ storeId: this.liveSession.storeId });
        }

        // ~1.4 FPS to keep it light but responsive
        this.detectInterval = setInterval(() => this.runDetectionOnce(), 700);
    }

    private stopDetectionLoop() {
        if (this.detectInterval) {
            clearInterval(this.detectInterval);
            this.detectInterval = null;
        }
    }

    private startOverlayLoop() {
        if (this.overlayRaf != null) return;
        const tick = () => {
            this.drawOverlay();
            this.overlayRaf = requestAnimationFrame(tick);
        };
        this.overlayRaf = requestAnimationFrame(tick);
    }

    private stopOverlayLoop() {
        if (this.overlayRaf != null) {
            cancelAnimationFrame(this.overlayRaf);
            this.overlayRaf = null;
        }
        this.latestDetections = [];
        this.latestFrameSize = null;
        this.latestDetectionsAtMs = 0;
        this.pendingDetections = [];
        this.drawOverlay();
    }

    private drawOverlay() {
        const canvasEl = this.overlayCanvas?.nativeElement;
        const videoEl = this.localVideo?.nativeElement;
        if (!canvasEl || !videoEl) return;

        // Match canvas to rendered video size
        const rect = videoEl.getBoundingClientRect();
        const w = Math.max(1, Math.floor(rect.width));
        const h = Math.max(1, Math.floor(rect.height));
        if (canvasEl.width !== w) canvasEl.width = w;
        if (canvasEl.height !== h) canvasEl.height = h;

        const ctx = canvasEl.getContext('2d');
        if (!ctx) return;

        ctx.clearRect(0, 0, w, h);
        if (!this.isCameraOn) return;
        if (!this.isSeller) return;

        // Clear stale detections (no boxes) after 1.2s
        if (this.latestDetectionsAtMs && Date.now() - this.latestDetectionsAtMs > 1200) {
            this.latestDetections = [];
        }

        const detections = (this.latestDetections || []).filter((d) => d.confidence >= 0.5).slice(0, 5);
        if (detections.length === 0) return;

        ctx.lineWidth = 3;
        ctx.font = '14px system-ui, -apple-system, Segoe UI, Roboto, Arial';

        for (const d of detections) {
            const x1 = d.bbox_xyxy_norm.x1 * w;
            const y1 = d.bbox_xyxy_norm.y1 * h;
            const x2 = d.bbox_xyxy_norm.x2 * w;
            const y2 = d.bbox_xyxy_norm.y2 * h;

            const bw = Math.max(0, x2 - x1);
            const bh = Math.max(0, y2 - y1);

            // red stroke
            ctx.strokeStyle = 'rgba(193, 39, 45, 0.95)';
            ctx.strokeRect(x1, y1, bw, bh);

            // label background
            const label = `${d.label} ${Math.round(d.confidence * 100)}%`;
            const padX = 6;
            const padY = 4;
            const textW = ctx.measureText(label).width;
            const boxW = textW + padX * 2;
            const boxH = 18 + padY;
            const bx = x1;
            const by = Math.max(0, y1 - boxH);

            ctx.fillStyle = 'rgba(0,0,0,0.55)';
            ctx.fillRect(bx, by, boxW, boxH);
            ctx.fillStyle = 'rgba(255,255,255,0.95)';
            ctx.fillText(label, bx + padX, by + 16);
        }
    }

    private async grabFrameBlob(): Promise<{ blob: Blob; previewUrl: string } | null> {
        const video = this.localVideo?.nativeElement;
        if (!video || video.videoWidth === 0 || video.videoHeight === 0) return null;

        const canvas = document.createElement('canvas');
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext('2d');
        if (!ctx) return null;

        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        const previewUrl = canvas.toDataURL('image/jpeg', 0.7);
        const blob: Blob | null = await new Promise((resolve) => canvas.toBlob(resolve, 'image/jpeg', 0.7));
        if (!blob) return null;
        return { blob, previewUrl };
    }

    private runDetectionOnce(): void {
        if (this.detectionBusy || this.showAddProductModal) return;
        if (!this.isCameraOn || !this.liveSession) return;
        if (!this.isSeller) return;

        this.detectionBusy = true;
        this.grabFrameBlob()
            .then((frame) => {
                if (!frame) return;
                return new Promise<void>((resolve) => {
                    this.odService.detectImage(frame.blob, 0.35).subscribe({
                        next: (res) => {
                            this.latestDetections = res.detections || [];
                            this.latestFrameSize = { w: res.width, h: res.height };
                            this.latestDetectionsAtMs = Date.now();

                            const topCandidates = (res.detections || [])
                                .filter((d) => d.confidence >= 0.6)
                                .slice(0, 5);
                            if (topCandidates.length) {
                                this.handleDetections(topCandidates, frame.previewUrl);
                            }
                            resolve();
                        },
                        error: (err) => {
                            console.error('Detection error', err);
                            resolve();
                        },
                    });
                });
            })
            .finally(() => {
                this.detectionBusy = false;
            });
    }

    private handleDetections(dets: Detection[], snapshotUrl: string) {
        const now = Date.now();

        // pick first candidate that isn't already queued (recently)
        const pick = dets.find((d) => {
            const label = String(d.label || '').trim().toLowerCase();
            if (!label) return false;

            // don't queue duplicates too frequently
            const lastSeen = this.recentlyQueued[label] ?? 0;
            if (now - lastSeen < 4000) return false;

            // avoid duplicates already in queue
            if (this.pendingDetections.some((p) => p.label.toLowerCase() === label)) return false;

            return true;
        });

        if (!pick) return;

        const label = String(pick.label || '').trim();
        this.productService.existsByName(label).subscribe({
            next: (exists) => {
                if (!exists) {
                    this.enqueuePendingCandidate(pick, snapshotUrl, false, now);
                    return;
                }
                this.productService.getByName(label).subscribe({
                    next: (p) => this.enqueuePendingCandidate(pick, snapshotUrl, true, now, p),
                    error: (err) => {
                        console.error('getByName error', err);
                        this.enqueuePendingCandidate(pick, snapshotUrl, true, now);
                    },
                });
            },
            error: (err) => {
                console.error('existsByName error', err);
                // Still show the action card; treat as unknown / not in DB
                this.enqueuePendingCandidate(pick, snapshotUrl, false, now);
            },
        });
    }

    private enqueuePendingCandidate(
        pick: Detection,
        snapshotUrl: string,
        existsInDb: boolean,
        now: number,
        product?: Product
    ): void {
        const label = String(pick.label || '').trim();
        const suggested = existsInDb
            ? this.buildDetectedProductMessage(label, pick.confidence, product)
            : `🔎 Detected: ${label} (${Math.round(pick.confidence * 100)}%) — not found in DB.`;

        this.pendingDetections = [
            ...this.pendingDetections,
            {
                label,
                confidence: pick.confidence,
                snapshotUrl,
                existsInDb,
                suggestedChatMessage: suggested,
                createdAtMs: now,
            },
        ].slice(0, 3);

        this.recentlyQueued[label.toLowerCase()] = now;
        this.lastCandidateAtMs = now;
        this.lastCandidateLabel = label;
    }

    private buildDetectedProductMessage(label: string, conf: number, product?: Product): string {
        const pct = Math.round(conf * 100);
        if (!product) {
            return `✅ Detected product: ${label} (${pct}%) — found in DB.`;
        }
        const price = product.price != null ? `${product.price}` : 'N/A';
        const stock = product.stock != null ? `${product.stock}` : 'N/A';
        const store = product.storeName ? ` • Store: ${product.storeName}` : '';
        const category = product.categoryName ? ` • Category: ${product.categoryName}` : '';
        return `✅ Product detected: ${product.name} (${pct}%) • Price: ${price} • Stock: ${stock}${store}${category}`;
    }

    private sendSystemChatMessage(content: string) {
        if (!this.liveSession?.id) return;
        const request: ChatMessageRequest = { content };
        this.chatService.sendMessage(this.liveSession.id, request).subscribe({
            next: () => this.fetchMessages(),
            error: (err) => console.error('send system chat failed', err),
        });
    }

    openAddProductModal(label: string, conf: number, snapshotUrl: string) {
        this.detectedLabel = label;
        this.detectedConfidence = conf;
        this.detectedSnapshotUrl = snapshotUrl;
        this.showAddProductModal = true;
        this.attachSnapshotAsImage = true;
        this.removeBackgroundBeforeUpload = false;

        this.productForm.patchValue({
            name: label,
            price: 0,
            stock: 0,
            active: true,
            storeId: this.liveSession?.storeId ?? this.productForm.value.storeId,
        });
    }

    closeAddProductModal() {
        this.showAddProductModal = false;
        this.detectedLabel = null;
        this.detectedConfidence = null;
        this.detectedSnapshotUrl = null;
        this.attachSnapshotAsImage = true;
        this.removeBackgroundBeforeUpload = false;
    }

    submitDetectedProduct() {
        if (this.productForm.invalid) {
            Object.values(this.productForm.controls).forEach((c) => c.markAsTouched());
            return;
        }
        const payload = this.productForm.value as any as Product;
        const nameStr = String(payload.name || '');

        this.productService
            .addProduct(payload)
            .pipe(
                switchMap((res) => {
                    const pid = res?.id;
                    if (pid == null || !this.shouldAttachSnapshot()) {
                        return of(res);
                    }
                    return from(this.buildSnapshotImageFile()).pipe(
                        switchMap((file) => {
                            if (!file) {
                                return of(res);
                            }
                            return this.uploadService.uploadImage(file).pipe(
                                switchMap((url) =>
                                    this.productImageService.addImageForProduct(pid, url, nameStr)
                                ),
                                switchMap(() => of(res)),
                                catchError((err) => {
                                    console.error('Image upload failed', err);
                                    alert('Product saved, but attaching the photo failed.');
                                    return of(res);
                                })
                            );
                        })
                    );
                }),
                finalize(() => {
                    this.uploadingSnapshot = false;
                })
            )
            .subscribe({
                next: (res) => {
                    this.sendSystemChatMessage(`🆕 Added product to DB: ${res?.name ?? payload.name}`);
                    this.closeAddProductModal();
                },
                error: (err) => {
                    console.error('addProduct failed', err);
                    alert('❌ Failed to add product. Check store/category selection.');
                },
            });
    }

    private shouldAttachSnapshot(): boolean {
        return this.attachSnapshotAsImage && !!this.detectedSnapshotUrl;
    }

    /**
     * Builds a File from the detection snapshot; optionally runs in-browser background removal (@imgly/background-removal).
     */
    private async buildSnapshotImageFile(): Promise<File | null> {
        if (!this.shouldAttachSnapshot() || !this.detectedSnapshotUrl) {
            return null;
        }
        this.uploadingSnapshot = true;
        try {
            const fetched = await fetch(this.detectedSnapshotUrl);
            let blob = await fetched.blob();

            if (this.removeBackgroundBeforeUpload) {
                const { removeBackground } = await import('@imgly/background-removal');
                blob = await removeBackground(blob);
                return new File([blob], 'product-cutout.png', { type: 'image/png' });
            }

            return new File([blob], 'detection.jpg', { type: blob.type || 'image/jpeg' });
        } catch (e) {
            console.error(e);
            alert('Could not prepare the product image. Try disabling background removal or skip the photo.');
            return null;
        }
    }

    isOwnChatMessage(msg: ChatMessage): boolean {
        if (msg.senderId != null && this.currentUserId != null) {
            return msg.senderId === this.currentUserId;
        }
        return msg.senderName === this.currentUserName;
    }

    unsendChatMessage(msg: ChatMessage): void {
        if (!msg.id || !this.liveSession?.id) {
            return;
        }
        if (!this.isOwnChatMessage(msg)) {
            return;
        }
        if (!confirm('Remove this message from the chat for everyone?')) {
            return;
        }
        this.chatService.unsendMessage(this.liveSession.id, msg.id).subscribe({
            next: () => this.fetchMessages(),
            error: (err) => {
                console.error('unsend failed', err);
                alert('Could not remove the message.');
            },
        });
    }

    sendPendingDetectionToChat() {
        const first = this.pendingDetections[0];
        if (!first) return;
        this.sendSystemChatMessage(first.suggestedChatMessage);
        this.pendingDetections = this.pendingDetections.slice(1);
    }

    dismissPendingDetection() {
        this.pendingDetections = this.pendingDetections.slice(1);
    }

    addPendingDetectionToDb() {
        const first = this.pendingDetections[0];
        if (!first) return;
        this.openAddProductModal(
            first.label,
            first.confidence,
            first.snapshotUrl
        );
        this.pendingDetections = this.pendingDetections.slice(1);
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
        this.stopDetectionLoop();
        this.stopOverlayLoop();
        
        // Stop the camera when leaving the component
        if (this.mediaStream) {
            this.mediaStream.getTracks().forEach(track => track.stop());
        }
    }
}
