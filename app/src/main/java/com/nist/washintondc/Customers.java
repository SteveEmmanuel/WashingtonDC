package com.nist.washintondc;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by oldtrafford on 05/06/18.
 */

@Entity(nameInDb = "customers")
public class Customers {

    @Id(autoincrement = true)
    private Long customerId;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "car_name")
    private String carName;

    @Property(nameInDb = "phone")
    private String phone;

    @Property(nameInDb = "email")
    private String email;

    @Property(nameInDb = "registration")
    private String registration;

    @Property(nameInDb = "cartype")
    private String carType;



    @Generated(hash = 1769248002)
    public Customers() {
    }


    @Generated(hash = 2086687254)
    public Customers(Long customerId, String name, String carName, String phone,
            String email, String registration, String carType) {
        this.customerId = customerId;
        this.name = name;
        this.carName = carName;
        this.phone = phone;
        this.email = email;
        this.registration = registration;
        this.carType = carType;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCarType() {
        return this.carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Customers setValues(String name, String carName,
                     String phone, String carType) {
        this.name = name;
        this.carName = carName;
        this.phone = phone;
        this.carType = carType;
        return this;
    }


    public String getCarName() {
        return this.carName;
    }


    public void setCarName(String carName) {
        this.carName = carName;
    }


    public String getEmail() {
        return this.email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getRegistration() {
        return this.registration;
    }


    public void setRegistration(String registration) {
        this.registration = registration;
    }

}