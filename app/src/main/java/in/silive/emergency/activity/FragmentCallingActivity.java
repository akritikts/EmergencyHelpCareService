package in.silive.emergency.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.silive.emergency.R;
import in.silive.emergency.fragment.TypeOfEmergency;
import in.silive.emergency.receiver.VolumeReceiver;
import in.silive.emergency.fragment.ContactsFragment;


public class FragmentCallingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters params;
    SharedPreferences sharedPreferences;
    String Sname,Sicon;
    String MyProfile = "Profile";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentcallingactivity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        sharedPreferences = getSharedPreferences(MyProfile, MODE_PRIVATE);
        Sname = sharedPreferences.getString("Name", "");
        Sicon = sharedPreferences.getString("icon", "");

        insertDummyInternetPermission();
        insertDummyAudioSettingPermission();

        Intent alarmIntent = new Intent(getApplicationContext(), VolumeReceiver.class);
        // Pending Intent Object
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Alarm Manager Object
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        // Remote MySQL DB
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 3000, 2000, pendingIntent);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.removeHeaderView(header);

        View hd =getLayoutInflater().inflate(R.layout.nav_header_nav_activity , null);
        TextView tvname = (TextView) hd.findViewById(R.id.tvnavtextview);
        tvname.setText(Sname);

            if (!Sicon.isEmpty()) {
                ImageView icon = (ImageView) hd.findViewById(R.id.ivnavIcon);
                byte[] array = Base64.decode(Sicon, Base64.DEFAULT);
                Bitmap bitmapIcon = BitmapFactory.decodeByteArray(array, 0, array.length);
                icon.setImageBitmap(getRoundedShape(bitmapIcon));
            }


        navigationView.addHeaderView(hd);
        navigationView.setNavigationItemSelectedListener(this);



    }
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 500;
        int targetHeight = 500;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TypeOfEmergency(), "EMERGENCY'S");
        adapter.addFragment(new ContactsFragment(), "CONTACTS");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>(); //for fragments
        private final List<String> mFragmentTitleList = new ArrayList<>(); //for fragment titles

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.profile){

            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);

        }
        if(id == R.id.flashlight){
            //getting flashLight feature
            hasFlash = getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            if (!hasFlash) {
                // device doesn't support flash
                // Show alert message and close the application
                final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Sorry, your device doesn't support flash light!");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.setCancelable(true);
                    }
                });
                alertDialog.create();
                alertDialog.show();

            }

            getCamera();

            if (isFlashOn) {
                // turn off flash
                turnOffFlash();
            } else {
                // turn on flash
                turnOnFlash();
            }

        }
        if(id == R.id.addContacts){
            Intent intent = new Intent(this , SelectContacts.class);
            intent.putExtra("contact" , "back");
            startActivity(intent);

        }

        if(id == R.id.deleteContacts){

            Intent intent = new Intent(this , DeleteContacts.class);
            startActivity(intent);

        }
        if(id == R.id.itemAbout){

            Intent intent = new Intent(this , About.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void turnOnFlash() {
        insertDummyCameraPermission();
        //if flash is in off condition
        if(!isFlashOn){
            if(camera == null || params == null)
                return;
        }
        params =camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        isFlashOn=true;
    }

    private void turnOffFlash() {
        //checking is flash is on or off
        if(isFlashOn){
            if(camera == null || params == null)
                return;
        }
        try {
            //get the parameters for camera
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            //set the camera parameters
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void getCamera() {
        if (camera == null) {
            try {
                //open camera
                camera = Camera.open();
                //get the camera parameters
                params = camera.getParameters();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        turnOffFlash();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
    final private int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 123;
    private void insertDummyCameraPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(FragmentCallingActivity.this,
                Manifest.permission.CAMERA);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(FragmentCallingActivity.this,
                    android.Manifest.permission.CAMERA)) {

                                ActivityCompat.requestPermissions(FragmentCallingActivity.this,
                                        new String[] {android.Manifest.permission.CAMERA},
                                        REQUEST_CODE_ASK_PERMISSIONS_CAMERA);

                return;
            }
            ActivityCompat.requestPermissions(FragmentCallingActivity.this,
                    new String[] {android.Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
            return;
        }
    }


    final private int REQUEST_CODE_ASK_PERMISSIONS_INTERNET = 123;
    private void insertDummyInternetPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(FragmentCallingActivity.this,
                Manifest.permission.INTERNET);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(FragmentCallingActivity.this,
                    android.Manifest.permission.INTERNET)) {

                ActivityCompat.requestPermissions(FragmentCallingActivity.this,
                        new String[] {android.Manifest.permission.INTERNET},
                        REQUEST_CODE_ASK_PERMISSIONS_INTERNET);

                return;
            }
            ActivityCompat.requestPermissions(FragmentCallingActivity.this,
                    new String[] {android.Manifest.permission.INTERNET},
                    REQUEST_CODE_ASK_PERMISSIONS_INTERNET);
            return;
        }

    }
    final private int REQUEST_CODE_ASK_PERMISSIONS_MODIFY_AUDIO_SETTINGS = 123;
    private void insertDummyAudioSettingPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(FragmentCallingActivity.this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(FragmentCallingActivity.this,
                    android.Manifest.permission.MODIFY_AUDIO_SETTINGS)) {

                ActivityCompat.requestPermissions(FragmentCallingActivity.this,
                        new String[] {android.Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        REQUEST_CODE_ASK_PERMISSIONS_MODIFY_AUDIO_SETTINGS);

                return;
            }
            ActivityCompat.requestPermissions(FragmentCallingActivity.this,
                    new String[] {android.Manifest.permission.MODIFY_AUDIO_SETTINGS},
                    REQUEST_CODE_ASK_PERMISSIONS_MODIFY_AUDIO_SETTINGS);
            return;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_ASK_PERMISSIONS_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
               turnOnFlash();
            }
        }
    }
}