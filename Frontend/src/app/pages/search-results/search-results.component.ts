import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TitleCard } from '../../models/title.model';
import { Person } from '../../models/person.model';
import { SearchService } from '../../services/search.service';
import { MovieCardComponent } from '../../components/movie-card/movie-card.component';
import { PersonCardComponent } from '../../components/person-card/person-card.component';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [RouterLink, MovieCardComponent, PersonCardComponent],
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.css'],
})
export class SearchResultsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly searchService = inject(SearchService);

  readonly query = signal('');
  readonly titles = signal<TitleCard[]>([]);
  readonly people = signal<Person[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const q = params.get('q') ?? '';
      this.query.set(q);
      this.fetch(q);
    });
  }

  private fetch(q: string): void {
    this.loading.set(true);
    this.searchService.searchTitles(q).subscribe((t) => {
      this.titles.set(t);
    });
    this.searchService.searchPeople(q).subscribe((p) => {
      this.people.set(p);
      this.loading.set(false);
    });
  }
}
