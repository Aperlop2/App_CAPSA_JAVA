package com.example.java_capsa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private final List<Cita> citasList;

    public CitaAdapter(List<Cita> citasList) {
        this.citasList = citasList;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = citasList.get(position);

        holder.detalle.setText(cita.getDetalle());
        holder.cuidador.setText(cita.getCuidador());
        holder.ubicacion.setText(cita.getUbicacion());

        holder.btnEditar.setOnClickListener(v -> {
            // Acción para editar cita
        });

        holder.btnEliminar.setOnClickListener(v -> {
            citasList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView detalle, cuidador, ubicacion;
        Button btnEditar, btnEliminar;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            detalle = itemView.findViewById(R.id.tvDetalleCita);
            btnEditar = itemView.findViewById(R.id.btnEditarCita);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCita);
        }
    }
}