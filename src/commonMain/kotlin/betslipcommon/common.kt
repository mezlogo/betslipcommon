package betslipcommon

expect annotation class CommonJsExport()

@CommonJsExport
data class ResponseDataClass(val msg: String, val code: Int)

@CommonJsExport
interface Callback { fun onCall(msg: String) : String }

@CommonJsExport
fun commonCode(callback: Callback) = ResponseDataClass(callback.onCall("fromCommon"), 2)