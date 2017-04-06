package com.dolphinblue.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Matt on 4/6/17.
 * Object class used for JSON conversion
 */
public class BlockWrapper {


    @JsonProperty(value="blocks")
    public List<Block> blocks;

    @JsonCreator
    public BlockWrapper(List<Block> blocks){
       this.blocks = blocks;
    }

    public List<Block> getBlocks(){
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

}
