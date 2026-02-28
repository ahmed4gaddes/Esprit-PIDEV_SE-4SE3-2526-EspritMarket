import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EventService } from '../../core/services/event.service';
import { Event } from '../../core/models/event.model';

@Component({
    selector: 'app-events-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './events-list.component.html',
    styleUrls: ['./events-list.component.css']
})
export class EventsListComponent implements OnInit {
    events: Event[] = [];
    loading = true;

    constructor(private eventService: EventService) { }

    ngOnInit(): void {
        this.eventService.getAll().subscribe({
            next: (data) => {
                this.events = data;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error fetching events', err);
                this.loading = false;
            }
        });
    }
}
