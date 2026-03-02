import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../Services/category.service'; // ✅ adapter
import { Category } from '../../models/category';             // ✅ adapter

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './category-form.component.html',
  styleUrl: './category-form.component.css'
})
export class CategoryFormComponent implements OnInit {

  categoryForm!: FormGroup;
  category!: Category;
  id!: number;

  categoryTypes: string[] = ['DIGITAL', 'PHYSICAL', 'SERVICE', 'EDUCATION', 'ART', 'TECH'];

  constructor(
    private categoryService: CategoryService,
    private act: ActivatedRoute,
    private router: Router
  ) {
    this.categoryForm = new FormGroup({
      name: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50)
      ]),
      description: new FormControl('', [
        Validators.maxLength(200)
      ]),
      type: new FormControl(null, [
        Validators.required
      ])
    });

    this.id = this.act.snapshot.params['id'];

    if (this.id) {
      this.categoryService.getCategoryById(this.id).subscribe((result: Category) => {
        this.category = result;
        this.categoryForm.patchValue({
          name:        result.name,
          description: result.description,
          type:        result.type
        });
      });
    }
  }

  ngOnInit(): void {}

  get name()        { return this.categoryForm.get('name'); }
  get description() { return this.categoryForm.get('description'); }
  get type()        { return this.categoryForm.get('type'); }

  goBack(): void {
    this.router.navigate(['/admin/categories']);
  }

  onSubmit(): void {
  console.log('🔥 onSubmit called');
  console.log('Form valid:', this.categoryForm.valid);

  if (this.categoryForm.valid) {
    const categoryData = { ...this.categoryForm.value };

    if (this.id) {
      // ✅ UPDATE
      this.categoryService.updateCategory(categoryData, this.id).subscribe({
        next: (res) => {
          console.log('✅ Catégorie modifiée:', res);
          alert('✅ Catégorie modifiée avec succès !');
          this.router.navigateByUrl('/user/category');
        },
        error: (err) => {
          console.error('❌ Erreur update:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    } else {
      // ✅ ADD
      this.categoryService.addCategory(categoryData).subscribe({
        next: (res) => {
          console.log('✅ Catégorie ajoutée:', res);
          alert('✅ Catégorie ajoutée avec succès !');
          this.categoryForm.reset();
        },
        error: (err) => {
          console.error('❌ Erreur add:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    }

  } else {
    console.log('❌ Formulaire invalide');
    Object.values(this.categoryForm.controls).forEach(c => c.markAsTouched());
  }
}
}