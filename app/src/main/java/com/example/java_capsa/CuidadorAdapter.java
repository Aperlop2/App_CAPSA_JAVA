package com.example.java_capsa;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CuidadorAdapter extends RecyclerView.Adapter<CuidadorAdapter.CuidadorViewHolder> {

    private List<Cuidador> cuidadoresList;

    public CuidadorAdapter(List<Cuidador> cuidadoresList) {
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
        Cuidador cuidador = cuidadoresList.get(position);

        holder.nombre.setText(cuidador.getNombre());
        holder.direccion.setText(cuidador.getDireccion());
        holder.telefono.setText(cuidador.getTelefono());
        holder.horario.setText(cuidador.getHorario());
        holder.imagen.setImageURI(Uri.parse(cuidador.getImageUri()));

        holder.btnEditar.setOnClickListener(v -> {
            // Acción para editar cuidador
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
