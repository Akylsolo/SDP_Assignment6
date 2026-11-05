package kz.aitu.filesystem.visitor;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.Element;
import kz.aitu.filesystem.model.FileNode;

import java.util.Objects;

/**
 * Visitor that computes the cumulative byte size of all files in an arbitrary subtree.
 * Adheres to R3: Each eligible file contributes exactly once; directories contribute 0 bytes.
 */
public class TotalSizeVisitor implements Visitor {

    private long totalSize = 0;

    /**
     * Resets the accumulated byte size, ensuring the visitor instance can be cleanly reused.
     */
    public void reset() {
        this.totalSize = 0;
    }

    /**
     * Returns the accumulated size in bytes.
     *
     * @return total byte size
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Visits a file node and accumulates its byte size.
     *
     * @param file the file node being processed
     */
    @Override
    public void visit(FileNode file) {
        Objects.requireNonNull(file, "FileNode must not be null");
        totalSize += file.getSize();
    }

    /**
     * Visits a directory node and propagates the traversal to all child elements.
     * Directory nodes do not contribute to the byte count.
     *
     * @param directory the directory node being processed
     */
    @Override
    public void visit(DirectoryNode directory) {
        Objects.requireNonNull(directory, "DirectoryNode must not be null");
        for (Element child : directory.getChildren()) {
            child.accept(this);
        }
    }
}
