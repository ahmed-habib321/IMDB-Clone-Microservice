import { Component, inject, input, OnChanges, OnInit, signal, SimpleChanges } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserList } from '../../models/list.model';
import { ListService } from '../../services/list.service';

@Component({
  selector: 'app-list-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './list-detail.component.html',
  styleUrls: ['./list-detail.component.css'],
})
export class ListDetailComponent implements OnInit, OnChanges {
  id = input.required<string>();

  private readonly listService = inject(ListService);

  readonly list = signal<UserList | null>(null);
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
    this.listService.getListById(id).subscribe((l) => {
      this.list.set(l);
      this.loading.set(false);
    });
  }
}
