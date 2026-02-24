import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, User } from '../../core/services/user.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  searchTerm: string = '';
  loading = false;
  errorMessage = '';

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.filterUsers();
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load users';
        this.loading = false;
        console.error(err);
      }
    });
  }

  filterUsers() {
    if (!this.searchTerm) {
      this.filteredUsers = this.users;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredUsers = this.users.filter(u =>
        u.name.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term)
      );
    }
  }

  toggleStatus(user: User) {
    const action = user.isActive ? 'block' : 'unblock';
    if (confirm(`Are you sure you want to ${action} ${user.name}?`)) {
      this.userService.toggleUserStatus(user.id).subscribe({
        next: (updatedUser) => {
          const index = this.users.findIndex(u => u.id === updatedUser.id);
          if (index !== -1) {
            this.users[index] = updatedUser;
            this.filterUsers();
          }
        },
        error: (err) => alert('Failed to update status')
      });
    }
  }

  deleteUser(user: User) {
    if (confirm(`Are you sure you want to DELETE ${user.name}? This cannot be undone.`)) {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== user.id);
          this.filterUsers();
        },
        error: (err) => alert('Failed to delete user')
      });
    }
  }

  getInitials(name: string): string {
    return name ? name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2) : '??';
  }
}
