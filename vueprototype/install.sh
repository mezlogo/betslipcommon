#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

version="1.4.0-M2"
bscommon_js="$DIR/../betslipcommon/build/js"
kotlin_dist_dir="$bscommon_js/packages_imported"
bscommon_dist_dir="$bscommon_js/packages"

echo "kotlin.js: pack and publish to local /usr/lib/node_modules"
sh -xc "cd $kotlin_dist_dir/kotlin/$version && npm pack && sudo npm link"

echo "kotlin-source-map-loader.js: pack and publish to local /usr/lib/node_modules"
sh -xc "cd $kotlin_dist_dir/kotlin-source-map-loader/$version && npm pack && sudo npm link"

echo "betslipcommon.js: link with kotlin-source-map-loader.js and kotlin.js then pack and publish"
sh -xc "cd $bscommon_dist_dir/betslipcommon && npm link kotlin-source-map-loader && npm link kotlin && npm pack && sudo npm link"

echo "client: import betslipcommon.js from local /usr/lib/node_modules"
sh -xc "npm link betslipcommon && npm install"

echo "install and run webpack"
sh -xc "npm install --save-dev webpack webpack-cli typescript ts-loader && npx webpack"
