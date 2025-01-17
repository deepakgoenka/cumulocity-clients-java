/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.cumulocity.me.lang;

class SingletonMap implements Map {
    
    private final Object key;
    private final Object value;

    public SingletonMap(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public int size() {
        return 1;
    }

    public boolean isEmpty() {
        return false;
    }
    
    public boolean containsKey(Object key) {
        return key.equals(key);
    }

    public Object get(Object key) {
        if (containsKey(key)) {
            return value;
        } else { 
            return null;
        }
    }

    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map newParams) {
        throw new UnsupportedOperationException();
    }

    public Set keySet() {
        return new SingletonSet(key);
    }

    public Set entrySet() {
        return new SingletonSet(new MapEntry(key, value));
    }
}