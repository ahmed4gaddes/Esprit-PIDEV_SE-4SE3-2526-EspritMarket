export interface Store {
  id: number;
  name: string;
  description: string;
  active: boolean;
  createdAt: Date;
  productIds: number[];
  productNames: string[];
  advertisementIds: number[];
  advertisementTitles: string[];
  commissionIds: number[];
  ruleIds: number[];
  ruleTitles: string[];
}
