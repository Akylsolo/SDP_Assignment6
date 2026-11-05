package kz.aitu.filesystem.visitor;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.FileNode;

/**
 * Visitor interface defining operations that can be performed over the file system hierarchy.
 * Encapsulates operations without altering the underlying element classes.
 */
public interface Visitor {

    /**
     * Operation executed when visiting a concrete FileNode.
     *
     * @param file the file node being processed
     */
    void visit(FileNode file);

    /**
     * Operation executed when visiting a concrete DirectoryNode.
     *
     * @param directory the directory node being processed
     */
    void visit(DirectoryNode directory);
}
