package com.example.bao48.studyblank;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bao48.studyblank.database.AppDatabase;
import com.example.bao48.studyblank.models.Deck;

import java.util.ArrayList;
import java.util.List;

/**
 * Deck List Activity - Shows all available study decks
 */
public class DeckListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DeckAdapter adapter;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_list);

        recyclerView = findViewById(R.id.recycler_decks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = AppDatabase.getInstance(this);
        adapter = new DeckAdapter();
        recyclerView.setAdapter(adapter);

        // Load decks from database
        new LoadDecksTask().execute();
    }

    private class LoadDecksTask extends AsyncTask<Void, Void, List<Deck>> {
        @Override
        protected List<Deck> doInBackground(Void... voids) {
            return database.deckDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Deck> decks) {
            adapter.setDecks(decks);
        }
    }

    /**
     * RecyclerView Adapter for Decks
     */
    private class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

        private List<Deck> decks = new ArrayList<>();

        void setDecks(List<Deck> decks) {
            this.decks = decks;
            notifyDataSetChanged();
        }

        @Override
        public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_deck, parent, false);
            return new DeckViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DeckViewHolder holder, int position) {
            final Deck deck = decks.get(position);
            holder.tvName.setText(deck.getName());
            holder.tvDescription.setText(deck.getDescription());
            holder.tvCardCount.setText(deck.getCardCount() + " cards");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to study activity for this deck
                    Intent intent = new Intent(DeckListActivity.this, StudyActivity.class);
                    intent.putExtra("DECK_ID", deck.getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return decks.size();
        }

        class DeckViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            TextView tvDescription;
            TextView tvCardCount;

            DeckViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_deck_name);
                tvDescription = itemView.findViewById(R.id.tv_deck_description);
                tvCardCount = itemView.findViewById(R.id.tv_card_count);
            }
        }
    }
}
