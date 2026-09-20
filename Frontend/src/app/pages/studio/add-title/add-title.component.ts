import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  CreateMovieRequest,
  CreateTvShowRequest,
  TitleService,
} from '../../../services/title.service';

type TitleKind = 'movie' | 'show';

@Component({
  selector: 'app-add-title',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './add-title.component.html',
})
export class AddTitleComponent {
  private readonly fb = inject(FormBuilder);
  private readonly titleService = inject(TitleService);

  readonly kind = signal<TitleKind>('movie');
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  readonly statuses = ['RELEASED', 'IN_PRODUCTION', 'ANNOUNCED', 'CANCELLED'];
  readonly movieTypes = ['MOVIE', 'SHORT'];
  readonly showTypes = ['TV_SERIES', 'TV_MINI'];

  form: FormGroup = this.buildForm('movie');

  switchKind(kind: TitleKind): void {
    if (this.kind() === kind) return;
    this.kind.set(kind);
    this.form = this.buildForm(kind);
    this.errorMessage = '';
    this.successMessage = '';
  }

  private baseControls(): Record<string, unknown> {
    return {
      primaryTitle: ['', Validators.required],
      originalTitle: [''],
      tagline: [''],
      overview: [''],
      posterUrl: [''],
      backdropUrl: [''],
      status: ['RELEASED'],
      runtimeMins: [''],
      budget: [''],
      revenue: [''],
      adult: [false],
      releaseDate: [''],
      genres: [''],
      languages: [''],
      countries: [''],
      titleType: ['MOVIE'],
    };
  }

  private buildForm(kind: TitleKind): FormGroup {
    const base = this.baseControls();
    if (kind === 'show') {
      return this.fb.group({
        ...base,
        titleType: ['TV_SERIES'],
        network: [''],
        totalSeasons: [''],
        totalEpisodes: [''],
        episodeRuntime: [''],
        isOnGoing: [false],
        finishedAt: [''],
      });
    }
    return this.fb.group(base);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.isSubmitting = true;

    const payload = this.buildPayload();

    const call =
      this.kind() === 'movie'
        ? this.titleService.createMovie(payload as unknown as CreateMovieRequest)
        : this.titleService.createShow(payload as unknown as CreateTvShowRequest);

    call.subscribe({
      next: () => {
        this.successMessage =
          this.kind() === 'movie' ? 'Movie created successfully!' : 'TV Show created successfully!';
        this.isSubmitting = false;
      },
      error: (err) => {
        const body = (err as { error?: { message?: string } }).error;
        this.errorMessage = body?.message ?? 'Failed to create title. Please try again.';
        this.isSubmitting = false;
      },
    });
  }

  private buildPayload(): Record<string, unknown> {
    const raw = this.form.getRawValue();

    const toNumber = (val: unknown): number | undefined =>
      val === '' || val === null || val === undefined ? undefined : Number(val);

    const splitList = (val: unknown): string[] | undefined => {
      if (typeof val !== 'string' || val.trim() === '') return undefined;
      return val
        .split(',')
        .map((item) => item.trim())
        .filter((item) => item.length > 0);
    };

    const payload: Record<string, unknown> = {
      ...raw,
      runtimeMins: toNumber(raw.runtimeMins),
      budget: toNumber(raw.budget),
      revenue: toNumber(raw.revenue),
      genres: splitList(raw.genres),
      languages: splitList(raw.languages),
      countries: splitList(raw.countries),
    };

    if (this.kind() === 'show') {
      payload['totalSeasons'] = toNumber(raw.totalSeasons);
      payload['totalEpisodes'] = toNumber(raw.totalEpisodes);
      payload['episodeRuntime'] = toNumber(raw.episodeRuntime);
    }

    const clean: Record<string, unknown> = {};
    for (const [key, value] of Object.entries(payload)) {
      if (value === '' || value === null || value === undefined) continue;
      if (Array.isArray(value) && value.length === 0) continue;
      clean[key] = value;
    }
    return clean;
  }
}