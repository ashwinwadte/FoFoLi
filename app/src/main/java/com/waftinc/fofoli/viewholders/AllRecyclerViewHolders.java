package com.waftinc.fofoli.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.waftinc.fofoli.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllRecyclerViewHolders {

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvProviderName)
        public TextView tvProviderName;
        @BindView(R.id.tvContact)
        public TextView tvProviderContact;
        @BindView(R.id.tvAddress)
        public TextView tvProviderAddress;
        @BindView(R.id.tvTimestamp)
        public TextView tvTimestamp;
        @BindView(R.id.tvCount)
        public TextView tvCount;
        @BindView(R.id.tvDistribute)
        public TextView tvDistribute;
        @BindView(R.id.bGetDirections)
        public Button bGetDirections;


        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
