package com.example.bookofbooks.Models;

public class User {

    private String firstName;
    private String lastName;
    private String country;
    private String email;

    public User(String firstName, String lastName, String country, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
