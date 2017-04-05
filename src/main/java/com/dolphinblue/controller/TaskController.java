package com.dolphinblue.controller;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Block.Type;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;
import com.dolphinblue.models.User;
import com.dolphinblue.service.*;

import java.util.ArrayList;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

/**
 * Created by FreddyEstevez on 3/29/17.
 * This controller should handle all requests for the task page.
 *
 * Request for a Task
 * - Request for Editor Blocks
 * - Request for Toolbox Blocks
 * Update Task
 * - Update Block Order
 * - Update Block Location for if it moves from Toolbox to Editor and the other way around
 * Restart Task
 * Restart Lesson
 * Update Lesson
 */

@Controller
public class TaskController {
    @Autowired
    LessonService lessonService;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    CodoUserService userService;

    /**
     * For debugging the block task pages
     * @param model
     * @return
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

        toolbox.add(new Block(new Long(1), "x = 2;", Type.ASSIGN, true));
        toolbox.add(new Block(new Long(2), "x += 5;", Type.ASSIGN, true));

        editor.add(new Block(new Long(2), "x += 1;", Type.ASSIGN, true));
        editor.add(new Block(new Long(2), "console.log(x);", Type.ASSIGN, true));

        model.addAttribute("lesson", l);
        model.addAttribute("toolbox", toolbox);
        model.addAttribute("editor", editor);
        model.addAttribute("task", t);

        return "block-task";
    }

    /**
     *
     */
    @RequestMapping(value = "/lesson/{lessonId}", method = RequestMethod.GET)
    public String get_task(@CookieValue("token") String token, @PathVariable(value = "lessonId") long lessonId, Model model){
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            //if the user isn't properly authenticated send them back to the login page
            return "redirect:login";
        }

        Objectify ofy = OfyService.ofy();
        Lesson l = ofy.load().type(Lesson.class).id(lessonId).now();
        List<Task> tasks = lessonService.get_tasks_by_id(l.getTasks());
        Task task = null;//need to get last task not completed
        for(int i = 0; i < tasks.size(); i++){
            if (!tasks.get(i).isCompleted()){
                task = tasks.get(i);//this task isnt completed
                break;
            }

        }
        if(task == null){
            task = tasks.get(tasks.size() - 1);
        }
        return "redirect:" + lessonId + "/task/" + task.getTask_id();
    }

    /**
     * gets tasks for a lesson
     * also gets the blocks for each task
     * 
     * TODO: Add a way to differentiate between which type of task to get.
     *
     * @param token - token of user to authenticate
     * @return
     */
    @RequestMapping(value = "/lesson/{lessonId}/task/{taskId}", method = RequestMethod.GET)
    public String get_task(@CookieValue(value = "token",defaultValue = "") String token, @PathVariable(value = "lessonId") long lessonId,@PathVariable(value = "taskId")long taskId,Model model){
        //grab the cookie and make sure the user is authenticated
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            //if the user isn't properly authenticated send them back to the login page
            return "redirect:login";
        }

        //if the user is authenticated get their id
        GoogleIdToken googleIdToken = authenticationService.getIdToken(token,new JacksonFactory(),new NetHttpTransport());
        String userId = userService.getUserId(googleIdToken);
        //TODO: we might need user and lesson id for stuff so I'm adding them in now

        Objectify ofy = OfyService.ofy();
        Lesson l = ofy.load().type(Lesson.class).id(lessonId).now();
        model.addAttribute("lesson", l);

        Task task = ofy.load().type(Task.class).id(taskId).now();
        model.addAttribute("task", task);
        System.out.println("OUTPUT: " + task.getExpected_output());

        List<Key<Block>> e_block_keys = task.getEditor();
        List<Block> editor_blocks = lessonService.get_blocks_by_id(e_block_keys);
        model.addAttribute("editor", editor_blocks);

        List<Key<Block>> t_block_keys = task.getToolbox();
        List<Block> toolbox_blocks = lessonService.get_blocks_by_id(t_block_keys);
        model.addAttribute("toolbox", toolbox_blocks);
        int index = l.getTasks().indexOf(Key.create(Task.class, task.getTask_id()));
        if(index != 0){
            //task is not the first one so there is a prev task
            model.addAttribute("prev_task", l.getTasks().get(index-1).getId());
        }else{
            //task is first task
            model.addAttribute("prev_task", -1);
        }
        if (index < l.getTasks().size()-1){
            //not last task so we have a next task
            model.addAttribute("next_task", l.getTasks().get(index+1).getId());
        }else{
            //task is last task, no next task
            model.addAttribute("next_task", -1);
        }

        return "block-task";
    }

    /**
     * Updates a task
     * @param task
     */
    @RequestMapping(value = "/updatetask", method = RequestMethod.POST)
    public void update_task(@RequestBody Task task){ // Figure out later getting blocks
        List<Key<Block>> editor_blocks = task.getEditor();
        List<Key<Block>> toolbox_blocks = task.getToolbox();

        lessonService.update_blocks(editor_blocks);

        OfyService.ofy().save().entity(task);
    }

    /**
     * Restarts lesson to original values.
     * @param token - user auth token
     * @param lessonId - lesson to restart
     * @param model
     * @return
     */
    @RequestMapping(value = "/restartlesson/{lessonId}", method = RequestMethod.GET)
    public String restart_lesson(@CookieValue("token") String token, @PathVariable(value = "lessonId") long lessonId, Model model){
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            //if the user isn't properly authenticated send them back to the login page
            return "redirect:login";
        }

        Objectify ofy = OfyService.ofy();
        //load lesson to be restarted
        Lesson l = ofy.load().type(Lesson.class).id(lessonId).now();

        //must set all tasks in lesson to original state
        List<Task> tasks = lessonService.get_tasks_by_id(l.getTasks());

        for(int i = 0; i < tasks.size(); i++){
            Task original = (Task) ofy.load().key(tasks.get(i).getOriginal_task()).now();
            //set to not completed
            Task task = tasks.get(i);
            task.setCompleted(false);
            //delete blocks
            for (int j = 0; j < task.getEditor().size(); j++){
                Block b = ofy.load().key(task.getEditor().get(j)).now();
                if (b.isCan_edit()){
                    //is not an original block so delete from datastore
                    ofy.delete().entity(b).now();
                }
            }
            for (int j = 0; j < task.getToolbox().size(); j++){
                Block b = ofy.load().key(task.getToolbox().get(j)).now();
                if (b.isCan_edit()){
                    //is not an original block so delete from datastore
                    ofy.delete().entity(b).now();
                }
            }

            //need to reset the toolbox for task
            task.setToolbox(original.getToolbox());
            //need to reset editor for task
            task.setEditor(original.getEditor());
            //save the changes to datastore
            ofy.save().entity(task).now();

        }
        //restarted the lesson so first task is needed
        Task task = tasks.get(0);
        //redirect
        return "redirect:" + lessonId + "/restarttask/" + task.getTask_id();
    }

    /**
     * restart a task
     * @param taskId the Id of the original task to retrieve from datastore
     * @param model
     * @return
     */
    @RequestMapping(value = "/restartlesson/{lessonId}/restarttask/{taskId}", method = RequestMethod.GET)
    public String restart_task(@CookieValue("token") String token, @PathVariable(value = "lessonId") Long lessonId, @PathVariable(value = "taskId") Long taskId, Model model){
        boolean isAuthenticated = authenticationService.isAuthenticated(token,new JacksonFactory(),new NetHttpTransport());
        if(!isAuthenticated){
            //if the user isn't properly authenticated send them back to the login page
            return "redirect:login";
        }

        Objectify ofy = OfyService.ofy();
        Lesson lesson = ofy.load().type(Lesson.class).id(lessonId).now();
        //task to be restarted
        Task task = ofy.load().type(Task.class).id(taskId).now();
        Task original_task = (Task) ofy.load().key(task.getOriginal_task()).now();
        task.setCompleted(false);
        //delete blocks
        for (int i = 0; i < task.getEditor().size(); i++){
            Block b = ofy.load().key(task.getEditor().get(i)).now();
            if (b.isCan_edit()){
                //is not an original block
                ofy.delete().entity(b).now();
            }
        }
        for (int i = 0; i < task.getToolbox().size(); i++){
            Block b = ofy.load().key(task.getToolbox().get(i)).now();
            if (b.isCan_edit()){
                //is not an original block so delete from datastore
                ofy.delete().entity(b).now();
            }
        }


        //reset toolbox
        task.setToolbox(original_task.getToolbox());
        //reset editor
        task.setEditor(original_task.getEditor());
        //save changes to this task
        ofy.save().entity(task).now();
        model.addAttribute("lesson", lesson);
        List<Key<Block>> e_block_keys = task.getEditor();
        List<Block> editor_blocks = lessonService.get_blocks_by_id(e_block_keys);

        model.addAttribute("editor", editor_blocks);

        List<Key<Block>> t_block_keys = task.getToolbox();
        List<Block> toolbox_blocks = lessonService.get_blocks_by_id(t_block_keys);
        model.addAttribute("toolbox", toolbox_blocks);
        model.addAttribute("task", task);

        int index = lesson.getTasks().indexOf(Key.create(Task.class, task.getTask_id()));
        if(index != 0){
            //task is not the first one so there is a prev task
            model.addAttribute("prev_task", lesson.getTasks().get(index-1));
        }else{
            //task is first task
            model.addAttribute("prev_task", -1);
        }
        if (index < lesson.getTasks().size()-1){
            //not last task so we have a next task
            model.addAttribute("next_task", lesson.getTasks().get(index+1));
        }else{
            //task is last task, no next task
            model.addAttribute("next_task", -1);
        }
        return "block-task";
    }

    /**
     * Makes a new lesson and saves it in the datastore
     * Currently all values of that lesson are the null.
     * @param model
     * @return
     */
    @RequestMapping(value = "/makelesson")
    public String make_lesson(Model model){
        Lesson l = new Lesson();
        model.addAttribute("lesson", l);
        OfyService.ofy().save().entity(l);
        return "index";
    }


    /**
     * Updates a lesson
     * @param model
     * @return
     */
    @RequestMapping(value = "/updatelesson", method = RequestMethod.POST)
    public String update_lesson(@RequestBody Lesson lesson, Model model){

        lesson.setPercent_complete(lessonService.get_percent_complete(lesson));
        OfyService.ofy().save().entity(lesson);
        return "lessoncard";
    }

    /**
     * Creates a lesson and saves it in the datastore via JSON.
     * NOTE: when supplying the path in url it should be WEB-INF/FILENAME.json
     * dont put quotes ex - "WEB-INF-/FILENAME.json"
     * @param path - path to json file
     * @return
     */
    @RequestMapping(value = "/jsonlesson")
    public String lessons_from_json(@RequestParam(value = "path") String path){
        Lesson l = LessonJSONService.create_lesson_from_JSON(path);
        OfyService.ofy().save().entity(l).now();
        return "index";
    }
}
