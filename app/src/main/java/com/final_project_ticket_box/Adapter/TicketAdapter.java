package com.final_project_ticket_box.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.final_project_ticket_box.Models.Ticket;
import com.final_project_ticket_box.R;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<Ticket> ticketList;
    private final OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    public TicketAdapter(List<Ticket> ticketList, OnTicketClickListener listener) {
        this.ticketList = ticketList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view);
    }

    public String getFormattedTime(String timestamp) {
        // Kiểm tra độ dài chuỗi và đảm bảo rằng nó có đủ thông tin
        if (timestamp.length() == 14) {
            // Lấy phần giờ và phút từ chuỗi
            String hour = timestamp.substring(8, 10);  // Giờ (HH)
            String minute = timestamp.substring(10, 12);  // Phút (mm)

            // Định dạng lại thời gian theo kiểu "00h52'"
            return hour + "h " + minute + "'";
        } else {
            // Nếu chuỗi không đúng định dạng, trả về giá trị mặc định
            return "Invalid timestamp";
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        holder.eventName.setText(ticket.getEventName());
        holder.bookingDate.setText("Date: " + ticket.getDate());
        holder.bookingTime.setText("Time: " + getFormattedTime( ticket.getDate())); // Example: Extract time
        holder.paymentMethod.setText("Payment: " + ticket.getPaymentMethod());

        holder.itemView.setOnClickListener(v -> listener.onTicketClick(ticket));
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView eventName, bookingDate, bookingTime, paymentMethod;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventName);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingTime = itemView.findViewById(R.id.bookingTime);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
        }
    }
}
