export interface SalonService {
  id: number
  name: string
  description: string
  durationMinutes: number
  price: number
  active: boolean
}

export type CreateSalonService = {
  name: string
  description: string
  durationMinutes: number
  price: number
}
