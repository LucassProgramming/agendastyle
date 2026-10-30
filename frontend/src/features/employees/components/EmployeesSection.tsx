import { useState, type FormEvent } from 'react'
import { getErrorMessage } from '../../../shared/api/http'
import FeedbackMessage from '../../../shared/components/FeedbackMessage'
import SectionTitle from '../../../shared/components/SectionTitle'
import type { SalonService } from '../../services/types/SalonService'
import { createEmployee, createEmployeeSchedule, getEmployeeSchedules } from '../api/employeesApi'
import type { DayOfWeek, Employee, EmployeeSchedule } from '../types/Employee'

type EmployeesSectionProps = { employees: Employee[]; services: SalonService[]; onCreated: (employee: Employee) => void }
const initialEmployee = { firstName: '', lastName: '', phone: '', email: '', serviceIds: [] as number[] }
const weekDays: { value: DayOfWeek; label: string }[] = [
  { value: 'MONDAY', label: 'Lunes' }, { value: 'TUESDAY', label: 'Martes' },
  { value: 'WEDNESDAY', label: 'Miércoles' }, { value: 'THURSDAY', label: 'Jueves' },
  { value: 'FRIDAY', label: 'Viernes' }, { value: 'SATURDAY', label: 'Sábado' },
  { value: 'SUNDAY', label: 'Domingo' },
]

function EmployeesSection({ employees, services, onCreated }: EmployeesSectionProps) {
  const [form, setForm] = useState(initialEmployee)
  const [selectedEmployeeId, setSelectedEmployeeId] = useState('')
  const [schedules, setSchedules] = useState<EmployeeSchedule[]>([])
  const [scheduleForm, setScheduleForm] = useState({ dayOfWeek: 'MONDAY' as DayOfWeek, startTime: '09:00', endTime: '17:00' })
  const [submitting, setSubmitting] = useState(false)
  const [scheduleSubmitting, setScheduleSubmitting] = useState(false)
  const [feedback, setFeedback] = useState<{ message: string; tone: 'error' | 'success' } | null>(null)
  const [scheduleFeedback, setScheduleFeedback] = useState<{ message: string; tone: 'error' | 'success' } | null>(null)

  function toggleService(serviceId: number) {
    setForm((current) => ({ ...current, serviceIds: current.serviceIds.includes(serviceId) ? current.serviceIds.filter((id) => id !== serviceId) : [...current.serviceIds, serviceId] }))
  }

  async function handleEmployeeSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setFeedback(null)
    try {
      const employee = await createEmployee(form)
      onCreated(employee)
      setForm(initialEmployee)
      setFeedback({ message: 'Empleado creado correctamente.', tone: 'success' })
    } catch (error) {
      setFeedback({ message: getErrorMessage(error), tone: 'error' })
    } finally {
      setSubmitting(false)
    }
  }

  async function selectEmployee(employeeId: string) {
    setSelectedEmployeeId(employeeId)
    setScheduleFeedback(null)
    if (!employeeId) return setSchedules([])
    try {
      setSchedules(await getEmployeeSchedules(Number(employeeId)))
    } catch (error) {
      setScheduleFeedback({ message: getErrorMessage(error), tone: 'error' })
    }
  }

  async function handleScheduleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!selectedEmployeeId) return
    setScheduleSubmitting(true)
    setScheduleFeedback(null)
    try {
      const schedule = await createEmployeeSchedule(Number(selectedEmployeeId), scheduleForm)
      setSchedules((current) => [...current, schedule])
      setScheduleFeedback({ message: 'Horario añadido correctamente.', tone: 'success' })
    } catch (error) {
      setScheduleFeedback({ message: getErrorMessage(error), tone: 'error' })
    } finally {
      setScheduleSubmitting(false)
    }
  }

  return (
    <section>
      <SectionTitle eyebrow="Equipo" title="Empleados y horarios" description="Asigna servicios a cada profesional y configura su jornada laboral." />
      <div className="workspace-grid">
        <form className="form-panel" onSubmit={handleEmployeeSubmit}>
          <h3>Nuevo empleado</h3>
          <div className="form-row">
            <label>Nombre<input required maxLength={100} value={form.firstName} onChange={(event) => setForm({ ...form, firstName: event.target.value })} /></label>
            <label>Apellidos<input required maxLength={100} value={form.lastName} onChange={(event) => setForm({ ...form, lastName: event.target.value })} /></label>
          </div>
          <label>Teléfono<input required maxLength={30} type="tel" value={form.phone} onChange={(event) => setForm({ ...form, phone: event.target.value })} /></label>
          <label>Correo electrónico<input required maxLength={150} type="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} /></label>
          <fieldset>
            <legend>Servicios que realiza</legend>
            {services.length === 0 ? <span className="field-hint">Crea al menos un servicio antes de registrar empleados.</span> : (
              <div className="check-list">{services.map((service) => <label className="check-option" key={service.id}><input checked={form.serviceIds.includes(service.id)} onChange={() => toggleService(service.id)} type="checkbox" />{service.name}</label>)}</div>
            )}
          </fieldset>
          <button className="button button--primary" disabled={submitting || form.serviceIds.length === 0} type="submit">{submitting ? 'Guardando…' : 'Crear empleado'}</button>
          <FeedbackMessage message={feedback?.message ?? null} tone={feedback?.tone} />
        </form>

        <div className="content-panel">
          <div className="panel-header"><h3>Equipo activo</h3><span className="count">{employees.length}</span></div>
          {employees.length === 0 ? <div className="empty-state"><strong>Aún no hay empleados</strong><span>Asigna servicios al crear el primer miembro del equipo.</span></div> : (
            <div className="list">{employees.map((employee) => (
              <article className="list-item" key={employee.id}>
                <div className="avatar avatar--dark">{employee.firstName[0]}{employee.lastName[0]}</div>
                <div className="list-item__content"><strong>{employee.firstName} {employee.lastName}</strong><span>{employee.email} · {employee.phone}</span></div>
                <span className="service-count">{employee.serviceIds.length} servicios</span>
              </article>
            ))}</div>
          )}
        </div>
      </div>

      <div className="schedule-panel">
        <div className="panel-header"><div><h3>Horario semanal</h3><p>Selecciona un empleado para consultar y ampliar su disponibilidad.</p></div></div>
        <label className="select-employee">Empleado<select value={selectedEmployeeId} onChange={(event) => void selectEmployee(event.target.value)}><option value="">Selecciona un empleado</option>{employees.map((employee) => <option key={employee.id} value={employee.id}>{employee.firstName} {employee.lastName}</option>)}</select></label>
        {selectedEmployeeId && (
          <>
            <form className="schedule-form" onSubmit={handleScheduleSubmit}>
              <label>Día<select value={scheduleForm.dayOfWeek} onChange={(event) => setScheduleForm({ ...scheduleForm, dayOfWeek: event.target.value as DayOfWeek })}>{weekDays.map((day) => <option key={day.value} value={day.value}>{day.label}</option>)}</select></label>
              <label>Desde<input required type="time" value={scheduleForm.startTime} onChange={(event) => setScheduleForm({ ...scheduleForm, startTime: event.target.value })} /></label>
              <label>Hasta<input required type="time" value={scheduleForm.endTime} onChange={(event) => setScheduleForm({ ...scheduleForm, endTime: event.target.value })} /></label>
              <button className="button button--secondary" disabled={scheduleSubmitting} type="submit">{scheduleSubmitting ? 'Añadiendo…' : 'Añadir horario'}</button>
            </form>
            <FeedbackMessage message={scheduleFeedback?.message ?? null} tone={scheduleFeedback?.tone} />
            {schedules.length === 0 ? <p className="field-hint">Este empleado todavía no tiene horarios configurados.</p> : <div className="schedule-list">{schedules.map((schedule) => <div className="schedule-chip" key={schedule.id}><strong>{weekDays.find((day) => day.value === schedule.dayOfWeek)?.label}</strong><span>{schedule.startTime.slice(0, 5)}–{schedule.endTime.slice(0, 5)}</span></div>)}</div>}
          </>
        )}
      </div>
    </section>
  )
}

export default EmployeesSection
