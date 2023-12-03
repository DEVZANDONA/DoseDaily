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

//Criado para decorar as seçoes da lista
public class DayDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final int dividerHeight;
    private final Paint paint;

    //construtor altura e o paint
    public DayDividerItemDecoration(Context context, int dividerHeight) {
        this.dividerHeight = dividerHeight;
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.black));
    }

    //define a margem inferior do item para ser igual à altura do divisor
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    //esse método é chamado para desenhar os divisores na tela.
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        //calcula as posições de início e fim horizontal dos divisores.
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

