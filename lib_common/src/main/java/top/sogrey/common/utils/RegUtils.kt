package top.sogrey.common.utils


/**
 * 描述：
 * Created by Sogrey on 2018/12/29.
 */
class RegUtils {
    constructor(){

    }
    companion object {
        val REG_HTTP =
            """(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?"""

        /**
         * 正则匹配
         * @param str 源
         * @param regEx 正则表达式匹配规则
         * @return 返回是否匹配
         */
        fun reg(str: String, regEx: String): Boolean = Regex(regEx).matches(str)

        /**
         * 正则匹配
         * @param str 源
         * @param regEx 正则表达式匹配规则
         * @return 返回所有匹配结果
         */
        fun find(str: String, regEx: String): List<String> =
            Regex(regEx).findAll(str).toList().flatMap(MatchResult::groupValues)

        /**
         * 替换
         */
        fun replace(content: String, strOld: String, strNew: String): String {
            return content.replace(strOld, strNew)
        }

        /**
         * 去掉空格
         */
        fun replaceSpace(content: String): String {
            return replace(content, " ", "")
        }
    }
}