package top.sogrey.common.utils

import android.content.Intent
import android.content.ComponentName
import android.content.Context
import android.os.StrictMode
import android.os.Build
import android.net.Uri
import android.text.TextUtils
import org.jetbrains.anko.toast
import java.io.File

/**
 * 分享
 * @author Sogrey
 * @date 2019/10/26
 */
open class ShareFileUtils {
    /**
     * 分享文本
     *
     * @param context
     * @param path
     */
    fun shareUrl(context: Context, path: String,title:String="分享") {
        if (TextUtils.isEmpty(path)) {
            return
        }

        checkFileUriExposure()

        val it = Intent(Intent.ACTION_SEND)
        it.putExtra(Intent.EXTRA_TEXT, path)
        it.type = "text/plain"
        context.startActivity(Intent.createChooser(it, title))
    }

    /**
     * 分享文件
     *
     * @param context
     * @param path
     */
    fun shareFile(context: Context, path: String,title:String="分享") {
        if (TextUtils.isEmpty(path)) {
            return
        }

        checkFileUriExposure()

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(path)))  //传输图片或者文件 采用流的方式
        intent.type = "*/*"   //分享文件
        context.startActivity(Intent.createChooser(intent, title))
    }

    /**
     * 分享单张图片
     *
     * @param context
     * @param path
     */
    fun shareImage(context: Context, path: String) {
        shareImage(context, path, null, null, null)
    }

    /**
     * 分享多张图片
     *
     * @param context
     * @param pathList
     */
    fun shareImage(context: Context, pathList: List<String>) {
        shareImage(context, null, pathList, null, null)
    }

    /**
     * 分享到微信好友，单图
     */
    fun shareImageToWeChat(context: Context, path: String) {
        //判断是否安装微信，如果没有安装微信 又没有判断就直达微信分享是会挂掉的
        if (!AppUtils.isAppInstalled(context, "com.tencent.mm")) {
            context.toast("您还没有安装微信")
            return
        }
        shareImage(context, path, null, "com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
    }

    /**
     * 分享到微信好友，多图
     */
    fun shareImageToWeChat(context: Context, pathList: List<String>) {
        //判断是否安装微信，如果没有安装微信 又没有判断就直达微信分享是会挂掉的
        if (!AppUtils.isAppInstalled(context, "com.tencent.mm")) {
            context.toast("您还没有安装微信")
            return
        }
        shareImage(context, null, pathList, "com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
    }

    /**
     * 分享到微信朋友圈，单图
     */
    fun shareImageToWeChatFriend(context: Context, path: String) {
        if (!AppUtils.isAppInstalled(context, "com.tencent.mm")) {
            context.toast("您还没有安装微信")
            return
        }
        shareImage(context, path, null, "com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI")
    }

    /**
     * 分享到微信朋友圈，多图
     */
    fun shareImageToWeChatFriend(context: Context, pathList: List<String>) {
        if (!AppUtils.isAppInstalled(context, "com.tencent.mm")) {
            context.toast("您还没有安装微信")
            return
        }
        shareImage(context, null, pathList, "com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI")
    }

    /**
     * 分享图片给QQ好友，单图
     */
    fun shareImageToQQ(context: Context, path: String) {
        if (!AppUtils.isAppInstalled(context, "com.tencent.mobileqq")) {
            context.toast("您还没有安装QQ")
            return
        }
        shareImage(context, path, null, "com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")
    }


    /**
     * 分享图片给QQ好友，多图
     */
    fun shareImageToQQ(context: Context, pathList: List<String>) {
        if (!AppUtils.isAppInstalled(context, "com.tencent.mobileqq")) {
            context.toast("您还没有安装QQ")
            return
        }
        shareImage(context, null, pathList, "com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")
    }


    /**
     * 分享图片到QQ空间，单图
     */
    fun shareImageToQZone(context: Context, path: String) {
        if (!AppUtils.isAppInstalled(context, "com.qzone")) {
            context.toast("您还没有安装QQ空间")
            return
        }
        shareImage(context, path, null, "com.qzone", "com.qzonex.module.operation.ui.QZonePublishMoodActivity")
    }


    /**
     * 分享图片到QQ空间，多图
     */
    fun shareImageToQZone(context: Context, pathList: List<String>) {
        if (!AppUtils.isAppInstalled(context, "com.qzone")) {
            context.toast("您还没有安装QQ空间")
            return
        }
        shareImage(context, null, pathList, "com.qzone", "com.qzonex.module.operation.ui.QZonePublishMoodActivity")
    }

    /**
     * 分享图片到微博，单图
     */
    fun shareImageToWeibo(context: Context, path: String) {
        if (!AppUtils.isAppInstalled(context, "com.sina.weibo")) {
            context.toast("您还没有安装新浪微博")
            return
        }
        shareImage(context, path, null, "com.sina.weibo", "com.sina.weibo.EditActivity")
    }


    /**
     * 分享图片到微博，多图
     */
    fun shareImageToWeibo(context: Context, pathList: List<String>) {
        if (!AppUtils.isAppInstalled(context, "com.sina.weibo")) {
            context.toast("您还没有安装新浪微博")
            return
        }
        shareImage(context, null, pathList, "com.sina.weibo", "com.sina.weibo.EditActivity")
    }
    /**
     * 分享前必须执行本代码，主要用于兼容SDK18以上的系统
     */
    private fun checkFileUriExposure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
        }
    }

    /**
     * @param context  上下文
     * @param path     不为空的时候，表示分享单张图片，会检验图片文件是否存在
     * @param pathList 不为空的时候表示分享多张图片，会检验每一张图片是否存在
     * @param pkg      分享到的指定app的包名
     * @param cls      分享到的页面（微博不需要指定页面）
     */
    private fun shareImage(context: Context, path: String?, pathList: List<String>?, pkg: String?, cls: String?) {
        if (path == null && pathList == null) {
            context.toast("找不到您要分享的图片文件")
            return
        }

        checkFileUriExposure()

        try {
            if (path != null) {
                //单张图片
                if (!FileKtUtils.isFile(path)) {
                    context.toast("图片不存在，请检查后重试")
                    return
                }

                val intent = Intent()
                if (pkg != null && cls != null) {
                    //指定分享到的app
                    if (pkg == "com.sina.weibo") {
                        //微博分享的需要特殊处理
                        intent.setPackage(pkg)
                    } else {
                        val comp = ComponentName(pkg, cls)
                        intent.component = comp
                    }
                }
                intent.action = Intent.ACTION_SEND
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(path)))
                intent.type = "image/*"   //分享文件
                context.startActivity(Intent.createChooser(intent, "分享"))
            } else {
                //多张图片
                val uriList = ArrayList<Uri>()
                for (i in pathList!!.indices) {
                    if (!FileKtUtils.isFile(pathList[i])) {
                        context.toast("第" + (i + 1) + "张图片不存在，请检查后重试")
                        return
                    }
                    uriList.add(Uri.fromFile(File(pathList[i])))
                }

                val intent = Intent()

                if (pkg != null && cls != null) {
                    //指定分享到的app
                    if (pkg == "com.sina.weibo") {
                        //微博分享的需要特殊处理
                        intent.setPackage(pkg)
                    } else {
                        val comp = ComponentName(pkg, cls)
                        intent.component = comp
                    }
                }
                intent.action = Intent.ACTION_SEND_MULTIPLE
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.type = "image/*"
                context.startActivity(Intent.createChooser(intent, "分享"))
            }

        } catch (e: Exception) {
            context.toast("分享失败，未知错误")
        }

    }

}