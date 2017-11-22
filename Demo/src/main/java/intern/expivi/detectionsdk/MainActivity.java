package intern.expivi.detectionsdk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import intern.expivi.detectionlib.CommunicationInterface;
import intern.expivi.detectionlib.InitializationFragment;
import intern.expivi.detectionlib.NativeWrapper;

public class MainActivity extends AppCompatActivity implements CommunicationInterface, ActivityCompat.OnRequestPermissionsResultCallback {

    private String TAG = "DemoActivity";
    private boolean isInitialized = false;
    private static boolean showBinaire = false;
    private View mLayout;

    private static final int REQUEST_CAMERA = 0;


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
        mLayout = findViewById(R.id.activity_demo);

        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Camera permission has not been granted.
                requestCameraPermission();

            } else {
                // Camera permissions is already available, show the camera preview.
                Log.i(TAG, "CAMERA permission has already been granted. Displaying camera preview.");
                EnableContent();
            }
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

    void requestCameraPermission()
    {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                EnableContent();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void EnableContent()
    {
        if (!isInitialized) {
            replaceFragment(R.id.initialization_fragment);
        } else {
            replaceFragment(R.id.demo_fragment);
        }
    }

    //TODO: Poorly designed and confusing to call, needs a better method of switching fragments
    private void replaceFragment(int newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        if (newFragment == R.id.initialization_fragment) {
            fragment = new InitializationFragment();
        } else {
            fragment = new DemoFragment();
        }
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitAllowingStateLoss();
        showBinaire = false;
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
    @Override
    public void ShowBinaire()
    {
        showBinaire = !showBinaire;
        NativeWrapper.ShowBinaire(showBinaire);
    }
}

