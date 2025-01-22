package com.example.java_capsa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CuidadorAdapter1 extends RecyclerView.Adapter<CuidadorAdapter1.ViewHolder> {
    private Context context;
    private List<Cuidador1> cuidadoresList;
    private OnItemDeleteListener onItemDeleteListener;

    // Interfaz para manejar la eliminación de elementos
    public interface OnItemDeleteListener {
        void onItemDelete(int id, int position);
    }

    // Constructor con el listener
    public CuidadorAdapter1(Context context, List<Cuidador1> cuidadoresList, OnItemDeleteListener listener) {
        this.context = context;
        this.cuidadoresList = cuidadoresList;
        this.onItemDeleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cuidador_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cuidador1 cuidador = cuidadoresList.get(position);

        if (cuidador != null) {
            Log.e("CuidadorAdapter", "Nombre recibido: " + cuidador.getNombre());

            // Validaciones seguras para evitar errores de NullPointerException
            holder.tvNombre.setText(cuidador.getNombre() == null || cuidador.getNombre().trim().isEmpty() ? "Nombre desconocido" : cuidador.getNombre());
            holder.tvUbicacion.setText(cuidador.getUbicacion() == null || cuidador.getUbicacion().trim().isEmpty() ? "Sin ubicación registrada" : cuidador.getUbicacion());
            holder.tvDescripcion.setText(cuidador.getDescripcion() == null || cuidador.getDescripcion().trim().isEmpty() ? "Sin descripción" : cuidador.getDescripcion());
            holder.tvFechaHora.setText(cuidador.getFechaHora() == null || cuidador.getFechaHora().trim().isEmpty() ? "Fecha desconocida" : cuidador.getFechaHora());

            // Manejo del clic en el botón "Verificado"
            holder.btnVerificado.setOnClickListener(v -> {
                if (onItemDeleteListener != null) {
                    onItemDeleteListener.onItemDelete(cuidador.getId(), position);
                }
            });

            // Validar la imagen antes de cargarla
            if (cuidador.getFoto() != null && !cuidador.getFoto().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(cuidador.getFoto(), Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.fotoEvidencia.setImageBitmap(decodedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.fotoEvidencia.setImageResource(R.drawable.baseline_image_24); // Imagen de error
                }
            } else {
                holder.fotoEvidencia.setImageResource(R.drawable.baseline_image_24);
            }
        } else {
            Log.e("CuidadorAdapter", "El cuidador en la posición " + position + " es nulo");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUbicacion, tvDescripcion, tvFechaHora;
        ImageView fotoEvidencia;
        Button btnVerificado; // Botón "Verificado"

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            fotoEvidencia = itemView.findViewById(R.id.fotoEvidencia);
            btnVerificado = itemView.findViewById(R.id.btnVerificado); // Inicialización del botón
        }
    }

    @Override
    public int getItemCount() {
        return cuidadoresList.size();
    }
}
