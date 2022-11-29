package com.javarush.task.task20.task2028;

import java.io.Serializable;
import java.util.*;

/* 
Построй дерево(1)
*/

public class CustomTree extends AbstractList<String> implements Cloneable, Serializable {
    /**
     * Стартовый корень для дерева;
     */
    Entry<String> root;

    /**
     * Переменная для фиксации уровня в котором все элементы имеют значения. Данная переменная необходима восстановления
     * возможности дерева вновь добавлять значения после удалений элементов;
     */
    int maxLevelAllInstallElementName;

    /**
     * root инициализируется по умолчанию, и становится первым элементом обозначенным как "новый корневой элемент для
     * текущего уровня дерева";
     */
    public CustomTree() {
        root = new Entry<>("0");
        root.parent = root;
        root.level = 0;
        maxLevelAllInstallElementName = 0;
    }

    /**
     * Метод add добавляет элемент в дерево. Если с первого раза не удалось добавить элемент, то восстанавливается возможность
     * добавления методом restoreAddition();
     *
     * @param s строка (String) которую необходимо добавить в коллекцию;
     * @return true после добавления нового элемента;
     */
    @Override
    public boolean add(String s) {
        boolean success = addByQueue(s);
        if(!success){
            restoreAddition();
            success = addByQueue(s);
        }
        return success;
    }

    /**
     * Метод add добавляет элемент в дерево обходом в ширину с помощью очереди.
     *
     * @param s строка (String) которую необходимо добавить в коллекцию;
     * @return true после добавления нового элемента;
     */
    private boolean addByQueue(String s) {
        Queue<Entry<String>> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Entry<String> entry = queue.poll();
            if(entry == null){
                continue;
            }
            if (entry.isAvailableToAddChildren()) {
                if (entry.availableToAddLeftChildren) {
                    entry.leftChild = new Entry<>(s);
                    entry.leftChild.parent = entry;
                    entry.leftChild.level = entry.level + 1;
                    entry.availableToAddLeftChildren = false;
                    return true;
                }

                if (entry.availableToAddRightChildren) {
                    entry.rightChild = new Entry<>(s);
                    entry.rightChild.parent = entry;
                    entry.rightChild.level = entry.level++;
                    entry.availableToAddRightChildren = false;
                    return true;
                }
            } else {
                queue.add(entry.leftChild);
                queue.add(entry.rightChild);
            }
        }
        return false;
    }

    /**
     * Метод getParent принимает значение String value, ищет в древе Entry c значением elementName эквивалентным
     * значению value, и возвращает значение elementName у найденного Entry (Entry.parent)
     *
     * @param s elementName переданного Entry
     * @return parent.elementName найденного Entry (Entry.parent)
     */
    public String getParent(String s) {
        Queue<Entry<String>> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Entry<String> entry = queue.poll();
            if (entry.leftChild != null && entry.leftChild.elementName.equals(s) ||
                    entry.rightChild != null && entry.rightChild.elementName.equals(s)) {
                return entry.elementName;
            } else {
                if (entry.leftChild != null) {
                    queue.add(entry.leftChild);
                }
                if (entry.rightChild != null) {
                    queue.add(entry.rightChild);
                }
            }
        }
        return null;
    }

    @Override
    public String get(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return Размер коллекции элементов Entry<String>;
     */
    @Override
    public int size() {
        Queue<Entry<String>> queue = new LinkedList<>();
        int size = 0;
        queue.add(root);
        while (!queue.isEmpty()) {
            Entry<String> entry = queue.poll();
            if(entry == null){
                continue;
            }
            if (entry.leftChild != null) {
                size++;
                queue.add(entry.leftChild);
            }
            if (entry.rightChild != null) {
                size++;
                queue.add(entry.rightChild);
            }
        }
        return size;
    }

    @Override
    public String set(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, String element) {
        throw new UnsupportedOperationException();
    }

    /**
     * Если ! о instanceof String throws UnsupportedOperationException();
     * При удалении устанавливает maxLevelAllInstallElementName максимальным значением уровня в дереве при котором все
     * элементы имеют свои значения;
     * Удаляет первый найденный в дереве элемент Entry<String> со значением параметра elementName
     * эквивалентного o и, в случае успеха возвращает true. Если элемент не найден возвращается false;
     *
     * @param o значение строки подлежащей удалению из дерева.
     * @return Если элемент найден и удален возвращается true, иначе false;
     */
    @Override
    public boolean remove(Object o) {
        if(!(o instanceof String)){
            throw new UnsupportedOperationException();
        }
        String s = (String) o;

        Queue<Entry<String>> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Entry<String> entry = queue.poll();
            if(entry == null){
                continue;
            }
            if (entry.leftChild != null && !entry.availableToAddLeftChildren && entry.leftChild.elementName.equals(s)) {
                entry.leftChild = null;
                if(entry.level > maxLevelAllInstallElementName){
                    maxLevelAllInstallElementName = entry.level;
                }
                return true;
            }
            if (entry.rightChild != null && !entry.availableToAddRightChildren && entry.rightChild.elementName.equals(s)) {
                entry.rightChild = null;
                if(entry.level > maxLevelAllInstallElementName){
                    maxLevelAllInstallElementName = entry.level;
                }
                return true;
            } else {
                queue.add(entry.leftChild);
                queue.add(entry.rightChild);
            }
        }
        return false;
    }

    /**
     * Восстанавливает возможность дерева устанавливать новые значения после удаления всего уровня;
     */
    private void restoreAddition(){
        Queue<Entry<String>> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()){
            Entry<String> entry = queue.poll();
            if(entry == null){
                continue;
            }
            if(entry.level == maxLevelAllInstallElementName){
                entry.availableToAddLeftChildren = true;
                entry.availableToAddRightChildren = true;
            }else {
                queue.add(entry.leftChild);
                queue.add(entry.rightChild);
            }
        }
    }

    @Override
    public String remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    static class Entry<T> implements Serializable {
        java.lang.String elementName;
        boolean availableToAddLeftChildren, availableToAddRightChildren;
        Entry<java.lang.String> parent, leftChild, rightChild;
        int level;

        public Entry(java.lang.String elementName) {
            this.elementName = elementName;
            availableToAddLeftChildren = true;
            availableToAddRightChildren = true;
        }

        public boolean isAvailableToAddChildren() {
            return availableToAddLeftChildren || availableToAddRightChildren;
        }
    }
}
