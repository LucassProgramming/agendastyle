import { useState, type FormEvent } from 'react'
import { getErrorMessage } from '../../../shared/api/http'
import FeedbackMessage from '../../../shared/components/FeedbackMessage'
import SectionTitle from '../../../shared/components/SectionTitle'
import { createClient } from '../api/clientsApi'
import type { Client } from '../types/Client'

type ClientsSectionProps = { clients: Client[]; onCreated: (client: Client) => void }
const initialForm = { firstName: '', lastName: '', phone: '', email: '' }

function ClientsSection({ clients, onCreated }: ClientsSectionProps) {
  const [form, setForm] = useState(initialForm)
  const [submitting, setSubmitting] = useState(false)
  const [feedback, setFeedback] = useState<{ message: string; tone: 'error' | 'success' } | null>(null)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setFeedback(null)
    try {
      const client = await createClient(form)
      onCreated(client)
      setForm(initialForm)
      setFeedback({ message: 'Cliente registrado correctamente.', tone: 'success' })
    } catch (error) {
      setFeedback({ message: getErrorMessage(error), tone: 'error' })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <SectionTitle eyebrow="Relación con clientes" title="Clientes" description="Registra los datos de contacto necesarios para gestionar sus citas." />
      <div className="workspace-grid">
        <form className="form-panel" onSubmit={handleSubmit}>
          <h3>Nuevo cliente</h3>
          <div className="form-row">
            <label>Nombre<input required maxLength={100} value={form.firstName} onChange={(event) => setForm({ ...form, firstName: event.target.value })} /></label>
            <label>Apellidos<input required maxLength={100} value={form.lastName} onChange={(event) => setForm({ ...form, lastName: event.target.value })} /></label>
          </div>
          <label>Teléfono<input required maxLength={30} type="tel" value={form.phone} onChange={(event) => setForm({ ...form, phone: event.target.value })} /></label>
          <label>Correo electrónico<input required maxLength={150} type="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} /></label>
          <button className="button button--primary" disabled={submitting} type="submit">{submitting ? 'Guardando…' : 'Registrar cliente'}</button>
          <FeedbackMessage message={feedback?.message ?? null} tone={feedback?.tone} />
        </form>
        <div className="content-panel">
          <div className="panel-header"><h3>Directorio de clientes</h3><span className="count">{clients.length}</span></div>
          {clients.length === 0 ? <div className="empty-state"><strong>Aún no hay clientes</strong><span>Registra un cliente antes de crear una cita.</span></div> : (
            <div className="list">{clients.map((client) => (
              <article className="list-item" key={client.id}>
                <div className="avatar">{client.firstName[0]}{client.lastName[0]}</div>
                <div className="list-item__content"><strong>{client.firstName} {client.lastName}</strong><span>{client.email}</span></div>
                <span className="list-item__meta">{client.phone}</span>
              </article>
            ))}</div>
          )}
        </div>
      </div>
    </section>
  )
}

export default ClientsSection
