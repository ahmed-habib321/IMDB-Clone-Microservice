import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-person-card',
  standalone: true,
  imports: [RouterLink],
  template: `
    <a class="flex flex-col items-center text-center group cursor-pointer" [routerLink]="['/people', person().id]">
      <div
        class="w-24 h-24 md:w-28 md:h-28 rounded-full bg-surface-container-high overflow-hidden mb-stack-sm border-2 border-outline-variant/40 group-hover:border-primary transition-colors">
        <img class="w-full h-full object-cover" [src]="person().profileUrl" [alt]="person().name" loading="lazy" />
      </div>
      <h3 class="font-bold text-on-surface group-hover:text-primary transition-colors text-sm text-center leading-tight">
        {{ person().name }}
      </h3>
      @if (role()) {
        <p class="text-on-surface-variant font-body-sm text-body-sm text-center">{{ role() }}</p>
      }
    </a>
  `,
})
export class PersonCardComponent {
  person = input.required<{ id: string; name: string; profileUrl: string }>();
  role = input('');
}
