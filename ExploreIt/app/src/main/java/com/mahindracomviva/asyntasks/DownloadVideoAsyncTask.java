package com.mahindracomviva.asyntasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;

import com.mahindracomviva.activities.MainActivity;
import com.mahindracomviva.utilities.GeneralOperationsNew;
import com.mahindracomviva.utilities.PropertiesNew;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Abhinav.Anand on 5/2/2016.
 */
public class DownloadVideoAsyncTask extends AsyncTask<String, String, String>{

    private MainActivity activity;
    ProgressDialog pDialog = null;
    InputStream input;
    OutputStream output;
    DownloadVideoAsyncTask downloadVideoAsyncTask = null;
    Boolean flag = true;
    GeneralOperationsNew generalOperationsNew;
    private int position = 0;
    private String extension = null;
    private String fileName = null;

    public DownloadVideoAsyncTask(MainActivity activity, ProgressDialog pDialog, int position, String extension, String fileName) {
        this.activity = activity;
        this.pDialog = pDialog;
        this.position = position;
        this.extension = extension;
        this.fileName = fileName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        generalOperationsNew = new GeneralOperationsNew();

        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Downloading... Please wait ...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);

        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) { // TODO
                if(dialog != null){
                    dialog.dismiss();
                }

                cancel(true);

            }


        });

        if(!isCancelled())
        pDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {

        downloadVideoAsyncTask = this;
        int count;

            if (isCancelled())
                return null;
            URL url = null;
            try {
                /*url = new URL(PropertiesNew.url
                        + PropertiesNew.titles[Integer.parseInt(params[0])]
                        + extension);*/
                url = new URL(PropertiesNew.url
                        + fileName
                        + extension);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lengthOfFile = conection.getContentLength();

                // download the file
                input = new BufferedInputStream(url.openStream(), 8192);

				/*
				 * try { File file = new
				 * File(Environment.getExternalStorageDirectory
				 * ().getAbsolutePath() + PropertiesNew.sourceFolder);
				 * if(!file.exists()){ file.mkdirs(); }
				 *
				 * } catch (Exception e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */

                // Output stream
                /*output = new FileOutputStream(
                        Environment.getExternalStorageDirectory()
                                + PropertiesNew.divider
                                + PropertiesNew.titles[Integer
                                .parseInt(params[0])]
                                + extension);*/

                output = new FileOutputStream(
                        Environment.getExternalStorageDirectory()
                                + PropertiesNew.divider
                                + fileName
                                + extension);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    if (!isCancelled()) {

                        output.write(data, 0, count);
                    } else {
                        break;
                    }

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(!isCancelled()){
                switch(extension){

                    case PropertiesNew.videoExtension:
                        generalOperationsNew.setBooleanSharedPreferenceValue(activity, PropertiesNew.isVideoDownloaded+position, true);
                        activity.refreshActivity();
                        break;

                    case PropertiesNew.imageExtension:
                        generalOperationsNew.setBooleanSharedPreferenceValue(activity, PropertiesNew.isImageDownloaded+position, true);
                        activity.refreshActivity();
                        break;

                    case PropertiesNew.audioExtension:
                        generalOperationsNew.setBooleanSharedPreferenceValue(activity, PropertiesNew.isAudioDownloaded+position, true);
                        activity.refreshActivity();
                        break;
                }

            }

            return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
        // setting progress percentage
        try {
            if (!isCancelled())
                pDialog.setProgress(Integer.parseInt(values[0]));
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String s) {


        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }

        super.onPostExecute(s);
    }
}
