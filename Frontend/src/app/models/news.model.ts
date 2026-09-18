export interface NewsArticle {
  id: string;
  authorUsername: string;
  title: string;
  slug: string;
  body: string;
  viewCount: number;
  publishedAt: string;
  taggedTitles: TaggedTitle[];
  taggedPeople: TaggedPerson[];
}

export interface TaggedTitle {
  titleId: string;
  primaryTitle: string;
  posterUrl: string;
}

export interface TaggedPerson {
  personId: string;
  name: string;
  profileUrl: string;
}
