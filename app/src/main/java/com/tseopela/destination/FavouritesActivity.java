package com.tseopela.destination;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {

    private EditText edLocation;
    private FloatingActionButton fabAdd;
    private AddressCollection location = new AddressCollection();
    private String orderedValue;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference destRef = database.getReference("location");
    ListView lstAdd;
    List<String> itemList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        itemList = new ArrayList<>();
        lstAdd = findViewById(R.id.lstAdd);
        edLocation = findViewById(R.id.edLocation);
        fabAdd = findViewById(R.id.fabAddLocation);
        orderedValue = getIntent().getStringExtra("location");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loc = edLocation.getText().toString();

                //checking if the user has filled all fields - name/cell/order date
                if(!TextUtils.isEmpty(loc))
                {
                    location.setLocation(loc);
                    destRef.push().setValue(location);
                    //if they haven't filled all the fields
                    Toast.makeText(FavouritesActivity.this,
                            "Location added...", Toast.LENGTH_SHORT).show();
                    itemList.clear();
                }else{
                    //if they haven't filled all the fields
                    Toast.makeText(FavouritesActivity.this,
                            "Please enter all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //linking to the realtime database
        destRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot pulledItem: snapshot.getChildren())
                {
                    AddressCollection order = pulledItem.getValue(AddressCollection.class);
                    itemList.add(order.toString());
                }
                //creating an adapter to display the data
                arrayAdapter = new ArrayAdapter<String>(FavouritesActivity.this,
                        android.R.layout.simple_list_item_1,itemList);
                lstAdd.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavouritesActivity.this, "Error loading",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}