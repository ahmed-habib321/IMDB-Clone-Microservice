import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { NewsArticle } from '../models/news.model';
import { Page } from '../models/common.model';
import { environment } from '../../environments/environment';

export interface CreateNewsRequest {
  title: string;
  body: string;
  authorUsername: string;
  taggedTitleIds?: string[];
  taggedPersonIds?: string[];
}

interface NewsCardResponse {
  id: string;
  title: string;
  slug: string;
  excerpt: string;
  coverUrl: string;
  authorUsername: string;
  isPublished: boolean;
  publishedAt: string;
  viewCount: number;
}

interface NewsDetailResponse {
  id: string;
  title: string;
  slug: string;
  excerpt: string;
  body: string;
  coverUrl: string;
  authorUsername: string;
  isPublished: boolean;
  publishedAt: string;
  viewCount: number;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NewsService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = environment.apiUrl;

  getNews(): Observable<NewsArticle[]> {
    return this.http
      .get<Page<NewsCardResponse>>(`${this.API_URL}/news`, { params: { page: 0, size: 20 } })
      .pipe(
        map((page) =>
          page.content.map((a) => ({
            id: a.id,
            authorUsername: a.authorUsername,
            title: a.title,
            slug: a.slug,
            body: a.excerpt ?? a.title,
            viewCount: a.viewCount ?? 0,
            publishedAt: a.publishedAt,
            taggedTitles: [],
            taggedPeople: [],
          })),
        ),
        catchError(() => of([])),
      );
  }

  getArticleBySlug(slug: string): Observable<NewsArticle | null> {
    return this.http.get<NewsDetailResponse>(`${this.API_URL}/news/${slug}`).pipe(
      map((a) => ({
        id: a.id,
        authorUsername: a.authorUsername,
        title: a.title,
        slug: a.slug,
        body: a.body ?? a.excerpt ?? '',
        viewCount: a.viewCount ?? 0,
        publishedAt: a.publishedAt,
        taggedTitles: [],
        taggedPeople: [],
      })),
      catchError(() => of(null)),
    );
  }

  createNews(req: CreateNewsRequest): Observable<NewsCardResponse> {
    return this.http.post<NewsCardResponse>(`${this.API_URL}/news`, req);
  }
}