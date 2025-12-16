# Grid Refactoring - Detailed Implementation Examples

This document provides concrete examples of how the refactoring will be implemented, showing before and after code structure for key extractions.

---

## Example 1: GridAccessibilitySupport Extraction

### Before: Grid.java (lines ~9432-9785)

```java
// Inside Grid.java - 354 line method
private void initAccessible() {
    final Accessible accessible = getAccessible();
    
    accessible.addAccessibleListener(new AccessibleAdapter() {
        @Override
        public void getName(AccessibleEvent e) {
            // 300+ lines of accessibility initialization
            // Handling various accessibility events
            // Setting up screen reader support
            // ...
        }
        
        @Override
        public void getHelp(AccessibleEvent e) {
            // ...
        }
        
        // Many more event handlers...
    });
    
    accessible.addAccessibleControlListener(new AccessibleControlAdapter() {
        @Override
        public void getRole(AccessibleControlEvent e) {
            // Role determination logic
        }
        
        @Override
        public void getValue(AccessibleControlEvent e) {
            // Value determination logic
        }
        
        // Many more control event handlers...
    });
}
```

### After: Grid.java

```java
// In Grid.java - just a delegation
private GridAccessibilitySupport accessibilitySupport;

// In constructor or initialization
this.accessibilitySupport = new GridAccessibilitySupport(this);
accessibilitySupport.initialize();

// Or even simpler - let the support class handle everything
private void initAccessible() {
    if (accessibilitySupport == null) {
        accessibilitySupport = new GridAccessibilitySupport(this);
    }
    accessibilitySupport.initialize();
}
```

### New File: GridAccessibilitySupport.java

```java
package org.eclipse.nebula.widgets.grid;

import org.eclipse.swt.accessibility.*;

/**
 * Package-private helper class that manages accessibility features for Grid.
 * Handles screen reader support and accessibility events.
 */
class GridAccessibilitySupport {
    
    private static final String ACC_COLUMN_DEFAULT_ACTION = "Click";
    private static final String ACC_ITEM_DEFAULT_ACTION = "Double Click";
    private static final String ACC_ITEM_ACTION_EXPAND = "Expand";
    private static final String ACC_ITEM_ACTION_COLLAPSE = "Collapse";
    private static final String ACC_TOGGLE_BUTTON_NAME = "Toggle Button";
    
    private final Grid grid;
    private Accessible accessible;
    
    /**
     * Creates a new accessibility support instance for the given grid.
     * @param grid the Grid widget to provide accessibility for
     */
    GridAccessibilitySupport(Grid grid) {
        this.grid = grid;
    }
    
    /**
     * Initializes accessibility features for the grid.
     * Sets up accessible listeners and control listeners.
     */
    void initialize() {
        accessible = grid.getAccessible();
        
        accessible.addAccessibleListener(new AccessibleAdapter() {
            @Override
            public void getName(AccessibleEvent e) {
                handleGetName(e);
            }
            
            @Override
            public void getHelp(AccessibleEvent e) {
                handleGetHelp(e);
            }
            
            // Other event handlers...
        });
        
        accessible.addAccessibleControlListener(new AccessibleControlAdapter() {
            @Override
            public void getRole(AccessibleControlEvent e) {
                handleGetRole(e);
            }
            
            @Override
            public void getValue(AccessibleControlEvent e) {
                handleGetValue(e);
            }
            
            // Other control event handlers...
        });
    }
    
    private void handleGetName(AccessibleEvent e) {
        // Extract name determination logic
        // Access grid properties via grid reference
        int childID = e.childID;
        if (childID == ACC.CHILDID_SELF) {
            // Handle grid name
        } else if (childID >= 0) {
            // Handle specific item/cell name
            GridItem item = grid.getItem(childID);
            if (item != null) {
                e.result = item.getText();
            }
        }
    }
    
    private void handleGetRole(AccessibleControlEvent e) {
        // Extract role determination logic
        int childID = e.childID;
        if (childID == ACC.CHILDID_SELF) {
            e.detail = ACC.ROLE_TABLE;
        } else {
            e.detail = ACC.ROLE_TABLECELL;
        }
    }
    
    // Additional helper methods...
}
```

**Benefits of This Extraction:**
- ✅ Removes 354 lines from Grid.java
- ✅ Clear single responsibility: accessibility
- ✅ Easier to test accessibility features
- ✅ Easier to enhance accessibility without touching Grid
- ✅ No public API changes

---

## Example 2: GridItemOperations Extraction

### Before: Grid.java (scattered across ~300-400 lines)

```java
// Inside Grid.java
private final List<GridItem> items = new ArrayList<>();
private final List<GridItem> rootItems = new ArrayList<>();
private int itemHeight = 1;
private boolean userModifiedItemHeight = false;

public GridItem getItem(int index) {
    checkWidget();
    if (index < 0 || index >= items.size()) {
        SWT.error(SWT.ERROR_INVALID_RANGE);
    }
    return items.get(index);
}

public int getItemCount() {
    checkWidget();
    return items.size();
}

public GridItem[] getItems() {
    checkWidget();
    return items.toArray(new GridItem[items.size()]);
}

public int indexOf(GridItem item) {
    checkWidget();
    if (item == null) {
        SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }
    return items.indexOf(item);
}

public void remove(int index) {
    checkWidget();
    if (index < 0 || index >= items.size()) {
        SWT.error(SWT.ERROR_INVALID_RANGE);
    }
    GridItem item = items.get(index);
    item.dispose();
    // Additional cleanup...
}

public void removeAll() {
    checkWidget();
    while (items.size() > 0) {
        items.get(0).dispose();
    }
}

// Similar methods for root items
public int getRootItemCount() {
    checkWidget();
    return rootItems.size();
}

public GridItem[] getRootItems() {
    checkWidget();
    return rootItems.toArray(new GridItem[rootItems.size()]);
}

// Visibility methods
public GridItem getNextVisibleItem(GridItem item) {
    checkWidget();
    // Complex logic to find next visible item...
    // ~37 lines of code
}
```

### After: Grid.java

```java
// In Grid.java
private GridItemOperations itemOperations;

// In constructor
this.itemOperations = new GridItemOperations(this);

// Public methods just delegate
public GridItem getItem(int index) {
    checkWidget();
    return itemOperations.getItem(index);
}

public int getItemCount() {
    checkWidget();
    return itemOperations.getItemCount();
}

public GridItem[] getItems() {
    checkWidget();
    return itemOperations.getItems();
}

public int indexOf(GridItem item) {
    checkWidget();
    return itemOperations.indexOf(item);
}

public void remove(int index) {
    checkWidget();
    itemOperations.remove(index);
}

public void removeAll() {
    checkWidget();
    itemOperations.removeAll();
}

public int getRootItemCount() {
    checkWidget();
    return itemOperations.getRootItemCount();
}

public GridItem[] getRootItems() {
    checkWidget();
    return itemOperations.getRootItems();
}

public GridItem getNextVisibleItem(GridItem item) {
    checkWidget();
    return itemOperations.getNextVisibleItem(item);
}
```

### New File: GridItemOperations.java

```java
package org.eclipse.nebula.widgets.grid;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;

/**
 * Package-private helper class that manages GridItem operations.
 * Handles item CRUD operations, item queries, and visibility calculations.
 */
class GridItemOperations {
    
    private final Grid grid;
    private final List<GridItem> items;
    private final List<GridItem> rootItems;
    private int itemHeight = 1;
    private boolean userModifiedItemHeight = false;
    
    /**
     * Creates a new item operations manager for the given grid.
     * @param grid the Grid widget to manage items for
     */
    GridItemOperations(Grid grid) {
        this.grid = grid;
        this.items = new ArrayList<>();
        this.rootItems = new ArrayList<>();
    }
    
    /**
     * Returns the item at the given index.
     * @param index the index of the item
     * @return the item at the given index
     * @throws IllegalArgumentException if index is out of range
     */
    GridItem getItem(int index) {
        if (index < 0 || index >= items.size()) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        return items.get(index);
    }
    
    /**
     * Returns the total number of items.
     * @return the item count
     */
    int getItemCount() {
        return items.size();
    }
    
    /**
     * Returns all items in the grid.
     * @return array of all items
     */
    GridItem[] getItems() {
        return items.toArray(new GridItem[items.size()]);
    }
    
    /**
     * Returns the index of the given item.
     * @param item the item to find
     * @return the index, or -1 if not found
     * @throws IllegalArgumentException if item is null
     */
    int indexOf(GridItem item) {
        if (item == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        return items.indexOf(item);
    }
    
    /**
     * Removes the item at the given index.
     * @param index the index of the item to remove
     * @throws IllegalArgumentException if index is out of range
     */
    void remove(int index) {
        if (index < 0 || index >= items.size()) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        GridItem item = items.get(index);
        item.dispose();
        // Grid callback for cleanup/redraw
        grid.redraw();
    }
    
    /**
     * Removes all items from the grid.
     */
    void removeAll() {
        while (items.size() > 0) {
            items.get(0).dispose();
        }
        grid.redraw();
    }
    
    /**
     * Returns the number of root items.
     * @return the root item count
     */
    int getRootItemCount() {
        return rootItems.size();
    }
    
    /**
     * Returns all root items.
     * @return array of root items
     */
    GridItem[] getRootItems() {
        return rootItems.toArray(new GridItem[rootItems.size()]);
    }
    
    /**
     * Adds an item to the internal lists.
     * Called by GridItem constructor.
     * @param item the item to add
     */
    void addItem(GridItem item) {
        items.add(item);
        if (item.getParentItem() == null) {
            rootItems.add(item);
        }
    }
    
    /**
     * Removes an item from the internal lists.
     * Called by GridItem.dispose().
     * @param item the item to remove
     */
    void removeItem(GridItem item) {
        items.remove(item);
        rootItems.remove(item);
    }
    
    /**
     * Returns the next visible item after the given item.
     * @param item the reference item
     * @return the next visible item, or null if none
     */
    GridItem getNextVisibleItem(GridItem item) {
        if (item == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        
        int index = indexOf(item);
        if (index == -1) {
            return null;
        }
        
        // Check children first
        if (item.isExpanded() && item.hasChildren()) {
            return item.getItem(0);
        }
        
        // Check siblings
        GridItem parent = item.getParentItem();
        if (parent != null) {
            int parentIndex = parent.indexOf(item);
            if (parentIndex < parent.getItemCount() - 1) {
                return parent.getItem(parentIndex + 1);
            }
            // Check parent's siblings recursively
            return getNextVisibleItem(parent);
        }
        
        // Check root level siblings
        int rootIndex = rootItems.indexOf(item);
        if (rootIndex < rootItems.size() - 1) {
            return rootItems.get(rootIndex + 1);
        }
        
        return null;
    }
    
    /**
     * Returns the previous visible item before the given item.
     * @param item the reference item
     * @return the previous visible item, or null if none
     */
    GridItem getPreviousVisibleItem(GridItem item) {
        if (item == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        
        int index = indexOf(item);
        if (index == -1) {
            return null;
        }
        
        // Check siblings first
        GridItem parent = item.getParentItem();
        if (parent != null) {
            int parentIndex = parent.indexOf(item);
            if (parentIndex > 0) {
                GridItem prevSibling = parent.getItem(parentIndex - 1);
                return getLastVisibleDescendant(prevSibling);
            }
            return parent;
        }
        
        // Check root level siblings
        int rootIndex = rootItems.indexOf(item);
        if (rootIndex > 0) {
            GridItem prevSibling = rootItems.get(rootIndex - 1);
            return getLastVisibleDescendant(prevSibling);
        }
        
        return null;
    }
    
    /**
     * Returns the last visible descendant of the given item.
     * @param item the item to check
     * @return the last visible descendant
     */
    private GridItem getLastVisibleDescendant(GridItem item) {
        if (item.isExpanded() && item.hasChildren()) {
            GridItem lastChild = item.getItem(item.getItemCount() - 1);
            return getLastVisibleDescendant(lastChild);
        }
        return item;
    }
    
    /**
     * Returns the item height.
     * @return the item height in pixels
     */
    int getItemHeight() {
        return itemHeight;
    }
    
    /**
     * Sets the item height.
     * @param height the new item height in pixels
     */
    void setItemHeight(int height) {
        this.itemHeight = height;
        this.userModifiedItemHeight = true;
    }
    
    /**
     * Returns whether the user has modified the item height.
     * @return true if user modified, false if default
     */
    boolean isUserModifiedItemHeight() {
        return userModifiedItemHeight;
    }
    
    /**
     * Provides access to the items list for internal grid operations.
     * @return the items list (internal use only)
     */
    List<GridItem> getItemsList() {
        return items;
    }
    
    /**
     * Provides access to the root items list for internal grid operations.
     * @return the root items list (internal use only)
     */
    List<GridItem> getRootItemsList() {
        return rootItems;
    }
}
```

**Benefits of This Extraction:**
- ✅ Removes ~350 lines from Grid.java
- ✅ Clear single responsibility: item management
- ✅ Easier to test item operations independently
- ✅ Encapsulates item collections
- ✅ No public API changes

---

## Example 3: GridColumnOperations Extraction

### Before: Grid.java (scattered across ~500-700 lines)

```java
// Inside Grid.java
private final List<GridColumn> columns = new ArrayList<>();
private final List<GridColumn> displayOrderedColumns = new ArrayList<>();
private GridColumnGroup[] columnGroups = {};

public GridColumn getColumn(int index) {
    checkWidget();
    if (index < 0 || index >= columns.size()) {
        SWT.error(SWT.ERROR_INVALID_RANGE);
    }
    return columns.get(index);
}

public int getColumnCount() {
    checkWidget();
    return columns.size();
}

public GridColumn[] getColumns() {
    checkWidget();
    return columns.toArray(new GridColumn[columns.size()]);
}

public void setColumnOrder(int[] order) {
    checkWidget();
    // 55 lines of complex logic
    // Validates order array
    // Updates displayOrderedColumns
    // Handles column groups
    // ...
}

public int[] getColumnOrder() {
    checkWidget();
    // Returns current display order
}

// And many more column-related methods...
```

### After: Grid.java

```java
// In Grid.java
private GridColumnOperations columnOperations;

// In constructor
this.columnOperations = new GridColumnOperations(this);

// Public methods delegate
public GridColumn getColumn(int index) {
    checkWidget();
    return columnOperations.getColumn(index);
}

public int getColumnCount() {
    checkWidget();
    return columnOperations.getColumnCount();
}

public GridColumn[] getColumns() {
    checkWidget();
    return columnOperations.getColumns();
}

public void setColumnOrder(int[] order) {
    checkWidget();
    columnOperations.setColumnOrder(order);
    // May need to trigger redraw/scroll update
    updateScrollbars();
    redraw();
}

public int[] getColumnOrder() {
    checkWidget();
    return columnOperations.getColumnOrder();
}
```

### New File: GridColumnOperations.java

```java
package org.eclipse.nebula.widgets.grid;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;

/**
 * Package-private helper class that manages GridColumn operations.
 * Handles column CRUD, ordering, grouping, and visibility calculations.
 */
class GridColumnOperations {
    
    private final Grid grid;
    private final List<GridColumn> columns;
    private final List<GridColumn> displayOrderedColumns;
    private GridColumnGroup[] columnGroups;
    
    /**
     * Creates a new column operations manager for the given grid.
     * @param grid the Grid widget to manage columns for
     */
    GridColumnOperations(Grid grid) {
        this.grid = grid;
        this.columns = new ArrayList<>();
        this.displayOrderedColumns = new ArrayList<>();
        this.columnGroups = new GridColumnGroup[0];
    }
    
    /**
     * Returns the column at the given index.
     * @param index the column index
     * @return the column at the index
     */
    GridColumn getColumn(int index) {
        if (index < 0 || index >= columns.size()) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        return columns.get(index);
    }
    
    /**
     * Returns the number of columns.
     * @return the column count
     */
    int getColumnCount() {
        return columns.size();
    }
    
    /**
     * Returns all columns.
     * @return array of all columns
     */
    GridColumn[] getColumns() {
        return columns.toArray(new GridColumn[columns.size()]);
    }
    
    /**
     * Returns the index of the given column.
     * @param column the column to find
     * @return the index, or -1 if not found
     */
    int indexOf(GridColumn column) {
        if (column == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        return columns.indexOf(column);
    }
    
    /**
     * Returns the current column display order.
     * @return array of column indices in display order
     */
    int[] getColumnOrder() {
        int[] order = new int[columns.size()];
        for (int i = 0; i < displayOrderedColumns.size(); i++) {
            order[i] = columns.indexOf(displayOrderedColumns.get(i));
        }
        return order;
    }
    
    /**
     * Sets the column display order.
     * @param order array of column indices in desired order
     */
    void setColumnOrder(int[] order) {
        if (order == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        
        if (order.length != columns.size()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        
        // Validate order array
        boolean[] seen = new boolean[columns.size()];
        for (int i = 0; i < order.length; i++) {
            if (order[i] < 0 || order[i] >= columns.size()) {
                SWT.error(SWT.ERROR_INVALID_RANGE);
            }
            if (seen[order[i]]) {
                SWT.error(SWT.ERROR_INVALID_ARGUMENT);
            }
            seen[order[i]] = true;
        }
        
        // Update display order
        displayOrderedColumns.clear();
        for (int i = 0; i < order.length; i++) {
            displayOrderedColumns.add(columns.get(order[i]));
        }
        
        // Notify grid of change
        grid.redraw();
    }
    
    /**
     * Returns columns in display order.
     * @return list of columns in display order
     */
    List<GridColumn> getDisplayOrderedColumns() {
        if (displayOrderedColumns.isEmpty() && !columns.isEmpty()) {
            // Initialize display order to match creation order
            displayOrderedColumns.addAll(columns);
        }
        return displayOrderedColumns;
    }
    
    /**
     * Clears the display order cache.
     * Called when columns are added/removed.
     */
    void clearDisplayOrderedCache() {
        displayOrderedColumns.clear();
    }
    
    /**
     * Adds a column to the grid.
     * Called by GridColumn constructor.
     * @param column the column to add
     */
    void addColumn(GridColumn column) {
        columns.add(column);
        displayOrderedColumns.add(column);
    }
    
    /**
     * Removes a column from the grid.
     * Called by GridColumn.dispose().
     * @param column the column to remove
     */
    void removeColumn(GridColumn column) {
        columns.remove(column);
        displayOrderedColumns.remove(column);
    }
    
    /**
     * Returns the column groups.
     * @return array of column groups
     */
    GridColumnGroup[] getColumnGroups() {
        return columnGroups;
    }
    
    /**
     * Returns the column group at the given index.
     * @param index the group index
     * @return the column group
     */
    GridColumnGroup getColumnGroup(int index) {
        if (index < 0 || index >= columnGroups.length) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        return columnGroups[index];
    }
    
    /**
     * Returns the number of column groups.
     * @return the column group count
     */
    int getColumnGroupCount() {
        return columnGroups.length;
    }
    
    /**
     * Adds a column group.
     * Called by GridColumnGroup constructor.
     * @param group the group to add
     */
    void addColumnGroup(GridColumnGroup group) {
        GridColumnGroup[] newGroups = new GridColumnGroup[columnGroups.length + 1];
        System.arraycopy(columnGroups, 0, newGroups, 0, columnGroups.length);
        newGroups[columnGroups.length] = group;
        columnGroups = newGroups;
    }
    
    /**
     * Removes a column group.
     * Called by GridColumnGroup.dispose().
     * @param group the group to remove
     */
    void removeColumnGroup(GridColumnGroup group) {
        GridColumnGroup[] newGroups = new GridColumnGroup[columnGroups.length - 1];
        int j = 0;
        for (int i = 0; i < columnGroups.length; i++) {
            if (columnGroups[i] != group) {
                newGroups[j++] = columnGroups[i];
            }
        }
        columnGroups = newGroups;
    }
    
    /**
     * Returns the next visible column after the given column.
     * @param column the reference column
     * @return the next visible column, or null if none
     */
    GridColumn getNextVisibleColumn(GridColumn column) {
        if (column == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        
        List<GridColumn> orderedCols = getDisplayOrderedColumns();
        int index = orderedCols.indexOf(column);
        if (index == -1) {
            return null;
        }
        
        for (int i = index + 1; i < orderedCols.size(); i++) {
            GridColumn col = orderedCols.get(i);
            if (col.isVisible()) {
                return col;
            }
        }
        
        return null;
    }
    
    /**
     * Returns the previous visible column before the given column.
     * @param column the reference column
     * @return the previous visible column, or null if none
     */
    GridColumn getPreviousVisibleColumn(GridColumn column) {
        if (column == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        
        List<GridColumn> orderedCols = getDisplayOrderedColumns();
        int index = orderedCols.indexOf(column);
        if (index == -1) {
            return null;
        }
        
        for (int i = index - 1; i >= 0; i--) {
            GridColumn col = orderedCols.get(i);
            if (col.isVisible()) {
                return col;
            }
        }
        
        return null;
    }
    
    /**
     * Provides access to the columns list for internal grid operations.
     * @return the columns list (internal use only)
     */
    List<GridColumn> getColumnsList() {
        return columns;
    }
}
```

**Benefits of This Extraction:**
- ✅ Removes ~600 lines from Grid.java
- ✅ Clear single responsibility: column management
- ✅ Encapsulates column ordering complexity
- ✅ Easier to test column operations
- ✅ No public API changes

---

## Summary of Extraction Benefits

### Quantitative Improvements

| Metric | Before | After Phase 1-3 | Improvement |
|--------|--------|-----------------|-------------|
| Grid.java LOC | 10,567 | ~8,500 | -20% |
| Largest method | 354 lines | ~200 lines | -44% |
| Private fields in Grid | 196 | ~140 | -29% |
| Distinct responsibilities | 8+ | ~5 | -38% |

### Qualitative Improvements

1. **Maintainability**: Each component has a single, clear responsibility
2. **Testability**: Components can be tested independently
3. **Readability**: Easier to understand and navigate
4. **Extensibility**: Easier to add features without side effects
5. **Documentation**: Clearer structure makes documentation easier

### No Breaking Changes

- ✅ All public methods remain in Grid.java
- ✅ All method signatures unchanged
- ✅ All behavior unchanged
- ✅ Package-private classes not visible externally
- ✅ Existing tests continue to pass

