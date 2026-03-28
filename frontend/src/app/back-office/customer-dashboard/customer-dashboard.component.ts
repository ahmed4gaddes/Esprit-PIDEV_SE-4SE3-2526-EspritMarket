import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// Reusing landing page components
import { NavigationComponent } from '../../front-office/navigation/navigation.component';
import { FooterComponent } from '../../front-office/footer/footer.component';

// IMPORTANT: We must ensure CustomerTicketsComponent is correctly imported
import { CustomerTicketsComponent } from '../customer-tickets/customer-tickets.component';

import { LiveSessionService } from '../../core/services/live-session.service';
import { EventService } from '../../core/services/event.service';
import { ServiceModuleService } from '../../core/services/service-module.service';
import { TicketService } from '../../core/services/ticket.service';
import { AuthService } from '../../auth/auth.service';
import { StoreServiceService } from '../../Services/store-service.service';
import { ProductService } from '../../Services/product.service';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { PaymentService } from '../../core/services/payment.service';
import { CartItemResponse } from '../../core/models/cart.model';
import { CategoryService } from '../../Services/category.service';
import { ProductAssessmentService } from '../../Services/product-assessment.service';
import { ProductAssessmentModalComponent } from '../../front-office/product-assessment-modal/product-assessment-modal.component';
import { RateService } from '../../Services/rate.service';
import { UserRateModalComponent } from '../../front-office/user-rate-modal/user-rate-modal.component';
import { SponsorshipRequestService } from '../../Services/sponsorship-request.service';
import { SponsorshipRequest } from '../../models/sponsorship-request';
import { jsPDF } from 'jspdf';

import { LiveSession } from '../../core/models/live-session.model';
import { Event as MarketEvent } from '../../core/models/event.model';
import { Store } from '../../models/store';
import { Product } from '../../models/product';
import { Category } from '../../models/category';
import { UploadService } from '../../core/services/upload.service';

@Component({
    selector: 'app-customer-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        NavigationComponent,
        FooterComponent,
        CustomerTicketsComponent,
        ProductAssessmentModalComponent,
        UserRateModalComponent
    ],
    templateUrl: './customer-dashboard.component.html',
    styleUrls: ['./customer-dashboard.component.css']
})
export class CustomerDashboardComponent implements OnInit, OnDestroy {
    activeTab: 'stores' | 'lives' | 'workshops' | 'tickets' | 'stages' | 'certificates' | 'orders' | 'notifications' = 'stores';
    lives: LiveSession[] = [];
    events: MarketEvent[] = [];
    stores: Store[] = [];
    products: Product[] = [];
    
    certificatesList: any[] = [];
    workshopsList: any[] = [];
    /** Workshop IDs the current user is already registered to */
    registeredWorkshopIds: number[] = [];
    registeringWorkshopId: number | null = null;
    internshipsList: any[] = [];
    coursesAll: any[] = [];

    loadingLives = false;
    loadingEvents = false;
    loadingWorkshops = false;
    loadingInternships = false;
    loadingCertificates = false;
    loadingStores = true;
    currentUserId: number | null = null;

    // Product rating (ProductAssessment)
    showAssessmentModal = false;
    assessmentProductId: number | null = null;
    productAverageById: Record<number, number> = {};
    productRatingsCountById: Record<number, number> = {};
    sellerAverageByUserId: Record<number, number> = {};
    sellerRatingsCountByUserId: Record<number, number> = {};

    showSellerRateModal = false;
    ratedSellerId: number | null = null;
    ratedSellerName = '';
    sponsoredAds: SponsorshipRequest[] = [];
    currentSponsoredIndex = 0;
    private sponsoredSlideTimerId?: ReturnType<typeof setInterval>;
    private readonly sponsoredThemes = ['s-theme-red', 's-theme-purple', 's-theme-blue', 's-theme-green', 's-theme-dark'];
    eligibilityByCertificateId: Record<number, any> = {};
    loadingEligibilityByCertificateId: Record<number, boolean> = {};
    completingCourseById: Record<number, boolean> = {};
    exportingCertificateById: Record<number, boolean> = {};
    expandedCertificateId: number | null = null;

    expandedStoreId: number | null = null;

    // Cart / checkout / orders state
    cartItems: CartItemResponse[] = [];
    processingPayment = false;
    paymentMethod: 'CASH' | 'CARD' | 'WALLET' | 'STRIPE' = 'CARD';
    checkoutAddress = '';
    orderHistory: any[] = [];

    /** Increment to refresh My Tickets after purchasing */
    ticketRefreshTrigger = 0;

    /** Paid ticket checkout (simulated payment before API create) */
    showTicketPaymentModal = false;
    ticketPaymentEvent: MarketEvent | null = null;
    processingTicketPayment = false;
    ticketPaymentMethod: 'CARD' | 'CASH' = 'CARD';

    // Internship apply
    showApplyInternshipModal = false;
    selectedInternshipForApply: any | null = null;
    internshipCvUrl = '';
    internshipCoverLetter = '';
    uploadingInternshipCv = false;
    internshipApplications: any[] = [];
    notifications: any[] = [];
    
    // Category state
    storeCategories: { [storeId: number]: Category[] } = {};
    loadingCategories: { [storeId: number]: boolean } = {};
    expandedCategoryId: number | null = null;

    constructor(
        private liveSessionService: LiveSessionService,
        private eventService: EventService,
        private serviceModuleService: ServiceModuleService,
        private ticketService: TicketService,
        private authService: AuthService,
        private storeService: StoreServiceService,
        private productService: ProductService,
        private cartService: CartService,
        private orderService: OrderService,
        private paymentService: PaymentService,
        private categoryService: CategoryService,
        private productAssessmentService: ProductAssessmentService,
        private rateService: RateService,
        private sponsorshipRequestService: SponsorshipRequestService,
        private uploadService: UploadService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.authService.getCurrentUser().subscribe({
            next: (u) => {
                this.currentUserId = u.id;
                this.loadOrderHistory();
            },
            error: () => {
                this.currentUserId = null;
                this.loadOrderHistory();
            }
        });
        this.loadLivesAndEvents();
        this.loadStoresAndProducts();
        this.loadSponsoredAds();
        this.cartService.loadCart().subscribe();
        this.cartService.cart$.subscribe({
            next: (cart) => {
                this.cartItems = cart?.items || [];
            },
            error: () => this.cartItems = []
        });
        this.serviceModuleService.getCourses().subscribe({
            next: (data) => this.coursesAll = data || [],
            error: () => this.coursesAll = []
        });
        this.loadNotifications();
    }

    ngOnDestroy(): void {
        this.stopSponsoredSlider();
    }

    loadCertificatesWithEligibility(): void {
        if (this.loadingCertificates) return;
        this.loadingCertificates = true;

        this.serviceModuleService.getCertificates().subscribe({
            next: (certs) => {
                this.certificatesList = (certs || []).filter(c => c.active);
                this.loadingCertificates = false;
                this.loadEligibilityForVisibleCertificates();
            },
            error: () => {
                this.certificatesList = [];
                this.loadingCertificates = false;
            }
        });
    }

    private loadEligibilityForVisibleCertificates(): void {
        for (const cert of this.certificatesList) {
            if (!cert?.id) continue;
            this.loadEligibilityForCertificate(cert.id);
        }
    }

    loadEligibilityForCertificate(certificateId: number): void {
        this.loadingEligibilityByCertificateId[certificateId] = true;
        this.serviceModuleService.getCertificateEligibility(certificateId).subscribe({
            next: (eligibility) => {
                this.eligibilityByCertificateId[certificateId] = eligibility;
                this.loadingEligibilityByCertificateId[certificateId] = false;
            },
            error: () => {
                this.eligibilityByCertificateId[certificateId] = null;
                this.loadingEligibilityByCertificateId[certificateId] = false;
            }
        });
    }

    toggleCertificateDetails(certificateId: number): void {
        this.expandedCertificateId = this.expandedCertificateId === certificateId ? null : certificateId;
    }

    getRequiredCourseIds(certificate: any): number[] {
        const eligibility = this.eligibilityByCertificateId[certificate?.id];
        if (eligibility?.requiredCourseIds?.length) return eligibility.requiredCourseIds;
        if (certificate?.courseIds?.length) return certificate.courseIds;
        if (certificate?.courses?.length) return certificate.courses.map((c: any) => c.id);
        return [];
    }

    getCompletedCourseIds(certificateId: number): number[] {
        const eligibility = this.eligibilityByCertificateId[certificateId];
        return eligibility?.completedCourseIds || [];
    }

    isCourseCompleted(certificateId: number, courseId: number): boolean {
        return this.getCompletedCourseIds(certificateId).includes(courseId);
    }

    getCourseLabel(courseId: number, certificate?: any): string {
        const fromCert = certificate?.courses?.find((c: any) => c.id === courseId);
        if (fromCert?.title) return fromCert.title;
        const fromAll = this.coursesAll.find(c => c.id === courseId);
        return fromAll?.title || `Course #${courseId}`;
    }

    getCertificateProgress(certificateId: number): number {
        const eligibility = this.eligibilityByCertificateId[certificateId];
        const required = Number(eligibility?.requiredCourseIds?.length || 0);
        const completed = Number(eligibility?.completedCourseIds?.length || 0);
        if (required === 0) return 100;
        return Math.round((completed / required) * 100);
    }

    getCertificateStatus(certificateId: number): 'eligible' | 'in_progress' | 'not_started' {
        const eligibility = this.eligibilityByCertificateId[certificateId];
        if (!eligibility) return 'not_started';
        if (eligibility.eligible) return 'eligible';
        const completed = Number(eligibility?.completedCourseIds?.length || 0);
        return completed > 0 ? 'in_progress' : 'not_started';
    }

    markCourseAsComplete(certificateId: number, courseId: number): void {
        if (!this.currentUserId || this.completingCourseById[courseId]) return;
        this.completingCourseById[courseId] = true;

        this.serviceModuleService.markCourseComplete(courseId).subscribe({
            next: () => {
                this.completingCourseById[courseId] = false;
                this.loadEligibilityForCertificate(certificateId);
            },
            error: (err) => {
                console.error(err);
                this.completingCourseById[courseId] = false;
                alert('Unable to mark course as completed.');
            }
        });
    }

    claimCertificate(certificate: any): void {
        const certificateId = Number(certificate?.id);
        const status = this.getCertificateStatus(certificate.id);
        if (status !== 'eligible') {
            alert('You must complete all required courses first.');
            return;
        }

        if (!certificateId || this.exportingCertificateById[certificateId]) return;
        this.exportingCertificateById[certificateId] = true;

        try {
            this.exportCertificatePdf(certificate);
        } catch (err) {
            console.error(err);
            alert('Unable to export certificate PDF right now.');
        } finally {
            this.exportingCertificateById[certificateId] = false;
        }
    }

    private exportCertificatePdf(certificate: any): void {
        const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });

        const title = certificate?.title || 'Certificate';
        const org = certificate?.organization || 'ESPRIT Market';
        const recipient = this.authService.getUserName() || this.authService.getUserEmail() || 'Participant';
        const issueDate = new Date();
        const issueDateLabel = issueDate.toLocaleDateString('en-GB');
        const certCode = `CERT-${certificate?.id || 'N/A'}-${issueDate.getTime()}`;

        // Background frame
        doc.setDrawColor(10, 107, 91);
        doc.setLineWidth(1.2);
        doc.rect(10, 10, 277, 190);
        doc.setLineWidth(0.6);
        doc.rect(14, 14, 269, 182);

        // Header
        doc.setFont('helvetica', 'bold');
        doc.setTextColor(10, 107, 91);
        doc.setFontSize(28);
        doc.text('Certificate of Completion', 148.5, 42, { align: 'center' });

        // Subtitle
        doc.setFont('helvetica', 'normal');
        doc.setTextColor(70, 70, 70);
        doc.setFontSize(13);
        doc.text('This is proudly presented to', 148.5, 62, { align: 'center' });

        // Recipient
        doc.setFont('times', 'bold');
        doc.setTextColor(25, 25, 25);
        doc.setFontSize(30);
        doc.text(recipient, 148.5, 82, { align: 'center' });

        // Body
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(14);
        doc.setTextColor(55, 55, 55);
        doc.text('for successfully completing all required courses for', 148.5, 96, { align: 'center' });

        doc.setFont('helvetica', 'bold');
        doc.setTextColor(0, 0, 0);
        doc.setFontSize(19);
        doc.text(title, 148.5, 110, { align: 'center' });

        doc.setFont('helvetica', 'normal');
        doc.setFontSize(13);
        doc.setTextColor(80, 80, 80);
        doc.text(`Issued by ${org}`, 148.5, 124, { align: 'center' });

        // Footer details
        doc.setFontSize(11);
        doc.setTextColor(90, 90, 90);
        doc.text(`Issue date: ${issueDateLabel}`, 26, 172);
        doc.text(`Certificate code: ${certCode}`, 26, 180);
        doc.text('Authorized Signature', 235, 172, { align: 'center' });
        doc.setDrawColor(120, 120, 120);
        doc.line(205, 176, 265, 176);
        doc.text('ESPRIT Market', 235, 184, { align: 'center' });

        const safeTitle = String(title).replace(/[^a-zA-Z0-9-_]+/g, '_');
        doc.save(`certificate_${safeTitle}.pdf`);
    }

    loadInternships(): void {
        if (this.loadingInternships) return;

        this.loadingInternships = true;
        this.serviceModuleService.getInternships().subscribe({
            next: (data) => {
                this.internshipsList = (data || []).filter(i => i.active);
                this.loadingInternships = false;
                this.loadMyInternshipApplications();
            },
            error: (err) => {
                console.error(err);
                this.internshipsList = [];
                this.loadingInternships = false;
            }
        });
    }

    private loadSponsoredAds(): void {
        this.sponsorshipRequestService.getApprovedForCustomers().subscribe({
            next: (data) => {
                this.sponsoredAds = (data || []).slice(0, 8);
                this.currentSponsoredIndex = 0;
                if (this.sponsoredAds.length > 1) {
                    this.startSponsoredSlider();
                } else {
                    this.stopSponsoredSlider();
                }
            },
            error: () => {
                this.sponsoredAds = [];
                this.stopSponsoredSlider();
            }
        });
    }

    get currentSponsoredAd(): SponsorshipRequest | null {
        if (!this.sponsoredAds.length) {
            return null;
        }
        return this.sponsoredAds[this.currentSponsoredIndex] || null;
    }

    get currentSponsoredThemeClass(): string {
        if (!this.sponsoredAds.length) {
            return 's-theme-red';
        }
        return this.sponsoredThemes[this.currentSponsoredIndex % this.sponsoredThemes.length];
    }

    nextSponsoredAd(): void {
        if (this.sponsoredAds.length <= 1) {
            return;
        }
        this.currentSponsoredIndex = (this.currentSponsoredIndex + 1) % this.sponsoredAds.length;
        this.restartSponsoredSlider();
    }

    prevSponsoredAd(): void {
        if (this.sponsoredAds.length <= 1) {
            return;
        }
        this.currentSponsoredIndex = (this.currentSponsoredIndex - 1 + this.sponsoredAds.length) % this.sponsoredAds.length;
        this.restartSponsoredSlider();
    }

    goToSponsoredAd(index: number): void {
        if (index < 0 || index >= this.sponsoredAds.length) {
            return;
        }
        this.currentSponsoredIndex = index;
        this.restartSponsoredSlider();
    }

    private startSponsoredSlider(): void {
        this.stopSponsoredSlider();
        this.sponsoredSlideTimerId = setInterval(() => {
            this.currentSponsoredIndex = (this.currentSponsoredIndex + 1) % this.sponsoredAds.length;
        }, 5000);
    }

    private stopSponsoredSlider(): void {
        if (this.sponsoredSlideTimerId) {
            clearInterval(this.sponsoredSlideTimerId);
            this.sponsoredSlideTimerId = undefined;
        }
    }

    private restartSponsoredSlider(): void {
        if (this.sponsoredAds.length > 1) {
            this.startSponsoredSlider();
        }
    }

    loadStoresAndProducts() {
        this.storeService.getAllStores().subscribe({
            next: (storesData) => {
                this.stores = storesData;
                this.productService.getAllProducts().subscribe({
                    next: (productsData) => {
                        this.products = productsData;
                        this.loadingStores = false;
                        this.loadProductAverages();
                        this.loadSellerRatesSummary();
                    },
                    error: () => this.loadingStores = false
                });
            },
            error: () => this.loadingStores = false
        });
    }

    private loadProductAverages(): void {
        this.productAverageById = {};
        this.productRatingsCountById = {};

        for (const p of this.products) {
            if (!p.id) continue;
            this.productAssessmentService.getAverageByProduct(p.id).subscribe({
                next: (summary) => {
                    this.productAverageById[p.id!] = Number(summary?.average || 0);
                    this.productRatingsCountById[p.id!] = Number(summary?.count || 0);
                },
                error: () => {
                    this.productAverageById[p.id!] = 0;
                    this.productRatingsCountById[p.id!] = 0;
                }
            });
        }
    }

    hasRatings(productId: number | undefined): boolean {
        if (!productId) return false;
        return (this.productRatingsCountById[productId] || 0) > 0;
    }

    getRoundedRating(productId: number | undefined): number {
        if (!productId) return 0;
        const avg = this.productAverageById[productId] || 0;
        return Math.max(0, Math.min(5, Math.round(avg)));
    }

    openAssessment(productId: number): void {
        if (!this.currentUserId) {
            alert('Please login first.');
            return;
        }
        this.assessmentProductId = productId;
        this.showAssessmentModal = true;
    }

    private loadSellerRatesSummary(): void {
        this.sellerAverageByUserId = {};
        this.sellerRatingsCountByUserId = {};
        const ownerIds = [...new Set(this.stores.map(s => s.ownerId).filter((id): id is number => !!id))];
        ownerIds.forEach((ownerId) => {
            this.rateService.getRatesForUser(ownerId).subscribe({
                next: (rates) => {
                    const list = rates || [];
                    const stars = list.map(r => Number(r.star || 0));
                    const sum = stars.reduce((a, b) => a + b, 0);
                    this.sellerAverageByUserId[ownerId] = stars.length ? (sum / stars.length) : 0;
                    this.sellerRatingsCountByUserId[ownerId] = list.length;
                },
                error: () => {
                    this.sellerAverageByUserId[ownerId] = 0;
                    this.sellerRatingsCountByUserId[ownerId] = 0;
                }
            });
        });
    }

    openSellerRate(store: Store): void {
        if (!this.currentUserId) {
            alert('Please login first.');
            return;
        }
        if (!store.ownerId) {
            alert('No seller linked to this store.');
            return;
        }
        this.ratedSellerId = store.ownerId;
        this.ratedSellerName = store.ownerName || `Seller #${store.ownerId}`;
        this.showSellerRateModal = true;
    }

    onProductAssessmentSaved(): void {
        this.loadProductAverages();
    }

    onSellerRateSaved(): void {
        this.loadSellerRatesSummary();
    }

    toggleStore(storeId: number) {
        if (this.expandedStoreId === storeId) {
            this.expandedStoreId = null;
        } else {
            this.expandedStoreId = storeId;
            this.expandedCategoryId = null; // Reset category selection when changing store
            
            // Load categories if not already loaded
            if (!this.storeCategories[storeId]) {
                this.loadingCategories[storeId] = true;
                this.categoryService.getCategoriesByStore(storeId).subscribe({
                    next: (cats) => {
                        this.storeCategories[storeId] = cats;
                        this.loadingCategories[storeId] = false;
                    },
                    error: () => this.loadingCategories[storeId] = false
                });
            }
        }
    }

    toggleCategory(categoryId: number | undefined) {
        if (!categoryId) return;
        this.expandedCategoryId = this.expandedCategoryId === categoryId ? null : categoryId;
    }

    getProductsForCategory(categoryId: number | undefined): Product[] {
        if (!categoryId) return [];
        // A product is linked to a category via product.categoryId
        return this.products.filter(p => p.categoryId === categoryId);
    }

    getProductsForStore(storeId: number): Product[] {
        return this.products.filter(p => p.storeId === storeId);
    }

    addToCart(product: Product) {
        if (!this.currentUserId) {
            alert('Please login first to add items to cart.');
            return;
        }
        this.cartService.addItem({ productId: product.id, quantity: 1 }).subscribe({
            next: () => alert(`"${product.name}" a été ajouté au panier !`),
            error: (err) => alert('Error adding to cart: ' + err.message)
        });
    }

    increaseCartItem(productId: number | undefined): void {
        if (!productId) return;
        const item = this.cartItems.find(ci => ci.productId === productId);
        if (!item) return;
        this.cartService.updateQuantity(item.id, item.quantity + 1).subscribe();
    }

    decreaseCartItem(productId: number | undefined): void {
        if (!productId) return;
        const item = this.cartItems.find(ci => ci.productId === productId);
        if (!item) return;
        this.cartService.updateQuantity(item.id, item.quantity - 1).subscribe();
    }

    removeFromCart(productId: number | undefined): void {
        const item = this.cartItems.find(ci => ci.productId === productId);
        if (item) {
            this.cartService.removeItem(item.id).subscribe();
        }
    }

    get cartSubtotal(): number {
        return this.cartItems.reduce((sum, item) => sum + item.totalPrice, 0);
    }

    get deliveryFee(): number {
        return this.cartItems.length > 0 ? 7 : 0;
    }

    get cartTotal(): number {
        return this.cartSubtotal + this.deliveryFee;
    }

    checkoutCart(): void {
        if (!this.currentUserId) {
            alert('Please login first.');
            return;
        }
        if (this.cartItems.length === 0) {
            alert('Your cart is empty.');
            return;
        }
        if (!this.checkoutAddress.trim()) {
            alert('Please enter a delivery address.');
            return;
        }

        this.processingPayment = true;
        
        // 1. Create order
        this.orderService.createOrder({
            shippingAddress: this.checkoutAddress.trim(),
            paymentMethod: this.paymentMethod
        }).subscribe({
            next: (order) => {
                // 2. Process payment (Simulated backend processing for now, real logic in PaymentController)
                this.paymentService.processPayment(order.id!, {
                    method: this.paymentMethod,
                    amount: order.totalAmount
                }).subscribe({
                    next: () => {
                        this.processingPayment = false;
                        this.checkoutAddress = '';
                        this.activeTab = 'orders';
                        this.loadOrderHistory();
                        this.cartService.loadCart().subscribe(); // Reload empty cart
                        alert('Payment successful! Your order has been placed.');
                    },
                    error: (err) => {
                        this.processingPayment = false;
                        alert('Order created, but payment failed: ' + err.message);
                    }
                });
            },
            error: (err) => {
                this.processingPayment = false;
                alert('Checkout failed: ' + err.message);
            }
        });
    }

    private loadOrderHistory(): void {
        this.orderService.getMyOrders().subscribe({
            next: (orders) => this.orderHistory = orders,
            error: () => this.orderHistory = []
        });
    }

    // Removed localStorage key getter


    private loadMyInternshipApplications(): void {
        this.serviceModuleService.getMyInternshipApplications().subscribe({
            next: (apps) => this.internshipApplications = apps || [],
            error: () => this.internshipApplications = []
        });
    }

    loadNotifications(): void {
        this.serviceModuleService.getMyNotifications().subscribe({
            next: (data) => this.notifications = data || [],
            error: () => this.notifications = []
        });
    }

    get unreadNotificationsCount(): number {
        return this.notifications.filter(n => !n.read).length;
    }

    markNotificationAsRead(notificationId: number): void {
        this.serviceModuleService.markNotificationAsRead(notificationId).subscribe({
            next: () => {
                this.notifications = this.notifications.map(n =>
                    n.id === notificationId ? { ...n, read: true } : n
                );
            },
            error: () => {}
        });
    }

    hasAppliedToInternship(internshipId: number | undefined): boolean {
        if (!internshipId) return false;
        return this.internshipApplications.some(app => app.internshipId === internshipId);
    }

    getInternshipApplicationStatus(internshipId: number | undefined): string {
        if (!internshipId) return '';
        const app = this.internshipApplications.find(a => a.internshipId === internshipId);
        return app?.status || '';
    }

    openApplyInternship(internship: any): void {
        if (!internship?.id) return;
        if (this.hasAppliedToInternship(internship.id)) {
            alert('You already applied to this internship.');
            return;
        }
        this.selectedInternshipForApply = internship;
        this.internshipCvUrl = '';
        this.internshipCoverLetter = '';
        this.showApplyInternshipModal = true;
    }

    onInternshipCvSelected(event: Event): void {
        const file = (event.target as HTMLInputElement | null)?.files?.[0];
        if (!file) return;

        this.uploadingInternshipCv = true;
        this.uploadService.uploadFile(file).subscribe({
            next: (url) => {
                this.internshipCvUrl = url;
                this.uploadingInternshipCv = false;
            },
            error: (err) => {
                console.error(err);
                this.uploadingInternshipCv = false;
                alert('CV upload failed. Please try again.');
            }
        });
    }

    submitInternshipApplication(): void {
        const internshipId = this.selectedInternshipForApply?.id;
        if (!internshipId) return;
        if (!this.internshipCvUrl) {
            alert('Please upload your CV before submitting.');
            return;
        }

        this.serviceModuleService.applyToInternship(internshipId, {
            cvUrl: this.internshipCvUrl,
            coverLetter: this.internshipCoverLetter
        }).subscribe({
            next: () => {
                this.showApplyInternshipModal = false;
                this.selectedInternshipForApply = null;
                this.loadMyInternshipApplications();
                alert('Application submitted successfully.');
            },
            error: (err) => {
                console.error(err);
                alert('Failed to submit application: ' + (err?.error?.error || err?.error?.message || 'Unknown error'));
            }
        });
    }

    loadLivesAndEvents() {
        if (this.lives.length === 0 && !this.loadingLives) {
            this.loadingLives = true;
            this.liveSessionService.getAll().subscribe({
                next: (data) => {
                    this.lives = data;
                    this.loadingLives = false;
                },
                error: () => this.loadingLives = false
            });
        }
        
        if (this.events.length === 0 && !this.loadingEvents) {
            this.loadingEvents = true;
            this.eventService.getAll().subscribe({
                next: (data) => {
                    this.events = data; 
                    this.loadingEvents = false;
                },
                error: () => this.loadingEvents = false
            });
        }

    }

    joinLive(live: LiveSession) {
        if (live.platform === 'LOCAL') {
            this.router.navigate(['/live/local', live.id]);
        } else if (live.link) {
            window.open(live.link, '_blank');
        } else {
            alert("This live session has no external link.");
        }
    }

    buyTicket(event: MarketEvent) {
        if (!event.id) return;
        if (!this.currentUserId) {
            alert('Please sign in to get a ticket.');
            return;
        }
        const price = event.ticketPrice ?? 0;
        if (price > 0) {
            this.ticketPaymentEvent = event;
            this.ticketPaymentMethod = 'CARD';
            this.showTicketPaymentModal = true;
            return;
        }
        if (!confirm(`Get a free ticket for "${event.title}"?`)) {
            return;
        }
        this.finalizeTicketPurchase(event);
    }

    closeTicketPaymentModal(): void {
        if (this.processingTicketPayment) return;
        this.showTicketPaymentModal = false;
        this.ticketPaymentEvent = null;
    }

    confirmTicketPayment(): void {
        const ev = this.ticketPaymentEvent;
        if (!ev?.id || !this.currentUserId) return;
        this.processingTicketPayment = true;
        const delayMs = 900;
        setTimeout(() => this.finalizeTicketPurchase(ev), delayMs);
    }

    private finalizeTicketPurchase(event: MarketEvent): void {
        this.ticketService.create(event.id!, { price: event.ticketPrice || 0, userId: this.currentUserId! }).subscribe({
            next: () => {
                this.processingTicketPayment = false;
                this.showTicketPaymentModal = false;
                this.ticketPaymentEvent = null;
                this.ticketRefreshTrigger++;
                this.events = [];
                this.loadLivesAndEvents();
                this.activeTab = 'tickets';
                alert('Payment successful. Your ticket is in My Tickets — you can export it as PDF.');
            },
            error: (err) => {
                this.processingTicketPayment = false;
                console.error(err);
                alert('Could not get ticket: ' + (err.error?.message || err.message || 'Unknown error'));
            }
        });
    }

    loadWorkshops(): void {
        if (this.loadingWorkshops) return;
        this.loadingWorkshops = true;
        this.serviceModuleService.getWorkshops().subscribe({
            next: (ws) => {
                this.workshopsList = (ws || []).filter((w: any) => w.active);
                this.loadingWorkshops = false;
                if (this.currentUserId) {
                    this.serviceModuleService.getMyWorkshopRegistrations().subscribe({
                        next: (regs) => {
                            this.registeredWorkshopIds = (regs || [])
                                .map((r: any) => r.workshopId)
                                .filter((id: number | undefined): id is number => id != null);
                        },
                        error: () => {
                            this.registeredWorkshopIds = [];
                        }
                    });
                } else {
                    this.registeredWorkshopIds = [];
                }
            },
            error: () => {
                this.workshopsList = [];
                this.loadingWorkshops = false;
            }
        });
    }

    isRegisteredToWorkshop(workshopId: number | undefined): boolean {
        if (workshopId == null) return false;
        return this.registeredWorkshopIds.includes(workshopId);
    }

    isWorkshopFull(w: any): boolean {
        const cap = w.capacity ?? 0;
        if (cap <= 0) return false;
        return (w.enrolledCount ?? 0) >= cap;
    }

    registerToWorkshop(w: any): void {
        if (!w?.id) return;
        if (!this.currentUserId) {
            alert('Please sign in to register.');
            return;
        }
        if (this.isWorkshopFull(w)) {
            alert('This workshop is full.');
            return;
        }
        if (this.isRegisteredToWorkshop(w.id)) {
            return;
        }
        if (!confirm(`Register for "${w.title}"?`)) return;
        this.registeringWorkshopId = w.id;
        this.serviceModuleService
            .createRegistration({ workshopId: w.id, userId: this.currentUserId })
            .subscribe({
                next: () => {
                    this.registeringWorkshopId = null;
                    this.registeredWorkshopIds = [...this.registeredWorkshopIds, w.id];
                    alert('You are registered for this workshop.');
                    this.loadWorkshops();
                },
                error: (err) => {
                    this.registeringWorkshopId = null;
                    const msg = err?.error?.message || err?.error?.error || err?.message || 'Registration failed';
                    alert(msg);
                }
            });
    }

    // --- Content Separation Filters ---

    get storeLives(): LiveSession[] {
        return this.lives.filter(l => l.storeId != null);
    }

    get expertLives(): LiveSession[] {
        return this.lives.filter(l => l.serviceId != null);
    }

    get storeEvents(): MarketEvent[] {
        return this.events.filter(e => e.storeId != null && e.type !== 'GAMIFICATION_EVENT' as any);
    }

    /** Expert-created events (workshop / certification) from the same Event API as the expert panel */
    get expertWorkshopEvents(): MarketEvent[] {
        return this.events.filter((e) => {
            const t = e.type as string;
            if (t !== 'WORKSHOP_EVENT' && t !== 'CERTIFICATION_EVENT') return false;
            const s = (e.status as string) || '';
            if (s === 'COMPLETED' || s === 'CANCELLED') return false;
            return true;
        });
    }

    eventSpotsLeft(e: MarketEvent): number {
        const cap = e.capacity ?? 0;
        const sold = e.ticketCount ?? 0;
        return Math.max(0, cap - sold);
    }

    isEventSoldOut(e: MarketEvent): boolean {
        const cap = e.capacity ?? 0;
        if (cap <= 0) return false;
        return this.eventSpotsLeft(e) <= 0;
    }

    get gamificationEvents(): MarketEvent[] {
        return this.events.filter(e => e.type === 'GAMIFICATION_EVENT' as any);
    }
}
