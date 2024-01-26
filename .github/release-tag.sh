#!/bin/sh

# Requires xmllint (libxml2-utils)

new_version=$1

test "${new_version}x" = "x" && \
echo "Usage: $(basename $0) <new_version>" && \
exit 1

current_version=$( (xmllint --shell pom.xml << EOF
setns mvn=http://maven.apache.org/POM/4.0.0
cat /mvn:project/mvn:version/text()
quit
EOF
) | grep -v '/ >')

test "${current_version}x" = "x" && \
echo 'Could not determine current version' && \
exit 1

old_version=${current_version%-SNAPSHOT}

test "$current_version" != "${old_version}-SNAPSHOT" && \
echo 'Current version is not SNAPSHOT' && \
exit 1

echo "Upgrading from $old_version to $new_version"

git_version_replace() {
  local next_version=$1 
  
  git grep -l "$current_version" -- '*pom.xml' | xargs \
  sed -i "s/<version>$current_version<\/version>/<version>$next_version<\/version>/g"
  git grep -l "$prev_version" -- '*.md' | xargs \
  sed -i "s/$current_version/$next_version/g"
  git add .
  git commit -m "Release v$new_version"
}

git checkout -b tmp/tag-$new_version
git_version_replace $new_version
git tag v$new_version

git checkout main
git_version_replace $new_version-SNAPSHOT

git branch -D tmp/tag-$new_version

git push origin main
git push origin v$new_version
