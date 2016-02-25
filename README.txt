TheGreatTwitter is an application that works like a simplified Twitter.

The domain model supports accounts, messages, and account following.
Messages are posted by accounts and an account can follow another account.

For each assignment a corresponding tag is created, for instance “assignment1” tag was added for the initial domain model assignment.

Steps to commit your files:
1. "git add ." (add all changed files)
    or "git add path/to/the/file" to add the individual file.
2. "git status" to view the status of the files
3. "git commit -m "Importing my code" -a" (this command will commit all the files you added above)
4. "git push -u origin master" (this command will publish all the changes you made to bitbucket master branch)

Additional useful git commands:
git reset --hard origin/master
git tag -a tag -m “my tag”

====to merge local changes
git stash
git pull
git stash pop
then check through gitgui or in intelij