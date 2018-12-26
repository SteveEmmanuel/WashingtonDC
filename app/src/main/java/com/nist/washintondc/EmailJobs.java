package com.nist.washintondc;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "email_jobs")
public class EmailJobs {
    @Id(autoincrement = true)
    private Long id;

    private String file_path;

    private Boolean completed;

    @Generated(hash = 1964613144)
    public EmailJobs(Long id, String file_path, Boolean completed) {
        this.id = id;
        this.file_path = file_path;
        this.completed = completed;
    }

    @Generated(hash = 235649079)
    public EmailJobs() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFile_path() {
        return this.file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public Boolean getCompleted() {
        return this.completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
