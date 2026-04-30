export interface Product {
  id?: number;
  name: string;
  description?: string;
  price: number;
  stock: number;
  active: boolean;
  createdAt?: Date;

  // Relations (IDs pour l'envoi au backend)
  storeId?: number;
  categoryId?: number;

  // Relations (noms pour l'affichage)
  storeName?: string;
  categoryName?: string;

  // Images & mouvements (lecture seule)
  images?: any[];
  imageUrl?: string;
  imageId?: number;
  stockMovements?: any[];

  // Stock status (calculé côté backend)
  stockStatus?: 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
}