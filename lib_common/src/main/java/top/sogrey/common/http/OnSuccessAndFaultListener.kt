package top.sogrey.common.http

interface OnSuccessAndFaultListener {
    fun onSuccess(result:String)

    fun onFault(errorMsg: String)
}