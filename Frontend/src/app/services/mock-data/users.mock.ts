import { User } from '../../models/auth.model';
import { UserProfileResponse } from '../../models/user.model';
import { IMAGES } from './images.mock';

export const MOCK_USERS: User[] = [
  {
    id: 'u1',
    email: 'alice@cinephile.test',
    username: 'alice_films',
    isVerified: true,
    roles: ['USER'],
  },
  {
    id: 'u2',
    email: 'bob@cinephile.test',
    username: 'bob_reviews',
    isVerified: true,
    roles: ['USER'],
  },
  {
    id: 'u3',
    email: 'admin@cinephile.test',
    username: 'cine_admin',
    isVerified: true,
    roles: ['ADMIN'],
  },
];

export const MOCK_PROFILES: UserProfileResponse[] = [
  {
    user: {
      id: 'u1',
      username: 'alice_films',
      email: 'alice@cinephile.test',
      isVerified: true,
    },
    profile: {
      id: 'up1',
      userId: 'u1',
      displayName: 'Alice',
      avatarUrl: IMAGES.avatar1,
      bio: 'Sci-fi enthusiast and amateur critic. Always chasing the next great space opera.',
      country: 'Canada',
      birthDate: '1995-04-12',
      gender: 'Female',
      websiteUrl: 'https://alice.example.com',
      totalRatings: 187,
      totalReviews: 42,
      memberSince: '2021-03-15',
    },
    preferences: {
      favGenres: ['Sci-Fi', 'Drama', 'Thriller'],
      favLanguages: ['English'],
      adultContent: false,
      emailNotifs: true,
      publicWatchlist: true,
    },
  },
  {
    user: {
      id: 'u2',
      username: 'bob_reviews',
      email: 'bob@cinephile.test',
      isVerified: true,
    },
    profile: {
      id: 'up2',
      userId: 'u2',
      displayName: 'Bob',
      avatarUrl: IMAGES.avatar2,
      bio: 'Film noir scholar. I rate what I rewatch.',
      country: 'United States',
      birthDate: '1988-09-30',
      gender: 'Male',
      websiteUrl: '',
      totalRatings: 342,
      totalReviews: 89,
      memberSince: '2020-01-05',
    },
    preferences: {
      favGenres: ['Thriller', 'Crime', 'Drama'],
      favLanguages: ['English'],
      adultContent: false,
      emailNotifs: false,
      publicWatchlist: true,
    },
  },
  {
    user: {
      id: 'u3',
      username: 'cine_admin',
      email: 'admin@cinephile.test',
      isVerified: true,
    },
    profile: {
      id: 'up3',
      userId: 'u3',
      displayName: 'Cine Admin',
      avatarUrl: IMAGES.avatar3,
      bio: 'Running the show behind the scenes.',
      country: 'United Kingdom',
      birthDate: '1980-01-01',
      gender: 'Other',
      websiteUrl: '',
      totalRatings: 55,
      totalReviews: 10,
      memberSince: '2019-06-01',
    },
    preferences: {
      favGenres: ['All'],
      favLanguages: ['English', 'French'],
      adultContent: true,
      emailNotifs: true,
      publicWatchlist: false,
    },
  },
];
