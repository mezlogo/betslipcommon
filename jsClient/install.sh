#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

echo "install betslipcommon and kotlin js"
npm install $DIR/../build/js/packages/betslipcommon

echo "install and run webpack"
npm install --save-dev webpack webpack-cli typescript ts-loader && npx webpack