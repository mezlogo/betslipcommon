import * as betslipCommonRoot from "betslipcommon"

console.log(betslipCommonRoot)

const betslipCommon = betslipCommonRoot.betslipcommon;

const myTsCallback = { onCall: function(inputMsg: string) { return `callback from TS. inputMsg: ${inputMsg}`; }};
const response = betslipCommon.commonCode(myTsCallback);

console.log(response);
console.log(response.toString());