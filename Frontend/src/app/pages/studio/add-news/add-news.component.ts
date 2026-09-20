import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NewsService } from '../../../services/news.service';

@Component({
  selector: 'app-add-news',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './add-news.component.html',
})
export class AddNewsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly newsService = inject(NewsService);

  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  createdLink: string | null = null;

  readonly bodyMaxLength = 255;

  form = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(150)]],
    body: ['', [Validators.required, Validators.maxLength(this.bodyMaxLength)]],
    authorUsername: ['', [Validators.required]],
    taggedTitleIds: [''],
    taggedPersonIds: [''],
  });

  ngOnInit(): void {
    const username = this.auth.currentUser()?.username;
    if (username) {
      this.form.patchValue({ authorUsername: username });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.errorMessage = 'Please fill in all required fields and keep the body under 255 characters.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.createdLink = null;
    this.isSubmitting = true;

    const raw = this.form.getRawValue();
    const splitIds = (val: string | null): string[] | undefined => {
      if (!val || val.trim() === '') return undefined;
      const ids = val
        .split(',')
        .map((item) => item.trim())
        .filter((item) => item.length > 0);
      return ids.length > 0 ? ids : undefined;
    };

    const payload = {
      title: raw.title ?? '',
      body: raw.body ?? '',
      authorUsername: raw.authorUsername ?? '',
      taggedTitleIds: splitIds(raw.taggedTitleIds),
      taggedPersonIds: splitIds(raw.taggedPersonIds),
    };

    this.newsService.createNews(payload).subscribe({
      next: (created) => {
        this.successMessage = 'News article created successfully!';
        this.createdLink = created?.slug ? `/news/${created.slug}` : null;
        this.isSubmitting = false;
      },
      error: (err) => {
        const body = (err as { error?: { message?: string } }).error;
        this.errorMessage = body?.message ?? 'Failed to create news article. Please try again.';
        this.isSubmitting = false;
      },
    });
  }
}