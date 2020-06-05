The goal is showing how to compile common code for both server and browser sides using kotlin/multiplatform framework with generating `.d.ts` files for js target.

Steps:
- build common module: `sh -c "cd betslipcommon && gradle clean build"`
- build client module: `sh -c "cd betslipcommonclient && ./install.sh"`
- run nginx: `sh -c "cd betslipcommonclient && ./nginx.sh"`
- open browser at `http://localhost/index.html`