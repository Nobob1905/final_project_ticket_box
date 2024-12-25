package com.final_project_ticket_box;

import android.app.AlertDialog;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.final_project_ticket_box.Adapter.SeatListAdapter;
import com.final_project_ticket_box.Models.Event;
import com.final_project_ticket_box.Models.Order;
import com.final_project_ticket_box.Models.Seat;
import com.final_project_ticket_box.databinding.ActivitySeatListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;

public class SeatListActivity extends AppCompatActivity {

    private ActivitySeatListBinding binding;
    private Event event;
    private double price = 0.0;
    private int number = 0;
    private List<Integer> selectedSeatIndices = new ArrayList<>();
    private List<Integer> unavailableSeats = new ArrayList<>(); // Danh sách ghế không khả dụng
    private  Seat selectedSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
        fetchUnavailableSeats(); // Lấy dữ liệu ghế không khả dụng từ Firebase
//        initSeatsList();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    // Lấy dữ liệu từ Firebase để cập nhật danh sách ghế không khả dụng
    private void fetchUnavailableSeats() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders");

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Integer> unavailableSeats1 = new ArrayList<>(); // Ghế không khả dụng cho RecyclerView1
                List<Integer> unavailableSeats2 = new ArrayList<>(); // Ghế không khả dụng cho RecyclerView2

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    // Lấy thông tin Order từ Firebase
                    Order order = orderSnapshot.getValue(Order.class);

                    if (order != null && order.getEventName().equals(event.getTitle())) {
                        // Lấy danh sách ghế từ đơn hàng
                        String[] seats = order.getSelectedSeats().split(", "); // Các ghế được chọn trong đơn hàng
                        for (String seat : seats) {
                            try {
                                int seatNumber = Integer.parseInt(seat);
                                if (seatNumber < 45) {
                                    unavailableSeats1.add(seatNumber); // Ghế thuộc RecyclerView1
                                } else {
                                    unavailableSeats2.add(seatNumber - 45); // Ghế thuộc RecyclerView2
                                }
                            } catch (NumberFormatException e) {
                                Log.e("SeatListActivity", "Invalid seat number: " + seat);
                            }
                        }
                    }
                }
                // Sau khi có danh sách ghế không khả dụng, khởi tạo danh sách ghế
                // Khởi tạo RecyclerView với danh sách ghế không khả dụng tương ứng
                initSeatRecyclerView(0, 45, binding.seatRecyclerview, unavailableSeats1);
                initSeatRecyclerView(45, 90, binding.seatRecyclerview1, unavailableSeats2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SeatListActivity", "Failed to fetch orders: " + databaseError.getMessage());
            }
        });
    }

//    private void initSeatsList() {
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return (position % 7 == 3) ? 1 : 1;
//            }
//        });
//
//        binding.seatRecyclerview.setLayoutManager(gridLayoutManager);
//
//        List<Seat> seatList = new ArrayList<>();
//        int numberSeats = 45;
//
//        for (int i = 0; i < numberSeats; i++) {
//            String seatName = "";
//            Seat.SeatStatus seatStatus;
//
//            if (unavailableSeats.contains(i)) {
//                seatStatus = Seat.SeatStatus.UNAVAILABLE; // Ghế không khả dụng
//            } else if ((i >= 33  && i <= 44)) {
//                seatStatus = Seat.SeatStatus.VIP; // Ghế VIP
//            } else {
//                seatStatus = Seat.SeatStatus.AVAILABLE; // Ghế khả dụng
//            }
//
//            seatList.add(new Seat(seatStatus, seatName));
//        }
//
//        SeatListAdapter seatAdapter = new SeatListAdapter(seatList, this, new SeatListAdapter.SelectedSeat() {
//            @Override
//            public void onReturn(int position, int num) {
//                selectedSeat = seatList.get(position);
//
//                if (selectedSeat.isSelected()) {
//                    selectedSeat.setSelected(false); // Unselect the seat
//                    selectedSeatIndices.remove(Integer.valueOf(position)); // Remove from the list of selected seats
//                } else {
//                    selectedSeat.setSelected(true); // Select the seat
//                    selectedSeatIndices.add(position); // Add to the list of selected seats
//                }
//
//                binding.numberSelectedTxt.setText(num + " Seat Selected");
//                DecimalFormat df = new DecimalFormat("#.##");
//                price = Double.parseDouble(df.format(num * event.getPrice()));
//                number = num;
//                binding.priceTxt.setText("$" + price);
//            }
//            @Override
//            public void onPriceChanged(double newPrice) {
//                // Cập nhật giá trong Activity khi có sự thay đổi giá ghế
//                price = newPrice;
//                binding.priceTxt.setText("$" + price);
//            }
//        }, event);
//
//        binding.seatRecyclerview.setAdapter(seatAdapter);
//        binding.seatRecyclerview.setNestedScrollingEnabled(false);
//    }

    private void initSeatRecyclerView(int startIndex, int endIndex, RecyclerView recyclerView, List<Integer> unavailableSeats) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        recyclerView.setLayoutManager(gridLayoutManager);

        List<Seat> seatList = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            Seat.SeatStatus seatStatus;

            if (unavailableSeats.contains(i - startIndex)) {
                seatStatus = Seat.SeatStatus.UNAVAILABLE; // Ghế không khả dụng
            } else if (i >= endIndex - 12) {
                seatStatus = Seat.SeatStatus.VIP; // Ghế VIP
            } else {
                seatStatus = Seat.SeatStatus.AVAILABLE; // Ghế khả dụng
            }

            seatList.add(new Seat(seatStatus, ""));
        }

        SeatListAdapter seatAdapter = new SeatListAdapter(seatList, this, new SeatListAdapter.SelectedSeat() {
            @Override
            public void onReturn(int position, int num) {
                int globalPosition = position + startIndex;

                Seat selectedSeat = seatList.get(position);
                if (selectedSeat.isSelected()) {
                    selectedSeat.setSelected(false); // Unselect seat
                    selectedSeatIndices.remove(Integer.valueOf(globalPosition));
                } else {
                    selectedSeat.setSelected(true); // Select seat
                    selectedSeatIndices.add(globalPosition);
                }

                binding.numberSelectedTxt.setText(num + " Seat Selected");
                DecimalFormat df = new DecimalFormat("#.##");
                price = Double.parseDouble(df.format(num * event.getPrice()));
                number = num;
                binding.priceTxt.setText("$" + price);
            }

            @Override
            public void onPriceChanged(double newPrice) {
                price = newPrice;
                binding.priceTxt.setText("$" + price);
            }
        }, event);

        recyclerView.setAdapter(seatAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // Xử lý nút Pay
        binding.payButton.setOnClickListener(v -> {
            if(price > 0) {
                // Convert the selected seat indices list to a string (space-separated)
                String selectedSeatsString = TextUtils.join(" ", selectedSeatIndices);

                Intent intent = new Intent(SeatListActivity.this, PaymentActivity.class);
                intent.putIntegerArrayListExtra("selectedSeats", new ArrayList<>(selectedSeatIndices)); // Pass the selected seats list
                intent.putExtra("totalPrice", price);    // Truyền tổng giá
                intent.putExtra("event", event);        // Truyền thông tin sự kiện
                startActivity(intent);
            }
            else {
                new AlertDialog.Builder(SeatListActivity.this)
                        .setTitle("No Seat Selected")
                        .setMessage("Please select a seat before proceeding to payment")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void getIntentExtra() {
        event = getIntent().getParcelableExtra("event");
    }
}
