package top.sogrey.common.utils

//日志相关

//getConfig : 获取 log 配置
//Config.setLogSwitch : 设置 log 总开关
//Config.setConsoleSwitch : 设置 log 控制台开关
//Config.setGlobalTag : 设置 log 全局 tag
//Config.setLogHeadSwitch : 设置 log 头部信息开关
//Config.setLog2FileSwitch : 设置 log 文件开关
//Config.setDir : 设置 log 文件存储目录
//Config.setFilePrefix : 设置 log 文件前缀
//Config.setBorderSwitch : 设置 log 边框开关
//Config.setSingleTagSwitch: 设置 log 单一 tag 开关（为美化 AS 3.1 的 Logcat）
//Config.setConsoleFilter : 设置 log 控制台过滤器
//Config.setFileFilter : 设置 log 文件过滤器
//Config.setStackDeep : 设置 log 栈深度
//Config.setStackOffset : 设置 log 栈偏移
//Config.setSaveDays : 设置 log 可保留天数
//Config.addFormatter : 新增 log 格式化器
//log : 自定义 tag 的 type 日志
//v : tag 为类名的 Verbose 日志
//vTag : 自定义 tag 的 Verbose 日志
//d : tag 为类名的 Debug 日志
//dTag : 自定义 tag 的 Debug 日志
//i : tag 为类名的 Info 日志
//iTag : 自定义 tag 的 Info 日志
//w : tag 为类名的 Warn 日志
//wTag : 自定义 tag 的 Warn 日志
//e : tag 为类名的 Error 日志
//eTag : 自定义 tag 的 Error 日志
//a : tag 为类名的 Assert 日志
//aTag : 自定义 tag 的 Assert 日志
//file : log 到文件
//json : log 字符串之 json
//xml : log 字符串之 xml