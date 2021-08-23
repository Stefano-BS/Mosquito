package com.example.mosquito;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;
import com.example.mosquito.notifiche.JobNotifiche;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

public class Mosquito extends android.app.Application {
    public static Context context;
    public static final int JOB_NOTIFICHE_ID = 325678;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public void onTerminate() {
        context = null;
        super.onTerminate();
    }

    public static boolean internet() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
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

        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (!ciSonoFontiNotificabili && jobScheduler.getPendingJob(JOB_NOTIFICHE_ID) != null) jobScheduler.cancel(JOB_NOTIFICHE_ID);
        else if (ciSonoFontiNotificabili && jobScheduler.getPendingJob(JOB_NOTIFICHE_ID) == null)
                jobScheduler.schedule(new JobInfo.Builder(JOB_NOTIFICHE_ID, new ComponentName(context, JobNotifiche.class))
                        .setPeriodic(Math.max(JobInfo.getMinPeriodMillis(), R.integer.timeout_notifiche), JobInfo.getMinFlexMillis()) // Su questa macchina il limite è 900k, ovvero 15 minuti
                        .setRequiresDeviceIdle(false)
                        .setEstimatedNetworkBytes(10000, 1000)
                        /*.setRequiredNetwork(new NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_FOREGROUND)
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build())*/
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                        .setPersisted(true).build());
    }
}