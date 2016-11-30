package simulation;

public class Pair<T> {
    private T first;
    private T second;

    public T getFirst() {return first;}
    public T getSecond() {return second;}
    public void setFirst(T value) {
        first = value;
    }
    public void setSecond(T value) {
        second = value;
    }

    public Pair(T first, T second) {
        setFirst(first);
        setSecond(second);
    }
}