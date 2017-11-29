package com.waftinc.fofoli;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.waftinc.fofoli.adapters.RecyclerPostAdapter;
import com.waftinc.fofoli.authentication.LoginActivity;
import com.waftinc.fofoli.posts.NewPostDialogFragment;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SHOW_DONATE_DIALOG = "SHOW_DONATE_DIALOG";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.recycler_view_all_posts)
    RecyclerView mRecyclerView_all_posts;

    private RecyclerPostAdapter rvPostAdapter;

    private DatabaseReference mFirebaseRootRef;
    private FirebaseAuth mAuth;

    private boolean mShowDonateDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mFirebaseRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // TODO: find why intent is not getting the boolean
        mShowDonateDialog = this.getIntent().getBooleanExtra(SHOW_DONATE_DIALOG, false);

        initRecyclerView();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        initWidgets(mNavigationView.getHeaderView(0));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            rvPostAdapter.startListening();
        }

        if (mShowDonateDialog)
            ShowDonateDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mShowDonateDialog = false;
        rvPostAdapter.stopListening();
    }

    private void initRecyclerView() {
        Query postRefQuery = mFirebaseRootRef.child(Constants.FIREBASE_LOCATION_POSTS)
                .orderByChild(Constants.FIREBASE_QUERY_TIMESTAMP);
        postRefQuery.keepSynced(true);

        mRecyclerView_all_posts.setHasFixedSize(true);
        mRecyclerView_all_posts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        rvPostAdapter = new RecyclerPostAdapter(this, Utils.getFirebaseRecyclerOptions(postRefQuery));

        mRecyclerView_all_posts.setAdapter(rvPostAdapter);

    }

    private void initWidgets(View headerView) {
        TextView tvUserName = headerView.findViewById(R.id.navbar_user_name);
        TextView tvUserEmail = headerView.findViewById(R.id.navbar_user_email);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String name = sp.getString(Constants.USER_NAME, getString(R.string.default_user_name));
        String email = sp.getString(Constants.USER_EMAIL, getString(R.string.string_default_email));

        tvUserName.setText(name);
        tvUserEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_help:
                intent = new Intent(getApplication(), HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                intent = new Intent(getApplication(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        mAuth.signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onDonatePressed(View view) {
        ShowDonateDialog();
    }

    private void ShowDonateDialog() {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = NewPostDialogFragment.newInstance();
        dialog.show(MainActivity.this.getFragmentManager(), NewPostDialogFragment.TAG);
    }
}
