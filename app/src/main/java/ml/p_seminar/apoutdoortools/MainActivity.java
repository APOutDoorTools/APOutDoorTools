package ml.p_seminar.apoutdoortools;


/**
 * Diese Klasse ermöglicht mittels dem seitlichen Menü zwischen den einzelnen Activities hin und her zu schalten.
 * Geschrieben von Lukas, Felix, Tobias, Sabrina.
 * Layout angepasst von Tobias
 */

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import static ml.p_seminar.apoutdoortools.R.layout.activity_main;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static int cam = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getFragmentManager();
        setContentView(activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*@Override
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
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentmanager=mFragmentManager;

        if (id == R.id.nav_nubibusmeter) {
            fragmentmanager.beginTransaction().replace(R.id.content_frame,new nubibusmeterFragment()).commit();
        } else if (id == R.id.nav_compass) {
            fragmentmanager.beginTransaction().replace(R.id.content_frame,new AcusMagneticaFragment()).commit();
        } else if (id == R.id.nav_camera) {
            fragmentmanager.beginTransaction().replace(R.id.content_frame,new GPSFragment()).commit();
        } else if (id == R.id.nav_gallery) {
            fragmentmanager.beginTransaction().replace(R.id.content_frame,new KartenFragment()).commit();
        }else if (id == R.id.nav_handbuch) {
            fragmentmanager.beginTransaction().replace(R.id.content_frame,new HbFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private static android.app.FragmentManager mFragmentManager = null;
    public static android.app.FragmentManager getMFragmentManager() {
        return mFragmentManager;
    }

}
