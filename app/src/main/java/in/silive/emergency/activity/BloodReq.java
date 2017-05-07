package in.silive.emergency.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import in.silive.emergency.R;
import in.silive.emergency.adapter.ConnectContactsAdapter;
import in.silive.emergency.database.DatabaseHandler;
import in.silive.emergency.fragment.BloodFragment;
import in.silive.emergency.fragment.ContactsFragment;
import in.silive.emergency.model.Contact;

public class BloodReq extends FragmentActivity {
    ConnectContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_req);
        ListView listView = (ListView)findViewById(R.id.connect_contact);
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        ArrayList<Contact> contactList = dbHandler.getContactList();
        adapter = new ConnectContactsAdapter(this, R.layout.connect_contact_row);

        for(int index = 0; index < contactList.size(); index++){
            adapter.add(contactList.get(index));
        }
        listView.setAdapter(adapter);
    }
    }

