import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

// Reusing landing page components
import { NavigationComponent } from '../../front-office/navigation/navigation.component';
import { FooterComponent } from '../../front-office/footer/footer.component';

// IMPORTANT: We must ensure CustomerTicketsComponent is correctly imported
import { CustomerTicketsComponent } from '../customer-tickets/customer-tickets.component';

// Services & Models
import { LiveSessionService } from '../../core/services/live-session.service';
import { EventService } from '../../core/services/event.service';
import { TicketService } from '../../core/services/ticket.service';
import { AuthService } from '../../auth/auth.service';
import { StoreServiceService } from '../../Services/store-service.service';
import { ProductService } from '../../Services/product.service';
import { CartService } from '../../core/services/cart.service';
import { CategoryService } from '../../Services/category.service';

import { LiveSession } from '../../core/models/live-session.model';
import { Event } from '../../core/models/event.model';
import { Store } from '../../models/store';
import { Product } from '../../models/product';
import { Category } from '../../models/category';

@Component({
    selector: 'app-customer-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        NavigationComponent,
        FooterComponent,
        CustomerTicketsComponent
    ],
    templateUrl: './customer-dashboard.component.html',
    styleUrls: ['./customer-dashboard.component.css']
})
export class CustomerDashboardComponent implements OnInit {
    activeTab: 'stores' | 'lives' | 'tickets' = 'stores';
    lives: LiveSession[] = [];
    events: Event[] = [];
    stores: Store[] = [];
    products: Product[] = [];

    loadingLives = false;
    loadingEvents = false;
    loadingStores = true;
    currentUserId: number | null = null;

    expandedStoreId: number | null = null;
    
    // Category state
    storeCategories: { [storeId: number]: Category[] } = {};
    loadingCategories: { [storeId: number]: boolean } = {};
    expandedCategoryId: number | null = null;

    constructor(
        private liveSessionService: LiveSessionService,
        private eventService: EventService,
        private ticketService: TicketService,
        private authService: AuthService,
        private storeService: StoreServiceService,
        private productService: ProductService,
        private cartService: CartService,
        private categoryService: CategoryService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.currentUserId = 1; // Mock user id for demonstration
        this.loadLivesAndEvents();
        this.loadStoresAndProducts();
    }

    loadStoresAndProducts() {
        this.storeService.getAllStores().subscribe({
            next: (storesData) => {
                this.stores = storesData;
                this.productService.getAllProducts().subscribe({
                    next: (productsData) => {
                        this.products = productsData;
                        this.loadingStores = false;
                    },
                    error: () => this.loadingStores = false
                });
            },
            error: () => this.loadingStores = false
        });
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
        // Un produit est lié à une catégorie par product.categoryId
        return this.products.filter(p => p.categoryId === categoryId);
    }

    getProductsForStore(storeId: number): Product[] {
        return this.products.filter(p => p.storeId === storeId);
    }

    addToCart(product: Product) {
        this.cartService.addItem(product, 1);
        // Simple visual feedback could be improved using a Toast service
        alert(`"${product.name}" a été ajouté au panier !`);
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
                    // Filter for product launch events if necessary
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

    buyTicket(event: Event) {
        if (!event.id) return;
        if (confirm("Are you sure you want to buy a ticket for '" + event.title + "' for " + (event.ticketPrice === 0 ? 'FREE' : event.ticketPrice) + "?")) {
            this.ticketService.create(event.id, { price: event.ticketPrice || 0, userId: this.currentUserId! }).subscribe({
                next: () => {
                    alert("Ticket purchased successfully! You can view it in the 'My Tickets' tab.");
                    this.events = [];
                    this.loadLivesAndEvents();
                },
                error: (err) => {
                    console.error(err);
                    alert("Error buying ticket: " + (err.error?.message || 'Unknown error'));
                }
            });
        }
    }

    // --- Content Separation Filters ---

    get storeLives(): LiveSession[] {
        return this.lives.filter(l => l.storeId != null);
    }

    get expertLives(): LiveSession[] {
        return this.lives.filter(l => l.serviceId != null);
    }

    get storeEvents(): Event[] {
        return this.events.filter(e => e.storeId != null && e.type !== 'GAMIFICATION_EVENT' as any);
    }

    get expertEvents(): Event[] {
        return this.events.filter(e => e.serviceId != null && e.type !== 'GAMIFICATION_EVENT' as any);
    }

    get gamificationEvents(): Event[] {
        return this.events.filter(e => e.type === 'GAMIFICATION_EVENT' as any);
    }
}
