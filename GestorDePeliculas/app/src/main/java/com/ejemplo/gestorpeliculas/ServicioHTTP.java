package com.ejemplo.gestorpeliculas;
// ServicioHTTP.java - Servicio para peticiones HTTP

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ServicioHTTP {
    // Para emulador:
    private static final String URL_BASE = "http://10.0.2.2/api-peliculas/";
    private static final String TAG = "ServicioHTTP";
    
    private RequestQueue colaPeticiones;
    private Context contexto;

    public interface RespuestaHTTP {
        void onRespuesta(JSONObject respuesta);
        void onError(VolleyError error);
    }

    public ServicioHTTP(Context contexto) {
        this.contexto = contexto;
        this.colaPeticiones = Volley.newRequestQueue(contexto);
    }

    // Login
    public void login(String email, String password, final RespuestaHTTP callback) {
        JSONObject datosLogin = new JSONObject();
        try {
            datosLogin.put("email", email);
            datosLogin.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = URL_BASE + "auth.php?action=login";
        
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST,
                url,
                datosLogin,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respuesta) {
                        Log.d(TAG, "Login exitoso: " + respuesta.toString());
                        callback.onRespuesta(respuesta);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error en login: " + error.getMessage());
                        callback.onError(error);
                    }
                }
        );

        peticion.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        colaPeticiones.add(peticion);
    }

    // Registro
    public void registro(String email, String password, String nombre, final RespuestaHTTP callback) {
        JSONObject datosRegistro = new JSONObject();
        try {
            datosRegistro.put("email", email);
            datosRegistro.put("password", password);
            datosRegistro.put("nombre", nombre);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = URL_BASE + "auth.php?action=registro";
        
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST,
                url,
                datosRegistro,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respuesta) {
                        Log.d(TAG, "Registro exitoso: " + respuesta.toString());
                        callback.onRespuesta(respuesta);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error en registro: " + error.getMessage());
                        callback.onError(error);
                    }
                }
        );

        peticion.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        colaPeticiones.add(peticion);
    }

    // Login con Google
    public void loginGoogle(String googleId, String nombre, String email, final RespuestaHTTP callback) {
        JSONObject datosGoogle = new JSONObject();
        try {
            datosGoogle.put("google_id", googleId);
            datosGoogle.put("nombre", nombre);
            datosGoogle.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = URL_BASE + "auth.php?action=google_login";
        
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST,
                url,
                datosGoogle,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respuesta) {
                        Log.d(TAG, "Login Google exitoso");
                        callback.onRespuesta(respuesta);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error en login Google: " + error.getMessage());
                        callback.onError(error);
                    }
                }
        );

        peticion.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        colaPeticiones.add(peticion);
    }

    // Obtener lista de películas
    public void listarPeliculas(int idUsuario, String filtroEstado, final RespuestaHTTP callback) {
        String url = URL_BASE + "peliculas.php?action=listar&id_usuario=" + idUsuario;
        
        if (!filtroEstado.isEmpty()) {
            url += "&estado=" + filtroEstado;
        }

        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respuesta) {
                        Log.d(TAG, "Películas obtenidas");
                        callback.onRespuesta(respuesta);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error al obtener películas: " + error.getMessage());
                        callback.onError(error);
                    }
                }
        );

        peticion.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        colaPeticiones.add(peticion);
    }

    // Agregar película
    public void agregarPelicula(int idUsuario, Pelicula pelicula, final RespuestaHTTP callback) {
        JSONObject datosPelicula = new JSONObject();
        try {
            datosPelicula.put("titulo", pelicula.getTitulo());
            datosPelicula.put("director", pelicula.getDirector());
            datosPelicula.put("genero", pelicula.getGenero());
            datosPelicula.put("año", pelicula.getAño());
            datosPelicula.put("descripcion", pelicula.getDescripcion());
            datosPelicula.put("estado", pelicula.getEstado());
            datosPelicula.put("puntuacion", pelicula.getPuntuacion());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = URL_BASE + "peliculas.php?id_usuario=" + idUsuario;
        
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST,
                url,
                datosPelicula,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respuesta) {
                        Log.d(TAG, "Película agregada");
                        callback.onRespuesta(respuesta);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error al agregar película: " + error.getMessage());
                        callback.onError(error);
                    }
                }
        );

        peticion.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        colaPeticiones.add(peticion);
    }

    // Actualizar película
    public void actualizarPelicula(int idUsuario, Pelicula pelicula, final RespuestaHTTP callback) {
        JSONObject datosPelicula = new JSONObject();
        try {
            datosPelicula.put("id_pelicula", pelicula.getIdPelicula());
            datosPelicula.put("titulo", pelicula.getTitulo());
            datosPelicula.put("director", pelicula.getDirector());
            datosPelicula.put("genero", pelicula.getGenero());
            datosPelicula.put("año", pelicula.getAño());
            datosPelicula.put("descripcion", pelicula.getDescripcion());
            datosPelicula.put("estado", pelicula.getEstado());
            datosPelicula.put("puntuacion", pelicula.getPuntuacion());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = URL_BASE + "peliculas.php?id_usuario=" + idUsuario;
        
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                datosPelicula,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respuesta) {
                        Log.d(TAG, "Película actualizada");
                        callback.onRespuesta(respuesta);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error al actualizar película: " + error.getMessage());
                        callback.onError(error);
                    }
                }
        );

        peticion.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        colaPeticiones.add(peticion);
    }

    // Eliminar película
    public void eliminarPelicula(int idUsuario, int idPelicula, final RespuestaHTTP callback) {
        String url = URL_BASE + "peliculas.php?action=delete&id_usuario=" + idUsuario + "&id_pelicula=" + idPelicula;
        
        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respuesta) {
                        Log.d(TAG, "Película eliminada");
                        callback.onRespuesta(respuesta);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error al eliminar película: " + error.getMessage());
                        callback.onError(error);
                    }
                }
        );

        peticion.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        colaPeticiones.add(peticion);
    }

    // Cancelar todas las peticiones
    public void cancelarTodo() {
        colaPeticiones.cancelAll(TAG);
    }
}
