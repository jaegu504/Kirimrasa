---
name: Emerald Cybernetic Automation
colors:
  surface: '#f7f9ff'
  surface-dim: '#d6dae1'
  surface-bright: '#f7f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f0f4fb'
  surface-container: '#eaeef5'
  surface-container-high: '#e4e8ef'
  surface-container-highest: '#dee3ea'
  on-surface: '#171c21'
  on-surface-variant: '#3c4a3d'
  inverse-surface: '#2c3136'
  inverse-on-surface: '#edf1f8'
  outline: '#6c7b6b'
  outline-variant: '#bbcbb9'
  surface-tint: '#016d2f'
  primary: '#016d2f'
  on-primary: '#ffffff'
  primary-container: '#71cb81'
  on-primary-container: '#005523'
  inverse-primary: '#7fda8f'
  secondary: '#006c49'
  on-secondary: '#ffffff'
  secondary-container: '#9af2c5'
  on-secondary-container: '#0c714d'
  tertiary: '#006c4a'
  on-tertiary: '#ffffff'
  tertiary-container: '#73c9a0'
  on-tertiary-container: '#005439'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#9bf7a9'
  primary-fixed-dim: '#7fda8f'
  on-primary-fixed: '#002109'
  on-primary-fixed-variant: '#005322'
  secondary-fixed: '#9df4c8'
  secondary-fixed-dim: '#81d8ad'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#9df4c9'
  tertiary-fixed-dim: '#81d8ae'
  on-tertiary-fixed: '#002114'
  on-tertiary-fixed-variant: '#005237'
  background: '#f7f9ff'
  on-background: '#171c21'
  surface-variant: '#dee3ea'
typography:
  headline-xl:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-xl-mobile:
    fontFamily: Inter
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 30px
    letterSpacing: -0.015em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 26px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: '0'
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: -0.005em
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: '0'
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.01em
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Inter
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 0.75rem
  gutter-tablet: 1rem
  margin: 1rem
  margin-tablet: 1.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1rem
  space-xl: 1.5rem
---

## Brand & Style

This design system is engineered specifically for high-efficiency Android mobile workflows, targeting power users, marketers, enterprise outreach teams, and community managers who require rapid, reliable bulk messaging operations. The aesthetic blends high-tech utilitarian precision with modern cybernetic light styling, projecting trust, unyielding speed, and robust operational stability.

The design philosophy unites modern minimalism with controlled glassmorphism and tactile glow accents:
- **Atmospheric Depth:** Clean light foundations eliminate glare, maintaining high visibility during bright daylight campaigns and long operational sessions.
- **Precision Accents:** WhatsApp electric emerald highlights signal active operational status, success triggers, and actionable automation controls.
- **Glassmorphic Control Surfaces:** Key cards and dialogs utilize semi-translucent backdrops, fine internal borders, and subtle outer green diffusions to create distinct elevation tiers without visual noise.
- **Tactile Android Utility:** Touch targets conform rigidly to mobile ergonomics (minimum 48x48dp interactive areas), utilizing pill-shaped controls and responsive, micro-elevation state shifts under touch.

## Colors

The color palette is engineered for light mode interfaces, anchoring on clean light slate tones paired with laser-sharp emerald and electric green functional indicators.

### Surface Tiers & Foundation
- **Base Canvas (`#F8FAFC`):** The absolute background surface for mobile screens, establishing rich contrast with zero pure-white harshness.
- **Surface Level 1 (`#FFFFFF`):** Primary card surfaces, bottom navigation bars, and top app bars.
- **Surface Level 2 (`#F1F5F9`):** Secondary nested containers, elevated bottom sheets, dropdown menus, and input fields.
- **Surface Highlight (`#E2E8F0`):** Active card backgrounds, selected row states, and pressed chip surfaces.

### Emerald & Green Signal Palette
- **Primary / Action Green (`#25D366`):** The signature vibrant messaging green used for primary triggers, active execution toggles, floating action buttons, and verified campaign progress indicators.
- **Secondary Accent (`#10B981`):** A cooler, high-tech emerald used for subtle glowing strokes, secondary icons, badges, and positive statistical trends.
- **Tertiary Structural Green (`#059669`):** Deep emerald used for subdued borders, badge backgrounds, and non-distracting progress track fills.

### Text & Iconography Contrast
- **Crisp Primary (`#0B1015`):** Reserved for primary headings, active button labels, critical counters, and high-priority metrics.
- **Muted Body (`#475569`):** Applied to general body copy, campaign descriptions, message previews, and metadata labels.
- **Subdued Tertiary (`#64748B`):** Applied to timestamps, character limits, disabled placeholders, and subtle divider strokes.

### Semantic Status
- **Error / Failure:** `#EF4444` (Banned accounts, failed transmissions, network dropouts).
- **Warning / Throttle:** `#F59E0B` (Approaching rate limits, pending anti-spam cooldowns).
- **Info / Queue:** `#0284C7` (Scheduled queues, background sync running).

## Typography

The typography stack relies exclusively on `Inter` for unmatched data legibility, numeric clarity, and technical authority on high-DPI mobile displays.

- **Numeric & Metric Readouts:** Use `font-feature-settings: "tnum" on, "cv05" on` to enforce tabular figures and open-tail characters across queue counters, batch percentages, delay timers, and transmission telemetry.
- **Hierarchy Enforcement:** Headers command attention with sharp contrast and slight negative tracking (`-0.01em` to `-0.02em`), eliminating the generic bubble-app aesthetic in favor of a precision tool suite.
- **Labeling & Micro-Stats:** Micro-labels (`label-sm`) employ uppercase styling paired with expanded tracking (`0.04em`) and 700 weight for instantaneous readability when paired with campaign status tags or speed throttle limits.

## Layout & Spacing

This layout system is structured around an ultra-compact 4-column fluid mobile grid (expanding to 8 columns on folding devices and tablets), built for dense Android operational interfaces.

### Mobile Grid Mechanics
- **Base Canvas Margins:** Strict `16px` (`margin`) outer edge gutters on phone viewports ensure controls never clip against curved glass edges or Android gesture zones.
- **Compact Padding Cadence:** Internal component layouts respect a strict 4px/8px modular rhythm. Component padding prioritizes high information density: lists, log rows, and contact rows leverage `space-sm` (`8px`) vertical gaps and `space-md` (`12px`) horizontal containment.
- **System Nav Bar & Safe Insets:** Respect Android Edge-to-Edge displays. Top app headers require dynamic padding matching `WindowInsets.statusBars`, while fixed bottom utility docks pad `WindowInsets.navigationBars + space-lg`.
- **Reflow & Foldables:** At breakpoints above 600dp, list panes (contact selection, active campaign queues) anchor permanently to the left 4 columns, while dispatch parameters, CSV import previewers, and message configuration fill the remaining width.

## Elevation & Depth

Visual hierarchy uses a hybrid of tonal layering, glassmorphism, and laser-precise emerald luminescence rather than conventional murky drop shadows.

### Surface Tiers
- **Tier 0 (Backdrop):** Raw background at `#F8FAFC`. Zero elevation.
- **Tier 1 (Resting Cards & List Shells):** Background `#FFFFFF` with a 1px perimeter outline of `rgba(15, 23, 42, 0.08)`. No shadow.
- **Tier 2 (Glassmorphic Elevated Panels & Sheets):** Background `rgba(255, 255, 255, 0.75)` with a backdrop blur of `16px` (`backdrop-filter: blur(16px)`). Bound by an upper-edge specular border of `rgba(255, 255, 255, 0.8)` and a bottom border of `rgba(15, 23, 42, 0.1)`. Shadow: `0 8px 24px -4px rgba(15, 23, 42, 0.08)`.

### Ambient Glows & Cybernetic Accents
- **Active Transmission Glow:** Critical executing elements (e.g., the primary "Start Campaign" CTA or live message pump progress card) emit a directional emerald halo: `0 0 20px rgba(37, 211, 102, 0.28), 0 2px 8px rgba(16, 185, 129, 0.2)`.
- **Glow Borders:** High-priority cards (such as Anti-Ban Delay Protection indicators) feature a 1px border colored `rgba(37, 211, 102, 0.35)`.
- **Android Modal Sheets:** Surface `#FFFFFF` resting over a `rgba(15, 23, 42, 0.4)` blurred scrim (`blur(8px)`).

## Shapes

The design system embraces sleek pill-shaped geometry (`roundedness: 3`) balanced with structural panel corners to achieve a refined, tactile mobile utility posture.

- **Primary & Secondary Action Controls:** Full pill geometry (`rounded-full` / `9999px`) across buttons, filter chips, operational toggle indicators, and search input bars.
- **Cards & Data Modules:** Enclosed within `rounded-lg` (`2rem` / `32px` on outer containers, tapering to `1.25rem` / `20px` for internal modular panels).
- **Text Entry Panels & Message Composition Areas:** Sculpted with `1.25rem` (`20px`) corners, establishing smooth visual containment for multiline templates and spintax strings.
- **Progress Track Caps & Small Badges:** Full pill radius (`9999px`) on both track rail and moving buffer indicators to sustain smooth fluidity.

## Components

### Buttons
- **Primary FAB / Trigger ("Send Bulk Batch"):** Pill-shaped, full-width or floating. Background `#25D366` with high-contrast `#0B1015` bold text (`label-lg`). Shadow: `0 0 24px rgba(37, 211, 102, 0.35)`. Pressed state scales down to `0.98` with `#10B981` surface.
- **Secondary Utility (e.g., "Import CSV / VCF"):** Pill-shaped. Background `#F1F5F9`, crisp dark `#0B1015` text, bound by a 1px border of `rgba(16, 185, 129, 0.3)`. Hover/Touch: subtle emerald back-glow.
- **Destructive ("Abort Transmission"):** Pill-shaped. Background `rgba(239, 68, 68, 0.12)`, text `#EF4444`, 1px border of `rgba(239, 68, 68, 0.3)`.

### Chips & Filter Pills
- **Campaign Tags & Filter Groups:** Height 32dp, pill radius. Idle: `#FFFFFF` with `#475569` text and `rgba(15, 23, 42, 0.12)` border.
- **Selected State:** `#F1F5F9` background with `#25D366` text, 1px `#25D366` glow border, and a 6dp active emerald indicator dot.

### Input Fields & Spintax Composer
- **Message Area & Inputs:** Background `rgba(255, 255, 255, 0.85)` with backdrop blur. Resting border: 1px `rgba(15, 23, 42, 0.15)`. Focused border: 1.5px `#25D366` accompanied by an exterior glow of `0 0 12px rgba(37, 211, 102, 0.2)`. Text: `#0B1015`. Placeholder: `#64748B`.
- **Spintax Helper Bar:** Integrated horizontal bar above keyboard displaying quick tags (`{Name}`, `{RandomKey}`) styled as compact micro-pills in `#F1F5F9` with `#10B981` text.

### Selection Controls (Checkboxes & Radios)
- **Checkboxes (Contact Selection):** 20x20dp rounded squircle (6dp radius). Unchecked: `#F1F5F9` fill with 1.5px `#64748B` border. Checked: `#25D366` fill with `#0B1015` checkmark icon and slight outer glow.
- **Switches (Anti-Ban Engine / Dynamic Delay):** 52x30dp pill track. Inactive: `#F1F5F9` track with `#64748B` thumb. Active: `#059669` track with `#25D366` thumb projecting `0 0 10px rgba(37, 211, 102, 0.5)`.

### Cards & Telemetry Displays
- **Glassmorphic Campaign Monitor Card:** Surface `rgba(255, 255, 255, 0.8)` with 16px blur, 1px upper highlight border `rgba(255, 255, 255, 0.8)`, and rounded-lg corners. Holds real-time metrics: Sent, Delivered, Failed, and Estimated Time Remaining using `Inter` tabular numerals.
- **Contact List Item:** Height 64dp. Includes contact avatar (light slate circle with `#10B981` monogram), dual-line phone/name info, and delivery status badge. Active touch response triggers an instant flash of `rgba(37, 211, 102, 0.08)`.

### Automation Delay Slider & Counter
- **Delay Regulator:** Custom 6dp rounded rail with `#F1F5F9` inactive background and `#25D366` active buffer fill. The draggable thumb is a 24dp `#FFFFFF` circle embedded with a `#25D366` center core and radiant emerald drop elevation.