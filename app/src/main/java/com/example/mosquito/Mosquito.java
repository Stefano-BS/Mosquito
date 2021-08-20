package com.example.mosquito;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;
import com.example.mosquito.notifiche.JobNotifiche;

import android.app.Activity;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

public class Mosquito extends Application {
    public static Context context;
    private static Mosquito instance;
    public static final int JOB_NOTIFICHE_ID = 325678;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
    }

    public static boolean internet() {
        ConnectivityManager cm = (ConnectivityManager) instance.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public static float convertDpToPixel(int dp){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void managerJobNotifiche() {
        boolean ciSonoFontiNotificabili = false;
        for (Fonte f : Fonti.getInstance().getFonti())
            if (f.notifiche) {
                ciSonoFontiNotificabili = true;
                break;
            }

        final JobScheduler jobScheduler = (JobScheduler) Mosquito.context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        if (!ciSonoFontiNotificabili && jobScheduler.getPendingJob(Mosquito.JOB_NOTIFICHE_ID) != null) jobScheduler.cancel(Mosquito.JOB_NOTIFICHE_ID);
        else if (ciSonoFontiNotificabili && jobScheduler.getPendingJob(Mosquito.JOB_NOTIFICHE_ID) == null)
                jobScheduler.schedule(new JobInfo.Builder(Mosquito.JOB_NOTIFICHE_ID, new ComponentName(context, JobNotifiche.class))
                        .setPeriodic(R.integer.timeout_notifiche,1000) // Su questa macchina il limite è 900k, ovvero 15 minuti
                        .setRequiresDeviceIdle(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING)
                        .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                        .setPersisted(true).build());
    }
}