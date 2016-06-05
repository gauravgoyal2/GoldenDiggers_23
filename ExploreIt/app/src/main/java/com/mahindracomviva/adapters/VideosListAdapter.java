package com.mahindracomviva.adapters;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahindracomviva.activities.MainActivity;
import com.mahindracomviva.asyntasks.DownloadVideoAsyncTask;
import com.mahindracomviva.exploreit.R;
import com.mahindracomviva.utilities.GeneralOperationsNew;
import com.mahindracomviva.utilities.PropertiesNew;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Abhinav.Anand on 5/2/2016.
 */
public class VideosListAdapter extends RecyclerView.Adapter<VideosListAdapter.ViewHolder>{

    private MainActivity activity;
    ImageLoader imageLoader = null;
    DisplayImageOptions options = null;
    GeneralOperationsNew generalOperationsNew;
    boolean isVideoDownloaded = false;

    ProgressDialog pDialog = null;

    public VideosListAdapter(MainActivity activity, GeneralOperationsNew generalOperationsNew){
        this.activity = activity;
        this.generalOperationsNew = generalOperationsNew;
        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(ImageLoaderConfiguration.createDefault(activity));

        this.options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .showImageOnLoading(R.mipmap.ic_launcher).build();

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
        View sView = mLayoutInflater.inflate(R.layout.videos_list_layout, null);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        try {

            isVideoDownloaded = generalOperationsNew.getBooleanSharedPreferenceValue(activity, PropertiesNew.isVideoDownloaded+position);
            imageLoader.displayImage(PropertiesNew.imageUrls[position], holder.iv_video_thumbnail,
                    options);

            holder.tv_video_title.setText(PropertiesNew.titles[position].replace("_", " "));
            if(isVideoDownloaded){
                holder.btn_video_download.setBackgroundResource(R.mipmap.play);
            }else{
                holder.btn_video_download.setBackgroundResource(R.mipmap.download);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return PropertiesNew.titles.length;
    }


    public void refresh(){

        this.notifyDataSetChanged();

        activity.refreshActivity();

    }

    public boolean isContentDownloaded(int position){
        boolean isDownloaded = generalOperationsNew.getBooleanSharedPreferenceValue(activity,PropertiesNew.isVideoDownloaded+position);
        return isDownloaded;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_video_thumbnail;
        TextView tv_video_title;
        Button btn_video_download;


        public ViewHolder(View itemView) {
            super(itemView);

            try {
                iv_video_thumbnail = (ImageView) itemView.findViewById(R.id.iv_videos);
                tv_video_title = (TextView) itemView.findViewById(R.id.tv_videos);
                btn_video_download = (Button) itemView.findViewById(R.id.btn_videos);

                //iv_video_thumbnail.setOnClickListener(this);
                //tv_video_title.setOnClickListener(this);
                btn_video_download.setOnClickListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){



                case R.id.btn_videos:

                    if(!isContentDownloaded(getPosition())){
                        if(generalOperationsNew.isInternetAvailable(activity)){
                            new DownloadVideoAsyncTask(activity, pDialog, getPosition(), PropertiesNew.videoExtension, PropertiesNew.titles[getPosition()]).execute(Integer.toString(getPosition()));
                        }


                    }else{
                        Intent launchIntent = activity.getPackageManager()
                                .getLaunchIntentForPackage(
                                        "com.mahindracomviva.drmplayer");
                        launchIntent.putExtra("contentId", Integer.toString(getPosition()+1));
                        launchIntent.putExtra("sourcePath", Environment
                                .getExternalStorageDirectory().getAbsolutePath()
                                + PropertiesNew.divider
                                + PropertiesNew.titles[getPosition()]
                                + PropertiesNew.videoExtension);
                        launchIntent.putExtra("fileName", PropertiesNew.titles[getPosition()]);
                        launchIntent.putExtra("extension", PropertiesNew.videoExtension);
                        activity.startActivity(launchIntent);
                    }




                    break;
            }
        }
    }
}
