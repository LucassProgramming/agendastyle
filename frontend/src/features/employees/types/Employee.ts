export interface Employee {
  id: number
  firstName: string
  lastName: string
  phone: string
  email: string
  active: boolean
  serviceIds: number[]
}

export type CreateEmployee = {
  firstName: string
  lastName: string
  phone: string
  email: string
  serviceIds: number[]
}

export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY'

export interface EmployeeSchedule {
  id: number
  employeeId: number
  dayOfWeek: DayOfWeek
  startTime: string
  endTime: string
}

export type CreateEmployeeSchedule = Omit<EmployeeSchedule, 'id' | 'employeeId'>
