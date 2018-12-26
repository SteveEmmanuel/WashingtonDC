package com.nist.washintondc;

/**
 * Created by oldtrafford on 09/07/18.
 */

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "email_recipients")
public class EmailRecipients {

    @Id(autoincrement = true)
    private Long id;

    private String email;

    @Generated(hash = 747158204)
    public EmailRecipients(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    @Generated(hash = 1494833528)
    public EmailRecipients() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
