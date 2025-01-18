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

    private final List<com.example.java_capsa.Cuidador> cuidadoresList;
    private static final int EDITAR_CUIDADOR_REQUEST = 1;

    public CuidadorAdapter(List<com.example.java_capsa.Cuidador> cuidadoresList) {
        this.cuidadoresList = cuidadoresList;
    }

    @NonNull
    @Override
    public CuidadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuidador, parent, false);
        return new CuidadorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CuidadorViewHolder holder, int position) {
        com.example.java_capsa.Cuidador cuidador = cuidadoresList.get(position);

        holder.nombre.setText(cuidador.getNombre());
        holder.direccion.setText(cuidador.getDireccion());
        holder.telefono.setText(cuidador.getTelefono());
        holder.horario.setText(cuidador.getHorario());
        holder.imagen.setImageURI(Uri.parse(cuidador.getImageUri()));

        holder.btnEditar.setOnClickListener(v -> {
            // Acción para editar cuidador
            Intent intent = new Intent(v.getContext(), com.example.java_capsa.EditarCuidador.class);
            intent.putExtra("nombre", cuidador.getNombre());
            intent.putExtra("direccion", cuidador.getDireccion());
            intent.putExtra("telefono", cuidador.getTelefono());
            intent.putExtra("horario", cuidador.getHorario());
            ((AppCompatActivity) v.getContext()).startActivityForResult(intent, EDITAR_CUIDADOR_REQUEST);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            // Acción para eliminar cuidador
            cuidadoresList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return cuidadoresList.size();
    }

    static class CuidadorViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, direccion, telefono, horario;
        ImageView imagen;
        Button btnEditar, btnEliminar;

        public CuidadorViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre_cuidador);
            direccion = itemView.findViewById(R.id.direccion_cuidador);
            telefono = itemView.findViewById(R.id.telefono_cuidador);
            horario = itemView.findViewById(R.id.horario_cuidador);
            imagen = itemView.findViewById(R.id.imagen_cuidador);
            btnEditar = itemView.findViewById(R.id.btnEditar1);
            btnEliminar = itemView.findViewById(R.id.btnEliminar1);
        }
    }
}