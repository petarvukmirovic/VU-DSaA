package trees;

import java.util.Comparator;
import java.util.function.Function;

/* General Binary Search Tree */
public class BSTree<E> {
    private Comparator<E> cmp;

    // We will either provide Comparator object or
    // have BST of items that are implementing Comparable
    // interface -- we do not want to limit ourselves only
    // on integers
    public BSTree(Comparator<E> cmp){
        this.cmp = cmp;
    }
    public BSTree(){
        this(null);
    }

    public Node<E> getRoot(){
        return root;
    }

    protected void LeftRotate(Node<E> start){
        if (start.getRight() == null){
            throw new IllegalArgumentException("start node must have right subtree");
        }

        Node<E> originalParent = start.getParent();
        Node<E> rightNode = start.getRight();
        changeChild(start, start.getRight(), rightNode.getLeft());
        changeChild(rightNode, rightNode.getLeft(), start);
        changeChild(originalParent, start, rightNode);
    }

    protected void RightRotate(Node<E> start){
        if (start.getLeft() == null){
            throw new IllegalArgumentException("start node must have left subtree");
        }

        Node<E> originalParent = start.getParent();
        Node<E> leftNode = start.getLeft();
        changeChild(start, start.getLeft(), leftNode.getRight());
        changeChild(leftNode, leftNode.getRight(), start);
        changeChild(originalParent, start, leftNode);
    }

    protected static class Node<E> {
        public Node(E key, Node<E> l, Node<E> r, Node<E> p){
            this.key = key;
            this.setLeft(l);
            this.setRight(r);
            this.setParent(p);
        }

        public Node(E key){
            this(key, null, null, null);
        }

        public Node(E key, Node<E> l, Node<E> r){
            this(key, l, r, null);
        }

        public boolean isLeftChildOf(Node<E> other){
            if (other == null){
                return false;
            }
            return other.getLeft() == this;
        }

        public boolean isRightChildOf(Node<E> other){
            if (other == null){
                return false;
            }
            return other.getRight() == this;
        }

        public boolean isParentOf(Node<E> other){
            if (other == null){
                return false;
            }
            return other.getParent() == this;
        }

        public boolean isRoot(){
            return getParent() == null;
        }

        private E key;

        private Node<E> left;
        private Node<E> right;
        private Node<E> parent;

        protected Node<E> getLeft() {
            return left;
        }

        protected void setLeft(Node<E> left) {
            this.left = left;
        }

        protected Node<E> getRight() {
            return right;
        }

        protected void setRight(Node<E> right) {
            this.right = right;
        }

        protected Node<E> getParent() {
            return parent;
        }

        protected void setParent(Node<E> parent) {
            this.parent = parent;
        }
    }

    private Node<E> root;

    protected int compareKeys(E key1, E key2){
        if (cmp != null){
            return cmp.compare(key1, key2);
        }
        else{
            try{
                return ((Comparable<E>) key1).compareTo(key2);
            }
            catch(ClassCastException e){
                throw new IllegalArgumentException("Keys must either implement Comparable<E> " +
                                                   "or a Comparator<E> has to be given ");
            }
        }
    }

    // Set the oldChild of the parent to be the newChild and
    // change the pointers that have to be changed in the
    // process
    protected void changeChild(Node<E> parent, Node<E> oldChild, Node<E> newChild){
        if (parent == null || (oldChild != null && oldChild.isRoot())){
            root = newChild;
            if (newChild != null) {
                newChild.setParent(null);
            }
        }
        else {
            /* If the child is null, it might be the case that the parent
               has both null children in which case, we have to choose
               the right subtree to insert the newChild to.
             */
            if (oldChild == null){
                if (newChild == null){
                    return;
                }
                int cmpRes = compareKeys(parent.key, newChild.key);
                if (cmpRes < 0 && parent.getRight() == null){
                    parent.setRight(newChild);
                }
                else if (cmpRes > 0 && parent.getLeft() == null){
                    parent.setLeft(newChild);
                }
                newChild.setParent(parent);
            }
            else {
                if (parent.getLeft() == oldChild) {
                    parent.setLeft(newChild);
                } else if (parent.getRight() == oldChild) {
                    parent.setRight(newChild);
                } else {
                    throw new IllegalArgumentException("first argument is not the parent of the" +
                            " second argument");
                }

                if (newChild != null) {
                    newChild.setParent(parent);
                }
            }
        }
    }
    /* Try to delete a node with key "key".
       If deletion is successful, return true,
       else return false.
     */
    public boolean delete(E key){
        Node<E> nodeToDelete = find(key);
        if (nodeToDelete == null){
            return false; // node not found
        }
        else {
            doDelete(nodeToDelete);
            return true;
        }
    }

    protected Node<E> doDelete(Node<E> nodeToDelete){
        if (nodeToDelete.getLeft() == null && nodeToDelete.getRight() == null){
            if (nodeToDelete.isLeftChildOf(nodeToDelete.getParent())){
                nodeToDelete.getParent().setLeft(null);
            }
            else if (nodeToDelete.isRightChildOf(nodeToDelete.getParent())) {
                nodeToDelete.getParent().setRight(null);
            }
            else if (nodeToDelete == root){
                root = null;
            }

            return nodeToDelete.getParent();
        }
        else if (nodeToDelete.getLeft() != null && nodeToDelete.getRight() == null) {
            changeChild(nodeToDelete.getParent(), nodeToDelete, nodeToDelete.getLeft());
            return nodeToDelete.getLeft();
        }
        else if (nodeToDelete.getLeft() == null && nodeToDelete.getRight() != null) {
            changeChild(nodeToDelete.getParent(), nodeToDelete, nodeToDelete.getRight());
            return nodeToDelete.getRight();
        }
        else { // has both children
            Node<E> newNodeToDelete = subtreeMin(nodeToDelete.getRight());
            Node<E> nodeToReturn = doDelete(newNodeToDelete); // it is a leaf, so it will be run only once
            nodeToDelete.key = newNodeToDelete.key;

            return nodeToReturn;
        }
    }

    public boolean isEmpty(){
        return root == null;
    }

    public Node<E> find(E key){
        Node<E> tmp = root;

        while(tmp != null){
            int cmpRes = compareKeys(tmp.key, key);
            if (cmpRes == 0){
                break;
            }
            else if (cmpRes > 0){
                tmp = tmp.getLeft();
            }
            else {
                tmp = tmp.getRight();
            }
        }

        return tmp;
    }
    public boolean insert(E key){
        Node<E> toInsert = new Node<E>(key);
        return doInsert(toInsert);
    }

    protected boolean doInsert(Node<E> newNode) {
        E key = newNode.key;

        Node<E> tmp = root;
        Node<E> prev = null;

        int cmpRes = 0;
        while(tmp != null){
            cmpRes = compareKeys(tmp.key, key);
            if (cmpRes > 0){
                prev = tmp;
                tmp = tmp.getLeft();
            }
            else if (cmpRes < 0){
                prev = tmp;
                tmp = tmp.getRight();
            }
            else {
                return false; // key already present
            }

        }

        if (prev == null){
            root = newNode;
        }
        else {
            newNode.setParent(prev);
            if (cmpRes > 0){
                prev.setLeft(newNode);
            }
            else {
                prev.setRight(newNode);
            }
        }

        return true;
    }

    public void inorderTraverse(Function<E, Void> f){
        doInorderTraverse(root, f);
    }

    private void doInorderTraverse(Node<E> node, Function<E, Void> f) {
        if (node != null){
            doInorderTraverse(node.getLeft(), f);
            f.apply(node.key);
            doInorderTraverse(node.getRight(), f);
        }
    }

    public void preOrderTraverse(Function<E, Void> f){
        doPreorderTraverse(root, f);
    }

    private void doPreorderTraverse(Node<E> node, Function<E, Void> f) {
        if (node != null){
            f.apply(node.key);
            doPreorderTraverse(node.getLeft(), f);
            doPreorderTraverse(node.getRight(), f);
        }
    }

    public void postOrderTraverse(Function<E, Void> f){
        doPostorderTraverse(root, f);
    }

    private void doPostorderTraverse(Node<E> node, Function<E, Void> f) {
        if (node != null){
            doPostorderTraverse(node.getLeft(), f);
            doPostorderTraverse(node.getRight(), f);
            f.apply(node.key);
        }
    }

    private Node<E> subtreeMax(Node<E> subtreeRoot){
        if (subtreeRoot == null){
            return null;
        }
        else {
            while(subtreeRoot.getRight() != null){
                subtreeRoot = subtreeRoot.getRight();
            }

            return subtreeRoot;
        }
    }

    private Node<E> subtreeMin(Node<E> subtreeRoot){
        if (subtreeRoot == null){
            return null;
        }
        else {
            while(subtreeRoot.getLeft() != null){
                subtreeRoot = subtreeRoot.getLeft();
            }

            return subtreeRoot;
        }
    }

    private static boolean isNull(Node<?> aNode){
        return aNode == null;
    }

    public static void main(String[] args){
        // Shows how to use BSTs
        Function<Integer, Void> printKey = key -> {
            System.out.println(key + " ");
            return null;
        };

        BSTree<Integer> tree1 = new BSTree<>();
        int[] numbersToInsert1 = new int[] {20, 50, 40, 45, -10, -20, 0};
        for(Integer number : numbersToInsert1) {
            tree1.insert(number);
        }

        System.out.println("--- CREATED TREE ---");
        tree1.inorderTraverse(printKey);
        System.out.println("\n--- ---");

        tree1.delete(50);
        System.out.println("--- DELETED 50 ---");
        tree1.inorderTraverse(printKey);
        System.out.println("\n--- ---");

        tree1.delete(20);
        System.out.println("--- DELETED 20 ---");
        tree1.inorderTraverse(printKey);
        System.out.println("\n--- ---");

        tree1.delete(-20);
        System.out.println("--- DELETED -20 ---");
        tree1.inorderTraverse(printKey);
        System.out.println("\n--- ---");

        if (tree1.delete(20)){
            System.out.println("ERROR: SHOULD NOT DELETE!");
        }
        else {
            System.out.println("INFO: DID NOT DELETE 20, SINCE IT IS NOT IN THE TREE.");
        }

        BSTree<Integer> withDecreasingComparator = new BSTree<>((a, b) -> -Integer.compare(a,b));
        for(Integer number : numbersToInsert1){
            withDecreasingComparator.insert(number);
        }

        System.out.println("--- WITH DECREASING COMPARATOR ---");
        withDecreasingComparator.inorderTraverse(printKey);
        System.out.println("\n--- ---");

        // Illegal use of BST.
        // We are creating BST of a Type that we do not know how to compare.
        try{
            class Incomparable {
                int a;
            }

            BSTree<Incomparable> anIncomparableElementTree = new BSTree<Incomparable>();
            Incomparable element = new Incomparable();
            element.a = 1;
            anIncomparableElementTree.insert(element);
            Incomparable element2 = new Incomparable();
            element2.a = 2;
            anIncomparableElementTree.insert(element2);
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

    }
}
