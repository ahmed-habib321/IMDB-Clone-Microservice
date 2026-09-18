import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  showPassword = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  form = this.fb.group({
    email: ['', [Validators.required]],
    password: ['', [Validators.required]],
    keepSignedIn: [false],
  });

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.errorMessage = 'Please fill in both fields.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.isSubmitting = true;

    const { email, password } = this.form.value;

    try {
      await firstValueFrom(
        this.auth.login({ email: email ?? '', password: password ?? '' }),
      );
      await this.router.navigate(['/']);
    } catch (err) {
      const status = (err as { status?: number }).status;
      if (status === 401) {
        this.errorMessage = 'Your account is not verified yet. Please verify your email first.';
      } else {
        this.errorMessage = 'Invalid email or password. Please try again.';
      }
    } finally {
      this.isSubmitting = false;
    }
  }

  oauthLogin(): void {
    console.log('OAuth login would be triggered here.');
  }
}