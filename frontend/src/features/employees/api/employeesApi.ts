import { apiRequest } from '../../../shared/api/http'
import type {
  CreateEmployee,
  CreateEmployeeSchedule,
  Employee,
  EmployeeSchedule,
} from '../types/Employee'

export async function getEmployees(): Promise<Employee[]> {
  return apiRequest<Employee[]>('/api/v1/employees')
}

export async function createEmployee(employee: CreateEmployee): Promise<Employee> {
  return apiRequest<Employee>('/api/v1/employees', {
    method: 'POST',
    body: JSON.stringify(employee),
  })
}

export async function getEmployeeSchedules(employeeId: number): Promise<EmployeeSchedule[]> {
  return apiRequest<EmployeeSchedule[]>(`/api/v1/employees/${employeeId}/schedules`)
}

export async function createEmployeeSchedule(
  employeeId: number,
  schedule: CreateEmployeeSchedule,
): Promise<EmployeeSchedule> {
  return apiRequest<EmployeeSchedule>(`/api/v1/employees/${employeeId}/schedules`, {
    method: 'POST',
    body: JSON.stringify(schedule),
  })
}
