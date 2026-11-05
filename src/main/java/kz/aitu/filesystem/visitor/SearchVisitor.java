package kz.aitu.filesystem.visitor;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.Element;
import kz.aitu.filesystem.model.FileNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Visitor that searches for files matching a configurable filename criterion and an inclusive
 * minimum-size threshold (size >= threshold).
 * Adheres to R4: Deterministic outputs with root-relative paths using '/' separator and sorted lexicographically.
 * Completely eliminates instanceof and type-checking chains via pure polymorphic double dispatch.
 */
public class SearchVisitor implements Visitor {

    private final Predicate<String> filenameCriterion;
    private final long minSizeThreshold;
    private final List<String> matchingPaths = new ArrayList<>();
    private final Deque<String> pathStack = new ArrayDeque<>();
    private DirectoryNode rootDirectory = null;

    /**
     * Constructs a SearchVisitor with a string matching criterion (case-insensitive substring) and size threshold.
     *
     * @param filenameSubstring substring to match in file name
     * @param minSizeThreshold  inclusive minimum file size in bytes
     */
    public SearchVisitor(String filenameSubstring, long minSizeThreshold) {
        this(name -> name != null && name.toLowerCase().contains(
                Objects.requireNonNull(filenameSubstring, "Filename criterion must not be null").toLowerCase()
        ), minSizeThreshold);
    }

    /**
     * Constructs a SearchVisitor with a custom filename predicate and size threshold.
     *
     * @param filenameCriterion predicate evaluated against file names
     * @param minSizeThreshold  inclusive minimum file size in bytes
     */
    public SearchVisitor(Predicate<String> filenameCriterion, long minSizeThreshold) {
        if (minSizeThreshold < 0) {
            throw new IllegalArgumentException("Minimum size threshold cannot be negative: " + minSizeThreshold);
        }
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion, "Filename criterion must not be null");
        this.minSizeThreshold = minSizeThreshold;
    }

    /**
     * Resets the visitor state, allowing safe reuse without stale data leakage.
     */
    public void reset() {
        matchingPaths.clear();
        pathStack.clear();
        rootDirectory = null;
    }

    /**
     * Returns the matching file paths relative to the root node, sorted lexicographically.
     *
     * @return unmodifiable list of deterministic root-relative paths
     */
    public List<String> getMatchingPaths() {
        List<String> sorted = new ArrayList<>(matchingPaths);
        Collections.sort(sorted);
        return Collections.unmodifiableList(sorted);
    }

    /**
     * Visits a file node, evaluating combined filename and minimum-size filters.
     *
     * @param file the file node being processed
     */
    @Override
    public void visit(FileNode file) {
        Objects.requireNonNull(file, "FileNode must not be null");

        // Evaluates both filter constraints (filename criterion AND size >= threshold)
        if (filenameCriterion.test(file.getName()) && file.getSize() >= minSizeThreshold) {
            matchingPaths.add(buildRelativePath(file.getName()));
        }
    }

    /**
     * Visits a directory node, updating path stack context and recursively propagating traversal.
     * Uses double dispatch exclusively without any runtime type discrimination.
     *
     * @param directory the directory node being processed
     */
    @Override
    public void visit(DirectoryNode directory) {
        Objects.requireNonNull(directory, "DirectoryNode must not be null");

        boolean isRoot = (rootDirectory == null);
        if (isRoot) {
            rootDirectory = directory;
        } else {
            pathStack.addLast(directory.getName());
        }

        for (Element child : directory.getChildren()) {
            child.accept(this);
        }

        if (!isRoot) {
            pathStack.removeLast();
        }
    }

    private String buildRelativePath(String fileName) {
        if (pathStack.isEmpty()) {
            return fileName;
        }
        return String.join("/", pathStack) + "/" + fileName;
    }
}
