package com.tseopela.destination;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tseopela.destination.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //variables
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    Boolean unitofmeasure = true;

    //const
    private ActivityMapsBinding binding;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 18f;

    //widgets
    private EditText edSearchText;
    private FloatingActionButton fabGPS, fabLandmark, fabInfo, fabDirection, fabDuration;
    private ImageButton ibHospital, ibATM, ibRestaurant, ibMuseum;

    //
    int PROXIMITY_RADIUS = 10000;
    private double lat, lng;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);

    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);


    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);


    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //attaching the controls
        edSearchText = findViewById(R.id.edSearch);
        fabGPS = findViewById(R.id.fabGPS);

        fabDirection = findViewById(R.id.fabDirections);
        fabDuration = findViewById(R.id.fabDuration);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ibATM = findViewById(R.id.atm);
        ibHospital = findViewById(R.id.hospital);
        ibMuseum = findViewById(R.id.museum);
        ibRestaurant = findViewById(R.id.restaurant);


//        Places.initialize(getApplicationContext(),"AIzaSyC-E5xMVmU8mkVyS3fjvh-L5frlpp5cx50");
//        PlacesClient placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocPermission();



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-26.258570, 28.013510);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Johannesburg"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (mLocationPermissionsGranted) {
            getLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }

    }


//
//    private void getroute()
//    {
//        LatLng home = new LatLng(-26.258609771728516, 28.013324737548828);
//        LatLng hosTest = new LatLng(-26.098970,27.781740);
//        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(new LatLng(-26.258609771728516, 28.013324737548828),
//                      new LatLng(-26.098970,27.781740)));
//        polyline1.setTag("A");
//        stylePolyline(polyline1);
//
//        Polyline polyline2 = mMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(new LatLng(-26.258609771728516, 28.013324737548828),
//                        new LatLng(-26.098970,27.781740)));
//        polyline2.setTag("B");
//        stylePolyline(polyline2);
//
//        Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
//                .clickable(true)
//                .add(new LatLng(-26.258609771728516, 28.013324737548828),
//                        new LatLng(-26.098970,27.781740)));
//
//        polygon1.setTag("alpha");
//        // Style the polygon.
//        stylePolygon(polygon1);
//
//        Polygon polygon2 = mMap.addPolygon(new PolygonOptions()
//                .clickable(true)
//                .add(new LatLng(-26.258609771728516, 28.013324737548828),
//                        new LatLng(-26.098970,27.781740)));
//
//        polygon1.setTag("beta");
//        // Style the polygon.
//        stylePolygon(polygon2);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-26.098970,27.781740), DEFAULT_ZOOM));
//        mMap.setOnPolylineClickListener((GoogleMap.OnPolylineClickListener) this);
//        mMap.setOnPolygonClickListener((GoogleMap.OnPolygonClickListener) this);
//
//    }
//
//    private void stylePolyline(Polyline polyline)
//    {
//        String type = "";
//        // Get the data object stored with the polyline.
//        if (polyline.getTag() != null) {
//            type = polyline.getTag().toString();
//        }
//
//        switch (type) {
//            // If no type is given, allow the API to use the default.
//            case "A":
//                // Use a custom bitmap as the cap at the start of the line.
//                polyline.setStartCap(
//                        new CustomCap(
//                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
//                break;
//            case "B":
//                // Use a round cap at the start of the line.
//                polyline.setStartCap(new RoundCap());
//                break;
//        }
//
//        polyline.setEndCap(new RoundCap());
//        polyline.setJointType(JointType.ROUND);
//
//    }
//
//
//
//    @Override
//    public void onPolylineClick(Polyline polyline) {
//        // Flip from solid stroke to dotted stroke pattern.
//        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
//            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
//        } else {
//            // The default pattern is a solid stroke.
//            polyline.setPattern(null);
//        }
//
//        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
//                Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * Listens for clicks on a polygon.
//     */
//
//    /**
//     * Listens for clicks on a polygon.
//     * @param polygon The polygon object that the user has clicked.
//     */
//
//    @Override
//    public void onPolygonClick(Polygon polygon) {
//        // Flip the values of the red, green, and blue components of the polygon's color.
//        int color = polygon.getStrokeColor() ^ 0x00ffffff;
//        polygon.setStrokeColor(color);
//        color = polygon.getFillColor() ^ 0x00ffffff;
//        polygon.setFillColor(color);
//
//        Toast.makeText(this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
//    }
//
//
//
//
//    private void stylePolygon(Polygon polygon)
//    {
//        String type = "";
//        // Get the data object stored with the polygon.
//        if (polygon.getTag() != null) {
//            type = polygon.getTag().toString();
//            List<PatternItem> pattern = null;
//
//            switch (type) {
//                // If no type is given, allow the API to use the default.
//                case "alpha":
//                    // Apply a stroke pattern to render a dashed line, and define colors.
//                    pattern = PATTERN_POLYGON_ALPHA;
//                    break;
//                case "beta":
//                    // Apply a stroke pattern to render a line of dots and dashes, and define colors.
//                    pattern = PATTERN_POLYGON_BETA;
//                    break;
//            }
//            polygon.setStrokePattern(pattern);
//
//        }
//    }

    private void getDirection()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json/")
                .buildUpon()
                .appendQueryParameter("destination","-26.098970, 8.929490")
                .appendQueryParameter("origin","-26.258570, 28.013510")
                .appendQueryParameter("mode","driving")
                .appendQueryParameter("key","AIzaSyC-E5xMVmU8mkVyS3fjvh-L5frlpp5cx50")
                .toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String status = response.getString("status");
                    if(status.equals("OK")){
                        JSONArray routes = response.getJSONArray("routes");

                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for(int i=0; i<routes.length(); i++) {
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
                            //dur = response.getJSONArray("routes").getJSONObject(i).getJSONArray("legs").getJSONObject(i).getJSONObject("duration").getString("text");

                            for (int j = 0; j < legs.length(); j++) {
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                                for (int k = 0; k < steps.length(); k++){
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    List<LatLng> list = decodePoly(polyline);

                                    for (int l=0; l<list.size();l++){
                                        LatLng position = new LatLng(-26.258570,28.013510);
                                        points.add(position);
                                    }

                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(10);
                            polylineOptions.color(ContextCompat.getColor(MapsActivity.this, R.color.purple_500));
                            polylineOptions.geodesic(true);

                        }
                        mMap.addPolyline(polylineOptions);

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(lat, lng))
                                .include(new LatLng(-26.098970, 27.781740)).build();
                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150,30));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000 ,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    private List<LatLng> decodePoly(String encoded){
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while(index < len){
            int b, shift = 0, result =0;
            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while(b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~ (result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while(b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng /1E5)));
            poly.add(p);
        }

        return poly;
    }


    private void init(){
        Log.d(TAG,"initialising");
        edSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if(actionID == EditorInfo.IME_ACTION_SEARCH
                        || actionID == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute method for searching
                    geoLocate();
                }
                return false;
            }
        });

        fabDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              getDirection();
            }
        });

        fabGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "Getting current location",
                        Toast.LENGTH_SHORT).show();
                getLocation();
            }
        });

        ibATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mMap.clear();
               String atm = "atm";
               String url = getURL(lat,lng,atm);
               Object dataTransfer[] = new Object[2];
               dataTransfer[0] = mMap;
               dataTransfer[1] = url;

               FetchData fetchData = new FetchData();
               fetchData.execute(dataTransfer);
                LatLng latLng = new LatLng(-26.08549922603839, 27.947220961956152);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Summerfields Standard Bank ATM"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Toast.makeText(MapsActivity.this, "Nearby ATMs",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ibRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                String restaurant = "restaurant";
                String url = getURL(lat,lng,restaurant);
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataTransfer);
                LatLng latLng = new LatLng(-26.09344441494034, 27.940295202094397);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Mcdonalds"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Toast.makeText(MapsActivity.this, "Nearby Restaurants",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ibMuseum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                String museum = "museum";
                String url = getURL(lat,lng,museum);
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                LatLng userloc;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataTransfer);

//                Log.d(TAG,"getLocation: getting the user locations");
//                try{
//                    if(mLocationPermissionsGranted){
//                        final Task location = mFusedLocationProviderClient.getLastLocation();
//                        location.addOnCompleteListener(new OnCompleteListener() {
//                            @Override
//                            public void onComplete(@NonNull Task task) {
//                                if(task.isSuccessful()){
//                                    Location currentLoc = (Location) task.getResult();
//                                    String musemReq = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?rankby=distance&location="
//                                            + currentLoc.toString()+"&types=";
//
//
//
//
//                                }else{
//                                    Toast.makeText(MapsActivity.this, "Unable to find location",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                }catch(SecurityException e){
//                    Log.d(TAG, "SecurityException: " + e.getMessage());
//                }

                LatLng latLng = new LatLng(-26.2375375,28.0084696);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Apartheid Museum"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Toast.makeText(MapsActivity.this, "Nearby Museums",
                        Toast.LENGTH_SHORT).show();

            }
        });

        ibHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                String hospital = "hospital";
                String url = getURL(lat,lng,hospital);
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataTransfer);
                LatLng latLng = new LatLng(-26.05633844127066, 27.971448039223848);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Netcare Olivedale Hospital"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                Toast.makeText(MapsActivity.this, "Nearby Hospitals",
                        Toast.LENGTH_SHORT).show();
            }
        });

        fabDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(unitofmeasure)
                {
                    unitofmeasure = false;
                    Toast.makeText(MapsActivity.this, "Unit of measurement set to Miles",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    unitofmeasure = true;
                    Toast.makeText(MapsActivity.this, "Unit of measurement set to Kilometers",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        hideKeyBoard();
    }

    private String getURL(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googlePlaceURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceURL.append("location"+latitude+","+longitude);
        googlePlaceURL.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceURL.append("&type="+nearbyPlace);
        googlePlaceURL.append("&sensor=true");
        googlePlaceURL.append("&key=AIzaSyC-E5xMVmU8mkVyS3fjvh-L5frlpp5cx50");
        return googlePlaceURL.toString();
    }

    //method for searching for location
    private void geoLocate()
    {
        String searchString = edSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list =  geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.d(TAG, "IOException: " + e.getMessage());
        }

        //if address is found
        if(list.size() > 0){
            Address address = list.get(0);
            Toast.makeText(this,"Address found", Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM
                    ,address.getAddressLine(0));
            LatLng dest = new LatLng(address.getLatitude(), address.getLongitude());
            //unitofmeasure =true;

            //true returns Kms, false returns miles
            CreateRoute(dest, unitofmeasure);
        }
    }

    //method to get and display the user's current location
    private void getLocation()
    {
        Log.d(TAG,"getLocation: getting the user locations");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
           if(mLocationPermissionsGranted){
               final Task location = mFusedLocationProviderClient.getLastLocation();
               location.addOnCompleteListener(new OnCompleteListener() {
                   @Override
                   public void onComplete(@NonNull Task task) {
                       if(task.isSuccessful()){
                           Toast.makeText(MapsActivity.this, "Location found",
                                   Toast.LENGTH_SHORT).show();
                           Location currentLoc = (Location) task.getResult();
                           moveCamera(new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude()),
                                   DEFAULT_ZOOM,"Current Location");
                       }else{
                           Toast.makeText(MapsActivity.this, "Unable to find location",
                                   Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }
        }catch(SecurityException e){
            Log.d(TAG, "SecurityException: " + e.getMessage());
        }
    }



    //method for specifically moving camera - zoom in and out
    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if(!title.equals("Current Location")){
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
        hideKeyBoard();
    }

    //method to get permissions
    private void getLocPermission()
    {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    //getting the required permissions from the Manifest
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0){
                    if(grantResults.length > 0) {
                        for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                        mLocationPermissionsGranted = true;
                        //initialise map
                        onMapReady(mMap);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void hideKeyBoard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

//Code that Generates route to destination and display duration/distance:
    public void CreateRoute(LatLng UserDestination, Boolean unitofmeasure)
    {
        //creating a route
        //get user location:
        Log.i("LOC","here");
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //return ;
            }else{

                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();

                        if(location!=null && UserDestination!= null){

                            try {
                                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                                List<Address> addresses =
                                        geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                                LocationClass locClass = new
                                        LocationClass(addresses.get(0).getLatitude(),addresses.get(0).getLongitude(),addresses.get(0).getAddressLine(0));

                                LatLng currentLoc = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                                mMap.addMarker(new MarkerOptions().position(currentLoc).title(addresses.get(0).getAddressLine(0)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
                                // Getting URL to the Google Directions API
                                String url = getDirectionsUrl(currentLoc, UserDestination, unitofmeasure);


                                DownloadTask downloadTask = new DownloadTask();

                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Toast.makeText(MapsActivity.this, "Issue with location, one of the two is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });}

        } else {
            ActivityCompat.requestPermissions
                    (MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    //this code is based off of the code written by Anupam Chungh
    //Their guide is available at:
    //https://www.digitalocean.com/community/tutorials/android-google-map-drawing-route-two-points
    private String getDirectionsUrl(LatLng origin,LatLng dest, Boolean untOfMeasure){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters;
        if(untOfMeasure)
        {
            parameters = str_origin+"&"+str_dest+"&"+sensor+"&units=metric";
        }
        else
        {
            parameters = str_origin+"&"+str_dest+"&"+sensor+"&units=imperial";
        }


        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&key=AIzaSyD82myNhXSf_yafMrYN0H1sKsFJcal9x_M";

        return url;
    }

    //this code is based off of the code written by Anupam Chungh
    //Their guide is available at:
    //https://www.digitalocean.com/community/tutorials/android-google-map-drawing-route-two-points
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception with URL Dld", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //this code is based off of the code written by Anupam Chungh
    //Their guide is available at:
    //https://www.digitalocean.com/community/tutorials/android-google-map-drawing-route-two-points
    /** Fetches data from url passed */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    //this code is based off of the code written by Anupam Chungh
    //Their guide is available at:
    //https://www.digitalocean.com/community/tutorials/android-google-map-drawing-route-two-points
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }
            Toast.makeText(MapsActivity.this, "Distance:"+distance+" Duration:"+duration, Toast.LENGTH_LONG).show();



            //clearing all existing markers/polyline off of the map
            mMap.clear();
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);

        }
    }

}