package com.waftinc.fofoli.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.waftinc.fofoli.FoFoLiIntentService;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.model.Post;
import com.waftinc.fofoli.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


// This is the same as adapter of listView with few changes
public class WidgetDataProviderFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = WidgetDataProviderFactory.class.getSimpleName();
    private List<Post> postList = new ArrayList<>();
    private Context mContext;
    private FirebaseAuth mAuth;
    private CountDownLatch mCountDownLatch;

    public WidgetDataProviderFactory(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
    }

    private void populatePostList() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Query postRefQuery = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.FIREBASE_LOCATION_POSTS)
                    .orderByChild(Constants.FIREBASE_QUERY_TIMESTAMP);
            postRefQuery.keepSynced(true);

            postRefQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    postList.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);

                        postList.add(post);
                    }
                    mCountDownLatch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.i(TAG, "loadPost:onCancelled", databaseError.toException());
                    mCountDownLatch.countDown();
                }
            });
        } else {
            Log.i(TAG, "user is null");
        }
        mCountDownLatch.countDown();

    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        mCountDownLatch = new CountDownLatch(1);
        populatePostList();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "countDownLatch interrupted" + e);
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (postList.isEmpty()) return 0;
        return postList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.list_row_post);

        // just returning the view, without doing any operations
        if (position == AdapterView.INVALID_POSITION || postList.isEmpty()) return remoteView;

        Post post = null;
        try {
            post = postList.get(position);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "postList invalid position: " + e);
            return remoteView;
        }

        remoteView.setTextViewText(R.id.tvCount, String
                .format(mContext.getString(R.string.food_count), post.getPeopleCount()));

        boolean isDistributed = post.isRequestAccepted();

        if (isDistributed) {
            remoteView.setTextViewText(R.id.tvDistribute, mContext.getString(R.string.distributed));
            remoteView.setTextViewCompoundDrawablesRelative(R.id.tvDistribute, 0, 0, R.drawable.dropping_hand_green, 0);

        } else {
            remoteView.setTextViewText(R.id.tvDistribute, mContext.getString(R.string.distribute_food));
            remoteView.setTextViewCompoundDrawablesRelative(R.id.tvDistribute, 0, 0, R.drawable.dropping_hand_grey, 0);
        }


        //fillInIntent for navigate button
        final String address = post.getProviderAddress();

        Bundle extras = new Bundle();
        extras.putString(FoFoLiIntentService.EXTRA_ADDRESS, address);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        remoteView.setOnClickFillInIntent(R.id.bGetDirections, fillInIntent);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.list_row_post);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
