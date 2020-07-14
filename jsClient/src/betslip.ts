import { betslipcommon } from 'betslipcommon';

function lg(msg: string) { console.log(`js: ${msg}`); }

function toCommonChoice(choice: Choice): betslipcommon.Choice {
    const selRef = new betslipcommon.SelectionRef(choice.eventId, choice.selUid);
    const fraction = new betslipcommon.Fraction(1, 1);
    const coeff = new betslipcommon.Coeffiicient(choice.coeffId, fraction);
    return new betslipcommon.Choice(selRef, coeff);
}

export class SelectionRef {
    readonly eventId: number;
    readonly selUid: string;

    constructor(eventId: number, selUid: string) {
        this.eventId = eventId;
        this.selUid = selUid;
    }
}

export class Choice {
    readonly eventId: number;
    readonly selUid: string;
    readonly coeffId: number;

    constructor(eventId: number, selUid: string, coeffId: number) {
        this.eventId = eventId;
        this.selUid = selUid;
        this.coeffId = coeffId;
    }
}

export class Betslip {
    private readonly bsc: betslipcommon.BetslipModelJs;

    constructor(bsc: betslipcommon.BetslipModelJs) {
        this.bsc = bsc;
    }

    private renderChoice(choice: betslipcommon.Choice) {
        return `eventId: ${choice.selectionRef.eventId} selUid: ${choice.selectionRef.selectionUid}`;
    }

    private renderSingleBet(bet: betslipcommon.Bet) {
        const renderedChoice = this.renderChoice(bet.getChoices().toArray()[0])
        return `type: ${bet.getBetType()} stake: ${bet.getStake()} choice: ${renderedChoice}`;
    }

    private render() {
        const modes = this.bsc.getAvailableModes().toArray();
        if (0 == modes.length) {
            lg("Render EMPTY betslip");
        } else {
            const currentMode = this.bsc.getCurrentMode();
            const ticket = this.bsc.getTicket(currentMode);

            let renderedTicket = ticket.getBets().toArray().map((bet: betslipcommon.Bet) => this.renderSingleBet(bet)).join(", ");

            lg(`Render betslip current mode: ${currentMode} available modes: ${modes} ticket: ${renderedTicket}`);
        }
    }
    

    private isSelectionRefsEqual(left: betslipcommon.SelectionRef, right: betslipcommon.SelectionRef): boolean {
        return left.eventId === right.eventId && left.selectionUid === right.selectionUid;
    }

    async onAddChoice(choice: Choice): Promise<boolean> {
        const commonChoice = toCommonChoice(choice);
        lg(`addChoice: commonChoice: ${commonChoice.toString()}`);
        const result = await this.bsc.addChoice(commonChoice);
        lg(`addChoice: after await result: ${result}`);
        this.render();
        return result;
    }

    onSetStakeSingles(stake: number, selRef: SelectionRef): boolean {
        const mode = this.bsc.getCurrentMode();

        if ("SINGLES" !== mode.toString()) {
            return false;
        }

        const ticket = this.bsc.getTicket(mode) as betslipcommon.SingleTicket;

        const commonSelRef = new betslipcommon.SelectionRef(selRef.eventId, selRef.selUid);

        const bet = ticket.getBets().toArray()
            .find((bet: betslipcommon.Bet) => this.isSelectionRefsEqual(bet.getChoices().toArray()[0].selectionRef, commonSelRef));

        if (undefined === bet) {
            return false;
        }

        bet.setStake(stake);
        return true;
    }
}

export function initApp(): Betslip {
    const bsc = new betslipcommon.BetslipModelJs();
    return new Betslip(bsc);
}