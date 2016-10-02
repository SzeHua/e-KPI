package com.example.user.e_kpiv3;

/**
 * Created by USER on 5/29/2016.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class EvidenceAdapter extends RecyclerView.Adapter<EvidenceAdapter.MyViewHolder> {

    private List<Evidence> evidenceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, date;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            date = (TextView) view.findViewById(R.id.date);
        }
    }


    public EvidenceAdapter(List<Evidence> moviesList) {
        this.evidenceList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.evidence_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Evidence evidence = evidenceList.get(position);
        holder.title.setText(evidence.getTitle());
        holder.description.setText(evidence.getDescription());
        holder.date.setText(evidence.getDate());
    }

    @Override
    public int getItemCount() {
        return evidenceList.size();
    }
}



