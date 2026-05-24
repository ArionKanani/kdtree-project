package kdtree;

import java.util.PriorityQueue;

public class KDTreeSearcher {

    public static class Neighbor implements Comparable<Neighbor> {
        public KDNode node;
        public double distance;

        public Neighbor(KDNode node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(Neighbor o) {
            return Double.compare(o.distance, this.distance);
        }
    }

    public boolean search(KDNode current, double[] target, int depth) {
        if (current == null) return false;
        if (java.util.Arrays.equals(current.point, target)) return true;

        int axis = depth % target.length;
        if (target[axis] < current.point[axis]) {
            return search(current.left, target, depth + 1);
        } else {
            return search(current.right, target, depth + 1);
        }
    }

    public PriorityQueue<Neighbor> findKNearestNeighbors(KDNode root, double[] target, int kNeighbors) {
        PriorityQueue<Neighbor> maxHeap = new PriorityQueue<>(kNeighbors);
        knnSearch(root, target, 0, kNeighbors, maxHeap);
        return maxHeap;
    }

    private void knnSearch(KDNode current, double[] target, int depth, int kNeighbors, PriorityQueue<Neighbor> maxHeap) {
        if (current == null) return;

        int axis = depth % target.length;
        double dSquared = squaredEuclideanDistance(current.point, target);

        if (maxHeap.size() < kNeighbors) {
            maxHeap.add(new Neighbor(current, dSquared));
        } else if (dSquared < maxHeap.peek().distance) {
            maxHeap.poll();
            maxHeap.add(new Neighbor(current, dSquared));
        }

        KDNode nextNode = (target[axis] < current.point[axis]) ? current.left : current.right;
        KDNode otherNode = (target[axis] < current.point[axis]) ? current.right : current.left;

        knnSearch(nextNode, target, depth + 1, kNeighbors, maxHeap);

        double planeDist = target[axis] - current.point[axis];
        double planeDistSquared = planeDist * planeDist;

        if (maxHeap.size() < kNeighbors || planeDistSquared < maxHeap.peek().distance) {
            knnSearch(otherNode, target, depth + 1, kNeighbors, maxHeap);
        }
    }

    private double squaredEuclideanDistance(double[] p1, double[] p2) {
        double sum = 0;
        for (int i = 0; i < p1.length; i++) {
            double diff = p1[i] - p2[i];
            sum += diff * diff;
        }
        return sum;
    }
}