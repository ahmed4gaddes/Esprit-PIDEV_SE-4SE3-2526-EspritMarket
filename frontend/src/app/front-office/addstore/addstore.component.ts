import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { StoreServiceService } from '../../Services/store-service.service';
import { ActivatedRoute, Router, RouterModule  } from '@angular/router';
import { Store } from '../../models/store';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-addstore',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './addstore.component.html',
  styleUrl: './addstore.component.css'
})
export class AddstoreComponent {
  storeForm!: FormGroup;
    store!: Store;
    id!: number;
  today: Date = new Date();
    constructor(
      private storeService: StoreServiceService,
      private act: ActivatedRoute,
      private router: Router
    ) {
      // Define the form
      this.storeForm = new FormGroup({
        name: new FormControl('', [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(50)
        ]),
        description: new FormControl('', [
          Validators.required,
          Validators.minLength(10)
        ]),
        createdAt: new FormControl({ value: null, disabled: true }),
        active: new FormControl(true)
      });
  
      // Read id from route params
      this.id = this.act.snapshot.params['id'];
  
      // If id exists -> update mode -> populate form
      if (this.id) {
        this.storeService.getStoreById(this.id).subscribe((result: Store) => {
          this.store = result;
          this.storeForm.patchValue(this.store);  // populate form
        });
      }
    }
  
    ngOnInit(): void {}
  
    // Validation getters
    get name() { return this.storeForm.get('name'); }
    get description() { return this.storeForm.get('description'); }
  get createdAt()   { return this.storeForm.get('createdAt'); }
    // Navigate back to list
    goBack(): void {
      this.router.navigate(['/seller/dashboard']);
    }
  
    // Submit add or update
    isSubmitting: boolean = false;

    onSubmit(): void {
      if (this.storeForm.valid && !this.isSubmitting) {
        this.isSubmitting = true;
        if (this.id) {
          // UPDATE - include id in payload
          const storeData = {
            ...this.storeForm.value,
            id: this.id  // include id
          };
          this.storeService.updateStore(storeData, this.id).subscribe({
            next: () => {
              this.isSubmitting = false;
              alert('Store modifié avec succès !');
              this.router.navigateByUrl('/seller/dashboard');
            },
            error: (err) => {
              this.isSubmitting = false;
              alert('Erreur: Impossible de modifier le store');
            }
          });
        } else {
          // ADD
          const storeData = {
            ...this.storeForm.value,
            createdAt: new Date()
          };
          this.storeService.addStore(storeData).subscribe({
            next: () => {
              this.isSubmitting = false;
              alert('Store ajouté avec succès !');
              this.router.navigateByUrl('/seller/dashboard');
            },
            error: (err) => {
              this.isSubmitting = false;
              alert('Erreur: Impossible d\'ajouter le store');
            }
          });
        }
      }
    }
  }
