package com.final_project_ticket_box;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.final_project_ticket_box.Adapter.TicketAdapter;
import com.final_project_ticket_box.Models.Ticket;
import com.final_project_ticket_box.databinding.FragmentMyTicketBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyTicketFragment extends Fragment {

    private FragmentMyTicketBinding binding;
    private List<Ticket> ticketList = new ArrayList<>();
    private TicketAdapter ticketAdapter;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyTicketBinding.inflate(inflater, container, false);

        initRecyclerView();
        fetchOrders();

        // Ánh xạ nút back và xử lý sự kiện click
        ImageView backBtn = binding.getRoot().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> requireActivity().onBackPressed()); // Quay lại màn hình trước


        return binding.getRoot();
    }

    private void initRecyclerView() {
        ticketAdapter = new TicketAdapter(ticketList, ticket -> {
            // Tạo Intent để chuyển đến TicketDetailActivity
            Intent intent = new Intent(getContext(), TicketDetailActivity.class);

            // Truyền đối tượng Ticket vào Intent
            intent.putExtra("ticket", ticket);

            // Khởi chạy TicketDetailActivity
            startActivity(intent);
        });

        binding.ticketRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ticketRecyclerView.setAdapter(ticketAdapter);
    }

    private void fetchOrders() {
        auth = FirebaseAuth.getInstance();
        String userEmail = auth.getCurrentUser().getEmail();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticketList.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String customerEmail = orderSnapshot.child("customerEmail").getValue(String.class);
                    if (userEmail.equals(customerEmail)) {
                        // Map fields manually
                        String eventName = orderSnapshot.child("eventName").getValue(String.class);
                        String paymentMethod = orderSnapshot.child("paymentMethod").getValue(String.class);
                        String selectedSeats = orderSnapshot.child("selectedSeats").getValue(String.class);
                        String ticketCode = orderSnapshot.child("ticketCode").getValue(String.class);
                        double totalPrice = orderSnapshot.child("totalPrice").getValue(Double.class);

                        Ticket ticket = new Ticket();
                        ticket.setEventName(eventName);
                        ticket.setPaymentMethod(paymentMethod);
                        ticket.setSelectedSeats(selectedSeats);
                        ticket.setTicketCode(ticketCode);
                        ticket.setTotalPrice(totalPrice);

                        ticketList.add(ticket);
                    }
                }
                ticketAdapter.notifyDataSetChanged(); // Notify adapter about data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MyTicketFragment", "Database error: " + error.getMessage());
            }
        });
    }
}
