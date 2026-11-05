package kz.aitu.filesystem.model;

import kz.aitu.filesystem.visitor.Visitor;

/**
 * Common interface for all hierarchical file system nodes.
 * Adheres to the Composite and Visitor design patterns.
 */
public interface Element {

    /**
     * Returns the name of the file system element.
     *
     * @return node name
     */
    String getName();

    /**
     * Returns the parent directory containing this element, or null if it is a root.
     *
     * @return parent directory node or null
     */
    DirectoryNode getParent();

    /**
     * Updates the parent directory reference.
     * Internal invariant managed during tree construction.
     *
     * @param parent new parent directory node
     */
    void setParent(DirectoryNode parent);

    /**
     * Accepts a visitor to perform an independent operation on this element.
     * Implementation delegates to visitor.visit(this) according to double dispatch.
     *
     * @param visitor the visitor executing an operation
     */
    void accept(Visitor visitor);
}
