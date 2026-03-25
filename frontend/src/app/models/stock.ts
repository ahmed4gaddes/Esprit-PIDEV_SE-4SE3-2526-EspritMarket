
export interface Stock {
  id?: number;
  quantity: number;
  type: string;       // MovementType enum : IN | OUT | ADJUSTMENT ...
  date?: Date;
  productId?: number;
  productName?: string;
}