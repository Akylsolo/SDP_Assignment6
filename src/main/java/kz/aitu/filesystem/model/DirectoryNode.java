package kz.aitu.filesystem.model;

import kz.aitu.filesystem.visitor.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Concrete composite element representing a directory in the file system.
 * Manages child elements and enforces tree integrity constraints.
 */
public class DirectoryNode implements Element {

    private final String name;
    private DirectoryNode parent;
    private final List<Element> children = new ArrayList<>();

    /**
     * Constructs a DirectoryNode with the specified non-empty name.
     *
     * @param name non-empty name of the directory
     * @throws IllegalArgumentException if name is null or blank
     */
    public DirectoryNode(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory name must not be null or blank");
        }
        this.name = name.trim();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    /**
     * Returns an unmodifiable view of the child elements.
     * Guarantees encapsulation and prevents external collection mutation.
     *
     * @return unmodifiable list of child elements
     */
    public List<Element> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Adds a child element to this directory while enforcing tree integrity:
     * - Child cannot be null
     * - Child cannot be self or an ancestor (cycle prevention)
     * - Child name must be unique among siblings
     * - Child cannot already belong to another parent
     *
     * @param child element to add
     * @throws IllegalArgumentException if child is null or duplicate sibling name exists
     * @throws IllegalStateException    if multiple parent ownership or cycle is detected
     */
    public void addChild(Element child) {
        if (child == null) {
            throw new IllegalArgumentException("Child element cannot be null");
        }
        if (child == this) {
            throw new IllegalStateException("Self-nesting cycle detected: directory cannot be added to itself");
        }

        // Cycle check: verify that child is not an ancestor in the parent chain
        DirectoryNode ancestor = this;
        while (ancestor != null) {
            if (ancestor == child) {
                throw new IllegalStateException("Cycle detected: cannot add ancestor '" + child.getName() + "' to directory '" + name + "'");
            }
            ancestor = ancestor.getParent();
        }

        // Duplicate sibling name check
        for (Element existing : children) {
            if (existing.getName().equalsIgnoreCase(child.getName())) {
                throw new IllegalArgumentException("Duplicate sibling name '" + child.getName() + "' in directory '" + name + "'");
            }
        }

        // Multiple parent ownership check
        if (child.getParent() != null && child.getParent() != this) {
            throw new IllegalStateException("Element '" + child.getName() + "' already belongs to parent '" + child.getParent().getName() + "'. Detach first.");
        }

        children.add(child);
        child.setParent(this);
    }

    /**
     * Removes a child element from this directory and detaches its parent reference.
     *
     * @param child element to remove
     * @return true if child was removed, false otherwise
     */
    public boolean removeChild(Element child) {
        if (child == null) {
            return false;
        }
        boolean removed = children.remove(child);
        if (removed && child.getParent() == this) {
            child.setParent(null);
        }
        return removed;
    }

    /**
     * Finds a child element by name.
     *
     * @param childName name to search for
     * @return Optional containing the child element if found
     */
    public Optional<Element> findChild(String childName) {
        if (childName == null) {
            return Optional.empty();
        }
        return children.stream()
                .filter(c -> c.getName().equalsIgnoreCase(childName.trim()))
                .findFirst();
    }

    /**
     * Double dispatch callback: dispatches directly to visitor.visit(DirectoryNode).
     *
     * @param visitor the visitor executing an operation
     */
    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "Visitor must not be null");
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "DirectoryNode{name='" + name + "', childrenCount=" + children.size() + "}";
    }
}
