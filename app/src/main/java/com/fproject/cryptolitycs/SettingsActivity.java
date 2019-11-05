 package com.fproject.cryptolitycs;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.fproject.cryptolitycs.converter.ConverterActivity;
import com.fproject.cryptolitycs.news.NewsActivity;
import com.fproject.cryptolitycs.topCoins.TopCoinsActivity;
import com.fproject.cryptolitycs.utility.Settings;
import com.fproject.cryptolitycs.watchlist.WatchListActivity;

 public class SettingsActivity extends AppCompatActivity
         implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Settings.getTheme(this));
        setContentView(R.layout.activity_settings);
        //
        // Activity
        //
        setupActivity();
        //
        // Listeners
        //
        setupListeners();
    }

     // --------------------------------------------------------------------------------------------
     //region Private Methods
     // --------------------------------------------------------------------------------------------

     /**
      * Configure the activity.
      */
     private void setupActivity(){
         //
         // Action Bar
         //
         this.setupActionBar();
         //
         // Radio Group
         //
         this.setupRadioGroup();
     }

     /**
      * Configure the Action Bar.
      */
     private void setupActionBar(){
         Toolbar toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
     }

     /**
      * Configure the Radio Button.
      */
     private void setupRadioGroup(){
         RadioGroup rgTheme = findViewById(R.id.rg_theme);

         // Check the radio button to indicate in order to
         // the currently active theme.

         switch (Settings.getTheme(this)) {
             case R.style.PurpleTheme:
                 rgTheme.check(R.id.rb_purple);
                 break;

             case R.style.GreyTheme:
                 rgTheme.check(R.id.rb_grey);
                 break;

             default:
                 rgTheme.check(R.id.rb_blue);
                 break;
         }
     }

     /**
      * Executed when the checked radio button changes.
      */
     private void onCheckedRadioButtonChanged(int id){
         switch (id) {
             case R.id.rb_purple:
                 this.restartActivity(R.style.PurpleTheme);
                 break;

             case R.id.rb_grey:
                 this.restartActivity(R.style.GreyTheme);
                 break;

             default:
                 this.restartActivity(R.style.BlueTheme);
                 break;
         }
     }

     /**
      * Restart the activity with the specified theme.
      */
     private void restartActivity(int themeId){
         Settings.setTheme(this, themeId);

         startActivity(getIntent());
         finish();

         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
     }

     /**
      * Hook up the event listeners.
      */
     private void setupListeners() {
         //
         // Drawer
         //
         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                 this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
         drawer.addDrawerListener(toggle);
         toggle.syncState();
         //
         // NavigationView
         //
         NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
         navigationView.setNavigationItemSelectedListener(this);
         //
         // Radio Button
         //
         RadioGroup rgTheme = findViewById(R.id.rg_theme);
         rgTheme.setOnCheckedChangeListener((radioGroup, id) ->
                 onCheckedRadioButtonChanged(id)
         );
     }

     // --------------------------------------------------------------------------------------------
     //endregion
     // --------------------------------------------------------------------------------------------


     // --------------------------------------------------------------------------------------------
     //region Override Methods
     // --------------------------------------------------------------------------------------------

     @SuppressWarnings("StatementWithEmptyBody")
     @Override
     public boolean onNavigationItemSelected(MenuItem item) {
         // Handle navigation view item clicks here.
         int id = item.getItemId();


         if (id == R.id.nav_top_coins) {

             switchToActivity(TopCoinsActivity.class);

         }  else if (id == R.id.nav_news) {

             switchToActivity(NewsActivity.class);

         } else if (id == R.id.nav_watchlist) {

             switchToActivity(WatchListActivity.class);

         } else if (id == R.id.nav_converter) {

             switchToActivity(ConverterActivity.class);

         } else if (id == R.id.nav_settings) {

             // already here

         } else if (id == R.id.nav_about) {

             switchToActivity(AboutActivity.class);

         }

         DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         drawer.closeDrawer(GravityCompat.START);
         return true;
     }

     /**
      * Close the current activity and open an activity of the specified type.
      */
     private void switchToActivity(Class activity) {

         Intent intent = new Intent(this, activity);

         startActivity(intent);
         finish();
         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

     // --------------------------------------------------------------------------------------------
     //endregion
     // --------------------------------------------------------------------------------------------
}
