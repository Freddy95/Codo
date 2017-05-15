package com.dolphinblue.comparator;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.LessonDetails;

import java.util.Comparator;

/**
 * used to compare lessons based on rating
 * sorts by descending order
 * Created by FreddyEstevez on 5/15/17.
 */
public class RatingComparator implements Comparator<LessonDetails> {
    public int compare(LessonDetails lesson1, LessonDetails lesson2){
        if(lesson1.getRating() > lesson2.getRating()){
            return -1;
        }else if(lesson2.getRating() < lesson1.getRating()){
            return 1;
        }
        //same rating
        return 0;
    }
}
