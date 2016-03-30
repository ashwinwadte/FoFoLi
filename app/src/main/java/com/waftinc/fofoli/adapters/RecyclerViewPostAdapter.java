package com.waftinc.fofoli.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.util.Log;
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

/**
 * Created by Ashwin on 29-Mar-16.
 */
public class RecyclerViewPostAdapter extends FirebaseRecyclerAdapter<Post, PostViewHolder> {

    Context context;
    String address;

    public RecyclerViewPostAdapter(Context context, Class<Post> modelClass, int modelLayout, Class<PostViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(PostViewHolder postViewHolder, Post post, int position) {

        this.address = post.getProviderAddress();

        postViewHolder.tvProviderName.setText(post.getProviderName());
        postViewHolder.tvProviderContact.setText(post.getProviderContact());
        postViewHolder.tvProviderAddress.setText(post.getProviderAddress());
        postViewHolder.tvCount.setText(String.format(context.getResources().getString(R.string.food_count), post.getPeopleCount()));

        //convert time to relative time
        String relativeTime = String.valueOf(DateUtils.getRelativeTimeSpanString(Long.parseLong(post.getTimestampCreated().toString())));

        postViewHolder.tvTimestamp.setText(relativeTime);

        boolean isDistributed = post.isRequestAccepted();

        if (isDistributed) {
            postViewHolder.tvDistribute.setText("Distributed");
            postViewHolder.tvDistribute.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropping_hand_green, 0);
        }

    }

    @Override
    public void onBindViewHolder(final PostViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);

        viewHolder.bGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mapIntent);
                Log.d("rajuAddress", address);
            }
        });

        viewHolder.tvDistribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDistributedTrue(viewHolder, position);
            }
        });


    }

    private void showDialog(final PostViewHolder viewHolder, final int position) {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm").setMessage("Are you really ready to distribute?\nThis action cannot be reverted.").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setDistributedTrue(viewHolder, position);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();*/
    }

    protected void setDistributedTrue(PostViewHolder viewHolder, int position) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String distributor = sp.getString(Constants.USER_EMAIL, "user@example.com");

        Post post = getItem(position);

        post.setRequestAccepted(true);
        post.setDistributor(distributor);
        post.setTimestampRequestAccepted(ServerValue.TIMESTAMP);

        Firebase ref = getRef(position);
        String key = ref.getKey();
        String encodedEmail = Utils.encodeEmail(post.getProviderEmail());

        Firebase userNewPostRef = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail).child(Constants.FIREBASE_LOCATION_USER_POSTS);

        ref.setValue(post);
        userNewPostRef.child(key).setValue(post);

        viewHolder.tvDistribute.setText("Distributed");
        viewHolder.tvDistribute.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropping_hand_green, 0);

        Snackbar.make(viewHolder.tvDistribute, "Please go to provider, collect and distribute food. Thank you...", Snackbar.LENGTH_LONG).show();
        Toast.makeText(context, "Please go to provider, collect and distribute food. Thank you...", Toast.LENGTH_LONG).show();
    }
}
