package com.example.meet_practical.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meet_practical.UserList_Bean.ResultsItem;
import com.example.meet_practical.R;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tv_Name , tv_Location , tv_Gender , tv_Name_Number , tv_Email , tv_Username , tv_DOB ,
                     tv_Age , tv_Registered_On , tv_Registered_At_Age , tv_Phone , tv_Cell;
    private CircleImageView circleImageView;
    private String Json_str = null;
    private Gson gson;
    private  ResultsItem resultsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Init();
        gson = new Gson();
        Json_str = getIntent().getStringExtra("UserProfile");

        resultsItem = gson.fromJson(Json_str, ResultsItem.class);

        Glide.with(UserProfileActivity.this)
                .load(resultsItem.getPicture().getLarge())
                .into(circleImageView);


        tv_Name.setText(resultsItem.getName().getFirst() + " " + resultsItem.getName().getLast() + " " + resultsItem.getName().getTitle());
        tv_Location.setText(resultsItem.getLocation().getCity() + "-" +  resultsItem.getLocation().getState() + "-" + resultsItem.getLocation().getCountry() + "-" + resultsItem.getLocation().getPostcode());
        tv_Gender.setText(resultsItem.getGender());
        tv_Name_Number.setText(resultsItem.getLocation().getStreet().getName() + " " + resultsItem.getLocation().getStreet().getNumber());
        tv_Email.setText(resultsItem.getEmail());
        tv_Username.setText(resultsItem.getLogin().getUsername());
        tv_DOB.setText(resultsItem.getDob().getDate());
        tv_Age.setText(resultsItem.getDob().getAge() + "");
        tv_Registered_On.setText(resultsItem.getRegistered().getDate());
        tv_Registered_At_Age.setText(resultsItem.getRegistered().getAge() + "");
        tv_Phone.setText(resultsItem.getPhone());
        tv_Cell.setText(resultsItem.getCell());


        // Click Event to Send Email
        tv_Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" +  tv_Email.getText().toString()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "body");
                //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text
                startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
            }
        });

        // Click Event to Dial Phone Call
        tv_Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+resultsItem.getPhone()));
                startActivity(callIntent);
            }
        });

        // Click Event to Dial Phone Call
        tv_Cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+resultsItem.getCell()));
                startActivity(callIntent);
            }
        });

        // Click Event to Navigate into Google Map
        tv_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String city = ((TextView) view.findViewById(R.id.city)).getText().toString();
                Intent i2 = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" +tv_Location.getText().toString()  ));
                view.getContext().startActivity(i2);
            }
        });

    }

    // Initialization UI Component
    public void Init()
    {
        tv_Name = (TextView)findViewById(R.id.tv_Name);
        tv_Location = (TextView)findViewById(R.id.tv_Location);
        tv_Gender = (TextView)findViewById(R.id.tv_Gender);
        tv_Name_Number = (TextView)findViewById(R.id.tv_Name_Number);
        tv_Email = (TextView)findViewById(R.id.tv_Email);
        tv_Username = (TextView)findViewById(R.id.tv_Username);
        tv_DOB = (TextView)findViewById(R.id.tv_DOB);
        tv_Age = (TextView)findViewById(R.id.tv_Age);
        tv_Registered_On = (TextView)findViewById(R.id.tv_Registered_On);
        tv_Registered_At_Age = (TextView)findViewById(R.id.tv_Registered_At_Age);
        tv_Phone = (TextView)findViewById(R.id.tv_Phone);
        tv_Cell = (TextView)findViewById(R.id.tv_Cell);
        circleImageView = (CircleImageView)findViewById(R.id.UserProfileImage);
    }

    // Contect Menu here, For Share User Image ...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.share:
                requestStoragePermission();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    // Funcion To Check User Has Permission or not IF Not hen Request Permission
    // If User has Permission then Share Image AND Text message also ...
    private void requestStoragePermission() {
        Dexter.withActivity(UserProfileActivity.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            (new ShareTask(UserProfileActivity.this)).execute(tv_Name.getText().toString());
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    //Asynctask Class To Share Image and Share Text Message also...
    public class ShareTask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog progressDialog;
        URL ImageUrl;
        Bitmap bitmapImg = null;
        File file;

        public ShareTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getString(R.string.loading_msg));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                ImageUrl = new URL(resultsItem.getPicture().getLarge()); //
                HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmapImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String path = ImageUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bitmapImg.compress(Bitmap.CompressFormat.PNG, 99, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_msg) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(intent, "Share Image"));
            progressDialog.dismiss();

        }
    }

    // AlertDialog box for Requst Permission
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle(R.string.permission_msg);
        builder.setMessage(R.string.permission_storage);
        builder.setPositiveButton(R.string.dialog_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(R.string.dialog_option_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


}
