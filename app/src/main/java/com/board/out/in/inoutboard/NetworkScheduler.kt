package com.board.out.`in`.inoutboard

import android.app.job.JobParameters
import android.app.job.JobService
import com.board.out.`in`.inoutboard.WifiStatusReceiver.Companion.getPositionAndSendIntent

class NetworkScheduler : JobService() {
    override fun onStopJob(params: JobParameters?): Boolean = true

    override fun onStartJob(params: JobParameters?): Boolean {
        getPositionAndSendIntent(applicationContext)
        return true
    }

}