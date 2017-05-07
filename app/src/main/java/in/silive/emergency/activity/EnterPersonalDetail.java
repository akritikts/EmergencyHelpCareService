package in.silive.emergency.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import in.silive.emergency.R;


public class EnterPersonalDetail extends AppCompatActivity implements View.OnClickListener {
    EditText mobile,name,dob,address,inheriteddiseases,diseases;
    Button submit;
    SharedPreferences sharedPreferences;
    String MyProfile = "Profile";
    Toolbar toolbar;
    Spinner bloodgroup;

    TextInputLayout inputLayoutName,inputLayoutMobile;

    String Sname,Smobile,Saddress,Sblood,Sdob,Sinherited,Sdiseases;

    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.silive.emergency.R.layout.enterpersonaldetail);

        toolbar = (Toolbar) findViewById(R.id.tbPersonal);
        setSupportActionBar(toolbar);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);


        mobile = (EditText) findViewById(R.id.etmobile);
        name = (EditText) findViewById(R.id.etname);
        dob = (EditText) findViewById(R.id.etdob);
        address = (EditText) findViewById(R.id.etaddress);
        bloodgroup = (Spinner) findViewById(R.id.sp_bloodgroup);
        inheriteddiseases = (EditText) findViewById(R.id.etinheriteddiseases);
        diseases = (EditText) findViewById(R.id.etdiseases);

        name.addTextChangedListener(new MyTextWatcher(name));
        mobile.addTextChangedListener(new MyTextWatcher(mobile));


        submit = (Button) findViewById(R.id.btSubmit);

        //accessing MyProfile file
        sharedPreferences = getSharedPreferences(MyProfile, MODE_PRIVATE);
        Sname = sharedPreferences.getString("Name", "");
        Smobile = sharedPreferences.getString("MobileNO", "");
        Saddress = sharedPreferences.getString("Address", "");
        Sdob = sharedPreferences.getString("DOB", "");
        Sblood = sharedPreferences.getString("BloodGroup", "");
        Sinherited = sharedPreferences.getString("InheritedDiseases", "");
        Sdiseases = sharedPreferences.getString("Diseases", "");

        //if name is not null
        if(!Sname.isEmpty())
            name.setText(Sname);
        //if mobile is not null
        if(!Smobile.isEmpty())
            mobile.setText(Smobile);
        //if address is not null
        if(!Saddress.isEmpty())
            address.setText(Saddress);
        //if date of birth is not null
        if(!Sdob.isEmpty())
            dob.setText(Sdob);
        //if inherited diseases is not null
        if(!Sinherited.isEmpty())
            inheriteddiseases.setText(Sinherited);
        //if diseases is not null
        if(!Sdiseases.isEmpty())
            diseases.setText(Sdiseases);

        insertDummyWriteStoragePermission();


        submit.setOnClickListener(this);

        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(b){

                    showDialog(DATE_PICKER_ID);

                }
            }
        });

        //getting year,month,day from calender
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
    }


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        //on submit button click
       if( view.getId() == R.id.btSubmit){

           try  {
               //disabling keyboard
               InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

           } catch (Exception e) {
               e.printStackTrace();
           }
           //getting mobile length
          int mobileNoLength =  mobile.getText().toString().length();
           //if validations are true
           if(validateName() && validateMobile() && mobileNoLength > 7) {


             //putting values through sharedPreference
               sharedPreferences = getSharedPreferences(MyProfile, Context.MODE_PRIVATE);
              //getting sharedPreferences editor
               SharedPreferences.Editor editor = sharedPreferences.edit();
             //putting values
               editor.putString("Name", name.getText().toString());
               editor.putString("MobileNO", mobile.getText().toString());
               editor.putString("DOB", dob.getText().toString());
               editor.putString("Address", address.getText().toString());
               editor.putString("BloodGroup", bloodgroup.getSelectedItem().toString());
               editor.putString("InheritedDiseases", inheriteddiseases.getText().toString());
               editor.putString("Diseases", diseases.getText().toString());

               editor.commit();
startnewactivity();
           }

       }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                //adding date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, pickerListener, 1990, month,1);
                datePickerDialog.getDatePicker().setMinDate(new Date().getDate());
                Calendar calendar = Calendar.getInstance();
                //going 7 years back from current calender
                calendar.add(Calendar.YEAR , -7);
                //converting date to millisecond
                long time = calendar.getTimeInMillis();
                datePickerDialog.getDatePicker().setMaxDate(time);
                return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            dob.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));
            requestFocus(address);


        }
    };
    public void startnewactivity(){
        String edit = "";
        //getting intent
        Intent i=getIntent();
        edit =  i.getStringExtra("mobile");

        if(edit.equals("no")){

            Intent intent = new Intent(this, SelectContacts.class);
            intent.putExtra("contact" , "ba");
            finish();
            startActivity(intent);
        }

        else if(edit.equals("edit")) {
            Intent intent = new Intent(this, FragmentCallingActivity.class);
            finish();
            startActivity(intent);
        }
    }


    private boolean validateName() {
        if (name.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("Enter your full name");
            //requsting focus on editText
            requestFocus(name);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateMobile() {
        int mobileNoLength =  mobile.getText().toString().length();
        if (mobile.getText().toString().trim().isEmpty()) {
            inputLayoutMobile.setError("Enter your Mobile No.");
            //requsting focus on editText
            requestFocus(mobile);
            return false;
        }
           else if( mobileNoLength <= 7 ){
                inputLayoutMobile.setError("Enter your correct Mobile No.");
            //requsting focus on editText
                requestFocus(mobile);
            return false;
        } else {
            inputLayoutMobile.setErrorEnabled(false);
        }

        return true;
    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
            //showing keyboard
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    public void openPic(View view){
        insertDummyReadStoragePermission();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
       // intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }
    final int REQUEST_CODE = 1;
Bitmap bitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // We need to recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
                byte[] image = byteArrayOutputStream.toByteArray();
                String Simage = Base64.encodeToString(image, Base64.DEFAULT);
                //putting values through sharedPreference
                sharedPreferences = getSharedPreferences(MyProfile, Context.MODE_PRIVATE);
                //getting sharedPreferences editor
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //putting values
                editor.putString("icon" , Simage);
                editor.commit();
                stream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private void insertDummyReadStoragePermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(EnterPersonalDetail.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(EnterPersonalDetail.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(EnterPersonalDetail.this,
                        new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);

                return;
            }
            ActivityCompat.requestPermissions(EnterPersonalDetail.this,
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

    }
    final private int REQUEST_CODE_ASK_PERMISSIONS_WRITE = 123;
    private void insertDummyWriteStoragePermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(EnterPersonalDetail.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(EnterPersonalDetail.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(EnterPersonalDetail.this,
                        new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS_WRITE);

                return;
            }
            ActivityCompat.requestPermissions(EnterPersonalDetail.this,
                    new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS_WRITE);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_ASK_PERMISSIONS){}
       else if(requestCode == REQUEST_CODE_ASK_PERMISSIONS_WRITE){

        }
    }
    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.etname:
                    inputLayoutName.setErrorEnabled(false);
                    break;
                case R.id.etmobile:
                    inputLayoutMobile.setErrorEnabled(false);
                    break;

            }
        }
        @Override
        public void afterTextChanged (Editable editable){

        }
    }
}