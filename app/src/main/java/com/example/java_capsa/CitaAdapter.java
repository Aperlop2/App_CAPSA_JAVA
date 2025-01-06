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
        // Inflar el diseño de la interfaz donde se mostrarán las citas
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        // Obtener la cita en la posición actual
        Cita cita = citasList.get(position);

        // Configurar los datos de la cita en las vistas
        String detalle = "Fecha y hora: " + cita.getCuidador() + "\n"
                + "Cuidador: " + cita.getCuidador() + "\n"
                + "Ubicación: " + cita.getUbicacion();

        holder.detalleCita.setText(detalle);

        // Configurar los botones de editar y eliminar
        holder.btnEditarCita.setOnClickListener(v -> {
            // Acción para editar la cita (puedes añadir lógica según necesidad)
        });

        holder.btnEliminarCita.setOnClickListener(v -> {
            // Eliminar la cita de la lista
            citasList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        // Retorna el número de citas en la lista
        return citasList.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView detalleCita;
        Button btnEditarCita, btnEliminarCita;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Enlazar las vistas del diseño
            detalleCita = itemView.findViewById(R.id.tvDetalleCita);
            btnEditarCita = itemView.findViewById(R.id.btnEditarCita);
            btnEliminarCita = itemView.findViewById(R.id.btnEliminarCita);
        }
    }
}
