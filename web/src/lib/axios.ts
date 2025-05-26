import { ApiClient } from '@wmmz/fn-api-client'

export const api = new ApiClient({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})
