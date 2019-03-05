package com.sph.healthtrac.more.inbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sph.healthtrac.R;
import com.sph.healthtrac.planner.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.List;

public class HTInboxListView extends ArrayAdapter<String> {

    private final Activity context;

    private final List<String> messageIDs;
    private final List<String> messageAckeds;
    private final List<String> messageSubjects;
    private final List<String> messageNotes;
    private final List<String> messageDates;

    Typeface subjectFont;
    Typeface noteFont;
    Typeface dateFont;

    private boolean isShowingDeleteConfirmDlg;

    public HTInboxListView(Activity context, List<String> messageIDs, List<String> messageAckeds,List<String> messageSubjects,List<String> messageNotes,List<String> messageDates) {

        super(context, R.layout.inbox_list_view_cell, messageIDs);
        this.context = context;
        this.messageIDs = messageIDs;
        this.messageAckeds = messageAckeds;
        this.messageSubjects = messageSubjects;
        this.messageNotes = messageNotes;
        this.messageDates = messageDates;

        subjectFont = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-DemiBold.ttf");
        noteFont = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Medium.ttf");
        dateFont = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater inflater = context.getLayoutInflater();

        if(view == null) {
            view = inflater.inflate(R.layout.inbox_list_view_cell, null, true);
            holder = new ViewHolder();
            holder.subjectView = (TextView) view.findViewById(R.id.msgSubjectLabel);
            holder.noteView = (TextView) view.findViewById(R.id.msgNoteLabel);
            holder.dateView = (TextView) view.findViewById(R.id.createDateLabel);

            holder.subjectView.setTypeface(subjectFont);
            holder.noteView.setTypeface(noteFont);
            holder.dateView.setTypeface(dateFont);

            holder.noteView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
            holder.dateView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));

            holder.subjectView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.noteView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        if("Y".equals(messageAckeds.get(position))){
            holder.subjectView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
            holder.dateView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
        }else{
            holder.subjectView.setTextColor(context.getResources().getColor(R.color.ht_blue));
            holder.dateView.setTextColor(context.getResources().getColor(R.color.ht_blue));
        }

        final String messageId = messageIDs.get(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageDetailActivity.class);
                intent.putExtra("message_id", messageId);
                context.startActivity(intent);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(messageId);
                return true;
            }
        });

        view.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                showDeleteDialog(messageId);
            }

            @Override
            public void onSwipeRight() {
                showDeleteDialog(messageId);
            }
        });

        holder.subjectView.setText(messageSubjects.get(position));
        holder.noteView.setText(messageNotes.get(position));
        holder.dateView.setText(messageDates.get(position));
        return view;
    }

    public static class ViewHolder{
        public TextView subjectView;
        public TextView noteView;
        public TextView dateView;
    }

    private void showDeleteDialog(final String messageId) {
        if(isShowingDeleteConfirmDlg)
            return;

        isShowingDeleteConfirmDlg = true;

        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(context);
        AlertDialog alert;
        builder.setTitle(R.string.delete_message_title)
                .setMessage(context.getResources()
                        .getString(R.string.delete_message_content))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((InboxActivity) context).deleteMessages(messageId);
                        isShowingDeleteConfirmDlg = false;
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isShowingDeleteConfirmDlg = false;
                    }
                });
        alert = builder.create();
        alert.show();
    }
}
