package com.example.memorybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorybook.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.adapterHolder> {
    public Adapter(ArrayList<Model> arrayList) {
        this.arrayList = arrayList;
    }

    ArrayList<Model> arrayList;

    @NonNull
    @Override
    public adapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new adapterHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterHolder holder, int position) {
        holder.recyclerRowBinding.textView.setText(arrayList.get(position).title);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class adapterHolder extends RecyclerView.ViewHolder{
        private RecyclerRowBinding recyclerRowBinding;
        public adapterHolder(RecyclerRowBinding recyclerRowView) {
            super(recyclerRowView.getRoot());
            this.recyclerRowBinding = recyclerRowView;
        }
    }
}
