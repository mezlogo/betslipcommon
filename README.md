# betslip common

Follow these steps:
- build kotlin multiplatform project: `gradle build`
- replace wrong generated `.d.ts` file: `cp betslipcommon.d.ts build/js/packages/betslipcommon/kotlin`
- test module with dummy typescript project: `cd jsClient && npm install && npm run build && npm run run`

Output:
```
js: addChoice: commonChoice: Choice(selectionRef=SelectionRef(eventId=100, selectionUid=Result.1), coeff=Coeffiicient(coeffId=10, value=Fraction(numerator=1, denumerator=1)))
kmp: addChoice: choice: Choice(selectionRef=SelectionRef(eventId=100, selectionUid=Result.1), coeff=Coeffiicient(coeffId=10, value=Fraction(numerator=1, denumerator=1))) delay 100
kmp: addChoice: choice: Choice(selectionRef=SelectionRef(eventId=100, selectionUid=Result.1), coeff=Coeffiicient(coeffId=10, value=Fraction(numerator=1, denumerator=1))) finish
js: addChoice: after await result: true
js: Render betslip current mode: SINGLES available modes: SINGLES ticket: singlesTicket: choices: Choice(selectionRef=SelectionRef(eventId=12, selectionUid=Result.2), coeff=Coeffiicient(coeffId=1000, value=Fraction(numerator=2, denumerator=1))) bets: [object Object]
```
