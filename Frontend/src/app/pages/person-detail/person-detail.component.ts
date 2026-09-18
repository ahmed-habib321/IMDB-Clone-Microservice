import { Component, inject, input, OnChanges, OnInit, signal, SimpleChanges } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { PersonDetail } from '../../models/person.model';
import { PersonService } from '../../services/person.service';

@Component({
  selector: 'app-person-detail',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './person-detail.component.html',
  styleUrls: ['./person-detail.component.css'],
})
export class PersonDetailComponent implements OnInit, OnChanges {
  id = input.required<string>();

  private readonly personService = inject(PersonService);

  readonly person = signal<PersonDetail | null>(null);
  readonly loading = signal(true);

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
    this.personService.getPersonById(id).subscribe((p) => {
      this.person.set(p);
      this.loading.set(false);
    });
  }
}
