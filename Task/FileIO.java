package Task;

import java.io.*;
import java.io.IOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author Jerry
 *
 * Class that takes care of file input and output.
 * Reads Tasks from a JSON file and converts to ArrayList<Tasks> in memory
 * Writes Tasks from memory to the same JSON file
 * Keeps track of the latest task_id so it can incremented from when instantiating new tasks in memory
 * Uses GSON, see imports above
 */
public class FileIO {
	private static Calendar        calendar          = Calendar.getInstance();
	private static SimpleDateFormat dateFormat       = new SimpleDateFormat("EEE, dd MMM, yyyy HHmm");
	
	// Error messages
	private final static String ERROR_MALFORMED_TASK = "ERROR! Corrupted task region. Task %d has been discarded.\n";
	private final static String ERROR_MALFORMED_FILE = "ERROR! Corrupted file region. Rest of file cannot be read.\n";
	private final static String ERROR_MALFORMED_KEY  = "ERROR! File does not match expected format. Restart program with a new file location.\n";
	private final static String ERROR_FILE_IO        = "ERROR! Cannot read from specified file location. Quit and restart. Exiting program...\n";
	private int currentTaskId;
	private String path;
	
	public FileIO(String path) {
		dateFormat.setCalendar(calendar);
		this.path = path;
		this.currentTaskId = 0;
	}
	
	/**
	 * Reads from file in the filepath. Expects valid JSON format
	 * @return   ArrayList<Task>
	 */
	public ArrayList<Task> readFromFile() {
		ArrayList<Task> taskList = new ArrayList<Task>();
		try {
			FileReader reader = new FileReader(this.path);
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				String name = jsonReader.nextName();
				if (name.equals("Tasks")) {
					jsonReader.beginArray();
					while (jsonReader.hasNext()) {
						try {
							Task task = getJSONTaskFromFile(jsonReader);
							assert(task!=null);			// Very important that task not be null here
							if (task != null) {
								taskList.add(task);
							}						
						} catch (ParseException e) {
							System.out.format(ERROR_MALFORMED_TASK, currentTaskId);
						} catch (MalformedJsonException e1) {
							System.out.format(ERROR_MALFORMED_FILE);
						} catch (IllegalStateException e2) {
							System.out.format(ERROR_MALFORMED_KEY);
							System.exit(0);
						}
					}
					jsonReader.endArray();
				}
			}
			jsonReader.endObject();		
			reader.close();
		} catch (FileNotFoundException e) {
			// Create an empty file if file is not found
			createNewFile();
		} catch (MalformedJsonException e1) {
			System.out.format(ERROR_MALFORMED_FILE);
		} catch (IllegalStateException e2) {
			System.out.format(ERROR_MALFORMED_KEY);
			System.exit(0);
		} catch (IOException e3) {
			System.out.format(ERROR_FILE_IO);
			System.exit(0);						
		}
		return taskList;
	}

	/**
	 * 
	 * @param taskList 	A list of tasks to write to. Will overwrite entire file
	 */
	public void writeToFile(ArrayList<Task> taskList) {
		try {
			FileWriter writer = new FileWriter(this.path);
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("    ");

			jsonWriter.beginObject();

			jsonWriter.name("Tasks");
			jsonWriter.beginArray();
			Iterator<Task> taskIterator = taskList.iterator();
			while (taskIterator.hasNext()) {
				Task currentTask = taskIterator.next();
				writeTaskToFile(jsonWriter, currentTask);
			}
			jsonWriter.endArray();
			jsonWriter.endObject();
			jsonWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createNewFile() {
		try {
			FileWriter writer = new FileWriter(this.path);
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("    ");

			jsonWriter.beginObject();
			jsonWriter.name("Tasks");
				jsonWriter.beginArray();
				jsonWriter.endArray();
			jsonWriter.endObject();
			jsonWriter.close();

			readFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int getCurrentTaskId() {
		return currentTaskId;
	}
	
	public String getFilePath() {
		return path;
	}

	/**
	 * Parses a JSON task object and instantiates it.
	 * 
	 * @param 	JsonReader jsonReader
	 * @return 	Task 	   if parsing error occurs, returns a null task. Informs user of problem.
	 */
	private Task getJSONTaskFromFile(JsonReader jsonReader) throws MalformedJsonException , ParseException, IllegalStateException, IOException {
		Task task = new Task(0, "Error while parsing file. Your file might be corrupted.", null);
		int taskId              = 1;
		String createdTime      = "";
		String lastModifiedTime = "";
		String startTime        = "";
		String endTime          = "";
		String deadline         = "";
		String venue            = "";
		String description      = "";
		boolean isDone          = false;
		boolean isPastDeadline  = false;
		boolean hasEnded        = false;
		
		try {				
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				String key = jsonReader.nextName();
				switch (key) {
					case "taskId" :
						taskId = jsonReader.nextInt();
						break;
					case "createdTime" :
						createdTime = parseNullOrString(jsonReader);
						break;
					case "lastModifiedTime" :
						lastModifiedTime = parseNullOrString(jsonReader);
						break;
					case "startTime" :
						startTime = parseNullOrString(jsonReader);
						break;
					case "endTime" :
						endTime = parseNullOrString(jsonReader);
						break;
					case "deadline" : 
						deadline = parseNullOrString(jsonReader);
						break;
					case "venue" : 
						venue = parseNullOrString(jsonReader);
						break;
					case "description" : 
						description = parseNullOrString(jsonReader);
						break;
					case "isDone" :
						isDone = jsonReader.nextBoolean();
						break;
					case "isPastDeadline" :
						isPastDeadline = jsonReader.nextBoolean();
						break;
					case "hasEnded" :
						hasEnded = jsonReader.nextBoolean();
						break;
					default:
						break;
					
				}
			}
			jsonReader.endObject();
			currentTaskId = taskId;
			
			Date createdTimeDate      = parseStringToDate(createdTime);
			Date lastModifiedTimeDate = parseStringToDate(lastModifiedTime);
			Date deadlineDate         = parseStringToDate(deadline);
			Date startDate            = parseStringToDate(startTime);
			Date endDate              = parseStringToDate(endTime);
			task = new Task(createdTimeDate, lastModifiedTimeDate, taskId, description, startDate, endDate, deadlineDate, venue, isDone, isPastDeadline, hasEnded);

		} catch (IllegalStateException  | 
			     MalformedJsonException | 
			     ParseException e1) {
			throw e1;
		} 
		System.out.println(task);
		return task;
	}
	
	private Date parseStringToDate(String dateString) throws ParseException {
		if (dateString == null) {
			return null;
		} else {
			return dateFormat.parse(dateString);
		}
	}
	
	/**
	 * Helper function to write a single Task to file
	 * 
	 * @param jsonWriter
	 * @param currentTask
	 */
	private void writeTaskToFile(JsonWriter jsonWriter, Task currentTask) throws IOException {
		jsonWriter.beginObject();
		jsonWriter.name("taskId").value(currentTask.getTaskId());
		jsonWriter.name("createdTime").value(toNullOrDateString(currentTask.getCreatedTime()));
		jsonWriter.name("lastModifiedTime").value(toNullOrDateString(currentTask.getModifiedTime()));
		jsonWriter.name("startTime").value(toNullOrDateString(currentTask.getStartTime()));
		jsonWriter.name("endTime").value(toNullOrDateString(currentTask.getEndTime()));
		jsonWriter.name("deadline").value(toNullOrDateString(currentTask.getDeadline()));
		jsonWriter.name("venue").value(currentTask.getVenue());
		jsonWriter.name("description").value(currentTask.getDescription());
		jsonWriter.name("isDone").value(currentTask.isDone());
		jsonWriter.name("isPastDeadline").value(currentTask.isPastDeadline());
		jsonWriter.name("hasEnded").value(currentTask.isHasEnded());
		
		jsonWriter.endObject();
	}
	
	// Utility method
	private String parseNullOrString(JsonReader jsonReader) throws IOException {
		if (jsonReader.peek() == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else {
			return jsonReader.nextString();
		}
	}
	
	// Utility method
	private String toNullOrDateString(Date date) {
		if (date == null) {
			return null;
		} else {
			return dateFormat.format(date);
		}
	}
}