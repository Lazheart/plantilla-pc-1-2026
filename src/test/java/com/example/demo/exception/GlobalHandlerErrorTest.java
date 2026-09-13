package com.example.demo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.user.UserMethodNotAllowed;
import com.example.demo.user.UserNotFoundException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class GlobalHandlerErrorTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private MockMvc standaloneMockMvc;

    @RestController
    @RequestMapping("/test-error")
    static class TestErrorController {

        @GetMapping("/user-not-found")
        public void throwUserNotFound() {
            throw new UserNotFoundException("Usuario no encontrado con id: 99");
        }

        @GetMapping("/insufficient-permissions")
        public void throwInsufficientPermissions() {
            throw new InsufficientPermissionsException("No tiene permisos de administrador");
        }

        @GetMapping("/bad-request")
        public void throwBadRequest() {
            throw new BadRequestException("El formato del campo es inválido");
        }

        @GetMapping("/unauthorized")
        public void throwUnauthorized() {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        @GetMapping("/user-already-exists")
        public void throwUserAlreadyExists() {
            throw new UserAlreadyExistsException("El nombre de usuario ya está registrado");
        }

        @GetMapping("/method-not-allowed")
        public void throwUserMethodNotAllowed() {
            throw new UserMethodNotAllowed("Método no permitido para este tipo de usuario");
        }

        @GetMapping("/security-access-denied")
        public void throwSecurityAccessDenied() {
            throw new org.springframework.security.access.AccessDeniedException("Acceso no autorizado por rol");
        }

        @GetMapping("/illegal-argument")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Parámetro inválido especificado");
        }

        @GetMapping("/internal-error")
        public void throwInternalError() {
            throw new RuntimeException("Error inesperado en el servidor");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        standaloneMockMvc = MockMvcBuilders.standaloneSetup(new TestErrorController())
                .setControllerAdvice(new GlobalHandlerError())
                .build();
    }

    @Test
    void testUserNotFoundException_Returns404() throws Exception {
        standaloneMockMvc.perform(get("/test-error/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Usuario no encontrado con id: 99")))
                .andExpect(jsonPath("$.path", is("/test-error/user-not-found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void testInsufficientPermissionsException_Returns403() throws Exception {
        standaloneMockMvc.perform(get("/test-error/insufficient-permissions"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.message", is("No tiene permisos de administrador")))
                .andExpect(jsonPath("$.path", is("/test-error/insufficient-permissions")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void testBadRequestException_Returns400() throws Exception {
        standaloneMockMvc.perform(get("/test-error/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("El formato del campo es inválido")))
                .andExpect(jsonPath("$.path", is("/test-error/bad-request")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void testUnauthorizedException_Returns401() throws Exception {
        standaloneMockMvc.perform(get("/test-error/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message", is("Credenciales inválidas")))
                .andExpect(jsonPath("$.path", is("/test-error/unauthorized")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void testUserAlreadyExistsException_Returns409() throws Exception {
        standaloneMockMvc.perform(get("/test-error/user-already-exists"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("El nombre de usuario ya está registrado")))
                .andExpect(jsonPath("$.path", is("/test-error/user-already-exists")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void testUserMethodNotAllowed_Returns405() throws Exception {
        standaloneMockMvc.perform(get("/test-error/method-not-allowed"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status", is(405)))
                .andExpect(jsonPath("$.error", is("Method Not Allowed")))
                .andExpect(jsonPath("$.message", is("Método no permitido para este tipo de usuario")))
                .andExpect(jsonPath("$.path", is("/test-error/method-not-allowed")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void testInternalServerError_Returns500() throws Exception {
        standaloneMockMvc.perform(get("/test-error/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("Ha ocurrido un error inesperado en el servidor")))
                .andExpect(jsonPath("$.path", is("/test-error/internal-error")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void testRegisterMissingFields_Returns400BadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"email\":\"test@example.com\",\"password\":\"12345\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("El nombre de usuario es obligatorio")));
    }

    @Test
    void testLoginUnknownUser_Returns401Unauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nonexistent\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message", is("Credenciales inválidas")));
    }

    @Test
    void testMalformedJson_Returns400BadRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{malformed-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("El cuerpo de la solicitud es inválido o no tiene un formato JSON legible")));
    }

    @Test
    void testSecurityAccessDenied_Returns403() throws Exception {
        standaloneMockMvc.perform(get("/test-error/security-access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.message", is("Acceso no autorizado por rol")))
                .andExpect(jsonPath("$.path", is("/test-error/security-access-denied")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void testIllegalArgument_Returns400() throws Exception {
        standaloneMockMvc.perform(get("/test-error/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Parámetro inválido especificado")))
                .andExpect(jsonPath("$.path", is("/test-error/illegal-argument")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
