import { Component, inject } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  showPassword = false;
  isSubmitting = false;
  errorMessage = '';

  strengthScore = 0;
  strengthText = 'Strength: Empty';
  strengthColor = 'text-on-surface-variant';

  form = this.fb.group(
    {
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: [SignupComponent.passwordMatchValidator] },
  );

  static passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirm = control.get('confirmPassword')?.value;
    return password === confirm ? null : { mismatch: true };
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onPasswordInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    let score = 0;

    if (value.length > 0) score = 1;
    if (value.length > 5) score = 2;
    if (value.length > 8 && /[0-9]/.test(value)) score = 3;
    if (value.length > 10 && /[A-Z]/.test(value) && /[^A-Za-z0-9]/.test(value)) score = 4;

    this.strengthScore = score;

    switch (score) {
      case 0:
        this.strengthText = 'Strength: Empty';
        this.strengthColor = 'text-on-surface-variant';
        break;
      case 1:
        this.strengthText = 'Strength: Weak';
        this.strengthColor = 'text-error';
        break;
      case 2:
        this.strengthText = 'Strength: Fair';
        this.strengthColor = 'text-orange-400';
        break;
      case 3:
        this.strengthText = 'Strength: Good';
        this.strengthColor = 'text-primary';
        break;
      case 4:
        this.strengthText = 'Strength: Strong';
        this.strengthColor = 'text-emerald-400';
        break;
    }
  }

  getBarClass(index: number): string {
    if (this.strengthScore === 0) return 'bg-surface-container-highest';
    if (this.strengthScore === 1) return index === 0 ? 'bg-error' : 'bg-surface-container-highest';
    if (this.strengthScore === 2) return index < 2 ? 'bg-orange-400' : 'bg-surface-container-highest';
    if (this.strengthScore === 3) return index < 3 ? 'bg-primary' : 'bg-surface-container-highest';
    return index < 4 ? 'bg-emerald-400' : 'bg-surface-container-highest';
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.errorMessage = 'Please check your details and try again.';
      return;
    }

    this.errorMessage = '';
    this.isSubmitting = true;

    const { username, email, password } = this.form.value;

    try {
      await firstValueFrom(
        this.auth.register({ email: email ?? '', username: username ?? '', password: password ?? '' }),
      );
      await this.router.navigate(['/verify-email'], { queryParams: { email } });
    } catch (err) {
      const body = (err as { error?: { message?: string } }).error;
      this.errorMessage =
        body?.message ?? 'Registration failed. An account with this email or username may already exist.';
    } finally {
      this.isSubmitting = false;
    }
  }

  oauthSignup(): void {
    console.log('OAuth signup would be triggered here.');
  }
}