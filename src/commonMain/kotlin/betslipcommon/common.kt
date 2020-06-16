@file:JsExport

package betslipcommon

import kotlin.js.JsExport

enum class BetType { SINGLE, ANTIEXPRESS, DOUBLE, DOUBLES, TREBLE, ACCUMULATOR, PATENT, TRIXIE }
enum class BetslipMode { SINGLES, ACCUMULATORS, ANTIEXPRESSES, MULTIPLES }
data class Fraction(val numerator: Long, val denumerator: Long)
data class SelectionRef(val eventId: Long, val selectionUid: String)
data class Coeffiicient(val coeffId: Long, val value: Fraction)
data class Choice(val selectionRef: SelectionRef, var coeff: Coeffiicient)
data class Stake(val value: Float)

interface Bet {
    fun getChoices(): List<Choice>
    fun getBetType(): BetType
    fun getStake(): Stake
    fun setStake(value: Float)
    fun getMin(): Stake
    fun getMax(): Stake
}

interface Ticket {
    fun getChoices() = getBets().asSequence().flatMap { it.getChoices().asSequence() }.distinct().toList()
    fun getBets(): List<Bet>
    fun place()
}

interface SingleTicket: Ticket {
    fun setStake(selectionRef: SelectionRef, stake: Stake): Boolean
}

interface ComplexTicket: Ticket {
    fun setStake(betType: BetType, stake: Stake): Boolean
}

interface BetslipModel {
    fun addChoice(choice: Choice): Boolean
    fun removeChoice(selectionRef: SelectionRef): Boolean
    fun getTicket(mode: BetslipMode): Ticket
    fun getAvailableModes(): List<BetslipMode>
    fun placeBets(bets: List<Bet>)
}