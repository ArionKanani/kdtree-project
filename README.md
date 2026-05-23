# K-D Tree Project

A K-D Tree implementation in Java built as a group project.

## Group Members
- Kristi Saliasi — Structure & Construction (KDNode.java, KDTree.java)
- Juri Gjana — Search Operations (KDTreeSearcher.java)
- Arion Kanani — Deletion, Analysis & Demo (KDTreeDeleter.java, KDTreeDemo.java)

## How to Run
```bash
javac -d bin src/kdtree/*.java
java -cp bin kdtree.KDTreeDemo
```

## Features
- Build a K-D tree from a set of 2D points
- Insert and delete points
- Exact point search
- K-nearest neighbor search with bounding-box pruning
- Interactive GUI demo
- Brute force vs K-D tree performance comparison
