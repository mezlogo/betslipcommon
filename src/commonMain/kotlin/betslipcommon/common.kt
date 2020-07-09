package betslipcommon

import kotlinx.coroutines.delay

private fun lg(msg: String) = println("kmp: $msg")

class BetDummy(private val choices: List<Choice>,
               private val betType: BetType,
               private var stake: Stake,
               private var min: Stake,
               private var max: Stake) : Bet {
    override fun getChoices() = choices

    override fun getBetType() = betType

    override fun getStake() = stake

    override fun setStake(value: Float) {
        stake = Stake(value)
    }

    override fun getMin() = min

    override fun getMax() = max
}

class SuccessfullyPlacedResultDummy : SuccessfullyPlacedResult {
    override fun getStatus() = PlaceBetStatus.OK

    override fun getBetIdForPrint() = "BetIdForPrint"
}

class SingleTicketDummy : SingleTicket {
    override fun setStake(selectionRef: SelectionRef, stake: Stake): Boolean {
        lg("setStake: selectionRef: $selectionRef stake: $stake")
        return true
    }

    override fun getBets(): List<Bet> = listOf(BetDummy(listOf(Choice(SelectionRef(12, "Result.2"), Coeffiicient(1000, Fraction(2, 1)))), BetType.SINGLE, Stake(0f), Stake(1f), Stake(10f)))

    override fun place(): PlaceBetResult = SuccessfullyPlacedResultDummy()
}

class BetslipModelCommonImpl : BetslipModel {
    suspend fun addChoice(choice: Choice): Boolean {
        lg("addChoice: choice: $choice delay 100")
        delay(100)
        lg("addChoice: choice: $choice finish")
        return true
    }

    suspend fun removeChoice(selectionRef: SelectionRef): Boolean {
        lg("removeChoice: selectionRef: $selectionRef delay 100")
        delay(100)
        lg("removeChoice: selectionRef: $selectionRef finish")
        return true
    }
}