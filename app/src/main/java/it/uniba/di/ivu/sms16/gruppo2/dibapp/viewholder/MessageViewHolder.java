package it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Message;


public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView senderTextView;
    public TextView messageTextView;
    public TextView hourTextView;
    public CircleImageView senderImageView;
    public LinearLayout messageLayout;
    public LinearLayout messageContentLayout;
    public ImageView messageImageView;


    public MessageViewHolder(View itemView) {
        super(itemView);

        senderTextView = (TextView) itemView.findViewById(R.id.senderTextView);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        hourTextView = (TextView) itemView.findViewById(R.id.hourTextView);
        senderImageView = (CircleImageView) itemView.findViewById(R.id.senderImageView);
        messageLayout = (LinearLayout) itemView.findViewById(R.id.messageLayout);
        messageContentLayout = (LinearLayout) itemView.findViewById(R.id.messageContentLayout);
        messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);

    }

    public void bindToOtherMessage(Message message, View.OnClickListener listener) {

        messageLayout.setGravity(Gravity.START);
        messageContentLayout.setBackground(itemView.getResources().getDrawable(R.drawable.layer_other_message));
        ((LinearLayout.LayoutParams) hourTextView.getLayoutParams()).gravity = Gravity.END;

        senderImageView.setVisibility(View.VISIBLE);
        senderTextView.setVisibility(View.VISIBLE);

        senderTextView.setText(message.senderName);
        senderImageView.setOnClickListener(listener);

        hourTextView.setText(getHour(message.date, message.hour));

        if (message.senderPhoto == null) {
            senderImageView
                    .setImageDrawable(ContextCompat
                            .getDrawable(itemView.getContext(),
                                    R.drawable.ic_account_circle_dark));
        } else {
            Glide.with(itemView.getContext())
                    .load(message.senderPhoto)
                    .into(senderImageView);
        }


        if (message.message.startsWith("https://firebasestorage.googleapis.com/")) {

            messageTextView.setVisibility(View.GONE);
            messageImageView.setVisibility(View.VISIBLE);

            Glide.with(itemView.getContext())
                    .load(message.message)
                    .into(messageImageView);

        } else {

            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message.message);
            messageImageView.setVisibility(View.GONE);
        }

    }

    public void bindToUserMessage(Message message) {

        messageLayout.setGravity(Gravity.END);
        messageContentLayout.setBackground(itemView.getResources().getDrawable(R.drawable.layer_user_message));
        ((LinearLayout.LayoutParams) hourTextView.getLayoutParams()).gravity = Gravity.START;

        senderImageView.setVisibility(View.GONE);
        senderTextView.setVisibility(View.GONE);

        hourTextView.setText(getHour(message.date, message.hour));

        if (message.message.startsWith("https://firebasestorage.googleapis.com/")) {

            messageTextView.setVisibility(View.GONE);
            messageImageView.setVisibility(View.VISIBLE);

            Glide.with(itemView.getContext())
                    .load(message.message)
                    .into(messageImageView);

        } else {

            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message.message);
            messageImageView.setVisibility(View.GONE);
        }
    }

    private String getHour(String date, String hour) {

        String result;
        SimpleDateFormat dayFormatter = new SimpleDateFormat("d MMM");
        SimpleDateFormat hourFormatter = new SimpleDateFormat("HH:mm");
        Date currentDate = new Date();

        if (date.equals(dayFormatter.format(currentDate))) {
            if (hour.equals(hourFormatter.format(currentDate))) {
                return "Now";
            }
            result = "Today, " + hour;
        } else {
            result = date + ", " + hour;
        }
        return result;
    }
}
