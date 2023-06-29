package com.tseopela.destination;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    //creating the controls
    private Button btnSettings,btnLocationAct,btnFavourites;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //linking the controls
        btnFavourites = findViewById(R.id.btnFavourites);
        btnLocationAct = findViewById(R.id.btnLocationAct);
        btnSettings = findViewById(R.id.btnSettings);


        if(isServicesOk()){
            //onclick-listener: navigating to the location activity
            btnLocationAct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open location activity                --current class    --navigating to class
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }
            });
        }

        //onclick-listener: navigating to the favourites activity
        btnFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register activity                --current class    --navigating to class
                startActivity(new Intent(MainActivity.this, FavouritesActivity.class));
            }
        });

        //onclick-listener: navigating to the settings activity
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register activity                --current class    --navigating to class
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

    }
        //method for checking if the
        public boolean isServicesOk() {
            Log.d(TAG, "isServiceOk: checking google services version");
            int available = GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(MainActivity.this);

            if (available == ConnectionResult.SUCCESS) {
                //everything is okay - user can make requests
                Log.d(TAG, "isServicesOk: Google Play Services is working");
                return true;
            } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                //error occured but we can fix it
                Log.d(TAG, "isServicesOk: Error occured but working on a solution...");
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,
                        available, ERROR_DIALOG_REQUEST);
                dialog.show();
            } else {
                Toast.makeText(this, "You can't make Map Requests", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

   }