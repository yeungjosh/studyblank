package com.example.bao48.studyblank;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bao48.studyblank.database.AppDatabase;
import com.example.bao48.studyblank.database.DatabaseInitializer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvDueCards;
    private TextView tvTotalCards;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize database
        database = AppDatabase.getInstance(this);

        // Initialize dashboard views
        tvDueCards = findViewById(R.id.tv_due_cards);
        tvTotalCards = findViewById(R.id.tv_total_cards);

        // Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to login
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, home.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize sample data if needed
        new InitializeDatabaseTask(this, database, currentUser.getUid()).execute();

        // Load dashboard data
        new LoadDashboardTask(this, database, currentUser.getUid()).execute();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Start study session
            Intent intent = new Intent(this, StudyActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            // View all decks
            Intent intent = new Intent(this, DeckListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            // Statistics (placeholder)
            Toast.makeText(this, "Statistics coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            // Settings (placeholder)
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            // Share app (placeholder)
            Toast.makeText(this, "Share coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            // Logout
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Initialize database with sample data if empty
     * Static inner class to prevent memory leaks
     */
    private static class InitializeDatabaseTask extends AsyncTask<Void, Void, Void> {
        private final AppDatabase database;
        private final String userId;

        InitializeDatabaseTask(MainActivity activity, AppDatabase database, String userId) {
            this.database = database;
            this.userId = userId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseInitializer.initializeIfEmpty(database, userId);
            return null;
        }
    }

    /**
     * Load dashboard statistics
     * Static inner class with WeakReference to prevent memory leaks
     */
    private static class LoadDashboardTask extends AsyncTask<Void, Void, int[]> {
        private final WeakReference<MainActivity> activityRef;
        private final AppDatabase database;
        private final String userId;

        LoadDashboardTask(MainActivity activity, AppDatabase database, String userId) {
            this.activityRef = new WeakReference<>(activity);
            this.database = database;
            this.userId = userId;
        }

        @Override
        protected int[] doInBackground(Void... voids) {
            int dueCards = database.studyProgressDao().getDueCardsCount(userId, System.currentTimeMillis());
            int totalCards = database.flashcardDao().getAll().size();
            return new int[]{dueCards, totalCards};
        }

        @Override
        protected void onPostExecute(int[] results) {
            MainActivity activity = activityRef.get();
            if (activity != null && !activity.isFinishing()) {
                if (activity.tvDueCards != null && activity.tvTotalCards != null) {
                    activity.tvDueCards.setText(String.valueOf(results[0]));
                    activity.tvTotalCards.setText(String.valueOf(results[1]));
                }
            }
        }
    }
}
