package com.mycon.barsh.finalproj;


import android.app.Activity;
import java.util.ArrayList;
import java.util.Arrays;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


public class FourthActivity extends Activity {
    int PossibleSize=0,firsttime=-1;
    boolean Checkeding[];
    ArrayList<NodeObject> node1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        int i=0,position;
        PossibleSize=getIntent().getIntExtra("ArraySize",0);
        final String[] PossibleNames = new String[PossibleSize];
        final NotesAdapter adapter2 = new NotesAdapter();
        final ListView Listview1 = findViewById(R.id.Listview1);
        Listview1.setAdapter(adapter2);


            while(getIntent().hasExtra("AvailableIng"+i))
            {
                PossibleNames[i] = getIntent().getStringExtra("AvailableIng"+i);
                i++;
            }
            Arrays.sort(PossibleNames);
            for (i=0;i<PossibleSize;i++)
            {
                NodeObject note = new NodeObject(PossibleNames[i]);
                node1.add(note);

            }
            if (Checkeding==null) Checkeding = new boolean[PossibleSize];
            i=0;
        while(getIntent().hasExtra("PresetIng"+i))
        {
            position = IngredientIndex(PossibleNames,getIntent().getStringExtra("PresetIng"+i));
            if (position>=0) Checkeding[position] = true;
            i++;
        }


            adapter2.notifyDataSetChanged();


        Listview1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CheckBox cb = view.findViewById(R.id.checkBox);
                boolean currentstate = cb.isChecked();
                cb.setChecked(!currentstate);
                Checkeding[i] = !currentstate;

                if (firsttime==-1) {
                    int j;
                    View view2;
                    CheckBox cb2;
                    firsttime=1;
                    for (j=0;j<PossibleSize;j++)
                    {
                        if (Checkeding[j])
                        {
                            view2 = getViewByPosition(j, Listview1);
                            cb2 = view2.findViewById(R.id.checkBox);
                            cb2.setChecked(true);
                        }
                    }
                }
            }

        });



    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // FIX THIS
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            int i,Checkedindex=0;
            SharedPreferences.Editor editor = getSharedPreferences("Checked", MODE_PRIVATE).edit();

            Intent startIntent = new Intent(getApplicationContext(),FirstActivity.class);
            startIntent.putExtra("LaunchSource","Fourth");
            for (i=0;i<PossibleSize;i++)
            {
                if(Checkeding[i])
                {
                    editor.putString("CheckedR"+Checkedindex, node1.get(i).title);
                    Checkedindex++;
                }
            }
            editor.apply();
            startActivity(startIntent);

        }

        return super.onKeyDown(keyCode, event);
    } // enters when a user pressed on the 'back' button
    public class NotesAdapter extends BaseAdapter {

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
            View view = layoutInflater.inflate(R.layout.node4_layout,viewGroup,false);

            TextView titleView = view.findViewById(R.id.title);
            NodeObject current = node1.get(i);

            titleView.setText(current.title);

            return view;
        }
    } // code for the list nodes
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    } // for the check box
    public int IngredientIndex(String[] IngArray, String IngName) {
      int i =0;
      for (i=0;i<IngArray.length;i++) if(IngArray[i].equals(IngName)) return i;
      return -1;
    }

}
