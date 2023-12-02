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

public class HistoricAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CONTENT_TYPE = 1;
    private static final int SECTION_TYPE = 0;


    private List<Item> itemList;

    public interface Item {
        int getItemType();
    }

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
