/// <reference types="vite/client" />
interface ImportMetaEnv {
  VITE_APP_ENV: 'development' | 'test' | 'production'
  VITE_API_BASE_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}