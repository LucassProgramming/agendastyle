type NavigationProps = {
  section: 'services' | 'employees'
  onSectionChange: (section: 'services' | 'employees') => void
}

function Navigation({ section, onSectionChange }: NavigationProps) {
  return (
    <nav>
      <button
        onClick={() => onSectionChange('services')}
        disabled={section === 'services'}
      >
        Services
      </button>

      <button
        onClick={() => onSectionChange('employees')}
        disabled={section === 'employees'}
      >
        Employees
      </button>
    </nav>
  )
}

export default Navigation