package com.example.java_capsa;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CuidadorAdapter extends RecyclerView.Adapter<CuidadorAdapter.CuidadorViewHolder> {

    private final List<Cuidador> cuidadoresList;
    private final OnCuidadorEliminarListener eliminarListener;
    private static final int EDITAR_CUIDADOR_REQUEST = 1;

    public interface OnCuidadorEliminarListener {
        void onEliminarCuidador(int id, int position);
    }

    public CuidadorAdapter(List<Cuidador> cuidadoresList) {
        this.cuidadoresList = cuidadoresList;
        OnCuidadorEliminarListener listener = null;
        this.eliminarListener = listener;  // Ahora 'listener' es el parámetro pasado
    }


    @NonNull
    @Override
    public CuidadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuidador, parent, false);
        return new CuidadorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CuidadorViewHolder holder, int position) {
        Cuidador cuidador = cuidadoresList.get(position);

        holder.nombre.setText(cuidador.getNombre());
        holder.ubicacion.setText(cuidador.getUbicacion());
        holder.descripcion.setText(cuidador.getDescripcion());
        holder.fechaHora.setText(cuidador.getFechaHora());

        if (!cuidador.getFotoUrl().equals("URL_DEFAULT")) {
            holder.imagen.setImageURI(Uri.parse(cuidador.getFotoUrl()));
        }

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditarCuidador.class);
            intent.putExtra("nombre", cuidador.getNombre());
            intent.putExtra("ubicacion", cuidador.getUbicacion());
            intent.putExtra("descripcion", cuidador.getDescripcion());
            intent.putExtra("fotoUrl", cuidador.getFotoUrl());
            intent.putExtra("fechaHora", cuidador.getFechaHora());
            ((AppCompatActivity) v.getContext()).startActivityForResult(intent, EDITAR_CUIDADOR_REQUEST);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (eliminarListener != null) {
                eliminarListener.onEliminarCuidador(cuidador.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cuidadoresList.size();
    }

    static class CuidadorViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, ubicacion, descripcion, fechaHora;
        ImageView imagen;
        Button btnEditar, btnEliminar;

        public CuidadorViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre_cuidador);
            ubicacion = itemView.findViewById(R.id.ubicacion_cuidador);
            descripcion = itemView.findViewById(R.id.descripcion_cuidador);
            fechaHora = itemView.findViewById(R.id.fecha_hora);
            imagen = itemView.findViewById(R.id.imagen_cuidador);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
