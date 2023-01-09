package com.example.servicestest

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class MyJobService : JobService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        log("onCreate")
        super.onCreate()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartJob")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutineScope.launch {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                    for (i in 1..5) {
                        log("Page: $page Counter: $i")
                        delay(1000)
                    }
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }
                jobFinished(params, true)
            }
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("DEBUG", "MyJobService: $message")
    }

    companion object {
        const val JOB_ID = 111
        private const val PAGE = "page"

        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE, page)
            }
        }
    }
}