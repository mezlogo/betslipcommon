package betslipcommon

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

private val betslipModel = BetslipModelCommonImpl()

@JsExport
class BetslipModelJs : BetslipModel by betslipModel {
    fun addChoice(choice: Choice) = GlobalScope.promise { betslipModel.addChoice(choice) }

    fun removeChoice(selectionRef: SelectionRef) = GlobalScope.promise { betslipModel.removeChoice(selectionRef) }
}