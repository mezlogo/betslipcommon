import { initApp, Betslip, Choice } from './betslip';

main();

async function main() {
    const bs = initApp();
    const ch = new Choice(100, "Result.1", 10);
    bs.onAddChoice(ch);
}