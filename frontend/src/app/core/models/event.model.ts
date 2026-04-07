export enum EventType {
    TOP_SELLER_EVENT = 'TOP_SELLER_EVENT',
    STORE_ACTIVE_EVENT = 'STORE_ACTIVE_EVENT',
    COMPANY_INTERNSHIP_EVENT = 'COMPANY_INTERNSHIP_EVENT',
    PRODUCT_LAUNCH_EVENT = 'PRODUCT_LAUNCH_EVENT',
    WORKSHOP_EVENT = 'WORKSHOP_EVENT',
    CERTIFICATION_EVENT = 'CERTIFICATION_EVENT',
    GAMIFICATION_EVENT = 'GAMIFICATION_EVENT',
    NETWORKING_EVENT = 'NETWORKING_EVENT'
}

export enum EventStatus {
    UPCOMING = 'UPCOMING',
    ONGOING = 'ONGOING',
    COMPLETED = 'COMPLETED',
    CANCELLED = 'CANCELLED'
}

export interface Event {
    id?: number;
    title: string;
    description: string;
    date: Date;
    capacity: number;
    location: string;
    imageUrl: string;
    status?: EventStatus;
    ticketPrice: number;
    ticketCount?: number;
    type: EventType;
    organizerName?: string;
    organizerId?: number;
    createdAt?: Date;
    // Store link (for seller product launch events)
    storeId?: number;
    storeName?: string;
    // Service link (for Workshops, Certificates, etc.)
    serviceId?: number;
    serviceTitle?: string;
}

// DTO pour la requête JPQL (statistiques organisateur)
export interface EventStatistics {
    eventId: number;
    title: string;
    date: Date;
    status: EventStatus;
    organizerName: string;
    ticketsSold: number;
    totalRevenue: number;
}

// Enum des rôles (pour la recherche Keywords)
export enum UserRole {
    ADMIN = 'ADMIN',
    SELLER = 'SELLER',
    CUSTOMER = 'CUSTOMER',
    SPONSOR = 'SPONSOR',
    EXPERT = 'EXPERT',
    COMPANY = 'COMPANY'
}

