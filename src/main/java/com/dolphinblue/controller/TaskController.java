package com.dolphinblue.controller;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Block.Type;
import com.dolphinblue.models.SaveTaskModel;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.service.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by FreddyEstevez on 3/29/17.
 *
 * This controller handles all requests for the task page.
 */
@Controller
@EnableWebMvc
public class TaskController {
    // Use Autowired to get an instance of the different Services
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;

    /**
     *  This is for debugging a block task
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/debug-block-task", method = RequestMethod.GET)
    public String get_task(Model model){
        Lesson l = new Lesson();
        l.setTitle("First Lesson");
        Task t = new Task();
        t.setTitle("First Block Task");
        t.setInstructions("These are the instructions.");
        t.setHint("This is the hint.");
        t.setTest_case("x = 1;");
        t.setExpected_output("3");

        ArrayList<Block> toolbox = new ArrayList<Block>();
        ArrayList<Block> editor = new ArrayList<Block>();

        toolbox.add(new Block(1, "x = 2;", Type.ASSIGN, true));
        toolbox.add(new Block(2, "x += 5;", Type.ASSIGN, true));

        editor.add(new Block(2, "x += 1;", Type.ASSIGN, true));
        editor.add(new Block(2, "console.log(x);", Type.ASSIGN, true));

        model.addAttribute("lesson", l);
        model.addAttribute("toolbox", toolbox);
        model.addAttribute("editor", editor);
        model.addAttribute("task", t);

        return "block-task";
    }

    /**
     * This method gets all of the information for a lesson to populate a lesson page
     * @param token -- the login token of the user
     * @param lessonId -- the lesson id to load
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.GET)
    public String get_lesson(@CookieValue("token") String token, @PathVariable(value = "lessonId") long lessonId, Model model){
        // Check to see if the user is authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:/login";
        }

        // Create an objectify object to make requests to the datastore
        Objectify ofy = OfyService.ofy();

        // Pull the lesson information from the datastore
        Lesson l = ofy.load().type(Lesson.class).id(lessonId).now();

        // Get all of the tasks for the lesson
        List<Task> tasks = lessonService.get_tasks_by_id(l.getTasks());
        // Find the last task that isn't completed
        Task task = null;
        for(int i = 0; i < tasks.size(); i++) {
            if (!tasks.get(i).isCompleted()) {
                // This task isn't completed
                task = tasks.get(i);
                break;
            }
        }
        // Check to see if any task was found
        if(task == null) {
            // If no task was found, populate the last completed task
            task = tasks.get(tasks.size() - 1);
        }

        // Populate the HTML lesson page with the correct task
        String requestUrl = "/lesson/" + lessonId + "/task/" + task.getTask_id();
        return "redirect:" + requestUrl;
    }

    /**
     * This method gets all of the information for a task to populate a task page
     * @param token -- the login token of the user
     * @param lessonId -- the lesson id from which the task belongs
     * @param taskId -- the task id to load
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     *
     * TODO: Add a way to differentiate between which type of task to get.
     */
    @RequestMapping(value = "/lesson/{lessonId}/task/{taskId}", method = RequestMethod.GET)
    public String get_task(@CookieValue(value = "token",defaultValue = "") String token, @PathVariable(value = "lessonId") long lessonId,@PathVariable(value = "taskId")long taskId,Model model){
        // Grab the cookie and make sure the user is authenticated
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:/login";
        }

        // If the user is authenticated get their id
        GoogleIdToken googleIdToken = authenticationService.getIdToken(token,new JacksonFactory(),new NetHttpTransport());
        String userId = userService.getUserId(googleIdToken);
        // TODO: we might need user and lesson id for stuff so I'm adding them in now

        // Create the objectify object to get stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Load the lesson from the datastore and add it to the thymeleaf model
        Lesson l = ofy.load().type(Lesson.class).id(lessonId).now();
        model.addAttribute("lesson", l);

        //load all tasks to check which are completed
        Map<Key<Task>, Task> mapOfTasks = ofy.load().keys(l.getTasks());
        //list of tasks in lesson
        List<Task> t = new ArrayList<>(mapOfTasks.values());
        //list of booleans checking if task is completed
        List<Boolean> taskStatusList = new ArrayList<>();
        //list of task title
        List<String> taskTitleList = new ArrayList<>();

        //iterate through tasks
        for (int i = 0; i < t.size(); i++){
            taskStatusList.add(t.get(i).isCompleted());
            taskTitleList.add(t.get(i).getTitle());
        }
        //load booleans and titles into thymeleaf model
        model.addAttribute("tasks_status", taskStatusList);
        model.addAttribute("tasks_titles", taskTitleList);

        // Load the task from the datastore and add it to the thymeleaf model
        Task task = ofy.load().type(Task.class).id(taskId).now();
        model.addAttribute("task", task);
        System.out.println("OUTPUT: " + task.getExpected_output());

        // Load the editor blocks for the task and add them to the thymeleaf model
        List<Key<Block>> e_block_keys = task.getEditor();
        List<Block> editor_blocks = lessonService.get_blocks_by_id(e_block_keys);
        model.addAttribute("editor", editor_blocks);

        // Load the toolbox blocks for the task and add them to the thymeleaf model
        List<Key<Block>> t_block_keys = task.getToolbox();
        List<Block> toolbox_blocks = lessonService.get_blocks_by_id(t_block_keys);
        model.addAttribute("toolbox", toolbox_blocks);

        // Get the index for the navigation bar in the lesson
        int index = l.getTasks().indexOf(Key.create(Task.class, task.getTask_id()));
        // Set the previous tasks for the lesson
        if(index != 0) {
            // This task is not the first one so there is a prev task
            model.addAttribute("prev_task", l.getTasks().get(index-1).getId());
        } else {
            // This task is the first task
            model.addAttribute("prev_task", -1);
        }

        // Set the next tasks for the lesson
        if (index < l.getTasks().size()-1){
            // Not last task so we have a next task
            model.addAttribute("next_task", l.getTasks().get(index+1).getId());
        }else{
            // The task is the last task, no next task
            model.addAttribute("next_task", -1);
        }

        // Populate the HTML lesson page with the correct task
        return "block-task";
    }

    /**
     * Updates the task for a lesson
     * @param token -- the login token of the user
     * @param taskId -- the id of the task to be updates
     * @param blocks -- the blocks within the task that need updating
     * @return
     */
    @RequestMapping(value = "/savelesson/{lessonId}/task/{taskId}",  method = RequestMethod.POST)
    public @ResponseBody
    SaveTaskModel update_task(@CookieValue("token") String token, @PathVariable(value = "taskId") Long taskId, @RequestBody SaveTaskModel blocks) {
        // Check if the user is still authenticated by google
        System.out.println("MATT");
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return null;
        }
        System.out.println("Blocks:");
        System.out.println(blocks);
        // Create the objectify object to get stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Get the original task
        Task task = ofy.load().type(Task.class).id(taskId).now();
        // Update the boolean value for this task
        task.setCompleted(blocks.getCompleted());

        // Update the blocks for this task
        List<Key<Block>> editor = lessonService.update_blocks(task.getTask_id(), blocks.getEditor().getBlocks());
        List<Key<Block>> toolbox = lessonService.update_blocks(task.getTask_id(), blocks.getToolbox().getBlocks());

        // Set the new block keys to the task
        task.setEditor(editor);
        task.setToolbox(toolbox);

        // Save the changes to the datastore
        ofy.save().entity(task);
        return blocks;

    }

    /**
     * Method for restarting a lesson
     * @param token -- the login token of the user
     * @param lessonId -- the lesson id of the lesson that is being reset
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/restartlesson/{lessonId}", method = RequestMethod.GET)
    public String restart_lesson(@CookieValue("token") String token, @PathVariable(value = "lessonId") long lessonId, Model model){
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:/login";
        }

        // Create an objectify object to load things from the datastore
        Objectify ofy = OfyService.ofy();
        // Load the lesson to be restarted
        Lesson l = ofy.load().type(Lesson.class).id(lessonId).now();

        // Set all of the tasks in the lesson to their original state
        List<Task> tasks = lessonService.get_tasks_by_id(l.getTasks());

        // Reset all of the tasks for the lesson
        for(int i = 0; i < tasks.size(); i++) {
            // Get the task and call the reset task method from the lesson service
            Task task = tasks.get(i);
            lessonService.reset_task(task);
        }

        // Get the first task to populate for the reset lesson
        Task task = tasks.get(0);

        // Redirect back to the first task in the reset lesson
        String requestUrl = "/lesson/" + lessonId + "/task/" + task.getTask_id();
        return "redirect:" + requestUrl;
    }

    /**
     * Restart a single task within a lesson
     * @param token -- the login token of the user
     * @param lessonId -- the lesson id from which the task belongs
     * @param taskId -- the task id of the task that should be reset
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/restartlesson/{lessonId}/restarttask/{taskId}", method = RequestMethod.GET)
    public String restart_task(@CookieValue("token") String token, @PathVariable(value = "lessonId") Long lessonId, @PathVariable(value = "taskId") Long taskId, Model model) {
        // Check to see if the user is authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token, new JacksonFactory(), new NetHttpTransport());
        if (!isAuthenticated) {
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:/login";
        }

        // Create the objectify object to load things from the datastore
        Objectify ofy = OfyService.ofy();
        // Load the lesson of the task, and the task itself, from the datastore
        Lesson lesson = ofy.load().type(Lesson.class).id(lessonId).now();
        Task old_task = ofy.load().type(Task.class).id(taskId).now();

        // Reset the task using the lesson service
        Task new_task = lessonService.reset_task(old_task);

        // // Put the lesson in the thymeleaf model
        // model.addAttribute("lesson", lesson);

        // // Get the editor blocks and put them in the thymeleaf model
        // List<Key<Block>> e_block_keys = new_task.getEditor();
        // List<Block> editor_blocks = lessonService.get_blocks_by_id(e_block_keys);
        // model.addAttribute("editor", editor_blocks);

        // // Get the toolbox blocks and put them in the thymeleaf model
        // List<Key<Block>> t_block_keys = new_task.getToolbox();
        // List<Block> toolbox_blocks = lessonService.get_blocks_by_id(t_block_keys);
        // model.addAttribute("toolbox", toolbox_blocks);

        // // Put the task in the thymeleaf model
        // model.addAttribute("task", new_task);

        // // Get the index for the lesson navigation bar
        // int index = lesson.getTasks().indexOf(Key.create(Task.class, new_task.getTask_id()));
        // if (index != 0) {
        //     // If the task is not the first one there is a prev task
        //     model.addAttribute("prev_task", lesson.getTasks().get(index - 1).getId());
        // } else {
        //     // If the task is the first task, there is not prev task
        //     model.addAttribute("prev_task", -1);
        // }
        // if (index < lesson.getTasks().size() - 1) {
        //     // If the task is not the last task, we have a next task
        //     model.addAttribute("next_task", lesson.getTasks().get(index + 1).getId());
        // } else {
        //     // If the task is the last task, there is no next task
        //     model.addAttribute("next_task", -1);
        // }

        // Return the HTML page to be loaded
        String requestUrl = "/lesson/" + lessonId + "/task/" + taskId;
        return "redirect:" + requestUrl;
    }

    /**
     * Creates a lesson and saves it in the datastore via JSON.
     * NOTE: when supplying the path in url it should be WEB-INF/FILENAME.json
     * dont put quotes ex - "WEB-INF-/FILENAME.json"
     * @param path - path to json file
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/jsonlesson")
    public String lessons_from_json(@RequestParam(value = "path") String path) {
        // Create a lesson from JSON
        LessonJSONService.create_lesson_from_JSON(path);

        // Return the HTML page to be loaded
        return "index";
    }


    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public @ResponseBody
    SaveTaskModel test(@RequestBody SaveTaskModel list){
        System.out.println(list.toString());
        return list;

    }

    @RequestMapping(value="/freecode" , method=RequestMethod.GET)
    public String getFreeCode(@CookieValue("token")String token,Model model){
        //TODO: change the route this takes
        // Check to see if the user is authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token, new JacksonFactory(), new NetHttpTransport());
        if (!isAuthenticated) {
            // If the user isn't properly authenticated send them back to the login page
            return "redirect:/login";
        }

        //Make a dummy task
        Task t = new Task();
        Lesson l = new Lesson();
        model.addAttribute("task",t);
        model.addAttribute("lesson",l);
        model.addAttribute("prev_task",-1);
        model.addAttribute("next_task",-1);
        return "freecode";
    }

}
