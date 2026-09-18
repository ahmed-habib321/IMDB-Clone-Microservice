export interface Rating {
  userId: string;
  titleId: string;
  score: number;
  ratedAt: string;
  updatedAt: string;
}

export interface RatingResponse {
  averageRating: number;
  totalVotes: number;
  userScore: number | null;
}

export interface RatingDistribution {
  distribution: { score: number; count: number }[];
  averageRating: number;
  totalVotes: number;
}

export interface Review {
  id: string;
  userId: string;
  username: string;
  titleId: string;
  reviewTitle: string;
  body: string;
  containsSpoiler: boolean;
  isApproved: boolean;
  helpfulYes: number;
  helpfulNo: number;
  createdAt: string;
  updatedAt: string;
}

export interface UserRatingResponse {
  titleId: string;
  titleName: string;
  posterUrl: string;
  score: number;
  ratedAt: string;
}
