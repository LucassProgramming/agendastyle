type HeaderProps = {
  title: string
  subtitle: string
  counts: { appointments: number; clients: number; employees: number }
}

function Header({ title, subtitle, counts }: HeaderProps) {
  return (
    <header>
      <div className="header-content">
        <div>
          <span className="brand-mark">AS</span>
          <p className="header-kicker">Panel de gestión</p>
          <h1>{title}</h1>
          <p>{subtitle}</p>
        </div>
        <dl className="header-stats">
          <div><dt>Citas</dt><dd>{counts.appointments}</dd></div>
          <div><dt>Clientes</dt><dd>{counts.clients}</dd></div>
          <div><dt>Equipo</dt><dd>{counts.employees}</dd></div>
        </dl>
      </div>
    </header>
  )
}

export default Header
