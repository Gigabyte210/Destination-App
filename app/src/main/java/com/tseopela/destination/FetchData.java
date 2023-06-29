package com.tseopela.destination;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class FetchData extends AsyncTask<Object,String,String>
{
    String googleNearByPlacesData;
    GoogleMap googleMap;
    String url;

    @Override
    protected String doInBackground(Object... objects) {
        try{
            googleMap =(GoogleMap) objects[0];
            url = (String) objects[1];
            DownloadURL downloadURL = new DownloadURL();
            googleNearByPlacesData = downloadURL.retrieveURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleNearByPlacesData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        List<HashMap<String,String>> nearbyPlaceList = null;
        JsonParser parser = new JsonParser();
        nearbyPlaceList = parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);



//        try{
//            JSONObject jsonObject = new JSONObject(s);
//            JSONArray jsonArray = jsonObject.getJSONArray("results");
//
//            for(int i =0;i<jsonArray.length();i++){
//                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                JSONObject getLocation = jsonObject1
//                        .getJSONObject("geometry")
//                        .getJSONObject("location");
//                String lat = getLocation.getString("lat");
//                String lng = getLocation.getString("lng");
//
//                JSONObject getName = jsonArray.getJSONObject(i);
//                String name = getName.getString("name");
//                LatLng latLng = new LatLng(Double.parseDouble(lat),
//                        Double.parseDouble(lng));
//
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.title("South Rand Hospital");
//                markerOptions.position(latLng);
//                googleMap.addMarker(markerOptions);
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
//
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        super.onPreExecute();
    }

    private void showNearbyPlaces(List<HashMap<String,String>> nearbyPlaceList)
    {
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlace = nearbyPlaceList.get(i);
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + vicinity);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
    }

}
