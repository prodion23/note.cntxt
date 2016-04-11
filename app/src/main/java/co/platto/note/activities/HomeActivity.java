package co.platto.note.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.platto.note.R;
import co.platto.note.adapter.NoteAdapter;
import co.platto.note.adapter.NoteTouchHelper;
import co.platto.note.beaconscanning.ScanningService;
import co.platto.note.data.DataSync;
import co.platto.note.data.UserAccount;
import co.platto.note.domain.Note;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.home_note_list)RecyclerView noteList;
    @Bind(R.id.home_empty_image)ImageView emptyImage;
    @Bind(R.id.home_empty_text)TextView emptyText;

    private NoteAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserAccount.getInstance().load();
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DataSync.getInstance().getAllGroupUsers();
        setupRecyclerView();
        setupRefreshLayout();
        startService();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNote();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startService(){
        Intent i = new Intent(this, ScanningService.class);
        if(startService(i) != null){
            //Toast.makeText(this, "Service Already Running", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(this, "Service Not Running", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupRecyclerView(){
        mLayoutManager = new LinearLayoutManager(this);
        noteList.setLayoutManager(mLayoutManager);
        mAdapter = new NoteAdapter(DataSync.getInstance().newNotes);
        DataSync.getInstance().getCachedNotes(mAdapter);
        noteList.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new NoteTouchHelper(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(noteList);


        mAdapter.setupEmptyPlaceHolder(noteList, emptyImage, emptyText);
    }

    private void setupRefreshLayout(){
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.home_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(ScanningService.inRange) {
                    RefreshAdapter refresh = new RefreshAdapter();
                    refresh.execute();
                }else{
                    swipeLayout.setRefreshing(false);
                    Toast.makeText(HomeActivity.this, "Not near beacon", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void createNote(){
        startActivity(new Intent(this, CreateNote.class));
    }

    private class RefreshAdapter extends AsyncTask<Void, Void, List<Note>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected List<Note> doInBackground(Void... voids) {
            return DataSync.getInstance().refreshAllNotes();
        }

        @Override
        @UiThread
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);
            swipeLayout.setRefreshing(false);
            if(notes != null && !notes.isEmpty()) {
                mAdapter.addItems(notes);
            }
            Toast.makeText(HomeActivity.this, "Updated", Toast.LENGTH_SHORT).show();
        }
    }
}
