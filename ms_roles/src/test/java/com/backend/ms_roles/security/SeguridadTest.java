package com.backend.ms_roles.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class SeguridadTest {

    @Test
    @DisplayName("la clave temporal siempre cumple las reglas: 10 caracteres, mayuscula, punto y 2 numeros al final")
    void claveTemporal_cumpleLasReglas() {
        for (int i = 0; i < 200; i++) {
            String clave = GeneradorClaveTemporal.generarClave();
            assertTrue(clave.matches("^[A-Z][A-Za-z0-9]{6}\\.[0-9]{2}$"), "clave invalida: " + clave);
        }
    }

    @Test
    @DisplayName("BCrypt: la clave se guarda encriptada y matches la reconoce")
    void passwordEncoder_encriptaYCompara() {
        PasswordEncoder encoder = new SecurityConfig().passwordEncoder();

        String hash = encoder.encode("Nueva.123");

        assertNotEquals("Nueva.123", hash);
        assertTrue(encoder.matches("Nueva.123", hash));
        assertFalse(encoder.matches("Otra.1234", hash));
    }
}
