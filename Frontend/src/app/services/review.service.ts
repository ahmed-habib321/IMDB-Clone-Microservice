import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { RatingDistribution, Review, UserRatingResponse } from '../models/review.model';
import { Page } from '../models/common.model';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

interface ReviewResponseBackend {
  id: string;
  userId: string;
  reviewTitle: string;
  body: string;
  containsSpoiler: boolean;
  isApproved: boolean;
  helpfulYes: number;
  helpfulNo: number;
  createdAt: string;
}

interface RatingDistributionBackend {
  distribution: Record<string, number>;
  averageRating: number;
  totalVotes: number;
}

interface UserRatingBackend {
  titleId: string;
  score: number;
  ratedAt: string;
}

interface TitleMiniResponse {
  id: string;
  primaryTitle: string;
  posterUrl: string;
  releaseDate: string;
  status: string;
  titleType: string;
  imdbRating: number;
}

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  private readonly API_URL = environment.apiUrl;

  getReviewsForTitle(titleId: string): Observable<Review[]> {
    return this.http
      .get<Page<ReviewResponseBackend>>(`${this.API_URL}/titles/${titleId}/reviews`, {
        params: { page: 0, size: 20 },
      })
      .pipe(map((page) => page.content.map((r) => this.mapReview(r))));
  }

  getReviewsByUser(userId: string): Observable<Review[]> {
    return this.http
      .get<Page<ReviewResponseBackend>>(`${this.API_URL}/users/${userId}/reviews`, {
        params: { page: 0, size: 20 },
      })
      .pipe(map((page) => page.content.map((r) => this.mapReview(r))));
  }

  getRatingDistribution(titleId: string): Observable<RatingDistribution> {
    return this.http
      .get<RatingDistributionBackend>(`${this.API_URL}/titles/${titleId}/ratings/distribution`)
      .pipe(
        map((d) => ({
          distribution: Object.entries(d.distribution ?? {})
            .map(([score, count]) => ({ score: Number(score), count: Number(count) }))
            .sort((a, b) => b.score - a.score),
          averageRating: d.averageRating ?? 0,
          totalVotes: d.totalVotes ?? 0,
        })),
      );
  }

  getRatingSummary(titleId: string): Observable<{ userScore: number | null; averageRating: number; voteCount: number }> {
    return this.http
      .get<{ userScore: number | null; averageRating: number; voteCount: number }>(
        `${this.API_URL}/titles/${titleId}/ratings`,
      )
      .pipe(
        map((r) => ({
          userScore: r.userScore ?? null,
          averageRating: r.averageRating ?? 0,
          voteCount: r.voteCount ?? 0,
        })),
      );
  }

  getRatingsByUser(userId: string): Observable<UserRatingResponse[]> {
    return this.http
      .get<Page<UserRatingBackend>>(`${this.API_URL}/users/${userId}/ratings`, {
        params: { page: 0, size: 20 },
      })
      .pipe(
        switchMap((page) => {
          const ratings = page.content;
          if (ratings.length === 0) return of([]);

          return this.http.post<TitleMiniResponse[]>(
            `${this.API_URL}/title/search`,
            ratings.map((r) => r.titleId),
          ).pipe(
            map((titles) => {
              const byId = new Map(titles.map((t) => [t.id, t]));
              return ratings.map((r) => ({
                titleId: r.titleId,
                titleName: byId.get(r.titleId)?.primaryTitle ?? '',
                posterUrl: byId.get(r.titleId)?.posterUrl ?? '',
                score: r.score,
                ratedAt: r.ratedAt,
              }));
            }),
          );
        }),
      );
  }

  createReview(review: Partial<Review>): Observable<Review> {
    const titleId = review.titleId ?? '';
    return this.http
      .post<ReviewResponseBackend>(`${this.API_URL}/titles/${titleId}/reviews`, {
        reviewTitle: review.reviewTitle ?? '',
        body: review.body ?? '',
        containsSpoiler: review.containsSpoiler ?? false,
      })
      .pipe(
        map((r) => ({
          ...this.mapReview(r),
          titleId,
          username: this.auth.currentUser()?.username ?? '',
        })),
      );
  }

  submitRating(titleId: string, score: number): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/titles/${titleId}/ratings`, { score });
  }

  deleteRating(titleId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/titles/${titleId}/ratings`);
  }

  voteHelpful(reviewId: string, helpful: boolean): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/reviews/${reviewId}/helpfulness`, null, {
      params: { helpful },
    });
  }

  private mapReview(r: ReviewResponseBackend): Review {
    return {
      id: r.id,
      userId: r.userId,
      username: '',
      titleId: '',
      reviewTitle: r.reviewTitle,
      body: r.body,
      containsSpoiler: r.containsSpoiler,
      isApproved: r.isApproved,
      helpfulYes: r.helpfulYes ?? 0,
      helpfulNo: r.helpfulNo ?? 0,
      createdAt: r.createdAt,
      updatedAt: r.createdAt,
    };
  }
}