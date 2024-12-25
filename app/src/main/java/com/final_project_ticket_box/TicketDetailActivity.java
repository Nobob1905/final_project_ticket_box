package com.final_project_ticket_box;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.final_project_ticket_box.Models.Ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class TicketDetailActivity extends AppCompatActivity {

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_detail);

        // Get the ticket object from the Intent
        Intent intent = getIntent();
        Ticket ticket = intent.getParcelableExtra("ticket");
        Log.e("TicketDetailActivity",ticket.toString());


        backBtn = findViewById(R.id.backBtn);

        // Thiết lập sự kiện click cho backBtn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (ticket != null) {
            String ticketCode = ticket.getTicketCode();
            if (ticketCode != null) {
                fetchTicketDetails(ticketCode);
            } else {
                Toast.makeText(this, "Ticket code is missing", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if the ticket code is missing
            }
        } else {
            Toast.makeText(this, "Ticket data is missing", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the ticket object is null
        }
    }

    private void fetchTicketDetails(String ticketCode) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        databaseReference.orderByChild("ticketCode").equalTo(ticketCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                                // Lấy thông tin vé từ Firebase
                                String customerName = ticketSnapshot.child("customerName").getValue(String.class);
                                String customerEmail = ticketSnapshot.child("customerEmail").getValue(String.class);
                                String eventName = ticketSnapshot.child("eventName").getValue(String.class);
                                String selectedSeats = ticketSnapshot.child("selectedSeats").getValue(String.class);
                                String paymentMethod = ticketSnapshot.child("paymentMethod").getValue(String.class);
                                double totalPrice = ticketSnapshot.child("totalPrice").getValue(Double.class);

                                // Hiển thị thông tin lên UI
                                populateUI(ticketCode, customerName, customerEmail, eventName, selectedSeats, paymentMethod, totalPrice);
                            }
                        } else {
                            Toast.makeText(TicketDetailActivity.this, "Ticket not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                        Toast.makeText(TicketDetailActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateUI(String ticketCode, String customerName, String customerEmail,
                            String eventName, String selectedSeats, String paymentMethod, double totalPrice) {
        TextView ticketCodeText = findViewById(R.id.textIdTicket);
        TextView eventNameText = findViewById(R.id.textEvent);
        TextView customerNameText = findViewById(R.id.textCustomerName);
        TextView customerEmailText = findViewById(R.id.textCustomerEmail);
        TextView seatsText = findViewById(R.id.textPosition);
        TextView paymentMethodText = findViewById(R.id.paymentMethod);
        TextView totalPriceText = findViewById(R.id.priceTxt);
        ImageView qrImageView = findViewById(R.id.imageQR);

        // Gán dữ liệu vào các TextView
        ticketCode = ticketCode.length() >= 12 ? ticketCode.substring(0, 12) : ticketCode;
        ticketCodeText.setText(ticketCode);
        eventNameText.setText(eventName);
        customerNameText.setText(customerName);
        customerEmailText.setText(customerEmail);
        seatsText.setText(selectedSeats);
        paymentMethodText.setText(paymentMethod);
        totalPriceText.setText("$" + totalPrice);

        // Tạo QR Code từ ticketCode
        generateQRCode(ticketCode, qrImageView);
    }

    private void generateQRCode(String text, ImageView imageView) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 500, 500);
            imageView.setImageBitmap(bitmap); // Set Bitmap cho ImageView
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
