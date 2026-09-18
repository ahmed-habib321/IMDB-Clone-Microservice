import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { Person } from '../models/person.model';
import { TitleCard } from '../models/title.model';
import { Page } from '../models/common.model';
import { TitleService } from './title.service';
import { environment } from '../../environments/environment';

export interface MultiSearchResult {
  titles: TitleCard[];
  people: Person[];
}

interface TitleSearchResponse {
  id: string;
  primaryTitle: string;
  posterUrl: string;
  imdbRating: number;
}

interface PersonSearchResponse {
  id: string;
  name: string;
  slug: string;
  alsoKnownAs: string[];
  biography: string;
  profileUrl: string;
  birthDate: string;
  deathDate: string | null;
  birthPlace: string;
  gender: string;
  heightCm: number;
  popularity: number;
  imdbId: string;
}

interface MultiSearchBackend {
  titles: TitleSearchResponse[];
  people: PersonSearchResponse[];
  totalTitles: number;
  totalPeople: number;
}

@Injectable({ providedIn: 'root' })
export class SearchService {
  private readonly http = inject(HttpClient);
  private readonly titleService = inject(TitleService);

  private readonly API_URL = environment.apiUrl;

  searchTitles(
    q: string,
    filters?: {
      genre?: string;
      yearFrom?: number;
      yearTo?: number;
      minRating?: number;
      titleType?: string;
    },
  ): Observable<TitleCard[]> {
    const params: Record<string, string> = { page: '0', size: '50' };
    if (q) params['q'] = q;
    if (filters?.genre) params['genre'] = filters.genre;
    if (filters?.yearFrom) params['yearFrom'] = String(filters.yearFrom);
    if (filters?.yearTo) params['yearTo'] = String(filters.yearTo);
    if (filters?.minRating) params['minRating'] = String(filters.minRating);
    if (filters?.titleType && filters.titleType !== 'ALL') params['titleType'] = filters.titleType;

    return this.http
      .get<Page<TitleSearchResponse>>(`${this.API_URL}/search/titles`, { params })
      .pipe(
        switchMap((page) => this.titleService.resolveTitles(page.content.map((t) => t.id))),
      );
  }

  searchPeople(name: string): Observable<Person[]> {
    const params: Record<string, string> = { page: '0', size: '20' };
    if (name) params['q'] = name;
    return this.http
      .get<Page<PersonSearchResponse>>(`${this.API_URL}/search/people`, { params })
      .pipe(map((page) => page.content.map((p) => this.mapPerson(p))));
  }

  multiSearch(q: string): Observable<MultiSearchResult> {
    return this.http.get<MultiSearchBackend>(`${this.API_URL}/search`, { params: { q } }).pipe(
      switchMap((result) =>
        this.titleService.resolveTitles(result.titles.map((t) => t.id)).pipe(
          map((titles) => ({
            titles,
            people: result.people.map((p) => this.mapPerson(p)),
          })),
        ),
      ),
    );
  }

  getAllGenres(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/title/genres`).pipe(
      map((genres) => (genres ?? []).sort()),
    );
  }

  private mapPerson(p: PersonSearchResponse): Person {
    return {
      id: p.id,
      name: p.name,
      slug: p.slug,
      alsoKnownAs: p.alsoKnownAs ?? [],
      biography: p.biography ?? '',
      profileUrl: p.profileUrl ?? '',
      birthDate: p.birthDate ?? '',
      deathDate: p.deathDate ?? null,
      birthPlace: p.birthPlace ?? '',
      gender: p.gender ?? '',
      heightCm: p.heightCm ?? 0,
      popularity: p.popularity ?? 0,
      imdbId: p.imdbId ?? '',
    };
  }
}