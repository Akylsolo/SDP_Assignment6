package kz.aitu.filesystem;

import kz.aitu.filesystem.model.DirectoryNode;
import kz.aitu.filesystem.model.FileNode;
import kz.aitu.filesystem.visitor.SearchVisitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("T3, T4, T6: Search Visitor Verification")
class SearchVisitorTest {

    private DirectoryNode buildSearchTree() {
        DirectoryNode root = new DirectoryNode("searchRoot");

        // root level files
        root.addChild(new FileNode("zeta.txt", 100));
        root.addChild(new FileNode("alpha.txt", 500));
        root.addChild(new FileNode("beta.log", 300));

        // docs folder
        DirectoryNode docs = new DirectoryNode("docs");
        docs.addChild(new FileNode("alpha.txt", 1000));
        docs.addChild(new FileNode("gamma.txt", 50));

        // docs/archive folder
        DirectoryNode archive = new DirectoryNode("archive");
        archive.addChild(new FileNode("delta.txt", 800));
        docs.addChild(archive);
        root.addChild(docs);

        // empty folder
        root.addChild(new DirectoryNode("emptyFolder"));

        return root;
    }

    @Test
    @DisplayName("T3: Search combines both filename criterion AND min-size threshold (inclusive)")
    void testCombinesBothFilterConditions() {
        DirectoryNode root = buildSearchTree();

        // Must contain ".txt" AND size >= 500
        SearchVisitor visitor = new SearchVisitor(".txt", 500);
        root.accept(visitor);

        List<String> results = visitor.getMatchingPaths();
        // Eligible: alpha.txt (500), docs/alpha.txt (1000), docs/archive/delta.txt (800)
        // Rejected: zeta.txt (100 < 500), gamma.txt (50 < 500), beta.log (not .txt)
        assertEquals(3, results.size());
        assertTrue(results.contains("alpha.txt"));
        assertTrue(results.contains("docs/alpha.txt"));
        assertTrue(results.contains("docs/archive/delta.txt"));
    }

    @Test
    @DisplayName("T4: Returns deterministic, lexicographically sorted, root-relative paths using '/'")
    void testDeterministicRootRelativePaths() {
        DirectoryNode root = buildSearchTree();

        SearchVisitor visitor = new SearchVisitor(".txt", 0);
        root.accept(visitor);

        List<String> results = visitor.getMatchingPaths();
        List<String> expected = List.of(
                "alpha.txt",
                "docs/alpha.txt",
                "docs/archive/delta.txt",
                "docs/gamma.txt",
                "zeta.txt"
        );

        assertEquals(expected, results, "Paths must be root-relative, use '/', and be sorted lexicographically");
    }

    @Test
    @DisplayName("T6: Handles empty structures and no-match search cases gracefully")
    void testEmptyAndNoMatchCases() {
        DirectoryNode emptyRoot = new DirectoryNode("emptyRoot");
        SearchVisitor searchVisitor = new SearchVisitor("any", 0);
        emptyRoot.accept(searchVisitor);
        assertTrue(searchVisitor.getMatchingPaths().isEmpty(), "Empty structure should yield empty results");

        DirectoryNode tree = buildSearchTree();
        SearchVisitor noMatch = new SearchVisitor("missing_query", 10_000);
        tree.accept(noMatch);
        assertNotNull(noMatch.getMatchingPaths());
        assertTrue(noMatch.getMatchingPaths().isEmpty(), "No-match query should return empty list");
    }

    @Test
    @DisplayName("T4: Custom Predicate filename criterion is supported")
    void testCustomPredicateSearch() {
        DirectoryNode root = buildSearchTree();

        // Predicate: filename starts with 'a' or 'd'
        SearchVisitor visitor = new SearchVisitor(
                name -> name.startsWith("a") || name.startsWith("d"),
                400
        );
        root.accept(visitor);

        List<String> results = visitor.getMatchingPaths();
        assertEquals(List.of("alpha.txt", "docs/alpha.txt", "docs/archive/delta.txt"), results);
    }
}
