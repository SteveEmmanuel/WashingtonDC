package com.nist.washintondc;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

@Entity(nameInDb = "pending_orders")
public class PendingOrders {
    @Id(autoincrement = true)
    private Long id;

    private long customerId;

    @ToOne(joinProperty = "customerId")
    private Customers customer;

    private long packageId;

    @ToOne(joinProperty = "packageId")
    private Packages packages;

    @Property(nameInDb = "date")
    private String date;

    @Property(nameInDb = "start_time")
    private String startTime;



    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1396944309)
    private transient PendingOrdersDao myDao;

    @Generated(hash = 569354822)
    public PendingOrders(Long id, long customerId, long packageId, String date,
            String startTime) {
        this.id = id;
        this.customerId = customerId;
        this.packageId = packageId;
        this.date = date;
        this.startTime = startTime;
    }

    @Generated(hash = 428342223)
    public PendingOrders() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getPackageId() {
        return this.packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Generated(hash = 8592637)
    private transient Long customer__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 931076069)
    public Customers getCustomer() {
        long __key = this.customerId;
        if (customer__resolvedKey == null || !customer__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CustomersDao targetDao = daoSession.getCustomersDao();
            Customers customerNew = targetDao.load(__key);
            synchronized (this) {
                customer = customerNew;
                customer__resolvedKey = __key;
            }
        }
        return customer;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1359001982)
    public void setCustomer(@NotNull Customers customer) {
        if (customer == null) {
            throw new DaoException(
                    "To-one property 'customerId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.customer = customer;
            customerId = customer.getCustomerId();
            customer__resolvedKey = customerId;
        }
    }

    @Generated(hash = 2141436743)
    private transient Long packages__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 851401044)
    public Packages getPackages() {
        long __key = this.packageId;
        if (packages__resolvedKey == null || !packages__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PackagesDao targetDao = daoSession.getPackagesDao();
            Packages packagesNew = targetDao.load(__key);
            synchronized (this) {
                packages = packagesNew;
                packages__resolvedKey = __key;
            }
        }
        return packages;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 633109325)
    public void setPackages(@NotNull Packages packages) {
        if (packages == null) {
            throw new DaoException(
                    "To-one property 'packageId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.packages = packages;
            packageId = packages.getId();
            packages__resolvedKey = packageId;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1667961625)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPendingOrdersDao() : null;
    }

}
