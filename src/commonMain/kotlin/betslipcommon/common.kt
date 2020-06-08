@file:JsExport

package betslipcommon

import kotlin.js.JsExport

data class SelectionRef(val eventId: Long, val selectionUid: String)
data class Choice(val selectionRef: SelectionRef, val coeffId: Long)
data class Stake(val value: Long)
data class SingleBet(var stake: Stake, var minStake: Stake, var maxStake: Stake)
enum class PlaceBetStatus { OK, LIVE_DELAY, ERROR}
data class PlaceBetResponse(val status: PlaceBetStatus)

interface BetslipApi {
    fun addRmChoice(clickedChoice: Choice)
    fun setStake(selectionRef: SelectionRef, stake: Stake)
    fun placeBet()
}

interface BetslipStorageSpi {
    fun getSelectedChoices(): List<Choice>
    fun addChoice(clickedChoice: Choice, singleBet: SingleBet)
    fun findSingleBet(selectionRef: SelectionRef): SingleBet?
    fun setSingleBet(selectionRef: SelectionRef, singleBet: SingleBet)
    fun getSingleBets(): List<SingleBet>
    fun betIsPlacedSuccessfully()
}

interface BetslipAoSpi {
    fun addChoice(selectedChoices: List<Choice>, clickedChoice: Choice): SingleBet
    fun placeBet(singleBets: List<SingleBet>) : PlaceBetResponse
}

class BetslipApiCommon(val storageSpi: BetslipStorageSpi, val aoSpi: BetslipAoSpi) : BetslipApi {
    override fun addRmChoice(clickedChoice: Choice) {
        val selectedChoices = storageSpi.getSelectedChoices()

        val clickedSelectionRef = clickedChoice.selectionRef

        val foundChoice = selectedChoices.firstOrNull { clickedSelectionRef == it.selectionRef }

        if (null != foundChoice) {
            return
        }

        val singleBet = aoSpi.addChoice(selectedChoices, clickedChoice)

        storageSpi.addChoice(clickedChoice, singleBet)
    }

    override fun setStake(selectionRef: SelectionRef, stake: Stake) {
        val singleBet = storageSpi.findSingleBet(selectionRef)

        if (null == singleBet) {
            return
        }

        storageSpi.setSingleBet(selectionRef, singleBet)
    }

    override fun placeBet() {
        val singleBets = storageSpi.getSingleBets()

        val singleBetsWithStake = singleBets.filter { 0 < it.stake.value }

        if (singleBetsWithStake.isEmpty()) {
            return
        }

        val placeBetResponse = aoSpi.placeBet(singleBets)

        when (placeBetResponse.status) {
            PlaceBetStatus.OK -> storageSpi.betIsPlacedSuccessfully()
        }
    }
}