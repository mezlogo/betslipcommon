@file:JsExport

package betslipcommon

import kotlin.js.JsExport

data class ResponseDataClass(val msg: String, val code: Int)

interface Callback {
    fun onCall(msg: String): String
}

fun commonCode(callback: Callback) = ResponseDataClass(callback.onCall("fromCommon"), 2)