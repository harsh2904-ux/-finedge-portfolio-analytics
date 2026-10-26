/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        fin: {
          bg: 'var(--fin-bg)',
          card: 'var(--fin-card)',
          cardHover: 'var(--fin-card-hover)',
          panel: 'var(--fin-panel)',
          border: 'var(--fin-border)',
          borderLight: 'var(--fin-border-light)',
          muted: 'var(--fin-muted)',
          text: 'var(--fin-text)',
          green: '#10B981',
          greenBg: 'rgba(16, 185, 129, 0.12)',
          emerald: '#10B981',
          emeraldBg: 'rgba(16, 185, 129, 0.12)',
          red: '#EF4444',
          redBg: 'rgba(239, 68, 68, 0.12)',
          blue: '#3B82F6',
          blueBg: 'rgba(59, 130, 246, 0.12)',
          amber: '#F59E0B',
          amberBg: 'rgba(245, 158, 11, 0.12)',
          purple: '#8B5CF6',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
        mono: ['JetBrains Mono', 'Fira Code', 'monospace'],
      }
    },
  },
  plugins: [],
}
