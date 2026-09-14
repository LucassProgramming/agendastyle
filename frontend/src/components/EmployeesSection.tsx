import type { Employee } from '../types/Employee'
import ServicesSection from './ServicesSection'

type EmployeesSectionProps = {
  employees: Employee[]
}

function EmployeesSection({ employees }: EmployeesSectionProps) {
  return (
    <section>
      <h2>Employees</h2>
      {ServicesSection.length === 0 ? (
        <p>No services available</p>
      ) : (
        employees.map((employee) => (
            <article key={employee.id}>
            <h3>
                {employee.firstName} {employee.lastName}
            </h3>

            <p>{employee.email}</p>
            <p>{employee.phone}</p>
            </article>
        ))
      )}
    </section>
  )
}

export default EmployeesSection