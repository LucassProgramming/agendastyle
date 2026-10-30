import { apiRequest } from '../../../shared/api/http'
import type { CreateSalonService, SalonService } from '../types/SalonService'

export async function getServices(): Promise<SalonService[]> {
  return apiRequest<SalonService[]>('/api/v1/services')
}

export async function createService(service: CreateSalonService): Promise<SalonService> {
  return apiRequest<SalonService>('/api/v1/services', {
    method: 'POST',
    body: JSON.stringify(service),
  })
}
