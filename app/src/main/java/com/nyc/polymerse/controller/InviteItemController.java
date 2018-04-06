package com.nyc.polymerse.controller;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nyc.polymerse.HomeActivity;
import com.nyc.polymerse.Invites.Invite_Schema;
import com.nyc.polymerse.R;
import com.nyc.polymerse.UserSingleton;
import com.nyc.polymerse.fragments.InviteDialogFragment;
import com.nyc.polymerse.fragments.NotificationFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shant on 3/24/2018.
 */

public class InviteItemController extends RecyclerView.Adapter<InviteItemController.InviteItemViewHolder> {

    List<Invite_Schema> inviteList = new ArrayList<>();
    FragmentManager fragmentManager;
    Context context;

    public InviteItemController(List<Invite_Schema> inviteList, FragmentManager fragmentManager, Context context) {
        this.inviteList = inviteList;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    public void setData(List<Invite_Schema> list) {
        inviteList = list;
        notifyDataSetChanged();
    }

    @Override
    public InviteItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_notification_item, parent, false);
        return new InviteItemViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(InviteItemViewHolder holder, int position) {
        Invite_Schema invite_schema = inviteList.get(position);
        holder.onBind(invite_schema);
    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }

    public class InviteItemViewHolder extends RecyclerView.ViewHolder {

        private TextView otherUserName;
        private TextView acceptStatus;
        private ImageView accepted;
        private TextView date;
        private TextView time;
        private CircleImageView otherUserImg;
        private String status;
        private Invite_Schema inviteSchema;
        private final String ID = UserSingleton.getInstance().getUser().getuID();

        public InviteItemViewHolder(View itemView) {
            super(itemView);
            otherUserName = itemView.findViewById(R.id.user_notification_user_name);
            accepted = itemView.findViewById(R.id.accepted);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            otherUserImg = itemView.findViewById(R.id.user_avatar_user_notification);
        }

        public void onBind(Invite_Schema invite) {
            inviteSchema = invite;



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inviteDialog();
                }
            });
            status = invite.getAcceptStatus();
            date.setText(invite.getDate());
            time.setText(invite.getTime());

            if (ID == invite.getSender_ID()) {
                switch (invite.getAcceptStatus()) {
                    case "accepted":
                        //NEED TO GET OTHER USER'S NAME AND IMAGE
                        otherUserName.setText(userResponse(invite.getReceiverName(), "accepted!"));
                        accepted.setImageResource(R.drawable.ic_check_circle_green_a700_18dp);
                        accepted.setVisibility(View.VISIBLE);
                        //otherUserImg.setImageResource(invite.get);
                        break;
                    case "pending":
                        otherUserName.setText(userResponse(invite.getReceiverName(), "has not responded"));
                        accepted.setImageResource(R.mipmap.hourglass);
                        accepted.setVisibility(View.VISIBLE);
                        otherUserImg.setImageResource(R.mipmap.man);
                        break;
                    case "rejected":
                        otherUserName.setText(userResponse(invite.getReceiverName(), "cannot meet"));
                        accepted.setImageResource(R.drawable.ic_cancel_red_500_18dp);
                        accepted.setVisibility(View.VISIBLE);
                        otherUserImg.setImageResource(R.mipmap.man);
                        break;
                    case "cancelled":

                        cancel();
                        otherUserImg.setImageResource(R.mipmap.man);
                        break;
                }
            } else {
                switch (invite.getAcceptStatus()) {
                    case "accepted":
                        otherUserName.setText(userResponse("You are meeting", invite.getSenderName()));
                        accepted.setImageResource(R.drawable.ic_check_circle_green_a700_18dp);
                        accepted.setVisibility(View.VISIBLE);
                        otherUserImg.setImageResource(R.mipmap.man);
                        break;
                    case "pending":
                        otherUserName.setText(userResponse("Respond to", invite.getSenderName()));
                        accepted.setVisibility(View.GONE);
                        otherUserImg.setImageResource(R.mipmap.man);
                        break;
                    case "rejected":
                        itemView.setVisibility(View.GONE);
                        break;
                    case "cancelled":
                        cancel();
                        otherUserImg.setImageResource(R.mipmap.man);
                        break;
                }
            }

        }

        public String userResponse(String first, String second) {
            StringBuilder userResponse = new StringBuilder();
            userResponse.append(first);
            userResponse.append(" " + second);
            return userResponse.toString();
        }

        public void cancel() {
            otherUserName.setText(userResponse("Invite", "cancelled"));
            otherUserName.setTextColor(Color.rgb(230, 34, 49));
            accepted.setImageResource(R.drawable.ic_cancel_red_500_18dp);
            accepted.setVisibility(View.VISIBLE);

        }

        public void inviteDialog() {
            InviteDialogFragment inviteDialogFragment = new InviteDialogFragment();
            inviteDialogFragment.setInvite(inviteSchema);
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            inviteDialogFragment.show(fragmentTransaction,"Invite Dialog");

        }

        private void fragmentJump(Fragment mFragment) {
            switchContent(R.id.fragment_container, mFragment);
        }

        public void switchContent(int id, Fragment fragment) {
            if (context == null)
                return;
            if (context instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.switchContent(id, fragment);
            }

        }
    }
}
