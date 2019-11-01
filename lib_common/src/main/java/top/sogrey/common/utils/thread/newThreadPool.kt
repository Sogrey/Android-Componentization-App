package top.sogrey.common.utils.thread

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 * 线程池
 */
private val newThreadPool by lazy {
    Executors.newCachedThreadPool(object : ThreadFactory {
        var count = 0
        override fun newThread(r: Runnable?): Thread {
            println("新开的线程: newThreadPool_$count")
            val thread = Thread(r, "newThreadPool_${count++}")
            thread.setUncaughtExceptionHandler { t, e -> //处理非正常的线程中止,多线程中通过trycatch试图捕获线程的异常是不可取的
                println(t.name)
                e.printStackTrace()
            }
            return thread
        }
    })
}

/**
 * 新建线程（线程池管理）
 */
fun newThread(r: () -> Unit) {
    newThreadPool.execute(r)
}


class TestThreadUtil {
    init {
        for (i in 0..5){
            println("for $i")
            newThread {
                //            业务代码
                println("业务代码_$i")
            }
            try {
                Thread.sleep(1000)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}

fun main(args: Array<String>) {
    val testThread = TestThreadUtil()
}