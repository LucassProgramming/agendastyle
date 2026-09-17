type HeaderProps = {
  title: string
  subtitle: string
}

function Header({ title, subtitle }: HeaderProps) {
  return (
    <header>
      <div className="header-content">
        <h1>{title}</h1>
        <p>{subtitle}</p>
      </div>
    </header>
  )
}

export default Header