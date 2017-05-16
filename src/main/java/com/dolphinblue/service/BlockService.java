package com.dolphinblue.service;

import com.dolphinblue.models.Block;
import com.dolphinblue.models.SaveBlockModel;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FreddyEstevez on 5/3/17.
 */
@Service
public class BlockService {

    public List<Key<Block>> save_list_blocks(List<SaveBlockModel> block_list){
        Objectify ofy = OfyService.ofy();
        List<Key<Block>> block_keys = new ArrayList<>();
        for (int i = 0; i < block_list.size(); i++){
            SaveBlockModel block_model = block_list.get(i);
            if(block_model.getBlock_id() == null) {
                Block b = new Block();
                b.setOriginal(true);
                Key<Block> block_key = save_block_model(b, block_model);
                block_keys.add(block_key);
            }else{
                Key<Block> block_key = save_block_model(ofy.load().type(Block.class).id(block_model.getBlock_id()).now(), block_model);
                block_keys.add(block_key);
            }
        }
        return block_keys;
    }

    public Key<Block> save_block_model(Block block, SaveBlockModel block_model){

        block.setCan_edit(block_model.isCan_edit());
        block.setType(block_model.getType());
        block.setValue(block_model.getValue());
        block.setValue(block_model.getValue());

        List<Key<Block>> children_key = new ArrayList<>();
        for(int i = 0; i < block_model.getChildren().size(); i++){
            SaveBlockModel child_model = block_model.getChildren().get(i);
            if(child_model.getBlock_id() == null){
                children_key.add(save_block_model(new Block(), child_model));
            }else {
                Block child = OfyService.ofy().load().type(Block.class).id(child_model.getBlock_id()).now();
                children_key.add(save_block_model(child, block_model.getChildren().get(i)));
            }
        }
        block.setChildren(children_key);
        return OfyService.ofy().save().entity(block).now();
    }


    public Key<Block> save_block_model_work(Block block, SaveBlockModel block_model){

        block.setCan_edit(block_model.isCan_edit());
        block.setType(block_model.getType());
        block.setValue(block_model.getValue());


        return OfyService.ofy().save().entity(block).now();
    }

    public SaveBlockModel block_to_block_model(Block block){
        SaveBlockModel block_model = new SaveBlockModel();
        block_model.setBlock_id(block.getBlock_id());
        block_model.setValue(block.getValue());
        block_model.setType(block.getType());
        block_model.setCan_edit(block.isCan_edit());
        for (int i = 0; i < block.getChildren().size(); i++){
            Block child = OfyService.ofy().load().key(block.getChildren().get(i)).now();
            block_model.addChild(block_to_block_model(child));
        }
        return block_model;

    }

    public List<SaveBlockModel> get_list_blocks(List<Block> blocks){
        List<SaveBlockModel> list = new ArrayList<>();
        for(int i = 0; i < blocks.size(); i++){
            list.add(block_to_block_model(blocks.get(i)));
        }
        return list;
    }

    public void delete_block(Block block){
        Objectify ofy = OfyService.ofy();
        if(!block.isOriginal()){
            for(int i = 0; i < block.getChildren().size(); i++){
                Block b = ofy.load().key(block.getChildren().get(i)).now();
                delete_block(b);
            }
            ofy.delete().entity(block).now();
        }

    }



}
