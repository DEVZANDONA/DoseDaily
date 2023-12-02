package com.tcc.DoseDaily.Adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcc.DoseDaily.API.Interacao;
import com.tcc.DoseDaily.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrincipioAtivoAdapter extends RecyclerView.Adapter<PrincipioAtivoAdapter.PrincipioAtivoViewHolder> implements Filterable {

    private List<Interacao.PrincipioAtivo> principiosAtivos;
    private List<Interacao.PrincipioAtivo> principiosAtivosFiltrados;
    private OnItemClickListener listener;
    private Set<Integer> selecionados = new HashSet<>();

    public interface OnItemClickListener {
        void onItemClick(Interacao.PrincipioAtivo principioAtivo);
    }

    public PrincipioAtivoAdapter(List<Interacao.PrincipioAtivo> principiosAtivos, OnItemClickListener listener) {
        this.principiosAtivos = principiosAtivos;
        this.principiosAtivosFiltrados = principiosAtivos; // Inicialmente, ambas as listas são iguais
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
        Interacao.PrincipioAtivo principioAtivo = principiosAtivosFiltrados.get(position);
        holder.bind(principioAtivo, listener);

        if (selecionados.contains(position)) {
            holder.showSelectedIcon();
        } else {
            holder.hideSelectedIcon();
        }
    }

    @Override
    public int getItemCount() {
        return principiosAtivosFiltrados.size();
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                FilterResults results = new FilterResults();

                if (TextUtils.isEmpty(filterPattern)) {
                    results.values = principiosAtivos; // Se estiver vazio, retorna a lista original
                    results.count = principiosAtivos.size();
                } else {
                    List<Interacao.PrincipioAtivo> filteredList = new ArrayList<>();

                    for (Interacao.PrincipioAtivo principioAtivo : principiosAtivos) {
                        if (principioAtivo.getNome().toLowerCase().contains(filterPattern)) {
                            filteredList.add(principioAtivo);
                        }
                    }

                    results.values = filteredList;
                    results.count = filteredList.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                principiosAtivosFiltrados = (List<Interacao.PrincipioAtivo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void clearSelection() {
        selecionados.clear();
        notifyDataSetChanged();
    }
}
