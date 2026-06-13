package com.ejemplo.gestorpeliculas;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenciasApp {
    private static final String NOMBRE_PREFERENCIAS = "com.ejemplo.gestorpeliculas.prefs";
    private static final String CLAVE_ID_USUARIO = "id_usuario";
    private static final String CLAVE_EMAIL = "email";
    private static final String CLAVE_NOMBRE = "nombre";
    private static final String CLAVE_TOKEN = "token";
    private static final String CLAVE_SESION_INICIADA = "sesion_iniciada";
    private static final String CLAVE_IDIOMA = "idioma";
    private static final String CLAVE_TEMA_OSCURO = "tema_oscuro";

    private SharedPreferences preferencias;
    private SharedPreferences.Editor editor;

    public PreferenciasApp(Context context) {
        preferencias = context.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        editor = preferencias.edit();
    }

    // Métodos para guardar sesión
    public void guardarSesion(int idUsuario, String email, String nombre, String token) {
        editor.putInt(CLAVE_ID_USUARIO, idUsuario);
        editor.putString(CLAVE_EMAIL, email);
        editor.putString(CLAVE_NOMBRE, nombre);
        editor.putString(CLAVE_TOKEN, token);
        editor.putBoolean(CLAVE_SESION_INICIADA, true);
        editor.apply();
    }

    public void guardarToken(String token) {
        editor.putString(CLAVE_TOKEN, token);
        editor.apply();
    }

    public String obtenerToken() {
        return preferencias.getString(CLAVE_TOKEN, "");
    }

    public int obtenerIdUsuario() {
        return preferencias.getInt(CLAVE_ID_USUARIO, -1);
    }

    public String obtenerEmail() {
        return preferencias.getString(CLAVE_EMAIL, "");
    }

    public String obtenerNombre() {
        return preferencias.getString(CLAVE_NOMBRE, "");
    }

    public boolean estaSesionIniciada() {
        return preferencias.getBoolean(CLAVE_SESION_INICIADA, false);
    }

    public void cerrarSesion() {
        editor.remove(CLAVE_ID_USUARIO);
        editor.remove(CLAVE_EMAIL);
        editor.remove(CLAVE_NOMBRE);
        editor.remove(CLAVE_TOKEN);
        editor.putBoolean(CLAVE_SESION_INICIADA, false);
        editor.apply();
    }

    // Métodos para preferencias de aplicación
    public void guardarIdioma(String idioma) {
        editor.putString(CLAVE_IDIOMA, idioma);
        editor.apply();
    }

    public String obtenerIdioma() {
        return preferencias.getString(CLAVE_IDIOMA, "es");
    }

    public void guardarTemaDarkMode(boolean esDark) {
        editor.putBoolean(CLAVE_TEMA_OSCURO, esDark);
        editor.apply();
    }

    public boolean obtenerTemaDarkMode() {
        return preferencias.getBoolean(CLAVE_TEMA_OSCURO, false);
    }

    // Limpiar todas las preferencias
    public void limpiarTodo() {
        editor.clear();
        editor.apply();
    }
}
