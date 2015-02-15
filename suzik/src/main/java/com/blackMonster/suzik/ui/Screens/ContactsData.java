package com.blackMonster.suzik.ui.Screens;

/**
 * Created by home on 2/14/2015.
 */
public class ContactsData {
    String number;
    String contactName;
    Boolean filterStatus;

    public ContactsData(String number, String contactName, Boolean filterStatus) {
        this.number = number;
        this.contactName = contactName;
        this.filterStatus = filterStatus;
    }

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
}
