package com.example.java_capsa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private List<Cita> citasList;
    private Context context;
    private DatabaseReference citasRef;

    public CitaAdapter(List<Cita> citasList, Context context) {
        this.citasList = citasList;
        this.context = context;
        this.citasRef = FirebaseDatabase.getInstance().getReference("citas");
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

        holder.tvFecha.setText("📅 Fecha: " + cita.getFecha());
        holder.tvHora.setText("⏰ Hora: " + cita.getHora());
        holder.tvUbicacion.setText("📍 Ubicación: " + cita.getUbicacion());

        if ("Completada".equals(cita.getEstado())) {
            holder.btnCompletada.setEnabled(false);
            holder.btnCompletada.setText("Completada");
        } else {
            holder.btnCompletada.setOnClickListener(v -> mostrarDialogoConfirmacion(holder, cita, position));
        }
    }

    private void mostrarDialogoConfirmacion(CitaViewHolder holder, Cita cita, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmación")
                .setMessage("¿Marcar esta cita como completada y eliminarla?")
                .setPositiveButton("Sí", (dialog, which) -> marcarCitaComoCompletada(holder, cita, position))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void marcarCitaComoCompletada(CitaViewHolder holder, Cita cita, int position) {
        // Eliminar la cita de Firebase
        citasRef.child(cita.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Eliminar la cita de la lista y actualizar la vista
                    citasList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, citasList.size());

                    Toast.makeText(context, "Cita eliminada y marcada como completada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar la cita", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvHora, tvUbicacion;
        Button btnCompletada;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            btnCompletada = itemView.findViewById(R.id.btnCompletada);
        }
    }
}
