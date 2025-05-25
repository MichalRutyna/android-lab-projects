package com.example.rm4rysowanie;


import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rm4rysowanie.databinding.FragmentItemBinding;

import java.util.ArrayList;
import java.util.List;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<Image> mValues;

    private OnItemClickListener mOnItemClickListener;

    public void setImageList(ArrayList<Image> list) {
        mValues = list;
        notifyDataSetChanged();
    }

    public MyItemRecyclerViewAdapter(List<Image> items, OnItemClickListener listener) {
        mValues = items;
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Image item = mValues.get(position);
        holder.mIdView.setText("" + item.id);
        holder.mContentView.setText(item.name);
        holder.mImage = item;

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    interface OnItemClickListener {
        void onItemClickListener(Image element);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mIdView;
        public final TextView mContentView;

        public Image mImage;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            itemView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() ;
        }

        @Override
        public void onClick(View v) {
            System.out.println("Clicked");
            mOnItemClickListener.onItemClickListener(mImage);
        }
    }
}