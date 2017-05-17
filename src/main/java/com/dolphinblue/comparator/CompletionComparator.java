package com.dolphinblue.comparator;

import com.dolphinblue.models.LessonDetails;

import java.util.Comparator;

/**
 * Created by Devon on 5/17/17.
 */
public class CompletionComparator implements Comparator<LessonDetails> {
    public int compare(LessonDetails lesson1, LessonDetails lesson2){
        if(lesson1.getPercent_complete() > lesson2.getPercent_complete()){
            return -1;
        }else if(lesson2.getPercent_complete() < lesson1.getPercent_complete()){
            return 1;
        }
        //same percent
        return 0;
    }
}
