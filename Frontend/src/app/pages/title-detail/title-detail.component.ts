import { Component, inject, input, OnChanges, OnInit, signal, SimpleChanges } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { TitleCasePipe } from '@angular/common';
import { TitleService } from '../../services/title.service';
import { ReviewService } from '../../services/review.service';
import { AuthService } from '../../services/auth.service';
import { ListService } from '../../services/list.service';
import { ContributionService, Goof, Quote, Trivia } from '../../services/contribution.service';
import { Movie, TvShow, CastMember, CrewMember, BoxOffice, TitleCard } from '../../models/title.model';
import { Review, RatingDistribution } from '../../models/review.model';
import { MovieCardComponent } from '../../components/movie-card/movie-card.component';
import { PersonCardComponent } from '../../components/person-card/person-card.component';
import { RatingBadgeComponent } from '../../components/rating-badge/rating-badge.component';
import { ReviewCardComponent } from '../../components/review-card/review-card.component';
import { GenreChipComponent } from '../../components/genre-chip/genre-chip.component';

@Component({
  selector: 'app-title-detail',
  standalone: true,
  imports: [
    RouterLink,
    TitleCasePipe,
    MovieCardComponent,
    PersonCardComponent,
    RatingBadgeComponent,
    ReviewCardComponent,
    GenreChipComponent,
  ],
  templateUrl: './title-detail.component.html',
  styleUrls: ['./title-detail.component.css'],
})
export class TitleDetailComponent implements OnInit, OnChanges {
  id = input.required<string>();

  private readonly titleService = inject(TitleService);
  private readonly reviewService = inject(ReviewService);
  private readonly auth = inject(AuthService);
  private readonly listService = inject(ListService);
  private readonly contributionService = inject(ContributionService);
  private readonly router = inject(Router);

  readonly title = signal<Movie | TvShow | null>(null);
  readonly cast = signal<CastMember[]>([]);
  readonly crew = signal<CrewMember[]>([]);
  readonly reviews = signal<Review[]>([]);
  readonly ratingDist = signal<RatingDistribution | null>(null);
  readonly similar = signal<TitleCard[]>([]);
  readonly boxOfficeSg = signal<BoxOffice | undefined>(undefined);
  readonly trivia = signal<Trivia[]>([]);
  readonly quotes = signal<Quote[]>([]);
  readonly goofs = signal<Goof[]>([]);
  readonly loading = signal(true);
  readonly inWatchlist = signal(false);
  readonly isLoggedIn = this.auth.isAuthenticated.asReadonly();

  ngOnInit(): void {
    this.load();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['id'] && !changes['id'].firstChange) {
      this.load();
    }
  }

  private load(): void {
    const id = this.id();
    if (!id) return;

    this.loading.set(true);
    this.titleService.getTitleById(id).subscribe((t) => {
      this.title.set(t);
      this.loading.set(false);
      if (t?.titleType === 'MOVIE') {
        this.titleService.getMovieBoxOffice(id).subscribe((b) => this.boxOfficeSg.set(b));
        this.titleService.getMovieTrailers(id).subscribe(() => undefined);
      }
    });
    this.titleService.getCast(id).subscribe((c) => this.cast.set(c));
    this.titleService.getCrew(id).subscribe((c) => this.crew.set(c));
    this.reviewService.getReviewsForTitle(id).subscribe((r) => this.reviews.set(r));
    this.reviewService.getRatingDistribution(id).subscribe((d) => this.ratingDist.set(d));
    this.titleService.getSimilar(id).subscribe((s) => this.similar.set(s));
    this.contributionService.getTrivia(id).subscribe((t) => this.trivia.set(t));
    this.contributionService.getQuotes(id).subscribe((q) => this.quotes.set(q));
    this.contributionService.getGoofs(id).subscribe((g) => this.goofs.set(g));

    if (this.auth.isAuthenticated()) {
      this.listService.isInWatchlist(id).subscribe((inList) => this.inWatchlist.set(inList));
    } else {
      this.inWatchlist.set(false);
    }
  }

  toggleWatchlist(): void {
    const id = this.id();
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    if (this.inWatchlist()) {
      this.listService.removeFromWatchlist(id).subscribe(() => this.inWatchlist.set(false));
    } else {
      this.listService.addToWatchlist(id).subscribe(() => this.inWatchlist.set(true));
    }
  }

  voteHelpful(reviewId: string, helpful: boolean): void {
    this.reviewService.voteHelpful(reviewId, helpful).subscribe(() => undefined);
  }

  addToList(): void {
    this.router.navigate(['/lists']);
  }

  runtime(): string {
    const t = this.title();
    if (!t) return '';
    const mins = t.runtimeMins;
    const h = Math.floor(mins / 60);
    const m = mins % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  isMovie(): boolean {
    return this.title()?.titleType === 'MOVIE';
  }

  boxOffice(): BoxOffice | undefined {
    return this.boxOfficeSg();
  }

  maxCount(dist: { score: number; count: number }[]): number {
    if (dist.length === 0) return 1;
    return Math.max(...dist.map((d) => d.count));
  }

  formatMoney(amount?: number): string {
    if (!amount) return '0';
    if (amount >= 1000000000) return (amount / 1000000000).toFixed(1) + 'B';
    if (amount >= 1000000) return (amount / 1000000).toFixed(1) + 'M';
    if (amount >= 1000) return (amount / 1000).toFixed(1) + 'K';
    return String(amount);
  }
}