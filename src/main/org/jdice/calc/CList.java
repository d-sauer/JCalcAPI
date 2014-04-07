/*
 * Copyright 2014 Davor Sauer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.jdice.calc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * List based on LinkedList that can hold object used by JCalc API.
 * Used for holding infix and postfix expressions 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class CList {

    private LinkedList<Object> list = new LinkedList<Object>();
    private CListListener listener;

    public CList() {

    }

    public CList(CListListener listener) {
        this.listener = listener;
    }

    private void change() {
        if (listener != null)
            listener.change();
    }

    public boolean add(Bracket bracket) {
        change();
        return list.add(bracket);
    }
    
    public boolean add(Operator operator) {
        change();
        return list.add(operator);
    }

    public boolean add(FunctionData functionData) {
        change();
        return list.add(functionData);
    }

    public boolean add(Function function) {
        change();
        return list.add(function);
    }

    public boolean add(Num value) {
        change();
        return list.add(value);
    }

    public boolean add(Bracket bracket, Num calcValue) {
        change();
        boolean isAdded = list.add(bracket);
        if (isAdded)
            isAdded = list.add(calcValue);
        return isAdded;
    }

    public boolean add(Class<? extends Operator> operationClass)  {
        change();
        Operator operationData = Cache.getOperator(operationClass);
        boolean isAdded = list.add(operationData);
        return isAdded;
    }

    public boolean addFunction(Class<? extends Function> functionClass, Num ... values)  {
        change();
        Function function = Cache.getFunction(functionClass);
        boolean isAdded = list.add(new FunctionData(function, values));
        return isAdded;
    }

    public boolean addFunction(FunctionData functionData)  {
        change();
        boolean isAdded = list.add(functionData);
        return isAdded;
    }

    public boolean add(Class<? extends Operator> operationClass, Num calcValue)  {
        change();
        Operator operationData = Cache.getOperator(operationClass);
        boolean isAdded = list.add(operationData);
        if (isAdded)
            isAdded = list.add(calcValue);
        return isAdded;
    }

    public CList put(Operator operationClass) {
        change();
        list.add(operationClass);
        return this;
    }

    public CList put(Num calcValue) {
        change();
        list.add(calcValue);
        return this;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Num o) {
        return list.contains(o);
    }

    public boolean contains(Operator operationClass) {
        return list.contains(operationClass);
    }

    public Iterator<Object> iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public void clear() {
        list.clear();
    }

    public Object get(int index) {
        return list.get(index);
    }

    public Object set(int index, Object element) {
        return list.set(index, element);
    }

    public void add(int index, Object element) {
        list.add(index, element);
    }

    public Object remove(int index) {
        return list.remove(index);
    }

    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

}
