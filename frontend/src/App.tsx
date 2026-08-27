import { useEffect, useState } from 'react'
import './App.css'

import { getServices } from './api/servicesApi'
import { getEmployees } from './api/employeesApi'

import type { SalonService } from './types/SalonService'
import type { Employee } from './types/Employee'

function App() {
  const [services, setServices] = useState<SalonService[]>([])
  const [employees, setEmployees] = useState<Employee[]>([])

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function loadData() {
      try {
        const servicesData = await getServices()
        setServices(servicesData)

        const employeesData = await getEmployees()
        setEmployees(employeesData)
      } catch {
        setError('Could not load AgendaStyle data')
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [])

  if (loading) {
    return <p>Loading...</p>
  }

  if (error) {
    return <p>{error}</p>
  }

  return (
    <main>
      <h1>AgendaStyle</h1>

      <section>
        <h2>Services</h2>

        {services.map((service) => (
          <article key={service.id}>
            <h3>{service.name}</h3>
            <p>{service.description}</p>
            <p>{service.durationMinutes} min</p>
            <p>{service.price} €</p>
          </article>
        ))}
      </section>

      <section>
        <h2>Employees</h2>

        {employees.map((employee) => (
          <article key={employee.id}>
            <h3>
              {employee.firstName} {employee.lastName}
            </h3>

            <p>{employee.email}</p>
            <p>{employee.phone}</p>
          </article>
        ))}
      </section>
    </main>
  )
}

export default App