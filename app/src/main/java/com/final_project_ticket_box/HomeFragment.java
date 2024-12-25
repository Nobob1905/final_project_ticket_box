package com.final_project_ticket_box;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.final_project_ticket_box.Adapter.EventListAdapter;
import com.final_project_ticket_box.Adapter.SliderAdapter;

import com.final_project_ticket_box.Models.Event;
import com.final_project_ticket_box.Models.SliderItems;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.final_project_ticket_box.databinding.FrameHomeBinding;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FrameHomeBinding binding;
    private FirebaseDatabase database;
    private final Handler sliderHandler = new Handler();
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            binding.viewPager2.setCurrentItem(binding.viewPager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout using the binding object
        binding = FrameHomeBinding.inflate(inflater, container, false);

        // Set up database and other initializations
        database = FirebaseDatabase.getInstance();

        initBanner();
        initTopMoving();
        initUpcoming();

        EditText searchEditText = binding.editTextText;
        searchEditText.setInputType(InputType.TYPE_NULL); // Ngăn bàn phím xuất hiện
        searchEditText.setOnClickListener(v -> {
            // Chuyển đến trang tìm kiếm
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        return binding.getRoot(); // Return the root view of the binding object
    }

    private void initTopMoving() {
        DatabaseReference myRef = database.getReference("Items");
        binding.progressBarTopMovies.setVisibility(View.VISIBLE);
        ArrayList<Event> items = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Event event = issue.getValue(Event.class);
                        if (event != null) {
                            items.add(event);
                        }
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewTopMovies.setLayoutManager(new LinearLayoutManager(
                                getActivity(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                        ));
                        binding.recyclerViewTopMovies.setAdapter(new EventListAdapter(items));
                    }
                    binding.progressBarTopMovies.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void initUpcoming() {
        DatabaseReference myRef = database.getReference("Upcomming");
        binding.progressBarupcomming.setVisibility(View.VISIBLE);
        ArrayList<Event> items = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Event event = issue.getValue(Event.class);
                        if (event != null) {
                            items.add(event);
                        }
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewUpcomming.setLayoutManager(new LinearLayoutManager(
                                getActivity(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                        ));
                        binding.recyclerViewUpcomming.setAdapter(new EventListAdapter(items));
                    }
                    binding.progressBarupcomming.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banners");
        binding.progressBarSlider.setVisibility(View.VISIBLE);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SliderItems> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    SliderItems list = childSnapshot.getValue(SliderItems.class);
                    if (list != null) {
                        lists.add(list);
                    }
                }
                binding.progressBarSlider.setVisibility(View.GONE);
                setupBanner(lists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void setupBanner(List<SliderItems> lists) {
        binding.viewPager2.setAdapter(new SliderAdapter(new ArrayList<>(lists), binding.viewPager2));
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        binding.viewPager2.setPageTransformer(compositePageTransformer);
        binding.viewPager2.setCurrentItem(1);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }
}
