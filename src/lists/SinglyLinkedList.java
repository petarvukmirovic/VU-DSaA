package lists;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
public class SinglyLinkedList<E> implements List<E>, Iterable<E> {

    public SinglyLinkedList() {

    }

    @Override
    public boolean isEmpty() {
        return _head == null;
    }

    /**
     * Add a node at the end of the linked list.
     * Operation complexity: O(1).
     * @param element Element to be added.
     */
    @Override
    public void append(E element) {
        SLLNode<E> newNode = new SLLNode<>(element);

        if (_head == null) {
            assert(_tail == null);

            _head = _tail = newNode;
        }
        else {
            assert(_tail != null);

            _tail.setNext(newNode);
            _tail = newNode;
        }
    }

    /**
     * Add a node at the beginning of the linked list.
     * Operation complexity: O(1).
     * @param element Element to be added.
     */
    public void prepend(E element) {
        SLLNode<E> newNode = new SLLNode<>(element);

        if (_head == null) {
            assert (_tail == null);

            _head = _tail = newNode;
        } else {
            assert (_tail != null);

            newNode.setNext(_head);
            _head = newNode;
        }
    }

    /**
     * Delete a node from the list. Node has to be in the list.
     * Operation complexity: O(length(list))
     * @param nodeToDelete A list node that is to be deleted
     */
    public void delete(SLLNode<E> nodeToDelete) {
        assert(nodeToDelete != null);

        SLLNode<E> prev = null;
        SLLNode<E> curr = _head;

        while(curr != null) {
            if (curr == nodeToDelete){
                _deleteNode(prev);
                break;
            }

            prev = curr;
            curr = curr.getNext();
        }

        if (curr == null) {
            throw new IllegalArgumentException("Node is not found");
        }
    }

    /**
     * Delete a node given the one that precedes it.
     * Operation complexity: O(1)
     * @param previousNode Node that precedes the one that is to be deleted,
     *                     or null if the node to be deleted is the head of
     *                     the list.
     */
    private void _deleteNode(SLLNode<E> previousNode) {
        if (previousNode == null) {
            _head = _head.getNext();
            if (_head == null) {
                _tail = null;
            }
        }
        else {
            SLLNode<E> nodeToDelete = previousNode.getNext();
            assert nodeToDelete != null;

            previousNode.setNext(nodeToDelete.getNext());
            if (nodeToDelete == _tail){
                _tail = previousNode;
            }
        }
    }

    /**
     * Calculate the size of the list.
     * Operation complexity: O(length(list))
     * @return The list length
     */
    @Override
    public int size() {
        int count = 0;

        SLLNode<E> temp = _head;

        while(temp != null){
            count++;

            temp = temp.getNext();
        }

        return count;
    }

    private SLLNode<E> _head;
    // _tail added to enable some operations to be O(1)
    private SLLNode<E> _tail;

    @Override
    public Iterator<E> iterator() {
        return new SinglyLinkedListIterator<>(this);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        this.iterator().forEachRemaining(action);
    }

    @Override
    public Spliterator<E> spliterator() {
        throw new NotImplementedException();
    }

    private class SLLNode<E> {
        private SLLNode<E> next;
        private E data;

        public SLLNode(E data){
            this.data = data;
            next = null;
        }

        public SLLNode(E data, SLLNode<E> next)
        {
            this(data);
            this.next = next;
        }

        public void setNext(SLLNode<E> next) {
            this.next = next;
        }

        public void setData(E data) {
            this.data = data;
        }

        public SLLNode<E> getNext() {
            return next;
        }

        public E getData() {
           return data;
        }

    }

    // Class used as an Iterator through LinkedList
    /*
     * It can be disregarded, as it is primarily used to enable
     * Java 5 collection for loops.
     */
    private class SinglyLinkedListIterator<E> implements Iterator<E> {
        private SinglyLinkedList<E>.SLLNode<E> forDelete;
        private SinglyLinkedList<E>.SLLNode<E> curr;
        private SinglyLinkedList<E> _traveresedList;

        private int currPosition;

        public SinglyLinkedListIterator(SinglyLinkedList<E> list) {
            curr = list._head;
            forDelete = list._head;
            currPosition = 0;
            _traveresedList = list;
        }

        @Override
        public boolean hasNext() {
            return curr != null;
        }

        @Override
        public E next() {
            if (curr == null){
                throw new NoSuchElementException();
            }
            else {
                E toReturn = curr.getData();

                if (currPosition > 2 && forDelete.getNext() != curr) {
                    forDelete = forDelete.getNext();
                }
                else {
                    currPosition++;
                }

                curr = curr.getNext();
                return toReturn;
            }
        }

        @Override
        public void remove() {
            if (currPosition == 0) {
                throw new IllegalStateException("next() is not called yet");
            }
            else if (currPosition == 1) {
                _traveresedList._deleteNode(null);
            }
            else {
                _traveresedList._deleteNode(forDelete);
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            while(hasNext()){
                action.accept(next());
            }
        }
    }

    public static void main(String args[]){
        /* Shows how to use the API */
        SinglyLinkedList<Integer> intSLL = new SinglyLinkedList<>();

        IntStream.range(1, 100)
                 .forEach(i -> intSLL.append(i));

        for(Integer i : intSLL){
            System.out.println(i);
        }


        /*Iterator<Integer> SLLIterator = intSLL.iterator();

        while(SLLIterator.hasNext()){
            Integer element = SLLIterator.next();

            if (element % 2 == 0){
                SLLIterator.remove();
            }
        }

        for(Integer i : intSLL){
            System.out.println(i);
        }*/
    }
}
