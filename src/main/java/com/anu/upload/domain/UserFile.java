package com.anu.upload.domain;

import java.util.ArrayList;
import java.util.List;

public class UserFile {

    private List<PersonalInfo> personalInfoList;
    
    private List<ContactDetails> contactDetailsList;
    
    public UserFile() {
        personalInfoList = new ArrayList<PersonalInfo>();
        contactDetailsList = new ArrayList<ContactDetails>();
    }
    
    public List<PersonalInfo> getPersonalInfoList() {
        return personalInfoList;
    }
    
    public void addPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfoList.add(personalInfo);
    }
    
    public List<ContactDetails> getContactDetailsList() {
        return contactDetailsList;
    }
    
    public void addContactDetailsList(ContactDetails contactDetails) {
        this.contactDetailsList.add(contactDetails);
    }
}
