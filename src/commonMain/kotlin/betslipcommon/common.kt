@file:JsExport

package betslipcommon

import kotlin.js.JsExport

interface BetslipApi {
    fun addRmChoice(clickedChoice: Choice)
    fun rmChoice(clickedChoice: Choice)
    fun clear()
    fun placeBet()
    fun setStake()
}

data class Choice(
        val eventId: Long,
        val selectionUid: String,
        val coeffId: Long,
)

interface BetslipStorageSpi {
    fun getSelectedChoices() : List<Choice>
    fun addChoice(clickedChoice: Choice)
    fun removeChoice(clickedChoice: Choice)
    fun clear()
}

class BetslipApiImpl(val storageSpi: BetslipStorageSpi) : BetslipApi {
    private fun isEventIdAndUidEqual(l: Choice, r: Choice) = l.eventId == r.eventId && l.selectionUid == r.selectionUid

    override fun addRmChoice(clickedChoice: Choice) {
        val selectedChoices = storageSpi.getSelectedChoices()
        val foundChoice = selectedChoices.firstOrNull { isEventIdAndUidEqual(it, clickedChoice) }
        if (null == foundChoice)
            storageSpi.addChoice(clickedChoice)
        else
            storageSpi.removeChoice(clickedChoice)
    }

    override fun rmChoice(clickedChoice: Choice) {
        val selectedChoices = storageSpi.getSelectedChoices()
        val foundChoice = selectedChoices.firstOrNull { isEventIdAndUidEqual(it, clickedChoice) }
        if (null != foundChoice)
            storageSpi.removeChoice(clickedChoice)
    }

    override fun clear() {
        storageSpi.clear()
    }

    override fun placeBet() {
        TODO("Not yet implemented")
    }

    override fun setStake() {
        TODO("Not yet implemented")
    }
}