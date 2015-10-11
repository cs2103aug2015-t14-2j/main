# TaskBuddy
## Scheduler/Tasking app for email power users! ##

### Launching of application ###

1. From the command line, navigate to the project root, and type in the following:

For linux filesystems:

java -cp '.:./lib/*' Task.TaskHandler

For Windows filesystems:

java -cp ".;./lib/*" Task.TaskHandler

2. Support for launch through keyboard shortcut will be released in v0.3.

### Git workflow and standards ###
>Reference : http://nvie.com/posts/a-successful-git-branching-model/

1.  main branches to be careful of - master, dev, staging

 Master reflects the live and production ready code. There should not be any direct commits to this branch except for emergency bug fixing. Staging merges into master and the subsequent commit will be tagged with a release version.

 Dev reflects new development work, which may be stable or unstable. Feature and hotfix branches should branch off dev.

 Staging is essentially a testing branch. After everyone has tested their own feature branch, merge into staging and test again. Once everything is working TOGETHER, merge staging into master and dev.

 To ensure dev is not too far off from master, encourage short development cycles of 1-2 weeks and bite-sized feature branches. 

2. Development cycle means branch - code on feature - individual testing - merge into staging - peer review and testing - push to dev and master.

3. Branch naming convention

 feature branch - prefix with feature, followed by description of feature branch

>E.g. feature-allow-search-by-tags

 hotfix branch (bug fixes) - prefix with hotfix,  followed by description of the bug

>E.g. hotfix-empty-calendar-throws-null-exception

4. When something can be encapsulated into its own object create a new class

### Outstanding tasks and issues ###

1.  command line parsing - allow for "this is, a parameter", " and this is another parameter" parsing.
2.  User manual, deadline 7th September 2015
3.  file I/O - implement a way to read tasks from a file and write tasks to the same file, suggested : JSON files using JSON-simple
> Reference : http://www.mkyong.com/java/json-simple-example-read-and-write-json/

4.  Maintain and update Timetable of tasks/periods that is organized chronologically.
  1. Implement a comparator function that compares 2 periods and returns whether one is precedes the other
  2. Do we allow for multiple tasks to occupy same Timetable slots?
5. Support undo operation.
  1. Maintain a stack of the 20 most recent user commands that resulted in a write to file
  2. Implement a stack of the most recent file states?
6.  Email integration - connect to a user-specified mailbox + parse emails to extract tasks, suggested resource : SIFT api from EasilyDo
> Reference : https://developer.easilydo.com/sift/documentation#discovery-endpoint
7.  Flexible data entry for users - how do we allow a broad range of user inputs for dates and be able to infer what the user means?
8.  Add and remove tags to a task to categorize them
9.  Search by keywords in description and tags
10.  Add iOS notifications to alert user of a upcoming deadline at x hours before the deadline
11. GUI - nice Graphical User Interface

### Commenting and documentation standards ###

1. Include comments at the top of every class to explain what the class is about

```
/** 
 * @author MATRIC NUM NAME
 * 
 *  This class describes a task. It has...
 *  Here is more comment describing how to use this class
 */
```

2. Add a JavaDoc before every function and global variable

```
/**
 * @author Jerry Tan
 * @params userInput    
 * @params numArguments
 */
```

3. Include inline comments whenever its not explicit

```
public static final String ADD_MESSAGE = "added!"    // Message to display to user after successful add operation
```

4. Every loop should have a comment regarding the goal of the loop

```
// Finds scheduled tasks
for(int i = 0; i < taskList.length(); i++){
	if (taskList.type ==  SCHEDULED_TASK){
		return taskList[i];
	}	
}
```


5. Avoid third level nested loops

```
// Finds the first 'a' char in a tag
for(int i = 0; i < taskList.length(); i++){
	for(int j = 0; i < taskList[i].tags.length(); j++){
		for(int k = 0; k < taskList[i].tags[j].length() ; k++){
			if(taskList[i].tags[j][k] == 'a'){
				return taskList[i].tags[j]; //Avoid this
			}
		}
	}
}
```


### Testing standards ###

1. @ tags
Preceed each test with @test and setup tear down with appropriate tags

  @Test
  public void printString() {

    // assert statements
    assertEquals("10 x 0 must be 0", 0, tester.multiply(10, 0));

  }

2. Testing command line outputs
A suggested way of doing so is the following:

    @Rule
    public final StandardOutputStreamLog printedText = new StandardOutputStreamLog();

    @Test
    public void HelloWorld() {
        System.out.print("hello world");
        assertEquals("hello world", log.getLog());
    }

}

3. Private method implicit testing
When not possible to access a method use other methods to proove full functionality

4. Null, 0, 1, many tests
The basic tests for most methods

