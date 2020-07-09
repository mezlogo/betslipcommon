package betslipcommon

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

private val betslipModel = BetslipModelCommonImpl()

@JsExport
class BetslipModelJs : BetslipModel by betslipModel {
    @JsName("addChoice")
    fun addChoice(choice: Choice) = GlobalScope.promise { betslipModel.addChoice(choice) }

    @JsName("removeChoice")
    fun removeChoice(selectionRef: SelectionRef) = GlobalScope.promise { betslipModel.removeChoice(selectionRef) }
}