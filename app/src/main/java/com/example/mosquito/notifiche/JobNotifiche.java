package com.example.mosquito.notifiche;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;
import com.example.mosquito.web.Parser;

import android.app.job.JobParameters;
import android.app.job.JobService;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class JobNotifiche extends JobService {
    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        LinkedList<Fonte> listaNotificabili = Fonti.getInstance().getFonti().stream().filter(f -> f.notifiche).collect(Collectors.toCollection(LinkedList::new));
        if (listaNotificabili.size()>0) {
            new Parser(this).execute(listaNotificabili);
            return true;
        }
        else {
            stopSelf();
            return false;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {return false;}

    public void fineLavoro() {
        jobFinished(params,false);
    }
}
