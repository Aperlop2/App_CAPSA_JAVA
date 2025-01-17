package com.example.java_capsa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private final List<Cita> citasList;
    private final Context context;
    private final OnCitaClickListener listener;

    public CitaAdapter(List<Cita> citasList, Context context, OnCitaClickListener listener) {
        this.citasList = citasList;
        this.context = context;
        this.listener = listener;
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
        holder.tvCita.setText(cita.getDetalle());
        holder.tvCuidador.setText(cita.getCuidador());
        holder.tvUbicacion.setText(cita.getUbicacion());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCitaClick(cita.getCuidador());
            }
        });
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {

        TextView tvCita, tvCuidador, tvUbicacion;

        @SuppressLint("CutPasteId")
        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCita = itemView.findViewById(R.id.tv_cuidador);
            tvCuidador = itemView.findViewById(R.id.tv_cuidador);
            tvUbicacion = itemView.findViewById(R.id.tv_ubicacion);
        }
    }

    public interface OnCitaClickListener {
        void onCitaClick(String cuidador);
    }
}