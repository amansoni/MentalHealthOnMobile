package com.example.theom.mmha;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.support.design.widget.NavigationView;
import com.example.theom.mmha.Fragments.HomepageFragment;
import com.example.theom.mmha.Fragments.Map.GmapFragment;
import com.example.theom.mmha.Fragments.Places.PlacePins;
import com.example.theom.mmha.Fragments.Places.PlacesList;
import com.example.theom.mmha.Fragments.Places.SearchDoctorFragment;
import com.example.theom.mmha.Fragments.QuestionFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomepageFragment.OnFragmentInteractionListener, QuestionFragment.OnFragmentInteractionListener{

    ListView listView;
    ArrayAdapter<String> listAdapter;
    String fragmentArray[] = {"Homepage", "Quiz", "Map"};
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_draw);

        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fragmentArray);
        listView.setAdapter(listAdapter);
        
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        Fragment fragment = new HomepageFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Fragment fragment;
                switch (position){
                    case 0:
                        fragment = new HomepageFragment();
                        break;
                    case 1:
                        fragment = new QuestionFragment();
                        break;
                    case 2:
                        fragment = new SearchDoctorFragment();
                        break;
                    default:
                        fragment = new HomepageFragment();
                        break;
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();
                drawerLayout.closeDrawers();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_items, menu);
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

    public static PlacePins returnPlacePins(){
        PlacePins placePins = new PlacePins();
        return placePins;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }
}


