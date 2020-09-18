package com.example.coronaapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.auth.User;

public class UserModel implements Parcelable {

    String FirstName;
    String LastName;
    String Email;
    String Gender;
    String BloodGroup;
    String City;
    String UserId;
    String Age;
    String Password;
    String Phone;

    protected UserModel(){}

    protected UserModel(String FirstName, String LastName,
                        String BloodGroup, String Age, String UserId, String Email, String Password, String Phone)
    {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.BloodGroup = BloodGroup;
        this.Age = Age;
        this.UserId = UserId;
        this.Email = Email;
        this.Password = Password;
        this.Phone = Phone;
    }

    protected UserModel(Parcel in) {
        FirstName = in.readString();
        LastName = in.readString();
        Gender = in.readString();
        BloodGroup = in.readString();
        City = in.readString();
        Age = in.readString();
        UserId = in.readString();
        Email = in.readString();
        Password = in.readString();
        Phone = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        BloodGroup = bloodGroup;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(Gender);
        dest.writeString(BloodGroup);
        dest.writeString(City);
        dest.writeString(Age);
        dest.writeString(UserId);
        dest.writeString(Email);
        dest.writeString(Password);
        dest.writeString(Phone);
    }
}
