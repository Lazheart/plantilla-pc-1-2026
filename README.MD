# Repositorio Plantilla

 Este repositorio contiene una implementación básica de una entidad con manejo de roles, un sistema de autenticación y el uso de **DTOs (Data Transfer Objects)**.

 El proyecto sigue una **arquitectura por capas**, implementando las siguientes capas principales:

 - **Controller Layer (Capa de Controlador):** Gestiona las solicitudes HTTP y expone los endpoints de la aplicación.
- **Service Layer (Capa de Servicio):** Contiene la lógica de negocio y coordina las operaciones de la aplicación.
- **Repository Layer (Capa de Repositorio):** Se encarga del acceso y la persistencia de los datos.

 Además, el proyecto cuenta con un **sistema de manejo de errores**, que permite gestionar y responder de manera adecuada ante las diferentes excepciones que puedan producirse durante la ejecución de la aplicación.

 En conjunto, esta implementación busca demostrar una estructura organizada y escalable, aplicando principios básicos de **separación de responsabilidades** mediante una arquitectura por capas.

 ## Propósito de la plantilla

 Esta plantilla ha sido creada como punto de partida para la **Práctica 1** del curso **Desarrollo Basado en Plataformas (CS2031)**.

 Su objetivo es proporcionar una estructura base sobre la cual los estudiantes puedan desarrollar y extender las funcionalidades solicitadas para la práctica, manteniendo una organización clara del código y siguiendo buenas prácticas de desarrollo.

 ## Endpoints disponibles

 A continuación, se detallan los endpoints disponibles en la aplicación:

 | Método | Endpoint | Descripción |
| --- | --- | --- |
| `GET` | `/auth/login` | Inicia sesión |
| `POST` | `/auth/register` | Registra un nuevo usuario |

## Entidad User
```java
private Long id;
private String name;
private String email;
private String password;
private Role role;
```

# Base de Datos

Para este proyecto se esta utilizando una base de datos H2 en memoria
su configuracion esta disponible en application.properties

# Uso

Para iniciar la implementacion (ejecute el proyecto) o corra desde una terminal

```
./mvnw spring-boot:run
```

Puedes acceder a documentacion de la api(endpoints de login y register) mediante swagger-ui  en la url: 

```bash
http://localhost:8080/swagger-ui.html
```



