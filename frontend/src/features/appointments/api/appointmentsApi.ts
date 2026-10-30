import { apiRequest } from '../../../shared/api/http'
import type { Appointment, CreateAppointment } from '../types/Appointment'

export async function getAppointments(): Promise<Appointment[]> {
  return apiRequest<Appointment[]>('/api/v1/appointments')
}

export async function createAppointment(appointment: CreateAppointment): Promise<Appointment> {
  return apiRequest<Appointment>('/api/v1/appointments', {
    method: 'POST',
    body: JSON.stringify(appointment),
  })
}

export async function cancelAppointment(appointmentId: number): Promise<Appointment> {
  return apiRequest<Appointment>(`/api/v1/appointments/${appointmentId}/cancel`, {
    method: 'PATCH',
  })
}

export async function getDailyAgenda(employeeId: number, date: string): Promise<Appointment[]> {
  const query = new URLSearchParams({ date })
  return apiRequest<Appointment[]>(
    `/api/v1/employees/${employeeId}/appointments?${query.toString()}`,
  )
}
