package com.example.java_capsa;

import android.content.Context;
import android.content.Intent;
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
    private final Context context;

    public CitaAdapter(List<Cita> citasList, Context context) {
        this.citasList = citasList;
        this.context = context;
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

        // Dividir el detalle en fecha y hora
        String[] detalleDividido = cita.getDetalle().split(" ");
        String fecha = "";
        String hora = "";

        // Validar y asignar fecha y hora
        if (detalleDividido.length >= 3) {
            fecha = detalleDividido[0]; // Fecha
            hora = detalleDividido[1] + " " + detalleDividido[2]; // Hora (con AM/PM)
        } else if (detalleDividido.length == 2) {
            fecha = detalleDividido[0]; // Fecha
            hora = detalleDividido[1]; // Hora (sin AM/PM)
        }

        // Asignar valores a las vistas
        holder.tvFecha.setText(fecha);
        holder.tvHora.setText(hora);
        holder.tvCuidador.setText(cita.getCuidador());
        holder.tvUbicacion.setText(cita.getUbicacion());

        // Acción para editar la cita
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarCita.class);
            intent.putExtra("position", position); // Posición en la lista
            intent.putExtra("detalle", cita.getDetalle()); // Enviar el detalle completo (fecha y hora)
            intent.putExtra("cuidador", cita.getCuidador()); // Enviar cuidador
            intent.putExtra("ubicacion", cita.getUbicacion()); // Enviar ubicación
            ((GestionDeCitas) context).startActivityForResult(intent, 200); // Código único para editar
        });

        // Acción para eliminar la cita
        holder.btnEliminar.setOnClickListener(v -> {
            citasList.remove(position); // Eliminar la cita de la lista
            notifyItemRemoved(position); // Notificar que un elemento fue eliminado
            notifyItemRangeChanged(position, citasList.size()); // Actualizar el rango del RecyclerView
        });
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvHora, tvCuidador, tvUbicacion;
        Button btnEditar, btnEliminar;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Enlazar vistas con los IDs del diseño XML
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvCuidador = itemView.findViewById(R.id.tvCuidador);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            btnEditar = itemView.findViewById(R.id.btnEditarCita);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCita);
        }
    }
}