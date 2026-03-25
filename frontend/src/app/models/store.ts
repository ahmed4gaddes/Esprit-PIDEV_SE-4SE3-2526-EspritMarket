export interface Store {
  id: number;
  name: string;
  description: string;
  active: boolean;
  createdAt: Date;
  ownerId?: number;
  ownerName?: string;
  productIds: number[];
  productNames: string[];
  categoryNames?: string[];
  advertisementIds: number[];
  advertisementTitles: string[];
  commissionIds: number[];
  ruleIds: number[];
  ruleTitles: string[];
}
