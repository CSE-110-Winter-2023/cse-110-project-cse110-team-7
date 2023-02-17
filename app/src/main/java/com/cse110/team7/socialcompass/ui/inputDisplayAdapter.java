package com.cse110.team7.socialcompass.ui;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cse110.team7.socialcompass.R;
import com.cse110.team7.socialcompass.models.House;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class inputDisplayAdapter extends RecyclerView.Adapter<inputDisplayAdapter.ViewHolder> {
    public List<House> houseList = Collections.emptyList();
    private BiConsumer<House, String> onParentLabelChanged;
    private BiConsumer<House, String> onCoordinatesChanged;

    public void setParentLabelChanged(BiConsumer<House, String> onTextEdited) {
        this.onParentLabelChanged = onTextEdited;
    }

    public void setCoordinatesChanged(BiConsumer<House, String> onTextEdited){
        this.onCoordinatesChanged = onTextEdited;
    }

    //May have issues later with clear; make sure to check.
    //This method simply sets up the house list with whatever values need to be inputted.
    public void setHouseList(List<House> newHouses) {
        this.houseList.clear();
        this.houseList = newHouses;
        notifyDataSetChanged();
    }

    public ArrayList<House> getHouseList() {
        return (ArrayList<House>)(houseList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_input_format, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull inputDisplayAdapter.ViewHolder holder, int position) {
        holder.setHouse(houseList.get(position));
    }

    @Override
    public int getItemCount() {
        return houseList.size();
    }

    @Override
    public long getItemId(int position) {
        return houseList.get(position).id;
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

        private House currHouse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.coordinates = itemView.findViewById(R.id.latLongTextView);
            this.labelName = itemView.findViewById(R.id.parentLabelName);

            this.coordinates.setOnFocusChangeListener((view, hasFocus) -> {
                if(!hasFocus) {
                    onCoordinatesChanged.accept(currHouse, coordinates.getText().toString());
                }
            });

            this.labelName.setOnFocusChangeListener((view, hasFocus) -> {
                if(!hasFocus) {
                    onParentLabelChanged.accept(currHouse, labelName.getText().toString());
                }
            });
        }

        public House getHouse() {
            return currHouse;
        }

        //May not be necessary, but it currently sets a house
        //(and its corresponding textViews to certain values):
        public void setHouse(House houseToSet) {
            currHouse = houseToSet;
            if(houseToSet.getLocation() != null){
                String latLongValue = houseToSet.getLocation().toString();
                this.coordinates.setText(latLongValue);
            }
            this.labelName.setText(houseToSet.getName());
        }
    }
}
