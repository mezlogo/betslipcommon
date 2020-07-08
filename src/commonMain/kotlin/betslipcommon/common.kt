package betslipcommon

import kotlinx.coroutines.delay

private fun lg(msg: String) = println("kmp: $msg")

class BetslipModelCommonImpl : BetslipModel {
    suspend fun addChoice(choice: Choice): Boolean {
        lg("addChoice: start remote call")
        delay(100)
        lg("addChoice: finish remote call")
        return true
    }

    suspend fun removeChoice(selectionRef: SelectionRef): Boolean {
        lg("removeChoice: start remote call")
        delay(100)
        lg("removeChoice: finish remote call")
        return true
    }

    override fun getTicket(mode: BetslipMode): Ticket {
        TODO("Not yet implemented")
    }

    override fun getAvailableModes(): List<BetslipMode> = listOf(BetslipMode.SINGLES, BetslipMode.ACCUMULATORS)

    override fun removeAllChoices() {}
}