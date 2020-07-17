package betslipcommon

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlin.js.Promise

@JsExport
fun <T> toList(array: Array<T>) = array.toList()

@JsExport
interface BetslipServiceAOPromises {
    @JsName("requestAvailableBetsForChoices")
    fun requestAvailableBetsForChoices(choices: List<Choice>): Promise<List<BetType>>
}

class BetslipServiceAOAdapterForJs(val betslipServiceAOPromises: BetslipServiceAOPromises) : BetslipServiceAOCoroutines {
    override suspend fun requestAvailableBetsForChoices(choices: List<Choice>): List<BetType> {
        return betslipServiceAOPromises.requestAvailableBetsForChoices(choices).await()
    }
}

@JsExport
fun createBetslipModelForDelegation(betslipStorageAo: BetslipStorageAo, betslipServiceAo: BetslipServiceAOPromises) = BetslipModelCommonImpl(betslipStorageAo, BetslipServiceAOAdapterForJs(betslipServiceAo))

@JsExport
class BetslipModelJs(val delegateTo: BetslipModelCommonImpl) : BetslipModel by delegateTo {
    @JsName("addChoice")
    fun addChoice(choice: Choice) = GlobalScope.promise { delegateTo.addChoice(choice) }

    @JsName("removeChoice")
    fun removeChoice(selectionRef: SelectionRef) = GlobalScope.promise { delegateTo.removeChoice(selectionRef) }
}