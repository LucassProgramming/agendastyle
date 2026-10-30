export type AppointmentStatus = 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW'

export interface Appointment {
  id: number
  startDateTime: string
  endDateTime: string
  status: AppointmentStatus
  notes: string | null
  createdAt: string
  clientId: number
  employeeId: number
  serviceId: number
}

export type CreateAppointment = {
  clientId: number
  employeeId: number
  serviceId: number
  startDateTime: string
  notes: string
}
