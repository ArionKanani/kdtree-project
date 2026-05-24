package kdtree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class KDTreeDemo extends JFrame {

    static KDTree tree = new KDTree(2);
    static KDTreeDeleter deleter = new KDTreeDeleter(2);
    static KDTreeSearcher searcher = new KDTreeSearcher();
    static boolean built = false;

    JTextArea outputArea;
    JTextField inputX, inputY, inputK;

    public KDTreeDemo() {
        setTitle("KD Tree Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        bar.add(new JLabel("X:"));
        inputX = new JTextField("0", 4);
        bar.add(inputX);

        bar.add(new JLabel("Y:"));
        inputY = new JTextField("0", 4);
        bar.add(inputY);

        bar.add(new JLabel("k:"));
        inputK = new JTextField("1", 3);
        bar.add(inputK);

        String[][] btns = {
                {"Build",   "build"},
                {"Insert",  "insert"},
                {"Search",  "search"},
                {"k-NN",    "knn"},
                {"Delete",  "delete"},
                {"Print",   "print"},
                {"Compare", "compare"},
                {"Clear",   "clear"}
        };

        for (String[] b : btns) {
            JButton btn = new JButton(b[0]);
            btn.setActionCommand(b[1]);
            btn.addActionListener(this::handleAction);
            bar.add(btn);
        }

        return bar;
    }

    JPanel buildCenter() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setText(
                "Welcome! Enter X and Y values above, then click an action.\n\n" +
                        " Build       -> opens a dialog to enter multiple points\n" +
                        " Insert      -> adds the (X, Y) point to the existing tree\n" +
                        " Search      -> checks if (X, Y) is in the tree\n" +
                        " k-NN        -> finds k nearest neighbors to (X, Y)\n" +
                        " Delete      -> removes (X, Y) from the tree\n" +
                        " Print       -> shows the tree structure\n" +
                        " Compare     -> timing comparison between brute force and KD Tree (opens dialog)\n" +
                        " Clear       -> clears this output area\n"
        );

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(outputArea));
        return p;
    }

    void handleAction(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "build":   doBuild();   break;
            case "insert":  doInsert();  break;
            case "search":  doSearch();  break;
            case "knn":     doKNN();     break;
            case "delete":  doDelete();  break;
            case "print":   doPrint();   break;
            case "compare": doCompare(); break;
            case "clear":   outputArea.setText(""); break;
        }
    }

    void doBuild() {
        JTextArea textArea = new JTextArea(8, 20);
        textArea.setText("5 4\n3 1\n7 2\n2 3\n4 7\n9 6");
        JScrollPane scroll = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scroll, "Enter points one per line (x y)", JOptionPane.PLAIN_MESSAGE);
        String input = textArea.getText();
        if (input == null || input.trim().isEmpty()) return;

        java.util.List<double[]> points = new ArrayList<>();
        for (String line : input.trim().split("\\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;
            try { points.add(parseXY(line)); }
            catch (Exception ex) { print("Skipped: " + line); }
        }
        if (points.isEmpty()) { print("No valid points."); return; }

        tree = new KDTree(2);
        deleter = new KDTreeDeleter(2);
        tree.build(points);
        built = true;
        print("Tree built with " + points.size() + " points.");
    }

    void doInsert() {
        if (!requireBuilt()) return;
        double[] p = readXY();
        tree.insert(p);
        print("Inserted " + fmt(p));
    }

    void doSearch() {
        if (!requireBuilt()) return;
        double[] p = readXY();
        boolean found = searcher.search(tree.getRoot(), p, 0);
        print("Search " + fmt(p) + " -> " + (found ? "FOUND" : "NOT FOUND"));
    }

    void doKNN() {
        if (!requireBuilt()) return;
        double[] query = readXY();
        int k;
        try { k = Integer.parseInt(inputK.getText().trim()); }
        catch (Exception ex) { print("Invalid k."); return; }

        PriorityQueue<KDTreeSearcher.Neighbor> result =
                searcher.findKNearestNeighbors(tree.getRoot(), query, k);

        java.util.List<KDTreeSearcher.Neighbor> sorted = new ArrayList<>(result);
        sorted.sort(Comparator.comparingDouble(nb -> nb.distance));

        print(k + " nearest neighbors to " + fmt(query) + ":");
        for (int i = 0; i < sorted.size(); i++)
            print("  " + (i+1) + ". " + fmt(sorted.get(i).node.point) +
                    "  dist=" + String.format("%.2f", Math.sqrt(sorted.get(i).distance)));
    }

    void doDelete() {
        if (!requireBuilt()) return;
        double[] p = readXY();
        if (!searcher.search(tree.getRoot(), p, 0)) {
            print(fmt(p) + " not found.");
            return;
        }
        deleter.delete(tree, p);
        print("Deleted " + fmt(p));
    }

    void doPrint() {
        if (!requireBuilt()) return;
        print("Tree structure:");
        printNode(tree.getRoot(), 0, "");
    }

    void printNode(KDNode node, int depth, String prefix) {
        if (node == null) return;
        printNode(node.right, depth + 1, prefix + "    ");
        outputArea.append(prefix + fmt(node.point) + " [dim=" + node.splitDim + "]\n");
        printNode(node.left,  depth + 1, prefix + "    ");
    }

    void doCompare() {
        String ns = JOptionPane.showInputDialog(this, "How many random points?", "10000");
        if (ns == null) return;
        String qs = JOptionPane.showInputDialog(this, "How many queries?", "200");
        if (qs == null) return;

        int n, queries;
        try { n = Integer.parseInt(ns.trim()); queries = Integer.parseInt(qs.trim()); }
        catch (Exception ex) { print("Invalid number."); return; }

        Random rng = new Random();
        java.util.List<double[]> dataset = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
            dataset.add(new double[]{rng.nextDouble() * 100, rng.nextDouble() * 100});

        double[][] queryPoints = new double[queries][2];
        for (int q = 0; q < queries; q++) {
            queryPoints[q][0] = rng.nextDouble() * 100;
            queryPoints[q][1] = rng.nextDouble() * 100;
        }

        KDTree testTree = new KDTree(2);
        testTree.build(new ArrayList<>(dataset));
        KDTreeSearcher testSearcher = new KDTreeSearcher();
        double[][] dataArr = dataset.toArray(new double[0][]);

        for (int w = 0; w < 10; w++) {
            bruteForceNN(dataArr, queryPoints[0]);
            testSearcher.findKNearestNeighbors(testTree.getRoot(), queryPoints[0], 1);
        }

        long t0 = System.nanoTime();
        for (double[] q : queryPoints) bruteForceNN(dataArr, q);
        long bruteMs = (System.nanoTime() - t0) / 1_000_000;

        t0 = System.nanoTime();
        for (double[] q : queryPoints)
            testSearcher.findKNearestNeighbors(testTree.getRoot(), q, 1);
        long kdMs = (System.nanoTime() - t0) / 1_000_000;

        double speedup = (kdMs == 0) ? bruteMs : (double) bruteMs / kdMs;

        print("Brute force vs K-D Tree (" + n + " points, " + queries + " queries):");
        print("  Brute   : " + bruteMs + " ms");
        print("  KD Tree : " + kdMs + " ms");
        print("  Speedup : " + String.format("%.1f", speedup) + "x");
    }

    static double[] bruteForceNN(double[][] points, double[] query) {
        double[] best = null;
        double bestDist = Double.MAX_VALUE;
        for (double[] p : points) {
            double d = squaredDist(p, query);
            if (d < bestDist) { bestDist = d; best = p; }
        }
        return best;
    }

    static double squaredDist(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += (a[i] - b[i]) * (a[i] - b[i]);
        return sum;
    }

    double[] readXY() {
        double x = Double.parseDouble(inputX.getText().trim());
        double y = Double.parseDouble(inputY.getText().trim());
        return new double[]{x, y};
    }

    double[] parseXY(String s) {
        String[] p = s.trim().split("\\s+");
        return new double[]{Double.parseDouble(p[0]), Double.parseDouble(p[1])};
    }

    String fmt(double[] p) {
        return String.format("(%.1f, %.1f)", p[0], p[1]);
    }

    void print(String msg) {
        outputArea.append(msg + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    boolean requireBuilt() {
        if (!built) { print("Build a tree first."); return false; }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KDTreeDemo().setVisible(true));
    }
}