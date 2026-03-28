export interface ProductAssessment {
  id?: number;
  star: number;       // 1 to 5
  comment?: string;
  date?: Date;
  userId?: number;
  productId?: number;

  // Backend may return nested entities (User/Product) instead of flat userId/productId
  user?: {
    id?: number;
  };
  product?: {
    id?: number;
    name?: string;
  };
}
