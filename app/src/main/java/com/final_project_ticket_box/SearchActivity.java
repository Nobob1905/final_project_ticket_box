package com.final_project_ticket_box;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.final_project_ticket_box.Adapter.EventListAdapter;
import com.final_project_ticket_box.Models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.final_project_ticket_box.databinding.ActivitySearchBinding;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private FirebaseDatabase database;
    private EventListAdapter adapter;
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Event> filteredList = new ArrayList<>();
    private String selectedGenre = "All";
    private String selectedDate = "";  // Biến để lưu ngày đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        fetchEvents();
    }

    private void setupUI() {
        // Back button functionality
        binding.backBtn.setOnClickListener(v -> finish());

        // RecyclerView setup
        adapter = new EventListAdapter(filteredList);
        binding.recyclerViewResult.setLayoutManager(new LinearLayoutManager(this,
                                                        LinearLayoutManager.HORIZONTAL,
                                                        false // không đảo ngược thứ tự
        ));
        binding.recyclerViewResult.setAdapter(adapter);

        // Spinner setup

        String[] generEvent  = {"All", "Concert", "Music", "Live Show", "Cultural"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                generEvent);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        binding.spinnerFiterGenre.setAdapter(spinnerAdapter);

        binding.spinnerFiterGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenre = parent.getItemAtPosition(position).toString();
                filterEvents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Search functionality
        binding.editTextText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        // Date filter button functionality
        binding.btnFilterDate.setOnClickListener(v -> showDatePickerDialog());
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Format the selected date as yyyy-MM-dd
                selectedDate = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                filterEvents();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Lắng nghe sự kiện "Cancel"
        datePickerDialog.setOnCancelListener(dialog -> {
            // Khi người dùng nhấn Cancel, đặt lại selectedDate về rỗng
            selectedDate = "";
            filterEvents(); // Cập nhật lại kết quả tìm kiếm mà không lọc theo ngày
        });

        datePickerDialog.show();
    }



    private void fetchEvents() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("Items");
        DatabaseReference upcomingRef = FirebaseDatabase.getInstance().getReference("Upcomming");
        binding.progressBarResult.setVisibility(View.VISIBLE);

        // Xóa danh sách hiện tại
        eventList.clear();

        // Đọc dữ liệu từ "Items"
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Event event = data.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }

                // Đọc dữ liệu từ "Upcoming" sau khi hoàn thành "Items"
                upcomingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Event event = data.getValue(Event.class);
                            if (event != null) {
                                eventList.add(event);
                            }
                        }

                        // Lọc kết quả sau khi tải xong cả hai nhánh
                        filterEvents();
                        binding.progressBarResult.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchActivity.this, "Failed to load events from Upcoming", Toast.LENGTH_SHORT).show();
                        binding.progressBarResult.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Failed to load events from Items", Toast.LENGTH_SHORT).show();
                binding.progressBarResult.setVisibility(View.GONE);
            }
        });
    }


    private void filterEvents() {
        String query = binding.editTextText.getText().toString().toLowerCase();
        filteredList.clear();

        // Kiểm tra ngày đã chọn
        String formattedSelectedDate = selectedDate; // Ngày người dùng chọn (ví dụ: "2024-12-19")

        for (Event event : eventList) {
            boolean matchesGenre = selectedGenre.equals("All") || event.getGenre().contains(selectedGenre);
            boolean matchesQuery = event.getTitle().toLowerCase().contains(query);
            boolean matchesDate = formattedSelectedDate.isEmpty() || event.getDate().equals(formattedSelectedDate); // So sánh ngày

            if (matchesGenre && matchesQuery && matchesDate) {
                filteredList.add(event);
            }
        }

        adapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
        }
    }

}
