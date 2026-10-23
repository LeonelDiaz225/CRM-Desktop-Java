# CRM Desktop Java

Aplicación de escritorio para gestionar clientes, equipos informáticos y órdenes de reparación. El sistema está pensado para un pequeño taller o servicio técnico que necesita centralizar la información de sus clientes, registrar los equipos recibidos y controlar el estado de cada reparación desde una interfaz gráfica.

> Proyecto desarrollado con Java 21, JavaFX, Spring Boot, Spring Data JPA y MySQL.

## Funcionalidades principales

### Dashboard

La pantalla inicial funciona como panel de control del taller e incluye:

- Cantidad de clientes registrados.
- Cantidad de reparaciones activas.
- Listado de las últimas reparaciones recibidas.
- Accesos directos a los módulos de clientes, equipos y reparaciones.
- Mensajes de error cuando la base de datos no está disponible o la configuración es incorrecta.

Las reparaciones entregadas no se consideran activas. El dashboard muestra hasta diez reparaciones ordenadas por fecha de recepción, de la más reciente a la más antigua.

### Gestión de clientes

El módulo de clientes permite:

- Consultar todos los clientes registrados.
- Buscar clientes por nombre, sin distinguir mayúsculas y minúsculas.
- Crear nuevos clientes.
- Editar un cliente seleccionándolo desde la tabla.
- Eliminar clientes con confirmación previa.
- Guardar nombre, teléfono, correo electrónico y dirección.

El nombre del cliente es obligatorio. Los equipos y reparaciones se relacionan con el cliente correspondiente.

### Gestión de equipos

El módulo de equipos permite administrar los dispositivos pertenecientes a los clientes:

- Registrar el tipo de equipo, por ejemplo PC o notebook.
- Registrar marca, modelo y número de serie.
- Asociar el equipo a un cliente existente.
- Buscar equipos por número de serie.
- Editar y eliminar equipos.
- Consultar el cliente propietario desde la tabla.

El tipo de equipo y el cliente son obligatorios. El número de serie, cuando se informa, debe ser único en la base de datos.

### Gestión de reparaciones

El módulo de reparaciones permite registrar y controlar las órdenes de trabajo:

- Seleccionar el cliente y el equipo involucrado.
- Registrar la descripción del problema.
- Registrar el diagnóstico técnico.
- Seleccionar el estado de la reparación.
- Indicar las fechas de recepción y finalización.
- Registrar el costo del servicio.
- Consultar las reparaciones desde una tabla.
- Editar y eliminar órdenes de reparación.

Los estados disponibles son:

1. `RECIBIDO`
2. `DIAGNOSTICO`
3. `EN_PROGRESO`
4. `ESPERA_REPUESTO`
5. `FINALIZADO`
6. `ENTREGADO`

Si no se indica un estado, se utiliza `RECIBIDO`. Si no se indica una fecha de recepción, se asigna la fecha actual. El costo no puede ser negativo.

## Tecnologías utilizadas

- **Java 21**: lenguaje principal del proyecto.
- **JavaFX 21.0.1**: construcción de la interfaz gráfica de escritorio.
- **FXML**: definición declarativa de las pantallas JavaFX.
- **Spring Boot 3.2.4**: configuración de la aplicación y contenedor de dependencias.
- **Spring Data JPA**: persistencia y acceso a datos.
- **Hibernate**: implementación JPA y generación/actualización del esquema.
- **MySQL**: base de datos relacional.
- **Lombok 1.18.36**: reducción de código repetitivo en las entidades.
- **Maven**: gestión de dependencias y ejecución del proyecto.

## Arquitectura del proyecto

El código está organizado en capas, separando la interfaz, la lógica de negocio y el acceso a datos:

```text
CRM-Desktop-Java/
├── pom.xml
├── src/
│   └── main/
│       ├── java/org/example/crmdesktop/
│       │   ├── CrmDesktopApplication.java
│       │   ├── controller/
│       │   │   ├── ClientController.java
│       │   │   ├── DashboardController.java
│       │   │   ├── EquipmentController.java
│       │   │   └── RepairController.java
│       │   ├── model/
│       │   │   ├── Client.java
│       │   │   ├── Equipment.java
│       │   │   ├── Repair.java
│       │   │   ├── RepairStatus.java
│       │   │   └── User.java
│       │   ├── repository/
│       │   │   ├── ClientRepository.java
│       │   │   ├── EquipmentRepository.java
│       │   │   ├── RepairRepository.java
│       │   │   └── UserRepository.java
│       │   └── service/
│       │       ├── ClientService.java
│       │       ├── DashboardService.java
│       │       ├── EquipmentService.java
│       │       ├── NavigationService.java
│       │       └── RepairService.java
│       └── resources/
│           ├── application.properties.example
│           └── fxml/
│               ├── clients.fxml
│               ├── dashboard.fxml
│               ├── equipment.fxml
│               └── repairs.fxml
└── .gitignore
```

### Punto de entrada

`CrmDesktopApplication` combina el ciclo de vida de JavaFX con el contexto de Spring Boot:

1. JavaFX inicia la aplicación.
2. En `init()` se crea el contexto de Spring.
3. En `start()` se obtiene el `NavigationService` y se configura la ventana principal.
4. Se carga el dashboard como primera pantalla.
5. En `stop()` se cierra el contexto de Spring y la plataforma JavaFX.

### Controladores

Los controladores conectan los archivos FXML con los servicios de la aplicación. Implementan las acciones de los botones, cargan las tablas, gestionan la selección de registros y muestran alertas de información, confirmación o error.

- `DashboardController`: carga los indicadores y las reparaciones recientes.
- `ClientController`: alta, edición, búsqueda y eliminación de clientes.
- `EquipmentController`: alta, edición, búsqueda y eliminación de equipos.
- `RepairController`: creación y mantenimiento de órdenes de reparación.

Los controladores son componentes administrados por Spring, por lo que reciben sus dependencias mediante inyección por constructor.

### Servicios

Los servicios concentran la lógica de negocio y validación:

- `ClientService`: consulta, búsqueda, guardado, validación y eliminación de clientes.
- `EquipmentService`: consulta de equipos, búsqueda por número de serie y filtrado por cliente.
- `RepairService`: gestión de reparaciones, estados, fechas y costos.
- `DashboardService`: cálculo de indicadores y consulta de reparaciones recientes.
- `NavigationService`: carga de vistas FXML y cambio de escenas dentro de la ventana principal.

Las operaciones de lectura se marcan como transacciones de solo lectura y las operaciones de modificación utilizan transacciones de escritura.

### Repositorios

Los repositorios extienden `JpaRepository`, por lo que Spring Data genera automáticamente las operaciones CRUD básicas. También se utilizan consultas derivadas por nombre para buscar:

- Clientes por nombre.
- Equipos por cliente.
- Equipos por número de serie.
- Reparaciones por cliente.
- Reparaciones por estado.
- Reparaciones recientes.
- Cantidad de reparaciones que todavía no fueron entregadas.

## Modelo de datos

La aplicación trabaja con cuatro entidades principales:

```text
Client 1 ──────── N Equipment
Client 1 ──────── N Repair
Equipment 1 ──── N Repair
User 1 ────────── N Repair
```

### `Client`

Representa a la persona o empresa propietaria de uno o más equipos.

Campos principales:

- `id`
- `name`
- `phone`
- `email`
- `address`
- `createdAt`
- `updatedAt`

Un cliente puede tener varios equipos y varias reparaciones.

### `Equipment`

Representa un equipo registrado en el sistema.

Campos principales:

- `id`
- `type`
- `brand`
- `model`
- `serialNumber`
- `client`
- `createdAt`
- `updatedAt`

Cada equipo pertenece obligatoriamente a un cliente y puede tener varias reparaciones históricas.

### `Repair`

Representa una orden de reparación.

Campos principales:

- `id`
- `problemDescription`
- `technicalDiagnosis`
- `status`
- `cost`
- `receivedDate`
- `completedDate`
- `equipment`
- `client`
- `technician`
- `createdAt`
- `updatedAt`

La reparación se relaciona obligatoriamente con un cliente y un equipo. El técnico es opcional en el modelo actual.

### `User`

Representa a un usuario o técnico del sistema. Actualmente la entidad y el repositorio están preparados para almacenar usuarios y asociarlos a reparaciones, aunque el proyecto no incluye una pantalla de autenticación ni un módulo visual de administración de usuarios.

## Configuración de la base de datos

El repositorio incluye `src/main/resources/application.properties.example`. Para configurar una instalación local:

1. Crear una base de datos MySQL llamada `gestion_pc`.
2. Copiar el archivo de ejemplo como `application.properties`.
3. Completar la contraseña del usuario de MySQL.
4. Verificar el puerto configurado.

Ejemplo de configuración:

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3307/gestion_pc?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=CAMBIAR_ESTA_CONTRASEÑA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

> El ejemplo utiliza el puerto `3307`. Si MySQL está instalado en el puerto estándar, normalmente `3306`, hay que ajustar la URL de conexión.

La propiedad `spring.jpa.hibernate.ddl-auto=update` permite que Hibernate actualice las tablas a partir de las entidades. Para entornos productivos se recomienda utilizar migraciones versionadas y no depender de esta opción.

## Requisitos previos

- JDK 21.
- Maven 3.8 o superior.
- MySQL en ejecución.
- Base de datos `gestion_pc` creada.
- Usuario de MySQL con permisos sobre la base de datos.
- Soporte gráfico para ejecutar JavaFX.

## Instalación y ejecución

Clonar el repositorio:

```bash
git clone https://github.com/LeonelDiaz225/CRM-Desktop-Java.git
cd CRM-Desktop-Java
```

Crear la base de datos:

```sql
CREATE DATABASE gestion_pc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Preparar la configuración:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

En Windows, el equivalente puede hacerse copiando manualmente el archivo desde el explorador o con:

```powershell
Copy-Item src/main/resources/application.properties.example src/main/resources/application.properties
```

Editar `application.properties` y establecer las credenciales reales de MySQL.

Compilar el proyecto:

```bash
mvn clean package
```

Ejecutar la aplicación JavaFX mediante el plugin configurado en Maven:

```bash
mvn javafx:run
```

El plugin JavaFX está configurado para utilizar como clase principal:

```text
org.example.crmdesktop.CrmDesktopApplication
```

## Flujo de uso recomendado

1. Abrir la aplicación y comprobar que el dashboard carga correctamente.
2. Crear un cliente desde **Gestionar clientes**.
3. Registrar uno o más equipos y asociarlos al cliente.
4. Crear una reparación seleccionando el cliente y el equipo.
5. Completar el problema, diagnóstico, estado, fechas y costo.
6. Actualizar el estado a medida que avanza el trabajo.
7. Marcar la reparación como `ENTREGADO` cuando el equipo haya sido devuelto.
8. Consultar el dashboard para revisar la actividad general del taller.

## Decisiones técnicas destacadas

- La interfaz está separada de la persistencia mediante controladores, servicios y repositorios.
- La navegación se concentra en `NavigationService`, evitando duplicar la lógica para cambiar escenas.
- Las vistas FXML definen la estructura visual y los controladores manejan el comportamiento.
- Las entidades usan relaciones JPA `@OneToMany` y `@ManyToOne` para representar clientes, equipos y reparaciones.
- Se utiliza carga `LAZY` en las relaciones para evitar cargar todos los datos relacionados innecesariamente.
- Se excluyen las relaciones de `toString()` y `equals/hashCode` mediante Lombok para reducir problemas con ciclos entre entidades.
- Las fechas de creación y modificación se completan automáticamente mediante `@CreationTimestamp` y `@UpdateTimestamp`.
- Las validaciones básicas se realizan antes de guardar información en la base de datos.
- Las credenciales se mantienen fuera del control de versiones mediante `application.properties` y `.gitignore`.

## Estado actual y posibles mejoras

El proyecto implementa un sistema funcional de escritorio para el seguimiento básico de reparaciones. Como líneas de evolución se pueden considerar:

- Incorporar login y autorización por roles utilizando la entidad `User`.
- Agregar un servicio específico para usuarios y técnicos.
- Añadir filtros por estado, fechas y cliente en el módulo de reparaciones.
- Evitar que se eliminen clientes o equipos que tengan registros relacionados, mostrando mensajes más claros al usuario.
- Incorporar validaciones de formato para email, teléfono, fechas y números de serie.
- Agregar estilos CSS externos para separar la apariencia de los archivos FXML.
- Crear pruebas unitarias para servicios y pruebas de integración para repositorios.
- Incorporar migraciones de base de datos con Flyway o Liquibase.
- Crear reportes o comprobantes de reparación.
- Preparar empaquetados instalables para Windows, Linux o macOS.

## Pruebas

En la estructura actual del repositorio no se observa un directorio `src/test` con pruebas automatizadas. La validación actual se realiza mediante las comprobaciones de los servicios y el uso manual de la interfaz.

Para agregar pruebas en el futuro, se recomienda cubrir especialmente:

- Validación de clientes sin nombre.
- Validación de equipos sin tipo o sin cliente.
- Validación de reparaciones sin cliente, equipo o descripción.
- Rechazo de costos negativos.
- Búsquedas por nombre y número de serie.
- Conteo de reparaciones activas.
- Navegación entre las vistas principales.

## Historial de desarrollo

La evolución reciente del proyecto refleja una construcción incremental:

- Configuración inicial de Spring Boot, JavaFX y Maven.
- Incorporación de la estructura principal.
- Creación de modelos, repositorios, controladores y vistas.
- Integración con la base de datos.
- Implementación del dashboard y los módulos de gestión.
- Correcciones en repositorios, dashboard y módulos.

## Licencia

El repositorio no declara actualmente una licencia de código abierto. Si el proyecto se va a distribuir, conviene añadir un archivo `LICENSE` con la licencia elegida.

## Autor

Proyecto desarrollado por **Leonel Diaz**.
