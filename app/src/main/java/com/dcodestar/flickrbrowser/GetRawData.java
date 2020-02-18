package com.dcodestar.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus{IDLE,PROCESSING,NOT_INITIALISED,FAILED_OR_EMPTY,OK}

class GetRawData extends AsyncTask<String,Void,String> {
    interface OnDownloadComplete{
        void onDownloadComplete(String data,DownloadStatus status);
    }

    private static final String TAG = "GetRawData";
    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete callback;


    public GetRawData(OnDownloadComplete callback){
        mDownloadStatus=DownloadStatus.IDLE;
        this.callback=callback;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: parameter is "+s);
        if(callback!=null) {
            callback.onDownloadComplete(s, mDownloadStatus);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.e(TAG, "doInBackground: "+strings[0] );
        try {
            StringBuilder result=new StringBuilder();
            if(strings==null){
                mDownloadStatus=DownloadStatus.NOT_INITIALISED;
                return null;
            }
            mDownloadStatus=DownloadStatus.PROCESSING;
            URL url=new URL(strings[0]);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response=connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was"+response);
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while(null!=(line=reader.readLine())){
                result.append(line).append("\n");
            }
            mDownloadStatus=DownloadStatus.OK;
            return result.toString();

        }catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: MalformedURLException "+e.getMessage() );
        }catch (IOException e){
            Log.e(TAG, "doInBackground: IOException "+e.getMessage() );
        }catch (SecurityException e){
            Log.e(TAG, "doInBackground: SecurityException "+e.getMessage() );
        }
        mDownloadStatus=DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

    void runInSameThread(String s){
//        onPostExecute(doInBackground(s));
        if(callback!=null){
            callback.onDownloadComplete(doInBackground(s),mDownloadStatus);
        }
    }

}
