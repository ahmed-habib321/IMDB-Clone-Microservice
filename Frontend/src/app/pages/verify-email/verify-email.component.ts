import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css'],
})
export class VerifyEmailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);

  status: 'pending' | 'success' | 'error' = 'pending';
  message = '';
  errorMessage = '';
  isSubmitting = false;
  otp = '';

  ngOnInit(): void {
    if (!this.email && !this.auth.isAuthenticated()) {
      this.status = 'error';
    }
  }

  get email(): string {
    return (
      (this.route.snapshot.queryParamMap.get('email') as string | null) ??
      this.auth.currentUser()?.email ??
      ''
    );
  }

  async verify(): Promise<void> {
    if (this.otp.length < 6) {
      this.errorMessage = 'Please enter the 6-digit code from your email.';
      return;
    }
    this.errorMessage = '';
    this.isSubmitting = true;
    try {
      await firstValueFrom(this.auth.verifyEmail(this.otp.trim()));
      const user = this.auth.currentUser();
      if (user) {
        this.auth.currentUser.set({ ...user, isVerified: true });
        localStorage.setItem('cinephile_user', JSON.stringify({ ...user, isVerified: true }));
      }
      this.status = 'success';
    } catch {
      this.status = 'error';
    } finally {
      this.isSubmitting = false;
    }
  }

  async resendVerification(): Promise<void> {
    if (!this.email) return;
    this.message = '';
    this.errorMessage = '';
    this.isSubmitting = true;
    try {
      await firstValueFrom(this.auth.triggerVerificationEmail(this.email));
      this.message = 'A new verification code has been sent. Please check your inbox.';
      this.status = 'pending';
    } catch {
      this.errorMessage = 'Could not send the code right now. Please try again later.';
    } finally {
      this.isSubmitting = false;
    }
  }

  goHome(): void {
    this.router.navigate(['/']);
  }
}