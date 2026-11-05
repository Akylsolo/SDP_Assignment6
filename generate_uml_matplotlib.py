import matplotlib.pyplot as plt
import matplotlib.patches as patches

# Set high DPI and font
plt.rcParams['font.sans-serif'] = 'DejaVu Sans'
plt.rcParams['font.size'] = 9

fig, ax = plt.subplots(figsize=(12, 7.5), dpi=300)
ax.set_xlim(0, 120)
ax.set_ylim(0, 75)
ax.axis('off')

# Helper to draw a UML class card
def draw_class(ax, x, y, w, h, title, stereotype, attributes, methods, header_bg='#EFF6FF', border_color='#1E40AF', title_color='#1E3A8A'):
    # Card shadow
    shadow = patches.FancyBboxPatch((x + 0.4, y - 0.4), w, h,
                                   boxstyle="round,pad=0.2,rounding_size=1.0",
                                   facecolor='#E2E8F0', edgecolor='none', zorder=1)
    ax.add_patch(shadow)

    # Main card
    card = patches.FancyBboxPatch((x, y), w, h,
                                 boxstyle="round,pad=0.2,rounding_size=1.0",
                                 facecolor='#FFFFFF', edgecolor=border_color, linewidth=1.5, zorder=2)
    ax.add_patch(card)

    # Header background
    header_h = 7.0 if stereotype else 5.5
    header = patches.FancyBboxPatch((x, y + h - header_h), w, header_h,
                                   boxstyle="round,pad=0.2,rounding_size=1.0",
                                   facecolor=header_bg, edgecolor='none', zorder=3)
    ax.add_patch(header)
    
    # Square off bottom of header
    header_rect = patches.Rectangle((x - 0.2, y + h - header_h), w + 0.4, header_h - 1.0,
                                   facecolor=header_bg, edgecolor='none', zorder=3)
    ax.add_patch(header_rect)

    # Header dividing line
    ax.plot([x - 0.2, x + w + 0.2], [y + h - header_h, y + h - header_h], color=border_color, linewidth=1.2, zorder=4)

    # Header text
    if stereotype:
        ax.text(x + w/2, y + h - 2.2, f'<<{stereotype}>>', ha='center', va='center',
                fontsize=8, fontstyle='italic', fontweight='bold', color=title_color, zorder=5)
        ax.text(x + w/2, y + h - 5.0, title, ha='center', va='center',
                fontsize=10.5, fontweight='bold', color='#0F172A', zorder=5)
    else:
        ax.text(x + w/2, y + h - 2.8, title, ha='center', va='center',
                fontsize=10.5, fontweight='bold', color='#0F172A', zorder=5)

    curr_y = y + h - header_h - 2.0

    # Attributes
    if attributes:
        for attr in attributes:
            ax.text(x + 1.2, curr_y, attr, ha='left', va='center', fontsize=8.2, color='#334155', zorder=5)
            curr_y -= 2.2
        # Divider between attributes and methods
        ax.plot([x - 0.2, x + w + 0.2], [curr_y + 0.9, curr_y + 0.9], color='#E2E8F0', linewidth=1.0, zorder=4)
        curr_y -= 1.2

    # Methods
    for meth in methods:
        color = '#1D4ED8' if 'accept' in meth or 'visit' in meth else '#0F172A'
        weight = 'bold' if 'accept' in meth or 'visit' in meth else 'normal'
        ax.text(x + 1.2, curr_y, meth, ha='left', va='center', fontsize=8.2, color=color, fontweight=weight, zorder=5)
        curr_y -= 2.2

# 1. Element Interface
draw_class(ax, 6, 48, 44, 21,
           'Element', 'interface',
           [],
           ['+ getName(): String',
            '+ getParent(): DirectoryNode',
            '+ setParent(parent: DirectoryNode): void',
            '+ accept(visitor: Visitor): void'],
           header_bg='#ECFDF5', border_color='#059669', title_color='#047857')

# 2. FileNode Class
draw_class(ax, 4, 15, 23, 24,
           'FileNode', None,
           ['- name: String',
            '- size: long',
            '- parent: DirectoryNode'],
           ['+ getSize(): long',
            '+ setSize(size: long): void',
            '+ getName(): String',
            '+ getParent(): DirectoryNode',
            '+ accept(v: Visitor): void'],
           header_bg='#F8FAFC', border_color='#475569', title_color='#1E293B')

# 3. DirectoryNode Class
draw_class(ax, 30, 12, 26, 27,
           'DirectoryNode', None,
           ['- name: String',
            '- parent: DirectoryNode',
            '- children: List<Element>'],
           ['+ getChildren(): List<Element>',
            '+ addChild(child: Element): void',
            '+ removeChild(child: Element): bool',
            '+ findChild(name: String): Opt',
            '+ getName(): String',
            '+ accept(v: Visitor): void'],
           header_bg='#F8FAFC', border_color='#475569', title_color='#1E293B')

# 4. Visitor Interface
draw_class(ax, 68, 50, 46, 18,
           'Visitor', 'interface',
           [],
           ['+ visit(file: FileNode): void',
            '+ visit(directory: DirectoryNode): void',
            '// Overloaded double dispatch methods'],
           header_bg='#EFF6FF', border_color='#2563EB', title_color='#1D4ED8')

# 5. TotalSizeVisitor
draw_class(ax, 62, 26, 26, 19,
           'TotalSizeVisitor', None,
           ['- totalSize: long'],
           ['+ visit(f: FileNode): void',
            '+ visit(d: DirectoryNode): void',
            '+ getTotalSize(): long',
            '+ reset(): void'],
           header_bg='#F8FAFC', border_color='#475569', title_color='#1E293B')

# 6. SearchVisitor
draw_class(ax, 91, 23, 26, 22,
           'SearchVisitor', None,
           ['- criterion: Predicate<String>',
            '- minSizeThreshold: long',
            '- matchingPaths: List<String>',
            '- pathStack: Deque<String>'],
           ['+ visit(f: FileNode): void',
            '+ visit(d: DirectoryNode): void',
            '+ getMatchingPaths(): List',
            '+ reset(): void'],
           header_bg='#F8FAFC', border_color='#475569', title_color='#1E293B')

# 7. StatisticsVisitor (R8 Extensibility)
draw_class(ax, 72, 2, 36, 18,
           'StatisticsVisitor (R8 Extension)', None,
           ['- fileCount, dirCount, maxDepth: int'],
           ['+ visit(f: FileNode): void | visit(d: DirNode): void',
            '+ getFileCount(), getDirectoryCount(): int',
            '+ getMaxDepth(): int [root depth = 0]',
            '+ reset(): void'],
           header_bg='#F5F3FF', border_color='#7C3AED', title_color='#5B21B6')

# Realization Arrows (Dashed with triangular head)
# FileNode -> Element
ax.annotate('', xy=(20, 48), xytext=(15, 39),
            arrowprops=dict(arrowstyle="-|>", linestyle="--", color="#059669", lw=1.5, mutation_scale=15))

# DirectoryNode -> Element
ax.annotate('', xy=(34, 48), xytext=(38, 39),
            arrowprops=dict(arrowstyle="-|>", linestyle="--", color="#059669", lw=1.5, mutation_scale=15))

# Visitors -> Visitor Interface
ax.annotate('', xy=(78, 50), xytext=(75, 45),
            arrowprops=dict(arrowstyle="-|>", linestyle="--", color="#2563EB", lw=1.5, mutation_scale=15))
ax.annotate('', xy=(95, 50), xytext=(100, 45),
            arrowprops=dict(arrowstyle="-|>", linestyle="--", color="#2563EB", lw=1.5, mutation_scale=15))
ax.annotate('', xy=(88, 50), xytext=(88, 20),
            arrowprops=dict(arrowstyle="-|>", linestyle="--", color="#7C3AED", lw=1.5, mutation_scale=15))

# Double Dispatch arrows
# Element.accept -> Visitor
ax.annotate('', xy=(68, 58), xytext=(50, 58),
            arrowprops=dict(arrowstyle="->", linestyle=":", color="#2563EB", lw=2, mutation_scale=12))
ax.text(59, 59.5, 'accept(v)', ha='center', va='bottom', fontsize=8.5, fontweight='bold', color='#1D4ED8')

# Visitor.visit -> Element
ax.annotate('', xy=(50, 54), xytext=(68, 54),
            arrowprops=dict(arrowstyle="->", linestyle=":", color="#059669", lw=2, mutation_scale=12))
ax.text(59, 52, 'v.visit(this)', ha='center', va='top', fontsize=8.5, fontweight='bold', color='#047857')

# Composite association: DirectoryNode contains children: List<Element>
ax.plot([56, 58, 58, 50], [25, 25, 62, 62], color='#1E293B', lw=1.3)
# Diamond on DirectoryNode
diamond = patches.Polygon([[56, 25], [54.5, 26], [53, 25], [54.5, 24]], closed=True,
                          facecolor='#1E293B', edgecolor='#1E293B', zorder=5)
ax.add_patch(diamond)
ax.text(59, 44, 'children 0..*', rotation=-90, ha='center', va='center', fontsize=7.5, fontweight='bold', color='#334155')

# Title annotation at top
ax.text(60, 73, 'Visitor Behavioral Pattern — Advanced File System Class Diagram',
        ha='center', va='center', fontsize=13, fontweight='bold', color='#0F172A')
ax.text(60, 71, 'Astana IT University | ShP-2216 Software Design Patterns | Author: Muratbek Akyl (SE 2523)',
        ha='center', va='center', fontsize=9, color='#64748B')

plt.tight_layout()
plt.savefig('uml_diagram.png', dpi=300, bbox_inches='tight')
print("uml_diagram.png generated successfully at 300 DPI.")
