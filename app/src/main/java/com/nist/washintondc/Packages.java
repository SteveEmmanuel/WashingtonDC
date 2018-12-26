package com.nist.washintondc;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by oldtrafford on 03/06/18.
 */

@Entity(nameInDb = "packages")
public class Packages {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "name")
    private String name;

    private long priceId;

    @ToOne(joinProperty = "priceId")
    private Price price;

    @Property(nameInDb = "info")
    private String info;

    @Property(nameInDb = "image")
    private String image;

    @Property(nameInDb = "type")
    private String type;

    Boolean selected = false;

    private int  time;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1141293423)
    private transient PackagesDao myDao;

    @Generated(hash = 1551885776)
    public Packages(Long id, String name, long priceId, String info, String image, String type,
            Boolean selected, int time) {
        this.id = id;
        this.name = name;
        this.priceId = priceId;
        this.info = info;
        this.image = image;
        this.type = type;
        this.selected = selected;
        this.time = time;
    }

    @Generated(hash = 688242455)
    public Packages() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPriceId() {
        return this.priceId;
    }

    public void setPriceId(long priceId) {
        this.priceId = priceId;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getSelected() {
        return this.selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Packages setValues( String name, long priceId, String info,
                               Boolean selected, String image, String type, int time) {
        this.name = name;
        this.priceId = priceId;
        this.info = info;
        this.selected = selected;
        this.image = image;
        this.type = type;
        this.time = time;
        return this;
    }

    @Generated(hash = 1564144330)
    private transient Long price__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1160510595)
    public Price getPrice() {
        long __key = this.priceId;
        if (price__resolvedKey == null || !price__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PriceDao targetDao = daoSession.getPriceDao();
            Price priceNew = targetDao.load(__key);
            synchronized (this) {
                price = priceNew;
                price__resolvedKey = __key;
            }
        }
        return price;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 993863417)
    public void setPrice(@NotNull Price price) {
        if (price == null) {
            throw new DaoException(
                    "To-one property 'priceId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.price = price;
            priceId = price.getPriceId();
            price__resolvedKey = priceId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 93078116)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPackagesDao() : null;
    }



}

