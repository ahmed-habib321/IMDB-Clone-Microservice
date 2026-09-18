import { RatingDistribution } from '../../models/review.model';
import { Review, UserRatingResponse } from '../../models/review.model';

export const MOCK_REVIEWS: Review[] = [
  {
    id: 'r1',
    userId: 'u1',
    username: 'alice_films',
    titleId: 't1',
    reviewTitle: 'A masterpiece of modern sci-fi',
    body:
      'Void Voyager is everything a science fiction epic should be. It balances breathtaking spectacle with genuine emotional stakes, and the lead performances anchor even the most abstract sequences. The finale had me holding my breath. This is the kind of film that reminds you why you love cinema.',
    containsSpoiler: false,
    isApproved: true,
    helpfulYes: 1240,
    helpfulNo: 42,
    createdAt: '2023-06-01T10:00:00Z',
    updatedAt: '2023-06-01T10:00:00Z',
  },
  {
    id: 'r2',
    userId: 'u2',
    username: 'bob_reviews',
    titleId: 't1',
    reviewTitle: 'Visually stunning, narratively dense',
    body:
      'The cinematography is a pure pleasure, and the production design creates a world I want to revisit. The script occasionally gets lost in its own mythology, but the emotional through-line keeps it grounded. Worth seeing on the biggest screen you can find.',
    containsSpoiler: true,
    isApproved: true,
    helpfulYes: 310,
    helpfulNo: 15,
    createdAt: '2023-06-10T14:30:00Z',
    updatedAt: '2023-06-10T14:30:00Z',
  },
  {
    id: 'r3',
    userId: 'u1',
    username: 'alice_films',
    titleId: 't2',
    reviewTitle: 'Rain-soaked perfection',
    body:
      'Nightfall Protocol is a taut, moody detective thriller that wears its noir influences proudly. Every frame is soaked in atmosphere, and the central mystery unfolds at a perfect pace. Elena Marchetti delivers career-best work.',
    containsSpoiler: false,
    isApproved: true,
    helpfulYes: 540,
    helpfulNo: 28,
    createdAt: '2024-04-02T09:00:00Z',
    updatedAt: '2024-04-02T09:00:00Z',
  },
  {
    id: 'r4',
    userId: 'u2',
    username: 'bob_reviews',
    titleId: 't7',
    reviewTitle: 'Quietly devastating',
    body:
      'The Silent North is a meditation on solitude and connection that rewards patience. The landscape photography alone is worth the price of admission, but the restrained performances elevate it into something genuinely moving. Voted #1 drama of the decade for good reason.',
    containsSpoiler: false,
    isApproved: true,
    helpfulYes: 890,
    helpfulNo: 33,
    createdAt: '2023-01-20T18:00:00Z',
    updatedAt: '2023-01-20T18:00:00Z',
  },
];

export const MOCK_RATING_DISTRIBUTIONS: Record<string, RatingDistribution> = {
  t1: {
    distribution: [
      { score: 10, count: 210000 },
      { score: 9, count: 175000 },
      { score: 8, count: 78000 },
      { score: 7, count: 32000 },
      { score: 6, count: 9500 },
      { score: 5, count: 4200 },
      { score: 4, count: 1900 },
      { score: 3, count: 900 },
      { score: 2, count: 500 },
      { score: 1, count: 340 },
    ],
    averageRating: 9.1,
    totalVotes: 512340,
  },
  t2: {
    distribution: [
      { score: 10, count: 60000 },
      { score: 9, count: 98000 },
      { score: 8, count: 72000 },
      { score: 7, count: 31000 },
      { score: 6, count: 14000 },
      { score: 5, count: 6500 },
      { score: 4, count: 2900 },
      { score: 3, count: 1500 },
      { score: 2, count: 900 },
      { score: 1, count: 611 },
    ],
    averageRating: 8.4,
    totalVotes: 288410,
  },
};

export const MOCK_USER_RATINGS: Record<string, UserRatingResponse[]> = {
  u1: [
    {
      titleId: 't1',
      titleName: 'Void Voyager',
      posterUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCNpBxTb75KXpREUuACabTucuCrFZDz8OeNhAZxKoVnVvQ8Mhg1kNyvAMSM0qDs1Oefb7F4QRQmFHMXcbOGfQviPs1lUm4FPLcTzIFWpDyxKX6beLkpC_zuEhpwqeS7CurmIaVu-dV0Cl1Vg6AGRXahlH4CCJnW-2eTk_MElcsxQiwuFBRfUpVaXumnSfzqd8akwi30zihx2wKCcfdVZX--AuW7TIfWpEWVwsJUJEFfR59KuBXnnvKBg2syJZMFfHM5OwdDseSbVKo',
      score: 10,
      ratedAt: '2023-06-01',
    },
    {
      titleId: 't2',
      titleName: 'Nightfall Protocol',
      posterUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAQQDrefMueDZumfDv9P6PBbOrOUjT2vrIzvcd549uZIEf8CLect-ixsfi2XoUNFW-AXAMBEr8lHl5207DIXnnN6cs2eYk7u_eh7cNhfStUqrEjaxZRYe-tJZai46voGu3gvn0BM2ewW4lSOQxt3TrLibTR1kY8LsqUJk4EPoILdr57as0NZ8pfpvWqfK-e2iJ0aux6fb9dTYJKTDr20-tBCC5ClPgQsUQAhxMV8MpS-2FJk2ccviUSXLEp5zfuQKIJ5Tkr5DejYSA',
      score: 9,
      ratedAt: '2024-04-02',
    },
    {
      titleId: 't7',
      titleName: 'The Silent North',
      posterUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCyIkX5X34pMeISpyBOlfHIqyCCDlp-prXEwulDw-C-o5r7vAUCejnzQS-0I4EnxzV0_bPYegrm420082-qvcZee0-BjJh9zf7QCq-RGY_aaBevtfhTjFfyzpgkaLndmFG9AW8myhtfVcHav3igg06nwVZswfH6NayyKh3rZOP_p87zYHW2E_VEn4p2X6z6HcSCdu4oIrUtQYIsS1EVI2v0rqpnDW7W2BDxrb-6kbYNikUMNrlZ3vI0gol18R5obLOJMfdakN3QRnI',
      score: 9,
      ratedAt: '2023-02-15',
    },
  ],
  u2: [
    {
      titleId: 't1',
      titleName: 'Void Voyager',
      posterUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCNpBxTb75KXpREUuACabTucuCrFZDz8OeNhAZxKoVnVvQ8Mhg1kNyvAMSM0qDs1Oefb7F4QRQmFHMXcbOGfQviPs1lUm4FPLcTzIFWpDyxKX6beLkpC_zuEhpwqeS7CurmIaVu-dV0Cl1Vg6AGRXahlH4CCJnW-2eTk_MElcsxQiwuFBRfUpVaXumnSfzqd8akwi30zihx2wKCcfdVZX--AuW7TIfWpEWVwsJUJEFfR59KuBXnnvKBg2syJZMFfHM5OwdDseSbVKo',
      score: 8,
      ratedAt: '2023-06-15',
    },
    {
      titleId: 't7',
      titleName: 'The Silent North',
      posterUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCyIkX5X34pMeISpyBOlfHIqyCCDlp-prXEwulDw-C-o5r7vAUCejnzQS-0I4EnxzV0_bPYegrm420082-qvcZee0-BjJh9zf7QCq-RGY_aaBevtfhTjFfyzpgkaLndmFG9AW8myhtfVcHav3igg06nwVZswfH6NayyKh3rZOP_p87zYHW2E_VEn4p2X6z6HcSCdu4oIrUtQYIsS1EVI2v0rqpnDW7W2BDxrb-6kbYNikUMNrlZ3vI0gol18R5obLOJMfdakN3QRnI',
      score: 10,
      ratedAt: '2023-02-20',
    },
  ],
};
