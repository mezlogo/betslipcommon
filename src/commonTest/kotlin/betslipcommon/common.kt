package betslipcommon

data class ClientChoice(val eventId: Long, val selectionUid: String, val coeffId: Long, val num: Long = 1, val denum: Long = 1)
data class ClientSingleBet(val choice: ClientChoice, val stake: Float)
data class ClientBet(val stake: Float)

class EmptyBetslipTicket() : BetslipTicket {
    override fun getChoices() = emptyList<Choice>()
    override fun getBets() = emptyList<Bet>()
}

class BetslipModelClientSample(private val betslipModel: BetslipModel) {
    private var betslipTicket: BetslipTicket = EmptyBetslipTicket()
    private var currentMode = BetslipMode.SINGLES

    fun toBetslipModelSelectionRef(clientRef: String) = SelectionRef(10L, "Result.1")

    fun toBetslipModelChoice(clientChoice: ClientChoice) = Choice(SelectionRef(clientChoice.eventId, clientChoice.selectionUid),
            clientChoice.coeffId, Fraction(clientChoice.num, clientChoice.denum))

    fun toClientChoice(choice: Choice) = ClientChoice(choice.selectionRef.eventId, choice.selectionRef.selectionUid, choice.coeffId, choice.coeff.numerator, choice.coeff.denumerator)

    fun onAddChoice(clientChoice: ClientChoice): Boolean {
        val choice = toBetslipModelChoice(clientChoice)
        val isAdded = betslipModel.addChoice(choice)

        if (!isAdded) {
            return false
        }

        val availableModes = betslipModel.getAvailableModes()

        this.currentMode = if (availableModes.contains(this.currentMode)) this.currentMode else BetslipMode.SINGLES
        this.betslipTicket = betslipModel.getTicket(currentMode)
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
            this.betslipTicket = betslipModel.getTicket(currentMode)
            renderBetslip()
        } else {
            this.betslipTicket = EmptyBetslipTicket()
            renderEmptyMode()
        }
        return true
    }

    fun onBetslipModeChange(mode: BetslipMode): Boolean {
        val availableModes = betslipModel.getAvailableModes()

        if (availableModes.contains(mode) && mode != this.currentMode) {
            this.currentMode = mode
            this.betslipTicket = betslipModel.getTicket(mode)
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

        val singleBetslipTicket = betslipTicket as SingleBetslipTicket
        return singleBetslipTicket.setStake(toBetslipModelSelectionRef(ref), Stake(stake))
    }

    fun onComplexStakeChange(betType: BetType, stake: Float): Boolean {
        if (BetslipMode.SINGLES == currentMode) {
            return false
        }

        val complexBetslipTicket = betslipTicket as ComplexBetslipTicket
        return complexBetslipTicket.setStake(betType, Stake(stake))
    }

    fun onPlaceTickets() {
        val betsWithNotEmptyStake = this.betslipTicket!!.getBets().filter { 0f < it.getStake().value }
        betslipModel.placeBets(betsWithNotEmptyStake)
    }

    private fun renderBetslip() = when (currentMode) {
        BetslipMode.SINGLES -> renderSingleMode(this.betslipTicket as SingleBetslipTicket)
        BetslipMode.ACCUMULATORS -> renderAccumMode(this.betslipTicket as ComplexBetslipTicket)
        BetslipMode.MULTIPLES -> renderMultipleMode(this.betslipTicket as ComplexBetslipTicket)
        BetslipMode.ANTIEXPRESSES -> renderAntiexpressMode(this.betslipTicket as ComplexBetslipTicket)
    }

    private fun renderSingleMode(singleBetslipTicket: SingleBetslipTicket): Map<String, Any> {
        val singleBets = singleBetslipTicket.getBets().map {
            ClientSingleBet(toClientChoice(it.getChoices().first()), it.getStake().value)
        }
        return mapOf(
                Pair("type", this.currentMode.name),
                Pair("singleBets", singleBets)
        )
    }

    //Right now there is no difference between multiple, accum and AE renders
    private fun renderAccumMode(complexBetslipTicket: ComplexBetslipTicket): Map<String, Any> {
        val bets = complexBetslipTicket.getBets().map { ClientBet(it.getStake().value) }
        val choices = complexBetslipTicket.getChoices().map { toClientChoice(it) }
        return mapOf(
                Pair("type", this.currentMode.name),
                Pair("bets", bets),
                Pair("choices", choices)
        )
    }
    private fun renderAntiexpressMode(complexBetslipTicket: ComplexBetslipTicket): Map<String, Any> {
        val bets = complexBetslipTicket.getBets().map { ClientBet(it.getStake().value) }
        val choices = complexBetslipTicket.getChoices().map { toClientChoice(it) }
        return mapOf(
                Pair("type", this.currentMode.name),
                Pair("bets", bets),
                Pair("choices", choices)
        )
    }
    private fun renderMultipleMode(complexBetslipTicket: ComplexBetslipTicket): Map<String, Any> {
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