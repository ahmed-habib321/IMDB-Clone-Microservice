import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import {
  BoxOffice,
  BoxOfficeEntry,
  CastMember,
  CrewMember,
  Movie,
  TitleCard,
  Trailer,
  TvShow,
} from '../models/title.model';
import { environment } from '../../environments/environment';

export interface TitleMiniResponse {
  id: string;
  primaryTitle: string;
  posterUrl: string;
  releaseDate: string;
  status: string;
  titleType: string;
  imdbRating: number;
}

export interface CreateTitleBaseRequest {
  primaryTitle: string;
  originalTitle?: string;
  tagline?: string;
  overview?: string;
  posterUrl?: string;
  backdropUrl?: string;
  status?: string;
  runtimeMins?: number;
  budget?: number;
  revenue?: number;
  adult?: boolean;
  releaseDate?: string;
  genres?: string[];
  languages?: string[];
  countries?: string[];
  titleType?: string;
}

export interface CreateMovieRequest extends CreateTitleBaseRequest {}

export interface CreateTvShowRequest extends CreateTitleBaseRequest {
  network?: string;
  creatorId?: string;
  totalSeasons?: number;
  totalEpisodes?: number;
  episodeRuntime?: number;
  isOnGoing?: boolean;
  seasons?: unknown[];
  finishedAt?: string;
}

interface BoxOfficeResponse {
  titleId?: string;
  id?: string;
  budget?: number;
  openingWeekend?: number;
  domestic?: number;
  international?: number;
  worldwide?: number;
  currency?: string;
  source?: string;
}

interface TrailerResponse {
  id: string;
  name: string;
  trailerType: string;
  youtubeKey: string;
  durationSecs: number;
  language: string;
  publishedAt: string;
}

interface CastResponse {
  id: string;
  personId: string;
  personName: string;
  personSlug: string;
  characterName: string;
  profileUrl: string;
  billingOrder: number;
  isVoice: boolean;
}

interface CrewResponse {
  id: string;
  personId: string;
  personName: string;
  personSlug: string;
  department: string;
  job: string;
  profileUrl: string;
}

interface MovieFullResponse {
  id: string;
  titleType: string;
  primaryTitle: string;
  originalTitle: string;
  slug: string;
  tagline: string;
  overview: string;
  posterUrl: string;
  backdropUrl: string;
  status: string;
  releaseDate: string;
  runtimeMins: number;
  budget: number;
  revenue: number;
  imdbRating: number;
  voteCount: number;
  popularity: number;
  adult: boolean;
  genres: string[];
  languages: string[];
  countries: string[];
  boxOffice: BoxOfficeResponse | null;
  trailers: TrailerResponse[];
}

interface ShowFullResponse extends MovieFullResponse {
  network: string;
  creatorId: string;
  totalSeasons: number;
  totalEpisodes: number;
  episodeRuntime: number;
  isOnGoing: boolean;
  seasons: SeasonResponse[];
}

interface SeasonResponse {
  id: string;
  seasonNumber: number;
  title: string;
  overview: string;
  posterUrl: string;
  airDate: string;
  episodes: EpisodeResponse[];
}

interface EpisodeResponse {
  id: string;
  episodeNumber: number;
  title: string;
  overview: string;
  stillUrl: string;
  airDate: string;
  runtimeMins: number;
  imdbRating: number;
  voteCount: number;
}

interface TitleSearchResponse {
  id: string;
  primaryTitle: string;
  posterUrl: string;
  imdbRating: number;
}

@Injectable({ providedIn: 'root' })
export class TitleService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = environment.apiUrl;

  getTrending(): Observable<TitleCard[]> {
    return this.http
      .get<TitleCard[]>(`${this.API_URL}/title/trending`)
      .pipe(map((t) => t.map((item) => this.mapCard(item))));
  }

  getFeatured(): Observable<TitleCard[]> {
    return this.http
      .get<TitleCard[]>(`${this.API_URL}/title/featured`)
      .pipe(map((t) => t.map((item) => this.mapCard(item))));
  }

  getNewReleases(): Observable<TitleCard[]> {
    return this.http
      .get<TitleCard[]>(`${this.API_URL}/title/new-releases`)
      .pipe(map((t) => t.map((item) => this.mapCard(item))));
  }

  getTitleById(id: string): Observable<Movie | TvShow | null> {
    return this.http.get<MovieFullResponse>(`${this.API_URL}/title/movie/${id}`).pipe(
      map((m) => this.mapMovie(m)),
      catchError((err: HttpErrorResponse) => {
        if (err.status !== 404) return of(null);
        return this.http.get<ShowFullResponse>(`${this.API_URL}/title/show/${id}`).pipe(
          map((s) => this.mapShow(s)),
          catchError(() => of(null)),
        );
      }),
    );
  }

  getCast(titleId: string): Observable<CastMember[]> {
    return this.http.get<CastResponse[]>(`${this.API_URL}/cast/${titleId}`).pipe(
      map((list) =>
        list.map((c) => ({
          personId: c.personId,
          name: c.personName,
          slug: c.personSlug,
          characterName: c.characterName,
          profileUrl: c.profileUrl,
          billingOrder: c.billingOrder ?? 0,
          isVoice: c.isVoice,
          episodeCount: 0,
        })),
      ),
    );
  }

  getCrew(titleId: string): Observable<CrewMember[]> {
    return this.http.get<CrewResponse[]>(`${this.API_URL}/crew/${titleId}`).pipe(
      map((list) =>
        list.map((c) => ({
          personId: c.personId,
          name: c.personName,
          slug: c.personSlug,
          department: c.department,
          job: c.job,
          profileUrl: c.profileUrl,
        })),
      ),
    );
  }

  getSimilar(titleId: string): Observable<TitleCard[]> {
    return this.http
      .get<TitleSearchResponse[]>(`${this.API_URL}/search/titles/${titleId}/similar`)
      .pipe(switchMap((list) => this.resolveTitles(list.map((t) => t.id))));
  }

  getMovieBoxOffice(titleId: string): Observable<BoxOffice | undefined> {
    return this.http.get<BoxOfficeResponse>(`${this.API_URL}/titles/${titleId}/box-office`).pipe(
      map((b) => this.mapBoxOffice(b)),
      catchError(() => of(undefined)),
    );
  }

  getMovieTrailers(titleId: string): Observable<Trailer[]> {
    return this.http.get<TrailerResponse[]>(`${this.API_URL}/titles/${titleId}/trailers`).pipe(
      map((list) =>
        list.map((t) => ({
          id: t.id,
          name: t.name,
          trailerType: t.trailerType,
          youtubeKey: t.youtubeKey,
          durationSecs: t.durationSecs,
          language: t.language,
          publishedAt: t.publishedAt,
        })),
      ),
      catchError(() => of([])),
    );
  }

  getBoxOfficeTop(): Observable<BoxOfficeEntry[]> {
    return this.http
      .get<BoxOfficeResponse[]>(`${this.API_URL}/box-office/top`, { params: { limit: 10 } })
      .pipe(
        switchMap((rows) => {
          const ids = rows.map((r) => r.titleId).filter((v): v is string => !!v);
          if (ids.length === 0) return of([]);
          return this.resolveTitles(ids).pipe(
            map((titles) => {
              const byId = new Map(titles.map((t) => [t.id, t]));
              return rows
                .filter((r) => r.titleId)
                .map((r) => {
                  const t = byId.get(r.titleId as string);
                  return {
                    id: r.titleId as string,
                    primaryTitle: t?.primaryTitle ?? 'Unknown',
                    posterUrl: t?.posterUrl ?? '',
                    boxOffice: { worldwide: r.worldwide ?? 0 },
                  };
                });
            }),
          );
        }),
      );
  }

  resolveTitles(ids: string[]): Observable<TitleCard[]> {
    if (ids.length === 0) return of([]);
    return this.http.post<TitleMiniResponse[]>(`${this.API_URL}/title/search`, ids).pipe(
      map((list) =>
        list.map((t) => ({
          id: t.id,
          titleType: (t.titleType ?? 'MOVIE') as TitleCard['titleType'],
          primaryTitle: t.primaryTitle,
          slug: '',
          overview: '',
          posterUrl: t.posterUrl ?? '',
          backdropUrl: '',
          status: (t.status ?? 'RELEASED') as TitleCard['status'],
          releaseDate: t.releaseDate ?? '',
          imdbRating: t.imdbRating ?? 0,
          voteCount: 0,
          popularity: 0,
          genres: [],
        })),
      ),
    );
  }

  private mapCard(t: TitleCard): TitleCard {
    return {
      ...t,
      genres: Array.isArray(t.genres) ? t.genres : [],
      releaseDate: t.releaseDate ?? '',
      imdbRating: t.imdbRating ?? 0,
    };
  }

  private mapBoxOffice(b: BoxOfficeResponse): BoxOffice {
    return {
      budget: b.budget ?? 0,
      openingWeekend: b.openingWeekend ?? 0,
      domestic: b.domestic ?? 0,
      international: b.international ?? 0,
      worldwide: b.worldwide ?? 0,
      currency: b.currency ?? 'USD',
    };
  }

  private mapMovie(m: MovieFullResponse): Movie {
    return {
      id: m.id,
      titleType: m.titleType as Movie['titleType'],
      primaryTitle: m.primaryTitle,
      originalTitle: m.originalTitle,
      slug: m.slug,
      tagline: m.tagline,
      overview: m.overview,
      posterUrl: m.posterUrl,
      backdropUrl: m.backdropUrl,
      status: m.status as Movie['status'],
      releaseDate: m.releaseDate ?? '',
      runtimeMins: m.runtimeMins ?? 0,
      budget: m.budget ?? 0,
      revenue: m.revenue ?? 0,
      imdbRating: m.imdbRating ?? 0,
      voteCount: m.voteCount ?? 0,
      popularity: m.popularity ?? 0,
      adult: m.adult ?? false,
      genres: m.genres ?? [],
      languages: m.languages ?? [],
      countries: m.countries ?? [],
      boxOffice: m.boxOffice ? this.mapBoxOffice(m.boxOffice) : (undefined as unknown as BoxOffice),
      trailers: m.trailers ?? [],
    };
  }

  private mapShow(s: ShowFullResponse): TvShow {
    return {
      ...s,
      titleType: s.titleType as TvShow['titleType'],
      status: s.status as TvShow['status'],
      releaseDate: s.releaseDate ?? '',
      seasons: s.seasons ?? [],
    };
  }

  createMovie(req: CreateMovieRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/title/movie`, req);
  }

  createShow(req: CreateTvShowRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/title/show`, req);
  }
}