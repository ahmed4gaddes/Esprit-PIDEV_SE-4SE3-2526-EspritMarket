export enum TicketStatus {
    VALID = 'VALID',
    USED = 'USED',
    CANCELLED = 'CANCELLED',
    EXPIRED = 'EXPIRED'
}

export interface Ticket {
    id?: number;
    price: number;
    qrCode?: string;
    checkedIn?: boolean;
    status?: TicketStatus;
    seatNumber?: string;
    purchaseDate?: Date;
    eventId?: number;
    eventTitle?: string;
    userId: number;
    userName?: string;
}
