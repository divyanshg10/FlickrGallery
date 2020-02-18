package com.dcodestar.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GetFlickrJsonData extends AsyncTask<String,Void,List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";
    private List<Photo> photoList=null;
    private String baseUrl;
    private String language;
    private boolean matchAll;

    private final OnDataAvailable callback;
    private Boolean runningOnSameThread=false;

    interface OnDataAvailable{
        void onDataAvailable(List<Photo> data,DownloadStatus status);
    }

    public GetFlickrJsonData(String baseUrl, String language, boolean matchAll, OnDataAvailable callback) {
        this.baseUrl = baseUrl;
        this.language = language;
        this.matchAll = matchAll;
        this.callback = callback;
    }

    void executeOnSameThread(String searchCriteria){
        runningOnSameThread=true;
        String destinationURI=createURI(searchCriteria,language,matchAll);

        GetRawData getRawData=new GetRawData(this);
        getRawData.execute(destinationURI);
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        if(callback!=null){
            callback.onDataAvailable(photoList,DownloadStatus.OK);
        }
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: start");
        String destinationUri=createURI(params[0],language,matchAll);
        GetRawData getRawData=new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");
        return photoList;
    }

    String createURI(String searchCriteria, String language, boolean matchAll){

        return Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("tags",searchCriteria)
                .appendQueryParameter("tagmode",matchAll?"all":"any")
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback","1")
                .appendQueryParameter("lang",language)
                .build().toString();
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        if(status==DownloadStatus.OK){
            photoList=new ArrayList<>();
            try{
                JSONObject jsondata=new JSONObject(data);
                JSONArray itemsArray=jsondata.getJSONArray("items");

                for(int i=0;i<itemsArray.length();i++){
                  JSONObject jsonPhoto=itemsArray.getJSONObject(i);
                  String title=jsonPhoto.getString("title");
                  String author=jsonPhoto.getString("author");
                  String authorId=jsonPhoto.getString("author_id");
                  String tags=jsonPhoto.getString("tags");

                  JSONObject jsonMedia=jsonPhoto.getJSONObject("media");
                  String photoUrl=jsonMedia.getString("m");

                  String link=photoUrl.replaceFirst("_m","_b");

                  Photo photo=new Photo(title,author,authorId,link,tags,photoUrl);
                  photoList.add(photo);
                }
            }catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: error processing json data"+e.getMessage() );
                status=DownloadStatus.FAILED_OR_EMPTY;
            }
        }
        if(callback!=null&&runningOnSameThread){
            callback.onDataAvailable(photoList,status);
        }
    }
}
