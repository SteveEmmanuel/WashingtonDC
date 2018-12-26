package com.nist.washintondc;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Used for migrating data from one schema version to another.
 */

public class DatabaseUpgradeHelper extends DaoMaster.OpenHelper {

    public DatabaseUpgradeHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        List<Migration> migrations = getMigrations();

        // Only run migrations past the old version
        for (Migration migration : migrations) {
            if (oldVersion < migration.getVersion()) {
                exportDatabase(migration.getVersion());
                migration.runMigration(db);
            }
        }
    }

    private List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new MigrationV2());
        migrations.add(new MigrationV3());
        migrations.add(new MigrationV4());
        migrations.add(new MigrationV5());
        migrations.add(new MigrationV6());

        // Sorting just to be safe, in case other people add migrations in the wrong order.
        Comparator<Migration> migrationComparator = new Comparator<Migration>() {
            @Override
            public int compare(Migration m1, Migration m2) {
                return m1.getVersion().compareTo(m2.getVersion());
            }
        };
        Collections.sort(migrations, migrationComparator);

        return migrations;
    }

   private static class MigrationV2 implements Migration {

       @Override
       public Integer getVersion() {
           return 2;
       }

       @Override
       public void runMigration(Database db) {
           //Adding new table
           EmailJobsDao.createTable(db, true);

       }
   }

    private static class MigrationV3 implements Migration {

        @Override
        public Integer getVersion() {
            return 3;
        }

        @Override
        public void runMigration(Database db) {
            //Adding new column to packages(time)
            db.execSQL("ALTER TABLE " + PackagesDao.TABLENAME + " ADD COLUMN " + PackagesDao.Properties.Time.columnName + " INTEGER DEFAULT 1260");
        }
    }

    private static class MigrationV4 implements Migration {

        @Override
        public Integer getVersion() {
            return 4;
        }

        @Override
        public void runMigration(Database db) {
            //Adding new column to packages(time)
            String orderTable = OrdersDao.TABLENAME;
            db.execSQL("ALTER TABLE " + orderTable+ " RENAME TO "+orderTable+"_old");
            OrdersDao.createTable(db, false);
            db.execSQL("INSERT INTO ORDERS (_id, customer_id, package_id, amount, discount, date, start_time, end_time) " +
                    "SELECT _id, customer_id,package_id, amount, discount, date, time, time from "+orderTable+"_old");
            db.execSQL("DROP TABLE "+orderTable+"_old");
        }
    }

    private static class MigrationV5 implements Migration {

        @Override
        public Integer getVersion() {
            return 5;
        }

        @Override
        public void runMigration(Database db) {
            //Adding new column to packages(time)
            db.execSQL("ALTER TABLE " + CustomersDao.TABLENAME + " ADD COLUMN " + CustomersDao.Properties.Email.columnName + " VARCHAR DEFAULT 'washingtondccwash@gmail.com'");
            db.execSQL("ALTER TABLE " + CustomersDao.TABLENAME + " ADD COLUMN " + CustomersDao.Properties.Registration.columnName + " VARCHAR DEFAULT Kl");
            db.execSQL("UPDATE " + CustomersDao.TABLENAME + " SET " + CustomersDao.Properties.Registration.columnName +" = " + CustomersDao.Properties.CarName.columnName);

        }
    }

    private static class MigrationV6 implements Migration {

        @Override
        public Integer getVersion() {
            return 6;
        }

        @Override
        public void runMigration(Database db) {
            //Adding new table
            PendingOrdersDao.createTable(db, true);

        }
    }

    private static class MigrationV7 implements Migration {

        @Override
        public Integer getVersion() {
            return 7;
        }

        @Override
        public void runMigration(Database db) {
            //Adding new column to packages(time)
            db.execSQL("ALTER TABLE " + CustomersDao.TABLENAME + " ADD COLUMN " + CustomersDao.Properties.Email.columnName + " VARCHAR DEFAULT 'washingtondccwash@gmail.com'");
            db.execSQL("ALTER TABLE " + CustomersDao.TABLENAME + " ADD COLUMN " + CustomersDao.Properties.Registration.columnName + " VARCHAR DEFAULT Kl");
            db.execSQL("UPDATE " + CustomersDao.TABLENAME + " SET " + CustomersDao.Properties.Registration.columnName +" = " + CustomersDao.Properties.CarName.columnName);

        }
    }

    private interface Migration {
        Integer getVersion();

        void runMigration(Database db);
    }

    public void exportDatabase(final int version) {

            try {
            File data = Environment.getDataDirectory();

            File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File exportDir = new File(sdCard.getAbsolutePath() + "/WashingtonDC");

            if (exportDir.canWrite()) {
                String currentDBPath = "//data//"+"com.nist.washintondc"+"//databases//"+"washinton-dc-db"+"";
                String backupDBPath = "version" + version + ".db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(exportDir, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            } catch (Exception e) {
                Log.v("exportDB", e.getMessage());
            }
        }
}