package kz.aitu.filesystem.visitor;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.Element;
import kz.aitu.filesystem.model.FileNode;

import java.util.Objects;

/**
 * Visitor that collects structural metrics across the file system hierarchy.
 * Adheres to R5: Tracks file count, directory count (including root if directory), and max depth (root depth = 0).
 * Implemented adhering to R8 (Extensibility): Added without modifying Element, FileNode, or DirectoryNode.
 */
public class StatisticsVisitor implements Visitor {

    private int fileCount = 0;
    private int directoryCount = 0;
    private int maxDepth = 0;
    private int currentDepth = 0;

    /**
     * Resets all accumulated counters and depth metrics for reuse.
     */
    public void reset() {
        this.fileCount = 0;
        this.directoryCount = 0;
        this.maxDepth = 0;
        this.currentDepth = 0;
    }

    /**
     * Returns the total count of visited files.
     *
     * @return file count
     */
    public int getFileCount() {
        return fileCount;
    }

    /**
     * Returns the total count of visited directories, including the root if it is a directory.
     *
     * @return directory count
     */
    public int getDirectoryCount() {
        return directoryCount;
    }

    /**
     * Returns the maximum depth reached during traversal, where root depth = 0.
     *
     * @return maximum hierarchy depth
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public void visit(FileNode file) {
        Objects.requireNonNull(file, "FileNode must not be null");
        fileCount++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        Objects.requireNonNull(directory, "DirectoryNode must not be null");
        directoryCount++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }

        for (Element child : directory.getChildren()) {
            currentDepth++;
            child.accept(this);
            currentDepth--;
        }
    }

    @Override
    public String toString() {
        return "Statistics{files=" + fileCount +
                ", directories=" + directoryCount +
                ", maxDepth=" + maxDepth + "}";
    }
}
