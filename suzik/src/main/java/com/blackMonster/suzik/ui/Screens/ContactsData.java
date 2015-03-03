package com.blackMonster.suzik.ui.Screens;

/**
 * Created by home on 2/14/2015.
 */
public class ContactsData implements Comparable<ContactsData> {
   private String number;
   private String contactName;
   private Boolean filterStatus;
   private Boolean lock;
   private int type;

    public static final int TYPE_ERROR=0;
    public static final int TYPE_DATA=1;
    public static final int TYPE_SEPERATOR=2;

    @Override
    public String toString() {
        return "ContactsData{" +
                "number='" + number + '\'' +
                ", contactName='" + contactName + '\'' +
                ", filterStatus=" + filterStatus +
                ", lock=" + lock +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactsData that = (ContactsData) o;

        if (contactName != null ? !contactName.equals(that.contactName) : that.contactName != null)
            return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = number != null ? number.hashCode() : 0;
        result = 31 * result + (contactName != null ? contactName.hashCode() : 0);
        return result;
    }

    public ContactsData(String number, String contactName, Boolean filterStatus) {
        this.number = number;
        this.contactName = contactName;
        this.filterStatus = filterStatus;
    }

    public ContactsData(String number, String contactName, Boolean filterStatus, int typeData) {

        this.number = number;
        this.contactName = contactName;
        this.filterStatus = filterStatus;
        this.type=typeData;
    }

    public ContactsData(String number, String contactName, Boolean filterStatus, int type, Boolean lock) {
        this.number = number;
        this.contactName = contactName;
        this.filterStatus = filterStatus;
        this.type=type;
        this.lock=lock;
    }

    public int getType() {   return type;  }

    public void setType(int type) {   this.type = type;    }

    public Boolean isLocked() {   return lock;   }

    public void setLock(Boolean lock) {  this.lock = lock;    }

    public Boolean getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(Boolean filterStatus) {
        this.filterStatus = filterStatus;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }



    @Override
    public int compareTo(ContactsData another) {
        return contactName.compareToIgnoreCase(another.contactName);
    }
}
