package com.dolphinblue.comparator;

import com.dolphinblue.models.Lesson;
import com.dolphinblue.models.LessonDetails;

import java.util.Comparator;

/**
 * This is used when sorted a list of lessons by the title.
 * Created by FreddyEstevez on 5/14/17.
 */
public class TitleComparator implements Comparator<LessonDetails> {

    public int compare(LessonDetails lesson1, LessonDetails lesson2){
        return lesson1.getTitle().compareTo(lesson2.getTitle());
    }
}
