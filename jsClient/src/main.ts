import { initApp, Choice, SelectionRef } from './betslip';

main();

async function main() {
    const selRef = new SelectionRef(12, "Result.2");
    const bs = initApp();
    const ch = new Choice(selRef.eventId, selRef.selUid, 10);
    
    const addStatus = await bs.onAddChoice(ch);
    if (!addStatus) {
        throw Error("addStatus is false");
    }

    const setStakeStatus = bs.onSetStakeSingles(25, selRef);
    if (!setStakeStatus) {
        throw Error("setStake is false");
    }
}