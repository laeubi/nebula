# Copilot Instructions for Eclipse Nebula

## About This Repository

Eclipse Nebula is a UI widget library built on top of SWT (Standard Widget Toolkit). This repository contains a large set of custom UI widgets that can be used in Java/SWT applications.

## Key Development Principles

### 1. Visual Verification for UI Changes

**IMPORTANT:** Since this is a UI library, visual verification is critical. When making any changes to widgets or their appearance:

- **Always provide before and after screenshots** when submitting a PR
- Screenshots must be actual images (PNG format), not ASCII art
- Use the screenshot template provided below to capture widget state
- Screenshots should clearly show the visual difference your change makes

### 2. Demonstrating New Features

When adding new functionality to widgets:

- **Enhance an existing snippet** or create a new one to demonstrate the feature
- Snippets live in the `examples/org.eclipse.nebula.snippets/src/org/eclipse/nebula/snippets/` directory
- Each widget typically has multiple snippets showing different use cases
- Snippets should be runnable examples that can be executed standalone
- **Do not commit temporary test scripts** - create them in `/tmp` for local testing only

### 3. Screenshot Template

Use this Java template to create screenshots of SWT widgets for PR documentation:

```java
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
// Import your widget class here

public class ScreenshotHelper {
    
    public static void captureWidgetScreenshot(Shell shell, String filename) {
        Display display = shell.getDisplay();
        
        // Ensure the shell is fully rendered
        while (display.readAndDispatch()) {
            // Process pending events
        }
        
        // Get the shell's client area size
        org.eclipse.swt.graphics.Rectangle bounds = shell.getClientArea();
        
        // Create an image with the shell's size
        Image image = new Image(display, bounds.width, bounds.height);
        
        // Create a GC for the image and print the shell onto it
        GC gc = new GC(image);
        shell.print(gc);
        gc.dispose();
        
        // Save the image as PNG
        ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[] { image.getImageData() };
        loader.save(filename, SWT.IMAGE_PNG);
        
        image.dispose();
        
        System.out.println("Screenshot saved to: " + filename);
    }
    
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        
        // Set up your widget here
        // Example:
        // shell.setLayout(new FillLayout());
        // YourWidget widget = new YourWidget(shell, SWT.NONE);
        // Configure widget properties...
        
        shell.setSize(400, 300);
        shell.open();
        
        // Let the UI fully render
        display.readAndDispatch();
        
        // Capture "before" screenshot
        captureWidgetScreenshot(shell, "/tmp/widget-before.png");
        
        // Make your changes to the widget here
        // ...
        
        // Force redraw
        shell.redraw();
        shell.update();
        display.readAndDispatch();
        
        // Capture "after" screenshot
        captureWidgetScreenshot(shell, "/tmp/widget-after.png");
        
        // Keep the window open for manual verification if needed
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        
        display.dispose();
    }
}
```

**Usage Notes:**
- Place this in `/tmp` for testing, don't commit it to the repository
- Modify the widget setup section for your specific widget
- Screenshots will be saved to `/tmp/widget-before.png` and `/tmp/widget-after.png`
- Include these screenshots in your PR description

### 4. Working with Snippets

Snippets are located in `examples/org.eclipse.nebula.snippets/src/org/eclipse/nebula/snippets/`:

**Snippet Structure:**
- Organized by widget type in subdirectories (e.g., `gallery/`, `grid/`, `cdatetime/`)
- Each snippet is a standalone Java class with a `main` method
- Follows naming convention: `Snippet*.java`
- Should include copyright header (see existing snippets for template)

**When to Create/Modify Snippets:**
- **Modify existing snippet:** When enhancing a feature already demonstrated
- **Create new snippet:** When adding a completely new feature or use case
- Snippets should be simple, focused examples showing one feature or pattern
- Include comments explaining non-obvious behavior

**Example Snippet Pattern:**
```java
package org.eclipse.nebula.snippets.yourwidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
// Import your widget

/**
 * Demonstrates [feature description]
 */
public class SnippetYourFeature {
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        
        // Create and configure widget
        // ...
        
        shell.setSize(400, 300);
        shell.open();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        
        display.dispose();
    }
}
```

## Build and Test

- **Build:** Run `mvn verify` from the repository root
- **IDE Setup:** Use the Oomph setup for a complete development environment (see README.md)
- Ensure your changes don't break existing builds or tests
- Test snippets by running them directly to verify they work

## Code Quality

- Follow existing code style and formatting in the repository
- Include EPL 2.0 license headers in new files
- Add javadoc for public APIs
- Keep changes minimal and focused on the specific issue being addressed

## Documentation

- Update widget-specific documentation if changing behavior
- Update README.md or docs/ folder if adding new widgets or major features
- Ensure snippet comments accurately describe what they demonstrate
