import { useState, type FormEvent } from 'react'
import { getErrorMessage } from '../../../shared/api/http'
import FeedbackMessage from '../../../shared/components/FeedbackMessage'
import SectionTitle from '../../../shared/components/SectionTitle'
import { createService } from '../api/servicesApi'
import type { SalonService } from '../types/SalonService'

type ServicesSectionProps = {
  services: SalonService[]
  onCreated: (service: SalonService) => void
}

const initialForm = { name: '', description: '', durationMinutes: '30', price: '' }

function ServicesSection({ services, onCreated }: ServicesSectionProps) {
  const [form, setForm] = useState(initialForm)
  const [submitting, setSubmitting] = useState(false)
  const [feedback, setFeedback] = useState<{ message: string; tone: 'error' | 'success' } | null>(null)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setFeedback(null)
    try {
      const service = await createService({ name: form.name.trim(), description: form.description.trim(), durationMinutes: Number(form.durationMinutes), price: Number(form.price) })
      onCreated(service)
      setForm(initialForm)
      setFeedback({ message: 'Servicio creado correctamente.', tone: 'success' })
    } catch (error) {
      setFeedback({ message: getErrorMessage(error), tone: 'error' })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <SectionTitle eyebrow="Catálogo" title="Servicios" description="Define los tratamientos que ofrece el salón y su duración." />
      <div className="workspace-grid">
        <form className="form-panel" onSubmit={handleSubmit}>
          <h3>Nuevo servicio</h3>
          <label>Nombre<input required maxLength={100} value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} placeholder="Corte y peinado" /></label>
          <label>Descripción<textarea maxLength={500} rows={3} value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} placeholder="Incluye lavado y acabado" /></label>
          <div className="form-row">
            <label>Duración (min)<input required min="1" type="number" value={form.durationMinutes} onChange={(event) => setForm({ ...form, durationMinutes: event.target.value })} /></label>
            <label>Precio (€)<input required min="0" step="0.01" type="number" value={form.price} onChange={(event) => setForm({ ...form, price: event.target.value })} placeholder="25.00" /></label>
          </div>
          <button className="button button--primary" disabled={submitting} type="submit">{submitting ? 'Guardando…' : 'Crear servicio'}</button>
          <FeedbackMessage message={feedback?.message ?? null} tone={feedback?.tone} />
        </form>

        <div className="content-panel">
          <div className="panel-header"><h3>Servicios disponibles</h3><span className="count">{services.length}</span></div>
          {services.length === 0 ? (
            <div className="empty-state"><strong>Aún no hay servicios</strong><span>Crea el primero para poder asignarlo a empleados y citas.</span></div>
          ) : (
            <div className="card-grid">
              {services.map((service) => (
                <article className="card" key={service.id}>
                  <div className="card-header"><h3>{service.name}</h3><span className="price">{Number(service.price).toFixed(2)} €</span></div>
                  <p>{service.description || 'Sin descripción'}</p>
                  <div className="card-footer"><span>{service.durationMinutes} min</span><span className={`status ${service.active ? 'status--active' : ''}`}>{service.active ? 'Activo' : 'Inactivo'}</span></div>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  )
}

export default ServicesSection
