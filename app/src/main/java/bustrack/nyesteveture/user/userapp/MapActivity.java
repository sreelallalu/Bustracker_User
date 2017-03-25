package bustrack.nyesteveture.user.userapp;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
     GoogleApiClient mGoogleApiClient;
    TextView placec,del,kmte;
    String lat,log,dist,delsy;
    String tripcode,vehicleno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i=getIntent();
        setContentView(R.layout.mapframe);

        tripcode=i.getStringExtra("tripcode");
        vehicleno=i.getStringExtra("busid");
        lat=i.getStringExtra("lati");
        log=i.getStringExtra("logi");
        dist=i.getStringExtra("dis");
        delsy=i.getStringExtra("delay");

      //  placec=(TextView)findViewById(R.id.textplace);
        del=(TextView)findViewById(R.id.textdelay);
        kmte=(TextView)findViewById(R.id.textViewkm);


       // placec.setText(i.getStringExtra(""));
        del.setText(i.getStringExtra(""));
       // placec.setText(i.getStringExtra(""));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        mTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 10 * 1000);

    }
    private Timer mTimer;
    TimerTask timerTask=new TimerTask() {
        @Override
        public void run() {

         CallM();


        }
    };

    private void CallM() {
        HashMap<String,String> hashmap=new HashMap<String, String>();
        hashmap.put("tag","viewposition");
        hashmap.put("tripCode",tripcode);
        hashmap.put("vehicleNo",vehicleno);

        PostResponseAsyncTask task=new PostResponseAsyncTask(MapActivity.this, hashmap,false, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject js=new JSONObject(s);

                    int suc=js.getInt("success");
                    if(suc==1)
                    {
                        String position=js.getString("position");
                        JSONObject gh=new JSONObject(position);
                        String lati=gh.getString("latitude");
                        String log=gh.getString("longitude");
                        String dis=gh.getString("distance");
                        String delay=gh.getString("delay");
                        Freshmm(lati,log,dis,delay);


                    }
                    else {
                        Toast.makeText(MapActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        task.execute("http://bustracking.nyesteventuretech.com/Service/index.php");
        task.setEachExceptionsHandler(new EachExceptionsHandler() {
            @Override
            public void handleIOException(IOException e) {

            }

            @Override
            public void handleMalformedURLException(MalformedURLException e) {

            }

            @Override
            public void handleProtocolException(ProtocolException e) {

            }

            @Override
            public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {

            }
        });


    }

    private void Freshmm(String lat, String log, String dis, String delay) {

        MarkerOptions options = new MarkerOptions();


        double v1=Double.parseDouble(lat);
        double d2=Double.parseDouble(log);

        LatLng latLng = new LatLng(v1, d2);

        options.position(latLng);

        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());

        try {
            List<Address> addresses   = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
//            Log.e("cityname",cityName);
  //          Log.e("stateName",stateName);
    //        Log.e("countryName",countryName);
           // placec.setText(cityName);
            kmte.setText(dis);
            del.setText(delay);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mark_Position(latLng);



    }

    private void Mark_Position(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude,latLng.longitude) , 14.0f) );
        Marker melbourne = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.favic)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

            }

            Freshmm(lat,log,dist,delsy);




        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

 final LatLng MELBOURNE = new LatLng(-37.813, 144.962);

        /*Marker melbourne = mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));*/
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("connection error",connectionResult.toString());

    }
}
