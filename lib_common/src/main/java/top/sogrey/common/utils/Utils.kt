package top.sogrey.common.utils

import android.os.Handler
import android.os.Looper
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.concurrent.Executors


/**
 * 其他工具方法
 * @author Sogrey
 * @date 2019/10/30
 */
class Utils {
    constructor(){
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }companion object{
        private val UTIL_POOL = Executors.newFixedThreadPool(3)
        private val UTIL_HANDLER = Handler(Looper.getMainLooper())
        abstract class Task<Result>(private val mCallback: Callback<Result>) : Runnable {

            @Volatile
            private var state = NEW

            val isDone: Boolean
                get() = state != NEW

            val isCanceled: Boolean
                get() = state == CANCELLED

            internal abstract fun doInBackground(): Result

            override fun run() {
                try {
                    val t = doInBackground()

                    if (state != NEW) return
                    state = COMPLETING
                    UTIL_HANDLER.post(Runnable { mCallback.onCall(t) })
                } catch (th: Throwable) {
                    if (state != NEW) return
                    state = EXCEPTIONAL
                }

            }

            fun cancel() {
                state = CANCELLED
            }

            companion object {
                private val NEW = 0
                private val COMPLETING = 1
                private val CANCELLED = 2
                private val EXCEPTIONAL = 3
            }
        }

        interface Callback<T> {
            fun onCall(data: T)
        }

        fun <T> doAsync(task: Task<T>): Task<T> {
            UTIL_POOL.execute(task)
            return task
        }

        fun runOnUiThread(runnable: Runnable) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                runnable.run()
            } else {
                Utils.UTIL_HANDLER.post(runnable)
            }
        }
        fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
            UTIL_HANDLER.postDelayed(runnable, delayMillis)
        }
    }
}