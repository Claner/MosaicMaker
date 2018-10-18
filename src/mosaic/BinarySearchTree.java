package mosaic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clanner on 2018/10/13.
 */
public class BinarySearchTree<V> {

    //节点数量
    private int nodeCount;
    //根节点
    private Node root;

    private class Node {
        Float key;
        V value;
        Node left, right;

        public Node(Float key, V value, Node left, Node right) {
            this.key = key;
            this.value = value;
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

    public V getClose(Float key) {
        if (root == null || key == null) return null;
        Node p = root;
        List<Node> list = new ArrayList<>();
        while (p != null) {
            int cmp = key.compareTo(p.key);
            if (cmp > 0) {
                list.add(p);
                p = p.right;
            } else if (cmp < 0) {
                list.add(p);
                p = p.left;
            } else {
                return p.value;
            }
        }
        float min = 300.f;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            float curDif = Math.abs(list.get(i).key - key);
            if (curDif < min) {
                min = curDif;
                index = i;
            }
        }
        return list.get(index).value;
    }

    //获取元素
    public V get(Float key) {
        if (root == null || key == null) return null;
        return get(root, key);
    }

    private V get(Node node, Float key) {
        if (node != null) {
            int tmp = key.compareTo(node.key);
            if (tmp > 0) {
                return get(node.right, key);
            } else if (tmp < 0) {
                return get(node.left, key);
            } else {
                return node.value;
            }
        }
        return null;
    }

    //添加元素
    public boolean add(Float key, V value) {
        if (contains(key)) {
            return false;
        } else {
            root = add(root, key, value);
            nodeCount++;
            return true;
        }
    }

    private Node add(Node node, Float key, V value) {
        if (node == null) {
            node = new Node(key, value, null, null);
        } else {
            if (key.compareTo(node.key) < 0) {
                node.left = add(node.left, key, value);
            } else if (key.compareTo(node.key) > 0) {
                node.right = add(node.right, key, value);
            } else {

            }
        }
        return node;
    }

    public boolean contains(Float key) {
        return contains(root, key);
    }

    private boolean contains(Node node, Float key) {
        if (node == null) return false;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return contains(node.left, key);
        } else if (cmp > 0) {
            return contains(node.right, key);
        } else {
            return true;
        }
    }

    public boolean remove(Float key) {
        if (contains(key)) {
            root = remove(root, key);
            nodeCount--;
            return true;
        }
        return false;
    }

    private Node remove(Node node, Float key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            //要删除的节点有右子树或没有子树时，清空节点，删除节点的父节点指向删除节点的右子树
            if (node.left == null) {
                Node rightChild = node.right;
                node.value = null;
                node = null;
                return rightChild;
                //要删除的节点有左子树或没有子树时，清空节点，删除节点的父节点指向删除节点的左子树
            } else if (node.right == null) {
                Node leftChild = node.left;
                node.value = null;
                node = null;
                return leftChild;
                //要删除的节点有右子树和左子树，
            } else {
                Node tmp = findMin(node.right);
                node.value = tmp.value;
                node.right = remove(root.right, tmp.key);
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

    //返回树的高度
    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == null) return 0;
        return Math.max(height(node.left), height(node.right)) + 1;
    }
}
