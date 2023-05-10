package com.example.testdemo;

import android.net.Uri;

public class user_profile_details {
    String qruri;
    String name;
    String pnumber;
    String address;
    String github;

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public user_profile_details(){

    }

    public String getQruri() {
        return qruri;
    }

    public void setQruri(String qruri) {
        this.qruri = qruri;
    }

    public user_profile_details(String name, String pnumber, String address, String github, String qruri){
        this.name=name;
        this.pnumber=pnumber;
        this.address=address;
        this.github=github;
        this.qruri=qruri;

    }
}
