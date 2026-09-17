import type { SalonService } from '../types/SalonService'
import SectionTitle from './SectionTitle'

type ServicesSectionProps = {
  services: SalonService[]
}

function ServicesSection({ services }: ServicesSectionProps) {
  return (
  <section>
    <SectionTitle title="Services" />

    {services.length === 0 ? (
      <p>No services available.</p>
    ) : (
      <div className="card-grid">
        {services.map((service) => (
          <article className="card" key={service.id}>
            <div className="card-header">
              <h3>{service.name}</h3>
              <span className="price">
                {service.price.toFixed(2)} €
              </span>
            </div>

            <p>{service.description}</p>

            <div className="card-footer">
              <span>{service.durationMinutes} min</span>
            </div>
          </article>
        ))}
      </div>
    )}
  </section>
)
}

export default ServicesSection