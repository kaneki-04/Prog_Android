package com.example.laptopfixlog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import com.example.laptopfixlog.R;
import com.example.laptopfixlog.model.Repair;

public class RepairAdapter extends RecyclerView.Adapter<RepairAdapter.RepairViewHolder> {

    private ArrayList<Repair> repairList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Repair repair, int position);
    }

    public RepairAdapter(ArrayList<Repair> repairList, OnItemClickListener listener) {
        this.repairList = repairList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RepairViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair, parent, false);
        return new RepairViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RepairViewHolder holder, int position) {
        Repair repair = repairList.get(position);
        holder.tvModel.setText(repair.getModeloLaptop());
        holder.tvClient.setText("Cliente: " + repair.getNombreCliente());
        holder.tvType.setText("Reparación: " + repair.getTipoReparacion());
        holder.tvStatus.setText(repair.getEstado());
        holder.tvCostDate.setText("Costo: $" + repair.getCosto() + " | Fecha: " + repair.getFecha());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(repair, position));
    }

    @Override
    public int getItemCount() {
        return repairList.size();
    }

    public void updateList(ArrayList<Repair> newList) {
        repairList = newList;
        notifyDataSetChanged();
    }

    public static class RepairViewHolder extends RecyclerView.ViewHolder {
        TextView tvModel, tvClient, tvType, tvStatus, tvCostDate;

        public RepairViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvClient = itemView.findViewById(R.id.tvClient);
            tvType = itemView.findViewById(R.id.tvType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCostDate = itemView.findViewById(R.id.tvCostDate);
        }
    }
}