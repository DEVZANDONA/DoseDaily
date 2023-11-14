package com.tcc.DoseDaily.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.tcc.DoseDaily.Models.Notifications;
import com.tcc.DoseDaily.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<Notifications> notificationsList;
    private List<Notifications> filteredList; // Lista filtrada
    private OnItemClickListener clickListener;

    // Construtor que aceita a lista de notificações e o ouvinte de cliques
    public NotificationsAdapter(List<Notifications> notificationsList, OnItemClickListener clickListener) {
        this.notificationsList = notificationsList;
        this.filteredList = new ArrayList<>(notificationsList); // Inicializa a lista filtrada
        this.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Notifications notification);

        void onItemClick(int position);
    }

    // Método para filtrar a lista com base na consulta de pesquisa
    public void filterList(List<Notifications> filteredList) {
        this.filteredList = new ArrayList<>(filteredList); // Atualiza a lista filtrada
        notifyDataSetChanged();
    }

    // Método para obter o item filtrado em uma posição específica
    public Notifications getFilteredItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflar o layout personalizado
        View notificationView = inflater.inflate(R.layout.recycler_item, parent, false);

        // Retornar uma nova instância do ViewHolder
        return new ViewHolder(notificationView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obter os dados do medicamento na posição atual
        Notifications notification = filteredList.get(position);

        // Verificar se o medicamento possui os campos necessários
        if (notification.getCorpo() != null && notification.getTempoNotificacao() != null && notification.getTitulo() != null) {
            // Atualizar as visualizações do ViewHolder com os dados do medicamento
            holder.recTitle.setText(notification.getTitulo());
            holder.recLang.setText(notification.getTempoNotificacao());
            holder.recDesc.setText(notification.getCorpo());

            // Modificação aqui: Adicione o clique na ViewHolder
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        clickListener.onItemClick(notification);
                        clickListener.onItemClick(position);
                    }
                }
            });

        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size(); // Alterado para retornar o tamanho da lista filtrada
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView recImage;
        public TextView recTitle;
        public TextView recLang;
        public TextView recDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recImage = itemView.findViewById(R.id.recImage);
            recTitle = itemView.findViewById(R.id.recTitle);
            recLang = itemView.findViewById(R.id.recLang);
            recDesc = itemView.findViewById(R.id.recDesc);
        }
    }
}
