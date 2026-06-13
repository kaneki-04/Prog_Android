package com.ejemplo.gestorpeliculas;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PeliculasActivity extends AppCompatActivity {
    private ListView lvPeliculas;
    private ProgressBar pbCargando;
    private AdaptadorPeliculas adaptador;
    private List<Pelicula> peliculas;
    private ServicioHTTP servicioHTTP;
    private PreferenciasApp preferencias;
    private int idUsuario;
    private Pelicula peliculaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas);

        lvPeliculas = findViewById(R.id.lvPeliculas);
        pbCargando = findViewById(R.id.pbCargando);

        servicioHTTP = new ServicioHTTP(this);
        preferencias = new PreferenciasApp(this);
        idUsuario = preferencias.obtenerIdUsuario();

        peliculas = new ArrayList<>();
        adaptador = new AdaptadorPeliculas(this, peliculas);
        lvPeliculas.setAdapter(adaptador);

        registerForContextMenu(lvPeliculas);

        lvPeliculas.setOnItemClickListener((parent, view, position, id) -> {
            peliculaSeleccionada = peliculas.get(position);
            abrirDetallesPelicula(peliculaSeleccionada);
        });

        cargarPeliculas("");
    }

    private void cargarPeliculas(String filtro) {
        mostrarCargando(true);
        servicioHTTP.listarPeliculas(idUsuario, filtro, new ServicioHTTP.RespuestaHTTP() {
            @Override
            public void onRespuesta(JSONObject respuesta) {
                mostrarCargando(false);
                try {
                    if (!respuesta.getBoolean("error")) {
                        peliculas.clear();
                        JSONArray arrayPeliculas = respuesta.getJSONArray("peliculas");
                        for (int i = 0; i < arrayPeliculas.length(); i++) {
                            JSONObject obj = arrayPeliculas.getJSONObject(i);
                            Pelicula pelicula = new Pelicula();
                            pelicula.setIdPelicula(obj.getInt("id_pelicula"));
                            pelicula.setTitulo(obj.getString("titulo"));
                            pelicula.setDirector(obj.getString("director"));
                            pelicula.setGenero(obj.getString("genero"));
                            pelicula.setAño(obj.getInt("año"));
                            pelicula.setDescripcion(obj.getString("descripcion"));
                            pelicula.setPuntuacion(obj.getInt("puntuacion"));
                            pelicula.setEstado(obj.getString("estado"));
                            peliculas.add(pelicula);
                        }
                        adaptador.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                mostrarCargando(false);
                Toast.makeText(PeliculasActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_agregar) {
            abrirDialogoAgregarPelicula();
            return true;
        } else if (id == R.id.menu_filtrar) {
            mostrarMenuFiltros();
            return true;
        } else if (id == R.id.menu_perfil) {
            mostrarDialogoPerfil();
            return true;
        } else if (id == R.id.menu_cerrar_sesion) {
            cerrarSesion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual_pelicula, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        peliculaSeleccionada = peliculas.get(info.position);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ctx_editar) {
            abrirDialogoEditarPelicula(peliculaSeleccionada);
            return true;
        } else if (id == R.id.ctx_eliminar) {
            mostrarDialogoConfirmacion(peliculaSeleccionada);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void abrirDialogoAgregarPelicula() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialogo_pelicula, null);

        EditText etTitulo = view.findViewById(R.id.etTitulo);
        EditText etDirector = view.findViewById(R.id.etDirector);
        EditText etGenero = view.findViewById(R.id.etGenero);
        EditText etAno = view.findViewById(R.id.etAno);
        EditText etDescripcion = view.findViewById(R.id.etDescripcion);
        Spinner spEstado = view.findViewById(R.id.spEstado);
        RatingBar rbPuntuacion = view.findViewById(R.id.rbPuntuacion);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);

        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                this, R.array.estados_pelicula, android.R.layout.simple_spinner_item);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterEstado);

        builder.setView(view);
        AlertDialog dialogo = builder.create();

        btnGuardar.setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString().trim();
            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título es requerido", Toast.LENGTH_SHORT).show();
                return;
            }
            Pelicula pelicula = new Pelicula();
            pelicula.setTitulo(titulo);
            pelicula.setDirector(etDirector.getText().toString().trim());
            pelicula.setGenero(etGenero.getText().toString().trim());
            String anoStr = etAno.getText().toString().trim();
            pelicula.setAño(anoStr.isEmpty() ? 0 : Integer.parseInt(anoStr));
            pelicula.setDescripcion(etDescripcion.getText().toString().trim());
            pelicula.setEstado(spEstado.getSelectedItem().toString());
            pelicula.setPuntuacion((int) rbPuntuacion.getRating());

            agregarPelicula(pelicula);
            dialogo.dismiss();
        });

        dialogo.show();
    }

    private void abrirDialogoEditarPelicula(Pelicula pelicula) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialogo_pelicula, null);

        EditText etTitulo = view.findViewById(R.id.etTitulo);
        EditText etDirector = view.findViewById(R.id.etDirector);
        EditText etGenero = view.findViewById(R.id.etGenero);
        EditText etAno = view.findViewById(R.id.etAno);
        EditText etDescripcion = view.findViewById(R.id.etDescripcion);
        Spinner spEstado = view.findViewById(R.id.spEstado);
        RatingBar rbPuntuacion = view.findViewById(R.id.rbPuntuacion);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);

        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                this, R.array.estados_pelicula, android.R.layout.simple_spinner_item);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterEstado);

        etTitulo.setText(pelicula.getTitulo());
        etDirector.setText(pelicula.getDirector());
        etGenero.setText(pelicula.getGenero());
        etAno.setText(String.valueOf(pelicula.getAño()));
        etDescripcion.setText(pelicula.getDescripcion());
        rbPuntuacion.setRating(pelicula.getPuntuacion());

        builder.setView(view);
        AlertDialog dialogo = builder.create();

        btnGuardar.setOnClickListener(v -> {
            pelicula.setTitulo(etTitulo.getText().toString().trim());
            pelicula.setDirector(etDirector.getText().toString().trim());
            pelicula.setGenero(etGenero.getText().toString().trim());
            String anoStr = etAno.getText().toString().trim();
            pelicula.setAño(anoStr.isEmpty() ? 0 : Integer.parseInt(anoStr));
            pelicula.setDescripcion(etDescripcion.getText().toString().trim());
            pelicula.setEstado(spEstado.getSelectedItem().toString());
            pelicula.setPuntuacion((int) rbPuntuacion.getRating());
            actualizarPelicula(pelicula);
            dialogo.dismiss();
        });

        dialogo.show();
    }

    private void abrirDetallesPelicula(Pelicula pelicula) {
        Intent intent = new Intent(this, DetallesPeliculaActivity.class);
        intent.putExtra("pelicula_id", pelicula.getIdPelicula());
        intent.putExtra("pelicula_titulo", pelicula.getTitulo());
        intent.putExtra("pelicula_director", pelicula.getDirector());
        intent.putExtra("pelicula_genero", pelicula.getGenero());
        intent.putExtra("pelicula_año", pelicula.getAño());
        intent.putExtra("pelicula_descripcion", pelicula.getDescripcion());
        intent.putExtra("pelicula_estado", pelicula.getEstado());
        intent.putExtra("pelicula_puntuacion", pelicula.getPuntuacion());
        startActivity(intent);
    }

    private void agregarPelicula(Pelicula pelicula) {
        mostrarCargando(true);
        servicioHTTP.agregarPelicula(idUsuario, pelicula, new ServicioHTTP.RespuestaHTTP() {
            @Override
            public void onRespuesta(JSONObject respuesta) {
                mostrarCargando(false);
                try {
                    if (!respuesta.getBoolean("error")) {
                        pelicula.setIdPelicula(respuesta.getInt("id_pelicula"));
                        adaptador.agregarPelicula(pelicula);
                        Toast.makeText(PeliculasActivity.this, "Película agregada", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                mostrarCargando(false);
                Toast.makeText(PeliculasActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPelicula(Pelicula pelicula) {
        mostrarCargando(true);
        servicioHTTP.actualizarPelicula(idUsuario, pelicula, new ServicioHTTP.RespuestaHTTP() {
            @Override
            public void onRespuesta(JSONObject respuesta) {
                mostrarCargando(false);
                try {
                    if (!respuesta.getBoolean("error")) {
                        adaptador.actualizarPelicula(pelicula);
                        Toast.makeText(PeliculasActivity.this, "Película actualizada", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                mostrarCargando(false);
                Toast.makeText(PeliculasActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion(Pelicula pelicula) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar película")
                .setMessage("¿Deseas eliminar \"" + pelicula.getTitulo() + "\"?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarPelicula(pelicula.getIdPelicula()))
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarPelicula(int idPelicula) {
        mostrarCargando(true);
        servicioHTTP.eliminarPelicula(idUsuario, idPelicula, new ServicioHTTP.RespuestaHTTP() {
            @Override
            public void onRespuesta(JSONObject respuesta) {
                mostrarCargando(false);
                try {
                    if (!respuesta.getBoolean("error")) {
                        adaptador.eliminarPelicula(idPelicula);
                        Toast.makeText(PeliculasActivity.this, "Película eliminada", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                mostrarCargando(false);
                Toast.makeText(PeliculasActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarMenuFiltros() {
        String[] opciones = {"Todas", "Por ver", "Viendo", "Visto"};
        new AlertDialog.Builder(this)
                .setTitle("Filtrar por estado")
                .setItems(opciones, (dialog, which) -> cargarPeliculas(which == 0 ? "" : opciones[which]))
                .show();
    }

    private void mostrarDialogoPerfil() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialogo_perfil, null);
        TextView tvNombre = view.findViewById(R.id.tvNombrePerfil);
        TextView tvEmail = view.findViewById(R.id.tvEmailPerfil);
        tvNombre.setText("Nombre: " + preferencias.obtenerNombre());
        tvEmail.setText("Email: " + preferencias.obtenerEmail());
        builder.setView(view).setPositiveButton("Cerrar", null).show();
    }

    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    preferencias.cerrarSesion();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void mostrarCargando(boolean mostrar) {
        pbCargando.setVisibility(mostrar ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        servicioHTTP.cancelarTodo();
    }
}