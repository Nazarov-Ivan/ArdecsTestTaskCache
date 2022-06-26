package com.ardecs.testcache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Выберите хранилище: disk, ram");
            String typeOfStorage = bufferedReader.readLine();
            System.out.println("Введите размер кеша");
            int sizeOfCache = Integer.parseInt(bufferedReader.readLine());
            System.out.println("Выберите стратегию удаления старых элементов: LFU, MRU, LRU");
            String typeOfStrategy = bufferedReader.readLine();
            if ((typeOfStrategy.equals("LFU") == false) && (typeOfStrategy.equals("MRU") == false) && (typeOfStrategy.equals("LRU") == false)){
                System.out.println("Перезапустите программу и введите стратегию удаления корректно");
                System.exit(0);
            }
            Cache myCache = null;
            if (typeOfStorage.equals("disk")){
                myCache = new CacheDisk(sizeOfCache,typeOfStrategy);
            } else if (typeOfStorage.equals("ram")){
                myCache = new CacheRAM(sizeOfCache, typeOfStrategy);
            } else {
                System.out.println("Перезапустите программу и введите хранилище корректно");
                System.exit(0);
            }
            while (true){
                System.out.println("Введите команду");
                String key = bufferedReader.readLine();
                if (key.equals("delete")) {
                    myCache.allDelete();
                    continue;
                }
                if(myCache.get(key) == null){
                    System.out.println("Результат команды " + key +
                            " не закеширован, введите пожалуйста результат");
                    String value = bufferedReader.readLine();
                    myCache.add(key,value);
                    System.out.println("Результат сохранен");
                } else {
                    System.out.println("Результатом выполнения команды " +
                            key + " является " + myCache.get(key));
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода-вывода");
        }


    }
}
