package top.sogrey.common.utils

import android.app.Fragment
import android.content.Context
import android.widget.Toast
import org.jetbrains.anko.AnkoContext

//吐司相关

//setGravity : 设置吐司位置
//setBgColor : 设置背景颜色
//setBgResource : 设置背景资源
//setMsgColor : 设置消息颜色
//setMsgTextSize : 设置消息字体大小
//showShort : 显示短时吐司
//showLong : 显示长时吐司
//showCustomShort: 显示短时自定义吐司
//showCustomLong : 显示长时自定义吐司
//cancel : 取消吐司显示


//inline fun AnkoContext<*>.toast(textResource: Int) = ctx.toast(textResource)
//inline fun Fragment.toast(textResource: Int): Unit = activity.toast(textResource)
//
//fun Context.toast(textResource: Int) = Toast.makeText(this, textResource, Toast.LENGTH_SHORT).show()
//
//
//inline fun AnkoContext<*>.toast(text: CharSequence) = ctx.toast(text)
//inline fun Fragment.toast(text: CharSequence): Unit = activity.toast(text)
//
//fun Context.toast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
//
//
//inline fun AnkoContext<*>.longToast(textResource: Int) = ctx.longToast(textResource)
//inline fun Fragment.longToast(textResource: Int): Unit = activity.longToast(textResource)
//
//fun Context.longToast(textResource: Int) = Toast.makeText(this, textResource, Toast.LENGTH_LONG).show()
//
//
//inline fun AnkoContext<*>.longToast(text: CharSequence) = ctx.longToast(text)
//inline fun Fragment.longToast(text: CharSequence): Unit = activity.longToast(text)
//
//fun Context.longToast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()