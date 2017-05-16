package com.dolphinblue.models;

import com.dolphinblue.service.OfyService;
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
    public static Long NULL_BLOCK_ID;
    @Id public Long block_id;
    public String value;
    public Type type;
    public boolean original;

    public boolean can_edit;
    List<Key<Block>> children;

    // Types of blocks available.
    public enum Type {
        STATIC,
        IF,
        FOR,
        WHILE,
        LOG,
        CURL,
        SQUARE,
        ELSE,
        ELSE_IF
    }

    public Block() {
        children = new ArrayList<Key<Block>>();
    }


    public Block(long block_id, String value,  boolean can_edit) {
        this.block_id = block_id;
        this.value = value;
        this.can_edit = can_edit;
        this.children = new ArrayList<Key<Block>>();
    }
    public Block(long block_id, String value,  Type t,boolean can_edit) {
        this.block_id = block_id;
        this.value = value;
        this.can_edit = can_edit;
        this.type=t;
        this.children = new ArrayList<Key<Block>>();
    }

    public Long getBlock_id() {
        return block_id;
    }

    public void setBlock_id(Long block_id) {
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




    public void addChild(Block newBlock) {
        this.children.add(OfyService.ofy().save().entity(newBlock).now());
    }

    public void removeChild(int index) {
        if (index > -1 && index < this.children.size()) {
            this.children.remove(index);
        }
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

    public List<Key<Block>> getChildren() {
        return children;
    }

    public void setChildren(List<Key<Block>> children) {
        this.children = children;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }
}
