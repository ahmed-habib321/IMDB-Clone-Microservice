import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ListService } from '../../services/list.service';
import { UserList } from '../../models/list.model';

@Component({
  selector: 'app-my-lists',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './my-lists.component.html',
  styleUrls: ['./my-lists.component.css'],
})
export class MyListsComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly listService = inject(ListService);
  private readonly router = inject(Router);

  readonly lists = signal<UserList[]>([]);
  readonly loading = signal(true);

  showCreateModal = false;
  newName = '';
  newDescription = '';
  newIsPublic = true;
  createError = '';

  ngOnInit(): void {
    this.listService.getLists().subscribe((l) => {
      this.lists.set(l);
      this.loading.set(false);
    });
  }

  openCreate(): void {
    this.showCreateModal = true;
    this.newName = '';
    this.newDescription = '';
    this.newIsPublic = true;
    this.createError = '';
  }

  closeCreate(): void {
    this.showCreateModal = false;
  }

  createList(): void {
    if (!this.newName.trim()) {
      this.createError = 'Please enter a list name.';
      return;
    }
    this.listService
      .createList(this.newName.trim(), this.newDescription.trim(), this.newIsPublic)
      .subscribe((created) => {
        this.lists.update((current) => [created, ...current]);
        this.closeCreate();
      });
  }

  deleteList(id: string): void {
    this.listService.deleteList(id).subscribe(() => {
      this.lists.update((current) => current.filter((l) => l.id !== id));
    });
  }

  goToWatchlist(): void {
    this.router.navigate(['/watchlist']);
  }
}
