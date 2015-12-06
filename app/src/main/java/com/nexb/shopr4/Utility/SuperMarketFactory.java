package com.nexb.shopr4.Utility;

import com.firebase.client.Firebase;
import com.nexb.shopr4.dataModel.SuperMarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Christian on 05-12-2015.
 */
public class SuperMarketFactory {

    public static SuperMarket generateSuperMarket (String name, LinkedHashMap<String, ArrayList<String>> categories, double lat, double lon){
        SuperMarket superMarket = new SuperMarket();
        superMarket.setCategories(categories);
        superMarket.setLattitude(lat);
        superMarket.setLongitude(lon);
        superMarket.setName("name");
        return superMarket;

    }
}
