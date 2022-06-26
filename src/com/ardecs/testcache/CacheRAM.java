package com.ardecs.testcache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CacheRAM implements Cache{

    private int sizeOfCache;
    private HashMap<String, String> mapCache;
    private LinkedList<String> listAgeOfUsing;
    private HashMap<String, Integer> mapCountOfUsing;
    private String typeOfStrategy;

    public CacheRAM(int sizeOfCache, String typeOfStrategy) {
        this.sizeOfCache = sizeOfCache;
        mapCache = new HashMap<>(sizeOfCache);
        this.typeOfStrategy = typeOfStrategy;
        mapCountOfUsing = new HashMap<>(sizeOfCache);
        listAgeOfUsing = new LinkedList<>();
    }
    public void add(String key, String value){
        switch (typeOfStrategy){
            case "LFU":
                while (mapCache.size() >= this.sizeOfCache){
                    List<Integer> listValuesCountOfUsing = new ArrayList<>(mapCountOfUsing.values());
                    int minNumberOfUse = listValuesCountOfUsing.get(0);
                    for (int i = 1; i < listValuesCountOfUsing.size(); i++){
                        if(minNumberOfUse >= listValuesCountOfUsing.get(i)){
                            minNumberOfUse = listValuesCountOfUsing.get(i);
                        }
                    }
                    List<String> listOfMapCountKeys = new ArrayList<>(mapCountOfUsing.keySet());
                    for (String keyForDelete : listOfMapCountKeys){
                        if (mapCountOfUsing.get(keyForDelete) == minNumberOfUse){
                            System.out.println("Ключ " + keyForDelete +
                                    " и его значение " +  mapCache.get(keyForDelete) + " удалены");
                            mapCache.remove(keyForDelete);
                            mapCountOfUsing.remove(keyForDelete);
                        }
                    }
                }
                mapCache.put(key, value);
                mapCountOfUsing.put(key,1);
                    break;
            case "MRU": while (listAgeOfUsing.size() >= this.sizeOfCache){
                Object keyForDelete = listAgeOfUsing.removeLast();
                System.out.println("Ключ " + keyForDelete +
                        " и его значение " +  mapCache.get(keyForDelete) + " удалены");
                mapCache.remove(keyForDelete);
            }
            listAgeOfUsing.addLast(key);
            mapCache.put(key, value);
            case "LRU": listAgeOfUsing.remove(key);
                while (listAgeOfUsing.size() >= this.sizeOfCache){
                Object keyForDelete = listAgeOfUsing.removeLast();
                System.out.println("Ключ " + keyForDelete +
                        " и его значение " +  mapCache.get(keyForDelete) + " удалены");
                mapCache.remove(keyForDelete);
            }
                    listAgeOfUsing.addFirst(key);
                    mapCache.put(key, value);
                break;

        }
    };
    public Object get(String key){
        if (mapCache.size() == 0) {
            return null;
        } else {
            switch (typeOfStrategy) {
            case "LFU":
                 if (mapCache.containsKey(key)){
                    int countOfUse = mapCountOfUsing.get(key);
                    countOfUse++;
                    mapCountOfUsing.put(key, countOfUse);
                    return mapCache.get(key);
                } break;
            case "MRU":  if(mapCache.containsKey(key)) {
                return mapCache.get(key);
            }break;
                case "LRU": if (listAgeOfUsing.remove(key)){
                    listAgeOfUsing.addFirst(key);
                    return mapCache.get(key);
            }break;

        }
        }
        return null;
    };
    public void allDelete(){
        mapCache = new HashMap<>(sizeOfCache);
        mapCountOfUsing = new HashMap<>(sizeOfCache);
        listAgeOfUsing = new LinkedList<>();
        System.out.println("Кеш очищен");
    }
}
