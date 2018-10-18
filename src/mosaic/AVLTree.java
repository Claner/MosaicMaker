package mosaic;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Clanner on 2018/10/18.
 * AVL树
 */
public class AVLTree<V> {

    private class Node {
        String key;
        int bf;
        V value;
        int height;
        Node left, right;

        public Node(String key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    //根节点
    private Node root;
    //节点总数
    private int nodeCount = 0;
    //空对象
    private Node TOKEN = new Node(null, null);

    public int height() {
        if (root == null) return 0;
        return root.height;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return nodeCount;
    }

    public boolean contains(String key) {
        Node node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean insert(String key, V value) {
//        synchronized (this) {
            if (value == null) return false;
            Node newRoot = insert(root, key, value);
            boolean insertedNode = (newRoot != TOKEN);
            if (insertedNode) {
                nodeCount++;
                root = newRoot;
            }
            return insertedNode;
//        }
    }

    private Node insert(Node node, String key, V value) {
        if (node == null) return new Node(key, value);
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            Node newRightNode = insert(node.right, key, value);
            if (newRightNode == TOKEN) return TOKEN;
            node.right = newRightNode;
        } else if (cmp < 0) {
            Node newLeftNode = insert(node.left, key, value);
            if (newLeftNode == TOKEN) return TOKEN;
            node.left = newLeftNode;
        } else {
            //表示已插入
            return TOKEN;
        }
        update(node);
        return balance(node);
    }

    public V getCloseByGray(String key) {
        if (root == null || key == null) return null;
        Node node = root;
        List<Node> list = new LinkedList<Node>();
        while (node != null) {
            list.add(node);
            int cmp = key.compareTo(node.key);
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                return node.value;
            }
        }
        float min = 900.f;
        float targetKey = Float.parseFloat(key);
        int indexK = 0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            float curDif = Math.abs(targetKey - Float.parseFloat(list.get(i).key));
            if (min > curDif) {
                min = curDif;
                indexK = i;
            }
        }
        return list.get(indexK).value;
    }

    public V getCloseByRGB(String key) {
        if (root == null || key == null) return null;
        Node node = root;
        List<Node> list = new LinkedList<Node>();
        while (node != null) {
            list.add(node);
            int cmp = key.compareTo(node.key);
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                return node.value;
            }
        }
        float min = 900.f;
        String[] rgb = key.split("-");
        float r = Float.parseFloat(rgb[0]);
        float g = Float.parseFloat(rgb[1]);
        float b = Float.parseFloat(rgb[2]);
        int indexK = 0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String[] mrgb = list.get(i).key.split("-");
            float mr = Float.parseFloat(mrgb[0]);
            float mg = Float.parseFloat(mrgb[1]);
            float mb = Float.parseFloat(mrgb[2]);
            float curDif = Math.abs(r - mr) + Math.abs(g - mg) + Math.abs(b - mb);
            if (min > curDif) {
                min = curDif;
                indexK = i;
            }
        }
        return list.get(indexK).value;
    }

    public V get(String key) {
        if (root == null || key == null) return null;
        return get(root, key);
    }

    private V get(Node node, String key) {
        if (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp > 0) {
                return get(node.right, key);
            } else if (cmp < 0) {
                return get(node.left, key);
            } else {
                return node.value;
            }
        } else {
            return null;
        }
    }

    private void update(Node node) {
        int leftNodeHeight = node.left == null ? -1 : node.left.height;
        int rightNodeHeight = node.right == null ? -1 : node.right.height;
        node.height = 1 + Math.max(leftNodeHeight, rightNodeHeight);
        node.bf = rightNodeHeight - leftNodeHeight;
    }

    private Node balance(Node node) {
        if (node.bf == 2) {
            if (node.right.bf >= 0) {
                return rightRightCase(node);
            } else {
                return rightLeftCase(node);
            }
        } else if (node.bf == -2) {
            if (node.left.bf <= 0) {
                return leftLeftCase(node);
            } else {
                return leftRightCase(node);
            }
        } else {
            return node;
        }
    }

    private Node rightRightCase(Node node) {
        return leftRotation(node);
    }

    private Node rightLeftCase(Node node) {
        node.right = rightRotation(node.right);
        return rightRightCase(node);
    }

    private Node leftLeftCase(Node node) {
        return rightRotation(node);
    }

    private Node leftRightCase(Node node) {
        node.left = leftRotation(node.left);
        return leftLeftCase(node);
    }

    //右旋
    private Node rightRotation(Node node) {
        Node newParent = node.left;
        node.left = newParent.right;
        newParent.right = node;
        update(node);
        update(newParent);
        return newParent;
    }

    //左旋
    private Node leftRotation(Node node) {
        Node newParent = node.right;
        node.right = newParent.left;
        newParent.left = node;
        update(node);
        update(newParent);
        return newParent;
    }

    public boolean remove(String key) {
        Node newRoot = remove(root, key);
        boolean removedNode = (newRoot != TOKEN) || (newRoot == null);
        if (removedNode) {
            root = newRoot;
            nodeCount--;
            return true;
        }
        return false;
    }

    private Node remove(Node node, String key) {
        if (node == null) return TOKEN;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            Node newLeftNode = remove(node.left, key);
            if (newLeftNode == TOKEN) return TOKEN;
            node.left = newLeftNode;
        } else if (cmp > 0) {
            Node newRightNode = remove(node.right, key);
            if (newRightNode == TOKEN) return TOKEN;
            node.right = newRightNode;
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                if (node.left.height > node.right.height) {
                    String successorKey = findMax(node.left);
                    node.key = successorKey;
                    Node replacement = remove(node.left, successorKey);
                    if (replacement == TOKEN) return TOKEN;
                    node.left = replacement;
                } else {
                    String successorKey = findMin(node.right);
                    node.key = successorKey;
                    Node replacement = remove(node.right, successorKey);
                    if (replacement == TOKEN) return TOKEN;
                    node.right = replacement;
                }
            }
        }
        update(node);
        return balance(node);
    }

    private String findMin(Node node) {
        while (node.left != null)
            node = node.left;
        return node.key;
    }

    private String findMax(Node node) {
        while (node.right != null)
            node = node.right;
        return node.key;
    }

}
