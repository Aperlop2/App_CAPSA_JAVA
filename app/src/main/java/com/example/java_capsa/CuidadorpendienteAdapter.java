package com.example.java_capsa;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CuidadorpendienteAdapter extends RecyclerView.Adapter<CuidadorpendienteAdapter.CuidadorViewHolder> {

    private List<Cuidadorpendiente> listaCuidadores;  // Cambiar a Cuidadorpendiente
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAceptarClick(Cuidadorpendiente cuidador);
        void onRechazarClick(Cuidadorpendiente cuidador);
    }

    public CuidadorpendienteAdapter(List<Cuidadorpendiente> listaCuidadores, OnItemClickListener listener) {
        this.listaCuidadores = listaCuidadores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CuidadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuidador, parent, false);
        return new CuidadorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CuidadorViewHolder holder, int position) {
        Cuidadorpendiente cuidador = listaCuidadores.get(position);

        holder.nombre.setText(cuidador.getNombre());
        holder.correo.setText(cuidador.getCorreo());
        holder.telefono.setText(cuidador.getTelefono());
        holder.especialidad.setText(cuidador.getEspecialidad());

        // Verificar la URL de la imagen
        String fotoPerfil = cuidador.getfotoPerfil ();
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            fotoPerfil = fotoPerfil.trim();  // Eliminar espacios en blanco

            Glide.with(holder.itemView.getContext())
                    .load(cuidador.getfotoPerfil()) // Cambiar getImagenUrl() por getFotoPerfil()
                    .placeholder(R.drawable.avatar)
                    .into(holder.imagen);


        } else {
            holder.imagen.setImageResource(R.drawable.avatar); // Imagen por defecto si no hay URL
        }

        Log.d("FirebaseImage", "URL de la imagen: " + fotoPerfil);
        Log.d("FirebaseURL", "Cargando imagen desde: " + cuidador.getfotoPerfil ());



        // Configurar botones de aceptar y rechazar
        holder.btnAceptar.setOnClickListener(v -> listener.onAceptarClick(cuidador));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazarClick(cuidador));
    }

    @Override
    public int getItemCount() {
        return listaCuidadores.size();
    }

    public static class CuidadorViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre, correo, telefono, especialidad;
        Button btnAceptar, btnRechazar;

        public CuidadorViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen_cuidador);
            nombre = itemView.findViewById(R.id.nombre_cuidador);
            correo = itemView.findViewById(R.id.correo_cuidador);
            telefono = itemView.findViewById(R.id.telefono_cuidador);
            especialidad = itemView.findViewById(R.id.especialidad_cuidador);
            btnAceptar = itemView.findViewById(R.id.btn_aceptar);
            btnRechazar = itemView.findViewById(R.id.btn_rechazar);
        }
    }
}
