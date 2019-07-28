package com.mycon.barsh.finalproj;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class SecondActivity extends Activity {

    ArrayList<NodeObject> node1 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        //-------------------------------------------------------- Main Start

        int index=0;
        String name,rank,url,imageurl,category;
        SharedPreferences prefs = getSharedPreferences("Checked", MODE_PRIVATE);

        ListView Listview1 = findViewById(R.id.Listview1);
        final NotesAdapter adapter2 = new NotesAdapter();
        Listview1.setAdapter(adapter2);

        while(!prefs.getString("Recname"+index,"").equals("")) {
            name = prefs.getString("Recname"+index,"");
            rank = prefs.getString("Recpercent"+index,"");
            url = prefs.getString("Recurl"+index,"");
            imageurl= prefs.getString("Recimageurl"+index,"");
            category= prefs.getString("RecCategory"+index,"");
            NodeObject note = new NodeObject(name, rank, url, imageurl, category);
            node1.add(note);
            index++;
        }

        adapter2.notifyDataSetChanged();

        Listview1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                NodeObject item = node1.get(i);
                Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);

                intent.putExtra("title", item.title);
                intent.putExtra("url",item.url);
                intent.putExtra("imageurl",item.imageurl);
                intent.putExtra("rank",item.rank);
                intent.putExtra("category",item.category);


                startActivity(intent);
            }

        });

    }
    public class NotesAdapter extends BaseAdapter // code for the list nodes
    {

        @Override
        public int getCount() {
            return node1.size();
        }

        @Override
        public Object getItem(int i) {
            return node1.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater.inflate(R.layout.node2_layout,viewGroup,false);
            TextView titleView = view.findViewById(R.id.title);
            TextView rank = view.findViewById(R.id.rank);
            TextView category = view.findViewById(R.id.category);

            NodeObject current = node1.get(i);
            titleView.setText(current.title);
            rank.setText(current.rank);
            category.setText(current.category);
            return view;
        }
    }


}
