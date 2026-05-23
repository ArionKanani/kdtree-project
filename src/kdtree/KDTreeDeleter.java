package kdtree;

import java.util.Arrays;

public class KDTreeDeleter {

    private final int k;

    public KDTreeDeleter(int k) {
        this.k = k;
    }

    public void delete(KDTree tree, double[] point) {
        KDNode newRoot = deleteRecursive(tree.getRoot(), point, 0);
        tree.setRoot(newRoot);
    }

    private KDNode deleteRecursive(KDNode node, double[] target, int depth) {
        if (node == null) return null;

        int splitDim = depth % k;

        if (Arrays.equals(node.point, target)) {
            if (node.left == null && node.right == null) {
                return null;
            } else if (node.right != null) {
                double[] minPoint = findMin(node.right, splitDim, depth + 1);
                node.point = minPoint;
                node.right = deleteRecursive(node.right, minPoint, depth + 1);
            } else {
                double[] minPoint = findMin(node.left, splitDim, depth + 1);
                node.point = minPoint;
                node.right = deleteRecursive(node.left, minPoint, depth + 1);
                node.left = null;
            }
        } else {
            if (target[splitDim] < node.point[splitDim]) {
                node.left = deleteRecursive(node.left, target, depth + 1);
            } else {
                node.right = deleteRecursive(node.right, target, depth + 1);
            }
        }

        return node;
    }

    private double[] findMin(KDNode node, int searchDim, int depth) {
        if (node == null) return null;

        int splitDim = depth % k;

        if (splitDim == searchDim) {
            double[] leftMin = findMin(node.left, searchDim, depth + 1);
            return minOnDim(node.point, leftMin, searchDim);
        } else {
            double[] leftMin  = findMin(node.left,  searchDim, depth + 1);
            double[] rightMin = findMin(node.right, searchDim, depth + 1);
            return minOnDim(node.point, minOnDim(leftMin, rightMin, searchDim), searchDim);
        }
    }

    private double[] minOnDim(double[] a, double[] b, int dim) {
        if (a == null) return b;
        if (b == null) return a;
        return a[dim] <= b[dim] ? a : b;
    }
}