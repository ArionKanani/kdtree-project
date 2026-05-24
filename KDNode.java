package kdtree;

public class KDNode {
    double[] point;
    int splitDim;
    KDNode left;
    KDNode right;

    public KDNode(double[] point, int splitDim) {
        this.point = point;
        this.splitDim = splitDim;
        this.left = null;
        this.right = null;
    }
}