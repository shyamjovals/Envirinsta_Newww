package bluepanther.envirinsta.Discussions;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bluepanther.envirinsta.Adapter.CurrentUser;
import bluepanther.envirinsta.Adapter.CustomAdapterCon;
import bluepanther.envirinsta.Adapter.RowItem;
import bluepanther.envirinsta.ContentDisp.imgdisp;
import bluepanther.envirinsta.FileUtils.ImgUri;
import bluepanther.envirinsta.NGO_Grid.GPSTracker;
import bluepanther.envirinsta.NGO_Grid.ShowMap;
import bluepanther.envirinsta.R;

/**
 * Created by hhs on 24/2/17.
 */

public class Dis_image extends FragmentActivity implements OnMapReadyCallback {

    TextView textView5,textView15, textView16, textView17;
    EditText editText2;
    ImageView imageView10, imageView6;;
    String res;
    String comment="";
    LatLng latLng =null;
    FloatingActionButton cmnt;
    private String Base_url = "https://envirinsta.firebaseio.com/";
    private Firebase fb_db,fb_db2;
    String title="",categ="",auth="";
    ArrayList<RowItem> rowItems;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView mylistview;

    Marker markerx;
    private GoogleMap mMap;
    ProgressDialog progressDialog;
    GPSTracker gps;
    LatLng mylatlang = new LatLng(0, 0);
    public String serverKey = "AIzaSyAjAjLy4ZKnku_DpOBDLoeULqfXbuyM6hw";
    public Double curlat, curlong;

    Route route;
    String location="";
    Leg leg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussions_image);
        mylistview = (RecyclerView) findViewById(R.id.myListView);
        mylistview.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(Dis_image.this);
        mylistview.setLayoutManager(layoutManager);
        mylistview.setItemAnimator(new DefaultItemAnimator());
        Firebase.setAndroidContext(Dis_image.this);
        fb_db=new Firebase(Base_url);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView15 = (TextView) findViewById(R.id.textView15);
        textView16 = (TextView) findViewById(R.id.textView16);
        textView17 = (TextView) findViewById(R.id.textView17);

        editText2 = (EditText) findViewById(R.id.editText2);

        imageView10 = (ImageView) findViewById(R.id.imageView10);
        imageView6 = (ImageView) findViewById(R.id.imageView6);
        cmnt=(FloatingActionButton)findViewById(R.id.fab);
//        getActionBar().setDisplayHomeAsUpEnabled(true);


        res=this.getIntent().getExtras().get("res").toString();
        title=this.getIntent().getExtras().get("title").toString();
        categ=this.getIntent().getExtras().get("categ").toString();
        auth=this.getIntent().getExtras().get("auth").toString();

        textView17.setText(title);
        textView16.setText(categ);
        textView15.setText(auth);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        new MyTask().execute();
        new MyTask2().execute();

        cmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment=editText2.getText().toString();
                new MyTask3().execute();
            }
        });
    }

    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gps = new GPSTracker(Dis_image.this);
        if (gps.canGetLocation()) {

            curlat = gps.getLatitude();
            curlong = gps.getLongitude();
            mylatlang = new LatLng(curlat, curlong);
            System.out.println("Your location is " + mylatlang);
        }
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        // Add a marker in Sydney and move the camera
        //    LatLng TutorialsPoint = new LatLng(21, 57);
        markerx = mMap.addMarker(new MarkerOptions().position(mylatlang).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylatlang));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatlang, 12.0f));
    }

    private class MyTask extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressDialog = ProgressDialog.show(Sign_Up.this, "Message", "Creating Account...");

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
           runOnUiThread(new Runnable() {
                public void run() {
                    progressDialog = new ProgressDialog(Dis_image.this);
                    progressDialog.setTitle("Message");
                    progressDialog.setMessage("Downloading Image...");

                    progressDialog.setCancelable(true);
                    progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    progressDialog.show();
                }
            });



                System.out.println("Downloading" + res);

                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Classes").child(CurrentUser.sclass).child(CurrentUser.ssec).child("Images").child(res);

                System.out.println("Storage refference : " + storageReference);


                ImgUri.ref=storageReference;

            runOnUiThread(new Runnable() {
                public void run() {
                    Glide.with(Dis_image.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(imageView6);


                    storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            // Metadata now contains the metadata for 'images/forest.jpg'
                            try {
                                Double lat = Double.parseDouble(storageMetadata.getCustomMetadata("latitude"));
                                Double lon = Double.parseDouble(storageMetadata.getCustomMetadata("longitude"));

                                latLng = new LatLng(lat, lon);
                                new MyTaskMap().execute();
                            }
                            catch(Exception e)
                            {

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception)   {
                            // Uh-oh, an error occurred!
                        }
                    });
                    progressDialog.dismiss();

                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    imageView6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                Intent i = new Intent(Dis_image.this, imgdisp.class);

                startActivity(i);
                        }
                    });
                }
                });




            return "SUCCESS";
        }


        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Do things like hide the progress bar or change a TextView
        }
    }

    private class MyTask2 extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressDialog = ProgressDialog.show(Sign_Up.this, "Message", "Creating Account...");

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            rowItems=new ArrayList<>();
            fb_db2=new Firebase(Base_url+"Discussions/"+CurrentUser.sclass+"/"+CurrentUser.ssec+"/Images/"+res);
            fb_db2.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    Img_com images = dataSnapshot.getValue(Img_com.class);
                    String date = images.date;
                    String user=images.user;
                    String comment=images.comment;
                    RowItem item = new RowItem(user,
                            R.drawable.doc, comment,
                            date, "","Comment");
                    rowItems.add(item);

                    CustomAdapterCon adapter=new CustomAdapterCon(Dis_image.this,rowItems);
                    mylistview.setAdapter(adapter);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            return "SUCCESS";
        }


        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Do things like hide the progress bar or change a TextView
        }
    }

    private class MyTask3 extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressDialog = ProgressDialog.show(Sign_Up.this, "Message", "Creating Account...");

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            String date=new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date());
            Img_com img = new Img_com(CurrentUser.user,date,comment);

            String imagenode = CurrentUser.user + date;

            fb_db.child("Discussions").child(CurrentUser.sclass).child(CurrentUser.ssec).child("Images").child(res).child("Comm_"+imagenode).setValue(img);

            return "SUCCESS";
        }


        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Do things like hide the progress bar or change a TextView
        }
    }
    private class MyTaskMap extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Dis_image.this, "Message", "Fetching Details...");

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            //  Accounts doc=new Accounts(date,fullname,image);


            runOnUiThread(new Runnable() {
                public void run() {



                    Boolean flag = true;

                    if (flag) {



                        progressDialog.dismiss();

                        // markerx.remove();
                        mMap.addMarker(new
                                MarkerOptions().position(latLng).title("NGO"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                        GoogleDirection.withServerKey(serverKey)
                                .from(latLng)
                                .to(mylatlang)
                                .execute(new DirectionCallback() {
                                    @Override
                                    public void onDirectionSuccess(Direction direction, String rawBody) {
                                        // Do something here
                                        System.out.println("Result isss" + direction.getStatus());
                                        String status = direction.getStatus();
                                        if (status.equals(RequestResult.OK)) {
                                            System.out.println("Result is ok");
                                            try {
                                                route = direction.getRouteList().get(0);
                                                leg = route.getLegList().get(0);
                                                Info distanceInfo = leg.getDistance();
                                                Info durationInfo = leg.getDuration();
                                                String tmp1 = distanceInfo.getText();
                                                tmp1 = tmp1.substring(0, tmp1.length() - 3);
                                                String tmp2 = durationInfo.getText();


                                            } catch (Exception e) {
                                                System.out.println("Exiting:" + e);
                                            }

                                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();


                                            PolygonOptions rectOptions = new PolygonOptions();

                                            System.out.println("Lol size is" + directionPositionList.size());
                                            for (int i = 0; i < directionPositionList.size(); i = i + 25) {

                                                try {
                                                    LatLng first = directionPositionList.get(i);
                                                    LatLng second = directionPositionList.get(i + 25);
                                                    Polyline line1 = mMap.addPolyline(new PolylineOptions()
                                                            .add(first, second)
                                                            .width(5)
                                                            .color(Color.RED));
                                                    System.out.println(first + "," + second);
                                                } catch (Exception e) {

                                                }
                                            }
                                        }
                                        if (status.equals(RequestResult.NOT_FOUND)) {
                                            System.out.println("Result is not ok");
                                        }


                                    }

                                    @Override
                                    public void onDirectionFailure(Throwable t) {
                                        // Do something here
                                        System.out.println("Direction failed");
                                    }
                                });


                    } else {
                        progressDialog.dismiss();

                    }

                }
            });



            return "SUCCESS";
        }


        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("SUCCESS")) {

            }
            // Do things like hide the progress bar or change a TextView
        }
    }

    public List<Address> getAddress(String location) {
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return addressList;
    }
}
