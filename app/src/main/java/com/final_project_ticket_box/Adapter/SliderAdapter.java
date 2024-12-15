package com.final_project_ticket_box.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.final_project_ticket_box.Models.SliderItems;
import com.final_project_ticket_box.databinding.ViewholderSliderBinding;


import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderItems> sliderItems;
    private ViewPager2 viewPager2;
    private Context context;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

    public SliderAdapter(List<SliderItems> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderSliderBinding binding;

        public SliderViewHolder(ViewholderSliderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SliderItems sliderItem) {
            RequestOptions requestOptions = new RequestOptions()
                    .transform(new CenterCrop(), new RoundedCorners(60));
            if (context != null) {
                Glide.with(context)
                        .load(sliderItem.getVideo())
                        .apply(requestOptions)
                        .into(binding.imageSlide);
            }
        }
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderSliderBinding binding = ViewholderSliderBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new SliderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.bind(sliderItems.get(position));
        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }


    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

}
