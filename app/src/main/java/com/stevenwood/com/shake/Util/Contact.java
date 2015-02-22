package com.stevenwood.com.shake.Util;

/**
 * Created by Temp on 2/22/2015.
 */
public class Contact {

    private String id;
    private String name;
    private String phoneNumber;
    private String address;
    private String company;
    private String email;

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    public String getphoneNumber(){
        return phoneNumber;
    }

    public String getAddress(){
        return address;
    }

    public String getCompany(){
        return company;
    }

    public String getEmail(){
        return email;
    }

    public void setId(String id){
        this.id = id;
    }
    public void setCompany(String company){
        this.company = company;
    }
    public void setName(String name){
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setAddres(String address){
        this.address = address;
    }

    public void setEmail(String email){
        this.email = email;
    }


}
