import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Page } from '../models/common.model';
import { environment } from '../../environments/environment';

export interface Nomination {
  id: string;
  awardName: string;
  awardAbbreviation: string;
  category: string;
  year: number;
  outcome: string;
  notes: string;
  titleId: string;
  titleName: string;
  personId: string;
  personName: string;
}

export interface Award {
  nominationId: string;
  awardName: string;
  category: string;
  year: number;
  outcome: string;
  notes: string;
}

export interface TopWinner {
  id: string;
  name: string;
  type: string;
  winsCount: number;
  nominationsCount: number;
}

interface NominationResponseBackend {
  id: string;
  awardName: string;
  awardAbbreviation: string;
  category: string;
  year: number;
  outcome: string;
  notes: string;
  titleId: string;
  titleName: string;
  personId: string;
  personName: string;
}

interface AwardResponseBackend {
  nominationId: string;
  awardName: string;
  category: string;
  year: number;
  outcome: string;
  notes: string;
}

interface TopWinnerResponseBackend {
  id: string;
  name: string;
  type: string;
  winsCount: number;
  nominationsCount: number;
}

@Injectable({ providedIn: 'root' })
export class AwardsService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = environment.apiUrl;

  getTitleAwards(titleId: string): Observable<Nomination[]> {
    return this.http.get<NominationResponseBackend[]>(`${this.API_URL}/awards/titles/${titleId}`).pipe(
      map((list) =>
        list.map((n) => ({
          id: n.id,
          awardName: n.awardName,
          awardAbbreviation: n.awardAbbreviation ?? '',
          category: n.category,
          year: n.year,
          outcome: n.outcome,
          notes: n.notes ?? '',
          titleId: n.titleId ?? '',
          titleName: n.titleName ?? '',
          personId: n.personId ?? '',
          personName: n.personName ?? '',
        })),
      ),
      catchError(() => of([])),
    );
  }

  getPersonAwards(personId: string): Observable<Nomination[]> {
    return this.http.get<NominationResponseBackend[]>(`${this.API_URL}/awards/people/${personId}`).pipe(
      map((list) =>
        list.map((n) => ({
          id: n.id,
          awardName: n.awardName,
          awardAbbreviation: n.awardAbbreviation ?? '',
          category: n.category,
          year: n.year,
          outcome: n.outcome,
          notes: n.notes ?? '',
          titleId: n.titleId ?? '',
          titleName: n.titleName ?? '',
          personId: n.personId ?? '',
          personName: n.personName ?? '',
        })),
      ),
      catchError(() => of([])),
    );
  }

  getTopWinners(year?: number, awardName?: string): Observable<TopWinner[]> {
    const params: Record<string, string> = {};
    if (year) params['year'] = String(year);
    if (awardName) params['awardName'] = awardName;
    return this.http.get<TopWinnerResponseBackend[]>(`${this.API_URL}/awards/top-winners`, { params }).pipe(
      map((list) =>
        list.map((w) => ({
          id: w.id,
          name: w.name,
          type: w.type,
          winsCount: w.winsCount ?? 0,
          nominationsCount: w.nominationsCount ?? 0,
        })),
      ),
      catchError(() => of([])),
    );
  }

  getAwards(filters?: { awardName?: string; year?: number; category?: string; outcome?: string }): Observable<Award[]> {
    const params: Record<string, string> = { page: '0', size: '100' };
    if (filters?.awardName) params['awardName'] = filters.awardName;
    if (filters?.year) params['year'] = String(filters.year);
    if (filters?.category) params['category'] = filters.category;
    if (filters?.outcome) params['outcome'] = filters.outcome;
    return this.http
      .get<Page<AwardResponseBackend>>(`${this.API_URL}/awards`, { params })
      .pipe(
        map((page) =>
          page.content.map((a) => ({
            nominationId: a.nominationId,
            awardName: a.awardName,
            category: a.category,
            year: a.year,
            outcome: a.outcome,
            notes: a.notes ?? '',
          })),
        ),
        catchError(() => of([])),
      );
  }
}