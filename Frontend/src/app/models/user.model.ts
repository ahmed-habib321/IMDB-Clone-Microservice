export interface UserProfile {
  id: string;
  userId: string;
  displayName: string;
  avatarUrl: string;
  bio: string;
  country: string;
  birthDate: string;
  gender: string;
  websiteUrl: string;
  totalRatings: number;
  totalReviews: number;
  memberSince: string;
}

export interface UserPreferences {
  favGenres: string[];
  favLanguages: string[];
  adultContent: boolean;
  emailNotifs: boolean;
  publicWatchlist: boolean;
}

export interface UserProfileResponse {
  user: {
    id: string;
    username: string;
    email: string;
    isVerified: boolean;
  };
  profile: UserProfile;
  preferences: UserPreferences;
}
