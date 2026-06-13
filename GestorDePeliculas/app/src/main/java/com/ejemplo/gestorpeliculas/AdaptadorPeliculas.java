package com.ejemplo.gestorpeliculas;
// AdaptadorPeliculas.java - Adaptador personalizado para las películas

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ejemplo.gestorpeliculas.R;

import java.util.List;

public class AdaptadorPeliculas extends ArrayAdapter<Pelicula> {
    private Context contexto;
    private List<Pelicula> peliculas;
    private LayoutInflater inflater;

    public AdaptadorPeliculas(Context contexto, List<Pelicula> peliculas) {
        super(contexto, 0, peliculas);
        this.contexto = contexto;
        this.peliculas = peliculas;
        this.inflater = LayoutInflater.from(contexto);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_pelicula, parent, false);
        }

        Pelicula pelicula = peliculas.get(position);

        // Elementos del layout
        TextView tvTitulo = convertView.findViewById(R.id.tvTituloPelicula);
        TextView tvDirector = convertView.findViewById(R.id.tvDirector);
        TextView tvGenero = convertView.findViewById(R.id.tvGenero);
        TextView tvEstado = convertView.findViewById(R.id.tvEstado);
        RatingBar rbPuntuacion = convertView.findViewById(R.id.rbPuntuacion);

        // Asignar valores
        tvTitulo.setText(pelicula.getTitulo());
        tvDirector.setText("Director: " + (pelicula.getDirector() != null ? pelicula.getDirector() : "N/A"));
        tvGenero.setText("Género: " + (pelicula.getGenero() != null ? pelicula.getGenero() : "N/A"));
        tvEstado.setText("Estado: " + pelicula.getEstado());

        // Color según estado
        switch (pelicula.getEstado()) {
            case "Por ver":
                tvEstado.setTextColor(contexto.getResources().getColor(android.R.color.holo_blue_light));
                break;
            case "Viendo":
                tvEstado.setTextColor(contexto.getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "Visto":
                tvEstado.setTextColor(contexto.getResources().getColor(android.R.color.holo_green_light));
                break;
        }

        // Rating bar
        rbPuntuacion.setRating(pelicula.getPuntuacion());
        rbPuntuacion.setIsIndicator(true);

        return convertView;
    }

    public void actualizarLista(List<Pelicula> nuevaLista) {
        peliculas.clear();
        peliculas.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    public void agregarPelicula(Pelicula pelicula) {
        peliculas.add(0, pelicula);
        notifyDataSetChanged();
    }

    public void eliminarPelicula(int idPelicula) {
        for (int i = 0; i < peliculas.size(); i++) {
            if (peliculas.get(i).getIdPelicula() == idPelicula) {
                peliculas.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void actualizarPelicula(Pelicula peliculaActualizada) {
        for (int i = 0; i < peliculas.size(); i++) {
            if (peliculas.get(i).getIdPelicula() == peliculaActualizada.getIdPelicula()) {
                peliculas.set(i, peliculaActualizada);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
