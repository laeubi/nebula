/*******************************************************************************
 * Copyright (c) 2025.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 *    Copilot - demonstration of centered red box feature
 *******************************************************************************/
package org.eclipse.nebula.snippets.gallery;

import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Demonstrates the Gallery widget with a centered red box overlay.
 * This snippet shows the red 50x50 pixel box that is drawn in the center
 * of the Gallery widget, overlaying all gallery items.
 */
public class SnippetRedBoxDemo {

	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setText("Gallery with Red Box - Demonstration");
		shell.setLayout(new FillLayout());
		Gallery gallery = new Gallery(shell, SWT.V_SCROLL | SWT.MULTI);

		// Renderers
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		gr.setMinMargin(2);
		gr.setItemHeight(56);
		gr.setItemWidth(72);
		gr.setAutoMargin(true);
		gallery.setGroupRenderer(gr);

		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		gallery.setItemRenderer(ir);

		// Add sample items to demonstrate the red box overlay
		for (int g = 0; g < 3; g++) {
			GalleryItem group = new GalleryItem(gallery, SWT.NONE);
			group.setText("Group " + g);
			group.setExpanded(true);

			for (int i = 0; i < 30; i++) {
				GalleryItem item = new GalleryItem(group, SWT.NONE);
				item.setText("Item " + i);
			}
		}

		shell.setSize(600, 400);
		shell.open();
		
		System.out.println("Gallery widget is now displaying with a red 50x50 box in the center.");
		System.out.println("Resize the window to see the red box stay centered.");
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}
}
