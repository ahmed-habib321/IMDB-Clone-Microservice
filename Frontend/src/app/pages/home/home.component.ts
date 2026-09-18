import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BoxOfficeEntry, TitleCard } from '../../models/title.model';
import { TitleService } from '../../services/title.service';
import { NewsService } from '../../services/news.service';
import { NewsArticle } from '../../models/news.model';
import { MovieCardComponent } from '../../components/movie-card/movie-card.component';
import { SectionHeaderComponent } from '../../components/section-header/section-header.component';
import { ArticleCardComponent } from '../../components/article-card/article-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, MovieCardComponent, SectionHeaderComponent, ArticleCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  private readonly titleService = inject(TitleService);
  private readonly newsService = inject(NewsService);

  readonly featured = signal<TitleCard[]>([]);
  readonly trending = signal<TitleCard[]>([]);
  readonly boxOffice = signal<BoxOfficeEntry[]>([]);
  readonly newReleases = signal<TitleCard[]>([]);
  readonly news = signal<NewsArticle[]>([]);
  readonly loading = signal(true);

  activeSlide = 0;

  ngOnInit(): void {
    this.titleService.getFeatured().subscribe((t) => {
      this.featured.set(t);
      this.loading.set(false);
    });
    this.titleService.getTrending().subscribe((t) => this.trending.set(t));
    this.titleService.getBoxOfficeTop().subscribe((b) => this.boxOffice.set(b));
    this.titleService.getNewReleases().subscribe((n) => this.newReleases.set(n));
    this.newsService.getNews().subscribe((n) => this.news.set(n.slice(0, 4)));
  }

  heroTitle(): TitleCard | null {
    const items = this.featured();
    return items.length > 0 ? items[this.activeSlide % items.length] : null;
  }

  nextSlide(): void {
    const items = this.featured();
    if (items.length === 0) return;
    this.activeSlide = (this.activeSlide + 1) % items.length;
  }

  prevSlide(): void {
    const items = this.featured();
    if (items.length === 0) return;
    this.activeSlide = (this.activeSlide - 1 + items.length) % items.length;
  }

  formatMoney(amount: number): string {
    if (amount >= 1000000) return (amount / 1000000).toFixed(1) + 'M';
    if (amount >= 1000) return (amount / 1000).toFixed(1) + 'K';
    return String(amount);
  }
}