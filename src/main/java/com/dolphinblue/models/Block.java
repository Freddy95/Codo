package com.dolphinblue.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FreddyEstevez on 3/21/17.
 * Model representing a block
 */
@Entity
public class Block {
    @JsonIgnore
    @Id public Long block_id;
    public String value;
    @JsonIgnore
    public Type type;
    @JsonIgnore
    public boolean can_edit;
    @JsonIgnore
    List<Block> children;

    public enum Type {//types of blocks
        IF,
        FOR,
        WHILE,
        ASSIGN,
        DECLARE,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        LOG,
        BRACKET;
    }

   public  Block() {
        children = new ArrayList<>();
    }


    public Block(long block_id, String value,  boolean can_edit) {
        this.block_id = block_id;
        this.value = value;
        this.can_edit = can_edit;
    }
    public Block(long block_id, String value,  Type t,boolean can_edit) {
        this.block_id = block_id;
        this.value = value;
        this.can_edit = can_edit;
        this.type=t;
    }

    public long getBlock_id() {
        return block_id;
    }

    public void setBlock_id(long block_id) {
        this.block_id = block_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isCan_edit() {
        return can_edit;
    }

    public void setCan_edit(boolean can_edit) {
        this.can_edit = can_edit;
    }


    public List<Block> getChildren() {
        return children;
    }

    public void setChildren(List<Block> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Block{" +
                "block_id=" + this.block_id +
                ", value='" + this.value + '\'' +
                ", type=" + this.type +
                ", can_edit=" + this.can_edit +
                ", children" + this.children+
                '}';
    }
}
