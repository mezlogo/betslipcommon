    export namespace betslipcommon {
        class Fraction {
            constructor(numerator: kotlin.Long, denumerator: kotlin.Long)
            readonly numerator: kotlin.Long;
            readonly denumerator: kotlin.Long;
            component1(): kotlin.Long
            component2(): kotlin.Long
            copy(numerator: kotlin.Long, denumerator: kotlin.Long): betslipcommon.Fraction
            toString(): string
            hashCode(): number
            equals(other: Nullable<any>): boolean
        }
        class SelectionRef {
            constructor(eventId: kotlin.Long, selectionUid: string)
            readonly eventId: kotlin.Long;
            readonly selectionUid: string;
            component1(): kotlin.Long
            component2(): string
            copy(eventId: kotlin.Long, selectionUid: string): betslipcommon.SelectionRef
            toString(): string
            hashCode(): number
            equals(other: Nullable<any>): boolean
        }
        class Coeffiicient {
            constructor(coeffId: kotlin.Long, value: betslipcommon.Fraction)
            readonly coeffId: kotlin.Long;
            readonly value: betslipcommon.Fraction;
            component1(): kotlin.Long
            component2(): betslipcommon.Fraction
            copy(coeffId: kotlin.Long, value: betslipcommon.Fraction): betslipcommon.Coeffiicient
            toString(): string
            hashCode(): number
            equals(other: Nullable<any>): boolean
        }
        class Choice {
            constructor(selectionRef: betslipcommon.SelectionRef, coeff: betslipcommon.Coeffiicient)
            readonly selectionRef: betslipcommon.SelectionRef;
            coeff: betslipcommon.Coeffiicient;
            component1(): betslipcommon.SelectionRef
            component2(): betslipcommon.Coeffiicient
            copy(selectionRef: betslipcommon.SelectionRef, coeff: betslipcommon.Coeffiicient): betslipcommon.Choice
            toString(): string
            hashCode(): number
            equals(other: Nullable<any>): boolean
        }
        class Stake {
            constructor(value: number)
            readonly value: number;
            component1(): number
            copy(value: number): betslipcommon.Stake
            toString(): string
            hashCode(): number
            equals(other: Nullable<any>): boolean
        }
        interface Bet {
            getChoices(): kotlin.collections.List<betslipcommon.Choice>
            getBetType(): any /*Class betslipcommon.BetType with kind: ENUM_CLASS*/
            getStake(): betslipcommon.Stake
            setStake(value: number): void
            getMin(): betslipcommon.Stake
            getMax(): betslipcommon.Stake
        }
        interface PlaceBetResult {
            getStatus(): any /*Class betslipcommon.PlaceBetStatus with kind: ENUM_CLASS*/
        }
        interface SuccessfullyPlacedResult extends betslipcommon.PlaceBetResult {
            getBetIdForPrint(): string
        }
        interface ErrorResult extends betslipcommon.PlaceBetResult {
            getErrorType(): any /*Class betslipcommon.ErrorType with kind: ENUM_CLASS*/
            getErrorMsg(): string
        }
        interface Ticket {
            getChoices(): kotlin.collections.List<betslipcommon.Choice>
            getBets(): kotlin.collections.List<betslipcommon.Bet>
            place(): betslipcommon.PlaceBetResult
        }
        interface SingleTicket extends betslipcommon.Ticket {
            setStake(selectionRef: betslipcommon.SelectionRef, stake: betslipcommon.Stake): boolean
        }
        interface ComplexTicket extends betslipcommon.Ticket {
            setStake(betType: any /*Class betslipcommon.BetType with kind: ENUM_CLASS*/, stake: betslipcommon.Stake): boolean
        }
        interface BetslipModel {
            getCurrentMode(): any /*Class betslipcommon.BetslipMode with kind: ENUM_CLASS*/
            getTicket(mode: any /*Class betslipcommon.BetslipMode with kind: ENUM_CLASS*/): betslipcommon.Ticket
            getAvailableModes(): kotlin.collections.List<any /*Class betslipcommon.BetslipMode with kind: ENUM_CLASS*/>
            removeAllChoices(): void
        }
        interface BetslipStorageAo {
            getChoices(): Array<betslipcommon.Choice>
            saveChoices(choices: Array<betslipcommon.Choice>): void
        }
    }
    export namespace betslipcommon {
        function createBetslipModelForDelegation(betslipStorageAo: betslipcommon.BetslipStorageAo): betslipcommon.BetslipModelCommonImpl
        class BetslipModelJs implements betslipcommon.BetslipModel {
            constructor(delegateTo: betslipcommon.BetslipModelCommonImpl)
            readonly delegateTo: betslipcommon.BetslipModelCommonImpl;
            getAvailableModes(): kotlin.collections.List<any /*Class betslipcommon.BetslipMode with kind: ENUM_CLASS*/>
            getCurrentMode(): any /*Class betslipcommon.BetslipMode with kind: ENUM_CLASS*/
            getTicket(mode: any /*Class betslipcommon.BetslipMode with kind: ENUM_CLASS*/): betslipcommon.Ticket
            removeAllChoices(): void
            addChoice(choice: betslipcommon.Choice): kotlin.js.Promise<boolean>
            removeChoice(selectionRef: betslipcommon.SelectionRef): kotlin.js.Promise<boolean>
        }
    }
