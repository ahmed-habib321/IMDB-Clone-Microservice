import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface AppNotification {
  id: string;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

interface NotificationResponseBackend {
  id: string;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

interface UnreadCountResponse {
  count: number;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = environment.apiUrl;

  getNotifications(): Observable<AppNotification[]> {
    return this.http.get<NotificationResponseBackend[]>(`${this.API_URL}/notification`).pipe(
      map((list) =>
        list.map((n) => ({
          id: n.id,
          title: n.title,
          message: n.message,
          type: n.type,
          isRead: n.isRead ?? false,
          createdAt: n.createdAt,
        })),
      ),
      catchError(() => of([])),
    );
  }

  getUnreadCount(): Observable<number> {
    return this.http.get<UnreadCountResponse>(`${this.API_URL}/notification/unread-count`).pipe(
      map((r) => r.count ?? 0),
      catchError(() => of(0)),
    );
  }

  markAsRead(id: string): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/notification/${id}/read`, null);
  }

  markAllAsRead(): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/notification/read-all`, null);
  }
}