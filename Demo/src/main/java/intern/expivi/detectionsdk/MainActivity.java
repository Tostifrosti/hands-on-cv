package intern.expivi.detectionsdk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import intern.expivi.detectionlib.CommunicationInterface;
import intern.expivi.detectionlib.InitializationFragment;
import intern.expivi.detectionlib.NativeWrapper;

public class MainActivity extends AppCompatActivity implements CommunicationInterface {

    private String TAG = "DemoActivity";
    private boolean isInitialized = false;

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: called");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);

        // recovering the instance state
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate: savedInstanceState");
            isInitialized = savedInstanceState.getBoolean("isInitialized");
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_demo);

        if (!isInitialized) {
            replaceFragment(R.id.initialization_fragment);
        } else {
            replaceFragment(R.id.demo_fragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called");
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        NativeWrapper.Destroy();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: called");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: called");
        outState.putBoolean("isInitialized", isInitialized);
        super.onSaveInstanceState(outState);
    }

    //TODO: Poorly designed and confusing to call, needs a better method of switching fragments
    private void replaceFragment(int newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        if (newFragment == R.id.initialization_fragment) {
            fragment = InitializationFragment.newInstance();
        } else {
            fragment = DemoFragment.newInstance();
        }
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void Initialize() {
        isInitialized = false;
        replaceFragment(R.id.initialization_fragment);
    }

    @Override
    public void Detect() {
        isInitialized = true;
        replaceFragment(R.id.demo_fragment);
    }
}

