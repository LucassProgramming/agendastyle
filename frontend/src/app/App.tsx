import { useEffect, useState } from 'react'
import Header from './components/Header.tsx'
import Navigation, { type AppSection } from './components/Navigation'
import ServicesSection from '../features/services/components/ServicesSection'
import EmployeesSection from '../features/employees/components/EmployeesSection'
import ClientsSection from '../features/clients/components/ClientsSection'
import AppointmentsSection from '../features/appointments/components/AppointmentsSection'
import AgendaSection from '../features/appointments/components/AgendaSection'
import './App.css'
import { getServices } from '../features/services/api/servicesApi'
import { getEmployees } from '../features/employees/api/employeesApi'
import { getClients } from '../features/clients/api/clientsApi'
import { getAppointments } from '../features/appointments/api/appointmentsApi'
import { getErrorMessage } from '../shared/api/http'
import FeedbackMessage from '../shared/components/FeedbackMessage'
import type { SalonService } from '../features/services/types/SalonService'
import type { Employee } from '../features/employees/types/Employee'
import type { Client } from '../features/clients/types/Client'
import type { Appointment } from '../features/appointments/types/Appointment'

function App() {
  const [services, setServices] = useState<SalonService[]>([])
  const [employees, setEmployees] = useState<Employee[]>([])
  const [clients, setClients] = useState<Client[]>([])
  const [appointments, setAppointments] = useState<Appointment[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [section, setSection] = useState<AppSection>('appointments')

  useEffect(() => {
    async function loadData() {
      try {
        const [servicesData, employeesData, clientsData, appointmentsData] = await Promise.all([
          getServices(), getEmployees(), getClients(), getAppointments(),
        ])
        setServices(servicesData)
        setEmployees(employeesData)
        setClients(clientsData)
        setAppointments(appointmentsData)
      } catch (loadError) {
        setError(getErrorMessage(loadError))
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [])

  function updateAppointment(updated: Appointment) {
    setAppointments((current) => current.map((appointment) => appointment.id === updated.id ? updated : appointment))
  }

  return (
    <>
      <Header title="AgendaStyle" subtitle="Organiza el trabajo del salón desde un solo lugar." counts={{ appointments: appointments.length, clients: clients.length, employees: employees.length }} />
      <main>
        <Navigation section={section} onSectionChange={setSection} />
        {loading && <div className="page-state"><span className="loader" /><strong>Cargando el salón…</strong></div>}
        {!loading && error && <div className="page-state"><strong>No se pudieron cargar los datos</strong><FeedbackMessage message={error} tone="error" /><span>Comprueba que el backend esté disponible y vuelve a cargar la página.</span></div>}
        {!loading && !error && (
          <>
            {section === 'services' && <ServicesSection services={services} onCreated={(service) => setServices((current) => [...current, service])} />}
            {section === 'employees' && <EmployeesSection employees={employees} services={services} onCreated={(employee) => setEmployees((current) => [...current, employee])} />}
            {section === 'clients' && <ClientsSection clients={clients} onCreated={(client) => setClients((current) => [...current, client])} />}
            {section === 'appointments' && <AppointmentsSection appointments={appointments} clients={clients} employees={employees} services={services} onCreated={(appointment) => setAppointments((current) => [...current, appointment])} onUpdated={updateAppointment} />}
            {section === 'agenda' && <AgendaSection employees={employees} clients={clients} services={services} />}
          </>
        )}
      </main>
    </>
  )
}

export default App
