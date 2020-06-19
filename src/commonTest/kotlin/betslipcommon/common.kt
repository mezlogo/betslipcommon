package betslipcommon

data class ClientChoice(val eventId: Long, val selectionUid: String, val coeffId: Long, val num: Long = 1, val denum: Long = 1)
data class ClientSingleBet(val choice: ClientChoice, val stake: Float)
data class ClientBet(val stake: Float)

class EmptyTicket() : Ticket {
    override fun getChoices() = emptyList<Choice>()
    override fun getBets() = emptyList<Bet>()
    override fun place(): PlaceBetResult = throw UnsupportedOperationException()
}

object Bundle {
    val SUCCESSFULLY_PLACED_MSG = "congrats"
    val LIVE_DELAY_MSG = "plz, wait"
}

class BetslipModelClientSample(private val betslipModel: BetslipModel) {
    var isRetainEnabled: Boolean = false

    private var ticket: Ticket = EmptyTicket()
    private var currentMode = BetslipMode.SINGLES

    fun toBetslipModelSelectionRef(clientRef: String) = SelectionRef(10L, "Result.1")

    fun toBetslipModelChoice(clientChoice: ClientChoice) = Choice(SelectionRef(clientChoice.eventId, clientChoice.selectionUid),
            Coeffiicient(clientChoice.coeffId, Fraction(clientChoice.num, clientChoice.denum)))

    fun toClientChoice(choice: Choice) = ClientChoice(choice.selectionRef.eventId, choice.selectionRef.selectionUid, choice.coeff.coeffId, choice.coeff.value.numerator, choice.coeff.value.denumerator)

    fun onAddChoice(clientChoice: ClientChoice): Boolean {
        val choice = toBetslipModelChoice(clientChoice)
        val isAdded = betslipModel.addChoice(choice)

        if (!isAdded) {
            return false
        }

        val availableModes = betslipModel.getAvailableModes()

        this.currentMode = if (availableModes.contains(this.currentMode)) this.currentMode else BetslipMode.SINGLES
        this.ticket = betslipModel.getTicket(currentMode)
        renderBetslip()
        return true
    }

    fun onRemoveChoice(clientRef: String): Boolean {
        val selectionRef = toBetslipModelSelectionRef(clientRef)

        val isRemoved = this.betslipModel.removeChoice(selectionRef)

        if (!isRemoved) {
            return false
        }

        val availableModes = betslipModel.getAvailableModes()

        this.currentMode = if (availableModes.contains(this.currentMode)) this.currentMode else BetslipMode.SINGLES

        if (availableModes.contains(currentMode)) {
            this.ticket = betslipModel.getTicket(currentMode)
            renderBetslip()
        } else {
            this.ticket = EmptyTicket()
            renderEmptyMode()
        }
        return true
    }

    fun onBetslipModeChange(mode: BetslipMode): Boolean {
        val availableModes = betslipModel.getAvailableModes()

        if (availableModes.contains(mode) && mode != this.currentMode) {
            this.currentMode = mode
            this.ticket = betslipModel.getTicket(mode)
            renderBetslip()
            return true
        } else {
            return false
        }
    }

    fun onSingleStakeChange(ref: String, stake: Float): Boolean {
        if (BetslipMode.SINGLES != currentMode) {
            return false
        }

        val singleBetslipTicket = ticket as SingleTicket
        return singleBetslipTicket.setStake(toBetslipModelSelectionRef(ref), Stake(stake))
    }

    fun onComplexStakeChange(betType: BetType, stake: Float): Boolean {
        if (BetslipMode.SINGLES == currentMode) {
            return false
        }

        val complexBetslipTicket = ticket as ComplexTicket
        return complexBetslipTicket.setStake(betType, Stake(stake))
    }

    fun onPlaceTickets() = handlePlace(this.ticket.place())

    fun onLiveDelayResult(placeBetResult: PlaceBetResult) = handlePlace(placeBetResult)

    private fun handlePlace(placeBetResult: PlaceBetResult) = when (placeBetResult.getStatus()) {
        PlaceBetStatus.OK -> showSuccessfullyPlacedMessage(placeBetResult as SuccessfullyPlacedResult)
        PlaceBetStatus.LIVE_DELAY -> showLiveDelayMessage(placeBetResult)
        PlaceBetStatus.ERROR -> showErrorPlacedMessage(placeBetResult as ErrorResult)
    }

    private fun showLiveDelayMessage(placeBetResult: PlaceBetResult): Map<String, Any> {
        return mapOf(
                Pair("type", placeBetResult.getStatus()),
                Pair("msg", Bundle.LIVE_DELAY_MSG),
        )
    }

    private fun showErrorPlacedMessage(placeBetResult: ErrorResult): Map<String, Any> {
        return mapOf(
                Pair("type", placeBetResult.getStatus()),
                Pair("errorType", placeBetResult.getErrorType()),
                Pair("errorMsg", placeBetResult.getErrorMsg()),
        )
    }

    private fun showSuccessfullyPlacedMessage(placeBetResult: SuccessfullyPlacedResult): Map<String, Any> {
        val choices = ticket.getChoices()

        betslipModel.removeAllChoices()

        if (isRetainEnabled) {
            betslipModel.initBetslip(choices)
        }

        return mapOf(
                Pair("type", placeBetResult.getStatus()),
                Pair("msg", Bundle.SUCCESSFULLY_PLACED_MSG),
                Pair("printBetId", placeBetResult.getBetIdForPrint()),
        )
    }

    private fun renderBetslip() = when (currentMode) {
        BetslipMode.SINGLES -> renderSingleMode(this.ticket as SingleTicket)
        BetslipMode.ACCUMULATORS -> renderAccumMode(this.ticket as ComplexTicket)
        BetslipMode.MULTIPLES -> renderMultipleMode(this.ticket as ComplexTicket)
        BetslipMode.ANTIEXPRESSES -> renderAntiexpressMode(this.ticket as ComplexTicket)
    }

    private fun renderSingleMode(singleBetslipTicket: SingleTicket): Map<String, Any> {
        val singleBets = singleBetslipTicket.getBets().map {
            ClientSingleBet(toClientChoice(it.getChoices().first()), it.getStake().value)
        }
        return mapOf(
                Pair("type", this.currentMode.name),
                Pair("singleBets", singleBets)
        )
    }

    //Right now there is no difference between multiple, accum and AE renders
    private fun renderAccumMode(complexBetslipTicket: ComplexTicket): Map<String, Any> {
        val bets = complexBetslipTicket.getBets().map { ClientBet(it.getStake().value) }
        val choices = complexBetslipTicket.getChoices().map { toClientChoice(it) }
        return mapOf(
                Pair("type", this.currentMode.name),
                Pair("bets", bets),
                Pair("choices", choices)
        )
    }
    private fun renderAntiexpressMode(complexBetslipTicket: ComplexTicket): Map<String, Any> {
        val bets = complexBetslipTicket.getBets().map { ClientBet(it.getStake().value) }
        val choices = complexBetslipTicket.getChoices().map { toClientChoice(it) }
        return mapOf(
                Pair("type", this.currentMode.name),
                Pair("bets", bets),
                Pair("choices", choices)
        )
    }
    private fun renderMultipleMode(complexBetslipTicket: ComplexTicket): Map<String, Any> {
        val bets = complexBetslipTicket.getBets().map { ClientBet(it.getStake().value) }
        val choices = complexBetslipTicket.getChoices().map { toClientChoice(it) }
        return mapOf(
                Pair("type", this.currentMode.name),
                Pair("bets", bets),
                Pair("choices", choices)
        )
    }
    private fun renderEmptyMode() = mapOf(Pair("type", "EMPTY_BETSLIP"))
}