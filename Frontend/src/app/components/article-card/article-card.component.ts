import { DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NewsArticle } from '../../models/news.model';

@Component({
  selector: 'app-article-card',
  standalone: true,
  imports: [RouterLink, DatePipe],
  template: `
    <article class="group bg-surface-container-low border border-outline-variant/30 rounded-xl overflow-hidden hover:border-primary/50 transition-colors cursor-pointer">
      <a [routerLink]="['/news', article().slug]" class="block">
        <div class="h-2 bg-gradient-to-r from-primary via-primary-container to-transparent"></div>
        <div class="p-stack-md">
          <div class="flex items-center gap-stack-sm mb-stack-sm">
            <span class="text-primary font-label-caps text-label-caps">{{ article().authorUsername }}</span>
            <span class="text-on-surface-variant font-label-caps text-label-caps">{{ article().publishedAt | date: 'mediumDate' }}</span>
          </div>
          <h3 class="font-headline-md text-headline-md font-bold text-on-surface group-hover:text-primary transition-colors leading-tight mb-stack-sm">
            {{ article().title }}
          </h3>
          <p class="text-body-sm text-on-surface-variant line-clamp-3 mb-stack-md">
            {{ truncatedBody(article().body) }}
          </p>
          <div class="flex items-center justify-between">
            <div class="flex gap-2">
              @for (t of article().taggedTitles.slice(0, 2); track t.titleId) {
                <span class="bg-surface-container-high text-on-surface-variant px-2 py-0.5 rounded text-label-caps text-label-caps">
                  {{ t.primaryTitle }}
                </span>
              }
            </div>
            <span class="text-on-surface-variant font-label-caps text-label-caps flex items-center gap-1">
              <span class="material-symbols-outlined text-base">visibility</span>
              {{ article().viewCount.toLocaleString() }}
            </span>
          </div>
        </div>
      </a>
    </article>
  `,
})
export class ArticleCardComponent {
  article = input.required<NewsArticle>();

  truncatedBody(body: string): string {
    return body.length > 140 ? body.substring(0, 140) + '…' : body;
  }
}
