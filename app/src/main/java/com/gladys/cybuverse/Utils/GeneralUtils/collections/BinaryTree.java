package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import java.util.ArrayList;
import java.util.List;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.format;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.print;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toDouble;

public class BinaryTree<T> {

    private Node<T> HEAD;

    private List<Node<T>> nodeList = new ArrayList<>();

    private Comparator<T> comparator;

    public BinaryTree(Comparator<T> comparator) {
        setComparator(comparator);
    }

    public Node<T> getHead() {
        return HEAD;
    }

    private void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void add(T value) {

        if (HEAD == null) {
            HEAD = new Node<>(value);
        } else {
            Node<T> current_node = HEAD;

            while (true) {
                if (comparator.item1_equals_item2(value, current_node.value)) {
                    current_node.count += 1;
                    break;
                } else if (comparator.item1_lesserthan_item2(value, current_node.value)) {
                    if (current_node.left == null) {
                        current_node.left = new Node<>(value);
                        break;
                    } else {
                        current_node = current_node.left;
                    }
                } else if (comparator.item1_greaterthan_item2(value, current_node.value)) {
                    if (current_node.right == null) {
                        current_node.right = new Node<>(value);
                        break;
                    } else {
                        current_node = current_node.right;
                    }
                }
            }

        }

    }

    public void addAll(T... values) {
        for (T i : values) {
            add(i);
        }
    }

    public boolean contains(T value) {
        return get(value) != null;
    }

    public Node<T> get(T value) {
        Node<T> current_node = HEAD;

        while (true) {
            if (current_node == null) {
                return null;
            } else if (comparator.item1_equals_item2(value, current_node.value)) {
                return current_node;
            } else if (comparator.item1_lesserthan_item2(value, current_node.value)) {
                current_node = current_node.left;
            } else if (comparator.item1_greaterthan_item2(value, current_node.value)) {
                current_node = current_node.right;
            }
        }

    }

    private Node<T> getParentNodeFor(T value) {

        Node<T> current_node = HEAD;

        if (current_node != null && current_node.value == value) {
            return current_node;
        }

        while (true) {
            if (current_node.left != null && comparator.item1_equals_item2(value, current_node.left.value)) {
                return current_node;
            } else if (current_node.right != null && comparator.item1_equals_item2(value, current_node.right.value)) {
                return current_node;
            } else if (comparator.item1_lesserthan_item2(value, current_node.value)) {
                current_node = current_node.left;
            } else if (comparator.item1_lesserthan_item2(value, current_node.value)) {
                current_node = current_node.right;
            }
        }

    }

    private Node<T> getRightmostNodeFor(Node<T> current_node) {
        while (current_node.right != null) {
            current_node = current_node.right;
        }
        return current_node;
    }

    private boolean hasNoChildNodes(Node<T> node) {
        return node.left == null && node.right == null;
    }

    private boolean hasOneChildNodes(Node<T> node) {
        if (node.left == null && node.right != null) {
            return true;
        } else {
            return node.left != null && node.right == null;
        }
    }

    private boolean hasTwoChildNodes(Node<T> node) {
        return node.left != null && node.right != null;
    }

    public void delete(T value) {
        if (contains(value)) {

            Node<T> temp = get(value);
            Node<T> temp_parent = getParentNodeFor(temp.value);

            if (temp.count > 1) {
                temp.count -= 1;
            } else if (hasNoChildNodes(temp)) {
                if (temp_parent.right == temp) {
                    temp_parent.right = null;
                } else if (temp_parent.left == temp) {
                    temp_parent.left = null;
                } else {
                    HEAD = null;
                }
                //unlink node from parent
            } else if (hasOneChildNodes(temp)) {
                if (temp_parent.left == temp) {
                    temp_parent.left = (temp.left != null) ? temp.left : temp.right;
                } else if (temp_parent.right == temp) {
                    temp_parent.right = (temp.left != null) ? temp.left : temp.right;
                } else {
                    HEAD = (temp.left != null) ? temp.left : temp.right;
                }
                //point parent node of temp to temp.child
            } else {

                Node<T> rightmost_node = getRightmostNodeFor(temp.left);
                Node<T> rightmost_node_parent = getParentNodeFor(rightmost_node.value);

                if (rightmost_node_parent != temp) {
                    rightmost_node_parent.right = rightmost_node.left;
                    rightmost_node.left = temp.left;
                }

                rightmost_node.right = temp.right;

                if (temp == temp_parent.left) {
                    temp_parent.left = rightmost_node;
                } else if (temp == temp_parent.right) {
                    temp_parent.right = rightmost_node;
                } else {
                    HEAD = rightmost_node;
                }

                //get the rightmost (largest) node in the left branch and set it to temp
                // or the leftmost (smallest) node in the right branch and set it to temp
            }
        }
    }

    public void deleteAll(T value) {
        while (contains(value)) {
            delete(value);
        }
    }

    public void replace(T value, T new_value) {
        if (contains(value)) {
            delete(value);
            add(new_value);
        }
    }

    public void replaceAll(T value, T new_value) {
        if (contains(value)) {
            int count = get(value).count;
            deleteAll(value);
            add(new_value);
            get(value).count = count;
        }
    }

    public int size() {
        return toListInOrder().size();
    }

    public boolean isEmpty() {
        return HEAD == null;
    }

    public void clear() {
        HEAD = null;
    }

    public Node[] toArray() {
        nodeList.clear();
        buildArray(HEAD);
        Node<T>[] n = new Node[nodeList.size()];
        n = nodeList.toArray(n);
        return n;
    }

    public List<Node<T>> toListInOrder() {
        nodeList.clear();
        buildArray(HEAD);
        return nodeList;
    }

    private void buildArray(Node<T> current_node) {
        if (current_node == null) {
            return;
        }
        buildArray(current_node.left);
        for (int i = 0; i < current_node.count; i++) {
            nodeList.add(current_node);
        }
        buildArray(current_node.right);
    }

    public void printInorder() {
        inOrder(HEAD);
    }

    public void printPreorder() {
        preOrder(HEAD);
    }

    public void printPostorder() {
        postOrder(HEAD);
    }

    private void inOrder(Node<T> head) {
        if (head == null) {
            return;
        } else {
            inOrder(head.left);
            print(head);
            print();
            inOrder(head.right);
        }
    }

    private void preOrder(Node<T> head) {
        if (head == null) {
            return;
        } else {
            print(head);
            print();
            preOrder(head.left);
            preOrder(head.right);
        }
    }

    private void postOrder(Node<T> head) {
        if (head == null) {
            return;
        } else {
            postOrder(head.left);
            postOrder(head.right);
            print(head);
            print();
        }
    }

    public String toString() {
        return format("BinaryTree(Head : <>)", HEAD.toString());
    }

    public interface Comparator<T> {
        boolean item1_equals_item2(T item1, T item2);

        boolean item1_greaterthan_item2(T item1, T item2);

        boolean item1_lesserthan_item2(T item1, T item2);
    }

    public static class Node<T> {

        public T value;
        public int count;
        public Node<T> left;
        public Node<T> right;

        Node(T value) {
            this.value = value;
            this.count = 1;
        }

        public String toString() {
            return format("Node(value:<>, count:<>)", value, count);
        }

        public Node clone() {
            Node<T> n = new Node<>(value);
            n.left = left;
            n.right = right;
            return n;
        }
    }

    public static class DefaultComparator {

        public static final Comparator NumberComparator = new Comparator() {
            @Override
            public boolean item1_equals_item2(Object item1, Object item2) {
                return toDouble(item1).equals(toDouble(item2));
            }

            @Override
            public boolean item1_greaterthan_item2(Object item1, Object item2) {
                return toDouble(item1) > toDouble(item2);
            }

            @Override
            public boolean item1_lesserthan_item2(Object item1, Object item2) {
                return toDouble(item1) < toDouble(item2);
            }
        };
        public static final Comparator<String> StringComparator = new Comparator<String>() {
            @Override
            public boolean item1_equals_item2(String item1, String item2) {
                return item1.equals(item2);
            }

            @Override
            public boolean item1_greaterthan_item2(String item1, String item2) {
                return item1.compareTo(item2) > 0;
            }

            @Override
            public boolean item1_lesserthan_item2(String item1, String item2) {
                return item1.compareTo(item2) < 0;
            }
        };
        public static final Comparator<Character> CharacterComparator = new Comparator<Character>() {
            @Override
            public boolean item1_equals_item2(Character item1, Character item2) {
                return item1 == item2;
            }

            @Override
            public boolean item1_greaterthan_item2(Character item1, Character item2) {
                return item1.toString().compareTo(item2.toString()) > 0;
            }

            @Override
            public boolean item1_lesserthan_item2(Character item1, Character item2) {
                return item1.toString().compareTo(item2.toString()) > 0;
            }
        };

        private DefaultComparator() {
        }
    }

}
    