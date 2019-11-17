package com.example.meet_practical.Utility;

import androidx.recyclerview.widget.DiffUtil;

import com.example.meet_practical.UserList_Bean.ResultsItem;

import java.util.List;

public class DiffUtil_Callback extends DiffUtil.Callback {

    private List<ResultsItem> RecordsOld;
    private List<ResultsItem> RecordsNew;

    public  DiffUtil_Callback (List<ResultsItem> RecordsOld , List<ResultsItem> RecordsNew)
    {
        this.RecordsOld = RecordsOld;
        this.RecordsNew = RecordsNew;
    }

    @Override
    public int getOldListSize() {
        return RecordsOld.size();
    }

    @Override
    public int getNewListSize() {
        return RecordsNew.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }
}
