import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ListSummary, UserList, UserListItem, Watchlist, WatchlistItem } from '../models/list.model';
import { Page } from '../models/common.model';
import { TitleService, TitleMiniResponse } from './title.service';
import { environment } from '../../environments/environment';

interface WatchlistItemBackend {
  id: string;
  titleId: string;
  primaryTitle: string;
  slug: string;
  posterUrl: string;
  imdbRating: number;
  watched: boolean;
  addedAt: string;
  watchedAt: string | null;
}

interface UserListCardBackend {
  id: string;
  name: string;
  description: string;
  isPublic: boolean;
  itemCount: number;
  createdAt: string;
}

interface UserListDetailBackend {
  id: string;
  name: string;
  description: string;
  isPublic: boolean;
  itemCount: number;
  createdAt: string;
  titleIds: string[];
}

interface ListSummaryBackend {
  id: string;
  name: string;
  itemCount: number;
}

@Injectable({ providedIn: 'root' })
export class ListService {
  private readonly http = inject(HttpClient);
  private readonly titleService = inject(TitleService);

  private readonly API_URL = environment.apiUrl;

  getWatchlist(): Observable<Watchlist | null> {
    return this.http
      .get<Page<WatchlistItemBackend>>(`${this.API_URL}/watchlist`, {
        params: { page: 0, size: 100 },
      })
      .pipe(
        map((page) => {
          const items = page.content.map((i) => this.mapWatchlistItem(i));
          const updatedAt = items.reduce(
            (max, i) => (i.addedAt > max ? i.addedAt : max),
            '',
          );
          return {
            id: '',
            userId: '',
            itemCount: items.length,
            items,
            updatedAt,
          } satisfies Watchlist;
        }),
      );
  }

  isInWatchlist(titleId: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_URL}/watchlist/contains`, {
      params: { titleId },
    });
  }

  addToWatchlist(titleId: string): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/watchlist`, JSON.stringify(titleId), {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  updateWatchedStatus(titleId: string, watched: boolean): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/watchlist`, { titleId, watched });
  }

  removeFromWatchlist(titleId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/watchlist`, {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(titleId),
    });
  }

  getLists(): Observable<UserList[]> {
    return this.http
      .get<Page<UserListCardBackend>>(`${this.API_URL}/lists`, {
        params: { page: 0, size: 100 },
      })
      .pipe(map((page) => page.content.map((l) => this.mapListCard(l))));
  }

  getListById(id: string): Observable<UserList | null> {
    return this.http.get<UserListDetailBackend>(`${this.API_URL}/lists/${id}`).pipe(
      switchMap((list) => {
        const base = {
          id: list.id,
          userId: '',
          name: list.name,
          description: list.description ?? '',
          isPublic: list.isPublic ?? true,
          itemCount: list.itemCount ?? 0,
          items: [] as UserListItem[],
          createdAt: list.createdAt ?? '',
          updatedAt: list.createdAt ?? '',
        };
        if (!list.titleIds || list.titleIds.length === 0) return of(base);

        return this.titleService.resolveTitles(list.titleIds).pipe(
          map((titles) => ({
            ...base,
            items: titles.map((t) => this.mapListItem(t.id, t)),
          })),
        );
      }),
    );
  }

  getListSummaries(): Observable<ListSummary[]> {
    return this.http
      .get<Page<ListSummaryBackend>>(`${this.API_URL}/lists/summary`, {
        params: { page: 0, size: 100 },
      })
      .pipe(
        map((page) =>
          page.content.map((s) => ({ id: s.id, name: s.name, itemCount: s.itemCount ?? 0 })),
        ),
      );
  }

  createList(name: string, description: string, isPublic: boolean): Observable<UserList> {
    return this.http
      .post<UserListCardBackend>(`${this.API_URL}/lists`, { name, description, isPublic })
      .pipe(map((l) => this.mapListCard(l)));
  }

  deleteList(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/lists/${id}`);
  }

  addTitleToList(listId: string, titleId: string): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/lists/${listId}`, JSON.stringify(titleId), {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  removeTitleFromList(listId: string, titleId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/lists/${listId}/${titleId}`);
  }

  private mapWatchlistItem(i: WatchlistItemBackend): WatchlistItem {
    return {
      id: i.id,
      titleId: i.titleId,
      primaryTitle: i.primaryTitle,
      posterUrl: i.posterUrl ?? '',
      addedAt: i.addedAt,
      watched: i.watched ?? false,
      watchedAt: i.watchedAt ?? null,
      notes: '',
    };
  }

  private mapListCard(l: UserListCardBackend): UserList {
    return {
      id: l.id,
      userId: '',
      name: l.name,
      description: l.description ?? '',
      isPublic: l.isPublic ?? true,
      itemCount: l.itemCount ?? 0,
      items: [],
      createdAt: l.createdAt ?? '',
      updatedAt: l.createdAt ?? '',
    };
  }

  private mapListItem(titleId: string, t: TitleMiniResponse): UserListItem {
    return {
      id: titleId,
      titleId,
      primaryTitle: t.primaryTitle,
      posterUrl: t.posterUrl ?? '',
      imdbRating: t.imdbRating ?? 0,
      addedAt: '',
      notes: '',
    };
  }
}