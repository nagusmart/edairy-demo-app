package com.example.edairycodinground;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.edairycodinground.model.SpeedModel;
import com.example.edairycodinground.recevier.NetworkChangeReceiver;
import com.example.internet_speed_testing.InternetSpeedBuilder;
import com.example.internet_speed_testing.ProgressionModel;
import com.github.anastr.speedviewlib.SpeedView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScreenOneActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    static int position = 0;
    static int lastPosition = 0;
    ImageView barImage,searchImg;
    TextView downloadSpeed, uploadSpeed, totalSpeed, selectMobileNo, submitTxt;

    InternetSpeedBuilder builder;

    EditText mobileNoEdt;

    GoogleApiClient mGoogleApiClient;

    private int RESOLVE_HINT = 2;

    DatabaseReference databaseReference;

    String totalSpeeds = "";
    String upSpeed = "";
    String downSpeed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_circle);
        barImage = (ImageView) findViewById(R.id.barImageView);
        downloadSpeed = (TextView) findViewById(R.id.download);
        uploadSpeed = (TextView) findViewById(R.id.uplaod);
        totalSpeed = (TextView) findViewById(R.id.total_speed);

        mobileNoEdt = findViewById(R.id.mobile_no);

        searchImg=findViewById(R.id.search_img);

        selectMobileNo = findViewById(R.id.select_mobile_no);

        submitTxt = findViewById(R.id.submit);

         builder = new InternetSpeedBuilder(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("SPEED_LIST");


        //set google api client for hint request
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        selectMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHintPhoneNumber();
            }
        });

        submitTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNoEdt.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ScreenOneActivity.this, "Please Enter the Mobile No Please", Toast.LENGTH_SHORT).show();
                } else {
                    String phoneNo = mobileNoEdt.getText().toString();
                    String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
                    SpeedModel speedModel = new SpeedModel(phoneNo, totalSpeeds, upSpeed, downSpeed, timeStamp);

                    databaseReference.child(phoneNo).setValue(speedModel, new DatabaseReference.CompletionListener() {
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                            if(error == null){
                               Toast.makeText(ScreenOneActivity.this, "SuccessFully Added",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(ScreenOneActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent=new Intent(ScreenOneActivity.this,ScreenTwoActivity.class);
               startActivity(intent);

            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("CHECK_CONNECTION"));


    }

    public void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Log.e("error = ", e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {

                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string

                String mobileNo=credential.getId().substring(3);

                mobileNoEdt.setText(mobileNo);
            }
        }
    }


    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean status = intent.getBooleanExtra("NETWORK_STATUS", false);

            if (status) {

                Toast.makeText(ScreenOneActivity.this, "Internet Connection Available", Toast.LENGTH_SHORT).show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScreenOneActivity.this);
                builder.setTitle("No internet Connection");
                builder.setMessage("Please turn on internet connection to continue");
                builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    };


    public static String formatFileSize(double size) {

        String hrSize;
        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" ");
        } else if (g > 1) {
            hrSize = dec.format(g);
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" mb/s");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" kb/s");
        } else {
            hrSize = dec.format(b);
        }

        return hrSize;
    }


    public int getPositionByRate(float rate) {

        if (rate <= 1) {
            return (int) (rate * 30);

        } else if (rate <= 10) {
            return (int) (rate * 6) + 30;

        } else if (rate <= 30) {
            return (int) ((rate - 10) * 3) + 90;

        } else if (rate <= 50) {
            return (int) ((rate - 30) * 1.5) + 150;

        } else if (rate <= 100) {
            return (int) ((rate - 50) * 1.2) + 180;
        }

        return 0;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
      //  Toast.makeText(ScreenOneActivity.this, "Connected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(ScreenOneActivity.this, "Suspended", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(ScreenOneActivity.this, "Failed", Toast.LENGTH_SHORT).show();


    }


    @Override
    protected void onResume() {
        super.onResume();

        builder.setOnEventInternetSpeedListener(new InternetSpeedBuilder.OnEventInternetSpeedListener() {
            @Override
            public void onDownloadProgress(int count, final ProgressionModel progressModel) {
                Log.d("SERVER", "" + progressModel.getDownloadSpeed());


                //double speed = progressModel.getUploadSpeed()/((Double)1000000);
                java.math.BigDecimal bigDecimal = new java.math.BigDecimal("" + progressModel.getDownloadSpeed());
                float finalDownload = (bigDecimal.longValue() / 1000000);

                Log.d("NET_SPEED", "" + (float) (bigDecimal.longValue() / 1000000));


                java.math.BigDecimal bd = progressModel.getDownloadSpeed();

                final double d = bd.doubleValue();
                Log.d("SHOW_SPEED", "" + formatFileSize(d));


                Log.d("ANGLE", "" + getPositionByRate(finalDownload));


                position = getPositionByRate(finalDownload);
                downSpeed=formatFileSize(d);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RotateAnimation rotateAnimation;
                        rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setInterpolator(new LinearInterpolator());
                        rotateAnimation.setDuration(100);
                        barImage.startAnimation(rotateAnimation);
                        downloadSpeed.setText("Download Speed: " + formatFileSize(d));
                    }
                });

                lastPosition = position;
            }

            @Override
            public void onUploadProgress(int count, final ProgressionModel progressModel) {

                //double speed = progressModel.getUploadSpeed()/((Double)1000000);
                java.math.BigDecimal bigDecimal = new java.math.BigDecimal("" + progressModel.getUploadSpeed());
                float finalDownload = (bigDecimal.longValue() / 1000000);

                Log.d("NET_SPEED", "" + (float) (bigDecimal.longValue() / 1000000));


                java.math.BigDecimal bd = progressModel.getUploadSpeed();

                final double d = bd.doubleValue();
                Log.d("SHOW_SPEED", "" + formatFileSize(d));


                Log.d("ANGLE", "" + getPositionByRate(finalDownload));


                position = getPositionByRate(finalDownload);
                upSpeed=formatFileSize(d);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RotateAnimation rotateAnimation;
                        rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setInterpolator(new LinearInterpolator());
                        rotateAnimation.setDuration(100);
                        barImage.startAnimation(rotateAnimation);
                        uploadSpeed.setText("Upload Speed: " + formatFileSize(d));
                    }
                });

                lastPosition = position;
            }

            @Override
            public void onTotalProgress(int count, final ProgressionModel progressModel) {


                java.math.BigDecimal downloadDecimal = progressModel.getDownloadSpeed();
                final double downloadFinal = downloadDecimal.doubleValue();

                java.math.BigDecimal uploadDecimal = progressModel.getUploadSpeed();
                final double uploadFinal = uploadDecimal.doubleValue();
                final double totalSpeedCount = (downloadFinal + uploadFinal) / 2;

                float finalDownload = (downloadDecimal.longValue() / 1000000);
                float finalUpload = (uploadDecimal.longValue() / 1000000);
                float totalassumtionSpeed = (finalDownload + finalUpload) / 2;

                position = getPositionByRate(totalassumtionSpeed);
                totalSpeeds=formatFileSize(totalSpeedCount);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     /*   RotateAnimation rotateAnimation;
                        rotateAnimation = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setInterpolator(new LinearInterpolator());
                        rotateAnimation.setDuration(500);
                        barImage.startAnimation(rotateAnimation);*/
                        barImage.setRotation(position);
                        totalSpeed.setText("Total Speed: " + formatFileSize(totalSpeedCount));
                    }
                });

                lastPosition = position;

            }
        });
        builder.start("http://makeup-api.herokuapp.com/api/v1/products/1048.json", 1);

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChangeReceiver(), intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
