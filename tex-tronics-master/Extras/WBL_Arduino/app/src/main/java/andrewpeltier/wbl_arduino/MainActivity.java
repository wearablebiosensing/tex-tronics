package andrewpeltier.wbl_arduino;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import andrewpeltier.wbl_arduino.fragments.HelpFragment;
import andrewpeltier.wbl_arduino.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private final static String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,                  // Required for BLE Operations
            Manifest.permission.BLUETOOTH_ADMIN,            // Required to access paired Bluetooth devices
            Manifest.permission.READ_EXTERNAL_STORAGE,      // Required to read help PDF from assets
            Manifest.permission.INTERNET                    // Optional to read help PDF from web URL
    };
    private static final int PERMISSION_CODE = 111;
    private Context mContext;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String mFragmentTag;
    private String mPreviousTag;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initially load the fragment container with the home fragment
        fragmentManager = getSupportFragmentManager();
        mFragmentTag = "HomeFragment";
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, new HomeFragment(), "HomeFragment");
        fragmentTransaction.commit();
    }

    public void addFragment(Fragment fragment, String tag)
    {
        // Replaces the current fragment with the fragment parameter
        Log.d(TAG, "addFragment: Adding fragment " + tag);
        if(!mFragmentTag.equals("HelpFragment"))
            mPreviousTag = mFragmentTag;
        mFragmentTag = tag;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.commit();
    }

    /**
                ====== Override Methods ======
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_help:
                addFragment(new HelpFragment(), "HelpFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!mFragmentTag.equals("HomeFragment"))
            addFragment(GetFragment.get(mPreviousTag), mPreviousTag);
        else
            super.onBackPressed();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.i(TAG, "onStart: starting...");

        // Check Permissions at Runtime (Android M+), and Request if Necessary
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // The Results from this Request are handled in a Callback Below.
            requestPermissions(PERMISSIONS, PERMISSION_CODE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        // TODO: Handle event Permissions denied
        Log.i(TAG, "onRequestPermissionsResult: permissions denied...");
    }
}
