package com.dolphinblue.service;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public static Lesson create_lesson_from_JSON(String file_path){
        JSONParser parser = new JSONParser();
        try{

            File file = new File(file_path);
            Object obj = parser.parse(new FileReader(file));
            JSONObject json_object = (JSONObject) obj;
            Lesson l = new Lesson();
            l.setTitle((String) json_object.get("title"));
            l.setShared((Boolean) json_object.get("shared"));
            l.setSite_owned((Boolean) json_object.get("site_owned"));
            l.setDescription((String) json_object.get("description"));
            JSONArray json_tasks = (JSONArray) json_object.get("tasks");
            l.setTasks(create_tasks_from_json(json_tasks));
            return l;
        }catch (Exception e){
            System.out.println("ERROR HERE");
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return null;
    }

    public static List<Key<Task>> create_tasks_from_json(JSONArray json_tasks){
        List<Task> tasks = new ArrayList<>();
        Iterator<JSONObject> iter = json_tasks.iterator();
        while (iter.hasNext()){
            JSONObject json_t = iter.next();
            Task t = new Task();
            t.setTitle((String) json_t.get("title"));
            t.setInstructions((String) json_t.get("instructions"));
            t.setEditor(create_blocks_from_json((JSONArray) json_t.get("program_blocks")));
            t.setToolbox(create_blocks_from_json((JSONArray) json_t.get("toolbox")));
            t.setExpected_output((String) json_t.get("expected_output"));
            tasks.add(t);
        }

        List<Key<Task>> keys = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++){
            keys.add(OfyService.ofy().save().entity(tasks.get(i)).now());
        }
        return keys;
    }

    public static List<Key<Block>> create_blocks_from_json(JSONArray json_blocks){
        List<Block> blocks = new ArrayList<>();
        Iterator<JSONObject> iterator = json_blocks.iterator();
        while(iterator.hasNext()){
            JSONObject json_b = iterator.next();
            Block b = new Block();
            b.setCan_edit((Boolean) json_b.get("can_edit"));
            b.setValue((String) json_b.get("value"));
            String type = (String) json_b.get("type");
            switch (type){
                case "LOG":
                    b.setType(Block.Type.LOG);
                    break;
                case "ASSIGN":
                    b.setType(Block.Type.ASSIGN);
                    break;
                case "BRACKET":
                    b.setType(Block.Type.BRACKET);
                    break;
                case "FOR":
                    b.setType(Block.Type.FOR);
                    break;
                case "WHILE":
                    b.setType(Block.Type.WHILE);
                    break;
                default:
                    b.setType(Block.Type.LOG);
                    break;
            }
            blocks.add(b);
        }
        List<Key<Block>> keys = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++){
            keys.add(OfyService.ofy().save().entity(blocks.get(i)).now());
        }
        return keys;
    }
}
