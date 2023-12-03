package com.tcc.DoseDaily.Adapters;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcc.DoseDaily.Models.HistoryItem;
import com.tcc.DoseDaily.R;

import java.util.ArrayList;
import java.util.List;

//Adaptador personalizado para o historico
public class HistoricAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CONTENT_TYPE = 1;
    private static final int SECTION_TYPE = 0;


    //Recebe os itens do histórico
    private List<Item> itemList;


    //Item é uma interface que define o método
    public interface Item {
        int getItemType();
    }

    //Uma classe que implementa a interface Item e representa uma seção na lista
    public static class SectionItem implements Item {
        private String sectionText;
        private boolean isExpanded;
        private List<ContentItem> contentItems;

        public SectionItem(String sectionText) {
            this.sectionText = sectionText;
            this.isExpanded = false;
            this.contentItems = new ArrayList<>();
        }

        public String getSectionText() {
            return sectionText;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }

        public List<ContentItem> getContentItems() {
            return contentItems;
        }

        public void addContentItem(ContentItem contentItem) {
            contentItems.add(contentItem);
        }

        @Override
        public int getItemType() {
            return SECTION_TYPE;
        }
    }

    //Uma classe que implementa a interface Item e representa um item de conteúdo na lista
    public static class ContentItem implements Item {
        private HistoryItem historyItem;

        public ContentItem(HistoryItem historyItem) {
            this.historyItem = historyItem;
        }

        public HistoryItem getHistoryItem() {
            return historyItem;
        }

        @Override
        public int getItemType() {
            return CONTENT_TYPE;
        }
    }

    public HistoricAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    //Infla o layout com base do tipo de item (SECTION_TYPE ou CONTENT_TYPE) e retorna o respectivo ViewHolder.
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == SECTION_TYPE) {
            view = inflater.inflate(R.layout.section_item_layout, parent, false);
            return new SectionViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.content_item_layout, parent, false);
            return new ContentViewHolder(view);
        }
    }


    //Vincula os dados do item à View no item.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        if (holder.getItemViewType() == SECTION_TYPE) {
            SectionItem sectionItem = (SectionItem) item;
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            sectionViewHolder.bind(sectionItem);


            sectionViewHolder.setaImageView.setOnClickListener(v -> {
                sectionItem.setExpanded(!sectionItem.isExpanded());

                if (sectionItem.isExpanded() && !sectionItem.getContentItems().isEmpty()) {

                    int contentPosition = itemList.indexOf(sectionItem) + 1;
                    itemList.addAll(contentPosition, sectionItem.getContentItems());
                    sectionViewHolder.setaImageView.setImageResource(R.drawable.seta_baixo);
                    notifyItemRangeInserted(contentPosition, sectionItem.getContentItems().size());
                } else {

                    int contentPosition = itemList.indexOf(sectionItem) + 1;
                    itemList.removeAll(sectionItem.getContentItems());
                    sectionViewHolder.setaImageView.setImageResource(R.drawable.seta_lado);
                    notifyItemRangeRemoved(contentPosition, sectionItem.getContentItems().size());
                }
            });
        } else {
            ContentItem contentItem = (ContentItem) item;
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            contentViewHolder.bind(contentItem);
        }
    }


    // Retorna o número total de itens na lista.
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getItemType();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionTextView;
        private ImageView setaImageView;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTextView = itemView.findViewById(R.id.sectionTextView);
            setaImageView = itemView.findViewById(R.id.setaImageView);

            sectionTextView.setTypeface(null, Typeface.BOLD);
            sectionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        }

        public void bind(SectionItem sectionItem) {
            sectionTextView.setText(sectionItem.getSectionText());

            setaImageView.setOnClickListener(v -> {
                sectionItem.setExpanded(!sectionItem.isExpanded());

            });
        }
    }


    //Configura o título e a data com base no HistoryItem.
    static class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView recTitle;
        private TextView recData;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            recTitle = itemView.findViewById(R.id.recTitle);
            recData = itemView.findViewById(R.id.recData);
        }

        public void bind(ContentItem contentItem) {
            HistoryItem historyItem = contentItem.getHistoryItem();
            recTitle.setText(historyItem.getMedicationName());
            recData.setText(historyItem.getConsumptionTime());
        }
    }
}
