package kz.aitu.filesystem;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.FileNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("T8: Tree Integrity & Engineering Invariants Verification")
class TreeIntegrityTest {

    @Test
    @DisplayName("T8: Rejects negative file size upon creation and modification")
    void testNegativeFileSizeRejection() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("invalid.bin", -1));
        assertThrows(IllegalArgumentException.class, () -> new FileNode("invalid.bin", -9999));

        FileNode valid = new FileNode("valid.bin", 100);
        assertThrows(IllegalArgumentException.class, () -> valid.setSize(-50));
        assertEquals(100, valid.getSize(), "Size must remain unchanged when invalid update is rejected");
    }

    @Test
    @DisplayName("T8: Rejects null or blank file and directory names")
    void testBlankNameRejection() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode(null, 10));
        assertThrows(IllegalArgumentException.class, () -> new FileNode("   ", 10));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(null));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode("   "));
    }

    @Test
    @DisplayName("T8: Rejects duplicate sibling names (case-insensitive)")
    void testDuplicateSiblingNameRejection() {
        DirectoryNode dir = new DirectoryNode("parent");
        dir.addChild(new FileNode("config.json", 100));

        // Adding exact duplicate
        assertThrows(IllegalArgumentException.class, () -> dir.addChild(new FileNode("config.json", 200)));

        // Adding case-insensitive duplicate
        assertThrows(IllegalArgumentException.class, () -> dir.addChild(new FileNode("CONFIG.JSON", 300)));

        // Adding directory with identical name as file sibling
        assertThrows(IllegalArgumentException.class, () -> dir.addChild(new DirectoryNode("config.json")));
    }

    @Test
    @DisplayName("T8: Rejects multiple parent ownership unless detached")
    void testMultipleParentOwnershipRejection() {
        DirectoryNode dir1 = new DirectoryNode("dir1");
        DirectoryNode dir2 = new DirectoryNode("dir2");

        FileNode file = new FileNode("shared.doc", 50);
        dir1.addChild(file);

        // Attempting to add file to dir2 while already owned by dir1
        assertThrows(IllegalStateException.class, () -> dir2.addChild(file));

        // Detach and move to dir2
        assertTrue(dir1.removeChild(file));
        assertNull(file.getParent());
        assertDoesNotThrow(() -> dir2.addChild(file));
        assertSame(dir2, file.getParent());
    }

    @Test
    @DisplayName("T8: Rejects self-nesting and cycles in directory hierarchy")
    void testCycleDetection() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");
        DirectoryNode grandChild = new DirectoryNode("grandChild");

        root.addChild(child);
        child.addChild(grandChild);

        // Self nesting
        assertThrows(IllegalStateException.class, () -> root.addChild(root));

        // Direct cycle: adding root to child
        assertThrows(IllegalStateException.class, () -> child.addChild(root));

        // Indirect cycle: adding root to grandChild
        assertThrows(IllegalStateException.class, () -> grandChild.addChild(root));

        // Indirect cycle: adding child to grandChild
        assertThrows(IllegalStateException.class, () -> grandChild.addChild(child));
    }

    @Test
    @DisplayName("T8: Unmodifiable child collection protects encapsulation")
    void testEncapsulationEnforcement() {
        DirectoryNode dir = new DirectoryNode("secured");
        dir.addChild(new FileNode("f.txt", 10));

        assertThrows(UnsupportedOperationException.class, () ->
                dir.getChildren().add(new FileNode("hacked.txt", 100))
        );
        assertThrows(UnsupportedOperationException.class, () ->
                dir.getChildren().clear()
        );
    }

    @Test
    @DisplayName("T8: Valid arbitrary nesting operates seamlessly")
    void testValidArbitraryNesting() {
        DirectoryNode current = new DirectoryNode("level0");
        DirectoryNode root = current;

        for (int i = 1; i <= 10; i++) {
            DirectoryNode next = new DirectoryNode("level" + i);
            current.addChild(next);
            current = next;
        }

        current.addChild(new FileNode("deep_leaf.txt", 777));
        assertNotNull(root);
        assertEquals("level0", root.getName());
    }
}
