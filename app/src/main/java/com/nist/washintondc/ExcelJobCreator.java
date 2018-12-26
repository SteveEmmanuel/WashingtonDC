package com.nist.washintondc;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class ExcelJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case SendExcelEmailJob.TAG:
                return new SendExcelEmailJob();
            case PrepareExcelForEmail.TAG:
                return new PrepareExcelForEmail();
            default:
                return null;
        }
    }
}