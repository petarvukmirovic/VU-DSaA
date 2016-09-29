package lists;

public interface List<E> {
    boolean isEmpty();
    void append(E element);
    int size();
}
