import { Component, output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="relative w-full">
      <span
        class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant">search</span>
      <input
        #searchInput
        [(ngModel)]="query"
        (keyup.enter)="search.emit(query)"
        class="w-full bg-surface-container-highest border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary py-2 pl-10 pr-4 rounded-lg text-body-sm text-on-surface placeholder:text-on-surface-variant transition-all outline-none"
        placeholder="Search CINEPHILE"
        type="text"
      />
    </div>
  `,
})
export class SearchBarComponent {
  query = '';
  search = output<string>();
}
