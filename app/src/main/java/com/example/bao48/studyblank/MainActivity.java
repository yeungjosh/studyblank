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

        // Initialize sample data if needed
        new InitializeDatabaseTask().execute();

        // Initialize dashboard views
        tvDueCards = findViewById(R.id.tv_due_cards);
        tvTotalCards = findViewById(R.id.tv_total_cards);

        // Load dashboard data
        new LoadDashboardTask().execute();
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
     */
    private class InitializeDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseInitializer.initializeIfEmpty(database,
                FirebaseAuth.getInstance().getCurrentUser().getUid());
            return null;
        }
    }

    /**
     * Load dashboard statistics
     */
    private class LoadDashboardTask extends AsyncTask<Void, Void, int[]> {
        @Override
        protected int[] doInBackground(Void... voids) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            int dueCards = database.studyProgressDao().getDueCardsCount(userId, System.currentTimeMillis());
            int totalCards = database.flashcardDao().getAll().size();
            return new int[]{dueCards, totalCards};
        }

        @Override
        protected void onPostExecute(int[] results) {
            if (tvDueCards != null && tvTotalCards != null) {
                tvDueCards.setText(String.valueOf(results[0]));
                tvTotalCards.setText(String.valueOf(results[1]));
            }
        }
    }
}
