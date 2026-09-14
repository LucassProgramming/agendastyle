import type { Employee } from '../types/Employee'
import ServicesSection from './ServicesSection'
import SectionTitle from './SectionTitle'

type EmployeesSectionProps = {
  employees: Employee[]
}

function EmployeesSection({ employees }: EmployeesSectionProps) {
  return (
    <section>
      <SectionTitle title="Employees" />
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