package kz.aitu.filesystem.model;

import kz.aitu.filesystem.visitor.Visitor;

import java.util.Objects;

/**
 * Concrete leaf element representing a file in the file system.
 * Holds an immutable/mutable file name, a non-negative byte size, and an optional parent directory.
 */
public class FileNode implements Element {

    private final String name;
    private long size;
    private DirectoryNode parent;

    /**
     * Constructs a new FileNode with the specified name and size in bytes.
     *
     * @param name non-empty name of the file
     * @param size non-negative size in bytes
     * @throws IllegalArgumentException if name is invalid or size is negative
     */
    public FileNode(String name, long size) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("File name must not be null or blank");
        }
        if (size < 0) {
            throw new IllegalArgumentException("File size cannot be negative: " + size);
        }
        this.name = name.trim();
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the non-negative size in bytes.
     *
     * @return file size in bytes
     */
    public long getSize() {
        return size;
    }

    /**
     * Updates the file size.
     *
     * @param size non-negative size in bytes
     * @throws IllegalArgumentException if size is negative
     */
    public void setSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("File size cannot be negative: " + size);
        }
        this.size = size;
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
     * Double dispatch callback: dispatches directly to visitor.visit(FileNode).
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
        return "FileNode{name='" + name + "', size=" + size + " bytes}";
    }
}
