package com.waftinc.fofoli;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.waftinc.fofoli.adapters.RecyclerViewPostAdapter;
import com.waftinc.fofoli.authentication.LoginActivity;
import com.waftinc.fofoli.posts.NewPostDialogFragment;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.viewholders.AllRecyclerViewHolders;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvUserName, tvUserEmail;
    RecyclerView recyclerView_all_posts;
    RecyclerViewPostAdapter rvPostAdapter;

    Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

        initRecyclerView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirm").setMessage("Are you ready to distribute?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplication(), "Please go to provider and distribute food. Thank you...", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();*/

                     /* Create an instance of the dialog fragment and show it */
                DialogFragment dialog = NewPostDialogFragment.newInstance();
                dialog.show(MainActivity.this.getFragmentManager(), "NewPostDialogFragment");


            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        initWidgets(headerView);
    }

    private void initRecyclerView() {
        Firebase postRef = new Firebase(Constants.FIREBASE_URL_POSTS);
        Query postRefQuery = postRef.orderByKey();

        recyclerView_all_posts = (RecyclerView) findViewById(R.id.recycler_view_all_posts);
        recyclerView_all_posts.setHasFixedSize(true);
        recyclerView_all_posts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        rvPostAdapter = new RecyclerViewPostAdapter(this.getApplicationContext(), Post.class, R.layout.card_view_post, AllRecyclerViewHolders.PostViewHolder.class, postRefQuery);

        recyclerView_all_posts.setAdapter(rvPostAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rvPostAdapter.cleanup();
    }

    private void initWidgets(View headerView) {
        tvUserName = (TextView) headerView.findViewById(R.id.navbar_user_name);
        tvUserEmail = (TextView) headerView.findViewById(R.id.navbar_user_email);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String name = sp.getString(Constants.USER_NAME, "User");
        String email = sp.getString(Constants.USER_EMAIL, "user@example.com");

        tvUserName.setText(String.format("Hello %s", name));
        tvUserEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        mFirebaseRef.unauth();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onDonatePressed(View view) {
        /* Create an instance of the dialog fragment and show it */
        DialogFragment dialog = NewPostDialogFragment.newInstance();
        dialog.show(MainActivity.this.getFragmentManager(), "NewPostDialogFragment");

    }
}
