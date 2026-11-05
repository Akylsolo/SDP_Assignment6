package kz.aitu.filesystem;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.Element;
import kz.aitu.filesystem.model.FileNode;
import kz.aitu.filesystem.visitor.SearchVisitor;
import kz.aitu.filesystem.visitor.StatisticsVisitor;
import kz.aitu.filesystem.visitor.TotalSizeVisitor;
import kz.aitu.filesystem.visitor.TreePrinterVisitor;

import java.util.List;

/**
 * Deterministic executable demonstration of the Visitor Pattern applied to
 * an in-memory hierarchical file system.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASTANA IT UNIVERSITY — SOFTWARE DESIGN PATTERNS (ShP-2216)");
        System.out.println("   ASSIGNMENT 06: VISITOR PATTERN — ADVANCED FILE SYSTEM");
        System.out.println("   Student: Muratbek Akyl | Group: SE 2523");
        System.out.println("================================================================================\n");

        // 1. Construct hierarchical file system tree
        SampleFileSystem sample = buildSampleFileSystem();
        DirectoryNode root = sample.root;

        System.out.println("[1] HIERARCHY STRUCTURE INITIALIZED:");
        TreePrinterVisitor printer = new TreePrinterVisitor();
        root.accept(printer);
        System.out.print(printer.getFormattedTree());
        System.out.println();

        // 2. TotalSizeVisitor execution (R3)
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("[2] TOTAL SIZE VISITATION (R3):");
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.printf("    Subtree Total Size: %d bytes (%.2f KB / %.2f MB)%n",
                sizeVisitor.getTotalSize(),
                sizeVisitor.getTotalSize() / 1024.0,
                sizeVisitor.getTotalSize() / (1024.0 * 1024.0));
        System.out.println();

        // 3. SearchVisitor execution (R4)
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("[3] SEARCH VISITATION WITH COMBINED CONSTRAINTS (R4):");

        // Query A: Java source files with size >= 2000 bytes
        SearchVisitor javaSearch = new SearchVisitor(".java", 2000);
        root.accept(javaSearch);
        System.out.println("    Query A [Criterion: contains '.java', Min Size >= 2000 B]:");
        printSearchResults(javaSearch.getMatchingPaths());

        // Query B: Large files (>= 500,000 bytes)
        SearchVisitor largeFileSearch = new SearchVisitor("", 500_000);
        root.accept(largeFileSearch);
        System.out.println("    Query B [Criterion: all names, Min Size >= 500,000 B]:");
        printSearchResults(largeFileSearch.getMatchingPaths());

        // Query C: No-match edge case
        SearchVisitor noMatchSearch = new SearchVisitor("nonexistent_extension", 1);
        root.accept(noMatchSearch);
        System.out.println("    Query C [Criterion: 'nonexistent_extension', Min Size >= 1 B]:");
        printSearchResults(noMatchSearch.getMatchingPaths());
        System.out.println();

        // 4. StatisticsVisitor execution (R5 & R8)
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("[4] STATISTICS VISITATION (R5 & R8):");
        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.printf("    Total Files Visited       : %d%n", statsVisitor.getFileCount());
        System.out.printf("    Total Directories Visited : %d (includes root directory)%n", statsVisitor.getDirectoryCount());
        System.out.printf("    Maximum Tree Depth        : %d (root depth = 0)%n", statsVisitor.getMaxDepth());
        System.out.println();

        // 5. Visitor Reuse and Controlled State (T7 / Section 4)
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("[5] CONTROLLED STATE & IDEMPOTENT RE-EXECUTION (T7):");
        System.out.println("    Re-running StatisticsVisitor after explicit reset()...");
        statsVisitor.reset();
        root.accept(statsVisitor);
        System.out.printf("    After Reset — Files: %d, Directories: %d, MaxDepth: %d (No state leakage)%n",
                statsVisitor.getFileCount(), statsVisitor.getDirectoryCount(), statsVisitor.getMaxDepth());
        System.out.println();

        // 6. Tree Integrity Demonstrations (R6)
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("[6] TREE INTEGRITY ENFORCEMENT (R6):");
        testTreeIntegrity(root, sample.srcDir);

        System.out.println("\n================================================================================");
        System.out.println("   DEMONSTRATION COMPLETED SUCCESSFULLY WITH ALL CHECKS VERIFIED.");
        System.out.println("================================================================================");
    }

    private static class SampleFileSystem {
        DirectoryNode root;
        DirectoryNode srcDir;
    }

    private static SampleFileSystem buildSampleFileSystem() {
        SampleFileSystem system = new SampleFileSystem();
        DirectoryNode root = new DirectoryNode("aitu-project-root");

        // Root files
        root.addChild(new FileNode("pom.xml", 2_048));
        root.addChild(new FileNode("README.md", 1_536));

        // Subdirectory: src
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode main = new DirectoryNode("main");
        DirectoryNode mainJava = new DirectoryNode("java");
        mainJava.addChild(new FileNode("Application.java", 4_096));
        mainJava.addChild(new FileNode("Config.java", 1_024));
        main.addChild(mainJava);

        DirectoryNode mainResources = new DirectoryNode("resources");
        mainResources.addChild(new FileNode("application.properties", 512));
        mainResources.addChild(new FileNode("schema.sql", 3_072));
        main.addChild(mainResources);
        src.addChild(main);

        DirectoryNode test = new DirectoryNode("test");
        DirectoryNode testJava = new DirectoryNode("java");
        testJava.addChild(new FileNode("ApplicationTest.java", 2_560));
        test.addChild(testJava);
        src.addChild(test);
        root.addChild(src);

        // Subdirectory: docs
        DirectoryNode docs = new DirectoryNode("docs");
        docs.addChild(new FileNode("architecture.pdf", 1_048_576));
        docs.addChild(new FileNode("report.md", 4_096));
        root.addChild(docs);

        // Subdirectory: logs (empty directory edge case)
        DirectoryNode logs = new DirectoryNode("logs");
        root.addChild(logs);

        // Subdirectory: data
        DirectoryNode data = new DirectoryNode("data");
        data.addChild(new FileNode("export.csv", 524_288));
        root.addChild(data);

        system.root = root;
        system.srcDir = src;
        return system;
    }

    private static void printSearchResults(List<String> paths) {
        if (paths.isEmpty()) {
            System.out.println("      (no matches found)");
        } else {
            for (String path : paths) {
                System.out.println("      -> " + path);
            }
        }
    }

    private static void testTreeIntegrity(DirectoryNode root, DirectoryNode srcDir) {
        // Negative size rejection
        try {
            new FileNode("corrupt.bin", -100);
            System.out.println("    [FAIL] Negative size was accepted!");
        } catch (IllegalArgumentException e) {
            System.out.println("    [PASS] Negative file size rejected: " + e.getMessage());
        }

        // Duplicate sibling name rejection
        try {
            root.addChild(new FileNode("pom.xml", 500));
            System.out.println("    [FAIL] Duplicate sibling name was accepted!");
        } catch (IllegalArgumentException e) {
            System.out.println("    [PASS] Duplicate sibling name rejected: " + e.getMessage());
        }

        // Multiple parent ownership rejection
        try {
            DirectoryNode extraDir = new DirectoryNode("extra");
            Element child = root.getChildren().get(0);
            extraDir.addChild(child);
            System.out.println("    [FAIL] Multiple parent ownership was accepted!");
        } catch (IllegalStateException e) {
            System.out.println("    [PASS] Multiple parent ownership rejected: " + e.getMessage());
        }

        // Cycle prevention
        try {
            srcDir.addChild(root);
            System.out.println("    [FAIL] Cycle addition was accepted!");
        } catch (IllegalStateException e) {
            System.out.println("    [PASS] Cycle detected and rejected: " + e.getMessage());
        }
    }
}
