import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent {
  users = [
    { id: 1, name: 'Ahmed Trabelsi', email: 'ahmed.t@esprit.tn', role: 'Student', joined: 'Oct 2025', status: 'Active', avatar: 'AT' },
    { id: 2, name: 'Sonia Ben Ali', email: 'sonia.ba@esprit.tn', role: 'Seller', joined: 'Nov 2025', status: 'Active', avatar: 'SB' },
    { id: 3, name: 'Mehdi Kamoun', email: 'mehdi.k@esprit.tn', role: 'Student', joined: 'Dec 2025', status: 'Inactive', avatar: 'MK' },
    { id: 4, name: 'Yassine Jlassi', email: 'yassine.j@esprit.tn', role: 'Seller', joined: 'Jan 2026', status: 'Active', avatar: 'YJ' },
    { id: 5, name: 'Fatma Dridi', email: 'fatma.d@esprit.tn', role: 'Admin', joined: 'Sep 2025', status: 'Active', avatar: 'FD' }
  ];
}
