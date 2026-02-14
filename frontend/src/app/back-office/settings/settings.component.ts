import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent {
  notifications = [
    { label: 'New User Alerts', desc: 'When a student registers', enabled: true },
    { label: 'Store Requests', desc: 'New store applications', enabled: true },
    { label: 'Reported Content', desc: 'Flags on products or reviews', enabled: true }
  ];

  maintenanceMode = false;

  toggleMaintenance() {
    this.maintenanceMode = !this.maintenanceMode;
  }

  toggleNotification(item: any) {
    item.enabled = !item.enabled;
  }
}
