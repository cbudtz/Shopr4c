package com.nexb.shopr4.Utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexb.shopr4.dataModel.Dictionary;
import com.nexb.shopr4.dataModel.DictionaryItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Christian on 26-11-2015.
 */
public class saveDictionaryUtitility {
    public static void main(String[] args){
        new saveDictionaryUtitility().run();
    }

    private void run() {
        Dictionary d = new Dictionary();
        ArrayList<DictionaryItem> dict = d.getDictionaryItems();
        dict.add(new DictionaryItem("Tomater", "Frugt og grønt", "bk",1));
        dict.add(new DictionaryItem("Bananer", "Frugt og grønt","stk",10));
        dict.add(new DictionaryItem("Rugbrød", "Brød", "stk", 1));
        dict.add(new DictionaryItem("Skummetmælk","Mejeri","L",1));
        dict.add(new DictionaryItem("Minimælk","Mejeri", "L", 1));
        dict.add(new DictionaryItem("Æg","Æg","stk",10));
        dict.add(new DictionaryItem("AppelsinJuice","Juice","L",1));
        dict.add(new DictionaryItem("Hakket Oksekød","Kød","pk",1));

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("standardDictionary.json"),d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
