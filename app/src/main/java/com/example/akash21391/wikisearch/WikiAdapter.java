package com.example.akash21391.wikisearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WikiAdapter extends RecyclerView.Adapter<WikiAdapter.WikiHolder> {

    private ArrayList<WikiData> wikiDataList;

    RecyclerView mRecyclerView;
    Context mContext;

    public WikiAdapter(RecyclerView recyclerView, Context context, ArrayList<WikiData> arrayList) {
        super();
        wikiDataList = arrayList;
        mContext = context;
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public WikiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wiki_data_item, parent, false);
        itemView.setOnClickListener(onClickListener);
        return new WikiHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WikiHolder holder, int position) {
        holder.titleView.setText(wikiDataList.get(position).title);
        holder.dataView.setText(wikiDataList.get(position).data);
    }

    @Override
    public int getItemCount() {
        return wikiDataList.size();
    }

    class WikiHolder extends RecyclerView.ViewHolder {

        TextView titleView, dataView;

        public WikiHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title_view);
            dataView = itemView.findViewById(R.id.data_view);
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (MainActivity.isNetworkConnected()) {
                int pos = mRecyclerView.getChildLayoutPosition(v);
                WikiData current = wikiDataList.get(pos);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(current.webUrl));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                Toast.makeText(mContext, "No connected to internet",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
}
