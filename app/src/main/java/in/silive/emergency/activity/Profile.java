package in.silive.emergency.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import in.silive.emergency.R;
import in.silive.emergency.activity.EnterPersonalDetail;


public class Profile extends AppCompatActivity {

    TextView mobile,disease,inherited,dob,address,blood;
    ImageView icon;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    SharedPreferences sharedPreferences;
    String MyProfile = "Profile";
    String Sname,Smobile,Saddress,Sblood,Sdob,Sinherited,Sdiseases,Sicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        toolbar = (Toolbar) findViewById(R.id.tbProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PROFILE");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout =(CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));


        mobile = (TextView) findViewById(R.id.tvmobile);
        dob = (TextView) findViewById(R.id.tvdob);
        address = (TextView) findViewById(R.id.tvaddress);
        inherited = (TextView) findViewById(R.id.tvinherited);
        disease = (TextView) findViewById(R.id.tvdiseases);
        blood = (TextView) findViewById(R.id.tvblood);
        icon = (ImageView) findViewById(R.id.ivIcon);


            sharedPreferences = getSharedPreferences(MyProfile, MODE_PRIVATE);
            Sname = sharedPreferences.getString("Name", "");
            Smobile = sharedPreferences.getString("MobileNO", "");
            Saddress = sharedPreferences.getString("Address", "");
            Sdob = sharedPreferences.getString("DOB", "");
            Sblood = sharedPreferences.getString("BloodGroup", "");
            Sinherited = sharedPreferences.getString("InheritedDiseases", "");
            Sdiseases = sharedPreferences.getString("Diseases", "");
            Sicon = sharedPreferences.getString("icon", "");

        if(!Sname.isEmpty())
            collapsingToolbarLayout.setTitle(Sname);

        if(!Smobile.isEmpty())
            mobile.setText(Smobile);

        if(!Saddress.isEmpty())
            address.setText(Saddress);

        if(!Sblood.isEmpty())
            blood.setText(Sblood);

        if(!Sdob.isEmpty())
            dob.setText(Sdob);

        if(!Sinherited.isEmpty())
            inherited.setText(Sinherited);

        if(!Sdiseases.isEmpty())
            disease.setText(Sdiseases);

        if(!Sicon.isEmpty()){
            byte[] array = Base64.decode(Sicon, Base64.DEFAULT);
            Bitmap bitmapIcon = BitmapFactory.decodeByteArray(array, 0, array.length);
            icon.setImageBitmap(bitmapIcon);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.itedit){

            Intent intent = new Intent(this , EnterPersonalDetail.class);
            intent.putExtra("mobile", "edit");
            finish();
            startActivity(intent);
        }
        if(item.getItemId() == R.id.itremovepic){

            sharedPreferences = getSharedPreferences(MyProfile , MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("icon" , "");
            editor.commit();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
