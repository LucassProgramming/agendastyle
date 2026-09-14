import type { SalonService } from '../types/SalonService'

type ServicesSectionProps = {
  services: SalonService[]
}

function ServicesSection({ services }: ServicesSectionProps) {
  return (
    <section>
      <h2>Services</h2>

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