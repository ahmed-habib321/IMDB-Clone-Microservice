import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [],
  template: `
    <div class="flex items-center justify-center gap-stack-sm mt-stack-lg">
      <button
        class="w-10 h-10 rounded-full border border-outline flex items-center justify-center hover:bg-surface-container transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
        [disabled]="currentPage() <= 1"
        (click)="pageChange.emit(currentPage() - 1)">
        <span class="material-symbols-outlined">chevron_left</span>
      </button>

      <span class="font-label-caps text-label-caps text-on-surface-variant px-stack-sm">
        {{ currentPage() }} / {{ totalPages() }}
      </span>

      <button
        class="w-10 h-10 rounded-full border border-outline flex items-center justify-center hover:bg-surface-container transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
        [disabled]="currentPage() >= totalPages()"
        (click)="pageChange.emit(currentPage() + 1)">
        <span class="material-symbols-outlined">chevron_right</span>
      </button>
    </div>
  `,
})
export class PaginationComponent {
  currentPage = input(1);
  totalPages = input(1);
  pageChange = output<number>();
}
