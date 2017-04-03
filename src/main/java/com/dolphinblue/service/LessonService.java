package com.dolphinblue.service;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.Task;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

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

        for(int i = 0; i < lesson_keys.size(); i++) {
            Key<Lesson> lesson_key = lesson_keys.get(i);
            Lesson lesson = ofy.load().type(Lesson.class).id(lesson_key.getId()).now();
            if(lesson.isSite_owned()) {
                main_lessons.add(lesson);
            }
        }
        return main_lessons;
    }

    public List<Block> get_blocks_by_id(List<Key<Block>> block_keys){
        Objectify ofy = OfyService.ofy();
        List<Block> blocks = new ArrayList<>();
        for(int i = 0; i < block_keys.size(); i++){
            blocks.add(ofy.load().key(block_keys.get(i)).now());
        }
        return blocks;
    }

    // Put checking for block changes here
    // Need to update for when we utilize editing blocks
    public String update_blocks(List<Key<Block>> blocks) {
        Objectify ofy = OfyService.ofy();

        for(int i = 0; i < blocks.size(); i++) {
            Block block = ofy.load().type(Block.class).id(blocks.get(i).getId()).now();
            // Check to see if the block can be edited
            if (!block.isCan_edit() && block.getValue().equals("hello")) {
                // If it can't be, create a new block
                Block new_block = new Block();
                new_block.setValue(block.getValue());
                new_block.setType(block.getType());
                new_block.setCan_edit(true);

                OfyService.ofy().save().entity(new_block);


            } else {
                // If it can, update the existing block
                OfyService.ofy().save().entity(block);
            }
        }

        return "this does not work yet";
    }

    // Maybe deprecated... remove if not used by final project
    public List<Task> get_tasks_by_id(List<Key<Task>> task_keys){
        Objectify ofy = OfyService.ofy();
        List<Task> tasks = new ArrayList<>();
        for (Key<Task> key : task_keys){
            tasks.add(ofy.load().key(key).now());
        }
        return tasks;
    }


}
