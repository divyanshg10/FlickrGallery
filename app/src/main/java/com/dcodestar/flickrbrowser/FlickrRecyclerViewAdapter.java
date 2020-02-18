package com.dcodestar.flickrbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotolist;
    private Context mcontext;

    public FlickrRecyclerViewAdapter(List<Photo> mPhotolist, Context mcontext) {
        this.mPhotolist = mPhotolist;
        this.mcontext = mcontext;
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder flickrImageViewHolder, int i) {
        if(mPhotolist!=null&&mPhotolist.size()==0){
            flickrImageViewHolder.thumbnail.setImageResource(R.drawable.placeholder);
            flickrImageViewHolder.title.setText("\n\nNo Image Found\n");
        }
        else {
            Photo photo = mPhotolist.get(i);
            Picasso.with(mcontext)
                    .load(photo.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(flickrImageViewHolder.thumbnail);
            flickrImageViewHolder.title.setText(photo.getTitle());
        }
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: new View requested");
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse,viewGroup,false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return ((mPhotolist!=null)&&(mPhotolist.size()!=0)?mPhotolist.size():1);
    }


    void loadNewData(List<Photo> newPhotos){
        mPhotolist=newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return ((mPhotolist!=null)&&(mPhotolist.size()!=0)?mPhotolist.get(position):null);
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail=null;
        TextView title=null;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail=itemView.findViewById(R.id.thumbnail);
            this.title=itemView.findViewById(R.id.title);
        }
    }
}
