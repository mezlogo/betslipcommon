@file:JsExport

package betslipcommon

import kotlin.js.JsExport
import kotlin.js.JsName

data class Fraction(val numerator: Long, val denumerator: Long)
data class SelectionRef(val eventId: Long, val selectionUid: String)
data class Coeffiicient(val coeffId: Long, val value: Fraction)
data class Choice(val selectionRef: SelectionRef, var coeff: Coeffiicient)
data class Stake(val value: Float)

interface Bet {
    @JsName("getChoices")
    fun getChoices(): List<Choice>
    @JsName("getBetType")
    fun getBetType(): BetType
    @JsName("getStake")
    fun getStake(): Stake
    @JsName("setStake")
    fun setStake(value: Float)
    fun getMin(): Stake
    fun getMax(): Stake
}

interface PlaceBetResult { fun getStatus(): PlaceBetStatus }

interface SuccessfullyPlacedResult: PlaceBetResult { fun getBetIdForPrint(): String }

interface ErrorResult: PlaceBetResult { fun getErrorType(): ErrorType; fun getErrorMsg(): String }

interface Ticket {
    @JsName("getChoices")
    fun getChoices() = getBets().asSequence().flatMap { it.getChoices().asSequence() }.distinct().toList()

    @JsName("getBets")
    fun getBets(): List<Bet>

    @JsName("place")
    fun place(): PlaceBetResult
}

interface SingleTicket: Ticket {
    @JsName("setStake")
    fun setStake(selectionRef: SelectionRef, stake: Stake): Boolean
}

interface ComplexTicket: Ticket {
    @JsName("setStake")
    fun setStake(betType: BetType, stake: Stake): Boolean
}

interface BetslipModel {
    @JsName("getCurrentMode")
    fun getCurrentMode(): BetslipMode = BetslipMode.SINGLES

    @JsName("getTicket")
    fun getTicket(mode: BetslipMode): Ticket

    @JsName("getAvailableModes")
    fun getAvailableModes(): List<BetslipMode> = listOf(BetslipMode.SINGLES)

    @JsName("removeAllChoices")
    fun removeAllChoices() {}
}

interface BetslipStorageAo {
    @JsName("getChoices")
    fun getChoices(): Array<Choice>
    @JsName("saveChoices")
    fun saveChoices(choices: Array<Choice>)
}