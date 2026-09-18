import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { UserPreferences, UserProfile, UserProfileResponse } from '../models/user.model';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

interface ProfileBackend {
  displayName: string;
  avatarUrl: string;
  bio: string;
  country: string;
  birthDate: string;
  totalRatings: number;
  totalReviews: number;
}

interface PreferencesBackend {
  favGenres: string[];
  favLanguages: string[];
  adultContent: boolean;
  emailNotifs: boolean;
  publicWatchlist: boolean;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  private readonly API_URL = environment.apiUrl;

  getProfileByUserId(userId: string): Observable<UserProfileResponse | null> {
    return this.http.get<ProfileBackend>(`${this.API_URL}/user/${userId}/profile`).pipe(
      switchMap((profile) =>
        this.http.get<PreferencesBackend>(`${this.API_URL}/user/${userId}/preferences`).pipe(
          map((prefs) => this.composeResponse(userId, profile, prefs)),
        ),
      ),
    );
  }

  updateProfile(profile: UserProfileResponse): Observable<UserProfileResponse> {
    const userId = profile.user.id;
    const body = {
      displayName: profile.profile.displayName || undefined,
      bio: profile.profile.bio || undefined,
      country: profile.profile.country || undefined,
      birthDate: profile.profile.birthDate || undefined,
      gender: profile.profile.gender || undefined,
      websiteUrl: profile.profile.websiteUrl || undefined,
    };
    return this.http.put<void>(`${this.API_URL}/user/${userId}/profile`, body).pipe(
      map(() => ({
        ...profile,
        profile: { ...profile.profile, displayName: body.displayName ?? profile.profile.displayName },
      })),
    );
  }

  updatePreferences(
    userId: string,
    prefs: Partial<UserPreferences>,
  ): Observable<UserPreferences> {
    return this.http.put<void>(`${this.API_URL}/user/${userId}/preferences`, prefs).pipe(
      map(() => prefs as UserPreferences),
    );
  }

  private composeResponse(userId: string, p: ProfileBackend, prefs: PreferencesBackend): UserProfileResponse {
    const me = this.auth.currentUser();
    const isMe = me !== null && userId === me.id;

    const profile: UserProfile = {
      id: userId,
      userId,
      displayName: p?.displayName ?? '',
      avatarUrl: p?.avatarUrl ?? '',
      bio: p?.bio ?? '',
      country: p?.country ?? '',
      birthDate: p?.birthDate ?? '',
      gender: '',
      websiteUrl: '',
      totalRatings: p?.totalRatings ?? 0,
      totalReviews: p?.totalReviews ?? 0,
      memberSince: '',
    };

    const preferences: UserPreferences = {
      favGenres: prefs?.favGenres ?? [],
      favLanguages: prefs?.favLanguages ?? [],
      adultContent: prefs?.adultContent ?? false,
      emailNotifs: prefs?.emailNotifs ?? false,
      publicWatchlist: prefs?.publicWatchlist ?? true,
    };

    return {
      user: {
        id: userId,
        username: isMe ? me.username : '',
        email: isMe ? me.email : '',
        isVerified: isMe ? me.isVerified : true,
      },
      profile,
      preferences,
    };
  }
}