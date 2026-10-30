export type AppSection = 'appointments' | 'agenda' | 'clients' | 'employees' | 'services'

type NavigationProps = { section: AppSection; onSectionChange: (section: AppSection) => void }

const items: { id: AppSection; label: string }[] = [
  { id: 'appointments', label: 'Citas' },
  { id: 'agenda', label: 'Agenda' },
  { id: 'clients', label: 'Clientes' },
  { id: 'employees', label: 'Equipo' },
  { id: 'services', label: 'Servicios' },
]

function Navigation({ section, onSectionChange }: NavigationProps) {
  return (
    <nav aria-label="Navegación principal" className="main-nav">
      {items.map((item) => (
        <button className={section === item.id ? 'is-active' : ''} key={item.id} onClick={() => onSectionChange(item.id)} type="button">
          {item.label}
        </button>
      ))}
    </nav>
  )
}

export default Navigation
