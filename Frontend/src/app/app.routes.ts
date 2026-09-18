import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./pages/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'title/:id',
    loadComponent: () =>
      import('./pages/title-detail/title-detail.component').then((m) => m.TitleDetailComponent),
  },
  {
    path: 'people/:id',
    loadComponent: () =>
      import('./pages/person-detail/person-detail.component').then((m) => m.PersonDetailComponent),
  },
  {
    path: 'news',
    loadComponent: () =>
      import('./pages/news-feed/news-feed.component').then((m) => m.NewsFeedComponent),
  },
  {
    path: 'news/:slug',
    loadComponent: () =>
      import('./pages/news-article/news-article.component').then((m) => m.NewsArticleComponent),
  },
  {
    path: 'awards',
    loadComponent: () =>
      import('./pages/awards/awards.component').then((m) => m.AwardsComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'signup',
    loadComponent: () =>
      import('./pages/signup/signup.component').then((m) => m.SignupComponent),
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./pages/forgot-password/forgot-password.component').then((m) => m.ForgotPasswordComponent),
  },
  {
    path: 'verify-email',
    loadComponent: () =>
      import('./pages/verify-email/verify-email.component').then((m) => m.VerifyEmailComponent),
  },
  {
    path: 'profile/:id',
    loadComponent: () =>
      import('./pages/profile/profile.component').then((m) => m.ProfileComponent),
  },
  {
    path: 'me',
    loadComponent: () =>
      import('./pages/my-profile/my-profile.component').then((m) => m.MyProfileComponent),
    canActivate: [authGuard],
  },
  {
    path: 'watchlist',
    loadComponent: () =>
      import('./pages/watchlist/watchlist.component').then((m) => m.WatchlistComponent),
    canActivate: [authGuard],
  },
  {
    path: 'lists',
    loadComponent: () =>
      import('./pages/my-lists/my-lists.component').then((m) => m.MyListsComponent),
    canActivate: [authGuard],
  },
  {
    path: 'lists/:id',
    loadComponent: () =>
      import('./pages/list-detail/list-detail.component').then((m) => m.ListDetailComponent),
    canActivate: [authGuard],
  },
  {
    path: 'search',
    loadComponent: () =>
      import('./pages/search-results/search-results.component').then((m) => m.SearchResultsComponent),
  },
  {
    path: 'explore',
    loadComponent: () =>
      import('./pages/explore/explore.component').then((m) => m.ExploreComponent),
  },
  {
    path: '**',
    loadComponent: () =>
      import('./pages/not-found/not-found.component').then((m) => m.NotFoundComponent),
  },
];
