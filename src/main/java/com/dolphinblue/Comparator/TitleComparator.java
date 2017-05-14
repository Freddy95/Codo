package com.dolphinblue.Comparator;

import com.dolphinblue.models.Lesson;

import java.util.Comparator;

/**
 * This is used when sorted a list of lessons by the title.
 * Created by FreddyEstevez on 5/14/17.
 */
public class TitleComparator implements Comparator<Lesson> {

    public int compare(Lesson lesson1, Lesson lesson2){
        return lesson1.getTitle().compareTo(lesson2.getTitle());
    }
}
