package com.example.finalmp;

public class UserData {
    private String fullname;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private String profilePicUrl;

    public UserData(String fullname, String email, String phone, String address, String gender) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
    }


    public UserData(){}


    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}