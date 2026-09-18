import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TitleCard } from '../../models/title.model';
import { SearchService } from '../../services/search.service';
import { MovieCardComponent } from '../../components/movie-card/movie-card.component';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [FormsModule, MovieCardComponent],
  templateUrl: './explore.component.html',
  styleUrls: ['./explore.component.css'],
})
export class ExploreComponent implements OnInit {
  private readonly searchService = inject(SearchService);

  readonly genres = signal<string[]>([]);
  readonly results = signal<TitleCard[]>([]);
  readonly loading = signal(true);

  searchQuery = '';
  selectedGenre = '';
  titleType = 'ALL';
  minRating = 0;
  yearFrom = 0;

  readonly ratingOptions = [0, 5, 6, 7, 8, 9];

  ngOnInit(): void {
    this.searchService.getAllGenres().subscribe((g) => this.genres.set(g));
    this.fetch();
  }

  fetch(): void {
    this.loading.set(true);
    this.searchService
      .searchTitles(this.searchQuery, {
        genre: this.selectedGenre || undefined,
        minRating: this.minRating || undefined,
        titleType: this.titleType,
        yearFrom: this.yearFrom || undefined,
      })
      .subscribe((r) => {
        this.results.set(r);
        this.loading.set(false);
      });
  }

  applyFilters(): void {
    this.fetch();
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.selectedGenre = '';
    this.titleType = 'ALL';
    this.minRating = 0;
    this.yearFrom = 0;
    this.fetch();
  }
}
