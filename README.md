# TaskBuddy
## Scheduler/Tasking app for email power users! ##

### Git workflow and standards ###
>Reference : http://nvie.com/posts/a-successful-git-branching-model/

1) 3 main branches to be careful of - master, dev, staging

> Master reflects the live and production ready code. There should not be any direct commits to this branch except for emergency bug fixing. Staging merges into master and the subsequent commit will be tagged with a release version.

> Dev reflects new development work, which may be stable or unstable. Feature and hotfix branches should branch off dev.

> Staging is essentially a testing branch. After everyone has tested their own feature branch, merge into staging and test again. Once everything is working TOGETHER, merge staging into master and dev.

>To ensure dev is not too far off from master, encourage short development cycles of 1-2 weeks and bite-sized feature branches. 

2) Development cycle means branch - code on feature - individual testing - merge into staging - peer review and testing - push to dev and master.

3) Branch naming convention

> feature branch - prefix with feature, followed by description of feature branch
>> E.g. feature-allow-search-by-tags

> hotfix branch (bug fixes) - prefix with hotfix, followed by description of the bug
>> E.g. hotfix-empty-calendar-throws-null-exception

### Outstanding tasks and issues ###

1)  command line parsing - allow for "this is, a parameter", " and this is another parameter" parsing
2)  file I/O - implement a way to read tasks from a file and write tasks to the same file, suggested : JSON files using JSON-simple
> Reference : http://www.mkyong.com/java/json-simple-example-read-and-write-json/
3)  Maintain and update Timetable of tasks/periods that is organized chronologically.
  1) Implement a comparator function that compares 2 periods and returns whether one is precedes the other
  2) Do we allow for multiple tasks to occupy same Timetable slots?
4) Support undo operation.
  1) Maintain a stack of the 20 most recent user commands that resulted in a write to file
  2) Implement a stack of the most recent file states?
5)  Email integration - connect to a user-specified mailbox + parse emails to extract tasks, suggested resource : SIFT api from EasilyDo
> Reference : https://developer.easilydo.com/sift/documentation#discovery-endpoint
6)  Flexible data entry for users - how do we allow a broad range of user inputs for dates and be able to infer what the user means?
7)  Add and remove tags to a task to categorize them
8)  Search by keywords in description and tags
9)  Add iOS notifications to alert user of a upcoming deadline at x hours before the deadline
10) GUI - nice Graphical User Interface

### Commenting and documentation standards ###

To be added by JP! :)
