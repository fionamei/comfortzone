package com.example.comfortzone.profile.callback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comfortzone.R;
import com.example.comfortzone.profile.ui.SavedCitiesAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final ColorDrawable background;
    private final int backgroundCornerOffset = 50;
    private SavedCitiesAdapter savedCitiesAdapter;
    private Drawable icon;

    public SwipeToDeleteCallback(SavedCitiesAdapter savedCitiesAdapter, Context context) {
        super(0, ItemTouchHelper.LEFT);
        this.savedCitiesAdapter = savedCitiesAdapter;
        icon = ContextCompat.getDrawable(context,
                R.drawable.ic_baseline_delete_24);
        background = new ColorDrawable(context.getResources().getColor(R.color.fillColorBlue));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        savedCitiesAdapter.deleteItem(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        if (dX < 0) { // Swiping to the left
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + iconMargin;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);
            icon.draw(c);
        }
    }
}
