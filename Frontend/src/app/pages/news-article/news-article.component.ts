import { Component, inject, input, OnChanges, OnInit, signal, SimpleChanges } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { NewsArticle } from '../../models/news.model';
import { NewsService } from '../../services/news.service';

@Component({
  selector: 'app-news-article',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './news-article.component.html',
  styleUrls: ['./news-article.component.css'],
})
export class NewsArticleComponent implements OnInit, OnChanges {
  slug = input.required<string>();

  private readonly newsService = inject(NewsService);

  readonly article = signal<NewsArticle | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.load();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['slug'] && !changes['slug'].firstChange) {
      this.load();
    }
  }

  private load(): void {
    const slug = this.slug();
    if (!slug) return;
    this.loading.set(true);
    this.newsService.getArticleBySlug(slug).subscribe((a) => {
      this.article.set(a);
      this.loading.set(false);
    });
  }

  paragraphs(body: string): string[] {
    return body.split('\n').filter((p) => p.trim().length > 0);
  }
}
