@file:JsExport

package betslipcommon

import kotlin.js.JsExport

data class SingleBetCalcResult(val totalReturns: Stake, val totalStake: Stake)
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
    fun addChoice(newChoice: Choice): Boolean
    fun removeChoice(selectionRef: SelectionRef): Boolean
    fun setStake(selectionRef: SelectionRef, stakeAsString: String): Boolean
    fun placeBet()
}

interface BetslipStorageSpi {
    fun getSelectedChoices(): List<Choice>
    fun addChoice(clickedChoice: Choice, singleBet: SingleBet)
    fun getSingleBets(): List<SingleBet>
    fun betIsPlacedSuccessfully()
    fun setSingleBets(singleBets: List<SingleBet>, singleBetCalcResult: SingleBetCalcResult)
    fun noChoicesLeft()
}

interface BetslipAoSpi {
    fun addChoice(selectedChoices: List<Choice>, clickedChoice: Choice): Bet
    fun placeSingleBets(bets: List<SingleBet>) : PlaceBetResponse
}

class BetslipModelCommon(val storageSpi: BetslipStorageSpi, val aoSpi: BetslipAoSpi) : BetslipModel {
    private fun validateStake(stakeAsString: String) = Stake(stakeAsString.toFloat())
    private fun calcReturns(stake: Stake, coeff: Fraction) = Stake(stake.value * coeff.numerator / coeff.denumerator)

    private fun calcTotalsForSingleBets(singleBets: List<SingleBet>): SingleBetCalcResult {
        val totalReturns = singleBets.map { calcReturns(it.bet.stake, it.choice.coeff) }.reduce { l, r -> l.add(r) }
        val totalStake = singleBets.map { it.bet.stake }.reduce { l, r -> l.add(r) }
        return SingleBetCalcResult(totalReturns, totalStake)
    }

    override fun addChoice(newChoice: Choice): Boolean {
        val selectedChoices = storageSpi.getSelectedChoices()

        val selectedChoice = selectedChoices.firstOrNull { newChoice.selectionRef == it.selectionRef }

        if (null != selectedChoice) {
            return false
        }

        val bet = aoSpi.addChoice(selectedChoices, newChoice)

        storageSpi.addChoice(newChoice, SingleBet(newChoice, bet))

        return true
    }

    override fun removeChoice(selectionRef: SelectionRef): Boolean {
        val selectedBets = storageSpi.getSingleBets()

        val foundSingleBet = selectedBets.firstOrNull { selectionRef == it.choice.selectionRef }

        if (null == foundSingleBet) {
            return false
        }

        val remainingSingleBets = selectedBets.filter { selectionRef != it.choice.selectionRef }

        if (remainingSingleBets.isEmpty()) {
            storageSpi.noChoicesLeft()
        } else {
            storageSpi.setSingleBets(remainingSingleBets, calcTotalsForSingleBets(remainingSingleBets))
        }

        return true
    }

    override fun setStake(selectionRef: SelectionRef, stakeAsString: String): Boolean {
        val stake = validateStake(stakeAsString)
        val singleBets = storageSpi.getSingleBets()

        val singleBet = singleBets.firstOrNull { it.choice.selectionRef == selectionRef }

        if (null == singleBet) {
            return false
        }

        singleBet.bet.stake = stake
        singleBet.potentialReturns = calcReturns(stake, singleBet.choice.coeff)

        storageSpi.setSingleBets(singleBets, calcTotalsForSingleBets(singleBets))

        return true
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