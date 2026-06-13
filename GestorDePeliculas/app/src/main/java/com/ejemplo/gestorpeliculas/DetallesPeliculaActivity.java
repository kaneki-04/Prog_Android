package com.ejemplo.gestorpeliculas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetallesPeliculaActivity extends AppCompatActivity {
    private TextView tvTitulo, tvDirector, tvGenero, tvAno, tvDescripcion, tvEstado;
    private RatingBar rbPuntuacion;
    private Button btnEditar, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pelicula);

        tvTitulo      = findViewById(R.id.tvTituloDetalle);
        tvDirector    = findViewById(R.id.tvDirectorDetalle);
        tvGenero      = findViewById(R.id.tvGeneroDetalle);
        tvAno         = findViewById(R.id.tvAnoDetalle);
        tvDescripcion = findViewById(R.id.tvDescripcionDetalle);
        tvEstado      = findViewById(R.id.tvEstadoDetalle);
        rbPuntuacion  = findViewById(R.id.rbPuntuacionDetalle);
        btnEditar     = findViewById(R.id.btnEditarDetalle);
        btnVolver     = findViewById(R.id.btnVolverDetalle);

        Intent intent = getIntent();
        tvTitulo.setText(intent.getStringExtra("pelicula_titulo"));
        tvDirector.setText("Director: " + intent.getStringExtra("pelicula_director"));
        tvGenero.setText("Género: " + intent.getStringExtra("pelicula_genero"));
        tvAno.setText("Año: " + intent.getIntExtra("pelicula_año", 0));
        tvDescripcion.setText(intent.getStringExtra("pelicula_descripcion"));
        tvEstado.setText("Estado: " + intent.getStringExtra("pelicula_estado"));
        rbPuntuacion.setRating(intent.getIntExtra("pelicula_puntuacion", 0));
        rbPuntuacion.setIsIndicator(true);

        btnEditar.setOnClickListener(v -> finish());
        btnVolver.setOnClickListener(v -> finish());
    }
}