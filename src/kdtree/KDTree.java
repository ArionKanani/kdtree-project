package kdtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class KDTree {
    private KDNode root;
    private final int k;

    public KDTree(int k) {
        this.k = k;
        this.root = null;
    }

    public void build(List<double[]> points) {
        List<double[]> copy = new ArrayList<>(points);
        this.root = buildRecursive(copy, 0);
    }

    private KDNode buildRecursive(List<double[]> points, int depth) {
        if (points.isEmpty()) {
            return null;
        }

        int splitDim = depth % k;
        points.sort(Comparator.comparingDouble(p -> p[splitDim]));

        int medianIndex = points.size() / 2;
        double[] medianPoint = points.get(medianIndex);

        KDNode node = new KDNode(medianPoint, splitDim);

        List<double[]> leftPoints = new ArrayList<>(points.subList(0, medianIndex));
        List<double[]> rightPoints = new ArrayList<>(points.subList(medianIndex + 1, points.size()));

        node.left = buildRecursive(leftPoints, depth + 1);
        node.right = buildRecursive(rightPoints, depth + 1);

        return node;
    }

    public void insert(double[] point) {
        if (point.length != k) {
            throw new IllegalArgumentException("Point must have " + k + " dimensions");
        }
        this.root = insertRecursive(this.root, point, 0);
    }

    private KDNode insertRecursive(KDNode node, double[] point, int depth) {
        if (node == null) {
            return new KDNode(point, depth % k);
        }

        int splitDim = node.splitDim;

        if (point[splitDim] < node.point[splitDim]) {
            node.left = insertRecursive(node.left, point, depth + 1);
        } else {
            node.right = insertRecursive(node.right, point, depth + 1);
        }

        return node;
    }

    public KDNode getRoot() {
        return root;
    }

    public void setRoot(KDNode node) {
        this.root = node;
    }

    public int getK() {
        return k;
    }

    public void print() {
        printRecursive(root, 0);
    }

    private void printRecursive(KDNode node, int depth) {
        if (node == null) return;
        printRecursive(node.right, depth + 1);
        for (int i = 0; i < depth; i++) System.out.print("    ");
        System.out.println(Arrays.toString(node.point) + " (split dim=" + node.splitDim + ")");
        printRecursive(node.left, depth + 1);
    }
}