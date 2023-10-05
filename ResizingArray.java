import java.util.Iterator;

public class ResizingArray<T> implements Iterable<T> {

    public static final int DEFAULT_CAPACITY = 16;

    private T[] array;

    public ResizingArray() {
        this(DEFAULT_CAPACITY);
    }

    public ResizingArray(int capacity) {
        this.array = (T[]) new Object[capacity];
    }

    public int capacity() {
        return this.array.length;
    }

    public T get(int index) {
        if (index < this.array.length) {
            return this.array[index];
        } else {
            return null;
        }
    }

    public void set(int index, T value) {
        if (index >= this.array.length) {
            resize(index);
        }
        this.array[index] = value;
    }

    private void resize(int size) {
        int capacity = this.array.length;
        while (capacity <= size) {
            capacity *= 2;
        }

        T[] oldArray = this.array;
        this.array = (T[]) new Object[capacity];
        for (int i = 0; i < oldArray.length; i++) {
            this.array[i] = oldArray[i];
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<T> {

        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < array.length;
        }

        @Override
        public T next() {
            return array[current++];
        }
    }
}
