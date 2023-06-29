//package com.tseopela.destination;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.Spinner;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.gson.JsonParser;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.List;
//
//
//public class Landmarks extends AppCompatActivity {
//
//    private Spinner spType;
//    private Button btnFind;
//    SupportMapFragment supportMapFragment;
//    GoogleMap mMap;
//    private FusedLocationProviderClient mFusedLocationProviderClient;
//    double currenLat = 0, currentLong = 0;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_landmarks);
//
//
//        spType = findViewById(R.id.sp_type);
//        btnFind = findViewById(R.id.btnFind);
//        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.google_map);
//
//        String[] placeTypeList = {"atm", "bank", "hospital", "restaurant"};
//        String[] placeNameList = {"ATM", "Bank", "Hospital", "Restaurant"};
//
//        //setting adapter on spinner
//        spType.setAdapter(new ArrayAdapter<>(Landmarks.this,
//                android.R.layout.simple_spinner_dropdown_item, placeNameList));
//
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this
//        );
//
//        if (ActivityCompat.checkSelfPermission(Landmarks.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            getCurrentLocation();
//        }else{
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
//        }
//
//        btnFind.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               int i = spType.getSelectedItemPosition();
//               String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
//                       "?location=" + currenLat + "," + currentLong + "&radius=5000"
//                      + "&types=" + placeTypeList[i] +
//                        "&sensor=true" +
//                        "&key=" + getResources().getString(R.string.google_map_api_key);
//
//               new PlaceTask().execute(url);
//            }
//        });
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode == 44){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                getCurrentLocation();
//            }
//        }
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//    }
//
//    private void getCurrentLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.
//                        permission.ACCESS_COARSE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED) {
//
//            Task<Location> task = mFusedLocationProviderClient.getLastLocation();
//            task.addOnSuccessListener(new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if(location != null){
//                         currenLat = location.getLatitude();
//                         currentLong = location.getLongitude();
//                         supportMapFragment.getMapAsync(new OnMapReadyCallback() {
//                             @Override
//                             public void onMapReady(@NonNull GoogleMap googleMap) {
//                                 mMap = googleMap;
//                                 mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                                         new LatLng(currenLat,currentLong),15
//                                 ));
//                             }
//                         });
//                    }
//                }
//            });
//        }
//
//    }
//
//    private class PlaceTask extends AsyncTask<String,Integer,String>
//    {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String data = null;
//            try {
//                data = downloadUrl(strings[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            new ParserTask().execute(s);
//            super.onPostExecute(s);
//        }
//    }
//
//    private String downloadUrl(String string) throws IOException
//    {
//        URL url = new URL(string);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.connect();
//        InputStream stream = connection.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//        StringBuilder builder = new StringBuilder();
//        String line = "";
//        while((line = reader.readLine()) != null){
//            builder.append(line);
//        }
//        String data = builder.toString();
//        reader.close();
//        return data;
//    }
//
//    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>>
//    {
//        @Override
//        protected List<HashMap<String, String>> doInBackground(String... strings) {
//            JsonParser jsonParser = new JsonParser();
//            List<HashMap<String,String>> mapList = null;
//            JSONObject object = null;
//            try {
//                 object = new  JSONObject(strings[0]);
//                 mapList = jsonParser.parseResults(object);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return mapList;
//        }
//
//
//        @Override
//        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
//            mMap.clear();
//            for (int i = 0; i < hashMaps.size();i++){
//
//            }
//            super.onPostExecute(hashMaps);
//        }
//    }
//}