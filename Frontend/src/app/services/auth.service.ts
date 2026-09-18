import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { AuthResponse, ChangePasswordRequest, ForgotPasswordRequest, LoginRequest, RegisterRequest, ResetPasswordRequest, User } from '../models/auth.model';
import { environment } from '../../environments/environment';

interface JwtClaims {
  sub: string;
  email: string;
  roles: string[];
  exp: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  readonly currentUser = signal<User | null>(null);
  readonly isAuthenticated = signal<boolean>(false);

  private readonly API_URL = environment.apiUrl;
  private readonly TOKEN_KEY = 'cinephile_access_token';
  private readonly REFRESH_KEY = 'cinephile_refresh_token';
  private readonly USER_KEY = 'cinephile_user';

  constructor() {
    const savedUser = localStorage.getItem(this.USER_KEY);
    const savedToken = localStorage.getItem(this.TOKEN_KEY);
    if (savedUser && savedToken) {
      try {
        this.currentUser.set(JSON.parse(savedUser));
        this.isAuthenticated.set(this.isTokenValid(savedToken));
      } catch {
        this.clearSession();
      }
    }
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<{ access: string; refresh: string }>(`${this.API_URL}/auth/login`, req)
      .pipe(
        tap((res) => this.setSession({ accessToken: res.access, refreshToken: res.refresh })),
        map((res) => ({ accessToken: res.access, refreshToken: res.refresh })),
      );
  }

  register(req: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/auth/register`, req);
  }

  triggerVerificationEmail(email: string): Observable<void> {
    return this.http.get<void>(`${this.API_URL}/auth/email-verification`, {
      params: { email },
    });
  }

  verifyEmail(otp: string): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/auth/email-verification`, { otp });
  }

  triggerPasswordForgot(req: ForgotPasswordRequest): Observable<void> {
    return this.http.get<void>(`${this.API_URL}/auth/password-forgot`, {
      params: { email: req.email },
    });
  }

  resetPassword(req: ResetPasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/auth/password-forgot`, req);
  }

  changePassword(req: ChangePasswordRequest): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/auth/password`, req);
  }

  logoutAll(): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/auth/logout-all`, null);
  }

  refreshSession(): Observable<boolean> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) return of(false);

    return this.http
      .post<{ access: string; refresh: string }>(
        `${this.API_URL}/auth/refresh`,
        null,
        { headers: { 'X-Refresh-Token': `Bearer ${refreshToken}` } },
      )
      .pipe(
        tap((res) => this.storeTokens({ accessToken: res.access, refreshToken: res.refresh })),
        map(() => true),
        catchError(() => {
          this.clearSession();
          return of(false);
        }),
      );
  }

  logout(): void {
    const token = this.getToken();
    if (token) {
      this.http
        .post<void>(`${this.API_URL}/auth/logout`, null)
        .pipe(catchError(() => of(null)))
        .subscribe();
    }
    this.clearSession();
  }

  setSession(res: AuthResponse): void {
    this.storeTokens(res);
    const claims = this.decodeToken(res.accessToken);
    const email = claims?.email ?? '';
    const user: User = {
      id: claims?.sub ?? '',
      email,
      username: this.usernameFromEmail(email),
      isVerified: true,
      roles: claims?.roles?.length ? claims.roles : ['USER'],
    };
    this.currentUser.set(user);
    this.isAuthenticated.set(true);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_KEY);
  }

  private storeTokens(res: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, res.accessToken);
    localStorage.setItem(this.REFRESH_KEY, res.refreshToken);
  }

  private clearSession(): void {
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_KEY);
  }

  private decodeToken(token: string): JwtClaims | null {
    try {
      const payload = token.split('.')[1];
      if (!payload) return null;
      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = normalized + '='.repeat((4 - (normalized.length % 4)) % 4);
      return JSON.parse(atob(padded)) as JwtClaims;
    } catch {
      return null;
    }
  }

  private isTokenValid(token: string): boolean {
    const claims = this.decodeToken(token);
    if (!claims?.exp) return false;
    return claims.exp * 1000 > Date.now();
  }

  private usernameFromEmail(email: string): string {
    return email.split('@')[0] || 'user';
  }
}