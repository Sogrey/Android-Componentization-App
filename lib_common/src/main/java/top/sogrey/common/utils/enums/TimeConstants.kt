package top.sogrey.common.utils.enums

enum class TimeConstants(val value: Long) {
    /**毫秒*/
    SEC1(1),
    /**秒*/
    SEC(1000),
    /**分钟*/
    MIN(60000),
    /**小时*/
    HOUR(3600000),
    /**天*/
    DAY(86400000)
}