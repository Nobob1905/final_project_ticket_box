package com.final_project_ticket_box;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.final_project_ticket_box.LoginRegister.MainActivity;
import com.final_project_ticket_box.Models.Event;
import com.final_project_ticket_box.Models.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class PaymentActivity extends AppCompatActivity {
    private TextView textIdTicket, textEvent, textCustomerName, textCustomerEmail, textPosition, priceTxt, numberSelectedTxt, discountTxt;
    private Spinner spinnerPaymentMethod;
    private ArrayList<Integer> selectedSeats;
    private String ticketCode, nameEvent, customerName, customerEmail, position, selectedPaymentMethod;
    private Event event;
    private ImageView imageQR, backBtn;
    private double totalPrice, discountFromPoints;
    private FirebaseUser currentUser;
    private LoadingDialog loadingDialog;
    Button confirmPay;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // Initialize UI elements
        textIdTicket = findViewById(R.id.textIdTicket);
        textEvent = findViewById(R.id.textEvent);
        textCustomerName = findViewById(R.id.textCustomerName);
        textCustomerEmail = findViewById(R.id.textCustomerEmail);
        textPosition = findViewById(R.id.textPosition);
        priceTxt = findViewById(R.id.priceTxt);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        numberSelectedTxt = findViewById(R.id.numberSelectedTxt);
        imageQR = findViewById(R.id.imageQR);
        confirmPay =  findViewById(R.id.confirm_button);
        discountTxt = findViewById(R.id.discountTxt);  // New TextView to display the discount

        loadingDialog = new LoadingDialog(PaymentActivity.this, "Your order is processing");

        // Retrieve data from Intent
        selectedSeats = getIntent().getIntegerArrayListExtra("selectedSeats");
        Collections.sort(selectedSeats);
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0);

//        DecimalFormat df = new DecimalFormat("#.##");
//        totalPrice = Double.parseDouble(df.format(totalPrice));

        event = getIntent().getParcelableExtra("event");
        nameEvent = event.getTitle();

        ticketCode = generateEventId(nameEvent);
        String generatedCode = ticketCode.length() >= 12 ? ticketCode.substring(0, 12) : ticketCode;
        customerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        customerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Set UI
        textIdTicket.setText(generatedCode);
        textEvent.setText(event.getTitle());
        textCustomerName.setText(customerName);
        textCustomerEmail.setText(customerEmail);
        position = TextUtils.join(", ", selectedSeats);
        textPosition.setText(position);
        generateQRCode(ticketCode);
//        priceTxt.setText("$" + totalPrice);
        numberSelectedTxt.setText(selectedSeats.size() + " Seat Selected");



        // Define the available payment methods
        String[] paymentMethods = {"Momo", "ZaloPay", "Credit Card", "Google Pay"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, // Use default layout
                paymentMethods
        );
        spinnerPaymentMethod.setAdapter(adapter);


        // Apply the custom dropdown layout
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        // Set the adapter to the Spinner
        spinnerPaymentMethod.setAdapter(adapter);

        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedPaymentMethod = paymentMethods[position];
                // TODO: Xử lý khi chọn phương thức thanh toán (cập nhật dữ liệu hoặc thực hiện các thao tác khác)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                new AlertDialog.Builder(PaymentActivity.this)
                        .setTitle("No Payment Method Selected")
                        .setMessage("Please select payment method")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        backBtn = findViewById(R.id.backBtn);

        // Thiết lập sự kiện click cho backBtn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi người dùng nhấn nút back, quay lại màn hình trước đó
                onBackPressed();
            }
        });

        getUserRewardPoints();
        confirmPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentUser.isAnonymous()) {
                    saveOrder();
                }
            }
        });
    }

    private void getUserRewardPoints() {
        // Lấy UID của người dùng đang đăng nhập
        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.child("rewardPoints").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int rewardPoints = task.getResult().exists() ? task.getResult().getValue(Integer.class) : 0;
                Log.d("PaymentActivity", "Reward Points: " + rewardPoints);

                // Tính toán giảm giá
                double discountPercentage = Math.min(rewardPoints / 1000.0, 10.0); // Giảm giá tối đa 10%
                discountFromPoints = totalPrice * (discountPercentage / 100);

                // Cập nhật giá trị thanh toán sau giảm giá
                totalPrice = totalPrice - discountFromPoints;
                DecimalFormat df = new DecimalFormat("#.#");
                totalPrice = Double.parseDouble(df.format(totalPrice));
                discountFromPoints = Double.parseDouble(df.format(discountFromPoints));
                priceTxt.setText("$" + totalPrice);
                discountTxt.setText("Discount: $" + discountFromPoints);

                // Cập nhật điểm thưởng mới sau khi thanh toán
                int newRewardPoints = rewardPoints + (int) (totalPrice);
                userRef.child("rewardPoints").setValue(newRewardPoints);
            } else {
                Log.e("Firebase", "Error getting reward points", task.getException());
            }
        });
    }

    private void saveOrder() {
        loadingDialog.loadingDialog();

//         public Order(String ticketCode, String eventName, String customerName, String customerEmail,
//                String selectedSeats, String paymentMethod, int totalPrice, Date date)

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Order order = new Order(
                ticketCode,                 // Unique ticket code
                nameEvent,
                customerName,               // Customer's name
                customerEmail,              // Customer's email// Event name
                position,                   // Selected seats (comma-separated)
                selectedPaymentMethod,              // Payment method selected from spinner
                totalPrice,                 // Total price
                timeStamp                  // Order date (current timestamp)
        );

        // Save the order in Firestore
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        databaseReference.push().setValue(order)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismissDialog();
                })
                .addOnFailureListener(e -> {
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setTitle("Error")
                            .setMessage("Failed to save order. Please try again.")
                            .setPositiveButton("OK", null)
                            .show();
                });

    }

    private void generateQRCode(String text) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            // Generate QR Code as Bitmap
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 500, 500);
            imageQR.setImageBitmap(bitmap); // Set the Bitmap to ImageView
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    public static String generateEventId(String eventName) {
        // Get current timestamp in the format "yyyyMMddHHmmss"
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Convert event name to numeric ASCII code
        StringBuilder numericEventName = new StringBuilder();
        for (char c : eventName.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                numericEventName.append((int) c); // Convert char to ASCII
            }
        }

        // Combine timestamp and event name in numeric form to create a unique ID
        return timeStamp + numericEventName.toString();
    }
}
