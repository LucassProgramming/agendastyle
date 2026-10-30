import { useState, type FormEvent } from 'react'
import { getErrorMessage } from '../../../shared/api/http'
import FeedbackMessage from '../../../shared/components/FeedbackMessage'
import SectionTitle from '../../../shared/components/SectionTitle'
import type { Client } from '../../clients/types/Client'
import type { Employee } from '../../employees/types/Employee'
import type { SalonService } from '../../services/types/SalonService'
import { getDailyAgenda } from '../api/appointmentsApi'
import type { Appointment } from '../types/Appointment'

type AgendaSectionProps = { employees: Employee[]; clients: Client[]; services: SalonService[] }

function AgendaSection({ employees, clients, services }: AgendaSectionProps) {
  const [employeeId, setEmployeeId] = useState('')
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10))
  const [agenda, setAgenda] = useState<Appointment[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setLoading(true)
    setError(null)
    try {
      setAgenda(await getDailyAgenda(Number(employeeId), date))
    } catch (searchError) {
      setError(getErrorMessage(searchError))
    } finally {
      setLoading(false)
    }
  }

  const clientName = (id: number) => { const client = clients.find((item) => item.id === id); return client ? `${client.firstName} ${client.lastName}` : `Cliente #${id}` }
  const serviceName = (id: number) => services.find((item) => item.id === id)?.name ?? `Servicio #${id}`

  return (
    <section>
      <SectionTitle eyebrow="Planificación" title="Agenda diaria" description="Consulta las citas de un profesional para una fecha concreta." />
      <div className="agenda-toolbar">
        <form onSubmit={handleSearch}>
          <label>Empleado<select required value={employeeId} onChange={(event) => setEmployeeId(event.target.value)}><option value="">Selecciona un empleado</option>{employees.map((employee) => <option key={employee.id} value={employee.id}>{employee.firstName} {employee.lastName}</option>)}</select></label>
          <label>Fecha<input required type="date" value={date} onChange={(event) => setDate(event.target.value)} /></label>
          <button className="button button--primary" disabled={loading} type="submit">{loading ? 'Consultando…' : 'Ver agenda'}</button>
        </form>
      </div>
      <FeedbackMessage message={error} tone="error" />
      <div className="timeline">
        {agenda.length === 0 ? <div className="empty-state empty-state--wide"><strong>Sin citas para mostrar</strong><span>Elige un empleado y una fecha para consultar su agenda.</span></div> : agenda.map((appointment) => (
          <article className="timeline-item" key={appointment.id}>
            <time>{new Date(appointment.startDateTime).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })}</time>
            <div><strong>{serviceName(appointment.serviceId)}</strong><span>{clientName(appointment.clientId)}</span>{appointment.notes && <small>{appointment.notes}</small>}</div>
            <span className={`status status--${appointment.status.toLowerCase()}`}>{appointment.status}</span>
          </article>
        ))}
      </div>
    </section>
  )
}

export default AgendaSection
