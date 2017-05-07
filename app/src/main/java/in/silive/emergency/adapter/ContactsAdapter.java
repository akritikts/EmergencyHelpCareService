package in.silive.emergency.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import in.silive.emergency.R;
import in.silive.emergency.model.Contact;


public class ContactsAdapter extends ArrayAdapter {

    /** List to store the contacts **/
    private ArrayList<Contact> displayList = new ArrayList();
    private ArrayList<Contact> originalList = new ArrayList();

    /** Boolean value to store whether the adapter will be used for selecting contacts or displaying contacts **/
    private boolean isSelectionAdapter;

    /** Simple constructor **/
    public ContactsAdapter(Context context, int resourceId, boolean isSelectionAdapter) {
        super(context, resourceId);
        this.isSelectionAdapter = isSelectionAdapter;
    }


    /** Getter method for isSelectionAdapter **/
    public boolean isSelectionAdapter() {
        return isSelectionAdapter;
    }

    public int getOriginalListCount(){
        return originalList.size();
    }

    public Contact getOriginalListItem(int index){
        return originalList.get(index);
    }

    /** Setter method for isSelectionAdapter **/
    public void setSelectionAdapter(boolean selectionAdapter) {
        isSelectionAdapter = selectionAdapter;
    }

    /** ViewHolder class will contain the view used to display data **/
    private static class ViewHolder {
        TextView nameView;      // display name of contact
        TextView phoneView;     // display phone of contact
        CheckBox checkBox;      // only for selecting contact

    }


    /** Setter method for displayList **/
    public void setList(ArrayList<Contact> contactList){
        this.originalList = contactList;
    }


    @Override
    public void add(Object object) {
        originalList.add((Contact)object);
        displayList.add((Contact)object);     // add the object to the displayList
    }


    @Override
    public int getCount() {
        return this.displayList.size();
    }

    /**
     * Returns the item stored in the displayList of adapter
     * @param position Position of item in the displayList.
     * @return  Object stored in the displayList. The displayList is of Contact type.
     */
    @Override
    public Object getItem(int position) {
        return displayList.get(position);
    }

    /**
     * Clears the displayList of the adapter. Removes all the contacts saved in the displayList.
     */
    public void clear(){
        originalList.clear();
        displayList.clear();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;         // Each row of the listView
        final ViewHolder holder;

        if(row == null){
            /** create holder and row **/
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.select_contact_row, null);
            holder = new ViewHolder();

            /** Initialize holder **/
            holder.nameView = (TextView)row.findViewById(R.id.tv_contactname);
            holder.phoneView = (TextView)row.findViewById(R.id.tv_contactno);
            holder.checkBox = (CheckBox)row.findViewById(R.id.cb_selectcontact);
            row.setTag(holder);         // can be used to store data within a view
        }
        else        holder = (ViewHolder) row.getTag();

        /** storing data into the holder **/
        final Contact contact = displayList.get(position); // get contact
        holder.nameView.setText(contact.getName());
        holder.phoneView.setText(contact.getPhoneNumber());     // save data into respective views

        /** If adapter will be used for selecting contacts then make checkbox visible else gone **/
        if(this.isSelectionAdapter)
            holder.checkBox.setVisibility(CheckBox.VISIBLE);
        else
            holder.checkBox.setVisibility(CheckBox.GONE);   /** free up space on layout **/

        holder.checkBox.setSelected(contact.isSelected()); // set checkbox selection state according to contacts selected state
        holder.checkBox.setChecked(contact.isSelected());  // set checkbox checked state
        // holder.checkBox.setOnCheckedChangeListener(null);  /** for resetting previous listeners **/


        if(this.isSelectionAdapter) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /** reverse the state of selected contacts **/
                    contact.setSelected(!contact.isSelected());
                    holder.checkBox.setSelected(contact.isSelected()); // set checkbox selection state according to contacts selected state
                    holder.checkBox.setChecked(contact.isSelected());   // set checkbox checked state


                }
            });


            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /** reverse the state of selected contacts **/
                    contact.setSelected(!contact.isSelected());
                    holder.checkBox.setSelected(contact.isSelected()); // set checkbox selection state according to contacts selected state
                    holder.checkBox.setChecked(contact.isSelected());   // set checkbox checked state


                }
            });
        }

        return row;
    }

    /**
     * Updates the display list and filters the contacts based on the name
     * @param filterString      string used for filtering
     */
    public void filter(String filterString){
        ArrayList<Contact> filterList = new ArrayList();

        if(filterString == null || filterString.length() == 0){
            filterList = originalList;
        }
        else {
            String name = filterString.toLowerCase().trim();
            for(int index = 0; index < originalList.size(); index++){
                String contactName =  originalList.get(index).getName().toLowerCase();
                if(contactName.startsWith( name) ){
                    filterList.add(originalList.get(index));
                }
            }
        }

        displayList = filterList;
        notifyDataSetChanged();
    }


}// end of class