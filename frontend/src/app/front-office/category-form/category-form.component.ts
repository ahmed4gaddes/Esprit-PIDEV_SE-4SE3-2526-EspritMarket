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
    if (this.categoryForm.valid) {
      if (this.id) {
        this.categoryService.updateCategory(this.categoryForm.value, this.id).subscribe(() => {
          this.router.navigateByUrl('/admin/categories');
        });
      } else {
        this.categoryService.addCategory(this.categoryForm.value).subscribe(() => {
          alert('✅ Category added successfully!');
          this.categoryForm.reset();
        });
      }
    }
  }
}