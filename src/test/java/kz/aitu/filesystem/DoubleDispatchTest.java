package kz.aitu.filesystem;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.Element;
import kz.aitu.filesystem.model.FileNode;
import kz.aitu.filesystem.visitor.Visitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("T1: Double Dispatch Verification")
class DoubleDispatchTest {

    private static class RecordingVisitor implements Visitor {
        final List<String> callLog = new ArrayList<>();
        FileNode lastVisitedFile = null;
        DirectoryNode lastVisitedDirectory = null;

        @Override
        public void visit(FileNode file) {
            callLog.add("FileNode:" + file.getName());
            lastVisitedFile = file;
        }

        @Override
        public void visit(DirectoryNode directory) {
            callLog.add("DirectoryNode:" + directory.getName());
            lastVisitedDirectory = directory;
        }
    }

    @Test
    @DisplayName("T1: FileNode and DirectoryNode reach their exact overloaded visit methods via double dispatch")
    void testDoubleDispatchRouting() {
        Element fileElement = new FileNode("sample.txt", 1024);
        Element dirElement = new DirectoryNode("docs");

        RecordingVisitor visitor = new RecordingVisitor();

        // Dispatch through polymorphic Element interface reference
        fileElement.accept(visitor);
        assertEquals(1, visitor.callLog.size());
        assertEquals("FileNode:sample.txt", visitor.callLog.get(0));
        assertSame(fileElement, visitor.lastVisitedFile);
        assertNull(visitor.lastVisitedDirectory);

        dirElement.accept(visitor);
        assertEquals(2, visitor.callLog.size());
        assertEquals("DirectoryNode:docs", visitor.callLog.get(1));
        assertSame(dirElement, visitor.lastVisitedDirectory);
    }

    @Test
    @DisplayName("T1: accept rejects null visitor predictably")
    void testNullVisitorRejection() {
        Element file = new FileNode("file.bin", 10);
        Element dir = new DirectoryNode("dir");

        assertThrows(NullPointerException.class, () -> file.accept(null));
        assertThrows(NullPointerException.class, () -> dir.accept(null));
    }
}
