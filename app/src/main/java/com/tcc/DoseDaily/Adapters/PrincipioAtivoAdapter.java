package com.tcc.DoseDaily.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcc.DoseDaily.API.Interacao;
import com.tcc.DoseDaily.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrincipioAtivoAdapter extends RecyclerView.Adapter<PrincipioAtivoAdapter.PrincipioAtivoViewHolder> {

    private List<Interacao.PrincipioAtivo> principiosAtivos;
    private OnItemClickListener listener;
    private Set<Integer> selecionados = new HashSet<>();

    public interface OnItemClickListener {
        void onItemClick(Interacao.PrincipioAtivo principioAtivo);
    }

    public PrincipioAtivoAdapter(List<Interacao.PrincipioAtivo> principiosAtivos, OnItemClickListener listener) {
        this.principiosAtivos = principiosAtivos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PrincipioAtivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_principio, parent, false);
        return new PrincipioAtivoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PrincipioAtivoViewHolder holder, int position) {
        Interacao.PrincipioAtivo principioAtivo = principiosAtivos.get(position);
        holder.bind(principioAtivo, listener);

        if (selecionados.contains(position)) {
            holder.showSelectedIcon();
        } else {
            holder.hideSelectedIcon();
        }
    }

    @Override
    public int getItemCount() {
        return principiosAtivos.size();
    }

    public class PrincipioAtivoViewHolder extends RecyclerView.ViewHolder {
        private TextView nomeTextView;
        private ImageView selectedIcon;

        public PrincipioAtivoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.recTitle);
            selectedIcon = itemView.findViewById(R.id.selectedIcon);
        }

        public void bind(final Interacao.PrincipioAtivo principioAtivo, final OnItemClickListener listener) {
            nomeTextView.setText(principioAtivo.getNome());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(principioAtivo);
                    int position = getAdapterPosition();
                    if (selecionados.contains(position)) {
                        selecionados.remove(position);
                    } else {
                        selecionados.add(position);
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void showSelectedIcon() {
            selectedIcon.setVisibility(View.VISIBLE);
        }

        public void hideSelectedIcon() {
            selectedIcon.setVisibility(View.GONE);
        }
    }

    // Adicione o método clearSelection aqui
    public void clearSelection() {
        selecionados.clear();
        notifyDataSetChanged();
    }
}
