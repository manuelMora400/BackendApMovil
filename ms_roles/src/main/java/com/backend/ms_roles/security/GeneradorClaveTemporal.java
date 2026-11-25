package com.backend.ms_roles.security;

import java.security.SecureRandom;

public class GeneradorClaveTemporal {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String LETRAS_MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALFABETO_MIXTO = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMEROS = "0123456789";

    private GeneradorClaveTemporal() {}

    public static String generarClave() {
        StringBuilder sb = new StringBuilder(10);

        sb.append(LETRAS_MAYUSCULAS.charAt(RANDOM.nextInt(LETRAS_MAYUSCULAS.length())));
       
        for (int i = 0; i < 6; i++) {
            sb.append(ALFABETO_MIXTO.charAt(RANDOM.nextInt(ALFABETO_MIXTO.length())));
        }
        sb.append('.');
        for (int i = 0; i < 2; i++) {
            sb.append(NUMEROS.charAt(RANDOM.nextInt(NUMEROS.length())));
        }
        return sb.toString();
    }
}