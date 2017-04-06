package com.dolphinblue.service;

import com.dolphinblue.models.*;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import com.googlecode.objectify.cmd.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devon on 4/2/17.
 *
 * This class provides calculation services for the controller classes
 */
@Service
public class LessonService {

    public double get_percent_complete(Lesson lesson) {
        Objectify ofy = OfyService.ofy();

        List<Key<Task>> tasks = lesson.getTasks();

        int total = tasks.size();
        int count = 0;

        for(int i = 0; i < tasks.size(); i++) {
            Key task_key = tasks.get(i);
            Task task = ofy.load().type(Task.class).id(task_key.getId()).now();
            if(task.isCompleted()) {
                count++;
            }
        }
        return (count/total);
    }

    public List<Lesson> get_main_lessons_by_id(List<Key<Lesson>> lesson_keys) {
        Objectify ofy = OfyService.ofy();
        List<Lesson> main_lessons = new ArrayList<>();
        System.out.println("lesson size: " + lesson_keys.size());
        for(int i = 0; i < lesson_keys.size(); i++) {
            Key<Lesson> lesson_key = lesson_keys.get(i);
            Lesson lesson = ofy.load().key(lesson_key).now();
            if(lesson.isSite_owned()) {
                main_lessons.add(lesson);
            }
        }
        return main_lessons;
    }

    /**
     * Creates main lesson objects for a specific user ONLY IF the user doesn't have a certain lesson object
     * @param user
     */
    public void create_main_lessons_for_user(User user){
        Objectify ofy = OfyService.ofy();
        List<Lesson> user_lessons;
        List<Lesson> main_lessons;
        Query<Lesson> q = ofy.load().type(Lesson.class).filter("user_id", user.getUser_id());
        if (q.list().size() == 0){
            user_lessons = new ArrayList<>();
        }else{
            user_lessons = q.list();
        }
        q =  ofy.load().type(Lesson.class).filter("site_owned", true).filter("user_id",null);
        if(q.list().size() == 0){
            LessonJSONService.create_lesson_from_JSON("WEB-INF/lesson1.json");
            main_lessons = ofy.load().type(Lesson.class).filter("site_owned", true).list();
        }else{
            main_lessons = q.list();
        }
        for(int i = 0; i < user_lessons.size(); i++){
            for(int j = 0; j < main_lessons.size(); j++){
                if(user_lessons.get(i).getOriginal_lesson().getId() == main_lessons.get(j).getLesson_id()){//user already has this lesson
                    main_lessons.remove(j);
                    break;
                }
            }
        }
        List<Key<Lesson>> user_lesson_keys = user.getLessons();
        for(int i =0; i < main_lessons.size(); i++){//user doesn't have the lessons in this list
            Lesson m = main_lessons.get(i);
            Lesson l = new Lesson();//create new lesson object
            l.setTitle(m.getTitle());
            l.setUser_id(user);
            l.setDescription(m.getDescription());
            l.setOriginal_lesson(m);
            l.setSite_owned(true);
            List<Task> tasks = get_tasks_by_id(m.getTasks());
            //create task object
            List<Key<Task>> task_keys = create_tasks_by_id(tasks);
            l.setTasks(task_keys);
            user_lesson_keys.add(ofy.save().entity(l).now());
        }
        if(main_lessons.size() > 0){//if there where new lessons added, save changes to datastore
            ofy.save().entity(user).now();
        }


    }

    public List<Key<Task>> create_tasks_by_id(List<Task> tasks){
        Objectify ofy = OfyService.ofy();
        List<Key<Task>> task_keys = new ArrayList<>();
        for(int j = 0; j < tasks.size(); j++){//create the tasks for the new lesson object
            Task original_task = tasks.get(j);
            Task t = new Task();
            t.setTitle(original_task.getTitle());
            t.setToolbox(original_task.getToolbox());
            t.setEditor(original_task.getEditor());
            t.setInstructions(original_task.getInstructions());
            t.setExpected_output(original_task.getExpected_output());
            t.setTest_case(original_task.getTest_case());
            t.setOriginal_task(original_task);
            t.setType(original_task.getType());
            task_keys.add(ofy.save().entity(t).now());
        }
        return task_keys;
    }

    public List<Block> get_blocks_by_id(List<Key<Block>> block_keys){
        Objectify ofy = OfyService.ofy();
        List<Block> blocks = new ArrayList<>();
        for(int i = 0; i < block_keys.size(); i++){
            blocks.add(ofy.load().key(block_keys.get(i)).now());
        }
        return blocks;
    }

    /**
     * Updates a list of blocks
     * @param task_id -- the id of the task the blocks belong to
     * @param blocks -- the list of blocks to be updated
     * @return -- the list of updated block keys
     */
    public List<Key<Block>> update_blocks(long task_id, List<Block> blocks) {
        // Get the objectify object to access the datastore
        Objectify ofy = OfyService.ofy();
        // Create a list of block ids
        List<Key<Block>> block_keys = new ArrayList<>();

        // For each block in the block list
        for(int i = 0; i < blocks.size(); i++) {
            // Get the new block from the list
            Block new_block = blocks.get(i);
            // Get the original block from the datastore
            Block old_block = ofy.load().type(Block.class).id(blocks.get(i).getBlock_id()).now();

            // Check to see if the new block has been changed
            if (new_block.getValue().equals(old_block.getValue())) {
                // Check to see if the new block can be edited
                if (new_block.isCan_edit()) {
                    // Change the block value by updating the block
                    OfyService.ofy().save().entity(new_block);
                    // Get the key value from the id
                    Key block_key = Key.create(Block.class,new_block.getBlock_id());
                    // Add the key to the list
                    block_keys.add(block_key);
                } else {
                    // Create a new block
                    Block block = new Block();
                    block.setValue(new_block.getValue());
                    block.setType(new_block.getType());
                    block.setCan_edit(true);

                    // Save the new block to the datastore
                    OfyService.ofy().save().entity(block);

                    // Get the key value from the id
                    Key block_key = Key.create(Block.class,block.getBlock_id());
                    // Add the id to the list
                    block_keys.add(block_key);
                }
            } else {
                // If it has not been changed just add the key to the list
                Key block_key = Key.create(Block.class,new_block.getBlock_id());
                block_keys.add(block_key);
            }
        }
        // Return the block keys
        return block_keys;
    }


    public List<Task> get_tasks_by_id(List<Key<Task>> task_keys){
        Objectify ofy = OfyService.ofy();
        List<Task> tasks = new ArrayList<>();
        for (Key<Task> key : task_keys){
            tasks.add(ofy.load().key(key).now());
        }
        return tasks;
    }

    /**
     * gets the main lesson objects specific to a single user
     * @param user
     * @return list of main lessons for a specific user
     */
    public List<Lesson> get_main_lessons_by_user(User user){
        Objectify ofy = OfyService.ofy();
        return ofy.load().type(Lesson.class).filter("user_id", user.getUser_id()).filter("site_owned", true).list();
    }

    /**
     * Reset a task in a lesson
     * @param task -- the task object that is to be reset
     * @return -- return the task that has been reset
     */
    public Task reset_task(Task task) {
        // Get the objectify object to get the original task from the datastore
        Objectify ofy = OfyService.ofy();

        // Load the original task for the lesson
        Task original = (Task) ofy.load().key(task.getOriginal_task()).now();

        // Set the completed boolean to false
        task.setCompleted(false);

        // Delete the edited blocks associated with the task in the editor
        for (int j = 0; j < task.getEditor().size(); j++) {
            // Load the block
            Block b = ofy.load().key(task.getEditor().get(j)).now();
            // Check if the block can be edited
            if (b.isCan_edit()){
                // If it is not an original block, delete it from the datastore
                ofy.delete().entity(b).now();
            }
        }

        // Delete the edited blocks associated with the task in the toolbox
        for (int j = 0; j < task.getToolbox().size(); j++) {
            // Load the block
            Block b = ofy.load().key(task.getToolbox().get(j)).now();
            // Check if the block can be edited
            if (b.isCan_edit()) {
                // If it is not an original block, delete it from the datastore
                ofy.delete().entity(b).now();
            }
        }

        // Reset the toolbox for task
        task.setToolbox(original.getToolbox());
        // Reset the editor for task
        task.setEditor(original.getEditor());
        // Save the changes to the datastore
        ofy.save().entity(task).now();

        // Return the updated task
        return task;
    }
}
