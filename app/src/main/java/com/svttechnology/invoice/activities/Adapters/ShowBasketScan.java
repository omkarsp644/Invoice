package com.svttechnology.invoice.activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.svttechnology.invoice.R;
import com.svttechnology.invoice.activities.ClasseFiles.InventoryData;
import androidx.appcompat.app.AppCompatActivity;
import static com.svttechnology.invoice.activities.Activities.Scan.cartId;
import static com.svttechnology.invoice.activities.Activities.Scan.countItems;

public class ShowBasketScan extends BaseAdapter {

    private AppCompatActivity activity;


    public ShowBasketScan(AppCompatActivity context) {
        this.activity = context;
    }

    @Override
    public int getCount() {
        return cartId.size();
    }

    @Override
    public InventoryData getItem(int position) {
        return cartId.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_showbucket, null, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.index.setText(Integer.toString(position+1));
        viewHolder.Name.setText(cartId.get(position).getName()!=null?cartId.get(position).getName():"No Data");
        viewHolder.Count.setText(Integer.toString(countItems.get(position)));
        int mul=countItems.get(position)*Integer.parseInt(cartId.get(position).getCost());
        viewHolder.Total.setText(Integer.toString(mul));
        viewHolder.Price.setText(cartId.get(position).getCost());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int data = Integer.parseInt(viewHolder.Count.getText().toString()) - 1;
                if (data <= 0) {
                    countItems.remove(position);
                    cartId.remove(position);
                    notifyDataSetChanged();
                } else {
                    viewHolder.Count.setText(Integer.toString(data));
                    countItems.set(position,data);
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                countItems.remove(position);
                cartId.remove(position);
                notifyDataSetChanged();
                return true;
            }
        });


        return convertView;
    }


    private static class ViewHolder {
        private TextView index;
        private TextView Name;
        private TextView Count;
        private TextView Price;
        private TextView Total;
        private ImageView delete;



        public ViewHolder(View v) {
            index = (TextView) v.findViewById(R.id.adapter_index);
            Name = (TextView) v.findViewById(R.id.adapter_ProductName);
            Count = (TextView) v.findViewById(R.id.adapter_count);
            Price = (TextView) v.findViewById(R.id.adapter_price);
            Total = (TextView) v.findViewById(R.id.adapter_totalPrice);

            delete=(ImageView)v.findViewById(R.id.deleteRecord);


        }
    }
}
