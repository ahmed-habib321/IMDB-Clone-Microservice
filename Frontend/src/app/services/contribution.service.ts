import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Page } from '../models/common.model';
import { environment } from '../../environments/environment';

export interface Trivia {
  id: string;
  body: string;
  isSpoiler: boolean;
  helpfulCount: number;
  createdAt: string;
}

export interface Quote {
  id: string;
  text: string;
  spokenBy: string;
  helpfulCount: number;
}

export interface Goof {
  id: string;
  goofType: string;
  body: string;
  isSpoiler: boolean;
  helpfulCount: number;
}

interface TriviaResponseBackend {
  id: string;
  body: string;
  isSpoiler: boolean;
  isApproved: boolean;
  helpfulCount: number;
  createdAt: string;
}

interface QuoteResponseBackend {
  id: string;
  text: string;
  spokenBy: string;
  helpfulCount: number;
}

interface GoofResponseBackend {
  id: string;
  goofType: string;
  body: string;
  isSpoiler: boolean;
  helpfulCount: number;
}

@Injectable({ providedIn: 'root' })
export class ContributionService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = environment.apiUrl;

  getTrivia(titleId: string): Observable<Trivia[]> {
    return this.http
      .get<Page<TriviaResponseBackend>>(`${this.API_URL}/titles/${titleId}/trivia`, {
        params: { page: 0, size: 50 },
      })
      .pipe(
        map((page) =>
          page.content.map((t) => ({
            id: t.id,
            body: t.body,
            isSpoiler: t.isSpoiler ?? false,
            helpfulCount: t.helpfulCount ?? 0,
            createdAt: t.createdAt,
          })),
        ),
        catchError(() => of([])),
      );
  }

  getQuotes(titleId: string): Observable<Quote[]> {
    return this.http
      .get<QuoteResponseBackend[]>(`${this.API_URL}/titles/${titleId}/quotes`)
      .pipe(
        map((list) =>
          list.map((q) => ({
            id: q.id,
            text: q.text,
            spokenBy: q.spokenBy ?? '',
            helpfulCount: q.helpfulCount ?? 0,
          })),
        ),
        catchError(() => of([])),
      );
  }

  getGoofs(titleId: string): Observable<Goof[]> {
    return this.http
      .get<GoofResponseBackend[]>(`${this.API_URL}/titles/${titleId}/goofs`)
      .pipe(
        map((list) =>
          list.map((g) => ({
            id: g.id,
            goofType: g.goofType ?? '',
            body: g.body,
            isSpoiler: g.isSpoiler ?? false,
            helpfulCount: g.helpfulCount ?? 0,
          })),
        ),
        catchError(() => of([])),
      );
  }

  addTrivia(titleId: string, body: string, isSpoiler = false): Observable<Trivia> {
    return this.http
      .post<TriviaResponseBackend>(`${this.API_URL}/titles/${titleId}/trivia`, { body, isSpoiler })
      .pipe(
        map((t) => ({
          id: t.id,
          body: t.body,
          isSpoiler: t.isSpoiler ?? false,
          helpfulCount: t.helpfulCount ?? 0,
          createdAt: t.createdAt,
        })),
      );
  }

  addQuote(titleId: string, text: string, spokenBy: string): Observable<Quote> {
    return this.http
      .post<QuoteResponseBackend>(`${this.API_URL}/titles/${titleId}/quotes`, { text, spokenBy })
      .pipe(
        map((q) => ({
          id: q.id,
          text: q.text,
          spokenBy: q.spokenBy ?? '',
          helpfulCount: q.helpfulCount ?? 0,
        })),
      );
  }

  addGoof(titleId: string, goofType: string, body: string, isSpoiler = false): Observable<Goof> {
    return this.http
      .post<GoofResponseBackend>(`${this.API_URL}/titles/${titleId}/goofs`, {
        goofType,
        body,
        isSpoiler,
      })
      .pipe(
        map((g) => ({
          id: g.id,
          goofType: g.goofType ?? '',
          body: g.body,
          isSpoiler: g.isSpoiler ?? false,
          helpfulCount: g.helpfulCount ?? 0,
        })),
      );
  }
}