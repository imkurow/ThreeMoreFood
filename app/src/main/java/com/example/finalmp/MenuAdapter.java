package com.example.finalmp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import com.bumptech.glide.Glide;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private List<Menu> menuList;

    public MenuAdapter(List<Menu> menuList) {
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = menuList.get(position);
        holder.bind(menu);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public void updateData(List<Menu> newMenuList) {
        menuList = newMenuList;
        notifyDataSetChanged();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMenu;
        TextView textViewName, textViewDescription, textViewPrice;
        RatingBar ratingBar;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMenu = itemView.findViewById(R.id.imageViewMenu);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        void bind(Menu menu) {
            textViewName.setText(menu.getName());
            textViewDescription.setText(menu.getDescription());
            textViewPrice.setText(String.format(Locale.getDefault(),
                    "Rp %,d", (int) menu.getPrice()));
            ratingBar.setRating(menu.getRating());

            Glide.with(itemView.getContext())
                    .load(menu.getImageUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .into(imageViewMenu);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), DetailMenuActivity.class);
                intent.putExtra("menuId", menu.getId());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
