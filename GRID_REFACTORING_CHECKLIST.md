# Grid.java Refactoring - Implementation Checklist

This checklist provides step-by-step instructions for implementing the Grid refactoring. Follow each phase sequentially and validate before proceeding.

## Prerequisites

- [ ] Read GRID_REFACTORING_SUMMARY.md
- [ ] Read GRID_REFACTORING_PROPOSAL.md (at least Implementation Plan section)
- [ ] Review GRID_REFACTORING_EXAMPLES.md (for code patterns)
- [ ] Understand Grid.java structure
- [ ] Ensure tests can be run: `mvn clean test` or equivalent
- [ ] Create feature branch: `git checkout -b refactor/grid-component-extraction`

---

## Phase 1: Extract GridAccessibilitySupport (Day 1-2)

### Step 1.1: Create GridAccessibilitySupport Class

- [ ] Create file: `GridAccessibilitySupport.java` in `org.eclipse.nebula.widgets.grid` package
- [ ] Make class package-private (no `public` modifier)
- [ ] Add constructor accepting `Grid` parameter
- [ ] Add copyright header (copy from Grid.java)

```java
package org.eclipse.nebula.widgets.grid;

/**
 * Package-private helper class managing accessibility features for Grid.
 */
class GridAccessibilitySupport {
    private final Grid grid;
    
    GridAccessibilitySupport(Grid grid) {
        this.grid = grid;
    }
}
```

### Step 1.2: Extract initAccessible() Method

- [ ] Copy `initAccessible()` method from Grid.java (around line 9432-9785)
- [ ] Paste into GridAccessibilitySupport as `initialize()` method
- [ ] Move accessibility constants (ACC_*) from Grid to GridAccessibilitySupport
- [ ] Update method to use `grid` reference where needed
- [ ] Extract helper methods as needed

### Step 1.3: Update Grid.java

- [ ] Add field: `private GridAccessibilitySupport accessibilitySupport;`
- [ ] In constructor or init, create instance: `accessibilitySupport = new GridAccessibilitySupport(this);`
- [ ] Replace `initAccessible()` body with delegation: `accessibilitySupport.initialize();`
- [ ] Remove moved constants from Grid.java

### Step 1.4: Compile and Test

- [ ] Compile: `mvn clean compile`
- [ ] Fix any compilation errors
- [ ] Run Grid tests: `mvn test -Dtest=Grid_Test`
- [ ] Run all grid widget tests: `mvn test`
- [ ] Verify all tests pass

### Step 1.5: Commit

- [ ] Review changes: `git diff`
- [ ] Stage files: `git add GridAccessibilitySupport.java Grid.java`
- [ ] Commit: `git commit -m "Extract GridAccessibilitySupport from Grid"`
- [ ] Verify: Grid.java reduced by ~350-400 lines

---

## Phase 1: Extract GridItemOperations (Day 2-3)

### Step 2.1: Create GridItemOperations Class

- [ ] Create file: `GridItemOperations.java` in `org.eclipse.nebula.widgets.grid` package
- [ ] Make class package-private
- [ ] Add constructor accepting `Grid` parameter
- [ ] Initialize collections: `items`, `rootItems`

```java
package org.eclipse.nebula.widgets.grid;

import java.util.ArrayList;
import java.util.List;

class GridItemOperations {
    private final Grid grid;
    private final List<GridItem> items = new ArrayList<>();
    private final List<GridItem> rootItems = new ArrayList<>();
    private int itemHeight = 1;
    private boolean userModifiedItemHeight = false;
    
    GridItemOperations(Grid grid) {
        this.grid = grid;
    }
}
```

### Step 2.2: Move Item Fields

- [ ] Move from Grid.java to GridItemOperations:
  - `List<GridItem> items`
  - `List<GridItem> rootItems`
  - `int itemHeight`
  - `boolean userModifiedItemHeight`

### Step 2.3: Extract Item Methods

Move and adapt these methods from Grid to GridItemOperations:

- [ ] `getItem(int)` → delegate from Grid
- [ ] `getItem(Point)` → keep in Grid (needs rendering info)
- [ ] `getItems()` → delegate from Grid
- [ ] `getItemCount()` → delegate from Grid
- [ ] `getRootItem(int)` → delegate from Grid
- [ ] `getRootItems()` → delegate from Grid
- [ ] `getRootItemCount()` → delegate from Grid
- [ ] `indexOf(GridItem)` → delegate from Grid
- [ ] `remove(int)` → delegate from Grid
- [ ] `remove(int, int)` → delegate from Grid
- [ ] `remove(int[])` → delegate from Grid
- [ ] `removeAll()` → delegate from Grid
- [ ] `disposeAllItems()` → delegate from Grid
- [ ] `clearItems()` → delegate from Grid
- [ ] `getNextVisibleItem()` → delegate from Grid
- [ ] `getPreviousVisibleItem()` → delegate from Grid
- [ ] `getItemHeight()` → delegate from Grid
- [ ] `setItemHeight(int)` → delegate from Grid

### Step 2.4: Add Internal Methods

- [ ] `addItem(GridItem)` - called by GridItem constructor
- [ ] `removeItem(GridItem)` - called by GridItem.dispose()
- [ ] `getItemsList()` - for internal Grid access
- [ ] `getRootItemsList()` - for internal Grid access

### Step 2.5: Update Grid.java

- [ ] Add field: `private GridItemOperations itemOperations;`
- [ ] In constructor: `itemOperations = new GridItemOperations(this);`
- [ ] Update all item methods to delegate
- [ ] Update GridItem class if it directly accesses items list

### Step 2.6: Update GridItem.java

- [ ] Change `grid.items.add(this)` to `grid.itemOperations.addItem(this)`
- [ ] Change `grid.items.remove(this)` to `grid.itemOperations.removeItem(this)`
- [ ] Or add package accessor methods in Grid

### Step 2.7: Compile and Test

- [ ] Compile: `mvn clean compile`
- [ ] Fix any compilation errors
- [ ] Run Grid tests: `mvn test -Dtest=Grid_Test`
- [ ] Run GridItem tests: `mvn test -Dtest=GridItem_Test`
- [ ] Run all tests: `mvn test`
- [ ] Verify all tests pass

### Step 2.8: Commit

- [ ] Review changes: `git diff`
- [ ] Stage files: `git add GridItemOperations.java Grid.java GridItem.java`
- [ ] Commit: `git commit -m "Extract GridItemOperations from Grid"`
- [ ] Verify: Grid.java reduced by ~300-350 lines

---

## Phase 1: Validation

### Milestone: Phase 1 Complete

- [ ] Total lines removed from Grid.java: ~650-750 lines
- [ ] New files created: 2 (GridAccessibilitySupport, GridItemOperations)
- [ ] All tests passing: `mvn test`
- [ ] No public API changes
- [ ] Grid.java now ~9,700-9,800 lines

### Code Review

- [ ] Review all changes: `git diff origin/main`
- [ ] Check for code quality issues
- [ ] Verify JavaDoc is complete
- [ ] Ensure no debug code or TODOs left
- [ ] Run linter if available

### Performance Check (Optional)

- [ ] Run performance tests if available
- [ ] Check memory usage hasn't increased significantly
- [ ] Verify no noticeable slowdown in UI

### Commit Phase 1

- [ ] Push branch: `git push origin refactor/grid-component-extraction`
- [ ] Create PR with Phase 1 changes
- [ ] Get code review approval
- [ ] Merge if approved OR proceed to Phase 2

---

## Phase 2: Extract GridColumnOperations (Day 3-4)

### Step 3.1: Create GridColumnOperations Class

- [ ] Create file: `GridColumnOperations.java` in package
- [ ] Make class package-private
- [ ] Add constructor accepting `Grid` parameter
- [ ] Initialize collections: `columns`, `displayOrderedColumns`

### Step 3.2: Move Column Fields

- [ ] Move from Grid.java to GridColumnOperations:
  - `List<GridColumn> columns`
  - `List<GridColumn> displayOrderedColumns`
  - `GridColumnGroup[] columnGroups`

### Step 3.3: Extract Column Methods

Move and delegate these methods:

- [ ] `getColumn(int)`
- [ ] `getColumn(Point)` - may need to keep in Grid
- [ ] `getColumns()`
- [ ] `getColumnCount()`
- [ ] `indexOf(GridColumn)`
- [ ] `getColumnOrder()`
- [ ] `setColumnOrder(int[])`
- [ ] `clearDisplayOrderedCache()`
- [ ] `getColumnGroup(int)`
- [ ] `getColumnGroups()`
- [ ] `getColumnGroupCount()`
- [ ] `getNextVisibleColumn()`
- [ ] `getPreviousVisibleColumn()`

### Step 3.4: Add Internal Methods

- [ ] `addColumn(GridColumn)`
- [ ] `removeColumn(GridColumn)`
- [ ] `addColumnGroup(GridColumnGroup)`
- [ ] `removeColumnGroup(GridColumnGroup)`
- [ ] `getColumnsList()`
- [ ] `getDisplayOrderedColumns()`

### Step 3.5: Update Grid.java

- [ ] Add field: `private GridColumnOperations columnOperations;`
- [ ] Initialize in constructor
- [ ] Update all methods to delegate
- [ ] Update places that directly access columns

### Step 3.6: Update GridColumn.java

- [ ] Update constructor/dispose to use columnOperations
- [ ] Add accessor methods if needed

### Step 3.7: Update GridColumnGroup.java

- [ ] Update constructor/dispose to use columnOperations
- [ ] Add accessor methods if needed

### Step 3.8: Compile and Test

- [ ] Compile: `mvn clean compile`
- [ ] Fix compilation errors
- [ ] Run Grid tests: `mvn test -Dtest=Grid_Test`
- [ ] Run GridColumn tests: `mvn test -Dtest=GridColumn_Test`
- [ ] Run GridColumnGroup tests: `mvn test -Dtest=GridColumnGroup_Test`
- [ ] Run all tests: `mvn test`

### Step 3.9: Commit

- [ ] Stage changes
- [ ] Commit: `git commit -m "Extract GridColumnOperations from Grid"`
- [ ] Verify: Grid.java reduced by ~500-600 lines

---

## Phase 3: Extract GridScrollingSupport (Day 5-6)

### Step 4.1: Create GridScrollingSupport Class

- [ ] Create file: `GridScrollingSupport.java`
- [ ] Make class package-private
- [ ] Add constructor accepting `Grid` parameter

### Step 4.2: Move Scrolling Fields

- [ ] Move from Grid.java:
  - `IScrollBarProxy vScroll`
  - `IScrollBarProxy hScroll`
  - `boolean scrollValuesObsolete`
  - `boolean columnScrolling`
  - `int currentVisibleItems`

### Step 4.3: Extract Scrolling Methods

- [ ] `updateScrollbars()`
- [ ] `getTopIndex()`
- [ ] `setTopIndex(int)`
- [ ] `showItem(GridItem)`
- [ ] `showColumn(GridColumn)`
- [ ] `showSelection()`
- [ ] `onScrollSelection()`
- [ ] `getHScrollSelectionInPixels()`
- [ ] `setScrollValuesObsolete()`
- [ ] Helper methods for scroll calculations

### Step 4.4: Update Grid.java

- [ ] Add field: `private GridScrollingSupport scrollingSupport;`
- [ ] Initialize in constructor
- [ ] Delegate all scrolling methods
- [ ] Update rendering to use scrollingSupport

### Step 4.5: Handle Dependencies

- [ ] GridScrollingSupport needs access to:
  - itemOperations (for item count, height)
  - columnOperations (for column widths)
  - May need to pass these or use Grid reference

### Step 4.6: Compile and Test

- [ ] Compile: `mvn clean compile`
- [ ] Fix compilation errors
- [ ] Run Grid tests: `mvn test -Dtest=Grid_Test`
- [ ] Test scrolling functionality specifically
- [ ] Run all tests: `mvn test`

### Step 4.7: Commit

- [ ] Stage changes
- [ ] Commit: `git commit -m "Extract GridScrollingSupport from Grid"`
- [ ] Verify: Grid.java reduced by ~400-500 lines

---

## Phase 3: Validation

### Milestone: Phase 3 Complete

- [ ] Total lines removed from Grid.java: ~1,850-2,000 lines
- [ ] New files created: 4 components
- [ ] All tests passing
- [ ] Grid.java now ~8,500-8,700 lines
- [ ] 20% reduction achieved ✓

### Final Validation

- [ ] Run complete test suite: `mvn clean test`
- [ ] All tests pass
- [ ] No warnings or errors
- [ ] Performance is acceptable
- [ ] Memory usage unchanged

---

## Phase 4: Documentation (Day 7)

### Update JavaDoc

- [ ] Add JavaDoc for all new classes
- [ ] Update Grid.java class JavaDoc
- [ ] Document package-private design
- [ ] Add @since tags with version

### Create Developer Guide

- [ ] Document new architecture
- [ ] Explain component responsibilities
- [ ] Show how to extend/modify
- [ ] Include architecture diagram

### Update Existing Docs

- [ ] Update any developer documentation
- [ ] Update contribution guidelines if needed
- [ ] Note refactoring in changelog

### Final Commit

- [ ] Commit documentation: `git commit -m "Add documentation for Grid refactoring"`
- [ ] Push: `git push origin refactor/grid-component-extraction`

---

## Final Steps

### Create Pull Request

- [ ] Create PR with all changes
- [ ] Link to this proposal in PR description
- [ ] Summarize changes and benefits
- [ ] Request reviews from team

### Code Review

- [ ] Address review comments
- [ ] Make requested changes
- [ ] Re-run tests after changes

### Merge

- [ ] Get approval from required reviewers
- [ ] Squash commits if requested
- [ ] Merge to main branch
- [ ] Delete feature branch

### Post-Merge

- [ ] Verify CI/CD passes
- [ ] Monitor for any issues
- [ ] Update issue/task tracker
- [ ] Celebrate success! 🎉

---

## Troubleshooting

### Tests Failing

1. Check which tests are failing
2. Review if they test internal implementation
3. Update tests if they rely on internal structure
4. Ensure delegation is correct
5. Check for null pointer exceptions

### Compilation Errors

1. Check package imports
2. Verify method signatures match
3. Check visibility modifiers
4. Ensure fields are accessible (add getters if needed)
5. Update GridItem/GridColumn if they access Grid internals

### Performance Issues

1. Profile before and after
2. Check for unnecessary object creation
3. Optimize hot paths if needed
4. Consider caching if delegation overhead is significant
5. May need to adjust design slightly

### Merge Conflicts

1. Sync with main branch frequently
2. Resolve conflicts carefully
3. Re-run tests after merge
4. Get fresh code review if significant changes

---

## Success Metrics

Track these metrics before and after:

| Metric | Before | After | Target |
|--------|--------|-------|--------|
| Grid.java LOC | 10,567 | ~8,500 | < 8,700 |
| Number of methods in Grid | 237 | ~180 | < 200 |
| Largest method size | 354 | < 250 | < 300 |
| Test success rate | 100% | 100% | 100% |
| Build time | X | X | ±5% |

---

## Notes

- Take your time with each phase
- Don't rush - validate thoroughly
- Ask for help if stuck
- It's OK to adjust the plan if needed
- Document any deviations from plan

## Questions?

Refer to:
- GRID_REFACTORING_PROPOSAL.md for strategy
- GRID_REFACTORING_EXAMPLES.md for code patterns
- GRID_ARCHITECTURE_DIAGRAM.md for architecture

Good luck! 🚀

