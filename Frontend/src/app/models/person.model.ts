export interface Person {
  id: string;
  name: string;
  slug: string;
  alsoKnownAs: string[];
  biography: string;
  profileUrl: string;
  birthDate: string;
  deathDate: string | null;
  birthPlace: string;
  gender: string;
  heightCm: number;
  popularity: number;
  imdbId: string;
}

export interface PersonDetail extends Person {
  filmography: FilmographyItem[];
}

export interface FilmographyItem {
  titleId: string;
  primaryTitle: string;
  posterUrl: string;
  titleType: string;
  characterName: string;
  department: string;
  job: string;
  releaseDate: string;
}
