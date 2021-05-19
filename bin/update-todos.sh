#!/bin/zsh --login

source /etc/zshrc
source /Users/bowmane/.zshrc

DIR=$(head -n 1 "$HOME/.md-updater-dir")
pushd "$DIR"
LATEST=$(ls -t 000*.md | head -n 1)
# echo "LATEST=$LATEST"
if [ "$LATEST" != "" ]; then
  NEWER=$(find . -newer "$LATEST")
  if [ "$NEWER" != "" ]; then
    #echo running
    md-open-items.scala "$DIR"
    #echo "done"
  else
    #echo "not running"
  fi
fi
popd

