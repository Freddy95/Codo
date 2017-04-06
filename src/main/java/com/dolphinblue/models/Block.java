package com.dolphinblue.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FreddyEstevez on 3/21/17.
 * Model representing a block
 */
@Entity
public class Block {

    @Id public Long block_id;
    public String value;
    @JsonIgnore
    public Type type;
    public boolean can_edit;


    public enum Type {//types of blocks
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

//        //mapping of enums to their string represenation so we can use them as JSON objects on the frontend
//        private static HashMap<String, Type> namesMap = new HashMap<String, Type>(3);
//
//        static {
//            namesMap.put("FOR", FOR);
//            namesMap.put("WHILE", WHILE);
//            namesMap.put("ASSIGN", ASSIGN);
//            namesMap.put("DECLARE", DECLARE);
//            namesMap.put("PLUS", PLUS);
//            namesMap.put("MINUS", MINUS);
//            namesMap.put("DIVIDE", DIVIDE);
//            namesMap.put("MULTIPLY", MULTIPLY);
//            namesMap.put("LOG", LOG);
//            namesMap.put("BRACKET", BRACKET);
//        }
//
//        @JsonCreator
//        public static Type fromString(String value) {
//            //get the enum representation, used for deserialization
//            Type val = namesMap.get(value);
//            if(val==null){
//                throw new IllegalArgumentException(value + " has no corresponding value");
//            }
//            return val;
//
//        }
//
//        @JsonValue
//        public String toValue() {
//            //get the string mapping, used for serialization
//            for (Map.Entry<String, Type> entry : namesMap.entrySet()) {
//                if (entry.getValue() == this)
//                    return entry.getKey();
//            }
//
//            return null; // or fail
//        }
    }

   public  Block() {

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

    @Override
    public String toString() {
        return "Block{" +
                "block_id=" + this.block_id +
                ", value='" + this.value + '\'' +
                ", type=" + this.type +
                ", can_edit=" + this.can_edit +
                '}';
    }
}
