export interface Category {
  id?: number;
  name: string;
  description?: string;
  type: string; // CategoryType : DIGITAL | PHYSICAL | SERVICE | EDUCATION | ART | TECH
  productIds?: number[];
  productNames?: string[];
}