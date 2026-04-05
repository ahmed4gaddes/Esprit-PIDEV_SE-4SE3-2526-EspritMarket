import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, User } from '../../core/services/user.service';
import { AuthService } from '../../auth/auth.service';

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
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;
  searchTerm: string = '';
  selectedRole = 'ALL';
  selectedStatus = 'ALL';
  loading = false;
  errorMessage = '';
  currentUserEmail = '';
  readonly roles = ['ADMIN', 'SELLER', 'CUSTOMER', 'EXPERT', 'COMPANY', 'SPONSOR'];

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.currentUserEmail = (this.authService.getUserEmail() || '').toLowerCase().trim();
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.userService.getAllUsers({
      role: this.selectedRole,
      active: this.selectedStatus,
      q: this.searchTerm,
      page: this.currentPage,
      size: this.pageSize
    }).subscribe({
      next: (data) => {
        this.users = data.items;
        this.filteredUsers = data.items;
        this.totalElements = data.totalElements;
        this.totalPages = data.totalPages;
        this.currentPage = data.page;
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
    this.currentPage = 0;
    this.loadUsers();
  }

  toggleStatus(user: User) {
    if (this.isCurrentUser(user)) {
      return;
    }
    const action = user.isActive ? 'block' : 'unblock';
    if (confirm(`Are you sure you want to ${action} ${user.name}?`)) {
      this.userService.toggleUserStatus(user.id).subscribe({
        next: (updatedUser) => {
          const index = this.users.findIndex(u => u.id === updatedUser.id);
          if (index !== -1) {
            this.users[index] = updatedUser;
            this.loadUsers();
          }
        },
        error: (err) => alert('Failed to update status')
      });
    }
  }

  deleteUser(user: User) {
    if (this.isCurrentUser(user)) {
      return;
    }
    if (confirm(`Are you sure you want to DELETE ${user.name}? This cannot be undone.`)) {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== user.id);
          if (this.users.length === 1 && this.currentPage > 0) {
            this.currentPage--;
          }
          this.loadUsers();
        },
        error: (err) => alert('Failed to delete user')
      });
    }
  }

  onRoleModelChange(user: User, newRole: string) {
    if (this.isCurrentUser(user)) {
      return;
    }
    const normalized = (newRole ?? '').toString().toUpperCase().trim();
    const currentRole = (user.role || '').toUpperCase().trim();
    if (!normalized || normalized === currentRole) {
      return;
    }

    const msg = `Change role for "${user.name}" (${user.email})?\n\nFrom: ${currentRole}\nTo: ${normalized}`;
    if (!confirm(msg)) {
      this.refreshUserRowBinding(user);
      return;
    }

    this.userService.updateUserRole(user.id, normalized).subscribe({
      next: (updated) => {
        const idx = this.users.findIndex((u) => u.id === updated.id);
        if (idx !== -1) {
          this.users[idx] = { ...this.users[idx], role: updated.role };
        }
        alert(`Role updated to ${updated.role}.`);
        this.loadUsers();
      },
      error: (err) => {
        this.refreshUserRowBinding(user);
        const serverMsg = err?.error?.message || err?.error?.error || err?.message || 'Failed to update role';
        alert(serverMsg);
      }
    });
  }

  /** Re-syncs the role select after cancel/error (one-way ngModel). */
  private refreshUserRowBinding(user: User) {
    const idx = this.filteredUsers.findIndex((u) => u.id === user.id);
    if (idx !== -1) {
      this.filteredUsers[idx] = { ...this.filteredUsers[idx] };
    }
  }

  /** Current role first so the dropdown reflects the real role (not the first global option). */
  getRoleOptions(user: User): string[] {
    const currentRole = (user.role || '').toUpperCase().trim();
    const base = !currentRole || this.roles.includes(currentRole)
      ? this.roles
      : [currentRole, ...this.roles];
    const rest = base.filter((r) => r !== currentRole);
    return currentRole ? [currentRole, ...rest] : [...this.roles];
  }

  getInitials(name: string): string {
    return name ? name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2) : '??';
  }

  isCurrentUser(user: User): boolean {
    return !!this.currentUserEmail
      && !!user.email
      && user.email.toLowerCase().trim() === this.currentUserEmail;
  }

  previousPage() {
    if (this.currentPage === 0) {
      return;
    }
    this.currentPage--;
    this.loadUsers();
  }

  nextPage() {
    if (this.currentPage + 1 >= this.totalPages) {
      return;
    }
    this.currentPage++;
    this.loadUsers();
  }
}
