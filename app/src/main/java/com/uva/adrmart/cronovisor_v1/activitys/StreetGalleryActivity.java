package com.uva.adrmart.cronovisor_v1.activitys;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.uva.adrmart.cronovisor_v1.R;
import com.uva.adrmart.cronovisor_v1.adapter.GridViewStreetAdapter;
import com.uva.adrmart.cronovisor_v1.domain.Street;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StreetGalleryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = StreetGalleryActivity.class.getName();
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewStreetAdapter mGridAdapter;
    private ArrayList<Street> mGridData;
    private RequestQueue requestQueue;

    private static final String URL_STREETS= "http://virtual.lab.inf.uva.es:20202/street/?format=json";
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewStreetAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                Street item = (Street) parent.getItemAtPosition(position);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(StreetGalleryActivity.this, ImageGalleryActivity.class);
                intent.putExtra(ImageGalleryActivity.EXTRA_PARAM_ID, item.getId());
                intent.putExtra(ImageGalleryActivity.EXTRA_PARAM, 2);

                //Start details activity
                startActivity(intent);
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        mProgressBar.setVisibility(View.VISIBLE);
        requestStreets();
    }

    private void requestStreets(){

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(URL_STREETS, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                // Obtener el marker del objeto
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject objeto= response.getJSONObject(i);
                        Street street = new Street(objeto.getString("description"),
                                objeto.getInt("id"),
                                objeto.getString("name"),
                                objeto.getString("represent"));
                        mGridData.add(street);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error de parsing: "+ e.getMessage());
                    }
                    if (i==response.length()-1){
                        Log.d(TAG,"Ultimo elemento de la respuesta: " + i);
                    }
                }
                mGridAdapter.setGridData(mGridData);
                mProgressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage() +  " || " + error.getLocalizedMessage());
            }
        });
        requestQueue.add(jsArrayRequest);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            Intent i = new Intent(getApplicationContext(), MapsActivity.class );
            startActivity(i);

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_route) {

        } else if (id == R.id.nav_download) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGridAdapter.clear();
                mGridAdapter.setGridData(mGridData);
                EditText et = (EditText) findViewById(R.id.search_src_text);

                //Clear the text from EditText view
                et.setText("");

                //Clear query
                searchView.setQuery("", false);
                //Collapse the action view
                searchView.onActionViewCollapsed();
                //Collapse the search widget
                searchItem.collapseActionView();
                requestStreets();
            }
        });
        if (searchView != null) {

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);
                    mGridAdapter.search(query);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onOptionsItemSelected(item);
    }
}
