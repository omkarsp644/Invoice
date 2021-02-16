package com.svttechnology.invoice.activities.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.svttechnology.invoice.R;
import com.svttechnology.invoice.activities.ClasseFiles.InventoryData;

import static com.svttechnology.invoice.activities.Activities.Scan.cartId;

public class ShowInventory extends AppCompatActivity {

    LinearLayout showTemp;
    TextView name,id,cost,available;
    ImageView delete,edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_inventory);
        showTemp=(LinearLayout)findViewById(R.id.ShowTempData);





       int count=0;

       for(InventoryData data:cartId){
            if (count>=5){
                break;
            }
           View v= View.inflate(this,R.layout.showinvent,null);
           id=v.findViewById(R.id.productId);
           name=v.findViewById(R.id.productName);
           cost=v.findViewById(R.id.productCost);
           available=v.findViewById(R.id.productAvail);

           delete=v.findViewById(R.id.productDelete);
           edit=v.findViewById(R.id.productEdit);
           id.setText(data.getId());
           name.setText(data.getName());
           cost.setText(data.getCost());
           available.setText(Integer.toString(data.getCount()));
           delete.setEnabled(false);
           edit.setEnabled(false);
            count++;
           showTemp.addView(v);
       }


    }
}