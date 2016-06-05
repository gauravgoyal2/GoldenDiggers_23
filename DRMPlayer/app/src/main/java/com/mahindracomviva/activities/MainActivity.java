package com.mahindracomviva.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.mahindracomviva.drmplayer.R;
import com.mahindracomviva.utilities.GeneralOperationsNew;
import com.mahindracomviva.utilities.PropertiesNew;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView vv_player;
    private ImageView iv_player;
    private LinearLayout ll_audio_controls;
    private Button btn_play_pause;
    private SeekBar seekBar;
    private MediaController mediaController;
    private ProgressDialog pDialog = null;
    private String sourcePath, destinationPath, contentId, fileName, fileName1;
    private String decryptionKey = null;
    private boolean decryptionSuccessful = false;
    GeneralOperationsNew generalOperationsNew;
    private boolean isAsyncCancelled = false;
    private String extension = null;

    private URL url = null;
    private HttpURLConnection conn = null;
    private MediaPlayer mediaPlayer = null;
    private Bundle extras = null;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vv_player = (VideoView) findViewById(R.id.vv_player);
        iv_player = (ImageView) findViewById(R.id.iv_player);
        seekBar = (SeekBar) findViewById(R.id.seekbar1);
        ll_audio_controls = (LinearLayout) findViewById(R.id.ll_audio_controls);
        btn_play_pause = (Button) findViewById(R.id.btn_play_pause);
        generalOperationsNew = new GeneralOperationsNew();

        // sourcePath =
        // Environment.getExternalStorageDirectory().getAbsolutePath() +
        // "/video_encrypted.mp4";
        destinationPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + PropertiesNew.sdCardPath
                + this.getPackageName() + PropertiesNew.destinationPath;
        // fileName = "video_encrypted.mp4";
        // fileName1 = "video_decrypted.mp4";
        // contentId = "1";

        try {

            if (generalOperationsNew.isInternetAvailable(this)) {

                extras = getIntent().getExtras();

                if (extras != null) {

                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                            askForPermissions();
                            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                                decryptionOperation();
                            }
                        }else{

                            decryptionOperation();
                        }


                } else {

                    System.out.println("Extras are null");
                }

            }else{
                Toast.makeText(this, "Internet is not available", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void decryptionOperation(){

        extension = extras.getString("extension");

        System.out.println("Extras not null");
        sourcePath = extras.getString("sourcePath");
        fileName = extras.getString("fileName");
        contentId = extras.getString("contentId");

        System.out.println(sourcePath);
        System.out.println(fileName);

        fileName1 = fileName + PropertiesNew.decrypted
                + extension;

                        /*File file = new File(destinationPath);
                        if (!file.exists()) {
                            file.mkdir();
                        }
    */
        new DecryptionAsync(this, extension).execute();

        btn_play_pause.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        System.out.println("Grant results are : "+ grantResults);

        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    decryptionOperation();

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        try {
            File dataFolder = new File(Environment
                    .getExternalStorageDirectory().getAbsolutePath()
                    + PropertiesNew.sdCardPath + getPackageName());
            if (dataFolder.exists()) {
                deleteDir(dataFolder);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void askForPermissions() {

        int hasWriteStoragePermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showMessageOKCancel("You need to allow access to write storage for the app to function smoothly",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            mediaPlayer = null;
        }

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    @Override
    public void onClick(View v) {

        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            btn_play_pause.setBackgroundResource(R.drawable.play);
        }else{
            mediaPlayer.start();
            btn_play_pause.setBackgroundResource(R.drawable.pause);
        }
    }

    private class DecryptionAsync extends AsyncTask<String, String, String> implements MediaPlayer.OnPreparedListener {

        private Context context;
        private String extension;

        public DecryptionAsync(Context context, String extension) {
            this.context = context;
            this.extension = extension;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait ....");

            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {

                    dialog.dismiss();
                    cancel(true);
                }
            });
            pDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            copyFiletoDataFolder(extension);

            if(isCancelled())
                return null;
            decryptionKey = fetchDecryptionKey();
            System.out.println(decryptionKey);

            byte[] encodedKey;
            SecretKey secretKey = null;

            try {
                encodedKey = Base64.decode(decryptionKey);
                secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length,
                        "Blowfish");
            } catch (Base64DecodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            decryptionSuccessful = decryptFile(destinationPath + fileName
                    + extension, destinationPath + fileName1
                    + extension, secretKey);

            System.out.println("Was decryption successful :- "
                    + decryptionSuccessful);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            pDialog.dismiss();
            if (pDialog != null) {
                pDialog = null;
            }

            if (decryptionSuccessful) {
                if(extension.equals(PropertiesNew.videoExtension)){
                    System.out.println("Ready to play video");
                    iv_player.setVisibility(View.GONE);
                    vv_player.setVisibility(View.VISIBLE);
                    ll_audio_controls.setVisibility(View.GONE);

                    vv_player.setVideoPath(destinationPath + fileName1
                            + extension);
                    vv_player.setMediaController(new MediaController(context));
                    vv_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            System.out.println("Before starting media player");
                            mp.start();
                            System.out.println("After starting media player");
                        }
                    });

                }else if(extension.equals(PropertiesNew.imageExtension)){

                    vv_player.setVisibility(View.GONE);
                    iv_player.setVisibility(View.VISIBLE);
                    ll_audio_controls.setVisibility(View.GONE);

                    Bitmap bitmap = BitmapFactory.decodeFile(destinationPath + fileName1 + extension);
                    iv_player.setImageBitmap(bitmap);

                }else if(extension.equals(PropertiesNew.audioExtension)){

                    vv_player.setVisibility(View.GONE);
                    iv_player.setVisibility(View.GONE);
                    ll_audio_controls.setVisibility(View.VISIBLE);

                    btn_play_pause.setBackgroundResource(R.drawable.pause);



                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(destinationPath + fileName1 + extension);
                        mediaPlayer.setLooping(false);
                        mediaPlayer.prepareAsync();



                       mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                           @Override
                           public void onPrepared(MediaPlayer mp) {
                               try {
                                   seekBar.setMax(mediaPlayer.getDuration());
                                   mp.start();

                               } catch (IllegalStateException e) {
                                   e.printStackTrace();
                               }
                           }
                       });



                        final Handler mHandler = new Handler();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mediaPlayer != null){
                                    try {
                                        int currentPosition = mediaPlayer.getCurrentPosition();
                                        seekBar.setProgress(currentPosition);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                mHandler.postDelayed(this, 1000);
                            }
                        });





                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if(mediaPlayer != null && fromUser){

                                        mediaPlayer.seekTo(progress);
                                        seekBar.setProgress(progress);


                                }else{
                                    if(mediaPlayer != null){
                                        if(progress == mediaPlayer.getDuration()){
                                            mediaPlayer.stop();
                                            seekBar.setProgress(0);
                                            mediaPlayer.seekTo(0);
                                            btn_play_pause.setBackgroundResource(R.drawable.play);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });




                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(context, "Decryption was unsuccessful",
                        Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            try {
                mp.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyFiletoDataFolder(String extension) {

        try {
            File sourceFile = new File(sourcePath);

            File destinationFile = new File(destinationPath, fileName
                    + extension);

            if(!destinationFile.exists())
            {
                destinationFile.mkdir();

            }


            try {
                FileUtils.copyFile(sourceFile, destinationFile);
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String fetchDecryptionKey() {



        try {


            url = new URL(PropertiesNew.url + contentId);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            InputStream stream = conn.getInputStream();
            String response = convertStreamToString(stream);

            return response;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {

            if (conn != null) {
                conn = null;
            }
        }

    }

    static String convertStreamToString(java.io.InputStream is) {

        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";

    }

    private boolean decryptFile(String srcPath, String destPath,
                                SecretKey secretKey) {
        File encryptedFile = new File(srcPath);
        File decryptedFile = new File(destPath);

        InputStream inStream = null;
        OutputStream outStream = null;
        Cipher cipher;
        try {
            System.out.println("----secretKey--decrypt---=" + secretKey);
            /**
             * Initialize the cipher for decryption
             */
            cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            /**
             * Initialize input and output streams
             */
            System.out.println("Before inputstream");
            inStream = new FileInputStream(encryptedFile);
            System.out.println("After inputstream");
            outStream = new FileOutputStream(decryptedFile);
            System.out.println("After outputstream");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
            return true;
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex);
            return false;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex);
            return false;
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
            return false;
        } catch (BadPaddingException ex) {
            System.out.println(ex);
            return false;
        } catch (InvalidKeyException ex) {
            System.out.println(ex);
            return false;
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            return false;
        } catch (IOException ex) {
            System.out.println(ex);
            return false;
        }
    }

}
