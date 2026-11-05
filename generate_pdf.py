import os
from reportlab.lib.pagesizes import letter, A4
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, Image, KeepTogether, Preformatted
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        self.setFont("Helvetica-Bold", 7.5)
        self.setFillColor(colors.HexColor("#475569"))
        
        # Header (pages 2+)
        if self._pageNumber > 1:
            self.drawString(40, 810, "ASTANA IT UNIVERSITY — ShP-2216 SOFTWARE DESIGN PATTERNS")
            self.drawRightString(555, 810, "ASSIGNMENT 06: VISITOR PATTERN")
            self.setStrokeColor(colors.HexColor("#CBD5E1"))
            self.setLineWidth(0.6)
            self.line(40, 804, 555, 804)

        # Footer (all pages)
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        self.drawString(40, 28, "Author: Muratbek Akyl | Group: SE 2523 | Java 17 & Maven")
        page_str = f"Page {self._pageNumber} of {page_count}"
        self.drawRightString(555, 28, page_str)
        self.setStrokeColor(colors.HexColor("#CBD5E1"))
        self.setLineWidth(0.6)
        self.line(40, 38, 555, 38)
        self.restoreState()


def build_pdf(filename="Assignment06_Report_Muratbek_Akyl_SE2523.pdf"):
    doc = SimpleDocTemplate(
        filename,
        pagesize=A4,
        leftMargin=40,
        rightMargin=40,
        topMargin=44,
        bottomMargin=44
    )

    styles = getSampleStyleSheet()

    # Custom styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=15.5,
        leading=19,
        textColor=colors.HexColor('#0F172A'),
        spaceAfter=3
    )

    inst_style = ParagraphStyle(
        'InstHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=colors.HexColor('#1E3A8A'),
        textTransform='uppercase',
        spaceAfter=1
    )

    sub_style = ParagraphStyle(
        'SubHeader',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8,
        leading=10,
        textColor=colors.HexColor('#64748B'),
        spaceAfter=5
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10.5,
        leading=13.5,
        textColor=colors.HexColor('#1E3A8A'),
        spaceBefore=7,
        spaceAfter=3,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.2,
        leading=10.8,
        textColor=colors.HexColor('#1E293B'),
        alignment=4, # Justified
        spaceAfter=4
    )

    bullet_style = ParagraphStyle(
        'Bullet_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.0,
        leading=10.5,
        textColor=colors.HexColor('#1E293B'),
        leftIndent=12,
        firstLineIndent=-8,
        spaceAfter=2
    )

    clean_code_style = ParagraphStyle(
        'CleanCode_Style',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.0,
        leading=10.5,
        textColor=colors.HexColor('#1E293B'),
        leftIndent=8,
        spaceAfter=3
    )

    callout_style = ParagraphStyle(
        'Callout_Style',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.0,
        leading=10.5,
        textColor=colors.HexColor('#1E3A8A')
    )

    table_header_style = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.8,
        leading=9.5,
        textColor=colors.HexColor('#1E3A8A')
    )

    table_cell_style = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=7.4,
        leading=9.2,
        textColor=colors.HexColor('#1E293B')
    )

    table_code_cell = ParagraphStyle(
        'TableCode',
        parent=styles['Normal'],
        fontName='Courier-Bold',
        fontSize=7.2,
        leading=9.0,
        textColor=colors.HexColor('#0F172A')
    )

    code_block_style = ParagraphStyle(
        'CodeBlock',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=6.8,
        leading=8.5,
        textColor=colors.HexColor('#F8FAFC')
    )

    story = []

    # ================= PAGE 1 =================
    # Top Academic Header
    story.append(Paragraph("ASTANA IT UNIVERSITY — DEPARTMENT OF COMPUTER ENGINEERING", inst_style))
    story.append(Paragraph("COURSE: ShP-2216 SOFTWARE DESIGN PATTERNS | RK2 LABORATORY ASSESSMENT", sub_style))
    story.append(Paragraph("Visitor Pattern — Advanced In-Memory File System", title_style))

    # Metadata Table Box
    meta_data = [
        [
            Paragraph("<b>Author:</b> Muratbek Akyl", table_cell_style),
            Paragraph("<b>Group:</b> SE 2523", table_cell_style),
            Paragraph("<b>Stack:</b> Java 17 | Maven | JUnit 5", table_cell_style),
            Paragraph("<b>Repo:</b> github.com/muratbek-akyl/SDP_Assignment6", table_cell_style)
        ]
    ]
    meta_table = Table(meta_data, colWidths=[110, 80, 130, 195])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F8FAFC')),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor('#CBD5E1')),
        ('PADDING', (0,0), (-1,-1), 3),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 4))

    # Section 1
    story.append(Paragraph("1. Purpose & Architectural Design Choices", h1_style))
    p1_text = (
        "This project implements an in-memory hierarchical file system utilizing the <b>Visitor Behavioral Design Pattern</b> "
        "(Gang of Four) to strictly decouple operational algorithms from the underlying composite data structure. In conventional "
        "hierarchical models, adding operations like size accumulation, multi-criteria filtering, or tree statistics causes "
        "class bloat, violates the Single Responsibility Principle, and couples nodes to transient domain logic. The Visitor pattern "
        "solves this by externalizing operations into specialized visitor objects without modifying the elements."
    )
    story.append(Paragraph(p1_text, body_style))

    # Callout box for Double Dispatch
    dd_box = [
        [Paragraph("<b>Double Dispatch Protocol (R2 & Engineering Constraint):</b><br/>"
                   "Double dispatch routes execution through two coordinated polymorphic dispatches:<br/>"
                   "• <b>Step 1 (Dynamic Dispatch on Element):</b> Client invokes <code>element.accept(visitor)</code>. At runtime, the JVM dispatches to the concrete element instance (<code>FileNode</code> or <code>DirectoryNode</code>).<br/>"
                   "• <b>Step 2 (Static Overload Resolution + Dynamic Dispatch on Visitor):</b> The element invokes <code>visitor.visit(this)</code>. Because <code>this</code> has compile-time static type <code>FileNode</code> or <code>DirectoryNode</code>, the Java compiler statically resolves the exact overloaded method signature without any runtime type discrimination (<b>zero <code>instanceof</code>, zero <code>getClass()</code>, zero casts</b>).", callout_style)]
    ]
    t_dd = Table(dd_box, colWidths=[515])
    t_dd.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#EFF6FF')),
        ('LINELEFT', (0,0), (-1,-1), 3.0, colors.HexColor('#2563EB')),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor('#BFDBFE')),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t_dd)
    story.append(Spacer(1, 3))

    # Section 2
    story.append(Paragraph("2. Traversal Ownership & Architectural Trade-offs", h1_style))
    story.append(Paragraph(
        "<b>Traversal Responsibility (R7):</b> We selected <b>Visitor-Owned Traversal (Internal Traversal)</b> over element-driven traversal or external walkers:",
        body_style
    ))
    story.append(Paragraph("• <b>Contextual State Isolation:</b> Different visitor operations require distinct transient state machines. <code>SearchVisitor</code> maintains a <code>Deque&lt;String&gt;</code> path stack to deterministically generate root-relative paths, while <code>StatisticsVisitor</code> tracks hierarchical depth levels. Placing traversal inside the visitor encapsulates contextual stacks without polluting <code>Element</code>.", bullet_style))
    story.append(Paragraph("• <b>Single Responsibility Principle:</b> <code>DirectoryNode</code> and <code>FileNode</code> remain lightweight composite containers managing child containment and byte sizes, free of traversal orchestration logic.", bullet_style))
    story.append(Paragraph("• <b>Guaranteed Reachability:</b> Every reachable node in the hierarchy is visited exactly once per execution. Both nested hierarchies and empty directories (depth and directory count) are comprehensively supported.", bullet_style))

    # Architectural Trade-off
    tradeoff_box = [
        [Paragraph("<b>Architectural Trade-off of the Visitor Pattern:</b><br/>"
                   "• <b>Adding New Operations (Extremely Cheap):</b> Fully conforms to the <i>Open/Closed Principle (OCP)</i>. Introducing <code>StatisticsVisitor</code> (R8) required zero modifications to <code>Element</code>, <code>FileNode</code>, or <code>DirectoryNode</code>.<br/>"
                   "• <b>Adding New Element Types (Costly):</b> Introducing a new element (e.g. <code>SymlinkNode</code>) breaks the <code>Visitor</code> interface contract, requiring a new <code>visit(SymlinkNode)</code> signature and forcing manual updates across all existing concrete visitors.", callout_style)]
    ]
    t_to = Table(tradeoff_box, colWidths=[515])
    t_to.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F8FAFC')),
        ('LINELEFT', (0,0), (-1,-1), 3.0, colors.HexColor('#475569')),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E1')),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t_to)
    story.append(Spacer(1, 3))

    # Section 3
    story.append(Paragraph("3. Five Concrete Clean Code Justifications (Section 6)", h1_style))
    story.append(Paragraph("<b>1. Meaningful Naming (<code>SearchVisitor.getMatchingPaths()</code>, <code>TotalSizeVisitor.getTotalSize()</code>):</b> Methods and variables employ self-describing domain terms that communicate operational intent unambiguously without inline commentary.", clean_code_style))
    story.append(Paragraph("<b>2. Single Responsibility Principle (<code>FileNode</code> vs. <code>StatisticsVisitor</code>):</b> Domain entities model structural hierarchy invariants exclusively, while computational algorithms (size accumulation, query filtering, metrics) are isolated in dedicated visitor classes.", clean_code_style))
    story.append(Paragraph("<b>3. Small Methods & Minimal Complexity (<code>SearchVisitor.buildRelativePath()</code>):</b> Methods are concise (under 12 lines) and perform a single logical task with zero nested looping, ensuring high readability and easy testability.", clean_code_style))
    story.append(Paragraph("<b>4. Encapsulation & Defensive Invariants (<code>DirectoryNode.getChildren()</code>):</b> Internal child collections are wrapped in <code>Collections.unmodifiableList()</code>. All modifications are forced through mutating methods enforcing tree integrity (R6).", clean_code_style))
    story.append(Paragraph("<b>5. Elimination of Type-Checking Chains (Double Dispatch in <code>Element.accept()</code>):</b> Replaced fragile <code>if-else</code> ladders and <code>instanceof</code> checks with polymorphic double dispatch, adhering strictly to engineering constraints.", clean_code_style))

    # ================= PAGE 2 =================
    story.append(PageBreak())
    story.append(Paragraph("4. System Architecture & UML Class Diagram", h1_style))
    story.append(Paragraph(
        "The class diagram below illustrates the complete object model, highlighting the separation between the Composite Element Hierarchy (left) and the Visitor Hierarchy (right), bridged by the bidirectional double dispatch interaction.",
        body_style
    ))

    # High-res UML Diagram
    if os.path.exists("uml_diagram.png"):
        img = Image("uml_diagram.png", width=7.2*inch, height=4.5*inch)
        story.append(img)
    story.append(Spacer(1, 4))

    # Architectural overview bullets
    story.append(Paragraph("<b>Key Architectural Components:</b>", body_style))
    story.append(Paragraph("• <b><code>Element</code> Interface:</b> Common abstraction declaring <code>getName()</code>, <code>getParent()</code>, <code>setParent()</code>, and <code>accept(Visitor)</code>.", bullet_style))
    story.append(Paragraph("• <b><code>FileNode</code> (Leaf):</b> Holds immutable name and non-negative byte size; dispatches to <code>visitor.visit(this)</code>.", bullet_style))
    story.append(Paragraph("• <b><code>DirectoryNode</code> (Composite):</b> Encapsulates unmodifiable child collection; guards invariants (non-null, no duplicate siblings, single-parent, cycle detection); dispatches to <code>visitor.visit(this)</code>.", bullet_style))
    story.append(Paragraph("• <b><code>Visitor</code> Interface:</b> Defines overloaded <code>visit(FileNode)</code> and <code>visit(DirectoryNode)</code> contracts.", bullet_style))
    story.append(Paragraph("• <b>Concrete Visitors:</b> <code>TotalSizeVisitor</code> (R3 size aggregator), <code>SearchVisitor</code> (R4 multi-constraint deterministic query), <code>StatisticsVisitor</code> (R5 metrics collector, R8 extension boundary), and <code>TreePrinterVisitor</code> (ASCII visualization).", bullet_style))

    # ================= PAGE 3 =================
    story.append(PageBreak())
    story.append(Paragraph("5. Tree Integrity & Engineering Invariants (R6)", h1_style))
    story.append(Paragraph(
        "To guarantee hierarchical stability and prevent corrupted data states, all mutating operations enforce strict invariant checks:",
        body_style
    ))

    integrity_table_data = [
        [Paragraph("Integrity Constraint", table_header_style), Paragraph("Enforcement Location", table_header_style), Paragraph("Behavioral Outcome & Exception", table_header_style)],
        [Paragraph("<b>Non-Negative Size</b>", table_cell_style), Paragraph("<code>FileNode(name, size)</code><br/><code>setSize(long)</code>", table_code_cell), Paragraph("Rejects negative values immediately with <code>IllegalArgumentException</code>; preserves previous state.", table_cell_style)],
        [Paragraph("<b>Duplicate Sibling Names</b>", table_cell_style), Paragraph("<code>DirectoryNode.addChild()</code>", table_code_cell), Paragraph("Case-insensitively checks siblings; rejects identical names with <code>IllegalArgumentException</code>.", table_cell_style)],
        [Paragraph("<b>Single-Parent Ownership</b>", table_cell_style), Paragraph("<code>DirectoryNode.addChild()</code>", table_code_cell), Paragraph("Prevents node adoption if <code>getParent() != null</code>; throws <code>IllegalStateException</code> unless detached.", table_cell_style)],
        [Paragraph("<b>Cycle & Self-Nesting</b>", table_cell_style), Paragraph("<code>DirectoryNode.addChild()</code>", table_code_cell), Paragraph("Traverses ancestor parent chains backwards; detects self-attachments and ancestor cycles, throwing <code>IllegalStateException</code>.", table_cell_style)],
    ]
    t_int = Table(integrity_table_data, colWidths=[120, 140, 255])
    t_int.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#F1F5F9')),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor('#CBD5E1')),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor('#E2E8F0')),
        ('PADDING', (0,0), (-1,-1), 3.5),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.HexColor('#FFFFFF'), colors.HexColor('#F8FAFC')]),
    ]))
    story.append(t_int)
    story.append(Spacer(1, 4))

    story.append(Paragraph("6. Controlled State Management & Determinism (Section 4)", h1_style))
    story.append(Paragraph("• <b>Controlled State (T7):</b> Visitors support an explicit <code>reset()</code> method clearing internal accumulators (<code>totalSize = 0</code>, <code>matchingPaths.clear()</code>, <code>pathStack.clear()</code>, depth counters). Re-running a visitor produces idempotent, leak-free results.", bullet_style))
    story.append(Paragraph("• <b>Deterministic Outputs (R4):</b> Search paths are constructed relative to the chosen root using <code>/</code> as the separator and sorted lexicographically via <code>Collections.sort()</code> before returning.", bullet_style))

    story.append(Paragraph("7. Automated Verification & Test Results (T1 – T8)", h1_style))
    story.append(Paragraph("All tests execute automatically from project root using <code>mvn test</code> without IDE reliance. All 20 tests pass with zero failures:", body_style))

    test_table_data = [
        [Paragraph("ID", table_header_style), Paragraph("Test Class Name", table_header_style), Paragraph("Verification Scope & Asserted Targets", table_header_style), Paragraph("Status", table_header_style)],
        [Paragraph("<b>T1</b>", table_cell_style), Paragraph("<code>DoubleDispatchTest</code>", table_code_cell), Paragraph("Both element types reach correct overloaded visit methods via double dispatch; null visitor rejected.", table_cell_style), Paragraph("<font color='#166534'><b>PASSED (2/2)</b></font>", table_cell_style)],
        [Paragraph("<b>T2</b>", table_cell_style), Paragraph("<code>TotalSizeVisitorTest</code>", table_code_cell), Paragraph("Size accumulation across 4+ nesting levels is accurate; directories contribute exactly 0 bytes.", table_cell_style), Paragraph("<font color='#166534'><b>PASSED (3/3)</b></font>", table_cell_style)],
        [Paragraph("<b>T3</b>", table_cell_style), Paragraph("<code>SearchVisitorTest</code>", table_code_cell), Paragraph("Combines both filename criterion AND inclusive minimum size threshold (<code>size >= threshold</code>).", table_cell_style), Paragraph("<font color='#166534'><b>PASSED (1/1)</b></font>", table_cell_style)],
        [Paragraph("<b>T4</b>", table_cell_style), Paragraph("<code>SearchVisitorTest</code>", table_code_cell), Paragraph("Deterministic root-relative paths using '/' separator, sorted lexicographically; custom Predicates.", table_cell_style), Paragraph("<font color='#166534'><b>PASSED (2/2)</b></font>", table_cell_style)],
        [Paragraph("<b>T5</b>", table_cell_style), Paragraph("<code>StatisticsVisitorTest</code>", table_code_cell), Paragraph("Collects accurate file count, directory count (including root), and maximum depth (root depth = 0).", table_cell_style), Paragraph("<font color='#166534'><b>PASSED (2/2)</b></font>", table_cell_style)],
        [Paragraph("<b>T6</b>", table_cell_style), Paragraph("<code>SearchVisitorTest</code><br/><code>StatisticsVisitorTest</code>", table_code_cell), Paragraph("Empty directories, 0-byte files, and no-match search queries handled gracefully without error.", table_cell_style), Paragraph("<font color='#166534'><b>PASSED (2/2)</b></font>", table_cell_style)],
        [Paragraph("<b>T7</b>", table_cell_style), Paragraph("<code>StatisticsVisitorTest</code>", table_code_cell), Paragraph("Controlled state: invoking <code>reset()</code> ensures repeat executions do not leak or multiply state.", table_cell_style), Paragraph("<font color='#166534'><b>PASSED (1/1)</b></font>", table_cell_style)],
        [Paragraph("<b>T8</b>", table_cell_style), Paragraph("<code>TreeIntegrityTest</code>", table_code_cell), Paragraph("Rejects negative size, blank names, duplicate siblings, multi-parent, and cycles while valid nesting works.", table_cell_style), Paragraph("<font color='#166534'><b>PASSED (7/7)</b></font>", table_cell_style)],
    ]
    t_test = Table(test_table_data, colWidths=[25, 125, 290, 75])
    t_test.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#F1F5F9')),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor('#CBD5E1')),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor('#E2E8F0')),
        ('PADDING', (0,0), (-1,-1), 2.8),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.HexColor('#FFFFFF'), colors.HexColor('#F8FAFC')]),
        ('ALIGN', (3,1), (3,-1), 'CENTER'),
    ]))
    story.append(t_test)

    # ================= PAGE 4 =================
    story.append(PageBreak())
    story.append(Paragraph("8. Deterministic Application Execution & Sample Output", h1_style))
    story.append(Paragraph(
        "Verbatim execution transcript produced by <code>kz.aitu.filesystem.Main</code> demonstrating the full composite hierarchy, visitor operations, state resets, and tree integrity enforcement:",
        body_style
    ))

    raw_output = """================================================================================
   ASTANA IT UNIVERSITY -- SOFTWARE DESIGN PATTERNS (ShP-2216)
   ASSIGNMENT 06: VISITOR PATTERN -- ADVANCED FILE SYSTEM
   Student: Muratbek Akyl | Group: SE 2523
================================================================================

[1] HIERARCHY STRUCTURE INITIALIZED:
+-- [aitu-project-root/]
    |-- pom.xml (2048 bytes)
    |-- README.md (1536 bytes)
    +-- [src/]
        +-- [main/]
            +-- [java/]
                |-- Application.java (4096 bytes)
                |-- Config.java (1024 bytes)
            +-- [resources/]
                |-- application.properties (512 bytes)
                |-- schema.sql (3072 bytes)
        +-- [test/]
            +-- [java/]
                |-- ApplicationTest.java (2560 bytes)
    +-- [docs/]
        |-- architecture.pdf (1048576 bytes)
        |-- report.md (4096 bytes)
    +-- [logs/]
    +-- [data/]
        |-- export.csv (524288 bytes)

[2] TOTAL SIZE VISITATION (R3):
    Subtree Total Size: 1591808 bytes (1554.50 KB / 1.52 MB)

[3] SEARCH VISITATION WITH COMBINED CONSTRAINTS (R4):
    Query A [Criterion: contains '.java', Min Size >= 2000 B]:
      -> src/main/java/Application.java
      -> src/test/java/ApplicationTest.java
    Query B [Criterion: all names, Min Size >= 500,000 B]:
      -> data/export.csv
      -> docs/architecture.pdf
    Query C [Criterion: 'nonexistent_extension', Min Size >= 1 B]:
      (no matches found)

[4] STATISTICS VISITATION (R5 & R8):
    Total Files Visited       : 10
    Total Directories Visited : 10 (includes root directory)
    Maximum Tree Depth        : 4 (root depth = 0)

[5] CONTROLLED STATE & IDEMPOTENT RE-EXECUTION (T7):
    Re-running StatisticsVisitor after explicit reset()...
    After Reset -- Files: 10, Directories: 10, MaxDepth: 4 (No state leakage)

[6] TREE INTEGRITY ENFORCEMENT (R6):
    [PASS] Negative file size rejected: File size cannot be negative: -100
    [PASS] Duplicate sibling name rejected: Duplicate sibling name 'pom.xml' in directory 'aitu-project-root'
    [PASS] Multiple parent ownership rejected: Element 'pom.xml' already belongs to parent 'aitu-project-root'. Detach first.
    [PASS] Cycle detected and rejected: Cycle detected: cannot add ancestor 'aitu-project-root' to directory 'src'
================================================================================"""

    # Dark background preformatted block
    pre_table = Table([[Preformatted(raw_output, code_block_style)]], colWidths=[515])
    pre_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#0F172A')),
        ('LINELEFT', (0,0), (-1,-1), 3.0, colors.HexColor('#3B82F6')),
        ('PADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(pre_table)
    story.append(Spacer(1, 4))

    story.append(Paragraph("9. Individual Defense Preparation Checklist & Submission Details", h1_style))
    
    defense_data = [
        [
            Paragraph("<b>Oral Defense Protocol Checklist:</b><br/>"
                      "1. <b>Dispatch Trace:</b> Traced from <code>element.accept(v)</code> to <code>v.visit(this)</code>.<br/>"
                      "2. <b>Automated Test Execution:</b> Run <code>mvn test</code> (20/20 tests passing in ~1.5s).<br/>"
                      "3. <b>Dynamic Query Mutation:</b> Demonstrated flexible Predicate/size filtering.<br/>"
                      "4. <b>Architectural Defense:</b> Articulated operation vs element extensibility trade-off.", table_cell_style),
            Paragraph("<b>Repository & Build Information:</b><br/>"
                      "• <b>GitHub:</b> github.com/muratbek-akyl/SDP_Assignment6<br/>"
                      "• <b>Student:</b> Muratbek Akyl | Group: SE 2523<br/>"
                      "• <b>Test Command:</b> <code>mvn clean test</code><br/>"
                      "• <b>Run Command:</b> <code>mvn compile exec:java</code><br/>"
                      "• <b>Java Version:</b> Java 17 | Maven 3.9+", table_cell_style)
        ]
    ]
    t_def = Table(defense_data, colWidths=[260, 255])
    t_def.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F8FAFC')),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor('#CBD5E1')),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor('#E2E8F0')),
        ('PADDING', (0,0), (-1,-1), 4),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
    ]))
    story.append(t_def)

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Academic PDF report generated successfully: {filename}")

if __name__ == "__main__":
    build_pdf()
