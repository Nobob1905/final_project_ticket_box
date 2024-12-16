package com.final_project_ticket_box.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.final_project_ticket_box.databinding.ViewholderCategoryBinding;

import java.util.List;
public class CategoryEachEventAdapter extends RecyclerView.Adapter<CategoryEachEventAdapter.ViewHolder>{

    private final List<String> items;

    public CategoryEachEventAdapter(List<String> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderCategoryBinding binding;

        public ViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.titleTxt.setText(items.get(position));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
