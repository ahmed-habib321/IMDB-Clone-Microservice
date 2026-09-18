import { Component, inject } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  step: 'email' | 'reset' | 'done' = 'email';
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  resetForm = this.fb.group(
    {
      otp: ['', [Validators.required, Validators.minLength(6)]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: [ForgotPasswordComponent.passwordMatchValidator] },
  );

  static passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('newPassword')?.value;
    const confirm = control.get('confirmPassword')?.value;
    return password === confirm ? null : { mismatch: true };
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.errorMessage = 'Please enter a valid email address.';
      return;
    }

    this.errorMessage = '';
    this.isSubmitting = true;

    const email = this.form.value.email ?? '';

    try {
      await firstValueFrom(this.auth.triggerPasswordForgot({ email }));
      this.step = 'reset';
      this.errorMessage = '';
    } catch {
      this.errorMessage =
        'Could not send a reset code. If this account exists, you can try again shortly.';
    } finally {
      this.isSubmitting = false;
    }
  }

  async resetPassword(): Promise<void> {
    if (this.resetForm.invalid) {
      this.errorMessage = 'Please check the code and your new password.';
      return;
    }

    this.errorMessage = '';
    this.isSubmitting = true;

    const { otp, newPassword } = this.resetForm.value;

    try {
      await firstValueFrom(
        this.auth.resetPassword({ otp: otp ?? '', newPassword: newPassword ?? '' }),
      );
      this.step = 'done';
      this.successMessage = 'Your password has been reset. You can now sign in.';
    } catch {
      this.errorMessage =
        'The reset code is invalid or expired. Please request a new one.';
    } finally {
      this.isSubmitting = false;
    }
  }

  backToEmailStep(): void {
    this.step = 'email';
    this.errorMessage = '';
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}