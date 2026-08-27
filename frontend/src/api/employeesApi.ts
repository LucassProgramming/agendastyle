import type { Employee } from '../types/Employee'

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/employees`

export async function getEmployees(): Promise<Employee[]> {
  const response = await fetch(API_URL)

  if (!response.ok) {
    throw new Error('Failed to load employees')
  }

  return response.json()
}