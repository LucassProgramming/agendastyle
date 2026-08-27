import type { SalonService } from '../types/SalonService'

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/services`

export async function getServices(): Promise<SalonService[]> {
  const response = await fetch(API_URL)

  if (!response.ok) {
    throw new Error('Failed to load services')
  }

  return response.json()
}