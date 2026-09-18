import { Component, inject, input, OnChanges, OnInit, signal, SimpleChanges } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { UserProfileResponse } from '../../models/user.model';
import { Review, UserRatingResponse } from '../../models/review.model';
import { UserList } from '../../models/list.model';
import { UserService } from '../../services/user.service';
import { ReviewService } from '../../services/review.service';
import { ReviewCardComponent } from '../../components/review-card/review-card.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterLink, DatePipe, ReviewCardComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit, OnChanges {
  id = input.required<string>();

  private readonly userService = inject(UserService);
  private readonly reviewService = inject(ReviewService);

  readonly profile = signal<UserProfileResponse | null>(null);
  readonly ratings = signal<UserRatingResponse[]>([]);
  readonly lists = signal<UserList[]>([]);
  readonly reviews = signal<Review[]>([]);
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
    this.userService.getProfileByUserId(id).subscribe((p) => {
      this.profile.set(p);
      this.loading.set(false);
    });
    this.reviewService.getRatingsByUser(id).subscribe((r) => this.ratings.set(r));
    this.reviewService.getReviewsByUser(id).subscribe((r) => this.reviews.set(r));
  }
}
