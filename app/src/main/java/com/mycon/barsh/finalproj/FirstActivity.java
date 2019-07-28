package com.mycon.barsh.finalproj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class FirstActivity extends Activity {

    static class Rdatabase    {
        String name;
        String Category;
        String UrlAddress;
        String ImageUrl;
        int NumOfIngredients;
        int NumOfAvailable;

        private Rdatabase() {
            this.NumOfIngredients = 0;
            this.NumOfAvailable=0;
        }
    } // Recipes database

    public static final int PossibleIngSize = 1000; // java version of DEFINE (more or less)
    public static final int MinimumPercentages = 20; // maybe will change the number or let the user control it
    int possibleIngIndex=0;
    String[] PossibleIng = new String [PossibleIngSize];
    Rdatabase[] database;
    JSONArray jsonArray;

    ArrayList<NodeObject> node1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


        final AutoCompleteTextView AddNameEditText = findViewById(R.id.Edittext1);
        final Button ShowBtn = findViewById(R.id.ShowBtn);
        final Button DeleteBtn = findViewById(R.id.DeleteBtn);
        final NotesAdapter adapter = new NotesAdapter();

        final ListView Listview1 = findViewById(R.id.Listview1);
        Listview1.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL); // push the first items up when keyboard is active.
        Listview1.setAdapter(adapter);



        if(getIntent().hasExtra("LaunchSource")){

            jsonArray = readjson(); // converting file into JSON array
            FillIngredients(); // filling possible ingredients
            database = new Rdatabase[jsonArray.length()];
            int k;
            String currentstring;
            SharedPreferences settings = getSharedPreferences("Available", MODE_PRIVATE);
            SharedPreferences prefs = getSharedPreferences("Checked", MODE_PRIVATE);

            for (k=0; k<jsonArray.length();k++) // for each recipe - fill recipes database
            {
                try {
                    database[k] = new Rdatabase();
                    JSONObject obj = jsonArray.getJSONObject(k);
                    String IngArray = obj.getString("ingredients");
                    JSONArray temp = new JSONArray(IngArray);

                    database[k].name = obj.getString("name");
                    database[k].Category = obj.getString("category");
                    database[k].UrlAddress = obj.getString("url");
                    database[k].ImageUrl = obj.getString("urlImg");
                    database[k].NumOfIngredients = temp.length();

                }
                catch(JSONException e){ e.printStackTrace(); }
            }


            // had to copy the entire string because the original (PossibleIng) has nulls, which doesn't work with the adapter
            String[] forautocomplete = new String[possibleIngIndex];

            for (k=0;k<possibleIngIndex;k++) forautocomplete[k] = PossibleIng[k];

            Arrays.sort(forautocomplete);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,android.R.layout.select_dialog_item,forautocomplete);
            AddNameEditText.setAdapter(adapter2);


            k = 0;
            currentstring = prefs.getString("CheckedR"+k,"");
            while(!currentstring.equals("")) // adding the ingredients to the list
            {
                    NodeObject note = new NodeObject(currentstring);
                    node1.add(0,note);
                k++;
                currentstring = prefs.getString("CheckedR"+k,"");
            }

            settings.edit().clear().apply(); // clears SharedPreferences
            prefs.edit().clear().apply();
            adapter.notifyDataSetChanged(); // commit the list's changes


        }


        //################# Buttons/List functions #################


        // user has entered an ingredient
        AddNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND)
                {

                    String CurrentText = AddNameEditText.getText().toString();
                    if (AlreadyExists(CurrentText)) {
                        Toast myToast = Toast.makeText(getApplicationContext(), "Name Already Exists.", Toast.LENGTH_LONG);
                        myToast.show();
                    }
                    else if(!IngredientExists(CurrentText)){
                        Toast myToast = Toast.makeText(getApplicationContext(), "Ingredient Doesn't Exist.", Toast.LENGTH_LONG);
                        myToast.show();
                    }
                    else {

                        NodeObject note = new NodeObject(CurrentText);
                        node1.add(0,note);
                        adapter.notifyDataSetChanged();
                        AddNameEditText.setText("");

                    }
                    handled = true;
                }
                return handled;
            }
        });

        // user has pressed on an existing ingredient (on the list)
        Listview1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CheckBox cb = view.findViewById(R.id.checkBox);
                boolean currentstate = cb.isChecked();
                if (currentstate)
                {
                    node1.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // user has pressed on "Possible ingredients" button - moving to the Fourth Activity
        ShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),FourthActivity.class);

                int pass;
                startIntent.putExtra("ArraySize",possibleIngIndex);

                for (pass=0; pass<possibleIngIndex;pass++)
                {
                    startIntent.putExtra("AvailableIng" + pass, PossibleIng[pass]);
                }

                for (pass=0; pass<node1.size();pass++)
                {
                    startIntent.putExtra("PresetIng"+pass,node1.get(pass).title);
                }

                startActivity(startIntent);
            }
        });

        // User has pressed on the "Delete list" button
        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while(node1.size()>0) node1.remove(node1.size()-1);
                adapter.notifyDataSetChanged();
            }
        });

    }


    //################# General functions #################
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            FillRData();
            SharedPreferences.Editor editor = getSharedPreferences("Available", MODE_PRIVATE).edit();
            SharedPreferences.Editor editor2 = getSharedPreferences("Checked", MODE_PRIVATE).edit();
            int index,Sharedindex=0,i,available,exist,percent;
            String percentS;
            editor.putString("text","NotEmpty");

            // saving the list ingredients
            for (index=0; index<node1.size(); index++)
            {
                editor2.putString("CheckedR"+index, node1.get(index).title);
            }

            // save the fitting recipes, used by 'Second Activity'
            for (i=0;i<database.length;i++)
            {
                exist = database[i].NumOfIngredients;
                available = database[i].NumOfAvailable;
                percent = available*100/exist;
                percentS = Integer.toString(percent);
                percentS = percentS + "%";
                if (percent>=MinimumPercentages)
                {
                    editor2.putString("Recname"+Sharedindex, database[i].name);
                    editor2.putString("RecCategory"+Sharedindex, database[i].Category);
                    editor2.putString("Recurl"+Sharedindex, database[i].UrlAddress);
                    editor2.putString("Recimageurl"+Sharedindex, database[i].ImageUrl);
                    editor2.putString("Recpercent"+Sharedindex, percentS);
                    Sharedindex++;
                }
            }
            editor.apply();
            editor2.apply();
            Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(startIntent);

        }
        return super.onKeyDown(keyCode, event);
    } // user has pressed the 'back' button -> going to 'Main Activity'
    public boolean AlreadyExists (String NewString) {
        int i;
        if(node1.size()!=0) {
            for (i = 0; i < node1.size(); i++) {
                if (node1.get(i).title.equals(NewString)) return true;
            }
        }
        return false;
    } // checks if a string exist in the list
    public boolean IngredientExists(String newIng) {
        int i;
        for (i=0;i<possibleIngIndex;i++)
        {
            if (PossibleIng[i]!=null)
            {
                if (PossibleIng[i].equals(newIng)) return true;
            }
            else break;
        }
        return false;
    } // checks if the ingredient we about to add to the database already exists
    public void FillIngredients() {
        int i,j,size = jsonArray.length();
        for (i=0;i<size;i++) {
            try
            {
            JSONObject obj = jsonArray.getJSONObject(i);
            String IngArray = obj.getString("ingredients");
            JSONArray temp = new JSONArray(IngArray);

            for (j=0;j<temp.length();j++) {
                String currenting = temp.getString(j);
                if (!IngredientExists(currenting)) {
                    PossibleIng[possibleIngIndex] = currenting;
                    possibleIngIndex++;
                }
            }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }// read the ingredients of each recipe and save all the possible ingredients
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
            View view = layoutInflater.inflate(R.layout.node_layout,viewGroup,false);
            TextView titleView = view.findViewById(R.id.title);

            NodeObject current = node1.get(i);
            titleView.setText(current.title);

            return view;
        }
    } // code for the list nodes
    public JSONArray readjson() {
        String json;
        try
        {
            InputStream is = getAssets().open("recipesjson.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer,"UTF-8");

            try {
                JSONObject obj = new JSONObject(json);
                jsonArray = obj.getJSONArray("recpi");
                return jsonArray;

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }// putting the recipes file into a JSON array
    public boolean IngInRec(int recindex, String ing){
        int i;
        try {
            JSONObject obj = jsonArray.getJSONObject(recindex);
            String bar = obj.getString("ingredients");
            JSONArray temp = new JSONArray(bar);
            for (i=0;i<temp.length();i++)
            {
                String currenting = temp.getString(i);
                if (currenting.equals(ing)) return true;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public void FillRData() {
        int i,j;
        for (i=0;i<node1.size();i++)
        {
            String CurrentIng = node1.get(i).title;
            for (j=0;j<database.length;j++)
            {
                if (IngInRec(j,CurrentIng)) database[j].NumOfAvailable++;
            }
        }
    } // updates "NumberOfAvailable" variable of each recipe

}
