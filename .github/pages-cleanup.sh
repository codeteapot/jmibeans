#!/bin/sh

git fetch --prune
git checkout gh-pages
git merge origin/gh-pages
git checkout --orphan gh-pages-new

ls -a -I '.' -I '..' -I '.git' -I '.nojekyll' -I 'v*' | xargs rm -rf
rm -rf v*-SNAPSHOT

git add .
git commit -m 'Cleanup'
git branch -D gh-pages
git branch -m gh-pages
git push --force-with-lease origin gh-pages

git checkout main
rm -rf *
git checkout -- .
