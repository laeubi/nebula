# Grid Architecture - Current vs Proposed

## Current Architecture (10,567 lines)

```
┌─────────────────────────────────────────────────────────────────┐
│                          Grid.java                              │
│                       (10,567 lines)                            │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Public API (182 methods)                                │  │
│  │  - Item CRUD: getItem, getItems, remove, clear         │  │
│  │  - Column ops: getColumn, setColumnOrder                │  │
│  │  - Selection: select, deselect, getSelection            │  │
│  │  - Scrolling: setTopIndex, showItem, showColumn         │  │
│  │  - Configuration: setHeaderVisible, setLinesVisible     │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐    │
│  │  Rendering     │ │ Event Handling │ │   Selection    │    │
│  │  (42 methods)  │ │  (47 methods)  │ │  (41 methods)  │    │
│  │  ~1,161 lines  │ │  ~1,265 lines  │ │  ~1,071 lines  │    │
│  └────────────────┘ └────────────────┘ └────────────────┘    │
│                                                                 │
│  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐    │
│  │   Scrolling    │ │    Columns     │ │     Items      │    │
│  │  (11 methods)  │ │  (22 methods)  │ │  (15 methods)  │    │
│  │   ~273 lines   │ │   ~503 lines   │ │   ~224 lines   │    │
│  └────────────────┘ └────────────────┘ └────────────────┘    │
│                                                                 │
│  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐    │
│  │ Accessibility  │ │  Cell Ops      │ │ Configuration  │    │
│  │   (1 method)   │ │  (9 methods)   │ │  (32 methods)  │    │
│  │   354 lines    │ │   ~182 lines   │ │   ~322 lines   │    │
│  └────────────────┘ └────────────────┘ └────────────────┘    │
│                                                                 │
│  196 private fields scattered throughout                       │
│  Complex interdependencies, unclear boundaries                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Problems with Current Architecture

1. **Monolithic**: Single class handling 8+ distinct responsibilities
2. **Hard to Test**: Cannot test components in isolation
3. **Difficult Navigation**: 10K+ lines, hard to find relevant code
4. **High Coupling**: Changes in one area affect multiple unrelated areas
5. **Maintenance Burden**: Understanding any feature requires understanding entire class
6. **Large Methods**: initAccessible (354 lines), onKeyDown (291 lines), onMouseDown (218 lines)

---

## Proposed Architecture (After Phase 1-3)

```
┌─────────────────────────────────────────────────────────────────┐
│                          Grid.java                              │
│                  (Facade - ~8,500 lines)                        │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Public API (182 methods) - UNCHANGED                    │  │
│  │  Delegates to internal components                         │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌─────────────────────────┐                                   │
│  │   Core Responsibilities │                                   │
│  │   - Event Handling      │ ◄────────────┐                   │
│  │   - Rendering/Painting  │              │                   │
│  │   - Mouse/Key Events    │              │                   │
│  └─────────────────────────┘              │                   │
│              │                             │                   │
│              │ delegates to               │                   │
│              ▼                             │ uses              │
│  ┌───────────────────────────────────────┐│                   │
│  │      Component References             ││                   │
│  │  - accessibilitySupport               ││                   │
│  │  - itemOperations                     ││                   │
│  │  - columnOperations                   ││                   │
│  │  - scrollingSupport                   ││                   │
│  └───────────────────────────────────────┘│                   │
└─────────────────────────────────────────────┼───────────────────┘
                                             │
         ┌───────────────────────────────────┼───────────────────┐
         │                                   │                   │
         ▼                                   ▼                   ▼
┌──────────────────┐              ┌──────────────────┐  ┌──────────────────┐
│GridAccessibility │              │ GridItem         │  │ GridColumn       │
│Support           │              │ Operations       │  │ Operations       │
│(package-private) │              │(package-private) │  │(package-private) │
├──────────────────┤              ├──────────────────┤  ├──────────────────┤
│ ~400 lines       │              │ ~350 lines       │  │ ~600 lines       │
│                  │              │                  │  │                  │
│ Responsibilities:│              │ Responsibilities:│  │ Responsibilities:│
│ • initAccessible │              │ • Item CRUD      │  │ • Column CRUD    │
│ • ACC setup      │              │ • Item queries   │  │ • Column order   │
│ • Screen readers │              │ • Visibility     │  │ • Groups mgmt    │
│                  │              │ • Tree relations │  │ • Display order  │
│ Fields:          │              │                  │  │                  │
│ • accessible     │              │ Fields:          │  │ Fields:          │
│ • ACC constants  │              │ • items list     │  │ • columns list   │
│                  │              │ • rootItems list │  │ • displayOrdered │
│ Methods:         │              │ • itemHeight     │  │ • columnGroups   │
│ • initAccessible │              │                  │  │                  │
│ • helper methods │              │ Methods:         │  │ Methods:         │
│                  │              │ • getItem(s)     │  │ • getColumn(s)   │
│                  │              │ • remove         │  │ • setOrder       │
│                  │              │ • indexOf        │  │ • getGroups      │
│                  │              │ • getNext/Prev   │  │ • indexOf        │
└──────────────────┘              └──────────────────┘  └──────────────────┘
                                           │                     │
                                           └─────────┬───────────┘
                                                     │
                                                     ▼
                                         ┌──────────────────────┐
                                         │ GridScrolling        │
                                         │ Support              │
                                         │ (package-private)    │
                                         ├──────────────────────┤
                                         │ ~500 lines           │
                                         │                      │
                                         │ Responsibilities:    │
                                         │ • Scrollbar mgmt     │
                                         │ • Viewport calc      │
                                         │ • showItem/Column    │
                                         │ • Top index          │
                                         │                      │
                                         │ Fields:              │
                                         │ • vScroll            │
                                         │ • hScroll            │
                                         │ • scrollValuesObs    │
                                         │                      │
                                         │ Methods:             │
                                         │ • updateScrollbars   │
                                         │ • setTopIndex        │
                                         │ • showItem           │
                                         │ • showColumn         │
                                         └──────────────────────┘
```

---

## Component Interaction Diagram

```
                                    ┌──────────────┐
                                    │  User/Client │
                                    └──────┬───────┘
                                           │
                                           │ public API calls
                                           ▼
┌────────────────────────────────────────────────────────────────┐
│                         Grid (Facade)                          │
│  - Maintains public API                                        │
│  - Coordinates between components                              │
│  - Handles events and rendering                                │
└────────────────────────────────────────────────────────────────┘
         │              │              │              │
         │ delegates    │ delegates    │ delegates    │ delegates
         ▼              ▼              ▼              ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│GridAccess   │  │GridItem     │  │GridColumn   │  │GridScroll   │
│Support      │  │Operations   │  │Operations   │  │Support      │
└─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘
                       │                  │              │
                       └─────────┬────────┴──────────────┘
                                 │ provides data
                                 ▼
                         ┌──────────────┐
                         │   GridItem   │
                         │  GridColumn  │
                         │  (Entities)  │
                         └──────────────┘
```

---

## Data Flow: Example Operations

### Example 1: setTopIndex(int index)

**Before Refactoring:**
```
Client
  └─> Grid.setTopIndex(index)
       ├─> Validates index
       ├─> Updates internal scroll position
       ├─> Calls updateScrollbars()
       └─> Redraws
```

**After Refactoring:**
```
Client
  └─> Grid.setTopIndex(index)
       └─> scrollingSupport.setTopIndex(index)
            ├─> itemOperations.validateIndex(index)
            ├─> Updates scroll position
            ├─> scrollingSupport.updateScrollbars()
            └─> Grid.redraw() (callback)
```

### Example 2: getItem(int index)

**Before Refactoring:**
```
Client
  └─> Grid.getItem(index)
       ├─> Validates index against items list
       └─> Returns items.get(index)
```

**After Refactoring:**
```
Client
  └─> Grid.getItem(index)
       └─> itemOperations.getItem(index)
            ├─> Validates index
            └─> Returns item from items list
```

### Example 3: setColumnOrder(int[] order)

**Before Refactoring:**
```
Client
  └─> Grid.setColumnOrder(order)
       ├─> Validates order array
       ├─> Updates displayOrderedColumns
       ├─> Clears cache
       ├─> Updates scrollbars
       └─> Redraws
```

**After Refactoring:**
```
Client
  └─> Grid.setColumnOrder(order)
       └─> columnOperations.setColumnOrder(order)
            ├─> Validates order
            ├─> Updates display order
            ├─> Clears cache
            └─> Grid callback to update UI
```

---

## Size Comparison

| Component | Current | After Phase 1 | After Phase 2 | After Phase 3 |
|-----------|---------|---------------|---------------|---------------|
| Grid.java | 10,567 | ~9,700 | ~9,100 | ~8,500 |
| GridAccessibilitySupport | - | ~400 | ~400 | ~400 |
| GridItemOperations | - | ~350 | ~350 | ~350 |
| GridColumnOperations | - | - | ~600 | ~600 |
| GridScrollingSupport | - | - | - | ~500 |
| **Total** | **10,567** | **~10,450** | **~10,450** | **~10,350** |
| **Grid Reduction** | **0%** | **~8%** | **~14%** | **~20%** |

*Note: Total lines remain similar due to some interface/delegation overhead, but Grid.java itself is significantly smaller and more maintainable.*

---

## Benefits Summary

### Maintainability
- ✅ Grid.java reduced by ~2,000 lines (20%)
- ✅ Each component has single, clear responsibility
- ✅ Easier to find and modify specific functionality
- ✅ Reduced cognitive load when working with code

### Testability
- ✅ Components can be unit tested independently
- ✅ Easier to mock dependencies in tests
- ✅ Better test coverage possible
- ✅ Faster test execution (can test components separately)

### Readability
- ✅ Clear separation of concerns
- ✅ Component names indicate responsibility
- ✅ Easier for new developers to understand
- ✅ Better code organization

### Extensibility
- ✅ Easier to add new features to specific components
- ✅ Less risk of breaking unrelated functionality
- ✅ Foundation for future refactoring
- ✅ Better encapsulation of functionality

### No Breaking Changes
- ✅ All public APIs remain unchanged
- ✅ Package-private classes not visible to clients
- ✅ Existing code continues to work
- ✅ Backward compatible

---

## Risk Mitigation

### Approach
1. **Incremental**: One component at a time
2. **Validated**: Run full test suite after each extraction
3. **Reversible**: Can roll back individual changes if issues arise
4. **Low-Risk First**: Start with isolated components (Accessibility, Items)
5. **Well-Documented**: Clear proposal and architecture documentation

### Safety Net
- Comprehensive test suite exists
- Git allows easy rollback
- Package-private changes don't affect external code
- Peer review before merging

---

## Next Steps

1. **Review this proposal** with the team
2. **Get approval** to proceed with Phase 1
3. **Implement** GridAccessibilitySupport extraction
4. **Implement** GridItemOperations extraction
5. **Run tests** and validate
6. **Review results** and decide on Phase 2
7. **Document** learnings and update proposal if needed

