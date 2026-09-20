import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { AdminService } from '../../services/admin.service';
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
  protected readonly auth = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly adminService = inject(AdminService);

  readonly profile = signal<UserProfileResponse | null>(null);
  readonly loading = signal(true);
  readonly editing = signal(false);
  readonly editorRequestBusy = signal(false);
  readonly editorRequestPending = signal(false);
  editorRequestMessage = '';
  editorRequestError = '';
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

  requestEditorRole(): void {
    if (this.editorRequestBusy()) return;
    this.editorRequestBusy.set(true);
    this.editorRequestMessage = '';
    this.editorRequestError = '';

    this.adminService.requestEditorRole().subscribe({
      next: () => {
        this.editorRequestPending.set(true);
        this.editorRequestMessage =
          'Request submitted! An admin will review your request and you will be notified.';
        this.editorRequestBusy.set(false);
      },
      error: (err) => {
        const body = (err as { error?: { message?: string } }).error;
        this.editorRequestError = body?.message ?? 'Failed to submit the request. Please try again.';
        this.editorRequestBusy.set(false);
      },
    });
  }
}
