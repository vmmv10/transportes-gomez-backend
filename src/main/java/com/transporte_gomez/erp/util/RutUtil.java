package com.transporte_gomez.erp.util;

/**
 * RUT chileno: se guarda sin puntos y con guion (12345678-9 / 61981360-K).
 */
public final class RutUtil {

    private RutUtil() {
    }

    /** "61.981.360-k" -> "61981360-K". Devuelve null si viene vacío. */
    public static String normalizar(String rut) {
        if (rut == null || rut.isBlank()) {
            return null;
        }
        String limpio = rut.replace(".", "").replace("-", "").replace(" ", "").trim().toUpperCase();
        if (limpio.length() < 2) {
            return limpio;
        }
        return limpio.substring(0, limpio.length() - 1) + "-" + limpio.charAt(limpio.length() - 1);
    }

    /** Valida el dígito verificador (módulo 11). */
    public static boolean esValido(String rut) {
        String normalizado = normalizar(rut);
        if (normalizado == null || !normalizado.matches("\\d{1,8}-[\\dK]")) {
            return false;
        }
        String cuerpo = normalizado.substring(0, normalizado.indexOf('-'));
        char dv = normalizado.charAt(normalizado.length() - 1);

        int suma = 0;
        int factor = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * factor;
            factor = factor == 7 ? 2 : factor + 1;
        }
        int resto = 11 - (suma % 11);
        char esperado = resto == 11 ? '0' : resto == 10 ? 'K' : Character.forDigit(resto, 10);
        return dv == esperado;
    }
}
