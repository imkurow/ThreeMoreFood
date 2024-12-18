package com.example.finalmp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private List<Order> orders;
    private SimpleDateFormat dateFormat;

    public OrderHistoryAdapter(List<Order> orders) {
        this.orders = orders;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id"));
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateData(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId, textOrderDate, textOrderStatus,
                textOrderTotal, textOrderAddress;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.textOrderId);
            textOrderDate = itemView.findViewById(R.id.textOrderDate);
            textOrderStatus = itemView.findViewById(R.id.textOrderStatus);
            textOrderTotal = itemView.findViewById(R.id.textOrderTotal);
            textOrderAddress = itemView.findViewById(R.id.textOrderAddress);
        }

        void bind(Order order) {
            textOrderId.setText("Order #" + order.getOrderId());
            textOrderDate.setText("Tanggal: " +
                    dateFormat.format(new Date(order.getOrderTime())));

            // Convert status to display name
            String statusDisplay;
            try {
                OrderStatus status = OrderStatus.valueOf(order.getStatus());
                statusDisplay = status.getDisplayName();
            } catch (IllegalArgumentException e) {
                statusDisplay = order.getStatus(); // Fallback to raw status
            }

            textOrderStatus.setText("Status: " + statusDisplay);
            textOrderTotal.setText(String.format(Locale.getDefault(),
                    "Total: Rp %,d", (int) order.getTotalAmount()));
            textOrderAddress.setText("Alamat: " + order.getDeliveryAddress());

            // Optional: Set different colors for different statuses
            int statusColor;
            try {
                OrderStatus status = OrderStatus.valueOf(order.getStatus());
                switch (status) {
                    case COMPLETED:
                        statusColor = itemView.getContext().getColor(android.R.color.holo_green_light);
                        break;
                    case CANCELLED:
                        statusColor = itemView.getContext().getColor(android.R.color.holo_red_light);
                        break;
                    case ON_DELIVERY:
                        statusColor = itemView.getContext().getColor(android.R.color.holo_blue_light);
                        break;
                    case PROCESSING:
                        statusColor = itemView.getContext().getColor(android.R.color.holo_orange_light);
                        break;
                    default:
                        statusColor = itemView.getContext().getColor(android.R.color.white);
                        break;
                }
                textOrderStatus.setTextColor(statusColor);
            } catch (IllegalArgumentException e) {
                textOrderStatus.setTextColor(itemView.getContext().getColor(android.R.color.white));
            }
        }
    }
}