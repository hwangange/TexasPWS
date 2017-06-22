package watersystems.texas.restfulcall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LruCache;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private Button button;
    private StringRequest request;
    private String link;
    private TextView display;
    private JsonArrayRequest jsObjRequest;
    private MySingleton mySingleton;
    private EditText water_system_name;
    private EditText county;
    private EditText city;

    private List<Map<String, String>> resultList;
    private String[] from = {"PWSID", "PWS_NAME"};
    private int[] to = {R.id.PWSID, R.id.PWS_NAME};
    private BaseAdapter simpleAdapterResult;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);
        water_system_name = (EditText) findViewById(R.id.water_system_name);
        county = (EditText) findViewById(R.id.county);
        city = (EditText) findViewById(R.id.city);

        button = (Button) findViewById(R.id.button);
        display = (TextView) findViewById(R.id.display);
        link = "https://iaspub.epa.gov/enviro/efservice/t_design_for_environment/JSON/rows/1:19";
        link = "https://iaspub.epa.gov/enviro/efservice/WATER_SYSTEM/PWSID/CONTAINING/TX/rows/1:10/JSON";
        link = "https://iaspub.epa.gov/enviro/efservice/GEOGRAPHIC_AREA/COUNTY_SERVED/CONTAINING/";
        //working: https://iaspub.epa.gov/enviro/efservice/WATER_SYSTEM/PWSID/CONTAINING/TX/ZIP_CODE/CONTAINING/77479/rows/1:10/JSON
        //also working; https://iaspub.epa.gov/enviro/efservice/WATER_SYSTEM/PWSID/CONTAINING/TX/CITY_NAME/CONTAINING/LAND/rows/1:10/JSON
        //note: city must be ALL CAPS.
        resultList = new ArrayList<Map<String, String>>();
        list = (ListView) findViewById(R.id.list);
        simpleAdapterResult = new SimpleAdapter(this, resultList, R.layout.search_result, from, to);
        list.setAdapter(simpleAdapterResult);


        mySingleton = new MySingleton(getApplication());



        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String countyName = county.getText().toString();
                String cityName = city.getText().toString().toUpperCase();
                String systemName = water_system_name.getText().toString().toUpperCase();
                link = "https://iaspub.epa.gov/enviro/efservice/WATER_SYSTEM/PWSID/CONTAINING/TX";
                if(countyName!=null && !countyName.equals("")) {
                    countyName = countyName.replace(" ", "%20");
                    link+= "/COUNTIES_SERVED/CONTAINING/" + countyName;
                }
                if(cityName!=null && !cityName.equals("")) {
                    cityName = cityName.replace(" ", "%20");
                    link+= "/CITY_NAME/CONTAINING/" + cityName;
                }
                if(systemName!=null && !systemName.equals("")) {
                    systemName = systemName.replace(" ", "%20");
                    link+= "/PWS_NAME/CONTAINING/" + systemName;
                }

                link+="/rows/1:10/JSON";

               // link = "https://iaspub.epa.gov/enviro/efservice/GEOGRAPHIC_AREA/COUNTY_SERVED/CONTAINING/" + countyName+"/rows/1:10/JSON";

                jsObjRequest = new JsonArrayRequest(Request.Method.GET, link, (String)null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        resultList.clear();
                        int length = response.length();
                        Toast.makeText(getApplicationContext(),"Length: " + length,Toast.LENGTH_LONG).show();
                        for(int i = 0; i < length; i ++) {
                            JSONObject obj = (JSONObject) response.opt(i);

                            try {
                                String pws_id = obj.getString("PWSID");
                                String pws_name = obj.getString("PWS_NAME");
                                resultList.add(createResult(pws_id, pws_name));
                                simpleAdapterResult.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(),"ERROR: " + error,Toast.LENGTH_LONG).show();
                    }
                });


                Toast.makeText(getApplicationContext(),"Link:   " + link,Toast.LENGTH_LONG).show();
                mySingleton.getInstance(getApplication()).addToRequestQueue(jsObjRequest);
            }

        });




    }

    private HashMap<String, String> createResult(String id, String name) {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("PWSID", id);
        result.put("PWS_NAME", name);
        return result;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


