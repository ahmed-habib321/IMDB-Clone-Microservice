import { Component, input } from '@angular/core';

@Component({
  selector: 'app-rating-badge',
  standalone: true,
  imports: [],
  template: `
    <div class="flex items-center gap-1">
      <span class="material-symbols-outlined text-primary text-lg" style="font-variation-settings: 'FILL' 1;">star</span>
      <span class="font-headline-md text-headline-md font-bold text-on-surface">{{ rating().toFixed(1) }}</span>
      @if (showVotes()) {
        <span class="text-on-surface-variant font-label-caps text-label-caps">/ 10</span>
      }
    </div>
  `,
})
export class RatingBadgeComponent {
  rating = input.required<number>();
  showVotes = input(false);
}
