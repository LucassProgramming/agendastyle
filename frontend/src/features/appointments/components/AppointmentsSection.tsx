import { useMemo, useState, type FormEvent } from 'react'
import { getErrorMessage } from '../../../shared/api/http'
import FeedbackMessage from '../../../shared/components/FeedbackMessage'
import SectionTitle from '../../../shared/components/SectionTitle'
import type { Client } from '../../clients/types/Client'
import type { Employee } from '../../employees/types/Employee'
import type { SalonService } from '../../services/types/SalonService'
import { cancelAppointment, createAppointment } from '../api/appointmentsApi'
import type { Appointment, AppointmentStatus } from '../types/Appointment'

type AppointmentsSectionProps = {
  appointments: Appointment[]
  clients: Client[]
  employees: Employee[]
  services: SalonService[]
  onCreated: (appointment: Appointment) => void
  onUpdated: (appointment: Appointment) => void
}

const statusLabels: Record<AppointmentStatus, string> = { CONFIRMED: 'Confirmada', COMPLETED: 'Completada', CANCELLED: 'Cancelada', NO_SHOW: 'No asistió' }

function AppointmentsSection({ appointments, clients, employees, services, onCreated, onUpdated }: AppointmentsSectionProps) {
  const [form, setForm] = useState({ clientId: '', employeeId: '', serviceId: '', startDateTime: '', notes: '' })
  const [submitting, setSubmitting] = useState(false)
  const [cancellingId, setCancellingId] = useState<number | null>(null)
  const [feedback, setFeedback] = useState<{ message: string; tone: 'error' | 'success' } | null>(null)
  const availableServices = useMemo(() => {
    const employee = employees.find((item) => item.id === Number(form.employeeId))
    return employee ? services.filter((service) => employee.serviceIds.includes(service.id)) : []
  }, [employees, form.employeeId, services])
  const sortedAppointments = [...appointments].sort((a, b) => new Date(b.startDateTime).getTime() - new Date(a.startDateTime).getTime())
  const clientName = (id: number) => { const client = clients.find((item) => item.id === id); return client ? `${client.firstName} ${client.lastName}` : `Cliente #${id}` }
  const employeeName = (id: number) => { const employee = employees.find((item) => item.id === id); return employee ? `${employee.firstName} ${employee.lastName}` : `Empleado #${id}` }
  const serviceName = (id: number) => services.find((item) => item.id === id)?.name ?? `Servicio #${id}`

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setFeedback(null)
    try {
      const appointment = await createAppointment({ clientId: Number(form.clientId), employeeId: Number(form.employeeId), serviceId: Number(form.serviceId), startDateTime: form.startDateTime, notes: form.notes.trim() })
      onCreated(appointment)
      setForm({ clientId: '', employeeId: '', serviceId: '', startDateTime: '', notes: '' })
      setFeedback({ message: 'Cita creada correctamente.', tone: 'success' })
    } catch (error) {
      setFeedback({ message: getErrorMessage(error), tone: 'error' })
    } finally {
      setSubmitting(false)
    }
  }

  async function handleCancel(appointmentId: number) {
    setCancellingId(appointmentId)
    setFeedback(null)
    try {
      onUpdated(await cancelAppointment(appointmentId))
      setFeedback({ message: 'Cita cancelada correctamente.', tone: 'success' })
    } catch (error) {
      setFeedback({ message: getErrorMessage(error), tone: 'error' })
    } finally {
      setCancellingId(null)
    }
  }

  const canCreate = clients.length > 0 && employees.length > 0 && services.length > 0

  return (
    <section>
      <SectionTitle eyebrow="Operación diaria" title="Citas" description="Reserva servicios respetando la disponibilidad y el horario de cada profesional." />
      <div className="workspace-grid">
        <form className="form-panel" onSubmit={handleSubmit}>
          <h3>Nueva cita</h3>
          {!canCreate && <FeedbackMessage message="Necesitas al menos un cliente, un empleado y un servicio para crear citas." />}
          <label>Cliente<select required value={form.clientId} onChange={(event) => setForm({ ...form, clientId: event.target.value })}><option value="">Selecciona un cliente</option>{clients.map((client) => <option key={client.id} value={client.id}>{client.firstName} {client.lastName}</option>)}</select></label>
          <label>Empleado<select required value={form.employeeId} onChange={(event) => setForm({ ...form, employeeId: event.target.value, serviceId: '' })}><option value="">Selecciona un empleado</option>{employees.map((employee) => <option key={employee.id} value={employee.id}>{employee.firstName} {employee.lastName}</option>)}</select></label>
          <label>Servicio<select required disabled={!form.employeeId} value={form.serviceId} onChange={(event) => setForm({ ...form, serviceId: event.target.value })}><option value="">Selecciona un servicio</option>{availableServices.map((service) => <option key={service.id} value={service.id}>{service.name} · {service.durationMinutes} min</option>)}</select></label>
          <label>Fecha y hora<input required type="datetime-local" value={form.startDateTime} onChange={(event) => setForm({ ...form, startDateTime: event.target.value })} /></label>
          <label>Notas<textarea maxLength={500} rows={3} value={form.notes} onChange={(event) => setForm({ ...form, notes: event.target.value })} placeholder="Preferencias o información relevante" /></label>
          <button className="button button--primary" disabled={submitting || !canCreate} type="submit">{submitting ? 'Reservando…' : 'Crear cita'}</button>
          <FeedbackMessage message={feedback?.message ?? null} tone={feedback?.tone} />
        </form>

        <div className="content-panel">
          <div className="panel-header"><h3>Historial de citas</h3><span className="count">{appointments.length}</span></div>
          {sortedAppointments.length === 0 ? <div className="empty-state"><strong>Aún no hay citas</strong><span>Las reservas aparecerán aquí cuando crees la primera.</span></div> : (
            <div className="appointment-list">{sortedAppointments.map((appointment) => (
              <article className="appointment" key={appointment.id}>
                <div className="appointment__date"><strong>{new Date(appointment.startDateTime).toLocaleDateString('es-ES', { day: '2-digit', month: 'short' })}</strong><span>{new Date(appointment.startDateTime).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })}</span></div>
                <div className="appointment__content"><strong>{serviceName(appointment.serviceId)}</strong><span>{clientName(appointment.clientId)} con {employeeName(appointment.employeeId)}</span>{appointment.notes && <small>{appointment.notes}</small>}</div>
                <div className="appointment__actions"><span className={`status status--${appointment.status.toLowerCase()}`}>{statusLabels[appointment.status]}</span>{appointment.status === 'CONFIRMED' && <button className="button-link button-link--danger" disabled={cancellingId === appointment.id} onClick={() => void handleCancel(appointment.id)} type="button">{cancellingId === appointment.id ? 'Cancelando…' : 'Cancelar'}</button>}</div>
              </article>
            ))}</div>
          )}
        </div>
      </div>
    </section>
  )
}

export default AppointmentsSection
