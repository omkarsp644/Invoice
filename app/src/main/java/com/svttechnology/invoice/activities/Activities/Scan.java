package com.svttechnology.invoice.activities.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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
import com.svttechnology.invoice.activities.Adapters.ShowBasketScan;
import com.svttechnology.invoice.activities.ClasseFiles.InventoryData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Scan extends AppCompatActivity implements View.OnClickListener {

    CompoundBarcodeView barcode;
    TextInputEditText id;
    DatabaseReference inventory;
    public static ArrayList<Integer> countItems=new ArrayList<>();
    public static ArrayList<InventoryData> cartId=new ArrayList<>();
    public  static  ArrayList<String> CartItemId=new ArrayList<>();
    ShowBasketScan adaper;
    ListView transactions;
    String Product_id;
    Button billing,reset;
    EditText cost,name;
    TextView TotalAmount,TaxPer,TaxAmount,PayableAmount;
    CardView with,without;
    ImageView close;
    AlertDialog dialog;
    CardView addDataH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        barcode=(CompoundBarcodeView)findViewById(R.id.compoundBarcode);
        barcode.setCameraSettings(new CameraSettings());
        barcode.decodeContinuous(callback);
        barcode.setStatusText("Click here to Scan");

        id=(TextInputEditText)findViewById(R.id.qrCodeId);

        inventory=FirebaseDatabase.getInstance().getReference().child("Data").child("Inventory");


        billing=(Button)findViewById(R.id.Billing);
        reset=(Button)findViewById(R.id.Reset);
        addDataH=(CardView)findViewById(R.id.AddDa);
        addDataH.setOnClickListener(this);
        billing.setOnClickListener(this);
        reset.setOnClickListener(this);
        transactions=(ListView) findViewById(R.id.showtrans);

        adaper=new ShowBasketScan(Scan.this);
        transactions.setAdapter(adaper);




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

           id.setText(result.getText());
           barcode.pause();

           InsertById(result.getText());

       }
   };

    private void InsertById(String text) {

            inventory.child(text).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        InsertInTheList(snapshot.getValue(InventoryData.class), text);
                    } else {
                        startAddingData(text);
                    }
                    id.getText().clear();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void startAddingData(String result) {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View v=View.inflate(this,R.layout.view_adddata,null);
        name = (EditText) v.findViewById(R.id.nameview);
        cost = (EditText) v.findViewById(R.id.costview);
        CardView submit=(CardView)v.findViewById(R.id.det);
        builder.setView(v);
        builder.setCancelable(true);
        submit.setOnClickListener(this);
        Product_id=result;

        dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert));
        dialog.show();


        adaper.notifyDataSetChanged();
    }

    private void InsertInTheList(InventoryData i,String id) {


        if (CartItemId.contains(id)){
            int in=CartItemId.indexOf(id);
            int ans=countItems.get(in)+1;
            countItems.set(in,ans);
        }
        else{
            countItems.add(1);
            cartId.add(i);
            CartItemId.add(id);
        }

        adaper.notifyDataSetChanged();


    }

    public void Start(View view) {
        barcode.resume();
    }

    public void StartHome(View view) {
        startActivity(new Intent(Scan.this,Home.class));
    }


    public void resetValues() {

        countItems.clear();
        cartId.clear();
        CartItemId.clear();
        adaper.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch ((v.getId())){
            case R.id.Billing:
                Toast.makeText(getApplicationContext(),"Billing...",Toast.LENGTH_SHORT).show();
                showAlertDialog();
            break;
            case R.id.Reset:
            Toast.makeText(getApplicationContext(),"Resetting ...",Toast.LENGTH_SHORT).show();
            resetValues();
            break;
            case R.id.details_with:
                    startActivity(new Intent(Scan.this,GenerateInvoice.class));
                break;
            case R.id.close:
                dialog.dismiss();
                break;

            case R.id.det:
                if (Integer.parseInt(cost.getText().toString()) > 0) {
                    InventoryData i = new InventoryData(Product_id, name.getText().toString(), cost.getText().toString());
                    inventory.child(Product_id).setValue(i).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Inserted Successfully...",Toast.LENGTH_SHORT).show();
                            InsertInTheList(i,Product_id);
                            dialog.dismiss();
                        }
                    });
                }
                break;

            case R.id.AddDa:
                Toast.makeText(getApplicationContext(),"Add...",Toast.LENGTH_SHORT).show();
                if (!id.getText().toString().isEmpty())
                    InsertById(id.getText().toString());
                else{
                    Toast.makeText(getApplicationContext(),"Insert Value ...",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }


    @SuppressLint("ResourceAsColor")
    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View v=View.inflate(this,R.layout.view_show_detailed_invoice,null);
        TotalAmount=v.findViewById(R.id.Amountdetails);
        TaxPer=v.findViewById(R.id.TaxPerdetails);
        TaxAmount=v.findViewById(R.id.TaxAmountdetails);
        PayableAmount=v.findViewById(R.id.TotalPayAmountdetails);
        with=v.findViewById(R.id.details_with);
        without=v.findViewById(R.id.details_withount);
        close=v.findViewById(R.id.close);
        with.setOnClickListener(this);
        without.setOnClickListener(this);
        close.setOnClickListener(this);



        int totalPrice=0;
        int siz=countItems.size();
        for (int i = 0; i < siz; i++) {

            int mul=countItems.get(i)*Integer.parseInt(cartId.get(i).getCost());
            totalPrice=totalPrice+mul;

        }


        double tax=0.18;
        TaxPer.setText("0.18 %");
        int taxAmt=(int)(tax*totalPrice);
        TaxAmount.setText(Integer.toString(taxAmt));
        TotalAmount.setText(Integer.toString(totalPrice));
        PayableAmount.setText(Integer.toString(totalPrice+taxAmt));





        builder.setView(v);
        builder.setCancelable(true);

        dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert));

        dialog.show();

    }
}