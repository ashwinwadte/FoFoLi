package com.waftinc.fofoli.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.model.Post;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.utils.Utils;
import com.waftinc.fofoli.viewholders.AllRecyclerViewHolders.PostViewHolder;

public class RecyclerPostAdapter extends FirebaseRecyclerAdapter<Post, PostViewHolder> {
    Context mContext;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RecyclerPostAdapter(Context context, FirebaseRecyclerOptions<Post> options) {
        super(options);
        mContext = context;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_post, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(final PostViewHolder postViewHolder, int position, Post post) {

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


        // click listener for navigate button
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

            DatabaseReference ref = getRef(position);

            String key = ref.getKey();
            String encodedEmail = Utils.encodeEmail(post.getProviderEmail());

            DatabaseReference userNewPostRef = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.FIREBASE_LOCATION_USERS).child(encodedEmail)
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
        } else {
            Snackbar.make(viewHolder.tvDistribute, R.string.string_already_distributed, Snackbar.LENGTH_SHORT).show();
        }
    }
}
