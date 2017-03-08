package bluepanther.envirinsta.Signing;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import bluepanther.envirinsta.Adapter.Cred_Update;
import bluepanther.envirinsta.Adapter.CurrentUser;
import bluepanther.envirinsta.InternalStorage.Loginext;
import bluepanther.envirinsta.R;

public class Sign_In extends AppCompatActivity {
    EditText username,password;
    Button signinbut;
    TextView signupbut,an_textview,pass_textview;
    String usn="",pass="";
    ProgressDialog progressDialog;
    private static final int CAMERA = 110 , WRITE_EXTERNAL_STORAGE = 111, INTERNET = 112, RECORD_AUDIO = 113;


    private String Base_url = "https://envirinsta.firebaseio.com/Accounts/";
    private Firebase fb_db;
    public Loginext obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        if(pref.getBoolean("islog",false))
        {
            new CurrentUser(pref.getString("user",null),pref.getString("pass",null),pref.getString("sclass",null),pref.getString("ssec",null),pref.getString("idate",null),pref.getString("adate",null),pref.getString("vdate",null),pref.getString("fdate",null),pref.getString("tdate",null));
            Intent intent = new Intent(Sign_In.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.signin);

        requestPermission();//oncreate


        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        signinbut = (Button)findViewById(R.id.signinbut);
        signupbut = (TextView)findViewById(R.id.signupbut);
        an_textview = (TextView)findViewById(R.id.an_textview);
        pass_textview = (TextView)findViewById(R.id.pass_textview);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-R.ttf");
        signinbut.setTypeface(typeface);
        an_textview.setTypeface(typeface);
        pass_textview.setTypeface(typeface);
        Firebase.setAndroidContext(this);
        fb_db = new Firebase(Base_url);

        signupbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sign_In.this,Sign_Up.class);
                startActivity(intent);

            }
        });

        signinbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usn = username.getText().toString();
                pass = password.getText().toString();
                if(usn.length()>0&&pass.length()>0)
                {
                    new MyTask().execute();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_In.this);
                    builder.setMessage("Please provide complete details")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });

    }





    public void requestPermission()
    {
        //request permission
        boolean hasPermissioncamera= (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) == MockPackageManager.PERMISSION_GRANTED);
        System.out.println("RES IS "+hasPermissioncamera);
        if (!hasPermissioncamera) {
            System.out.println("NOOB CAMERA");
            ActivityCompat.requestPermissions(Sign_In.this,
                    new String[]{android.Manifest.permission.CAMERA},CAMERA);
        }

        boolean hasPermissionexternal = (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == MockPackageManager.PERMISSION_GRANTED);
        System.out.println("RES IS "+hasPermissionexternal);

        if (!hasPermissionexternal) {
            System.out.println("NOOB EXTERNAL");
            ActivityCompat.requestPermissions(Sign_In.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_STORAGE);
        }

        boolean hasPermissionInternet = (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.INTERNET) == MockPackageManager.PERMISSION_GRANTED);
        System.out.println("RES IS "+hasPermissionInternet);

        if (!hasPermissionInternet) {
            System.out.println("NOOB INTERNET");
            ActivityCompat.requestPermissions(Sign_In.this,
                    new String[]{android.Manifest.permission.INTERNET},INTERNET);
        }

        boolean hasPermissionaudio = (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.RECORD_AUDIO) == MockPackageManager.PERMISSION_GRANTED);
        System.out.println("RES IS "+hasPermissionaudio);

        if (!hasPermissionaudio) {
            System.out.println("NOOB AUDIO");
            ActivityCompat.requestPermissions(Sign_In.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},RECORD_AUDIO);
        }
    }
    
    
    


    private class MyTask extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Sign_In.this, "Message", "Logging in...");

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array

            System.out.println("BGPROCESS");
            fb_db=new Firebase(Base_url+usn+"/");
            fb_db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
//                    {
                        Cred_Update cred_update = dataSnapshot.getValue(Cred_Update.class);
                    if(cred_update!=null) {
                        String uname = cred_update.getUsn();
                        String password = cred_update.getPass();

                        if ((uname.equals(usn)) && (password.equals(pass))) {

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();

                            editor.putBoolean("islog",true);
                            editor.putString("user",uname);
                            editor.putString("pass",password);
                            editor.putString("sclass",cred_update.cls);
                            editor.putString("ssec",cred_update.sec);
                            editor.putString("tdate",cred_update.tdate);
                            editor.putString("idate",cred_update.idate);
                            editor.putString("adate",cred_update.adate);
                            editor.putString("vdate",cred_update.vdate);
                            editor.putString("fdate",cred_update.fdate);


                            editor.commit();
                            new CurrentUser(uname, password, cred_update.cls, cred_update.sec, cred_update.idate, cred_update.adate, cred_update.vdate, cred_update.fdate, cred_update.tdate);


                            System.out.println("SUCCESS");
                            Toast.makeText(Sign_In.this, "Login Successful", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                            Intent intent = new Intent(Sign_In.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Sign_In.this,"Login Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(Sign_In.this,"Login Failed",Toast.LENGTH_SHORT).show();
                    }



               //     }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("FIREBASE ERROR OCCOURED");
                }
            });

            return "SUCCESS";
        }



        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result)
        {


        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case CAMERA: {
                System.out.println("inside camera");
                if (grantResults.length > 0 && grantResults[0] == MockPackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(Sign_In.this, "The app was not allowed to get your phone Camera. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case WRITE_EXTERNAL_STORAGE: {
                System.out.println("inside storage");

                if (grantResults.length > 0 && grantResults[0] == MockPackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(Sign_In.this, "The app was not allowed to get your Internal Storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                break;

            }

            case INTERNET: {
                System.out.println("inside internet");

                if (grantResults.length > 0 && grantResults[0] == MockPackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(Sign_In.this, "The app was not allowed to access internet. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                break;

            }

            case RECORD_AUDIO: {
                System.out.println("inside audio");

                if (grantResults.length > 0 && grantResults[0] == MockPackageManager.PERMISSION_GRANTED)
                {
//                    Toast.makeText(Sign_In.this, "Permission granted.", Toast.LENGTH_SHORT).show();
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(Sign_In.this, "The app was not allowed to record audio. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }
    
}
