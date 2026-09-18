export type TitleType = 'MOVIE' | 'TV_SERIES' | 'TV_MINI' | 'SHORT';
export type TitleStatus = 'RELEASED' | 'IN_PRODUCTION' | 'ANNOUNCED' | 'CANCELLED';

export interface Title {
  id: string;
  titleType: TitleType;
  primaryTitle: string;
  originalTitle: string;
  slug: string;
  tagline: string;
  overview: string;
  posterUrl: string;
  backdropUrl: string;
  status: TitleStatus;
  releaseDate: string;
  runtimeMins: number;
  budget: number;
  revenue: number;
  imdbRating: number;
  voteCount: number;
  popularity: number;
  adult: boolean;
  genres: string[];
  languages: string[];
  countries: string[];
}

export interface TitleCard {
  id: string;
  titleType: TitleType;
  primaryTitle: string;
  slug: string;
  overview: string;
  posterUrl: string;
  backdropUrl: string;
  status: TitleStatus;
  releaseDate: string;
  imdbRating: number;
  voteCount: number;
  popularity: number;
  genres: string[];
}

export interface BoxOfficeEntry {
  id: string;
  primaryTitle: string;
  posterUrl: string;
  boxOffice: Pick<BoxOffice, 'worldwide'>;
}

export interface Movie extends Title {
  boxOffice: BoxOffice;
  trailers: Trailer[];
}

export interface TvShow extends Title {
  network: string;
  creatorId: string;
  totalSeasons: number;
  totalEpisodes: number;
  episodeRuntime: number;
  isOnGoing: boolean;
  seasons: Season[];
}

export interface Season {
  id: string;
  seasonNumber: number;
  title: string;
  overview: string;
  posterUrl: string;
  airDate: string;
  episodes: Episode[];
}

export interface Episode {
  id: string;
  episodeNumber: number;
  title: string;
  overview: string;
  stillUrl: string;
  airDate: string;
  runtimeMins: number;
  imdbRating: number;
  voteCount: number;
}

export interface CastMember {
  personId: string;
  name: string;
  slug: string;
  characterName: string;
  profileUrl: string;
  billingOrder: number;
  isVoice: boolean;
  episodeCount: number;
}

export interface CrewMember {
  personId: string;
  name: string;
  slug: string;
  department: string;
  job: string;
  profileUrl: string;
}

export interface BoxOffice {
  budget: number;
  openingWeekend: number;
  domestic: number;
  international: number;
  worldwide: number;
  currency: string;
}

export interface Trailer {
  id: string;
  name: string;
  trailerType: string;
  youtubeKey: string;
  durationSecs: number;
  language: string;
  publishedAt: string;
}
