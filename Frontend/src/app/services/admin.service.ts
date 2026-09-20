import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export enum EditorRequestStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
}

export interface EditorRequestResponse {
  id: string;
  userId: string;
  email: string;
  status: EditorRequestStatus;
  requestedAt: string;
  reviewedAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = environment.apiUrl;

  requestEditorRole(): Observable<EditorRequestResponse> {
    return this.http.post<EditorRequestResponse>(`${this.API_URL}/auth/editor-request`, null);
  }

  getEditorRequests(status?: EditorRequestStatus): Observable<EditorRequestResponse[]> {
    return this.http.get<EditorRequestResponse[]>(
      `${this.API_URL}/admin/editor-requests`,
      status ? { params: { status } } : {},
    );
  }

  approveEditorRequest(requestId: string): Observable<EditorRequestResponse> {
    return this.http.post<EditorRequestResponse>(
      `${this.API_URL}/admin/editor-requests/${requestId}/approve`,
      null,
    );
  }

  rejectEditorRequest(requestId: string): Observable<EditorRequestResponse> {
    return this.http.post<EditorRequestResponse>(
      `${this.API_URL}/admin/editor-requests/${requestId}/reject`,
      null,
    );
  }
}