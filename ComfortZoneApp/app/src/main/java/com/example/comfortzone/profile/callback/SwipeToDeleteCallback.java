package com.example.comfortzone.profile.callback;

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
    private SavedCitiesAdapter savedCitiesAdapter;
    private Drawable icon;
    private final int backgroundCornerOffset = 50;

    public SwipeToDeleteCallback(SavedCitiesAdapter savedCitiesAdapter) {
        super(0, ItemTouchHelper.LEFT);
        this.savedCitiesAdapter = savedCitiesAdapter;
        icon = ContextCompat.getDrawable(savedCitiesAdapter.getContext(),
                R.drawable.ic_baseline_delete_24);
        background = new ColorDrawable(savedCitiesAdapter.getContext().getResources().getColor(R.color.fillColorBlue));
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

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX < 0) { // Swiping to the left
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
