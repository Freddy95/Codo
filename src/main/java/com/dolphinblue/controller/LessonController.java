package com.dolphinblue.controller;

import com.dolphinblue.models.*;
import com.dolphinblue.models.Block.Type;
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

import java.util.*;


/**
 * Created by FreddyEstevez on 3/29/17.
 *
 * This controller handles all requests for the task page.
 */
@Controller
@EnableWebMvc
public class LessonController {
    // Use Autowired to get an instance of the different Services
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;
    @Autowired
    TaskService taskService;

    /**
     *  This is for debugging a block task
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/debug-edit-task", method = RequestMethod.GET)
    public String debug_edit_task(Model model){
        Lesson l = new Lesson();
        l.setTitle("First Lesson");

        Task t = new Task();
        t.setTitle("First Block Task");
        t.setInstructions("These are the instructions.");
        t.setHint("This is the hint.");

        String[] test = {"x = 1;"};
        String[] expected = {"3"};
        t.setTest_case(Arrays.asList(test));
        t.setExpected_output(Arrays.asList(expected));


        List<Block> toolbox = new ArrayList<Block>();
        List<Block> editor = new ArrayList<Block>();
        List<Block> catalog = taskService.get_catalog();

        toolbox.add(new Block(1, "x = 2;", Type.STATIC, false));
        toolbox.add(new Block(2, "x += 5;", Type.STATIC, false));
        toolbox.add(new Block(3, "x > 5;", Type.STATIC, false));
        toolbox.add(new Block(4, "blah;", Type.IF, true));

        Block testChild = new Block(5, "butts;", Type.FOR, true);
        testChild.addChild(new Block(6, "x = 2;", Type.STATIC, false));
        testChild.addChild(new Block(7, "butts;", Type.LOG, true));


        editor.add(new Block(8, "x += 1;", Type.STATIC, false));
        editor.add(new Block(9, "console.log(x);", Type.STATIC, false));
        editor.add(testChild);

        model.addAttribute("lesson", l);
        model.addAttribute("toolbox", toolbox);
        model.addAttribute("editor", editor);
        model.addAttribute("catalog", catalog);
        model.addAttribute("task", t);

        return "edit-block-task";
    }

    /**
     *  This is for debugging a block task
     * @param model -- the thymeleaf model used to send data to the front end
     * @return -- the HTML page to be loaded
     */
    @RequestMapping(value = "/debug-block-task", method = RequestMethod.GET)
    public String get_task(Model model){
        Task t = new Task();
        t.setTitle("First Block Task");
        t.setInstructions("These are the instructions.");
        t.setHint("This is the hint.");
        String[] test = {"x = 1;"};
        String[] expected = {"3"};
        t.setTest_case(Arrays.asList(test));
        t.setExpected_output(Arrays.asList(expected));

        ArrayList<Block> toolbox = new ArrayList<Block>();
        ArrayList<Block> editor = new ArrayList<Block>();

        toolbox.add(new Block(1, "x = 2;", Type.STATIC, false));
        toolbox.add(new Block(2, "x += 5;", Type.STATIC, false));
        toolbox.add(new Block(3, "blah;", Type.IF, true));
        toolbox.add(new Block(4, "butts;", Type.FOR, true));
        toolbox.add(new Block(4, "{", Type.STATIC, false));
        toolbox.add(new Block(4, "}", Type.STATIC, false));


        editor.add(new Block(2, "x += 1;", Type.STATIC, false));
        editor.add(new Block(2, "console.log(x);", Type.STATIC, false));

        // model.addAttribute("lesson", l);
        model.addAttribute("toolbox", toolbox);
        model.addAttribute("editor", editor);
        model.addAttribute("task", t);

        return "debug-block-task";
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
        Lesson original_lesson = (Lesson) ofy.load().key(l.getOriginal_lesson()).now();
        if(original_lesson.getLast_edited().compareTo(l.getLast_accessed()) > 0){
            //the lesson has been changed
            System.out.println("LESSON UPDATED");
            //TODO: Make prompt to user to see if they want to continue with old version.
        }
        l.setLast_accessed(new Date());
        model.addAttribute("lesson", l);

        //get task titles and check to see which tasks have already been started
        taskService.get_task_navigation(l, model);

        // Load the task from the datastore and add it to the thymeleaf model
        Task task = ofy.load().type(Task.class).id(taskId).now();

        //get next and previous task
        taskService.get_next_task(task, l, model);
        taskService.get_previous_task(task, l, model);

        //task is being worked on so it is in progress
        task.setIn_progress(true);
        //save change
        ofy.save().entity(task).now();

        model.addAttribute("task", task);

        // Load the user's information from the datastore and store it in a user object
        User user = ofy.load().type(User.class).id(userId).now();

        // Add the user information to the thymeleaf model
        model.addAttribute("new_lesson", user.isFirst_lesson());

        // Check to see if the user is a new user
        if (user.isFirst_lesson()) {
            user.setFirst_lesson(false);
            ofy.save().entity(user).now();
            System.out.println("making false");
        }

        //check type of task
        if(task.getFreecode() == null){
            //this is a block task
            // Load the editor blocks for the task and add them to the thymeleaf model
            List<Key<Block>> e_block_keys = task.getEditor();
            List<Block> editor_blocks = lessonService.get_blocks_by_id(e_block_keys);
            model.addAttribute("editor", editor_blocks);

            // Load the toolbox blocks for the task and add them to the thymeleaf model
            List<Key<Block>> t_block_keys = task.getToolbox();
            List<Block> toolbox_blocks = lessonService.get_blocks_by_id(t_block_keys);
            model.addAttribute("toolbox", toolbox_blocks);
            // Populate the HTML lesson page with the correct task
            return "block-task";
        }
        else{
            //its a freecode task
            model.addAttribute("freecode", task.getFreecode());
            //TODO: Add more attributes to model?
            return "freecode";
        }
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
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return null;
        }
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



    @RequestMapping(value = "/savelesson/{lessonId}/freecodetask/{taskId}",  method = RequestMethod.POST)
    public @ResponseBody
    SaveTaskModel update_freecode_task(@CookieValue("token") String token, @PathVariable(value = "taskId") Long taskId, @RequestBody SaveTaskModel taskModel) {
        // Check if the user is still authenticated by google
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            // If the user isn't properly authenticated send them back to the login page
            return null;
        }
        // Create the objectify object to get stuff from the datastore
        Objectify ofy = OfyService.ofy();

        // Get the original task
        Task task = ofy.load().type(Task.class).id(taskId).now();
        // Update the boolean value for this task
        task.setCompleted(taskModel.getCompleted());
        //update freecode
        task.setFreecode(taskModel.getFreecode());

        // Save the changes to the datastore
        ofy.save().entity(task);
        return taskModel;

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
        Lesson l = new Lesson();
        l.setTitle("First Lesson");
        Task t = new Task();
        t.setTitle("First Block Task");
        t.setInstructions("These are the instructions.");
        t.setHint("This is the hint.");
        String[] test = {"x = 1;"};
        String[] expected = {"3"};
        t.setTest_case(Arrays.asList(test));
        t.setExpected_output(Arrays.asList(expected));

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

        model.addAttribute("prev_task", -1);
        model.addAttribute("next_task", -1);

        return "freecode";
    }

}