package com.example.finalmp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;
    private SimpleDateFormat dateFormat;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id"));
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateData(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textComment, textDate;
        RatingBar ratingBar;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textComment = itemView.findViewById(R.id.textComment);
            textDate = itemView.findViewById(R.id.textDate);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        void bind(Review review) {
            textUserName.setText(review.getUserName());
            textComment.setText(review.getComment());
            textDate.setText(new SimpleDateFormat("dd MMM yyyy HH:mm",
                    new Locale("id")).format(new Date(review.getTimestamp())));
            ratingBar.setRating(review.getRating());
        }
    }
}