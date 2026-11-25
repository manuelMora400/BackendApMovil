# Backend ElectricAPP

Backend de **ElectricAPP**, la app móvil para **Induztek**. Está hecho con microservicios en Spring Boot 4.1.2, Java 21, Spring Cloud 2025.1.4 y MySQL.

App móvil: https://github.com/manuelMora400/ElectricAPP

## Microservicios

| Carpeta | Nombre en Eureka | Puerto | Base de datos |
|---|---|---|---|
| `eureka` | eureka | 8761 | - |
| `api-gateway` | api-gateway | 8081 | - |
| `ms_roles` | ms-usuarios | 8080 | `bd_roles` |
| `instrumentos` | instrumentos | 8082 | `bd_instrumento` |
| `ms-protocolo` | ms-protocolo | 8083 | `bd_protocolo` |
| `equipos` | ms-equipos | 8084 | `bd_equipos` |

La app solo habla con el **API Gateway (8081)**, y el gateway reparte las peticiones:

- `/api/usuarios/**` → ms-usuarios
- `/api/instrumentos/**` → instrumentos
- `/api/equipos/**` → ms-equipos
- `/api/protocolos/**` y `/api/aislacion/**` → ms-protocolo

## Requisitos

- Java 21
- MySQL en `localhost:3306` con usuario `root`. La clave está en el `application.properties` de cada microservicio.

No hay que crear las bases de datos a mano: cada microservicio las crea al partir y les carga datos de prueba.

## Cómo levantarlo

Se levanta en este orden, cada uno en su propia terminal:

1. `eureka`
2. `ms_roles`, `instrumentos`, `equipos` y `ms-protocolo`
3. `api-gateway`

En cada carpeta se corre:

```bash
./mvnw spring-boot:run
```

Para ver los servicios registrados: http://localhost:8761

## Documentación (Swagger)

Cada microservicio tiene su Swagger en `http://localhost:<puerto>/swagger-ui.html`.

## Tests y cobertura

En `ms_roles`, `instrumentos`, `equipos` y `ms-protocolo` se corre:

```bash
./mvnw verify
```

Esto corre los tests unitarios (JUnit 5 + Mockito) y genera el reporte de JaCoCo en `target/site/jacoco/index.html`. La cobertura mínima configurada es 80%.
