import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// GitHub Pages serves the app at https://matin-mohebi.github.io/TaqvimCod/
// Relative base keeps every asset URL portable.
export default defineConfig({
  plugins: [react()],
  base: './',
  build: {
    target: 'es2019',
    outDir: 'dist',
    assetsInlineLimit: 0,
  },
});
