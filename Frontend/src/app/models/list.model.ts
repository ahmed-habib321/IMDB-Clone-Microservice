export interface Watchlist {
  id: string;
  userId: string;
  itemCount: number;
  items: WatchlistItem[];
  updatedAt: string;
}

export interface WatchlistItem {
  id: string;
  titleId: string;
  primaryTitle: string;
  posterUrl: string;
  addedAt: string;
  watched: boolean;
  watchedAt: string | null;
  notes: string;
}

export interface UserList {
  id: string;
  userId: string;
  name: string;
  description: string;
  isPublic: boolean;
  itemCount: number;
  items: UserListItem[];
  createdAt: string;
  updatedAt: string;
}

export interface UserListItem {
  id: string;
  titleId: string;
  primaryTitle: string;
  posterUrl: string;
  imdbRating: number;
  addedAt: string;
  notes: string;
}

export interface ListSummary {
  id: string;
  name: string;
  itemCount: number;
}
