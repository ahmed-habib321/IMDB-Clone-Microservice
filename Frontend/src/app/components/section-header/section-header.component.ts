import { Component, input } from '@angular/core';

@Component({
  selector: 'app-section-header',
  standalone: true,
  imports: [],
  template: `
    <div class="flex items-center justify-between mb-stack-md">
      <h2
        class="font-headline-md text-headline-md flex items-center gap-2 border-l-4 border-primary pl-3">
        @if (icon()) {
          <span class="material-symbols-outlined text-primary mr-1">{{ icon() }}</span>
        }
        {{ title() }}
        @if (chevron()) {
          <span class="material-symbols-outlined text-primary ml-1">chevron_right</span>
        }
      </h2>
      @if (subtitle()) {
        <a
          [href]="link()"
          class="text-on-surface-variant font-label-caps text-label-caps hover:text-primary transition-colors">
          {{ subtitle() }}
        </a>
      }
    </div>
  `,
})
export class SectionHeaderComponent {
  title = input.required<string>();
  subtitle = input('');
  link = input('');
  icon = input('');
  chevron = input(false);
}
