import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TitleCard } from '../../models/title.model';

@Component({
  selector: 'app-movie-card',
  standalone: true,
  imports: [RouterLink],
  template: `
    <a
      [routerLink]="['/title', title().id]"
      class="group block cursor-pointer"
    >
      <div
        class="relative aspect-[2/3] bg-surface-container rounded-lg overflow-hidden mb-stack-sm border border-outline-variant/30 transition-transform duration-200 hover:scale-102">
        <img
          class="w-full h-full object-cover"
          [src]="title().posterUrl"
          [alt]="title().primaryTitle"
          loading="lazy"
        />
        <div
          class="absolute top-2 left-2 bg-black/60 backdrop-blur-md px-1.5 py-0.5 rounded flex items-center gap-1">
          <span class="material-symbols-outlined text-[16px] text-primary" style="font-variation-settings: 'FILL' 1;">star</span>
          <span class="font-label-caps text-label-caps text-on-surface">{{ title().imdbRating.toFixed(1) }}</span>
        </div>
        @if (showType()) {
          <div class="absolute top-2 right-2 bg-black/60 backdrop-blur-md px-1.5 py-0.5 rounded">
            <span class="font-label-caps text-label-caps text-on-surface">{{ title().titleType }}</span>
          </div>
        }
      </div>
      <h3 class="font-bold text-on-surface truncate group-hover:text-primary transition-colors">
        {{ title().primaryTitle }}
      </h3>
      <div class="flex items-center justify-between mt-1">
        <span class="text-on-surface-variant font-label-caps text-label-caps">{{ releaseYear }}</span>
      </div>
    </a>
  `,
})
export class MovieCardComponent {
  title = input.required<TitleCard>();
  showType = input(false);

  get releaseYear(): string {
    return this.title().releaseDate.substring(0, 4);
  }
}
