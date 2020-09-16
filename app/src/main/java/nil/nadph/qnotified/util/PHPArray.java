/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.util;

import java.math.BigDecimal;
import java.util.*;

/**
 * A simple class to use json and vector like php array
 * When I wrote this class, both God and me could understand,
 * But now, only the God.
 *
 * @author cinit@github
 */
public class PHPArray implements Iterable<HashMap.Entry> {
    private final static int _TYPE_OBJECT = 2;
    private final static int _TYPE_ARRAY = 3;
    public HashMap map;

    public PHPArray() {
        map = new HashMap();
    }

    public static boolean isset(Ref ref) {
        try {
            return ref._$() != null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void unset(Ref ref) {
        Object index;
        PHPArray last = ref.root;
        int ii;
        String si;
        for (int i = 0; i < ref.chain.size(); i++) {
            index = ref.chain.get(i);
            if (index == null) throw new RuntimeException("Cannot use [] for unsetting");
            if (last.map.containsKey(index)) {
                if (i != ref.chain.size() - 1) {
                    try {
                        last = (PHPArray) last.map.get(index);
                    } catch (ClassCastException e) {
                        throw new RuntimeException("Cannot use " + last.map.get(index).getClass().getName() + " as an array");
                    }
                } else last.map.remove(index);
            } else {
                System.out.println("WARNING: Use null as an array for unsetting");
                return;
            }
        }
    }

    //@Deprecated
    public static PHPArray fromJson(String json) {
        int off = 0;
        Stack<Integer> types = new Stack<>();
        Stack<PHPArray> stack = new Stack<>();
        boolean hasValue = false;
        Object value = null;
        Stack keys = new Stack();
        int t;
        boolean readKey = false;
        PHPArray root = new PHPArray();
        char c;
        PHPArray curr;
        while (off < json.length()) {
            c = json.charAt(off);
            off++;
            switch (c) {
                case ' ':
                case '\n':
                case '\r':
                case '\b':
                case '\0':
                case '\u3000':
                    continue;
                case '[':
                    if (types.empty()) curr = root;
                    else curr = new PHPArray();
                    types.push(_TYPE_ARRAY);
                    stack.push(curr);
                    readKey = false;
                    hasValue = false;
                    break;
                case '{':
                    if (types.empty()) curr = root;
                    else curr = new PHPArray();
                    types.push(_TYPE_OBJECT);
                    stack.push(curr);
                    readKey = true;
                    hasValue = false;
                    break;
                case ']':
                case '}':
                    if (hasValue) {
                        t = types.peek();
                        if (t == _TYPE_OBJECT) {
                            stack.peek().__(keys.pop()).$$(value);
                        } else if (t == _TYPE_ARRAY) {
                            stack.peek().__().$$(value);
                        }
                    }
                    types.pop();
                    value = stack.pop();
                    hasValue = true;
                    break;
                case '"':
                    StringBuilder ret = new StringBuilder();
                    while (off < json.length()) {
                        int q = json.indexOf("\"", off);
                        int bs = json.indexOf("\\", off);
                        if (bs != -1 && bs > q) bs = -1;
                        if (bs == -1) {
                            ret.append(json, off, q);
                            off = q + 1;
                            if (readKey) keys.push(ret.toString());
                            else value = ret.toString();
                            hasValue = true;
                            break;
                        } else {
                            ret.append(json, off, bs);
                            c = json.charAt(bs + 1);
                            switch (c) {
                                case '"':
                                    ret.append('"');
                                    break;
                                case 'n':
                                    ret.append('\n');
                                    break;
                                case 'r':
                                    ret.append('\r');
                                    break;
                                case '/':
                                    ret.append('/');
                                    break;
                                case '\\':
                                    ret.append('\\');
                                    break;
                                case '0':
                                    ret.append('\0');
                                    break;
                                case 'b':
                                    ret.append('\b');
                                    break;
                                case 't':
                                    ret.append('\t');
                                    break;
                                default:
                                    throw new IllegalArgumentException("Bad \\" + c);
                            }
                            off = bs + 2;
                        }
                    }
                    break;
                case ':':
                    readKey = false;
                    break;
                case ',':
                    if (!hasValue) continue;
                    t = types.peek();
                    if (t == _TYPE_OBJECT) {
                        stack.peek().__(keys.pop()).$$(value);
                        readKey = true;
                    } else if (t == _TYPE_ARRAY) {
                        stack.peek().__().$$(value);
                        readKey = false;
                    }
                    hasValue = false;
                    break;
                default:
                    hasValue = true;
                    try {
                        if (json.substring(off - 1, off + 3).equalsIgnoreCase("null")) {
                            off = off + 3;
                            value = null;
                            break;
                        } else if (json.substring(off - 1, off + 3).equalsIgnoreCase("true")) {
                            off = off + 3;
                            value = true;
                            break;
                        } else if (json.substring(off - 1, off + 4).equalsIgnoreCase("false")) {
                            off = off + 4;
                            value = false;
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                    int i1 = json.indexOf(",", off);
                    int i2 = json.indexOf("]", off);
                    int i3 = json.indexOf("}", off);
                    if (i1 == -1) i1 = json.length();
                    if (i2 == -1) i2 = json.length();
                    if (i3 == -1) i3 = json.length();
                    int end = Math.min(i3, Math.min(i1, i2));
                    try {
                        BigDecimal num = new BigDecimal(json.substring(off - 1, end).replace(" ", ""));
                        if (readKey) {
                            keys.push(num.intValue());
                        } else {
                            value = num;
                        }
                        off = end;
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Unexpected \"" + json.substring(off - 1, end) + "\" at " + (off - 1));
                    }
            }
        }
        return root;
    }

    private static String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\n", "\\n").replace("\0", "\\0")
                .replace("\t", "\\t").replace("\b", "\\b").replace("\r", "\\r").replace("\"", "\\\"") + "\"";
    }

    public static boolean array_key_exists(String k, PHPArray php) {
        return php.map.containsKey(k);
    }

    public static String strtolower(String s) {
        return s.toLowerCase();
    }

    public static String str_replace(String from, String to, String str) {
        return str.replace(from, to);
    }

    public static PHPArray array() {
        return new PHPArray();
    }

    public static PHPArray array_map(Object... x) {
        if (x.length % 2 == 1) throw new IllegalArgumentException("x.length == " + x.length);
        PHPArray ret = new PHPArray();
        for (int i = 0; i < x.length / 2; i++) {
            ret.__(x[i * 2]).$$(x[i * 2 + 1]);
        }
        return ret;
    }

    public static PHPArray array_arr(Object... x) {
        PHPArray ret = new PHPArray();
        for (int i = 0; i < x.length; i++) {
            ret.__().$$(x[i]);
        }
        return ret;
    }

    public static int count(PHPArray arr) {
        return arr.map.size();
    }

    public static int count(PHPArray.Ref ref) {
        return ref._$_().map.size();
    }

    public static boolean in_array(Object obj, PHPArray arr) {
        return arr.map.containsValue(obj);
    }

    public static Object array_search(Object val, PHPArray arr) {
        for (Map.Entry entry : arr) {
            Object a = val, b = entry.getValue();
            // Objcets.equals requires API19,current14
            //noinspection EqualsReplaceableByObjectsCall
            if ((a == b) || a != null && a.equals(b)) return entry.getKey();
        }
        return false;
    }

    public static PHPArray array_merge(PHPArray... arrs) {
        PHPArray ret = array();
        for (PHPArray a : arrs) {
            for (Map.Entry en : a) {
                if (en.getKey() instanceof Integer) {
                    ret.__().$$(en.getValue());
                } else {
                    ret.__(en.getKey()).$$(en.getValue());
                }
            }
        }
        return ret;
    }

    public Ref __(Object i) {
        return new Ref(this).__(i);
    }

    public Ref __() {
        return new Ref(this).__();
    }

    public Collection _$_E() {
        return map.values();
    }

    @Override
    public Iterator<HashMap.Entry> iterator() {
        return map.entrySet().iterator();
    }

    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        HashMap.Entry en;
        Object v;
        int size = map.size();
        StringBuilder sb = new StringBuilder();
        if (isStdArray()) {
            sb.append('[');
            for (int i = 0; i < size; i++) {
                v = map.get(i);
                if (v == null) sb.append("null");
                else if (v instanceof String) sb.append(quote((String) v));
                else if (v instanceof BigDecimal) sb.append(v.toString());
                else sb.append(v);
                if (i + 1 != size) sb.append(',');
            }
            sb.append(']');
            return sb.toString();
        } else {
            sb.append('{');
            for (Object o : map.entrySet()) {
                en = (Map.Entry) o;
                v = en.getKey();
                sb.append(quote("" + v));
                sb.append(':');
                v = en.getValue();
                if (v == null) sb.append("null");
                else if (v instanceof String) sb.append(quote((String) v));
                else if (v instanceof BigDecimal) sb.append(((BigDecimal) v).toPlainString());
                else sb.append(v);
                sb.append(',');
            }
            if (sb.charAt(sb.length() + -1) == ',') sb.deleteCharAt(sb.length() - 1);
            sb.append('}');
            return sb.toString();
        }
    }

    private boolean isStdArray() {
        int i;
        int max = -1;
        int size = map.size();
        if (size == 0) return true;
        for (Object k : map.keySet()) {
            if (k instanceof String) return false;
            i = (Integer) k;
            if (i < 0) return false;
            if (max < i) max = i;
        }
        return max + 1 == size;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public static class Ref {
        ArrayList chain;
        private final PHPArray root;

        Ref(PHPArray arr) {
            root = arr;
            chain = new ArrayList();
        }

        public Ref __() {
            chain.add(null);
            return this;
        }

        public Ref __(Object i) {
            if (i == null) chain.add("");
            else if (i instanceof String) {
                boolean dec = false;
                int ii = 0;
                try {
                    ii = Integer.parseInt((String) i);
                    if (((String) i).equalsIgnoreCase("" + ii)) dec = true;
                } catch (NumberFormatException ignored) {
                }
                if (dec) chain.add(ii);
                else chain.add(i);
            } else if (i instanceof Number) chain.add(((Number) i).intValue());
            else if (i instanceof Boolean) {
                if ((Boolean) i) chain.add(1);
                else chain.add(0);
            } else throw new RuntimeException("Illegal offset type " + i.getClass().getName());
            return this;
        }

        public Object _$() {
            Object index;
            PHPArray last = root;
            int ii;
            String si;
            for (int i = 0; i < chain.size(); i++) {
                index = chain.get(i);
                if (index == null) throw new RuntimeException("Cannot use [] for reading");
                if (last.map.containsKey(index)) {
                    if (i != chain.size() - 1) {
                        try {
                            last = (PHPArray) last.map.get(index);
                        } catch (ClassCastException e) {
                            throw new RuntimeException("Cannot use " + last.map.get(index).getClass().getName() + " as an array");
                        }
                    } else return last.map.get(index);
                } else {
                    System.out.println("WARNING: Use null as an array");
                    return null;
                }
            }
            throw new RuntimeException("Please do NOT use this in multi-thread mode.");
        }

        public void $$(Object obj) {
            if (obj instanceof Ref) obj = ((Ref) obj)._$();
            Object index;
            PHPArray last = root;
            int ii;
            String si;
            for (int i = 0; i < chain.size(); i++) {
                index = chain.get(i);
                if (index == null) {
                    index = nextIndex(last.map.keySet());
                }
                if (i != chain.size() - 1) {
                    if (last.map.containsKey(index)) {
                        try {
                            last = (PHPArray) last.map.get(index);
                        } catch (ClassCastException e) {
                            throw new RuntimeException("Cannot use " + last.map.get(index).getClass().getName() + " as an array");
                        }
                    } else {
                        PHPArray t = new PHPArray();
                        last.map.put(index, t);
                        last = t;
                    }
                } else {
                    last.map.put(index, obj);
                }
            }
        }

        public PHPArray _$_() {
            return (PHPArray) _$();
        }

        public boolean _$b() {
            return (Boolean) _$();
        }

        public Collection _$_E() {
            return _$_().map.values();
        }

        private int nextIndex(Set set) {
            int i = 0;
            int ci;
            for (Object k : set) {
                if (k instanceof Integer) {
                    ci = (Integer) k;
                    i = Math.max(i, ci + 1);
                }
            }
            return i;
        }

        public boolean isEmpty() {
            try {
                return ((PHPArray) _$()).isEmpty();
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

    /*public static class KVIterator implements Iterator{
        KVPair[] pairs;
        KVIterator(HashMap map){
            pairs=new KVPair[map.size()];
            for ()
        }

        @Override
        public b
    public static class KVPair{
        public Object k;
        public Object v;
    }oolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }
    }

    public static class KVPair{
        public Object k;
        public Object v;
    }*/
}
