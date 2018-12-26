package com.nist.washintondc;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by oldtrafford on 04/06/18.
 */

@Entity(nameInDb = "prices")
public class Price {

    @Id(autoincrement = true)
    private Long priceId;

    @Property(nameInDb = "suv")
    private Float suv;

    @Property(nameInDb = "hatch")
    private Float hatch;

    @Property(nameInDb = "sedan")
    private Float sedan;

    @Generated(hash = 874179650)
    public Price(Long priceId, Float suv, Float hatch, Float sedan) {
        this.priceId = priceId;
        this.suv = suv;
        this.hatch = hatch;
        this.sedan = sedan;
    }

    @Generated(hash = 812905808)
    public Price() {
    }

    public Long getPriceId() {
        return this.priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public Float getSuv() {
        return this.suv;
    }

    public void setSuv(Float suv) { this.suv = suv; }

    public Float getHatch() {
        return this.hatch;
    }

    public void setHatch(Float hatch) {
        this.hatch = hatch;
    }

    public Float getSedan() {
        return this.sedan;
    }

    public void setSedan(Float sedan) {
        this.sedan = sedan;
    }

    public Price setValues(float hatch, float sedan, float suv){
        this.suv = suv;
        this.hatch = hatch;
        this.sedan = sedan;
        return this;
    }

    public float getPriceByCarType(String cartype){
        switch (cartype.toLowerCase()){
            case "hatchback": return this.getHatch();
            case "suv": return this.getSuv();
            case "sedan": return this.getSedan();
        }
        return 0f;
    }
}