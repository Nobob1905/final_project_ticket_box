package com.final_project_ticket_box.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.final_project_ticket_box.Models.Event;
import com.final_project_ticket_box.Models.Seat;
import com.final_project_ticket_box.R;
import com.final_project_ticket_box.databinding.SeatItemBinding;

import java.util.ArrayList;
import java.util.List;

public class SeatListAdapter extends RecyclerView.Adapter<SeatListAdapter.SeatViewHolder> {
    private final List<Seat> seatList;
    private final Context context;
    private final SelectedSeat selectedSeat;
//    private final List<String> selectedSeatNames = new ArrayList<>();
    private Event event; // Đối tượng Film được truyền vào Adapter
    private double price = 0.0;
    private int selectedSeatsCount = 0; // Track number of selected seats

    public SeatListAdapter(List<Seat> seatList, Context context, SelectedSeat selectedSeat, Event event) {
        this.seatList = seatList;
        this.context = context;
        this.selectedSeat = selectedSeat;
        this.event = event; // Lưu đối tượng event
    }

    // ViewHolder class
    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        final SeatItemBinding binding;

        public SeatViewHolder(SeatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SeatItemBinding binding = SeatItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new SeatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        holder.binding.seat.setText(seat.getName());

        // Set background và màu sắc dựa vào SeatStatus
        switch (seat.getStatus()) {
            case AVAILABLE:
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_available);
                holder.binding.seat.setTextColor(context.getColor(R.color.white));
                break;
            case SELECTED:
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_selected);
                holder.binding.seat.setTextColor(context.getColor(R.color.black));
                break;
            case UNAVAILABLE:
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_unavailable);
                holder.binding.seat.setTextColor(context.getColor(R.color.grey));
                break;
            case VIP:
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_available_vip); // Hình nền cho ghế VIP
                holder.binding.seat.setTextColor(context.getColor(R.color.vip)); // Màu chữ cho ghế VIP
                break;
        }

        // Xử lý sự kiện click
        holder.binding.seat.setOnClickListener(v -> {
            switch (seat.getStatus()) {
                case AVAILABLE:
                    // Chọn ghế thường
                    seat.setStatus(Seat.SeatStatus.SELECTED);
                    price += event.getPrice(); // Thêm giá ghế thường
                    selectedSeatsCount++;
                    notifyItemChanged(position);
                    break;

                case VIP:
                    // Chọn ghế VIP
                    seat.setStatus(Seat.SeatStatus.SELECTED);
                    price += event.getPrice() + 200.0;  // Ghế VIP cộng thêm 100
                    selectedSeatsCount++;
                    notifyItemChanged(position);
                    break;

                case SELECTED:
                    if ((position >= 33 && position <= 45) || (position >=78  && position <= 90)) {
                        // Giảm giá cho ghế VIP, nhưng không thay đổi trạng thái ghế
                        Log.d("selectedSeatNames SELECTED",position +"");
                        seat.setStatus(Seat.SeatStatus.VIP);
                        price -= event.getPrice() + 200.0; // Trừ giá ghế VIP
                        notifyItemChanged(position);
                    } else {
                        // Bỏ chọn ghế thường
                        seat.setStatus(Seat.SeatStatus.AVAILABLE);
                        Log.d("selectedSeatNames UNSELECTED",position +"");
                        price -= event.getPrice(); // Trừ giá ghế thường
                        selectedSeatsCount--;
                        notifyItemChanged(position);
                    }
                    break;


                default:
                    break;
            }

            selectedSeat.onReturn(position, selectedSeatsCount);
            selectedSeat.onPriceChanged(price);
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    // Callback interface
    public interface SelectedSeat {
        void onReturn(int position, int num);
        void onPriceChanged(double newPrice);
    }
}
