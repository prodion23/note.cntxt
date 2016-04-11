package co.platto.note.adapter;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;

import co.platto.note.R;
import co.platto.note.data.UserAccount;
import co.platto.note.domain.CustomUser;
import co.platto.note.domain.Note;

/**
 * Created by Donnie Propst on 3/17/2016.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private RecyclerView noteList;
    private ImageView emptyImage;
    private TextView emptyText;


    private List<Note> notes;
    private CustomUser self;
    private String name;

    public NoteAdapter(List<Note> notes){
        this.notes = notes;
        if(this.notes == null){
            this.notes = new ArrayList<Note>();
        }
        self = UserAccount.getInstance().getUser();
        name = self.getName();
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticky_note, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        if(!name.equals(notes.get(position).getFromUserName())) {
            holder.fromName.setText("From: " + notes.get(position).getFromUserName());
        }else{
            SpannableString spanString = new SpannableString("Note to self ");
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            holder.fromName.setText(spanString);
        }
        holder.content.setText(notes.get(position).getContent());
        if(!notes.get(position).isRead()){
            holder.itemView.setBackgroundColor(Color.parseColor("#ffff88"));
        }

        if(!notes.get(position).isViaSlack()){
            holder.viaSlack.setVisibility(View.INVISIBLE);
        }
        PrettyTime pt = new PrettyTime();
        holder.timeStamp.setText(pt.format(notes.get(position).getCreatedAt()));
        notes.get(position).setRead(true);
        notes.get(position).pinInBackground("NOTE");
        notes.get(position).saveInBackground();


    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView fromName;
        public TextView content;
        public TextView viaSlack;
        public TextView timeStamp;
        public ViewHolder(View v) {
            super(v);
            fromName = (TextView)v.findViewById(R.id.item_card_from_name);
            content = (TextView)v.findViewById(R.id.item_card_content);
            viaSlack = (TextView)v.findViewById(R.id.item_card_via_slack);
            timeStamp = (TextView)v.findViewById(R.id.item_card_timestamp);
        }
    }

    public void addItems(List<Note> newNotes){

        notes.addAll(0, newNotes);
        notifyDataSetChanged();
        hideEmptyPlaceHolders();
    }

    public void updateItems(List<Note> fetchedNotes){
        if(fetchedNotes != null && !fetchedNotes.isEmpty()) {
            notes.clear();
            notes.addAll(fetchedNotes);
            notifyDataSetChanged();
            hideEmptyPlaceHolders();
        }else{
            showEmptyPlaceHolders();
        }
    }

    public void remove(int position) {

        notes.get(position).unpinInBackground();
        notes.get(position).deleteEventually();
        notes.remove(position);
        notifyItemRemoved(position);
        if(getItemCount() == 0){
            showEmptyPlaceHolders();
        }
    }

    public void setupEmptyPlaceHolder(RecyclerView recyclerView, ImageView imagePlaceHolder, TextView textPlaceHolder){
        noteList = recyclerView;
        emptyImage = imagePlaceHolder;
        emptyText = textPlaceHolder;
    }

    @UiThread
    private void showEmptyPlaceHolders(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && emptyImage.isAttachedToWindow()) {
            int cx = emptyImage.getMeasuredWidth() / 2;
            int cy = emptyImage.getMeasuredHeight() / 2;
            int finalRadius = Math.max(emptyImage.getWidth(), emptyImage.getHeight()) / 2;
            Animator anim = ViewAnimationUtils.createCircularReveal(emptyImage, cx, cy, 0, finalRadius);

            // make the view visible and start the animation
            emptyImage.setVisibility(View.VISIBLE);
            anim.start();
        }else{
            emptyImage.setVisibility(View.VISIBLE);
        }

        //emptyImage.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        noteList.setVisibility(View.INVISIBLE);
    }
    private void hideEmptyPlaceHolders(){
        emptyImage.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
        noteList.setVisibility(View.VISIBLE);
    }

}
