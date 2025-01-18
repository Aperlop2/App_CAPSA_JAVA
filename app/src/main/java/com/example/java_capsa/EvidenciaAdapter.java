package com.example.java_capsa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class EvidenciaAdapter extends RecyclerView.Adapter<EvidenciaAdapter.EvidenciaViewHolder> {

    private final List<Evidencia> evidenciasList;

    public EvidenciaAdapter(List<Evidencia> evidenciasList) {
        this.evidenciasList = evidenciasList;
    }

    @NonNull
    @Override
    public EvidenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evidencia, parent, false);
        return new EvidenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvidenciaViewHolder holder, int position) {
        Evidencia evidencia = evidenciasList.get(position);

        holder.detalle.setText(evidencia.getDetalle());
        holder.ratingBar.setRating(evidencia.getRating());
        holder.comentario.setText(evidencia.getComentario());
        holder.imagen.setImageResource(evidencia.getImagen());

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> Toast.makeText(holder.itemView.getContext(), "Calificación actualizada a " + rating, Toast.LENGTH_SHORT).show());

        holder.iconoExportar.setOnClickListener(v -> {
            // Lógica para exportar la evidencia
            Toast.makeText(holder.itemView.getContext(), "Exportando evidencia...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return evidenciasList.size();
    }

    static class EvidenciaViewHolder extends RecyclerView.ViewHolder {
        TextView detalle, comentario;
        RatingBar ratingBar;
        ImageView imagen, iconoExportar;

        public EvidenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            detalle = itemView.findViewById(R.id.tvDetalleEvidencia);
            comentario = itemView.findViewById(R.id.tvComentarioEvidencia);
            ratingBar = itemView.findViewById(R.id.ratingBarEvidencia);
            imagen = itemView.findViewById(R.id.imagenEvidencia);
            iconoExportar = itemView.findViewById(R.id.iconoExportar);
        }
    }
}