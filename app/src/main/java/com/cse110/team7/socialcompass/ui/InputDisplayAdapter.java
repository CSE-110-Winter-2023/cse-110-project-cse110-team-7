package com.cse110.team7.socialcompass.ui;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cse110.team7.socialcompass.R;
import com.cse110.team7.socialcompass.models.FriendAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * This class implements the RecyclerView used in activity_main.xml; its ViewHolder is used
 * to represent the single element of the RecyclerView, which can be found in label_input_format.xml
 */
public class InputDisplayAdapter extends RecyclerView.Adapter<InputDisplayAdapter.ViewHolder> {
    public List<FriendAccount> friendAccountList = Collections.emptyList();
    private BiConsumer<FriendAccount, String> onParentLabelChanged; //Used for updating the parentLabel
    private BiConsumer<FriendAccount, String> onCoordinatesChanged; //Used for updating the coordinateLabel

    public void setParentLabelChanged(BiConsumer<FriendAccount, String> onTextEdited) {
        this.onParentLabelChanged = onTextEdited;
    }

    public void setCoordinatesChanged(BiConsumer<FriendAccount, String> onTextEdited){
        this.onCoordinatesChanged = onTextEdited;
    }

    //May have issues later with clear; make sure to check.
    //This method simply sets up the friend  list with whatever values need to be inputted.
    public void setFriendList(List<FriendAccount> newFriendAccounts) {
        this.friendAccountList.clear();
        this.friendAccountList = newFriendAccounts;
        notifyDataSetChanged();
    }

    //Not really used, but may be useful for testing.
    public ArrayList<FriendAccount> getFriendList() {
        return (ArrayList<FriendAccount>)(friendAccountList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_input_format, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputDisplayAdapter.ViewHolder holder, int position) {
        holder.setFriend(friendAccountList.get(position));
    }

    @Override
    public int getItemCount() {
        return friendAccountList.size();
    }

    @Override
    public long getItemId(int position) {
        return friendAccountList.get(position).id;
    }

    /**
     * Creates each Recyclerview, which will appear in activity_main.xml
     *
     * The details for what constitutes each element of a Recyclerview is on the
     * label_input_format.xml file.
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView coordinates;
        private final TextView labelName;

        private FriendAccount currFriendAccount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.coordinates = itemView.findViewById(R.id.latLongTextView);
            this.labelName = itemView.findViewById(R.id.parentLabelName);

            this.coordinates.setOnFocusChangeListener((view, hasFocus) -> {
                if(!hasFocus) {
                    onCoordinatesChanged.accept(currFriendAccount, coordinates.getText().toString());
                }
            });

            this.labelName.setOnFocusChangeListener((view, hasFocus) -> {
                if(!hasFocus) {
                    onParentLabelChanged.accept(currFriendAccount, labelName.getText().toString());
                }
            });
        }

        public FriendAccount getFriend() {
            return currFriendAccount;
        }

        //May not be necessary, but it currently sets a friend account
        //(and its corresponding textViews to certain values):
        public void setFriend(FriendAccount friendAccountToSet) {
            currFriendAccount = friendAccountToSet;
            if(friendAccountToSet.getLocation() != null){
                String latLongValue = friendAccountToSet.getLocation().toString();
                this.coordinates.setText(latLongValue);
            }
            this.labelName.setText(friendAccountToSet.getName());
        }
    }
}
