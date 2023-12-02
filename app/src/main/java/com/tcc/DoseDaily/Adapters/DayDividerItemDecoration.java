package com.tcc.DoseDaily.Adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tcc.DoseDaily.R;

public class DayDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final int dividerHeight;
    private final Paint paint;

    public DayDividerItemDecoration(Context context, int dividerHeight) {
        this.dividerHeight = dividerHeight;
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.black));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + dividerHeight;

            c.drawRect(left, top, right, bottom, paint);
        }
    }
}

