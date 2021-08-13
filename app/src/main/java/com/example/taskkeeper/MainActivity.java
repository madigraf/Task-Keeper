package com.example.taskkeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.taskkeeper.Dialog.ManageCategoriesDialog;
import com.example.taskkeeper.Fragment.ArchivetasksFragment;
import com.example.taskkeeper.Fragment.MaintasksFragment;
import com.example.taskkeeper.Fragment.TaskFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize toolbar as appbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Initialize Bottom Navigation View
        BottomNavigationView navView = findViewById(R.id.bottomNav_view);

        // Pass the ID's of Different destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_today, R.id.navigation_week, R.id.navigation_maintasks,
                R.id.navigation_archivetasks, R.id.navigation_routines)
                .build();

        // Initialize NavController
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Set up listener for when ManageCategoriesDialog closes, and the activity needs to refresh the data in fragments
        getSupportFragmentManager().setFragmentResultListener(ManageCategoriesDialog.TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(String requestKey, Bundle result) {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
                try{
                    TaskFragment current = (TaskFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    current.refreshList();
                } catch (Exception e) {
                    Log.e(TAG, "onFragmentResult for MainActivity: ", e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_categories:
                // User chose the "Manage Categories" item, show the app settings UI...
                new ManageCategoriesDialog().show(getSupportFragmentManager(), ManageCategoriesDialog.TAG);
                return true;

            case R.id.action_prune:
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
                try{
                    TaskFragment current = (TaskFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    current.prune();
                } catch (Exception e) {
                    Log.e(TAG, "onOptionsItemSelected: ", e);
                }

                return true;

            /*case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;*/

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}