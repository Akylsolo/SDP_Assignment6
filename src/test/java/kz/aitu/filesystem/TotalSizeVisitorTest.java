package kz.aitu.filesystem;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.FileNode;
import kz.aitu.filesystem.visitor.TotalSizeVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("T2: Total Size Visitor Verification")
class TotalSizeVisitorTest {

    private TotalSizeVisitor sizeVisitor;

    @BeforeEach
    void setUp() {
        sizeVisitor = new TotalSizeVisitor();
    }

    @Test
    @DisplayName("T2: Accurately accumulates file sizes across multiple nesting levels")
    void testSizeAccumulationAcrossLevels() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("f1.txt", 100)); // Level 1

        DirectoryNode level1 = new DirectoryNode("level1");
        level1.addChild(new FileNode("f2.txt", 250)); // Level 2

        DirectoryNode level2 = new DirectoryNode("level2");
        level2.addChild(new FileNode("f3.txt", 650)); // Level 3

        DirectoryNode level3 = new DirectoryNode("level3");
        level3.addChild(new FileNode("f4.txt", 1000)); // Level 4
        level3.addChild(new FileNode("empty.txt", 0)); // 0-byte file

        level2.addChild(level3);
        level1.addChild(level2);
        root.addChild(level1);

        root.accept(sizeVisitor);
        assertEquals(2000L, sizeVisitor.getTotalSize());
    }

    @Test
    @DisplayName("T2: Directory nodes contribute exactly zero bytes to total size")
    void testDirectoriesDoNotContributeSize() {
        DirectoryNode emptyRoot = new DirectoryNode("emptyRoot");
        DirectoryNode sub1 = new DirectoryNode("sub1");
        DirectoryNode sub2 = new DirectoryNode("sub2");
        emptyRoot.addChild(sub1);
        sub1.addChild(sub2);

        emptyRoot.accept(sizeVisitor);
        assertEquals(0L, sizeVisitor.getTotalSize());
    }

    @Test
    @DisplayName("T2: Single FileNode accepted as root accumulates exactly its own size")
    void testSingleFileRoot() {
        FileNode single = new FileNode("standalone.dat", 4096);
        single.accept(sizeVisitor);
        assertEquals(4096L, sizeVisitor.getTotalSize());
    }
}
