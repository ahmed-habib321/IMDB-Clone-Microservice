import { Component, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Review } from '../../models/review.model';

@Component({
  selector: 'app-review-card',
  standalone: true,
  imports: [RouterLink, DatePipe],
  template: `
    <article class="bg-surface-container-low border border-outline-variant/30 rounded-xl p-stack-md">
      <div class="flex items-center justify-between mb-stack-sm">
        <div class="flex items-center gap-stack-sm">
          <a
            [routerLink]="['/profile', review().userId]"
            class="w-9 h-9 rounded-full bg-surface-container-high overflow-hidden flex items-center justify-center">
            <span class="material-symbols-outlined text-on-surface-variant">person</span>
          </a>
          <div>
            <a
              [routerLink]="['/profile', review().userId]"
              class="font-bold text-body-md text-on-surface hover:text-primary transition-colors">
              {{ review().username || 'Member' }}
            </a>
            <p class="text-on-surface-variant font-label-caps text-label-caps">{{ review().createdAt | date: 'mediumDate' }}</p>
          </div>
        </div>
        @if (review().containsSpoiler) {
          <span class="bg-error-container text-on-error-container px-2 py-0.5 rounded font-label-caps text-label-caps">SPOILER</span>
        }
      </div>

      <h4 class="font-headline-md text-headline-md font-bold text-on-surface mb-stack-xs">
        {{ review().reviewTitle }}
      </h4>
      <p class="text-body-md text-on-surface-variant leading-relaxed">
        {{ review().body }}
      </p>

      <div class="flex items-center gap-stack-md mt-stack-md pt-stack-sm border-t border-outline-variant/30">
        <button
          class="flex items-center gap-1 text-on-surface-variant hover:text-primary transition-colors"
          (click)="helpful.emit(true)">
          <span class="material-symbols-outlined text-lg">thumb_up</span>
          <span class="font-label-caps text-label-caps">{{ review().helpfulYes }}</span>
        </button>
        <button
          class="flex items-center gap-1 text-on-surface-variant hover:text-error transition-colors"
          (click)="helpful.emit(false)">
          <span class="material-symbols-outlined text-lg">thumb_down</span>
          <span class="font-label-caps text-label-caps">{{ review().helpfulNo }}</span>
        </button>
      </div>
    </article>
  `,
})
export class ReviewCardComponent {
  review = input.required<Review>();
  helpful = output<boolean>();
}
