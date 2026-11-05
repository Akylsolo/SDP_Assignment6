# Visitor Pattern — Advanced File System

**Course:** ShP-2216 Software Design Patterns  
**Institution:** Astana IT University  
**Student:** Muratbek Akyl  
**Group:** SE 2523  

---

## 1. Project Overview

This project implements an in-memory hierarchical file system utilizing the **Visitor Behavioral Design Pattern** in Java 17 with Maven. The system separates the composite object structure (`Element`, `FileNode`, `DirectoryNode`) from operations executed over it.

Three independent visitor operations are implemented over the same hierarchical structure:
1. **`TotalSizeVisitor` (R3):** Computes the recursive byte size sum of all files in an arbitrary subtree. Directory nodes contribute zero bytes to the size calculation.
2. **`SearchVisitor` (R4):** Multi-constraint file query engine accepting a configurable filename criterion and an inclusive minimum size threshold (`size >= threshold`). Produces deterministic, root-relative file paths separated by `/` and sorted lexicographically.
3. **`StatisticsVisitor` (R5 & R8):** Structural metrics collector tracking total file count, total directory count (including root if directory), and maximum hierarchy depth (root depth = 0). Extends system behavior without modifying any element classes.

---

## 2. Architecture & Design Patterns

### 2.1 Double Dispatch Mechanism (R2)
Double dispatch decouples operations from the element hierarchy by routing execution through two polymorphic dispatches:
1. **Primary Dispatch (Dynamic Dispatch on Element):** Client invokes `element.accept(visitor)`.
2. **Secondary Dispatch (Static Overload Resolution + Dynamic Dispatch on Visitor):** The concrete element invokes `visitor.visit(this)`:
   - Inside `FileNode`: `this` is typed as `FileNode`, binding to `visitor.visit(FileNode)`.
   - Inside `DirectoryNode`: `this` is typed as `DirectoryNode`, binding to `visitor.visit(DirectoryNode)`.

This completely eliminates runtime type inspection (`instanceof`, `getClass()`, or reflection).

### 2.2 Traversal Responsibility (R7)
**Design Decision:** *Visitor-Owned Traversal (Internal Visitor Traversal)*.
- **Justification:**
  - **Contextual State Isolation:** Different visitor operations require different traversal states. For instance, `SearchVisitor` maintains a `Deque<String>` path stack to compute root-relative paths, while `StatisticsVisitor` maintains `currentDepth` counters to determine tree depth. Placing traversal within the visitor allows each visitor to own and manage its contextual stack without polluting `Element` interfaces.
  - **Single Responsibility Principle (SRP):** Element classes remain focused purely on managing composite data invariants, without traversal orchestration logic.
  - **Reachable Node Guarantee:** Every reachable node in the hierarchy is visited exactly once per operation. Empty directories and deeply nested trees are fully supported.

### 2.3 Tree Integrity & Invariant Preservation (R6)
`DirectoryNode` and `FileNode` enforce strict structural integrity invariants:
- **Non-negative file sizes:** Rejects negative size values during initialization and mutation.
- **Duplicate sibling name prevention:** Rejects adding elements with identical names (case-insensitive) under the same parent directory.
- **Single-parent ownership:** Prevents elements from being added to a directory if already owned by another directory (requires explicit removal first).
- **Cycle prevention:** Detects and prevents self-nesting and ancestral cycles before child attachment by checking parent reference chains.
- **Encapsulation:** Child collections are exposed strictly through `Collections.unmodifiableList()`.

### 2.4 Extensibility Boundary (R8)
The `StatisticsVisitor` was introduced strictly by implementing the `Visitor` interface. Zero modifications were made to `Element`, `FileNode`, `DirectoryNode`, or their `accept()` methods, confirming adherence to the **Open/Closed Principle (OCP)**.

---

## 3. Project Structure

```
SDP_Assignment6/
├── pom.xml                               # Maven project configuration (Java 17, JUnit 5)
├── mvn.cmd / mvn.bat                     # Maven wrapper scripts
├── README.md                             # Project documentation
└── src/
    ├── main/java/kz/aitu/filesystem/
    │   ├── Main.java                     # Deterministic demonstration executable
    │   ├── model/
    │   │   ├── Element.java              # Common Element interface
    │   │   ├── FileNode.java             # Concrete leaf file element
    │   │   └── DirectoryNode.java        # Concrete composite directory element
    │   └── visitor/
    │       ├── Visitor.java              # Visitor interface with overloaded visit methods
    │       ├── TotalSizeVisitor.java     # Subtree file size aggregator
    │       ├── SearchVisitor.java        # Multi-constraint query visitor
    │       ├── StatisticsVisitor.java    # Structural metrics visitor
    │       └── TreePrinterVisitor.java   # Hierarchy ASCII visualization visitor
    └── test/java/kz/aitu/filesystem/
        ├── DoubleDispatchTest.java       # T1: Double dispatch verification
        ├── TotalSizeVisitorTest.java     # T2: Size accumulation verification
        ├── SearchVisitorTest.java        # T3, T4, T6: Search filter & path verification
        ├── StatisticsVisitorTest.java    # T5, T6, T7: Structural metrics & reset verification
        └── TreeIntegrityTest.java        # T8: Tree invariants & cycle prevention
```

---

## 4. Build and Execution Instructions

### 4.1 Running Automated Unit Tests
To execute all JUnit 5 test suites from the project root:
```bash
mvn clean test
```
*Alternatively, using the bundled wrapper on Windows:*
```cmd
.\mvn.cmd clean test
```

### 4.2 Running the Application Demonstration
To run the deterministic demonstration program:
```bash
mvn compile exec:java
```
*Alternatively:*
```cmd
.\mvn.cmd compile exec:java
```

---

## 5. Verification Checkpoints Summary

| Checkpoint | Automated Verification Target | Status |
|---|---|:---:|
| **T1** | Both element types reach the correct overloaded visit methods via double dispatch | **PASSED** |
| **T2** | Size accumulation across multiple nesting levels is accurate; directories contribute 0 bytes | **PASSED** |
| **T3** | Search correctly combines both filename criterion and inclusive minimum size | **PASSED** |
| **T4** | Search produces deterministic, root-relative paths separated by `/` and sorted lexicographically | **PASSED** |
| **T5** | Statistics correctly report file count, directory count (including root), and maximum depth (root = 0) | **PASSED** |
| **T6** | Empty directory structures and no-match search queries are handled gracefully | **PASSED** |
| **T7** | Controlled state: `reset()` ensures repeat executions do not leak or multiply state | **PASSED** |
| **T8** | Tree integrity: negative sizes, duplicate names, multi-parent ownership, and cycles are rejected | **PASSED** |

**Total Tests:** 20 | **Failures:** 0 | **Errors:** 0 | **Skipped:** 0 | **Build Status:** SUCCESS
