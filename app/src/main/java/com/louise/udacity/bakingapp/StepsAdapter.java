package com.louise.udacity.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.louise.udacity.bakingapp.data.Step;
import com.louise.udacity.bakingapp.util.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder>{
    List<Step> mStepList;
    Context mContext;
    ItemClickListener mItemClickListener;

    public StepsAdapter(Context mContext, ItemClickListener itemClickListener) {
        this.mContext = mContext;
        mItemClickListener = itemClickListener;
    }

    public void swapData(List<Step> steps) {
        mStepList = steps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.steps_item, parent, false);
        return new StepsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.shortDescription.setText(mStepList.get(position).getShortDescription());
    }

    @Override
    public int getItemCount() {
        if (mStepList == null)
            return 0;
        return mStepList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.textView_step_short_description)
        TextView shortDescription;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null)
            mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
