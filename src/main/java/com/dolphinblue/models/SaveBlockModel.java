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
public class SaveBlockModel {
    @Id public Long block_id;
    public String value;
    public Block.Type type;

    public boolean can_edit;
    List<SaveBlockModel> children;

    public SaveBlockModel() {
        children = new ArrayList<SaveBlockModel>();
    }


    public SaveBlockModel(long block_id, String value,  boolean can_edit) {
        this.block_id = block_id;
        this.value = value;
        this.can_edit = can_edit;
        this.children = new ArrayList<SaveBlockModel>();
    }
    public SaveBlockModel(long block_id, String value,  Block.Type t,boolean can_edit) {
        this.block_id = block_id;
        this.value = value;
        this.can_edit = can_edit;
        this.type=t;
        this.children = new ArrayList<SaveBlockModel>();
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

    public Block.Type getType() {
        return type;
    }

    public void setType(Block.Type type) {
        this.type = type;
    }

    public boolean isCan_edit() {
        return can_edit;
    }

    public void setCan_edit(boolean can_edit) {
        this.can_edit = can_edit;
    }


    public List<SaveBlockModel> getChildren() {
        return children;
    }

    public void setChildren(List<SaveBlockModel> children) {
        this.children = children;
    }

    public void addChild(SaveBlockModel newBlock) {
        this.children.add(newBlock);
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
}
