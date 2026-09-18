import { Component, inject, OnInit, signal } from '@angular/core';
import { NewsService } from '../../services/news.service';
import { NewsArticle } from '../../models/news.model';
import { ArticleCardComponent } from '../../components/article-card/article-card.component';

@Component({
  selector: 'app-news-feed',
  standalone: true,
  imports: [ArticleCardComponent],
  templateUrl: './news-feed.component.html',
  styleUrls: ['./news-feed.component.css'],
})
export class NewsFeedComponent implements OnInit {
  private readonly newsService = inject(NewsService);

  readonly articles = signal<NewsArticle[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.newsService.getNews().subscribe((a) => {
      this.articles.set(a);
      this.loading.set(false);
    });
  }
}
