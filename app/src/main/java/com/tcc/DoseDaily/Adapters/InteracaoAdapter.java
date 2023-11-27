package com.tcc.DoseDaily.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcc.DoseDaily.API.Interacao;
import com.tcc.DoseDaily.R;

import java.util.List;

public class InteracaoAdapter extends RecyclerView.Adapter<InteracaoAdapter.ViewHolder> {

    private List<Interacao> interacoes;

    public InteracaoAdapter(List<Interacao> interacoes) {
        this.interacoes = interacoes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View interacaoView = inflater.inflate(R.layout.item_interacao, parent, false);
        return new ViewHolder(interacaoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Interacao interacao = interacoes.get(position);

        holder.gravidadeTextView.setText("Gravidade: " + interacao.getGravidade());
        holder.evidenciaTextView.setText("Evidência: " + interacao.getEvidencia());
        holder.acaoTextView.setText("Ação: " + interacao.getAcao());
        // Adicione outros campos conforme necessário
    }

    @Override
    public int getItemCount() {
        return interacoes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView gravidadeTextView;
        public TextView evidenciaTextView;
        public TextView acaoTextView;
        // Adicione outros TextViews conforme necessário

        public ViewHolder(View itemView) {
            super(itemView);
            gravidadeTextView = itemView.findViewById(R.id.recGravidade);
            evidenciaTextView = itemView.findViewById(R.id.recEvidencia);
            acaoTextView = itemView.findViewById(R.id.recAcao);
            // Inicialize outros TextViews conforme necessário
        }
    }
}
