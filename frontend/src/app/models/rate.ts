export interface Rate {
  id?: number;
  star: number;       // 1 to 5
  comment?: string;
  createdAt?: Date;
  raterId?: number;
  ratedUserId?: number;
}
