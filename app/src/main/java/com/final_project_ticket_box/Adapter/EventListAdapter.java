package com.final_project_ticket_box.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.final_project_ticket_box.EventDetailActivity;
import com.final_project_ticket_box.Models.Event;
import com.final_project_ticket_box.databinding.ViewholderEventBinding;

import java.util.ArrayList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>{
    private ArrayList<Event> items;
    private Context context;

    // Constructor
    public EventListAdapter(ArrayList<Event> items) {
        this.items = items;
    }

    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewholderEventBinding binding;

        public ViewHolder(ViewholderEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Event event, Context context) {
            binding.nameTxt.setText(event.getTitle());

            RequestOptions requestOptions = new RequestOptions()
                    .transform(new CenterCrop(), new RoundedCorners(30));

            Glide.with(context)
                    .load(event.getPoster())
                    .apply(requestOptions)
                    .into(binding.pic);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra("object", event);
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderEventBinding binding = ViewholderEventBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), context);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
