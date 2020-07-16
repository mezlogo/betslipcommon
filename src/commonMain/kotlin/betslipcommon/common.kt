package betslipcommon

import kotlinx.coroutines.delay

private fun lg(msg: String) = println("kmp: $msg")

enum class BetType { SINGLE, ANTIEXPRESS, DOUBLE, DOUBLES, TREBLE, ACCUMULATOR, PATENT, TRIXIE }
enum class BetslipMode { SINGLES, ACCUMULATORS, ANTIEXPRESSES, MULTIPLES }

enum class ErrorType { NOT_ENOUGH_MONEY, BAD_PUNTER, TERMS_HAS_CHANGED, GENERAL_ERROR }
enum class PlaceBetStatus { OK, LIVE_DELAY, ERROR }

class BetslipModelCommonImpl(val betslipStorageAo: BetslipStorageAo) : BetslipModel {
    suspend fun addChoice(choice: Choice): Boolean {
        val choices = betslipStorageAo.getChoices()
        if (null != choices.find { it.selectionRef == choice.selectionRef }) {
            lg("addChoice: choice: $choice: this selectionRef is already added")
            return false
        }
        lg("addChoice: choice: $choice delay 100")
        delay(100)
        lg("addChoice: choice: $choice finish")

        val array = Array(choices.size + 1) { if (it < choices.size) choices[it] else choice }
        betslipStorageAo.saveChoices(array)
        return true
    }

    suspend fun removeChoice(selectionRef: SelectionRef): Boolean {
        lg("removeChoice: selectionRef: $selectionRef delay 100")
        delay(100)
        lg("removeChoice: selectionRef: $selectionRef finish")
        return true
    }

    override fun getTicket(mode: BetslipMode): Ticket {
        val choices = betslipStorageAo.getChoices()
        val singleBets = choices.map { MySingleBet(it, Stake(.0f)) }
        return MySinglesTicket(singleBets)
    }
}

class MySingleBet(val choice: Choice, private var stake: Stake): Bet {
    override fun getChoices() = listOf(choice)

    override fun getBetType() = BetType.SINGLE

    override fun getStake() = stake

    override fun setStake(value: Float) { stake = Stake(value) }

    override fun getMin() = Stake(0.1f)

    override fun getMax() = Stake(50.0f)
}

data class MySinglesTicket(private val bets: List<Bet>): SingleTicket {
    override fun setStake(selectionRef: SelectionRef, stake: Stake) = true

    override fun getBets() = bets

    override fun place(): PlaceBetResult { throw RuntimeException("UNSUPPORTED") }
}