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
      // ✅ Définir le formulaire
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
  
      // ✅ Récupérer l'id depuis l'URL
      this.id = this.act.snapshot.params['id'];
  
      // ✅ Si id existe → mode Update → remplir le formulaire
      if (this.id) {
        this.storeService.getStoreById(this.id).subscribe((result: Store) => {
          this.store = result;
          this.storeForm.patchValue(this.store);  // ✅ remplir formulaire
        });
      }
    }
  
    ngOnInit(): void {}
  
    // ✅ Getters validation
    get name() { return this.storeForm.get('name'); }
    get description() { return this.storeForm.get('description'); }
  get createdAt()   { return this.storeForm.get('createdAt'); }
    // ✅ Retour à la liste
    goBack(): void {
      this.router.navigate(['/stores']);
    }
  
    // ✅ Submit Add ou Update
    onSubmit(): void {
  if (this.storeForm.valid) {
    if (this.id) {
      // ✅ UPDATE — inclure l'id dans les données envoyées
      const storeData = {
        ...this.storeForm.value,
        id: this.id  // ✅ ajouter l'id
      };
      this.storeService.updateStore(storeData, this.id).subscribe(() => {
        alert('Store modifié avec succès !');
        this.router.navigateByUrl('/store');
      });
    } else {
      // ADD
      const storeData = {
        ...this.storeForm.value,
        createdAt: new Date()
      };
      this.storeService.addStore(storeData).subscribe(() => {
        alert('Store ajouté avec succès !');
        this.storeForm.reset({ active: true });
      });
    }
  }
}
  }
