package com.dolphinblue.service;

import com.dolphinblue.models.*;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import com.googlecode.objectify.cmd.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Devon on 4/2/17.
 *
 * This class provides calculation services for the controller classes
 */
@Service
public class LessonService {

    /**
     * This is for getting percentage completed of a lesson.
     * @param lesson -- Lesson to get percentage completed.
     * @return -- percentage completed.
     */
    public int get_percent_complete(Lesson lesson) {
        Objectify ofy = OfyService.ofy();
        //Get tasks of lesson.
        List<Key<Task>> tasks = lesson.getTasks();
        //number of lessons.
        double total = tasks.size();
        //number of lessons completed.
        double count = 0;

        for(int i = 0; i < tasks.size(); i++) {
            Key task_key = tasks.get(i);
            Task task = ofy.load().type(Task.class).id(task_key.getId()).now();
            if(task.isCompleted()) {
                //if completed add to count
                count++;
            }
        }
        int percent = (int)((count/total) * 100);

        return percent;
    }

    public int get_average_rating(Long original_key) {
        // Get the ofy service for the datastore
        Objectify ofy = OfyService.ofy();
        // Get the lessons with this as the original
        Query<Lesson> q = ofy.load().type(Lesson.class).filter("original_lesson", original_key).filter("rating >", 0);
        System.out.println("Query Size: " + q.list().size());
        // Create variables for total and average
        int average, total = 0;
        for (int i = 0; i < q.list().size(); i++) {
            // Add up all the ratings
            Lesson lesson = q.list().get(i);
            total = total + lesson.getRating();
            System.out.println("Total: " + total);
        }
        average = total / q.list().size();
        return average;
    }

    /**
     * Creates main lesson objects for a specific user ONLY IF the user doesn't have a certain lesson object
     * @param user -- User to create lesson objects for.
     */
    public void create_main_lessons_for_user(User user){
        Objectify ofy = OfyService.ofy();
        List<Lesson> user_lessons;
        List<Lesson> main_lessons;
        //load all main lessons user already has.
        Query<Lesson> q = ofy.load().type(Lesson.class).filter("user_id", user.getUser_id()).filter("site_owned", true);
        if (q.list().size() == 0){
            //User has no main lessons.
            user_lessons = new ArrayList<>();
        }else{
            //User has some main lessons.
            user_lessons = q.list();
        }
        //get main lesson objects not attached to a user.
        q =  ofy.load().type(Lesson.class).filter("site_owned", true).filter("user_id",null);
        if(q.list().size() == 0){
            //no lessons created in datastore.
            LessonJSONService.create_lesson_from_JSON("WEB-INF/lesson1.json");
            LessonJSONService.create_lesson_from_JSON("WEB-INF/lesson2.json");
            LessonJSONService.create_lesson_from_JSON("WEB-INF/lesson3.json");
            LessonJSONService.create_lesson_from_JSON("WEB-INF/lesson4.json");
            main_lessons = ofy.load().type(Lesson.class).filter("site_owned", true).list();
            //System.out.println("Main lessons size : " + main_lessons.size());
        }else{
            main_lessons = q.list();
        }
        for(int i = 0; i < user_lessons.size(); i++){
            for(int j = 0; j < main_lessons.size(); j++){
                if(user_lessons.get(i).getOriginal_lesson().getId() == main_lessons.get(j).getLesson_id()){
                    //user already has this lesson.
                    main_lessons.remove(j);
                    break;
                }
            }
        }
        List<Key<Lesson>> user_lesson_keys = user.getLessons();

        //user doesn't have the lessons in this list.
        for(int i =0; i < main_lessons.size(); i++){
            //Original lesson object.
            Lesson m = main_lessons.get(i);
            //create new lesson object
            Lesson l = new Lesson();
            //Set all attributes of original lesson object to new lesson object.
            l.setTitle(m.getTitle());
            l.setUser_id(user);
            l.setCreator_id("");
            l.setDescription(m.getDescription());
            l.setOriginal_lesson(m);
            l.setSite_owned(true);
            l.setIndex(m.getIndex());
            l.setLast_edited(m.getLast_edited());
            l.setLast_accessed(new Date());
            //Get original lesson tasks.
            List<Task> tasks = get_tasks_by_id(m.getTasks());
            //create task objects
            List<Key<Task>> task_keys = create_tasks_by_id(tasks);
            l.setTasks(task_keys);
            //save new lesson object in datastore and add keys to user object.
            user_lesson_keys.add(ofy.save().entity(l).now());
        }
        if(main_lessons.size() > 0){
            //if there where new lessons added, save changes to datastore
            ofy.save().entity(user).now();
        }


    }

    /**
     * Creates new tasks objects from other tasks.
     * @param tasks -- Original task objects.
     * @return -- keys of new tasks objects in datastore.
     */
    public List<Key<Task>> create_tasks_by_id(List<Task> tasks){
        Objectify ofy = OfyService.ofy();
        List<Key<Task>> task_keys = new ArrayList<>();
        //create the tasks for the new lesson object
        for(int j = 0; j < tasks.size(); j++){
            Task original_task = tasks.get(j);
            //new task object
            Task t = new Task();
            //set attributes of new task object to original task
            t.setTitle(original_task.getTitle());
            if(original_task.getFreecode() != null){
                t.setFreecode(original_task.getFreecode());
            }else{
                t.setToolbox(original_task.getToolbox());
                t.setEditor(original_task.getEditor());
            }

            t.setInstructions(original_task.getInstructions());
            t.setExpected_output(original_task.getExpected_output());
            t.setTest_case(original_task.getTest_case());
            t.setOriginal_task(original_task);
            t.setType(original_task.getType());
            t.setHint(original_task.getHint());
            //save new task object to datastore.
            task_keys.add(ofy.save().entity(t).now());
        }
        return task_keys;
    }

    /**
     * Returns list of blocks based on their keys in datastore.
     * @param block_keys -- Keys of blocks to fetch.
     * @return -- List of blocks.
     */
    public List<Block> get_blocks_by_id(List<Key<Block>> block_keys){
        Objectify ofy = OfyService.ofy();
        List<Block> blocks = new ArrayList<>();
        for(int i = 0; i < block_keys.size(); i++){
            //add block to list to return
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
    public List<Key<Block>> update_blocks(long task_id, List<SaveBlockModel> blocks) {
        // Get the objectify object to access the datastore
        Objectify ofy = OfyService.ofy();
        // Create a list of block ids
        BlockService blockService = new BlockService();
        List<Key<Block>> block_keys = new ArrayList<>();

        // For each block in the block list
        for(int i = 0; i < blocks.size(); i++) {
            //System.out.println("Getting new block from blocks list");
            // Get the new block from the list
            SaveBlockModel new_block = blocks.get(i);
            // Get the original block from the datastore
            Block old_b = ofy.load().type(Block.class).id(blocks.get(i).getBlock_id()).now();
            SaveBlockModel old_block = blockService.block_to_block_model(old_b);

            if(new_block.isCan_edit()){
                //check to see if children blocks are equal
                boolean same = false;
                if(new_block.getChildren().size() == old_block.getChildren().size()){
                    //size is same so check list
                    for(int j = 0; j < new_block.getChildren().size(); j++){
                        String old_value = old_block.getChildren().get(j).getValue();
                        String new_value = new_block.getChildren().get(j).getValue();
                        if(!new_value.equals(old_value)){
                            //not the same
                            same = false;
                            break;
                        }
                        same = true;
                    }
                }
                if(!same){
                    //create new block in datastore
                    //set id to null so google app engine creates new id
                    ofy.delete().entity(old_b).now();
                    new_block.setBlock_id(null);

                    // Save the new block to the datastore
                    // Get key generated by google appengine
                    Key block_key = blockService.save_block_model(new Block(), new_block);
                    // Add the key to the list
                    block_keys.add(block_key);
                }
            }
            else{

                Key block_key = blockService.save_block_model(old_b, new_block);
                block_keys.add(block_key);
            }

        }
        // Return the block keys
        return block_keys;
    }

    /**
     * Gets list of task objects based on their keys in the datastore.
     * @param task_keys -- Keys of tasks to fetch.
     * @return -- List of tasks.
     */
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
     * gets the shared lesson objects site wide
     * @param user
     * @return list of shared lessons for the site
     */
    public List<Lesson> get_shared_lessons_by_user(User user){
        Objectify ofy = OfyService.ofy();
        return ofy.load().type(Lesson.class).filter("creator_id !=", user.getUser_id()).filter("site_owned", false).filter("shared",true).filter("user_id", user.getUser_id()).list();
    }

    public List<LessonDetails> extract_details(List<Lesson> lessons) {
        Objectify ofy = OfyService.ofy();
        List<LessonDetails> lessonDetails = new ArrayList<LessonDetails>();
        for (int i = 0; i < lessons.size(); i++) {

            LessonDetails l = new LessonDetails(lessons.get(i));
            Lesson original_lesson = ofy.load().key(lessons.get(i).getOriginal_lesson()).now();
            //search should display average rating
            l.setRating(get_average_rating(original_lesson));
            lessonDetails.add(l);
        }
        return lessonDetails;
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

    /**
     * gets the main lesson objects specific to a single user
     * @param user
     * @return list of main lessons for a specific user
     */
    public List<Lesson> get_own_lessons(User user){
        Objectify ofy = OfyService.ofy();
        return ofy.load().type(Lesson.class).filter("creator_id", user.getUser_id()).filter("site_owned", false).list();
    }

    /**
     * Converts a SaveLessonModel object to a lesson object.
     * @param lesson
     * @param lesson_model
     */
    public void lesson_model_to_lesson(Lesson lesson, SaveLessonModel lesson_model){
        Objectify ofy = OfyService.ofy();
        TaskService taskService = new TaskService();

        lesson.setDescription(lesson_model.getDescription());
        lesson.setTitle(lesson_model.getTitle());
        lesson.setShared(lesson_model.isShared());
        List<Key<Task>> lesson_task_keys = lesson.getTasks();
        List<Key<Task>> lesson_model_task_keys = new ArrayList<>();
        if (lesson_model.getTasks() != null) {

            for(int i = 0; i < lesson_model.getTasks().size(); i++){
                Key<Task> task_key = Key.create(Task.class, lesson_model.getTasks().get(i));
                lesson_model_task_keys.add(task_key);
            }

            for(int i = 0; i < lesson_task_keys.size(); i++){
                Key<Task> task_key = lesson_task_keys.get(i);
                if(!lesson_model_task_keys.contains(task_key)){
                    //new lesson model doesn't contain this task so remove it.
                    taskService.delete_task(lesson, task_key);
                }
            }

            lesson.setTasks(lesson_model_task_keys);
        }else {
            if(lesson_task_keys != null){
                for(int i = 0; i < lesson_task_keys.size(); i++){
                    taskService.delete_task(lesson, lesson_task_keys.get(i));
                }
            }
            lesson.setTasks(new ArrayList<Key<Task>>());
        }

    }

    /**
     * Creates a user's own shared lesson objects.
     * Also returns the user's shared lesson objects to be displayed on user page.
     * @param user
     * @return -- list of lesson objects.
     */
    public List<Lesson> create_shared_lessons_for_user(User user){
        Objectify ofy = OfyService.ofy();

        List<Lesson> user_lessons = ofy.load().type(Lesson.class)
                .filter("user_id", user.getUser_id())
                .filter("site_owned", false)
                .filter("shared", true).list();


        List<Lesson> shared_lessons = ofy.load().type(Lesson.class)
                .filter("user_id", null)
                .filter("site_owned", false)
                .filter("shared", true)
                .filter("creator_id !=", user.getUser_id()).list();

        //check which lessons user already has
        for(int i = 0; i < user_lessons.size(); i++){
            for(int j = 0; j < shared_lessons.size(); j++){
                if(user_lessons.get(i).getOriginal_lesson().getId() == shared_lessons.get(j).getLesson_id()){
                    //user already has this lesson.
                    shared_lessons.remove(j);
                    break;
                }
            }
        }



        List<Key<Lesson>> user_lesson_keys = user.getLessons();

        //user doesn't have the lessons in this list.
        for(int i =0; i < shared_lessons.size(); i++){
            //Original lesson object.
            Lesson m = shared_lessons.get(i);
            //create new lesson object
            Lesson l = new Lesson();
            //Set all attributes of original lesson object to new lesson object.
            l.setTitle(m.getTitle());
            l.setUser_id(user);
            l.setDescription(m.getDescription());
            l.setOriginal_lesson(m);
            l.setCreator_id(m.getCreator_id());
            l.setSite_owned(false);
            l.setShared(true);
            l.setLast_accessed(new Date());
            //Get original lesson tasks.
            List<Task> tasks = get_tasks_by_id(m.getTasks());
            //create task objects
            List<Key<Task>> task_keys = create_tasks_by_id(tasks);
            l.setTasks(task_keys);
            //save new lesson object in datastore and add keys to user object.
            user_lesson_keys.add(ofy.save().entity(l).now());
            user_lessons.add(l);
        }
        if(shared_lessons.size() > 0){
            //if there where new lessons added, save changes to datastore
            ofy.save().entity(user).now();
        }
        return user_lessons;
    }

    /**
     * searches through list of lessons for lessons that contain @param description.
     * @param lessons -- list of lessons
     * @param description -- description to search for
     */
    public List<Lesson> search_by_description(List<Lesson> lessons, String description){
        List<Lesson> list = new ArrayList<>();
        for (int i = 0; i < lessons.size(); i++){
            //lesson description contains the input string
            if(lessons.get(i).getDescription().toLowerCase().contains(description.toLowerCase())){
                list.add(lessons.remove(i));
                i--;
            }
        }
        return list;
    }
    /**
     * searches through list of lessons for lessons that contain @param author.
     * @param lessons -- list of lessons
     * @param author -- author to search for
     */
    public List<Lesson> search_by_author(List<Lesson> lessons, String author){
        Objectify ofy = OfyService.ofy();
        List<Lesson> list = new ArrayList<>();
        for (int i = 0; i < lessons.size(); i++){
            User user = ofy.load().type(User.class).id(lessons.get(i).getCreator_id()).now();
            //lesson author contains the input string
            if(user.getUsername().toLowerCase().contains(author.toLowerCase())){
                list.add(lessons.remove(i));
                i--;
            }
        }
        return list;
    }
    /**
     * searches through list of lessons for lessons that contain @param description.
     * @param lessons
     * @param title
     */
    public List<Lesson> search_by_title(List<Lesson> lessons, String title){
        List<Lesson> list = new ArrayList<>();
        for (int i = 0; i < lessons.size(); i++){
            //lesson title contains the input string
            if(lessons.get(i).getTitle().toLowerCase().contains(title.toLowerCase())){
                list.add(lessons.remove(i));
                i--;
            }
        }
        return list;
    }

    /**
     * updates average rating of a lesson and returns the new average
     * @param lesson -- lesson to change
     * @param new_rating -- new rate
     */
    public void update_average_rating(Lesson original_lesson, Lesson lesson, int new_rating){
        if(lesson.getRating() == 0){
            //first rate by this user
            original_lesson.setNumber_of_ratings(original_lesson.getNumber_of_ratings() + 1);
            original_lesson.setRating(original_lesson.getRating() + new_rating);
        }else {
            //just change rating to be the rating plus difference of new rating - old rating
            //dont increase number of ratings
            int old_rating = lesson.getRating();
            original_lesson.setRating(original_lesson.getRating() + (new_rating - old_rating));
        }

    }

    /**
     * @returns average rating of a lesson.
     */
    public int get_average_rating(Lesson lesson){
        try {
            int ret = lesson.getRating()/lesson.getNumber_of_ratings();
            return ret;
        }catch (Exception e){
            return 0;
        }

    }
}
