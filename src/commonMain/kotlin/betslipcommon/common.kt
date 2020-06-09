@file:JsExport

package betslipcommon

import kotlin.js.JsExport

data class Fraction(val numerator: Long, val denumerator: Long)
data class SelectionRef(val eventId: Long, val selectionUid: String)
data class Choice(val selectionRef: SelectionRef, var coeffId: Long, var coeff: Fraction)
data class Stake(val value: Float) {
    fun add(stake: Stake) = Stake(value + stake.value)
}
data class Bet(var stake: Stake, var minStake: Stake, var maxStake: Stake)
data class SingleBet(val choice: Choice, val bet: Bet, var potentialReturns: Stake = Stake(0f))
enum class PlaceBetStatus { OK, LIVE_DELAY, ERROR}
data class PlaceBetResponse(val status: PlaceBetStatus)

interface BetslipModel {
    fun addRmChoice(clickedChoice: Choice)
    fun setStake(selectionRef: SelectionRef, stakeAsString: String)
    fun placeBet()
}

interface BetslipStorageSpi {
    fun getSelectedChoices(): List<Choice>
    fun addChoice(clickedChoice: Choice, singleBet: SingleBet)
    fun saveNewSingleBet(singleBet: SingleBet, totalStake: Stake, totalReturns: Stake)
    fun getSingleBets(): List<SingleBet>
    fun betIsPlacedSuccessfully()
}

interface BetslipAoSpi {
    fun addChoice(selectedChoices: List<Choice>, clickedChoice: Choice): Bet
    fun placeSingleBets(bets: List<SingleBet>) : PlaceBetResponse
}

class BetslipModelCommon(val storageSpi: BetslipStorageSpi, val aoSpi: BetslipAoSpi) : BetslipModel {
    private fun validateStake(stakeAsString: String) = Stake(stakeAsString.toFloat())
    private fun calcReturns(stake: Stake, coeff: Fraction) = Stake(stake.value * coeff.numerator / coeff.denumerator)

    override fun addRmChoice(clickedChoice: Choice) {
        val selectedChoices = storageSpi.getSelectedChoices()

        val clickedSelectionRef = clickedChoice.selectionRef

        val foundChoice = selectedChoices.firstOrNull { clickedSelectionRef == it.selectionRef }

        if (null != foundChoice) {
            return
        }

        val bet = aoSpi.addChoice(selectedChoices, clickedChoice)

        storageSpi.addChoice(clickedChoice, SingleBet(clickedChoice, bet))
    }

    override fun setStake(selectionRef: SelectionRef, stakeAsString: String) {
        val stake = validateStake(stakeAsString)
        val singleBets = storageSpi.getSingleBets()

        val singleBet = singleBets.firstOrNull { it.choice.selectionRef == selectionRef }

        if (null == singleBet) {
            return
        }

        singleBet.bet.stake = stake
        singleBet.potentialReturns = calcReturns(stake, singleBet.choice.coeff)

        val otherSingleBets = singleBets.filter { it.choice.selectionRef != selectionRef }

        val totalStake = otherSingleBets.map { it.bet.stake }.reduce { l, r -> l.add(r) }
        val totalReturns = otherSingleBets.map { calcReturns(it.bet.stake, it.choice.coeff) }.reduce { l, r -> l.add(r) }

        storageSpi.saveNewSingleBet(singleBet, totalStake.add(stake), totalReturns.add(singleBet.potentialReturns))
    }

    override fun placeBet() {
        val singleBets = storageSpi.getSingleBets()

        val singleBetsWithStake = singleBets.filter { 0 < it.bet.stake.value }

        if (singleBetsWithStake.isEmpty()) {
            return
        }

        val placeBetResponse = aoSpi.placeSingleBets(singleBets)

        when (placeBetResponse.status) {
            PlaceBetStatus.OK -> storageSpi.betIsPlacedSuccessfully()
        }
    }
}