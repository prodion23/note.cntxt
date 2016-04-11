package co.platto.note.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

/**
 * Created by Donnie Propst on 3/31/2016.
 */
public class NoteTouchHelper extends ItemTouchHelper.SimpleCallback {
    private NoteAdapter noteAdapter;

    public NoteTouchHelper(NoteAdapter noteAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.noteAdapter = noteAdapter;
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        noteAdapter.remove(viewHolder.getAdapterPosition());
        Toast.makeText(viewHolder.itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
}
