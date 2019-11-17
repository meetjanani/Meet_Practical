package com.example.meet_practical.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meet_practical.Activity.UserProfileActivity;
import com.example.meet_practical.UserList_Bean.ResultsItem;
import com.example.meet_practical.DBHelper.Users_Db_Helper;
import com.example.meet_practical.R;
import com.example.meet_practical.Utility.DiffUtil_Callback;
import com.google.gson.Gson;

import java.util.List;

public class Display_Adapter extends RecyclerView.Adapter<Display_Adapter.MyViewHolder>   {

    private List<ResultsItem> Records;
    private Context context;
    private Users_Db_Helper users_db_helper;
    private String Json_str = null;
    Gson gson = new Gson();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Tv_User_Name , Tv_Email , Tv_Phone , Tv_Cell;
        ImageView Img_User;
        LinearLayout LL_Outer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Tv_User_Name = (TextView)itemView.findViewById(R.id.Tv_User_Name);
            Tv_Email = (TextView)itemView.findViewById(R.id.Tv_Email);
            Tv_Phone = (TextView)itemView.findViewById(R.id.Tv_Phone);
            Tv_Cell = (TextView)itemView.findViewById(R.id.Tv_Cell);
            Img_User = (ImageView) itemView.findViewById(R.id.Img_User);
            LL_Outer = (LinearLayout)itemView.findViewById(R.id.LL_Outer);
        }
    }

    public Display_Adapter(List<ResultsItem> Records, Context context  ) {
        this.Records = Records;
        this.context = context;
    }

    @NonNull
    @Override
    public Display_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pattern_userlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Display_Adapter.MyViewHolder holder, final int position) {

        holder.Tv_User_Name.setText(Records.get(position).getName().getFirst() + " " + Records.get(position).getName().getLast() + " " + Records.get(position).getName().getLast());
        holder.Tv_Email.setText("Email: " + Records.get(position).getEmail() + "");
        holder.Tv_Cell.setText("Cell: " + Records.get(position).getCell() + "");
        holder.Tv_Phone.setText("Phone: " + Records.get(position).getPhone() + "");

        Glide.with(context)
                .load(Records.get(position).getPicture().getLarge())
                .into(holder.Img_User);

        // Open New Activity to Show More Details of User
        // Simple Click Event
        holder.LL_Outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context , UserProfileActivity.class);
                Json_str = gson.toJson(Records.get(position));
                in.putExtra("UserProfile" ,Json_str);
                context.startActivity(in);
            }
        });

        // Delete User
        // Long Press Click Event
        holder.LL_Outer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.dialog_delete_title);
                builder.setMessage(R.string.dialog_alert);
                builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        Json_str = gson.toJson(Records.get(position));
                        notifyItemRemoved(position);
                        Records.remove(Records.get(position));
                        users_db_helper = new Users_Db_Helper(context);
                        users_db_helper.Delete(Json_str);
                    }
                });
                builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Records.size();
    }


    public void InsertNewUsers(List<ResultsItem> RecordsNew)
    {
        DiffUtil_Callback diffUtil_callback =  new DiffUtil_Callback(Records , RecordsNew);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil_callback);

        diffResult.dispatchUpdatesTo(this);

    }

}