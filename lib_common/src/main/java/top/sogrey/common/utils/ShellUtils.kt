package top.sogrey.common.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader


/**
 * 命令行相关
 * @author Sogrey
 * @date 2019/10/30
 */
class ShellUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        private val LINE_SEP = System.getProperty("line.separator")

        /**
         * Execute the command asynchronously.
         *
         * @param command  The command.
         * @param isRooted True to use root, false otherwise.
         * @param callback The callback.
         * @return the task
         */
        fun execCmdAsync(
            command: String,
            isRooted: Boolean,
            callback: Utils.Companion.Callback<CommandResult>
        ): Utils.Companion.Task<CommandResult> {
            return execCmdAsync(arrayOf(command), isRooted, true, callback)
        }

        /**
         * Execute the command asynchronously.
         *
         * @param commands The commands.
         * @param isRooted True to use root, false otherwise.
         * @param callback The callback.
         * @return the task
         */
        fun execCmdAsync(
            commands: List<String>?,
            isRooted: Boolean,
            callback: Utils.Companion.Callback<CommandResult>
        ): Utils.Companion.Task<CommandResult> {
            return execCmdAsync(commands?.toTypedArray(), isRooted, true, callback)
        }

        /**
         * Execute the command asynchronously.
         *
         * @param commands The commands.
         * @param isRooted True to use root, false otherwise.
         * @param callback The callback.
         * @return the task
         */
        fun execCmdAsync(
            commands: Array<String>,
            isRooted: Boolean,
            callback: Utils.Companion.Callback<CommandResult>
        ): Utils.Companion.Task<CommandResult> {
            return execCmdAsync(commands, isRooted, true, callback)
        }

        /**
         * Execute the command asynchronously.
         *
         * @param command         The command.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @param callback        The callback.
         * @return the task
         */
        fun execCmdAsync(
            command: String,
            isRooted: Boolean,
            isNeedResultMsg: Boolean,
            callback: Utils.Companion.Callback<CommandResult>
        ): Utils.Companion.Task<CommandResult> {
            return execCmdAsync(arrayOf(command), isRooted, isNeedResultMsg, callback)
        }

        /**
         * Execute the command asynchronously.
         *
         * @param commands        The commands.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @param callback        The callback.
         * @return the task
         */
        fun execCmdAsync(
            commands: List<String>?,
            isRooted: Boolean,
            isNeedResultMsg: Boolean,
            callback: Utils.Companion.Callback<CommandResult>
        ): Utils.Companion.Task<CommandResult> {
            return execCmdAsync(
                commands?.toTypedArray(),
                isRooted,
                isNeedResultMsg,
                callback
            )
        }

        /**
         * Execute the command asynchronously.
         *
         * @param commands        The commands.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @param callback        The callback.
         * @return the task
         */
        fun execCmdAsync(
            commands: Array<String>?,
            isRooted: Boolean,
            isNeedResultMsg: Boolean,
            callback: Utils.Companion.Callback<CommandResult>
        ): Utils.Companion.Task<CommandResult> {
            return Utils.doAsync(object : Utils.Companion.Task<CommandResult>(callback) {
                override fun doInBackground(): CommandResult {
                    return execCmd(commands, isRooted, isNeedResultMsg)
                }
            })
        }

        /**
         * Execute the command.
         *
         * @param command  The command.
         * @param isRooted True to use root, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(command: String, isRooted: Boolean): CommandResult {
            return execCmd(arrayOf(command), isRooted, true)
        }

        /**
         * Execute the command.
         *
         * @param commands The commands.
         * @param isRooted True to use root, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(commands: List<String>?, isRooted: Boolean): CommandResult {
            return execCmd(commands?.toTypedArray(), isRooted, true)
        }

        /**
         * Execute the command.
         *
         * @param commands The commands.
         * @param isRooted True to use root, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(commands: Array<String>, isRooted: Boolean): CommandResult {
            return execCmd(commands, isRooted, true)
        }

        /**
         * Execute the command.
         *
         * @param command         The command.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(
            command: String,
            isRooted: Boolean,
            isNeedResultMsg: Boolean
        ): CommandResult {
            return execCmd(arrayOf(command), isRooted, isNeedResultMsg)
        }

        /**
         * Execute the command.
         *
         * @param commands        The commands.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(
            commands: List<String>?,
            isRooted: Boolean,
            isNeedResultMsg: Boolean
        ): CommandResult {
            return execCmd(
                commands?.toTypedArray(),
                isRooted,
                isNeedResultMsg
            )
        }

        /**
         * Execute the command.
         *
         * @param commands        The commands.
         * @param isRooted        True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(
            commands: Array<String>?,
            isRooted: Boolean,
            isNeedResultMsg: Boolean
        ): CommandResult {
            var result = -1
            if (commands == null || commands.isEmpty()) {
                return CommandResult(result, "", "")
            }
            var process: Process? = null
            var successResult: BufferedReader? = null
            var errorResult: BufferedReader? = null
            var successMsg: StringBuilder? = null
            var errorMsg: StringBuilder? = null
            var os: DataOutputStream? = null
            try {
                process = Runtime.getRuntime().exec(if (isRooted) "su" else "sh")
                os = DataOutputStream(process!!.outputStream)
                for (command in commands) {
                    if (command == null) continue
                    os.write(command.toByteArray())
                    os.writeBytes(LINE_SEP!!)
                    os.flush()
                }
                os.writeBytes("exit$LINE_SEP")
                os.flush()
                result = process.waitFor()
                if (isNeedResultMsg) {
                    successMsg = StringBuilder()
                    errorMsg = StringBuilder()
                    successResult = BufferedReader(
                        InputStreamReader(process.inputStream, "UTF-8")
                    )
                    errorResult = BufferedReader(
                        InputStreamReader(process.errorStream, "UTF-8")
                    )
                    var line: String = successResult.readLine()
                    successMsg.append(line)
                    while (true) {
                        line = successResult.readLine()
                        if (line != null) {
                            successMsg.append(LINE_SEP).append(line)
                        } else break
                    }
                    line = errorResult.readLine()
                    if (line != null) {
                        errorMsg.append(line)
                        while (true) {
                            line = errorResult.readLine()
                            if (line != null) {
                                errorMsg.append(LINE_SEP).append(line)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    if (os != null) {
                        os!!.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                try {
                    if (successResult != null) {
                        successResult!!.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                try {
                    if (errorResult != null) {
                        errorResult!!.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                process?.destroy()
            }
            return CommandResult(
                result,
                successMsg?.toString() ?: "",
                errorMsg?.toString() ?: ""
            )
        }

        /**
         * The result of command.
         */
        class CommandResult(var result: Int, var successMsg: String, var errorMsg: String) {

            override fun toString(): String {
                return "result: " + result + "\n" +
                        "successMsg: " + successMsg + "\n" +
                        "errorMsg: " + errorMsg
            }
        }
    }
}