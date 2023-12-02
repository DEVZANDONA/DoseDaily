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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<Notifications> notificationsList;
    private List<Notifications> filteredList;
    private OnItemClickListener clickListener;


    public NotificationsAdapter(List<Notifications> notificationsList, OnItemClickListener clickListener) {
        this.notificationsList = notificationsList;
        this.filteredList = new ArrayList<>(notificationsList);
        this.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Notifications notification, int position);
    }


    public void filterList(List<Notifications> filteredList) {
        this.filteredList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    public Notifications getFilteredItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        View notificationView = inflater.inflate(R.layout.item_notification, parent, false);


        return new ViewHolder(notificationView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifications notification = filteredList.get(position);


        if (notification != null) {

            holder.bind(notification);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        clickListener.onItemClick(notification, position);
                    }
                }
            });

        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView recImage;
        public TextView recTitle;
        public TextView recData;
        public TextView recHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recImage = itemView.findViewById(R.id.recImage);
            recTitle = itemView.findViewById(R.id.recTitle);
            recData = itemView.findViewById(R.id.recData);
            recHora = itemView.findViewById(R.id.recHora);
        }


        public void bind(Notifications notification) {
            recTitle.setText(notification.getTitulo());


            SimpleDateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date date = dateFormatInput.parse(notification.getTempoNotificacao());
                String formattedDate = dateFormatOutput.format(date);
                recData.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                Date date = dateFormatInput.parse(notification.getTempoNotificacao());
                SimpleDateFormat timeFormatOutput = new SimpleDateFormat("HH:mm");
                String formattedTime = timeFormatOutput.format(date);
                recHora.setText(formattedTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}