import type { Employee } from '../types/Employee'
import SectionTitle from './SectionTitle'

type EmployeesSectionProps = {
  employees: Employee[]
}

function EmployeesSection({ employees }: EmployeesSectionProps) {
  return (
  <section>
    <SectionTitle title="Employees" />

    {employees.length === 0 ? (
      <p>No employees available.</p>
    ) : (
      <div className="card-grid">
        {employees.map((employee) => (
          <article className="card" key={employee.id}>
            <h3>
              {employee.firstName} {employee.lastName}
            </h3>

            <p>{employee.email}</p>
            <p>{employee.phone}</p>
          </article>
        ))}
      </div>
    )}
  </section>
)
}

export default EmployeesSection