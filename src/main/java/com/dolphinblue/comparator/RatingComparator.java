package com.dolphinblue.comparator;

import com.dolphinblue.models.Lesson;

import java.util.Comparator;

/**
 * used to compare lessons based on rating
 * Created by FreddyEstevez on 5/15/17.
 */
public class RatingComparator implements Comparator<Lesson> {
    public int compare(Lesson lesson1, Lesson lesson2){
        if(lesson1.getRating() < lesson2.getRating()){
            return -1;
        }else if(lesson2.getRating() > lesson1.getRating()){
            return 1;
        }
        //same rating
        return 0;
    }
}
