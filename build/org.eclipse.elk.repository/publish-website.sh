#!/bin/bash
#
# This script clones the website repository, copies the (previously built) site
# over to the repository, and, if necessary, pushes any changes. Execute this
# script in the directory it's placed in.

echo "Website Publisher"
echo "================="

# Change to the directory the ELK repository is a subfolder of
cd ../../..

# Check if the website exists in the first place and abort otherwise
if [ ! -d elk/docs/public ]
then
  echo "docs/public/ not found"
  exit 1
fi

# Produce a shallow clone of the repository
git clone --depth 1 file:///gitroot/www.eclipse.org/elk.git website

# Synchronize the website over to the repository
rsync -crv --delete --exclude=".*/" --exclude=".*" elk/docs/public/ website

# Change into the git repository folder
cd website

# If nothing has changed, abort
if git diff-files --quiet --ignore-submodules --
then
  echo "The documentation website has not changed. Won't push stuff."
  exit 0
fi

# Push changes
git add -A
git commit -m "Nightly website build job"
git push origin master
