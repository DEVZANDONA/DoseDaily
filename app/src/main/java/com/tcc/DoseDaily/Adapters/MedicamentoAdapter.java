package com.tcc.DoseDaily.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcc.DoseDaily.API.Medicamento;
import com.tcc.DoseDaily.R;

import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.ViewHolder> {
    private List<Medicamento> medicamentos;
    private OnItemClickListener onItemClickListener;

    public MedicamentoAdapter(List<Medicamento> medicamentos, OnItemClickListener onItemClickListener) {
        this.medicamentos = medicamentos;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View medicamentoView = inflater.inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(medicamentoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicamento medicamento = medicamentos.get(position);

        holder.nomeTextView.setText(medicamento.getNome());
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(medicamento);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.recTitle);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Medicamento medicamento);
    }
}
