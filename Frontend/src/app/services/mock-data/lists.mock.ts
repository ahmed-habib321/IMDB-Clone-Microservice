import { UserList, Watchlist } from '../../models/list.model';
import { IMAGES } from './images.mock';

export const MOCK_WATCHLIST: Watchlist = {
  id: 'w1',
  userId: 'u1',
  itemCount: 3,
  items: [
    {
      id: 'wi1',
      titleId: 't1',
      primaryTitle: 'Void Voyager',
      posterUrl: IMAGES.poster2,
      addedAt: '2023-06-02T10:00:00Z',
      watched: true,
      watchedAt: '2023-06-05T20:00:00Z',
      notes: 'Rewatched twice. The finale never gets old.',
    },
    {
      id: 'wi2',
      titleId: 't2',
      primaryTitle: 'Nightfall Protocol',
      posterUrl: IMAGES.poster1,
      addedAt: '2024-04-03T09:30:00Z',
      watched: false,
      watchedAt: null,
      notes: 'Save for a rainy weekend.',
    },
    {
      id: 'wi3',
      titleId: 's1',
      primaryTitle: 'The Neon Divide',
      posterUrl: IMAGES.poster2,
      addedAt: '2024-07-18T15:00:00Z',
      watched: false,
      watchedAt: null,
      notes: '',
    },
  ],
  updatedAt: '2024-08-01T12:00:00Z',
};

export const MOCK_LISTS: UserList[] = [
  {
    id: 'l1',
    userId: 'u1',
    name: 'Greatest Sci-Fi of All Time',
    description: 'My personal ranking of the best science fiction films ever made.',
    isPublic: true,
    itemCount: 2,
    items: [
      {
        id: 'li1',
        titleId: 't1',
        primaryTitle: 'Void Voyager',
        posterUrl: IMAGES.poster2,
        imdbRating: 9.1,
        addedAt: '2023-07-01T10:00:00Z',
        notes: '#1 - A flawless masterpiece.',
      },
      {
        id: 'li2',
        titleId: 't7',
        primaryTitle: 'The Silent North',
        posterUrl: IMAGES.fan1,
        imdbRating: 9.4,
        addedAt: '2023-07-02T10:00:00Z',
        notes: 'Not quite sci-fi, but the atmosphere belongs here.',
      },
    ],
    createdAt: '2023-07-01T09:00:00Z',
    updatedAt: '2024-01-10T10:00:00Z',
  },
  {
    id: 'l2',
    userId: 'u1',
    name: 'Weekend Watchlist',
    description: 'Quick picks for relaxed viewing.',
    isPublic: true,
    itemCount: 1,
    items: [
      {
        id: 'li3',
        titleId: 't4',
        primaryTitle: 'Robot in the Reed',
        posterUrl: IMAGES.poster4,
        imdbRating: 8.2,
        addedAt: '2024-05-03T10:00:00Z',
        notes: 'Great family film.',
      },
    ],
    createdAt: '2024-05-01T09:00:00Z',
    updatedAt: '2024-05-03T10:00:00Z',
  },
  {
    id: 'l3',
    userId: 'u2',
    name: 'Noir Classics',
    description: 'The essential detective thrillers everyone should see.',
    isPublic: true,
    itemCount: 1,
    items: [
      {
        id: 'li4',
        titleId: 't2',
        primaryTitle: 'Nightfall Protocol',
        posterUrl: IMAGES.poster1,
        imdbRating: 8.4,
        addedAt: '2024-04-05T10:00:00Z',
        notes: 'Modern noir at its finest.',
      },
    ],
    createdAt: '2024-04-05T09:00:00Z',
    updatedAt: '2024-04-05T10:00:00Z',
  },
];

export const MOCK_LIST_SUMMARIES = MOCK_LISTS.map((l) => ({
  id: l.id,
  name: l.name,
  itemCount: l.itemCount,
}));
