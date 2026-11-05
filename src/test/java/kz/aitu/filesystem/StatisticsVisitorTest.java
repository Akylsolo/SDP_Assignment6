package kz.aitu.filesystem;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.FileNode;
import kz.aitu.filesystem.visitor.StatisticsVisitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("T5, T6, T7: Statistics Visitor Verification")
class StatisticsVisitorTest {

    @Test
    @DisplayName("T5: Collects accurate file count, directory count, and max depth (root depth = 0)")
    void testStatisticsCollection() {
        // Tree:
        // root/ (depth 0)
        // |-- a.txt (depth 1)
        // +-- d1/ (depth 1)
        //     |-- b.txt (depth 2)
        //     +-- d2/ (depth 2)
        //         |-- c.txt (depth 3)
        // +-- emptyDir/ (depth 1)
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("a.txt", 100));

        DirectoryNode d1 = new DirectoryNode("d1");
        d1.addChild(new FileNode("b.txt", 200));

        DirectoryNode d2 = new DirectoryNode("d2");
        d2.addChild(new FileNode("c.txt", 300));
        d1.addChild(d2);

        root.addChild(d1);
        root.addChild(new DirectoryNode("emptyDir"));

        StatisticsVisitor stats = new StatisticsVisitor();
        root.accept(stats);

        // 3 files: a.txt, b.txt, c.txt
        assertEquals(3, stats.getFileCount(), "File count must reflect all visited files");
        // 4 directories: root, d1, d2, emptyDir
        assertEquals(4, stats.getDirectoryCount(), "Directory count must include root and subdirectories");
        // max depth: root=0, d1=1, d2=2, c.txt=3
        assertEquals(3, stats.getMaxDepth(), "Max depth must reflect deepest node with root depth = 0");
    }

    @Test
    @DisplayName("T6: Evaluates empty directory correctly (fileCount=0, dirCount=1, maxDepth=0)")
    void testEmptyDirectoryStatistics() {
        DirectoryNode emptyRoot = new DirectoryNode("emptyRoot");
        StatisticsVisitor stats = new StatisticsVisitor();
        emptyRoot.accept(stats);

        assertEquals(0, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount());
        assertEquals(0, stats.getMaxDepth());
    }

    @Test
    @DisplayName("T5: Single FileNode accepted as root produces fileCount=1, dirCount=0, maxDepth=0")
    void testSingleFileRootStatistics() {
        FileNode file = new FileNode("lone.bin", 500);
        StatisticsVisitor stats = new StatisticsVisitor();
        file.accept(stats);

        assertEquals(1, stats.getFileCount());
        assertEquals(0, stats.getDirectoryCount());
        assertEquals(0, stats.getMaxDepth());
    }

    @Test
    @DisplayName("T7: Repeat executions do not leak or multiply state when reset() is invoked")
    void testControlledStateAndReset() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("f1.txt", 10));
        root.addChild(new DirectoryNode("sub"));

        StatisticsVisitor stats = new StatisticsVisitor();

        // Run 1
        root.accept(stats);
        assertEquals(1, stats.getFileCount());
        assertEquals(2, stats.getDirectoryCount());
        assertEquals(1, stats.getMaxDepth());

        // Reset and Run 2
        stats.reset();
        root.accept(stats);
        assertEquals(1, stats.getFileCount(), "State must not multiply after reset");
        assertEquals(2, stats.getDirectoryCount(), "State must not multiply after reset");
        assertEquals(1, stats.getMaxDepth(), "State must not multiply after reset");
    }
}
