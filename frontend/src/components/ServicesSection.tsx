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
        services.map((service) => (
          <article key={service.id}>
            <h3>{service.name}</h3>
            <p>{service.description}</p>
            <p>{service.durationMinutes} min</p>
            <p>{service.price} €</p>
          </article>
        ))
      )}
    </section>
  )
}

export default ServicesSection