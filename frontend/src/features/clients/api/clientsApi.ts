import { apiRequest } from '../../../shared/api/http'
import type { Client, CreateClient } from '../types/Client'

export async function getClients(): Promise<Client[]> {
  return apiRequest<Client[]>('/api/v1/clients')
}

export async function createClient(client: CreateClient): Promise<Client> {
  return apiRequest<Client>('/api/v1/clients', {
    method: 'POST',
    body: JSON.stringify(client),
  })
}
