package com.MobileFolk.utils;

import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadLanguage {

    @Test
    public void readFile() throws Exception {
        File dirVi = new File("D:\\i18n\\vi");
        File dirEn = new File("D:\\i18n\\en");

        File[] listVi = dirVi.listFiles();
        File[] listEn = dirEn.listFiles();
        List<Item> items = new ArrayList<>();
        for (File file : listVi) {
            System.out.println("Read file: " + file.getAbsolutePath());
            String pathVi = file.getAbsolutePath();
            readFileAsString2(pathVi, items, true);

        }

        for (File file : listEn) {
            System.out.println("Read file: " + file.getAbsolutePath());
            String pathEn = file.getAbsolutePath();
            readFileAsString2(pathEn, items, false);
        }

        List<Item> itemTmp = new CopyOnWriteArrayList(items);

        for (Item item : items) {
            if (item.valueVi==null || item.valueEn==null)
                itemTmp.remove(item);
        }

        items = new CopyOnWriteArrayList(itemTmp);

        List<Item> itemRemoved = new ArrayList<>();

        for(Item item1 : items) {
            if (isRemovedItem(item1, itemRemoved))
                continue;
            for (Item item2 : items) {
                if (item1.key.equals(item2.key) && item1.valueVi.equals(item2.valueVi) && item1.valueEn.equals(item2.valueEn))
                    continue;

                if (item1.valueEn.equals(item2.valueEn) && item1.valueVi.equals(item2.valueVi)) {
                    itemTmp.remove(item2);
                    itemRemoved.add(item2);
                    }

//                if (Objects.nonNull(item1.valueEn) && Objects.nonNull(item2.valueEn)
//                        && Objects.isNull(item1.valueVi) && Objects.isNull(item2.valueVi)
//                        && item1.valueEn.equals(item2.valueEn)) {
//                    itemTmp.remove(item2);
//                    itemRemoved.add(item2);
//                    }
//
//                if (Objects.isNull(item1.valueEn) && Objects.nonNull(item1.valueVi)
//                        && Objects.isNull(item2.valueEn) && Objects.nonNull(item2.valueVi)
//                        && item1.valueVi.equals(item2.valueVi)) {
//                    itemTmp.remove(item2);
//                    itemRemoved.add(item2);
//                    }
            }
        }


        Collections.sort(itemTmp);

        PrintWriter writer = new PrintWriter("language.csv", "UTF-8");
        for (Item item : itemTmp) {
            if (item.valueEn!=null && item.valueVi!=null && !item.valueEn.isEmpty() && !item.valueVi.isEmpty()) {
                writer.println(String.format("%s\t%s\t%s", item.key, item.valueVi, item.valueEn));
            }
        }
        writer.close();
        for(int i=0;i<itemTmp.size()-1;i++) {
            for (int j=i+1;j<itemTmp.size();j++) {
                if (itemTmp.get(i).key.equals(itemTmp.get(j).key) && !itemTmp.get(i).key.contains("#") && !itemTmp.get(j).key.contains("#")) {
                    itemTmp.get(j).key = "#" + itemTmp.get(j).key;
                }
            }
        }
        PrintWriter writer1 = new PrintWriter("language.properties", "UTF-8");
        PrintWriter writer2 = new PrintWriter("language_vi.properties", "UTF-8");
        PrintWriter writer3 = new PrintWriter("language_en.properties", "UTF-8");
        for (Item item : itemTmp) {
            if (item.valueEn!=null && item.valueVi!=null) {
                writer1.println(String.format("%s=", item.key));
                writer2.println(String.format("%s=%s", item.key, item.valueVi));
                writer3.println(String.format("%s=%s", item.key, item.valueEn));
            }
        }
        writer1.close();
        writer2.close();
        writer3.close();
    }

    public boolean isRemovedItem(Item item, List<Item> itemsRemoved) {

        for (Item i : itemsRemoved) {
            if (item.equals(i)) {
                return true;
            }
        }
        return false;

    }

    public String readFileAsString2(String fileName, List<Item> items, boolean isVi)throws Exception
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));

        Pattern pattern = Pattern.compile("\".+\":\\s+\".+\"", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            String src = matcher.group();
            String[] arr = src.replace("\"", "").split(":\\s+");

            Item item;
            if (isVi)
                item = new Item(arr[0], arr[1], null);
            else
                item = new Item(arr[0], null, arr[1]);
            boolean isNew = true;
            for (Item i : items) {
                if (item.equals(i)) {
                    if (isVi)
                        i.valueVi = arr[1];
                    else
                        i.valueEn = arr[1];
                    isNew = false;
                    break;
                }
            }

            if(isNew)
                items.add(item);

        }
        return data;
    }



    public class Item implements Comparable {
        String key;
        String valueEn;
        String valueVi;

        Item(String key, String valueVi, String valueEn) {
            this.key = key;
            this.valueVi = valueVi;
            this.valueEn = valueEn;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Item)) {
                return false;
            }

            Item i = (Item) obj;

            if (i.key.equals(this.key)) {
                if (valueVi!=null && i.valueVi!=null && !valueVi.equals(i.valueVi))
                    return false;
                if (valueEn!=null && i.valueEn!=null && !valueEn.equals(i.valueEn))
                    return false;
            } else
                return false;

            return true;
        }

        @Override
        public int compareTo(Object o) {
            if (this.valueVi.equals(((Item) o).valueVi))
                return this.key.compareTo(((Item) o).key);
            else
                return this.valueVi.compareTo(((Item) o).valueVi);
        }
    }

}
