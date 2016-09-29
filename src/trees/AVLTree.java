package trees;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static trees.AVLTree.AVLNode.balanceOK;
import static trees.AVLTree.AVLNode.height;

/* AVLTree is a special kind of a BST */
public class AVLTree<E> extends BSTree<E> {
    @Override
    public AVLNode<E> getRoot(){
        return (AVLNode<E>) super.getRoot();
    }

    public AVLTree(){
        super();
    }

    public AVLTree(Comparator<E> cmp){
        super(cmp);
    }

    @Override
    protected void LeftRotate(Node<E> start) {
        if (start instanceof AVLNode){
            super.LeftRotate(start);

            ((AVLNode<E>) start).resetHeight();
            ((AVLNode<E>) start.getParent()).resetHeight();

        }
        else{
            throw new IllegalArgumentException("This method can only be called on AVLNode<E>");
        }
    }

    @Override
    protected void RightRotate(Node<E> start) {
        if (start instanceof AVLNode){
            super.RightRotate(start);

            ((AVLNode<E>) start).resetHeight();
            ((AVLNode<E>) start.getParent()).resetHeight();

        }
        else{
            throw new IllegalArgumentException("This method can only be called on AVLNode<E>");
        }
    }

    @Override
    public boolean insert(E key){
        AVLNode<E> newNode = new AVLNode<E>(key);
        boolean success = doInsert(newNode);
        if (success){
            AVLNode<E> imbalancedNode = newNode;
            AVLNode<E> child = null;
            AVLNode<E> grandchild = null;

            while(imbalancedNode != null){
                imbalancedNode.resetHeight();

                if (balanceOK(imbalancedNode.getBalance())){
                    grandchild = child;
                    child = imbalancedNode;
                    imbalancedNode = imbalancedNode.getParent();
                }
                else {
                    balance(imbalancedNode, child, grandchild);

                    // since we fixed the tree imbalance, we can break the loop
                    imbalancedNode = null; // there are no more imbalanced nodes
                }
            }
        }
        return success;
    }

    private void balance(AVLNode<E> imbalancedNode, AVLNode<E> child, AVLNode<E> grandchild) {
        if (child.isLeftChildOf(imbalancedNode)){
            if (grandchild.isLeftChildOf(child)){
                RightRotate(imbalancedNode); // LL case
            }
            else { // LR case
                LeftRotate(child);
                RightRotate(imbalancedNode);
            }
        }
        else {
            if (grandchild.isRightChildOf(child)){
                LeftRotate(imbalancedNode); //RR case
            }
            else { // RL case
                RightRotate(child);
                LeftRotate(imbalancedNode);
            }
        }
    }

    @Override
    public boolean delete(E key){
        Node<E> nodeToDelete = find(key);
        if (nodeToDelete == null){
            return false; // node not found
        }
        else {
            AVLNode<E> startNode = (AVLNode<E>) doDelete(nodeToDelete);
            while(startNode != null){
                startNode.resetHeight();

                if (!balanceOK(startNode.getBalance())){
                    AVLNode<E> child, grandchild;
                    if (height(startNode.getLeft()) > height(startNode.getRight())){
                        child = startNode.getLeft();
                    }
                    else {
                        child = startNode.getRight();
                    }

                    if (height(child.getLeft()) > height(child.getRight())){
                        grandchild = child.getLeft();
                    }
                    else {
                        grandchild = child.getRight();
                    }

                    balance(startNode, child, grandchild);
                }
                startNode = startNode.getParent();
            }
            return true;
        }
    }

    protected final static class AVLNode<E> extends BSTree.Node<E>{
        private int height;

        public static int height(AVLNode<?> node){
            return node == null ? 0 : node.getHeight();
        }

        public static boolean balanceOK(int balance){
            return Math.abs(balance) <= 1;
        }

        public int getBalance(){
            return height(getLeft()) - height(getRight());
        }

        public int getHeight(){
            return height;
        }

        public void setHeight(int newHeight){
            height = newHeight;
        }

        public AVLNode(E key, AVLNode<E> l, AVLNode<E> r, AVLNode<E> p){
            super(key, l, r, p);
        }

        public AVLNode(E key, AVLNode<E> l, AVLNode<E> r){
            this(key, l, r, null);
        }

        public AVLNode(E key){
            this(key, null, null, null);
        }

        @Override
        public AVLNode<E> getLeft(){
            return (AVLNode<E>) super.getLeft();
        }

        @Override
        public void setLeft(Node<E> left){
            if (left == null || left instanceof AVLNode) {
                super.setLeft(left);
            }
            else {
                throw new IllegalArgumentException("All nodes in AVLTree have to be AVLNodes");
            }
        }

        @Override
        public AVLNode<E> getRight(){
            return (AVLNode<E>) super.getRight();
        }

        @Override
        public void setRight(Node<E> right){
            if (right == null || right instanceof AVLNode) {
                super.setRight(right);
            }
            else {
                throw new IllegalArgumentException("All nodes in AVLTree have to be AVLNodes");
            }
        }

        @Override
        public AVLNode<E> getParent(){
            return (AVLNode<E>) super.getParent();
        }

        @Override
        public void setParent(Node<E> parent){
            if (parent == null || parent instanceof AVLNode) {
                super.setParent(parent);
            }
            else {
                throw new IllegalArgumentException("All nodes in AVLTree have to be AVLNodes");
            }
        }


        public void resetHeight() {
            setHeight(Math.max(height(getLeft()), height(getRight())) + 1);
        }

        public static void main(String[] args){
            /* A simple comparison between Java SDK RedBlack trees
               and our implementation of AVL trees.
             */
            long start = System.nanoTime();
            AVLTree<Integer> avlTree = new AVLTree<>();

            int i = 1, j = 10000000;
            IntStream.range(i, j)
                     .forEach(avlTree::insert);
            long end = System.nanoTime();

            long avlInsert = end - start;

            Void v = null;
            start = System.nanoTime();
            TreeSet<Integer> javaMap = new TreeSet<>();
            IntStream.range(i, j)
                    .forEach(javaMap::add);
            end = System.nanoTime();

            long javaMapInsert = end-start;

            System.out.println("AVL: " + (avlInsert / 1e9)  + ", JavaTreeMap: " + (javaMapInsert / 1e9));

            int[] randomInts = new Random().ints(1000000)
                                           .toArray();

            avlTree = new AVLTree<>();
            javaMap.clear();

            start = System.nanoTime();
            for (int num : randomInts) {
                avlTree.insert(num);
            }
            end = System.nanoTime();
            avlInsert = end - start;

            start = System.nanoTime();
            for (int num : randomInts) {
                javaMap.add(num);
            }
            end = System.nanoTime();
            javaMapInsert = end - start;

            System.out.println("AVL: " + (avlInsert / 1e9)  + ", JavaTreeMap: " + (javaMapInsert / 1e9));
        }
    }
}
