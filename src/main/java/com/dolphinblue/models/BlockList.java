package com.dolphinblue.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 4/6/17.
 * Object class used for JSON conversion
 */
public class BlockList {


    @JsonProperty(value="blocks")
    public List<SaveBlockModel> blocks;

    public BlockList(){
        blocks = new ArrayList<>();
    }

    @JsonCreator
    public BlockList(List<SaveBlockModel> blocks){
       this.blocks = blocks;
    }

    public List<SaveBlockModel> getBlocks(){
        return blocks;
    }

    public void setBlocks(List<SaveBlockModel> blocks) {
        this.blocks = blocks;
    }

    public String toString(){
        return blocks.get(0).getValue();
    }
}
