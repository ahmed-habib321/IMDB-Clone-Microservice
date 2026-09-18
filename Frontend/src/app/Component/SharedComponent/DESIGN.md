---
name: Cinematic Noir
colors:
  surface: '#121414'
  surface-dim: '#121414'
  surface-bright: '#37393a'
  surface-container-lowest: '#0c0f0f'
  surface-container-low: '#1a1c1c'
  surface-container: '#1e2020'
  surface-container-high: '#282a2b'
  surface-container-highest: '#333535'
  on-surface: '#e2e2e2'
  on-surface-variant: '#d1c5ac'
  inverse-surface: '#e2e2e2'
  inverse-on-surface: '#2f3131'
  outline: '#9a9078'
  outline-variant: '#4e4633'
  surface-tint: '#f0c110'
  primary: '#ffe5a0'
  on-primary: '#3d2f00'
  primary-container: '#f5c518'
  on-primary-container: '#695200'
  inverse-primary: '#745b00'
  secondary: '#c8c6c5'
  on-secondary: '#313030'
  secondary-container: '#4a4949'
  on-secondary-container: '#bab8b7'
  tertiary: '#e9e6e6'
  on-tertiary: '#303030'
  tertiary-container: '#cccaca'
  on-tertiary-container: '#555555'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffe08b'
  primary-fixed-dim: '#f0c110'
  on-primary-fixed: '#241a00'
  on-primary-fixed-variant: '#584400'
  secondary-fixed: '#e5e2e1'
  secondary-fixed-dim: '#c8c6c5'
  on-secondary-fixed: '#1c1b1b'
  on-secondary-fixed-variant: '#474646'
  tertiary-fixed: '#e4e2e1'
  tertiary-fixed-dim: '#c8c6c5'
  on-tertiary-fixed: '#1b1c1c'
  on-tertiary-fixed-variant: '#474747'
  background: '#121414'
  on-background: '#e2e2e2'
  surface-variant: '#333535'
typography:
  display-lg:
    fontFamily: Archivo Narrow
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 52px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Archivo Narrow
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Archivo Narrow
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: 0.01em
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0em
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  label-caps:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  container-max: 1280px
  gutter: 1.5rem
  margin-desktop: 2rem
  margin-mobile: 1rem
  stack-xs: 0.25rem
  stack-sm: 0.5rem
  stack-md: 1rem
  stack-lg: 2rem
---

## Brand & Style

The design system is engineered to evoke the immersive atmosphere of a premium cinema. The brand personality is authoritative, sophisticated, and data-centric, catering to cinephiles who value both aesthetic depth and information density. 

The style utilizes a **Modern-Corporate** approach with **Minimalist** leanings. It prioritizes content (posters, trailers, and metadata) by utilizing a deep, monochromatic background strategy that allows the iconic accent color to command attention. The interface feels like a high-end production tool—precise, high-contrast, and impeccably organized. It avoids unnecessary decoration, relying instead on structured grids and purposeful motion to guide the user through vast libraries of cinematic data.

## Colors

The palette is anchored in a "True Dark" philosophy to ensure the screen recedes, making imagery pop.

- **Primary (#F5C518):** The iconic cinematic yellow. Used exclusively for primary calls to action, ratings, and critical highlights. It must maintain high contrast against dark backgrounds.
- **Secondary (#121212):** The base "Deep Charcoal." This is the primary surface color for the application background to reduce eye strain and provide a premium feel.
- **Tertiary (#2C2C2C):** "Studio Grey." Used for container surfaces, card backgrounds, and input fields to create subtle depth against the secondary base.
- **Neutral (#FFFFFF):** Pure white is reserved for high-priority headings. Secondary text should utilize a 70% opacity (Silver) to maintain hierarchy.

## Typography

The typography strategy focuses on industrial efficiency and readability. 

**Archivo Narrow** is used for headlines to echo the verticality of classic film posters and maximize horizontal space for long movie titles. **Inter** provides a neutral, highly legible foundation for synopses and cast lists. **JetBrains Mono** is introduced for technical metadata (run times, release years, technical specs) to give the system a precise, "database" feel.

All headings should be high-contrast (White), while body text should move to a subtle off-white to prevent "vibrating" against the dark background.

## Layout & Spacing

This design system uses a **Fixed Grid** model for desktop to ensure data remains scannable and controlled. 

- **Desktop:** 12-column grid with a 1280px max-width. Gutters are kept tight (24px) to emphasize the density of information.
- **Tablet:** 8-column fluid grid with 24px margins.
- **Mobile:** 4-column fluid grid with 16px margins. 

The vertical spacing rhythm follows a strict 4px baseline. Use `stack-lg` for separating major sections (e.g., "Top Cast" from "User Reviews") and `stack-sm` for internal component grouping (e.g., a movie title and its release year).

## Elevation & Depth

In a dark-themed cinematic environment, shadows are less effective. Instead, this design system utilizes **Tonal Layers** and **Low-Contrast Outlines**.

- **Level 0 (Background):** Secondary color (#121212).
- **Level 1 (Cards/Sections):** Tertiary color (#2C2C2C).
- **Level 2 (Modals/Popovers):** Tertiary color with a 1px solid stroke of #3D3D3D to define edges.

For interactive elements like hovering over a movie poster, use a subtle scale-up effect (1.02x) rather than a heavy shadow to maintain a clean, modern aesthetic.

## Shapes

The shape language is "Professional-Soft." We avoid aggressive rounding to maintain a serious, cinematic tone. 

Standard components like buttons and input fields use a **4px radius (Soft)**. This provides just enough approachable warmth without sacrificing the structured, grid-based feel of a data-rich application. Large containers like hero sections or carousels remain sharp or use the minimum 4px radius to align with the rectangular nature of film frames.

## Components

### Buttons
- **Primary:** Background in Primary Yellow, text in Secondary Black. Bold, uppercase labels.
- **Secondary:** Transparent background with a 1px White stroke.
- **Ghost:** No background, Primary Yellow text for inline actions.

### Cards
- **Movie Cards:** Vertical aspect ratio (2:3). Content info (title/rating) sits on a Tertiary background below the image. 
- **Character Cards:** Circular avatars with centered text below.

### Inputs
- **Search Bar:** Large, spanning the header, using Tertiary background with a leading magnifying glass icon. 
- **Select/Dropdowns:** Minimalist chevron icon, using `label-caps` for the selection text.

### Interactive Elements
- **Ratings:** Always feature the Primary Yellow star icon followed by the rating in `headline-md`.
- **Chips/Badges:** Used for genres (e.g., "Action", "Sci-Fi"). Small, Tertiary background, 4px roundedness, with `body-sm` text.
- **Lists:** Cast lists should use a horizontal scrolling pattern on mobile and a structured grid on desktop with "See All" chevron links.