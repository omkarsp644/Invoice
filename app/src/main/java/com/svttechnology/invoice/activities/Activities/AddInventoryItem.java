package com.svttechnology.invoice.activities.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.svttechnology.invoice.R;
import com.svttechnology.invoice.activities.ClasseFiles.InventoryData;

public class AddInventoryItem extends AppCompatActivity {

    EditText id,name,cost;

    CompoundBarcodeView barcode;
    Button submit;
    DatabaseReference inventory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory_item);
        id=(EditText)findViewById(R.id.id1);
        name=(EditText)findViewById(R.id.name1);
        cost=(EditText)findViewById(R.id.cost1);

        barcode=(CompoundBarcodeView)findViewById(R.id.compoundBarcode);
        barcode.setCameraSettings(new CameraSettings());
        barcode.decodeContinuous(callback);
        barcode.setStatusText("Click here to Scan");
        submit=(Button)findViewById(R.id.Sumit);
        inventory=FirebaseDatabase.getInstance().getReference().child("Data").child("Inventory");



        submit.setOnClickListener(v -> {

            String i= String.valueOf(id.getText());
            String na=name.getText().toString();
            String Cost=cost.getText().toString();
            if (i.length()!=12|| na.isEmpty() || Cost.isEmpty()){
                Toast.makeText(getApplicationContext(),"Barcode Must be 12 digit..",Toast.LENGTH_SHORT).show();
            }
            else{
                DatabaseReference def=FirebaseDatabase.getInstance().getReference().child("Data").child("Inventory");
                InventoryData d=new InventoryData(i,na,Cost);
                def.child(i).setValue(d).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        id.getText().clear();
                        name.getText().clear();
                        cost.getText().clear();
                        barcode.resume();
                        Toast.makeText(getApplicationContext(),"Data Added...",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        barcode.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcode.pause();
        super.onPause();
    }

    private BarcodeCallback callback=new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            Toast.makeText(getApplication(),"Id :"+result.getText(),Toast.LENGTH_LONG).
                    show();
            id.setText(result.getText());

            inventory.child(result.getText()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        InventoryData dat=snapshot.getValue(InventoryData.class);
                        name.setText(dat.getName());
                        name.setEnabled(false);
                        cost.setText(dat.getCost());
                        cost.setEnabled(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            barcode.pause();
        }
    };

    public void Start1(View view) {
        barcode.resume();
        name.setEnabled(true);
        cost.setEnabled(true);

        name.getText().clear();
        cost.getText().clear();
    }


    public void GoBack(View view) {
        onBackPressed();
    }
}