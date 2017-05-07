package in.silive.emergency.model;



import java.util.ArrayList;

/** This class stores basic information of a contact. It includes name, phone number.**/
public class Contact {

    private String name;            // name of contact
    private String phoneNumber;     // phone number of contact
    private boolean selected;       // used to check whether the contact is selected. Useful in selecting contacts.

    /** Simple Constructor. By default all contacts are not selected **/
    public Contact(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.selected = false;
    }

    /** Getter method for name **/
    public String getName() {
        return this.name;
    }

    /** Setter method for name **/
    public void setName(String name) {
        this.name = name;
    }

    /** Getter method for phone number **/
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /** Setter method for phone number **/
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    /** Getter method for selected **/
    public boolean isSelected(){
        return this.selected;
    }

    /** Setter method for selected **/
    public void setSelected(boolean selectedStatus){
        this.selected = selectedStatus ;
    }


    /**
     * Checks whethe this contact is present in an Array List or not.
     * @param contactList   List in which the array needs to be checked.
     * @return  true if present else false
     */
    public boolean isInList(ArrayList<Contact> contactList){
        for(int index = 0; index < contactList.size(); index++){
            Contact tempContact = contactList.get(index);
            if(tempContact.getName().equals(this.getName()) &&
                    tempContact.getPhoneNumber().equals(this.getPhoneNumber()))
                return true;
        }
        return false;

    }

}




