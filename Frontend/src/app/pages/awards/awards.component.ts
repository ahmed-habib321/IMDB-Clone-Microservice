import { Component, inject, OnInit, signal } from '@angular/core';
import { AwardsService, Award, TopWinner } from '../../services/awards.service';

@Component({
  selector: 'app-awards',
  standalone: true,
  imports: [],
  template: `
    <main class="w-full max-w-container-max mx-auto px-gutter py-stack-lg">
      <h1 class="font-display-lg text-display-lg font-bold text-on-surface mb-stack-lg uppercase">Awards</h1>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-gutter mb-stack-lg">
        <!-- Top Winners -->
        <div class="lg:col-span-1 bg-surface-container-low rounded-xl border border-outline-variant/20 p-stack-md">
          <h2 class="font-headline-md text-headline-md mb-stack-md flex items-center gap-2 border-l-4 border-primary pl-3">Top Winners</h2>
          @if (topWinners().length === 0) {
            <p class="text-on-surface-variant font-body-sm">No award data available yet.</p>
          } @else {
            <ol class="space-y-stack-sm">
              @for (w of topWinners().slice(0, 10); track w.id; let i = $index) {
                <li class="flex items-center gap-stack-sm">
                  <span class="font-display-lg text-primary/50 text-[24px] w-6 text-right">{{ i + 1 }}</span>
                  <span class="text-on-surface font-bold truncate" [title]="w.name">{{ w.name }}</span>
                  <span class="text-on-surface-variant font-label-caps text-label-caps ml-auto">{{ w.winsCount }} win(s)</span>
                </li>
              }
            </ol>
          }
        </div>

        <!-- Nominations -->
        <div class="lg:col-span-2 bg-surface-container-low rounded-xl border border-outline-variant/20 p-stack-md">
          <div class="flex items-center justify-between mb-stack-md">
            <h2 class="font-headline-md text-headline-md flex items-center gap-2 border-l-4 border-primary pl-3">Nominations</h2>
            <select
              (change)="onYearChange($event)"
              class="bg-surface-container-highest border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary py-2 px-3 rounded-lg text-body-sm text-on-surface transition-all outline-none">
              <option value="">All Years</option>
              @for (y of years(); track y) {
                <option [value]="y">{{ y }}</option>
              }
            </select>
          </div>
          @if (loading()) {
            <div class="h-40 flex items-center justify-center">
              <span class="material-symbols-outlined text-primary animate-spin text-4xl">progress_activity</span>
            </div>
          } @else if (awards().length === 0) {
            <p class="text-on-surface-variant font-body-sm">No nominations match this filter.</p>
          } @else {
            <ul class="space-y-stack-sm">
              @for (a of awards(); track a.nominationId) {
                <li class="flex flex-wrap items-center gap-stack-sm border-b border-outline-variant/10 pb-stack-sm">
                  <span class="font-bold text-on-surface">{{ a.awardName }}</span>
                  <span class="text-on-surface-variant font-body-sm">– {{ a.category }}</span>
                  <span class="text-on-surface-variant font-label-caps text-label-caps">{{ a.year }}</span>
                  <span
                    class="px-2 py-0.5 rounded font-label-caps text-label-caps"
                    [class.bg-emerald-500/15]="a.outcome === 'WON'"
                    [class.text-emerald-400]="a.outcome === 'WON'"
                    [class.bg-surface-container-highest]="a.outcome !== 'WON'"
                    [class.text-on-surface-variant]="a.outcome !== 'WON'">
                    {{ a.outcome }}
                  </span>
                </li>
              }
            </ul>
          }
        </div>
      </div>
    </main>
  `,
})
export class AwardsComponent implements OnInit {
  private readonly awardsService = inject(AwardsService);

  readonly awards = signal<Award[]>([]);
  readonly topWinners = signal<TopWinner[]>([]);
  readonly years = signal<number[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.awardsService.getTopWinners().subscribe((w) => {
      this.topWinners.set(w);
    });
    this.fetchAwards();
  }

  onYearChange(event: Event): void {
    const year = (event.target as HTMLSelectElement).value;
    this.fetchAwards(year ? Number(year) : undefined);
  }

  private fetchAwards(year?: number): void {
    this.loading.set(true);
    this.awardsService.getAwards(year ? { year } : undefined).subscribe((list) => {
      this.awards.set(list);
      const distinct = new Set<number>();
      list.forEach((a) => distinct.add(a.year));
      this.years.set(Array.from(distinct).sort((a, b) => b - a));
      this.loading.set(false);
    });
  }
}