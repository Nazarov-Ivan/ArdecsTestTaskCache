package com.ardecs.testcache;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CacheDisk implements Cache{

    private int sizeOfCache;
    private HashMap<String, String> mapCache;
    private LinkedList<String> listAgeOfUsing;
    private HashMap<String, Integer> mapCountOfUsing;
    private String typeOfStrategy;
    String fileName;
    String fileNameHelp;

    public CacheDisk(int sizeOfCache, String typeOfStrategy) {
        this.sizeOfCache = sizeOfCache;
        mapCache = new HashMap<>(sizeOfCache);
        this.typeOfStrategy = typeOfStrategy;
        mapCountOfUsing = new HashMap<>(sizeOfCache);
        listAgeOfUsing = new LinkedList<>();
        switch (typeOfStrategy) {
            case "LFU":
                fileName = "LFUCache";
                fileNameHelp = "LRUHelpCache";
                break;
            case "MRU":
                fileName = "MRUCache";
                fileNameHelp = "MRUHelpCache";
                break;
            case "LRU":
                fileName = "LRUCache";
                fileNameHelp = "LRUHelp";
                break;
        }
    }
    @Override
    public void add(String key, String value) {
        switch (typeOfStrategy){
            case "LFU":
                while (mapCache.size() >= this.sizeOfCache){
                    ArrayList<Integer> listValuesCountOfUsing = new ArrayList<>(mapCountOfUsing.values());
                    int minNumberOfUse = listValuesCountOfUsing.get(0);
                    for (int i = 1; i < listValuesCountOfUsing.size(); i++){
                        minNumberOfUse = listValuesCountOfUsing.get(i).compareTo(minNumberOfUse);
                    }
                    List<String> listOfMapCountKeys = new ArrayList<>(mapCountOfUsing.keySet());
                    for (String keyForDelete : listOfMapCountKeys){
                        if (mapCountOfUsing.get(keyForDelete) == minNumberOfUse){
                            System.out.println("Ключ " + keyForDelete +
                                    " и его значение " +  mapCache.get(keyForDelete) + " удалены");
                            mapCache.remove(keyForDelete);
                            mapCountOfUsing.remove(keyForDelete);
                            break;
                        }
                    }
                }
                mapCache.put(key, value);
                mapCountOfUsing.put(key,1);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(mapCache);
                    objectOutputStream.close();
                    fileOutputStream = new FileOutputStream(fileNameHelp);
                    objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(mapCountOfUsing);
                    objectOutputStream.close();
                } catch (IOException e) {
                    System.out.println("Ошибка ввода-вывода");
                }
                break;
            case "MRU": while (listAgeOfUsing.size() >= this.sizeOfCache){
                Object keyForDelete = listAgeOfUsing.removeLast();
                System.out.println("Ключ " + keyForDelete +
                        " и его значение " +  mapCache.get(keyForDelete) + " удалены");
                mapCache.remove(keyForDelete);
            }
                listAgeOfUsing.addLast(key);
                mapCache.put(key, value);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(mapCache);
                    objectOutputStream.close();
                    fileOutputStream = new FileOutputStream(fileNameHelp);
                    objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(listAgeOfUsing);
                    objectOutputStream.close();
                } catch (IOException e) {
                    System.out.println("Ошибка ввода-вывода");
                }
                break;
            case "LRU": listAgeOfUsing.remove(key);
                while (listAgeOfUsing.size() >= this.sizeOfCache){
                    Object keyForDelete = listAgeOfUsing.removeLast();
                    System.out.println("Ключ " + keyForDelete +
                            " и его значение " +  mapCache.get(keyForDelete) + " удалены");
                    mapCache.remove(keyForDelete);
                }
                listAgeOfUsing.addFirst(key);
                mapCache.put(key, value);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(mapCache);
                    objectOutputStream.close();
                    fileOutputStream = new FileOutputStream(fileNameHelp);
                    objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(listAgeOfUsing);
                    objectOutputStream.close();
                } catch (IOException e) {
                    System.out.println("Ошибка ввода-вывода");
                }
                break;

        }
    }

    @Override
    public Object get(String key) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            HashMap<String, String> mapFromDisk = (HashMap<String, String>) objectInputStream.readObject();
            objectInputStream.close();
            mapCache = mapFromDisk;
            fileInputStream = new FileInputStream(fileNameHelp);
            objectInputStream = new ObjectInputStream(fileInputStream);
            if (typeOfStrategy.equals("LFU")) {
                HashMap<String, Integer> mapCount = (HashMap<String, Integer>) objectInputStream.readObject();
                if (mapCount != null) {
                    mapCountOfUsing = mapCount;
                }
            } else {
                listAgeOfUsing = (LinkedList<String>) objectInputStream.readObject();
            }
            objectInputStream.close();

        } catch (FileNotFoundException e) {
            System.out.println("Файл не создан");
        } catch (IOException e) {
            System.out.println("Диск пуст");
        } catch (ClassNotFoundException e) {
            System.out.println("Класс не был найден");
        }
        if (mapCache.size() == 0) {
            return null;
        } else {
            switch (typeOfStrategy) {
                case "LFU":
                    if (mapCache.containsKey(key)){
                        int countOfUse = mapCountOfUsing.get(key);
                        countOfUse++;
                        mapCountOfUsing.put(key, countOfUse);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                            objectOutputStream.writeObject(mapCache);
                            objectOutputStream.close();
                            fileOutputStream = new FileOutputStream(fileNameHelp);
                            objectOutputStream = new ObjectOutputStream(fileOutputStream);
                            objectOutputStream.writeObject(mapCountOfUsing);
                            objectOutputStream.close();
                        } catch (IOException e) {
                            System.out.println("Ошибка ввода-вывода");
                        }
                        return mapCache.get(key);
                    } break;
                case "MRU":  if(listAgeOfUsing.contains(key)) {
                    return mapCache.get(key);
                }break;
                case "LRU": if (listAgeOfUsing.remove(key)){
                    listAgeOfUsing.addFirst(key);
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                        objectOutputStream.writeObject(mapCache);
                        objectOutputStream.close();
                        fileOutputStream = new FileOutputStream(fileNameHelp);
                        objectOutputStream = new ObjectOutputStream(fileOutputStream);
                        objectOutputStream.writeObject(listAgeOfUsing);
                        objectOutputStream.close();
                    } catch (IOException e) {
                        System.out.println("Ошибка ввода-вывода");
                    }
                    return mapCache.get(key);
                }break;

            }
        }
        return null;
    }

    @Override
    public void allDelete() {
        mapCache.clear();
        listAgeOfUsing.clear();
        mapCountOfUsing.clear();
        mapCache = new HashMap<>(sizeOfCache);
        mapCountOfUsing = new HashMap<>(sizeOfCache);
        listAgeOfUsing = new LinkedList<>();
        try {
            FileWriter fileWriter = new FileWriter(fileName, false);
            PrintWriter printWriter = new PrintWriter(fileWriter, false);
            printWriter.flush();
            printWriter.close();
            fileWriter.close();
            fileWriter = new FileWriter(fileNameHelp, false);
            printWriter = new PrintWriter(fileWriter, false);
            printWriter.flush();
            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Ошибка ввода-вывода");
        }
        System.out.println("Кеш очищен");
    }

}
