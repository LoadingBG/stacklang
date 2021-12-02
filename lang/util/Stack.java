package lang.util;

import java.util.ArrayList;
import java.util.List;

public class Stack<E> {
    private List<E> elems;

    public Stack() {
        elems = new ArrayList<>();
    }

    public void push(E elem) {
        elems.add(elem);
    }

    public E pop() {
        return elems.remove(elems.size() - 1);
    }

    public E peek() {
        return elems.get(elems.size() - 1);
    }

    public int size() {
        return elems.size();
    }

    public boolean isEmpty() {
        return elems.isEmpty();
    }

    @Override
    public int hashCode() {
        return elems.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Stack) {
            Stack<?> s = (Stack<?>) o;
            return elems.equals(s.elems);
        }
        return false;
    }

    @Override
    public String toString() {
        return elems.toString();
    }
}
