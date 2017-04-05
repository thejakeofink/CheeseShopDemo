package com.vikingsen.cheesedemo.job;


import com.evernote.android.job.JobRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppJobScheduler {

    @Inject
    AppJobScheduler() {
        // Dagger Constructor
    }

    public void scheduleCommentSync() {
        new JobRequest.Builder(CommentSyncJob.TAG)
                .setUpdateCurrent(true)
                .setPersisted(true)
                .setExecutionWindow(CommentSyncJob.EXECUTION_WINDOW_START, CommentSyncJob.EXECUTION_WINDOW_END)
                .setBackoffCriteria(CommentSyncJob.BACK_OFF_MS, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
    }

}
