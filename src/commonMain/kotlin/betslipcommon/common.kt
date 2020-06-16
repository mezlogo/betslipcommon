@file:JsExport

package betslipcommon

import kotlin.js.JsExport

enum class BetType { SINGLE, ANTIEXPRESS, DOUBLE, DOUBLES, TREBLE, ACCUMULATOR, PATENT, TRIXIE }
enum class BetslipMode { SINGLES, ACCUMULATORS, ANTIEXPRESSES, MULTIPLES }
data class Fraction(val numerator: Long, val denumerator: Long)
data class SelectionRef(val eventId: Long, val selectionUid: String)
data class Choice(val selectionRef: SelectionRef, var coeffId: Long, var coeff: Fraction)
data class Stake(val value: Float)

interface Bet {
    fun getChoices(): List<Choice>
    fun getBetType(): BetType
    fun getStake(): Stake
    fun setStake(value: Float)
    fun getMin(): Stake
    fun getMax(): Stake
}

interface BetslipTicket {
    fun getChoices(): List<Choice>
    fun getBets(): List<Bet>
}

interface SingleBetslipTicket: BetslipTicket {
    fun setStake(selectionRef: SelectionRef, stake: Stake): Boolean
}

interface ComplexBetslipTicket: BetslipTicket {
    fun setStake(betType: BetType, stake: Stake): Boolean
}

interface BetslipModel {
    fun addChoice(choice: Choice): Boolean
    fun removeChoice(selectionRef: SelectionRef): Boolean
    fun getTicket(mode: BetslipMode): BetslipTicket
    fun getAvailableModes(): List<BetslipMode>
    fun placeBets(bets: List<Bet>)
}