import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe, NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  AdminService,
  EditorRequestResponse,
  EditorRequestStatus,
} from '../../../services/admin.service';

@Component({
  selector: 'app-editor-requests',
  standalone: true,
  imports: [RouterLink, DatePipe, NgClass],
  templateUrl: './editor-requests.component.html',
})
export class EditorRequestsComponent implements OnInit {
  private readonly adminService = inject(AdminService);

  readonly requests = signal<EditorRequestResponse[]>([]);
  readonly loading = signal(true);
  readonly filter = signal<EditorRequestStatus | 'ALL'>(EditorRequestStatus.PENDING);
  readonly busyId = signal<string | null>(null);
  errorMessage = '';

  readonly statuses: Array<EditorRequestStatus | 'ALL'> = [
    'ALL',
    EditorRequestStatus.PENDING,
    EditorRequestStatus.APPROVED,
    EditorRequestStatus.REJECTED,
  ];

  ngOnInit(): void {
    this.load();
  }

  setFilter(status: EditorRequestStatus | 'ALL'): void {
    if (this.filter() === status) return;
    this.filter.set(status);
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.errorMessage = '';
    const current = this.filter();
    const status = current === 'ALL' ? undefined : current;
    this.adminService.getEditorRequests(status).subscribe({
      next: (reqs) => {
        this.requests.set(reqs);
        this.loading.set(false);
        this.busyId.set(null);
      },
      error: () => {
        this.errorMessage = 'Failed to load editor requests.';
        this.loading.set(false);
        this.busyId.set(null);
      },
    });
  }

  approve(id: string): void {
    this.busyId.set(id);
    this.adminService.approveEditorRequest(id).subscribe({
      next: () => this.load(),
      error: () => {
        this.errorMessage = 'Failed to approve the request.';
        this.busyId.set(null);
      },
    });
  }

  reject(id: string): void {
    this.busyId.set(id);
    this.adminService.rejectEditorRequest(id).subscribe({
      next: () => this.load(),
      error: () => {
        this.errorMessage = 'Failed to reject the request.';
        this.busyId.set(null);
      },
    });
  }

  isBusy(id: string): boolean {
    return this.busyId() === id;
  }

  statusLabel(status: EditorRequestStatus): string {
    return status;
  }
}