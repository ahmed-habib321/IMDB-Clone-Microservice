/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    './src/**/*.{html,ts}',
  ],
  theme: {
    extend: {
      colors: {
        'secondary-fixed-dim': '#c8c6c5',
        'error-container': '#93000a',
        'error': '#ffb4ab',
        'on-surface-variant': '#d1c5ac',
        'on-secondary-fixed-variant': '#474646',
        'outline': '#9a9078',
        'surface-bright': '#37393a',
        'on-error-container': '#ffdad6',
        'on-error': '#690005',
        'on-tertiary-container': '#555555',
        'tertiary-fixed-dim': '#c8c6c5',
        'inverse-surface': '#e2e2e2',
        'surface-container-highest': '#333535',
        'on-tertiary-fixed-variant': '#474747',
        'surface-container-high': '#282a2b',
        'tertiary': '#e9e6e6',
        'surface': '#121414',
        'outline-variant': '#4e4633',
        'tertiary-fixed': '#e4e2e1',
        'on-surface': '#e2e2e2',
        'on-primary': '#3d2f00',
        'on-secondary-fixed': '#1c1b1b',
        'primary-fixed': '#ffe08b',
        'surface-dim': '#121414',
        'on-secondary': '#313030',
        'on-secondary-container': '#bab8b7',
        'surface-container': '#1e2020',
        'on-primary-fixed': '#241a00',
        'on-background': '#e2e2e2',
        'secondary-fixed': '#e5e2e1',
        'primary': '#ffe5a0',
        'surface-container-low': '#1a1c1c',
        'surface-container-lowest': '#0c0f0f',
        'surface-tint': '#f0c110',
        'inverse-primary': '#745b00',
        'primary-fixed-dim': '#f0c110',
        'on-tertiary-fixed': '#1b1c1c',
        'secondary': '#c8c6c5',
        'inverse-on-surface': '#2f3131',
        'on-primary-fixed-variant': '#584400',
        'background': '#121414',
        'on-tertiary': '#303030',
        'surface-variant': '#333535',
        'secondary-container': '#4a4949',
        'on-primary-container': '#695200',
        'tertiary-container': '#cccaca',
        'primary-container': '#f5c518'
      },

      borderRadius: {
        DEFAULT: '0.125rem',
        lg: '0.25rem',
        xl: '0.5rem',
        full: '0.75rem'
      },

      spacing: {
        'container-max': '1920px',
        'stack-lg': '2rem',
        'margin-mobile': '1rem',
        'stack-sm': '0.5rem',
        'gutter': '1.5rem',
        'margin-desktop': '2rem',
        'stack-md': '1rem',
        'stack-xs': '0.25rem'
      },

      fontFamily: {
        'display-lg-mobile': ['Archivo Narrow'],
        'label-caps': ['JetBrains Mono'],
        'body-md': ['Inter'],
        'display-lg': ['Archivo Narrow'],
        'body-sm': ['Inter'],
        'headline-md': ['Archivo Narrow']
      },

      fontSize: {
        'display-lg-mobile': [
          '32px',
          {
            lineHeight: '36px',
            letterSpacing: '-0.01em',
            fontWeight: '700'
          }
        ],
        'label-caps': [
          '12px',
          {
            lineHeight: '16px',
            letterSpacing: '0.05em',
            fontWeight: '500'
          }
        ],
        'body-md': [
          '16px',
          {
            lineHeight: '24px',
            letterSpacing: '0',
            fontWeight: '400'
          }
        ],
        'display-lg': [
          '48px',
          {
            lineHeight: '52px',
            letterSpacing: '-0.02em',
            fontWeight: '700'
          }
        ],
        'body-sm': [
          '14px',
          {
            lineHeight: '20px',
            letterSpacing: '0',
            fontWeight: '400'
          }
        ],
        'headline-md': [
          '24px',
          {
            lineHeight: '32px',
            letterSpacing: '0.01em',
            fontWeight: '600'
          }
        ]
      }
    }
  }
};
