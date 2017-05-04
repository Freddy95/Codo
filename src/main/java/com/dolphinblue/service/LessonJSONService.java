package com.dolphinblue.service;

import java.io.File;
import java.io.FileReader;
import java.util.*;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.googlecode.objectify.Key;

/**
 * Created by FreddyEstevez on 4/2/17.
 */
public class LessonJSONService {
    /**
     * This should be called when wanting to create a lesson via a JSON file and saves it to datastore.
     * @param file_path -- path to json file containing contents of the lesson.
     * @return -- Lesson object created.
     */
    public static Lesson create_lesson_from_JSON(String file_path){
        JSONParser parser = new JSONParser();
        try{
            //get JSON file.
            File file = new File(file_path);
            Object obj = parser.parse(new FileReader(file));
            //Lesson json object
            JSONObject json_object = (JSONObject) obj;
            Lesson l = new Lesson();
            //set lesson attributes
            l.setTitle((String) json_object.get("title"));
            l.setShared((Boolean) json_object.get("shared"));
            l.setSite_owned((Boolean) json_object.get("site_owned"));
            l.setDescription((String) json_object.get("description"));
            l.setIndex((Long) json_object.get("index"));
            l.setLast_edited(new Date());
            JSONArray json_tasks = (JSONArray) json_object.get("tasks");
            //create and save task objects for lesson.
            l.setTasks(create_tasks_from_json(json_tasks));
            // save lesson object in datastore.
            OfyService.ofy().save().entity(l).now();
            //return lesson object.
            return l;
        }catch (Exception e){
            System.out.println("ERROR HERE");
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return null;
    }

    /**
     * Create tasks for a lesson and saves them in the datastore.
     * Returns the keys of each task in a list.
     * @param json_tasks -- Array representing tasks
     * @return -- List of keys for tasks created.
     */
    public static List<Key<Task>> create_tasks_from_json(JSONArray json_tasks){
        List<Task> tasks = new ArrayList<>();
        Iterator<JSONObject> iter = json_tasks.iterator();
        //iterate through tasks
        while (iter.hasNext()){
            JSONObject json_t = iter.next();
            //create new task object.
            Task t = new Task();
            //set task attributes.

            t.setTitle((String) json_t.get("title"));
            t.setInstructions((String) json_t.get("instructions"));

            JSONArray expected_output_json = (JSONArray) json_t.get("expected_output");
            String[] expected_output = new String[expected_output_json.size()];
            expected_output_json.toArray(expected_output);
            t.setExpected_output(
                    Arrays.asList(expected_output)
            );

            JSONArray test_case_json = (JSONArray) json_t.get("test_case");
            String[] test_case = new String[test_case_json.size()];
            test_case_json.toArray(test_case);
            t.setTest_case(
                    Arrays.asList(test_case)
            );

            t.setHint((String) json_t.get("hint"));

            //check if type of task is block task
            if(json_t.get("type").equals("BLOCK-TASK")){
                //get program blocks.
                t.setEditor(create_blocks_from_json((JSONArray) json_t.get("program_blocks")));
                //get toolbox blocks.
                t.setToolbox(create_blocks_from_json((JSONArray) json_t.get("toolbox")));
            }else{
                t.setFreecode((String) json_t.get("freecode"));
            }


            //add task to list of tasks
            tasks.add(t);
        }

        List<Key<Task>> keys = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++){
            //save list of tasks to datastore
            keys.add(OfyService.ofy().save().entity(tasks.get(i)).now());
        }
        return keys;
    }

    /**
     * Create blocks for a task and saves them in the datastore.
     * Returns the keys of each block in a list.
     * @param json_blocks -- array of blocks in JSON format.
     * @return -- List of keys of blocks.
     */
    public static List<Key<Block>> create_blocks_from_json(JSONArray json_blocks){
        List<Block> blocks = new ArrayList<>();
        Iterator<JSONObject> iterator = json_blocks.iterator();
        //create blocks from JSON objects.
        while(iterator.hasNext()){
            JSONObject json_b = iterator.next();
            Block b = new Block();
            //set block attributes.
            b.setCan_edit((Boolean) json_b.get("can_edit"));
            b.setValue((String) json_b.get("value"));
            String type = (String) json_b.get("type");
            //set type of block.
            switch (type){
                case "LOG":
                    b.setType(Block.Type.LOG);
                    break;
                case "SQUARE":
                    b.setType(Block.Type.SQUARE);
                    break;
                case "CURL":
                    b.setType(Block.Type.CURL);
                    break;
                case "FOR":
                    b.setType(Block.Type.FOR);
                    break;
                case "WHILE":
                    b.setType(Block.Type.WHILE);
                    break;
                case "ELSE":
                    b.setType(Block.Type.ELSE);
                    break;
                case "ELSE IF":
                    b.setType(Block.Type.ELSE_IF);
                    break;
                case "IF":
                    b.setType(Block.Type.IF);
                    break;
                default:
                    b.setType(Block.Type.LOG);
                    break;
            }
            blocks.add(b);
        }
        List<Key<Block>> keys = new ArrayList<>();
        //save blocks to datastore
        for (int i = 0; i < blocks.size(); i++){
            keys.add(OfyService.ofy().save().entity(blocks.get(i)).now());
        }
        //return keys of blocks created.
        return keys;
    }
}
