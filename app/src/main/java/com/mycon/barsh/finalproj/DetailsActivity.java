package com.mycon.barsh.finalproj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(getIntent().hasExtra("title"))
        {
            String t;
            TextView title = findViewById(R.id.title);
            t = getIntent().getStringExtra("title");
            title.setText(t);
            final String imageurl = getIntent().getStringExtra("imageurl");
            final ImageView Ron = findViewById(R.id.ImageView1);
            new Thread("bar")
            {
                public void run()
                {

                try
                {
                    URL url = new URL(imageurl);
                    InputStream stream = url.openConnection().getInputStream();
                    final Bitmap bnp;
                    bnp = BitmapFactory.decodeStream(stream);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Ron.setImageBitmap(bnp);
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                }
            }.start();



            Button InternetBtn = findViewById(R.id.InternetBtn);
            InternetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String urladdress = getIntent().getStringExtra("url");



                    Uri webaddress = Uri.parse(urladdress);

                    Intent gotoInternet = new Intent(Intent.ACTION_VIEW,webaddress);
                    if(gotoInternet.resolveActivity(getPackageManager())!=null)
                    {
                        startActivity(gotoInternet);
                    }
                }
            });
        }
    }

}
