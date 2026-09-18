import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { FilmographyItem, Person, PersonDetail } from '../models/person.model';
import { Page } from '../models/common.model';
import { TitleMiniResponse } from './title.service';
import { environment } from '../../environments/environment';

interface PersonDetailResponse {
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

@Injectable({ providedIn: 'root' })
export class PersonService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = environment.apiUrl;

  getPersonById(id: string): Observable<PersonDetail | null> {
    return this.http.get<PersonDetailResponse>(`${this.API_URL}/people/${id}/detail`).pipe(
      switchMap((person) =>
        this.fetchFilmography(id).pipe(
          map((filmography) => ({ ...this.mapPerson(person), filmography })),
        ),
      ),
      catchError(() => of(null)),
    );
  }

  getPersonBySlug(slug: string): Observable<PersonDetail | null> {
    return this.http.get<PersonDetailResponse>(`${this.API_URL}/people/slug/${slug}`).pipe(
      switchMap((person) =>
        this.fetchFilmography(person.id).pipe(
          map((filmography) => ({ ...this.mapPerson(person), filmography })),
        ),
      ),
      catchError(() => of(null)),
    );
  }

  searchPeople(name: string): Observable<Person[]> {
    return this.http
      .get<Page<PersonDetailResponse>>(`${this.API_URL}/search/people`, {
        params: { q: name, page: 0, size: 20 },
      })
      .pipe(map((page) => page.content.map((p) => this.mapPerson(p))));
  }

  private fetchFilmography(personId: string): Observable<FilmographyItem[]> {
    return this.http
      .get<Page<TitleMiniResponse>>(`${this.API_URL}/people/${personId}/filmography`, {
        params: { page: 0, size: 100 },
      })
      .pipe(
        map((page) =>
          page.content.map((t) => ({
            titleId: t.id,
            primaryTitle: t.primaryTitle,
            posterUrl: t.posterUrl ?? '',
            titleType: t.titleType ?? '',
            characterName: '',
            department: '',
            job: '',
            releaseDate: t.releaseDate ?? '',
          })),
        ),
        catchError(() => of([])),
      );
  }

  private mapPerson(p: PersonDetailResponse): Person {
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