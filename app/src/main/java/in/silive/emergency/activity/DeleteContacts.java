package in.silive.emergency.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import in.silive.emergency.R;
import in.silive.emergency.adapter.ContactsAdapter;
import in.silive.emergency.database.DatabaseHandler;
import in.silive.emergency.model.Contact;


public class DeleteContacts extends AppCompatActivity {

    ContactsAdapter adapter;
    Button deleteButton;
    ListView listView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contacts);
        deleteButton = (Button)findViewById(R.id.bt_delete);
        listView = (ListView)findViewById(R.id.lv_delete_contacts);
        adapter = new ContactsAdapter(getApplicationContext(), R.layout.select_contact_row, true);


        toolbar = (Toolbar) findViewById(R.id.tbdelete);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delete Contact");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /** Get the contact list from database **/
        final DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
        ArrayList<Contact> contactList = dbHandler.getContactList();

        /** add to contacts adapter **/
        for(int index = 0; index < contactList.size(); index++){
            adapter.add(contactList.get(index));
        }
        listView.setAdapter(adapter);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.bt_delete) {
                    int count = 0;
                    int indexPositions[] = new int[adapter.getCount()];

                    /** count the number of selected contacts and store their positions **/
                    for (int index = 0; index < adapter.getCount(); index++) {
                        Contact contact = (Contact) adapter.getItem(index);
                        if (contact.isSelected()) {
                            indexPositions[count++] = index;
                        }
                    }

                    /** If all contacts not selected **/
                    if (count < adapter.getCount()) {
                        for (int index = 0; index < count; index++)
                            dbHandler.deleteContact((Contact) adapter.getItem(indexPositions[index]));
                        Intent intent = new Intent(DeleteContacts.this, FragmentCallingActivity.class);
                        startActivity(intent);
                    } else {
                        /** show message **/
                        Toast.makeText(getApplicationContext(), "Cannot delete all emergency contacts.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }




}