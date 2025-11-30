/// <reference types="vite/client" />
export {};

interface ImportMetaEnv {
  VITE_APP_ENV: 'development' | 'test' | 'production'
  VITE_API_BASE_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}