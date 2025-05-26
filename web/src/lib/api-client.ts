import { ApiClient } from '@wmmz/fn-api-client'
import { ENDPOINT_PREFIX } from './endpoints'

export const api = new ApiClient({
  baseURL: ENDPOINT_PREFIX,
  headers: {
    'Content-Type': 'application/json',
  },
})
