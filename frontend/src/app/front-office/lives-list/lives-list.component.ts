import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LiveSessionService } from '../../core/services/live-session.service';
import { LiveSession, LiveSessionStatus } from '../../core/models/live-session.model';

@Component({
    selector: 'app-lives-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './lives-list.component.html',
    styleUrls: ['./lives-list.component.css']
})
export class LivesListComponent implements OnInit {
    lives: LiveSession[] = [];
    loading = true;

    constructor(private liveSessionService: LiveSessionService) { }

    ngOnInit(): void {
        this.liveSessionService.getAll().subscribe({
            next: (data) => {
                this.lives = data;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error fetching live sessions', err);
                this.loading = false;
            }
        });
    }

    isLive(status?: LiveSessionStatus): boolean {
        return status === LiveSessionStatus.LIVE;
    }
}
