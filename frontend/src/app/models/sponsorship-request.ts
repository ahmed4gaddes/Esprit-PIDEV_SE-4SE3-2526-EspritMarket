export interface SponsorshipRequest {
  id?: number;
  offerTitle?: string;
  offerDescription?: string;
  budget?: number;
  message?: string;
  date?: string | Date;
  state?: 'PENDING' | 'APPROVED' | 'REJECTED';
  sponsorDesignUrl?: string | null;
  sponsorNote?: string | null;
  companyId?: number;
  companyName?: string;
  sponsorId?: number;
  sponsorName?: string;
}

export interface SponsorshipDecisionPayload {
  approved: boolean;
  designUrl?: string;
  note?: string;
}

