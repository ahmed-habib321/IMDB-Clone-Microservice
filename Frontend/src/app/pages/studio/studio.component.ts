import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface StudioCard {
  title: string;
  description: string;
  icon: string;
  link: string;
  adminOnly?: boolean;
}

@Component({
  selector: 'app-studio',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './studio.component.html',
})
export class StudioComponent {
  protected readonly auth = inject(AuthService);

  readonly cards: StudioCard[] = [
    {
      title: 'Add Title',
      description: 'Create a new Movie or TV Show entry in the catalog.',
      icon: 'movie',
      link: '/studio/titles/new',
    },
    {
      title: 'Write News',
      description: 'Publish a news article for the community.',
      icon: 'newspaper',
      link: '/studio/news/new',
    },
    {
      title: 'Editor Requests',
      description: 'Review and approve users requesting the editor role.',
      icon: 'verified_user',
      link: '/admin/editor-requests',
      adminOnly: true,
    },
    {
      title: 'Monitoring',
      description: 'Grafana dashboards for the whole platform.',
      icon: 'monitoring',
      link: '/admin/monitoring',
      adminOnly: true,
    },
  ];
}