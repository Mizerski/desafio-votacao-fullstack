import { ApiClient } from '@wmmz/fn-api-client'
import { ENDPOINT_PREFIX } from './endpoints'
import { storage } from './storage'

export const api = new ApiClient({
  baseURL: ENDPOINT_PREFIX,
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${storage.getTokens()?.accessToken}`,
  },
})

console.log(
  'storage.getTokens()?.accessToken',
  storage.getTokens()?.accessToken,
)
