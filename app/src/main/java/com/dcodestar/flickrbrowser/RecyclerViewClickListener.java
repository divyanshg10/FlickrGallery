package com.dcodestar.flickrbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

class RecyclerViewClickListener extends RecyclerView.SimpleOnItemTouchListener {
    interface OnItemClick{
        void onSingleClick(View v,int position);
        void onLongClick(View v,int position);
    }
    OnItemClick listener;
    GestureDetectorCompat gestureDetector;

    public RecyclerViewClickListener(final OnItemClick listener, Context context, final RecyclerView recyclerView) {
        gestureDetector=new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(child!=null&&gestureDetector!=null) {
                    listener.onSingleClick(child, recyclerView.getChildAdapterPosition(child));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(child!=null&&gestureDetector!=null) {
                    listener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        if(gestureDetector!=null){
            boolean result=gestureDetector.onTouchEvent(e);
            return result;
        }else {
            return false;
        }
    }
}
