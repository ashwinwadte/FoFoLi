package com.waftinc.fofoli.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.model.Post;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.utils.Utils;
import com.waftinc.fofoli.viewholders.AllRecyclerViewHolders.PostViewHolder;

public class RecyclerViewPostAdapter extends FirebaseRecyclerAdapter<Post, PostViewHolder> {

    private Context mContext;

    public RecyclerViewPostAdapter(Context context, Class<Post> modelClass, int modelLayout, Class<PostViewHolder>
            viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(PostViewHolder postViewHolder, Post post, int position) {

        postViewHolder.tvProviderName.setText(post.getProviderName());
        postViewHolder.tvProviderContact.setText(post.getProviderContact());
        postViewHolder.tvProviderAddress.setText(post.getProviderAddress());
        postViewHolder.tvCount
                .setText(String.format(mContext.getString(R.string.food_count), post.getPeopleCount()));

        //convert time to relative time
        String relativeTime = String
                .valueOf(DateUtils.getRelativeTimeSpanString(Long.parseLong(post.getTimestampCreated().toString())));

        postViewHolder.tvTimestamp.setText(relativeTime);

        boolean isDistributed = post.isRequestAccepted();

        if (isDistributed) {
            postViewHolder.tvDistribute.setText(R.string.distributed);
            postViewHolder.tvDistribute
                    .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropping_hand_green, 0);
        } else {
            postViewHolder.tvDistribute.setText(R.string.distribute_food);
            postViewHolder.tvDistribute
                    .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropping_hand_grey, 0);
        }

    }

    @Override
    public void onBindViewHolder(final PostViewHolder postViewHolder, int position) {
        super.onBindViewHolder(postViewHolder, position);

        postViewHolder.bGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = getItem(postViewHolder.getAdapterPosition()).getProviderAddress();

                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(mapIntent);
            }
        });

        boolean isDistributed = getItem(postViewHolder.getAdapterPosition()).isRequestAccepted();

        if (isDistributed) {
            postViewHolder.tvDistribute.setText(R.string.distributed);
            postViewHolder.tvDistribute
                    .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropping_hand_green, 0);
        } else {
            postViewHolder.tvDistribute.setText(R.string.distribute_food);
            postViewHolder.tvDistribute
                    .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropping_hand_grey, 0);
        }

        postViewHolder.tvDistribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDistributedTrue(postViewHolder, postViewHolder.getAdapterPosition());
            }
        });


    }

    private void setDistributedTrue(PostViewHolder viewHolder, int position) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String distributor = sp.getString(Constants.USER_EMAIL, mContext.getString(R.string.string_default_email));

        Post post = getItem(position);
        boolean isDistributed = getItem(position).isRequestAccepted();

        if (!isDistributed) {
            post.setRequestAccepted(true);
            post.setDistributor(distributor);
            post.setTimestampRequestAccepted(ServerValue.TIMESTAMP);

            Firebase ref = getRef(position);
            String key = ref.getKey();
            String encodedEmail = Utils.encodeEmail(post.getProviderEmail());

            Firebase userNewPostRef = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail)
                    .child(Constants.FIREBASE_LOCATION_USER_POSTS);

            ref.setValue(post);
            userNewPostRef.child(key).setValue(post);

            viewHolder.tvDistribute.setText(R.string.distributed);
            viewHolder.tvDistribute.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropping_hand_green, 0);

            Snackbar.make(viewHolder.tvDistribute, R.string.string_go_to_provider, Snackbar.LENGTH_LONG)
                    .setAction(R.string.string_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, R.string.string_thank_you, Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
        else {
            Snackbar.make(viewHolder.tvDistribute, R.string.string_already_distributed, Snackbar.LENGTH_SHORT).show();
        }
    }
}
