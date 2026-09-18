import { Component, input } from '@angular/core';

@Component({
  selector: 'app-genre-chip',
  standalone: true,
  imports: [],
  template: `
    <span
      class="inline-flex items-center px-2.5 py-0.5 rounded bg-surface-container-high text-on-surface-variant text-body-sm text-body-sm border border-outline-variant/40">
      {{ genre() }}
    </span>
  `,
})
export class GenreChipComponent {
  genre = input.required<string>();
}
