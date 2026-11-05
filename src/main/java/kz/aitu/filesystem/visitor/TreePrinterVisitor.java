package kz.aitu.filesystem.visitor;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.Element;
import kz.aitu.filesystem.model.FileNode;

import java.util.Objects;

/**
 * Visitor that formats and prints the file system hierarchy as an indented ASCII tree.
 * Demonstrates the elegance of the Visitor pattern by eliminating any need for instanceof
 * in tree visualization.
 */
public class TreePrinterVisitor implements Visitor {

    private final StringBuilder output = new StringBuilder();
    private String indent = "";

    @Override
    public void visit(FileNode file) {
        Objects.requireNonNull(file, "FileNode must not be null");
        output.append(indent)
                .append("|-- ")
                .append(file.getName())
                .append(" (")
                .append(file.getSize())
                .append(" bytes)\n");
    }

    @Override
    public void visit(DirectoryNode directory) {
        Objects.requireNonNull(directory, "DirectoryNode must not be null");
        output.append(indent)
                .append("+-- [")
                .append(directory.getName())
                .append("/]\n");

        String previousIndent = indent;
        indent = indent + "    ";
        for (Element child : directory.getChildren()) {
            child.accept(this);
        }
        indent = previousIndent;
    }

    /**
     * Returns the formatted ASCII tree.
     *
     * @return tree string representation
     */
    public String getFormattedTree() {
        return output.toString();
    }

    /**
     * Resets internal buffer and indent.
     */
    public void reset() {
        output.setLength(0);
        indent = "";
    }
}
