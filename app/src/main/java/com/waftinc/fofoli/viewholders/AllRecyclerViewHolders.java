package com.waftinc.fofoli.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.waftinc.fofoli.R;

/**
 * Created by Ashwin on 29-Mar-16.
 */
public class AllRecyclerViewHolders {

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProviderName, tvProviderContact, tvProviderAddress, tvTimestamp, tvCount, tvDistribute;
        public Button bGetDirections;


        public PostViewHolder(View itemView) {
            super(itemView);
            tvProviderName = (TextView) itemView.findViewById(R.id.tvProviderName);
            tvProviderContact = (TextView) itemView.findViewById(R.id.tvContact);
            tvProviderAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
            tvCount = (TextView) itemView.findViewById(R.id.tvCount);
            tvDistribute = (TextView) itemView.findViewById(R.id.tvDistribute);
            bGetDirections = (Button) itemView.findViewById(R.id.bGetDirections);
        }
    }

}
