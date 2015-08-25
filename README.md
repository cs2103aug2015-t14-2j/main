# TaskBuddy
Scheduler/Tasking app for email power users!

/* Git workflow and standards */
Reference : http://nvie.com/posts/a-successful-git-branching-model/

1) 3 main branches to be careful of - master, dev, staging

Master reflects the live and production ready code. There should not be any direct commits to this branch except for emergency bug fixing. Staging merges into master and the subsequent commit will be tagged with a release version.

Dev reflects new development work, which may be stable or unstable. Feature and hotfix branches should branch off  
dev.

Staging is essentially a testing branch. After everyone has tested their own feature branch, merge into staging and test again. Once everything is working TOGETHER, merge staging into master and dev.

To ensure dev is not too far off from master, encourage short development cycles of 1-2 weeks and bite-sized feature branches. 

2) Development cycle means branch - code on feature - individual testing - merge into staging - peer review and testing - push to dev and master.

3) Branch naming convention

feature branch - prefix with feature, followed by description of feature branch
E.g. feature-allow-search-by-tags

hotfix branch (bug fixes) - prefix with hotfix, followed by description of the bug
E.g. hotfix-empty-calendar-throws-null-exception
