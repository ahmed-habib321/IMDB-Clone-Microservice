import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const RETRY_HEADER = 'X-Auth-Retry';
const SKIP_REFRESH_PATHS = ['/auth/login', '/auth/refresh', '/auth/logout'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  const token = auth.getToken();
  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      const canRefresh =
        err.status === 401 &&
        auth.getRefreshToken() &&
        !req.headers.has(RETRY_HEADER) &&
        !SKIP_REFRESH_PATHS.some((p) => req.url.includes(p));

      if (!canRefresh) return throwError(() => err);

      return auth.refreshSession().pipe(
        switchMap((ok) => {
          if (!ok) return throwError(() => err);
          const newToken = auth.getToken();
          const retryReq = req.clone({
            setHeaders: { Authorization: `Bearer ${newToken ?? ''}`, [RETRY_HEADER]: 'true' },
          });
          return next(retryReq);
        }),
        catchError(() => {
          auth.logout();
          return throwError(() => err as unknown);
        }),
      );
    }),
  );
};