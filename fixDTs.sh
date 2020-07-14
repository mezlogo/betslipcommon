#!/usr/bin/env sh

#remove "type Nullable"
#remove first enclosing namespace (first and last line)
#add "export" before each "namespace"
#concat with kotlin-stdlib .d.ts
#replace betslipcommon.d.ts

sed -e '$d' -e '1d' -e '/type Nullable/d' -e 's_\(namespace \)_export \1_g' build/js/packages/betslipcommon/kotlin/betslipcommon.d.ts > fixed.d.ts
cat fixed.d.ts kotlin.d.ts > build/js/packages/betslipcommon/kotlin/betslipcommon.d.ts
