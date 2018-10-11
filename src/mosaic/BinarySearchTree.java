package mosaic;

import java.util.Stack;

/**
 * Created by Clanner on 2018/10/11.
 */
public class BinarySearchTree<T extends Comparable<T>> {

    //节点数量
    private int nodeCount;
    //根节点
    private Node root;

    private class Node {
        T data;
        Node left, right;

        public Node(T data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }
    }

    public int size() {
        return nodeCount;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public T getClose(T elem) {
        if (root == null) return null;
        return getClose(root, elem);
    }

    private T getClose(Node node, T elem) {
        Stack<Node> stack = new Stack<Node>();
        stack.push(node);
        Node last = null;
        while (!stack.isEmpty()) {
            Node p = stack.pop();
            last = p;
            int cmp = elem.compareTo(p.data);
            if (cmp == 1) {
                if (p.right != null) stack.push(p.right);
            } else if (cmp == -1) {
                if (p.left != null) stack.push(p.left);
            } else {
                return p.data;
            }
        }
        return last.data;
    }

    //获取元素
    public T get(T elem) {
        if (root == null) return null;
        return get(root, elem);
    }

    private T get(Node node, T elem) {
        if (node != null) {
            int tmp = node.data.compareTo(elem);
            if (tmp > 0) {
                return get(node.right, elem);
            } else if (tmp < 0) {
                return get(node.left, elem);
            } else {
                return node.data;
            }
        }
        return null;
    }

    //添加元素
    public boolean add(T elem) {
        if (contains(elem)) {
            return false;
        } else {
            root = add(root, elem);
            nodeCount++;
            return true;
        }
    }

    private Node add(Node node, T elem) {
        if (node == null) {
            node = new Node(elem, null, null);
        } else {
            if (elem.compareTo(node.data) < 0) {
                node.left = add(node.left, elem);
            } else {
                node.right = add(node.right, elem);
            }
        }
        return node;
    }

    public boolean remove(T elem) {
        if (contains(elem)) {
            root = remove(root, elem);
            nodeCount--;
            return true;
        }
        return false;
    }

    private Node remove(Node node, T elem) {
        if (node == null) return null;
        int cmp = elem.compareTo(node.data);
        if (cmp < 0) {
            node.left = remove(node.left, elem);
        } else if (cmp > 0) {
            node.right = remove(node.right, elem);
        } else {
            //要删除的节点有右子树或没有子树时，清空节点，删除节点的父节点指向删除节点的右子树
            if (node.left == null) {
                Node rightChild = node.right;
                node.data = null;
                node = null;
                return rightChild;
                //要删除的节点有左子树或没有子树时，清空节点，删除节点的父节点指向删除节点的左子树
            } else if (node.right == null) {
                Node leftChild = node.left;
                node.data = null;
                node = null;
                return leftChild;
                //要删除的节点有右子树和左子树，
            } else {
                Node tmp = findMin(node.right);
                node.data = tmp.data;
                node.right = remove(root.right, tmp.data);
            }
        }
        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    private Node findMax(Node node) {
        while (node.right != null)
            node = node.right;
        return node;
    }

    public boolean contains(T elem) {
        return contains(root, elem);
    }

    private boolean contains(Node node, T elem) {
        if (node == null) return false;
        int cmp = elem.compareTo(node.data);
        if (cmp < 0) {
            return contains(node.left, elem);
        } else if (cmp > 0) {
            return contains(node.right, elem);
        } else {
            return true;
        }
    }

    //返回树的高度
    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == null) return 0;
        return Math.max(height(node.left), height(node.right)) + 1;
    }
}
