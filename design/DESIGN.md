---
name: Boutique Editorial
colors:
  surface: '#fff8f2'
  surface-dim: '#e0d9d0'
  surface-bright: '#fff8f2'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#faf2e9'
  surface-container: '#f5ede4'
  surface-container-high: '#efe7de'
  surface-container-highest: '#e9e1d8'
  on-surface: '#1e1b16'
  on-surface-variant: '#59413c'
  inverse-surface: '#34302a'
  inverse-on-surface: '#f8efe6'
  outline: '#8d716b'
  outline-variant: '#e1bfb8'
  surface-tint: '#ae311b'
  primary: '#ab2e18'
  on-primary: '#ffffff'
  primary-container: '#cd472e'
  on-primary-container: '#fffbff'
  inverse-primary: '#ffb4a5'
  secondary: '#5f5e5e'
  on-secondary: '#ffffff'
  secondary-container: '#e2dfde'
  on-secondary-container: '#636262'
  tertiary: '#5c5c58'
  on-tertiary: '#ffffff'
  tertiary-container: '#757571'
  on-tertiary-container: '#fefcf7'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdad3'
  primary-fixed-dim: '#ffb4a5'
  on-primary-fixed: '#3f0400'
  on-primary-fixed-variant: '#8c1804'
  secondary-fixed: '#e5e2e1'
  secondary-fixed-dim: '#c8c6c5'
  on-secondary-fixed: '#1c1b1b'
  on-secondary-fixed-variant: '#474746'
  tertiary-fixed: '#e4e2dd'
  tertiary-fixed-dim: '#c8c6c2'
  on-tertiary-fixed: '#1b1c19'
  on-tertiary-fixed-variant: '#474744'
  background: '#fff8f2'
  on-background: '#1e1b16'
  surface-variant: '#e9e1d8'
typography:
  display-xl:
    fontFamily: Playfair Display
    fontSize: 80px
    fontWeight: '700'
    lineHeight: 88px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Playfair Display
    fontSize: 48px
    fontWeight: '600'
    lineHeight: 56px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-md:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '500'
    lineHeight: 40px
  body-lg:
    fontFamily: Work Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Work Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-sm:
    fontFamily: Work Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.1em
spacing:
  container-max: 1280px
  gutter: 24px
  margin-desktop: 64px
  margin-mobile: 20px
  stack-unit: 8px
---

## Brand & Style

This design system shifts the brand toward a high-fashion, hospitality-centric identity. The aesthetic is inspired by premium print magazines and boutique hotel branding, emphasizing curation over utility. 

The design style is **Editorial Minimalism**. It prioritizes extreme whitespace, intentional asymmetry, and a rigorous adherence to a grid that mimics a physical broadsheet. By stripping away common SaaS tropes—such as soft shadows, pill-shaped buttons, and vibrant gradients—the UI achieves an air of exclusivity and permanence. The emotional response is one of calm, sophisticated luxury and authoritative taste.

## Colors

The palette is rooted in organic, architectural tones that evoke natural materials and high-end print.

- **Primary (#D34B32):** The legacy terracotta is retained but used sparingly as an "Editorial Accent"—reserved for key call-to-actions, active states, or critical semantic markers.
- **Secondary (#1A1A1A):** A deep charcoal, almost ink-black, used for primary typography and structural borders to provide a grounded, authoritative weight.
- **Tertiary (#F9F7F2):** A rich, warm cream that serves as the primary canvas color. It is softer than pure white, reducing eye strain and feeling more "physical" like premium paper stock.
- **Neutral (#6B665F):** A muted earth tone used for secondary text, captions, and subtle dividers.

## Typography

Typography is the cornerstone of the editorial look. It relies on the dramatic contrast between a sophisticated Serif and a functional Sans-Serif.

- **Headlines:** Playfair Display provides a high-contrast, elegant feel. For the largest display sizes, a slight negative letter-spacing is applied to mimic tight kerning in print headlines.
- **Body:** Work Sans is chosen for its grounded, professional nature. It offers excellent legibility at medium sizes and provides a neutral counter-balance to the expressive headlines.
- **Labels:** Use Work Sans in uppercase with generous letter-spacing (tracking) for a "gallery" or "catalog" aesthetic.

## Layout & Spacing

The layout follows a **Strict Fixed Grid** for desktop and a **Fluid Mono-column** for mobile.

- **Grid:** A 12-column grid with wide gutters (24px) allows for significant white space. Elements should often "offset" from the grid (e.g., spanning columns 2 through 10) to create a centered, curated look.
- **Rhythm:** Spacing follows an 8px base unit, but "Editorial Gaps" of 80px, 120px, or 160px should be used between major sections to emphasize the premium nature of the content.
- **Mobile:** On mobile, margins reduce to 20px, and typography scales down aggressively to maintain the high-contrast hierarchy without overwhelming the viewport.

## Elevation & Depth

This design system rejects traditional shadows. Depth is created through **Tonal Layering** and **Structural Outlines**.

- **Flat Depth:** Use the Tertiary cream (#F9F7F2) as the base, with secondary containers using a slightly darker parchment tone or a very fine 1px charcoal border (#1A1A1A at 10% opacity).
- **Hard Lines:** Instead of shadows, use crisp 1px lines to separate content sections or define cards.
- **Overlap:** Use CSS grid to allow images to slightly overlap text blocks or other containers, creating a collage-like depth common in fashion editorials.

## Shapes

The shape language is architectural and precise. 

- **Corners:** Use sharp, 0px corners for almost all structural elements (containers, cards, buttons).
- **Exceptions:** If a "softening" is required for specific interactive elements like input fields or small chips, a maximum radius of 2px may be applied, but square edges are the preferred default to maintain the boutique aesthetic.

## Components

- **Buttons:** Rectangular with 0px radius. Use the Secondary charcoal for primary buttons with white text, and a 1px charcoal outline for secondary buttons. No shadows. Text should be uppercase Work Sans with 0.1em letter spacing.
- **Cards:** Defined by a 1px border or simply by the layout of the image and text. Avoid background fills for cards; let them sit directly on the Tertiary canvas.
- **Input Fields:** Bottom-border only (1px charcoal) to mimic high-end stationery or registration forms.
- **Chips/Tags:** Small, rectangular, with a light neutral background and tight typography.
- **Lists:** Use wide spacing between items, separated by 1px horizontal rules that span the full width of the container.
- **Imagery:** All images should use a consistent aspect ratio (e.g., 4:5 or 1:1) and have sharp corners. Treat images like art pieces in a gallery.