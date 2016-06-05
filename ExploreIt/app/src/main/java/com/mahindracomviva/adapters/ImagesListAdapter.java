package com.mahindracomviva.adapters;

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
 * Created by Abhinav.Anand on 5/3/2016.
 */
public class ImagesListAdapter extends RecyclerView.Adapter<ImagesListAdapter.ViewHolder> {

    private MainActivity activity;
    ImageLoader imageLoader = null;
    DisplayImageOptions options = null;
    GeneralOperationsNew generalOperationsNew;
    boolean isImageDownloaded = false;

    ProgressDialog pDialog = null;

    public ImagesListAdapter(MainActivity activity, GeneralOperationsNew generalOperationsNew){
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
        View sView = mLayoutInflater.inflate(R.layout.images_list_layout, null);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {

            isImageDownloaded = generalOperationsNew.getBooleanSharedPreferenceValue(activity, PropertiesNew.isImageDownloaded+position);
            imageLoader.displayImage(PropertiesNew.url + PropertiesNew.titles_images[position] +PropertiesNew.thumbnail + PropertiesNew.imageExtension,holder.iv_image_thumbnail ,
                    options);

            holder.tv_image_title.setText(PropertiesNew.titles_images[position].replace("_", " "));
            if(isImageDownloaded){
                holder.btn_image_download.setBackgroundResource(R.mipmap.open);
            }else{
                holder.btn_image_download.setBackgroundResource(R.mipmap.download);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return PropertiesNew.titles_images.length;
    }

    public void refresh(){

        this.notifyDataSetChanged();

        activity.refreshActivity();

    }

    public boolean isContentDownloaded(int position){
        boolean isDownloaded = generalOperationsNew.getBooleanSharedPreferenceValue(activity,PropertiesNew.isImageDownloaded+position);
        return isDownloaded;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_image_thumbnail;
        TextView tv_image_title;
        Button btn_image_download;

        public ViewHolder(View itemView) {
            super(itemView);

            try {
                iv_image_thumbnail = (ImageView) itemView.findViewById(R.id.iv_images);
                tv_image_title = (TextView) itemView.findViewById(R.id.tv_images);
                btn_image_download = (Button) itemView.findViewById(R.id.btn_images);

                //iv_video_thumbnail.setOnClickListener(this);
                //tv_video_title.setOnClickListener(this);
                btn_image_download.setOnClickListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.btn_images:

                    if(!isContentDownloaded(getPosition())){
                        if(generalOperationsNew.isInternetAvailable(activity)){
                            new DownloadVideoAsyncTask(activity, pDialog, getPosition(), PropertiesNew.imageExtension, PropertiesNew.titles_images[getPosition()]).execute(Integer.toString(getPosition()));
                        }


                    }else{
                        Intent launchIntent = activity.getPackageManager()
                                .getLaunchIntentForPackage(
                                        "com.mahindracomviva.drmplayer");
                        launchIntent.putExtra("contentId", Integer.toString(getPosition()+11));
                        launchIntent.putExtra("sourcePath", Environment
                                .getExternalStorageDirectory().getAbsolutePath()
                                + PropertiesNew.divider
                                + PropertiesNew.titles_images[getPosition()]
                                + PropertiesNew.imageExtension);
                        launchIntent.putExtra("fileName", PropertiesNew.titles_images[getPosition()]);
                        launchIntent.putExtra("extension", PropertiesNew.imageExtension);
                        activity.startActivity(launchIntent);
                    }
                    break;
            }
        }
    }
}
