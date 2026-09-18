import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ListService } from '../../services/list.service';
import { Watchlist, WatchlistItem } from '../../models/list.model';

@Component({
  selector: 'app-watchlist',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css'],
})
export class WatchlistComponent implements OnInit {
  private readonly listService = inject(ListService);

  readonly watchlist = signal<Watchlist | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.listService.getWatchlist().subscribe((w) => {
      this.watchlist.set(w);
      this.loading.set(false);
    });
  }

  toggleWatched(item: WatchlistItem): void {
    const wl = this.watchlist();
    if (!wl) return;
    const nextWatched = !item.watched;
    this.listService.updateWatchedStatus(item.titleId, nextWatched).subscribe(() => {
      const updatedItems = wl.items.map((i) =>
        i.id === item.id
          ? { ...i, watched: nextWatched, watchedAt: nextWatched ? new Date().toISOString() : null }
          : i,
      );
      this.watchlist.set({ ...wl, items: updatedItems });
    });
  }

  removeItem(item: WatchlistItem): void {
    const wl = this.watchlist();
    if (!wl) return;
    this.listService.removeFromWatchlist(item.titleId).subscribe(() => {
      const updatedItems = wl.items.filter((i) => i.id !== item.id);
      this.watchlist.set({ ...wl, items: updatedItems, itemCount: updatedItems.length });
    });
  }
}
