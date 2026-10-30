import { useEffect, useState } from 'react'
import Header from './components/Header.tsx'
import Navigation from './components/Navigation'
import ServicesSection from '../features/services/components/ServicesSection'
import EmployeesSection from '../features/employees/components/EmployeesSection'


import './App.css'

import { getServices } from '../features/services/api/servicesApi'
import { getEmployees } from '../features/employees/api/employeesApi'

import type { SalonService } from '../features/services/types/SalonService'
import type { Employee } from '../features/employees/types/Employee'


function App() {
  const [services, setServices] = useState<SalonService[]>([])
  const [employees, setEmployees] = useState<Employee[]>([])

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [section, setSection] = useState<'services' | 'employees'>('services')

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
    <>
      <Header
        title = "AgendaStyle"
        subtitle='Manage your salon appointments easily'
      />

    <main>
      <Navigation
        section={section}
        onSectionChange={setSection}
      />
    {section === 'services' && (
      <ServicesSection services={services} />
    )}

    {section === 'employees' && (
      <EmployeesSection employees={employees} />
    )}
    </main>
  </>
  )
}

export default App
