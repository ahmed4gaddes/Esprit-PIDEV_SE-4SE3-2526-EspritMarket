export enum LivePlatform {
    TIKTOK = 'TIKTOK',
    INSTAGRAM = 'INSTAGRAM',
    YOUTUBE = 'YOUTUBE',
    ZOOM = 'ZOOM',
    GOOGLE_MEET = 'GOOGLE_MEET'
}

export enum LiveSessionStatus {
    SCHEDULED = 'SCHEDULED',
    LIVE = 'LIVE',
    ENDED = 'ENDED',
    CANCELLED = 'CANCELLED'
}

export interface LiveSession {
    id?: number;
    title: string;
    description?: string;
    platform: LivePlatform;
    status?: LiveSessionStatus;
    link?: string;
    scheduledAt: Date;
    endTime?: Date;
    thumbnailUrl?: string;
    eventId?: number;
    eventTitle?: string;
    storeId?: number;
    storeName?: string;
    creatorId?: number;
    creatorName?: string;
}
