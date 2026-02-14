import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {
    email = '';
    password = '';
    showPassword = false;

    togglePassword() {
        this.showPassword = !this.showPassword;
    }

    onSubmit() {
        console.log('Login:', { email: this.email, password: this.password });
        // TODO: connect to backend API
    }
}
