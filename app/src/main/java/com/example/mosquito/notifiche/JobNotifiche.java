package com.example.mosquito.notifiche;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;
import com.example.mosquito.web.Parser;

import android.app.job.JobParameters;
import android.app.job.JobService;
import java.util.LinkedList;

public class JobNotifiche extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        //Toast.makeText(getApplicationContext(), "Job started ", Toast.LENGTH_LONG).show();
        LinkedList<Fonte> listaNotificabili = new LinkedList<>();
        for (Fonte f : Fonti.getInstance().getFonti())
            if (f.notifiche) listaNotificabili.add(f);
        if (listaNotificabili.size()>0) new Parser(this).execute(listaNotificabili);
        else stopSelf();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {return false;}
}
