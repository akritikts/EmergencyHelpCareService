package in.silive.emergency.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import in.silive.emergency.R;
import in.silive.emergency.adapter.ConnectContactsAdapter;
import in.silive.emergency.database.DatabaseHandler;
import in.silive.emergency.model.Contact;

/**
 * A simple {@link Fragment} subclass.
 */
public class BloodFragment extends Fragment {


    ConnectContactsAdapter adapter;
    public BloodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contactView = inflater.inflate(R.layout.fragment_blood, container, false);
        ListView listView = (ListView)contactView.findViewById(R.id.lv_connect_contact);
        DatabaseHandler dbHandler = new DatabaseHandler(getActivity());

        ArrayList<Contact> contactList = dbHandler.getContactList();
        adapter = new ConnectContactsAdapter(getActivity(), R.layout.connect_contact_row);

        for(int index = 0; index < contactList.size(); index++){
            adapter.add(contactList.get(index));
        }
        listView.setAdapter(adapter);

        return listView;
    }

}
