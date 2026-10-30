# Estructura del proyecto

AgendaStyle es un monorepositorio con tres unidades: [backend](../backend/),
[frontend](../frontend/) e [infraestructura](../infrastructure/). Cada una conserva
su herramienta de construcción y su configuración.

```text
agendastyle/
├── .github/workflows/       # Integración y despliegue continuos
├── backend/                # API Spring Boot y pruebas Maven
├── frontend/
│   └── src/
│       ├── main.tsx         # Entrada de React
│       ├── app/            # Composición, navegación y estilos globales
│       │   └── components/ # Cabecera y navegación de la aplicación
│       ├── features/
│       │   ├── services/   # API, componentes y tipos de servicios
│       │   └── employees/  # API, componentes y tipos de empleados
│       ├── shared/
│       │   └── components/ # Componentes reutilizables entre funcionalidades
│       └── assets/         # Recursos gráficos
├── infrastructure/
│   └── terraform/          # Recursos AWS
├── docs/                   # Arquitectura, decisiones y operaciones
├── docker-compose.yml      # Entorno local
└── docker-compose.prod.yml # Entorno de producción
```

## Frontend: organización por funcionalidad

Cada funcionalidad reúne sus componentes, llamadas HTTP y tipos. Por ejemplo,
los cambios de servicios se encuentran en [features/services](../frontend/src/features/services/).
Esto evita repartir una misma funcionalidad entre varias carpetas globales.

- [app](../frontend/src/app/) compone las funcionalidades y gestiona la navegación.
- Las funcionalidades pueden utilizar [shared](../frontend/src/shared/), pero no
  deben importar elementos de `app`.
- `shared` contiene código reutilizable sin dependencias de funcionalidades concretas.
- Mantén un componente dentro de su funcionalidad hasta que exista una necesidad
  real de compartirlo. No es necesario crear carpetas vacías por anticipado.
- Los tipos de respuesta y las llamadas HTTP de cada recurso permanecen juntos
  dentro de su funcionalidad.

## Backend: organización por dominio

El [paquete principal](../backend/src/main/java/com/agendastyle/backend/) ya agrupa
el código por dominio: `appointment`, `catalog`, `client` y `staff`.
Dentro de cada dominio se encuentran controladores, servicios de aplicación,
entidades, repositorios, DTO y excepciones.

- Los controladores atienden HTTP; los servicios de aplicación coordinan las reglas
  de negocio; los repositorios acceden a los datos.
- `dto` contiene los contratos de entrada y salida; `exception` contiene excepciones.
- La ruta de cada archivo Java debe coincidir con su declaración `package`.
- `common` reúne la configuración transversal y la traducción de excepciones a HTTP.
- Las pruebas se sitúan en `src/test/java`, siguiendo los paquetes del código probado.

## Infraestructura y operaciones

Los Dockerfiles siguen junto a cada aplicación. Los archivos Compose permanecen
en la raíz, desde donde coordinan los dos servicios y PostgreSQL. Estas rutas
también son utilizadas por CI y por el despliegue.

Consulta las [instrucciones de desarrollo en Ubuntu](../README.md#local-development) para ejecutar comprobaciones,
las [decisiones técnicas](technical-decisions.md) para el contexto del diseño y la
[documentación de Terraform](terraform-infrastructure.md) para infraestructura.
