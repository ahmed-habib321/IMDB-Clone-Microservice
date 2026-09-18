import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { NotificationService, AppNotification } from '../../../services/notification.service';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, DatePipe],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
})
export class NavBarComponent {
  private readonly router = inject(Router);
  protected readonly auth = inject(AuthService);
  private readonly notificationService = inject(NotificationService);

  mobileMenuOpen = false;
  notifications: AppNotification[] = [];
  unreadCount = 0;
  bellOpen = false;

  constructor() {
    if (this.auth.isAuthenticated()) {
      this.refreshNotifications();
    }
  }

  refreshNotifications(): void {
    this.notificationService.getNotifications().subscribe((n) => {
      this.notifications = n;
      this.unreadCount = n.filter((item) => !item.isRead).length;
    });
  }

  toggleBell(): void {
    this.bellOpen = !this.bellOpen;
    if (this.bellOpen && this.unreadCount > 0) {
      this.markAllRead();
    }
  }

  markAllRead(): void {
    this.notificationService.markAllAsRead().subscribe(() => {
      this.notifications = this.notifications.map((n) => ({ ...n, isRead: true }));
      this.unreadCount = 0;
    });
  }

  search(query: string): void {
    if (query.trim()) {
      this.router.navigate(['/search'], { queryParams: { q: query.trim() } });
      this.mobileMenuOpen = false;
    }
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}