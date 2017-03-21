package com.dolphinblue.models;

/**
 * Created by FreddyEstevez on 3/21/17.
 * Model representing a block
 */
public class Block {

    private long block_id;
    private String value;
    private Type type;
    private boolean can_edit;


    private enum Type{//types of blocks
        FOR,
        WHILE,
        ASSIGN,
        PLUS,
        MINUs,
        MULTIPLY,
        DIVIDE;
    }

    public Block(){}

    public Block(long block_id, String value, Type type, boolean can_edit) {
        this.block_id = block_id;
        this.value = value;
        this.type = type;
        this.can_edit = can_edit;
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
}
