# MecaBot Classifier

Microservicio desarrollado con Spring Boot 3.3 y Java 17 para la clasificación automática de mensajes de clientes en un taller mecánico.

## Características

- Endpoint POST `/classify` para clasificar mensajes de texto
- Uso de OpenAI para clasificación mediante IA
- Fallback a búsqueda en base de datos PostgreSQL cuando la IA no reconoce la categoría
- Categorización en formato snake_case y estimación de tiempo en minutos

## Tecnologías

- Java 17
- Spring Boot 3.3
- Spring Data JPA
- PostgreSQL
- RestTemplate para integración con OpenAI

## Configuración

### Requisitos previos

- JDK 17
- Maven
- Docker (opcional, para la base de datos)

### Variables de entorno

- `OPENAI_API_KEY`: Clave API de OpenAI

### Base de datos

El proyecto incluye un archivo `docker-compose.yml` para levantar una instancia de PostgreSQL:

```bash
docker-compose up -d
```

### Ejecución

```bash
mvn spring-boot:run
```

## Uso

```bash
curl -X POST http://localhost:8080/classify \
     -H "Content-Type: application/json" \
     -d '{"message": "Necesito un cambio de aceite"}'
```

Respuesta:

```json
{
  "category": "cambio_aceite",
  "minutes": 30
}
```

## Licencia

[MIT](LICENSE)
