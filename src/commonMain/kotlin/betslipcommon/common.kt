@file:JsExport

package betslipcommon

import kotlin.js.JsExport

//Supports up to 5 choices
enum class BetType { SINGLE, ANTIEXPRESS, DOUBLE, DOUBLES, TREBLE, TREBELS, ACCUMULATOR, FOLD4, PATENT, TRIXIE, YANKEE, LUCKY15, SUPER_YANKEE, LUCKY31 }

enum class BetslipTab { SINGLES, ACCUMULATORS, ANTIEXPRESSES, MULTIPLES }

data class SingleBetCalcResult(val totalReturns: Stake, val totalStake: Stake)
data class Fraction(val numerator: Long, val denumerator: Long)
data class SelectionRef(val eventId: Long, val selectionUid: String)
data class Choice(val selectionRef: SelectionRef, var coeffId: Long, var coeff: Fraction)
data class Stake(val value: Float) {
    fun add(stake: Stake) = Stake(value + stake.value)
}

data class Bet(var stake: Stake, var minStake: Stake, var maxStake: Stake)
data class SingleBet(val choice: Choice, val bet: Bet, var potentialReturns: Stake = Stake(0f))
enum class PlaceBetStatus { OK, LIVE_DELAY, ERROR }
data class PlaceBetResponse(val status: PlaceBetStatus)

interface BetslipModel {
    fun addChoice(newChoice: Choice): Boolean
    fun removeChoice(selectionRef: SelectionRef): Boolean
    fun placeBet()
    fun selectSinglesTab(): SinglesBetslipModel
    fun selectAccumulatorsTab(): ComplexBetslipModel
    fun selectAntiexpressTab(): ComplexBetslipModel
    fun selectMultipleTab(): ComplexBetslipModel
}

interface SinglesBetslipModel : BetslipModel {
    fun setSingleStake(selectionRef: SelectionRef, stake: Stake): Boolean
    fun getSingleBets(): List<SingleBet>
}

interface ComplexBetslipModel : BetslipModel {
    fun setStake(betType: BetType, stake: Stake): Boolean
    fun getBetsForSelectedBetslipTab(): List<Bet>
}

interface BetslipStorageSpi {
    fun getSelectedChoices(): List<Choice>
    fun addChoice(clickedChoice: Choice, singleBet: SingleBet)
    fun getSingleBets(): List<SingleBet>
    fun betIsPlacedSuccessfully()
    fun setSingleBets(singleBets: List<SingleBet>, singleBetCalcResult: SingleBetCalcResult)
    fun noChoicesLeft()
    fun getSelectedBetslipTab(): BetslipTab
    fun setSelectedBetslipTab(selectedBetslipTab: BetslipTab)
}

data class CheckBetsAoSpiResponse(val minStake: Stake, val maxStake: Stake)

interface BetslipAoSpi {
    fun getAvailableBetTypes(choices: List<Choice>): List<BetType>
    fun checkBets(bets: List<Bet>): List<CheckBetsAoSpiResponse>
    fun placeSingleBets(bets: List<SingleBet>): PlaceBetResponse
}

class BetslipModelCommon(val storageSpi: BetslipStorageSpi, val aoSpi: BetslipAoSpi) : SinglesBetslipModel, ComplexBetslipModel {

    private fun mapBetTypeToBetslipTabs(betType: BetType) = when (betType) {
        BetType.SINGLE -> BetslipTab.SINGLES
        BetType.ANTIEXPRESS -> BetslipTab.ANTIEXPRESSES

        BetType.DOUBLE, BetType.DOUBLES, BetType.TREBLE, BetType.TREBELS,
        BetType.ACCUMULATOR, BetType.FOLD4 -> BetslipTab.ACCUMULATORS

        else -> BetslipTab.MULTIPLES
    }


    private fun calcReturns(stake: Stake, coeff: Fraction) = Stake(stake.value * coeff.numerator / coeff.denumerator)

    override fun addChoice(newChoice: Choice): Boolean {
        val alreadySelectedChoices = storageSpi.getSelectedChoices()

        val isChoiceAlreadySelected = null != alreadySelectedChoices.firstOrNull { newChoice.selectionRef == it.selectionRef }

        if (isChoiceAlreadySelected) {
            return false
        }

        val choices = ArrayList(alreadySelectedChoices)
        choices.add(newChoice)

        val availableBetTypes = aoSpi.getAvailableBetTypes(choices)
        var selectedBetslipTab = storageSpi.getSelectedBetslipTab()
        val betTypesByBetlipTabs = availableBetTypes.groupBy { mapBetTypeToBetslipTabs(it) }

        if (!betTypesByBetlipTabs.containsKey(selectedBetslipTab)) {
            selectedBetslipTab = BetslipTab.SINGLES
            storageSpi.setSelectedBetslipTab(selectedBetslipTab)
        }

        if (BetslipTab.SINGLES == selectedBetslipTab) {
            val singleBets = getSingleBets().map { it.bet }
            val checkBets = aoSpi.checkBets(singleBets)
        } else {
            val selectedBetTypes = betTypesByBetlipTabs[selectedBetslipTab]
        }

        storageSpi.addChoice(newChoice, SingleBet(newChoice, bet))

        return true
    }

    private fun calcTotalsForSingleBets(singleBets: List<SingleBet>): SingleBetCalcResult {
        val totalReturns = singleBets.map { calcReturns(it.bet.stake, it.choice.coeff) }.reduce { l, r -> l.add(r) }
        val totalStake = singleBets.map { it.bet.stake }.reduce { l, r -> l.add(r) }
        return SingleBetCalcResult(totalReturns, totalStake)
    }

    override fun setSingleStake(selectionRef: SelectionRef, stake: Stake): Boolean {
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

    override fun getSingleBets(): List<SingleBet> {
        TODO("Not yet implemented")
    }

    override fun setStake(betType: BetType, stake: Stake): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBetsForSelectedBetslipTab(): List<Bet> {
        TODO("Not yet implemented")
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

    override fun selectSinglesTab() = this
    override fun selectAccumulatorsTab() = this
    override fun selectAntiexpressTab() = this
    override fun selectMultipleTab() = this
}