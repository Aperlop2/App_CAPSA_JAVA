package com.example.java_capsa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private List<Cita> citasList;
    private Context context;

    public CitaAdapter(List<Cita> citasList, Context context) {
        this.citasList = citasList;
        this.context = context;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = citasList.get(position);

        if (cita != null) {
            holder.tvFecha.setText("📅 Fecha: " + (cita.getFecha() != null ? cita.getFecha() : "No disponible"));
            holder.tvHora.setText("⏰ Hora: " + (cita.getHora() != null ? cita.getHora() : "No disponible"));
            holder.tvUbicacion.setText("📍 Ubicación: " + (cita.getUbicacion() != null ? cita.getUbicacion() : "No disponible"));
        }
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvHora, tvUbicacion;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
        }
    }
}
