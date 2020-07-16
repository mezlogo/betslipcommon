package betslipcommon

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@JsExport
fun createBetslipModelForDelegation(betslipStorageAo: BetslipStorageAo) = BetslipModelCommonImpl(betslipStorageAo)

@JsExport
class BetslipModelJs(val delegateTo: BetslipModelCommonImpl) : BetslipModel by delegateTo {
    @JsName("addChoice")
    fun addChoice(choice: Choice) = GlobalScope.promise { delegateTo.addChoice(choice) }

    @JsName("removeChoice")
    fun removeChoice(selectionRef: SelectionRef) = GlobalScope.promise { delegateTo.removeChoice(selectionRef) }
}