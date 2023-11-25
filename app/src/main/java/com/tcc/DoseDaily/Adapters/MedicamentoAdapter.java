package com.tcc.DoseDaily.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tcc.DoseDaily.API.Medicamento;
import com.tcc.DoseDaily.R;

import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.ViewHolder> {
    private List<List<Medicamento>> medicamentos;

    public MedicamentoAdapter(List<List<Medicamento>> medicamentos) {
        this.medicamentos = medicamentos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View medicamentoView = inflater.inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(medicamentoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medicamento medicamento = (Medicamento) medicamentos.get(position);

        holder.nomeTextView.setText(medicamento.getNome());
        // Adicione outros campos conforme necessário
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeTextView;
        // Adicione outros TextViews conforme necessário

        public ViewHolder(View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.recTitle);
            // Inicialize outros TextViews conforme necessário
        }
    }
}
