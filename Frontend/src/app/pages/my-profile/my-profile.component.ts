import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { UserProfileResponse } from '../../models/user.model';

@Component({
  selector: 'app-my-profile',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css'],
})
export class MyProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly userService = inject(UserService);

  readonly profile = signal<UserProfileResponse | null>(null);
  readonly loading = signal(true);
  readonly editing = signal(false);
  savedMessage = '';
  saveError = '';

  form = this.fb.group({
    displayName: [''],
    bio: [''],
    country: [''],
    websiteUrl: [''],
  });

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (!user) return;
    this.userService.getProfileByUserId(user.id).subscribe((p) => {
      this.profile.set(p);
      this.loading.set(false);
      if (p) {
        this.form.patchValue({
          displayName: p.profile.displayName,
          bio: p.profile.bio,
          country: p.profile.country,
          websiteUrl: p.profile.websiteUrl,
        });
      }
    });
  }

  toggleEdit(): void {
    this.editing.set(!this.editing());
    this.saveError = '';
  }

  save(): void {
    const current = this.profile();
    if (!current) return;

    const updated: UserProfileResponse = {
      ...current,
      profile: {
        ...current.profile,
        displayName: this.form.value.displayName ?? current.profile.displayName,
        bio: this.form.value.bio ?? current.profile.bio,
        country: this.form.value.country ?? current.profile.country,
        websiteUrl: this.form.value.websiteUrl ?? current.profile.websiteUrl,
      },
    };

    this.userService.updateProfile(updated).subscribe({
      next: (saved) => {
        this.profile.set(saved);
        this.editing.set(false);
        this.savedMessage = 'Profile updated successfully!';
        setTimeout(() => (this.savedMessage = ''), 3000);
      },
      error: () => {
        this.saveError = 'Failed to save profile. Please try again.';
      },
    });
  }
}
