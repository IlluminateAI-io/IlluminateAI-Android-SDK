#!/bin/bash

BIN="$(cd "$(dirname "$0")" && pwd)"
pushd ${BIN}/../.git/hooks >/dev/null
ln -sf ../../config/git/hooks/* .
popd > /dev/null
