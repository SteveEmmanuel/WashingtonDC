package com.nist.washintondc;

/**
 * Created by oldtrafford on 28/06/18.
 */

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "expenses")
public class Expenses {
    @Id(autoincrement = true)
    private Long id;

    private String date;

    private String time;

    private String particulars;

    private String amount;

    private String type; //credit or debit

    @Generated(hash = 14110034)
    public Expenses(Long id, String date, String time, String particulars,
            String amount, String type) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.particulars = particulars;
        this.amount = amount;
        this.type = type;
    }

    @Generated(hash = 227211866)
    public Expenses() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getParticulars() {
        return this.particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
