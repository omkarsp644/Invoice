package com.svttechnology.invoice.activities.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.pdf.PdfDocument.*;
import com.svttechnology.invoice.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import static com.svttechnology.invoice.activities.Activities.Scan.cartId;
import static com.svttechnology.invoice.activities.Activities.Scan.countItems;


public class GenerateInvoice extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout llPdf;

    private TableLayout stk;
    private CardView submit;
    private RelativeLayout rootContent;

    private TextView amount,totalAmount,taxPer,taxAmount;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_invoice);

        stk = (TableLayout) findViewById(R.id.tableInvoice);
        submit = (CardView) findViewById(R.id.Print);
        submit.setOnClickListener(this);
        rootContent = (RelativeLayout) findViewById(R.id.relativeCOntent);
        stk.setStretchAllColumns(true);

        amount = (TextView) findViewById(R.id.TotalAmount);
        totalAmount = (TextView) findViewById(R.id.TotalPayAmount);
        taxPer = (TextView) findViewById(R.id.TaxPer);
        taxAmount = (TextView) findViewById(R.id.TaxAmount);

        llPdf=(LinearLayout) findViewById(R.id.PrintScreen);

        init();


    }
    public static Bitmap loadBitmapFromView(View v, int width, int height) {

        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void openGeneratedPDF(){
        File file = new File("INVOICE/pdffromlayout.pdf");

        if  (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(GenerateInvoice.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createPdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHighet = (int) hight, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PageInfo pageInfo = new PageInfo.Builder(convertWidth, convertHighet, 1).create();


        Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);



        String targetPdf = getStorageDir("file.txt");

            File filePath = new File(targetPdf);
            try {
                document.writeTo(new FileOutputStream(filePath));

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
            }

            // close the document
            document.close();
            Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();
        shareScreenshot(filePath);

//            openGeneratedPDF();
    }
    public String getStorageDir(String fileName) {
        //create folder
        File file = new File(Environment.getExternalStorageDirectory() + "/folder/INVOICE");
        if (!file.mkdirs()) {
            file.mkdirs();
        }
        String filePath = file.getAbsolutePath() + File.separator + fileName;
        return filePath;
    }

    private void TakeScreeShots() {
        bitmap = loadBitmapFromView(llPdf, llPdf.getWidth(), llPdf.getHeight());
        createPdf();
    }


    private void shareScreenshot(File file) {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.ABC)));
    }



    public void init() {

        int totalPrice=0;
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

        tableRowParams.setMargins(5,5,5,5);


        TableRow tbrow0 = new TableRow(this);
        tbrow0.setLayoutParams(tableRowParams);
        TextView tv0 = new TextView(this);
        tv0.setText("Cart Id");
        tv0.setTextColor(Color.BLACK);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Product");
        tv1.setTextColor(Color.BLACK);
        tbrow0.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(" Unit Price ");
        tv2.setTextColor(Color.BLACK);
        tbrow0.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText(" Count");
        tv3.setTextColor(Color.BLACK);
        tbrow0.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setText(" Total");
        tv4.setTextColor(Color.BLACK);
        tbrow0.addView(tv4);

        stk.addView(tbrow0);
        
        int siz=countItems.size();
        for (int i = 0; i < siz; i++) {

            TableRow tbrow = new TableRow(this);
            tbrow.setLayoutParams(tableRowParams);
            int colo=0;
            if (i%2==0){
                colo=Color.GRAY;
            }
            else {
                colo=Color.DKGRAY;
            }

            tbrow.setBackgroundColor(colo);

            TextView t1v = new TextView(this);
            t1v.setText(Integer.toString(i+1));
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText(cartId.get(i).getName());
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);

            TextView t3v = new TextView(this);
            t3v.setText(cartId.get(i).getCost());
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);

            TextView t4v = new TextView(this);
            t4v.setText(Integer.toString(countItems.get(i)));
            t4v.setTextColor(Color.WHITE);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);

            TextView t5v = new TextView(this);
            int mul=countItems.get(i)*Integer.parseInt(cartId.get(i).getCost());
            totalPrice=totalPrice+mul;
            t5v.setText(Integer.toString(mul));
            t5v.setTextColor(Color.WHITE);
            t5v.setGravity(Gravity.CENTER);
            tbrow.addView(t5v);


            stk.addView(tbrow);
        }


        double tax=0.18;
        taxPer.setText("0.18 %");
        int taxAmt=(int)(tax*totalPrice);
        taxAmount.setText(Integer.toString(taxAmt));
        amount.setText(Integer.toString(totalPrice));
        totalAmount.setText(Integer.toString(totalPrice+taxAmt));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Print:
                TakeScreeShots();
                break;
        }
    }
}