package com.bhoffpauir.blisp.lib;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Abstract base class for viewing hierarchical data as a tree structure in a GUI window.
 * This class extends {@link JFrame} and provides a graphical user interface (GUI) for visualizing
 * tree structures using the {@link JTree} component. It includes functionality for expanding and
 * collapsing all nodes in the tree, and the ability for subclasses to define their custom data structure 
 * and root node via the abstract {@link #createRoot()} method.
 * 
 * <p>
 * Subclasses of this class must implement the {@link #createRoot()} method to provide the data 
 * that will be displayed in the tree. Typically, this method will return a root {@link DefaultMutableTreeNode}
 * containing other {@link DefaultMutableTreeNode} children.
 * </p>
 * 
 * <p>
 * This class supports the following actions:
 * </p>
 * <ul>
 *     <li>Expand all tree nodes using the "Expand All" button.</li>
 *     <li>Collapse all tree nodes using the "Collapse All" button.</li>
 *     <li>Display and close the tree viewer window.</li>
 * </ul>
 */
@SuppressWarnings("serial")
public abstract class TreeViewer extends JFrame {
	protected JTree tree;
	
	/**
     * Constructs a new {@code TreeViewer} with the specified title for the JFrame window.
     *
     * @param title The title of the window.
     */
	public TreeViewer(String title) {
		super(title);
	}
	
	/**
     * Initializes the GUI components and prepares the tree viewer for display.
     * This method should be called once by subclasses. It sets up the layout,
     * initializes the {@link JTree} with the root node from {@code createRoot()},
     * and adds action buttons to expand or collapse all nodes in the tree.
     */
	protected void init() {
		// The interpreter should keep running when the frame is closed 
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// Layout for the frame
		getContentPane().setLayout(new BorderLayout());
		
		// Initialize tree with root node & add to JScrollPane
		DefaultMutableTreeNode root = createRoot();
		tree = new JTree(root);
		JScrollPane scrollPane = new JScrollPane(tree);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		// Create expand/collapse buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		JButton expandAllButton = new JButton("Expand All");
		expandAllButton.addActionListener(e -> expandAllNodes());
		
		JButton collapseAllButton = new JButton("Collapse All");
		collapseAllButton.addActionListener(e -> collapseAllNodes());
		
		buttonPanel.add(expandAllButton);
		buttonPanel.add(collapseAllButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		setSize(600, 400);
	}
	
	/**
     * Subclasses must implement this method to create and return the root node for the tree structure.
     * The returned root node will be used as the starting point for the tree, and it should contain all
     * other nodes for the hierarchical structure.
     *
     * @return The root {@link DefaultMutableTreeNode} for the tree.
     */
	protected abstract DefaultMutableTreeNode createRoot();
	
	/**
     * Expands all the nodes in the {@link JTree}.
     * This method traverses the tree and opens each node, making all children visible.
     */
	protected void expandAllNodes() {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}
	
	/**
     * Collapses all the nodes in the {@link JTree}.
     * This method traverses the tree and collapses all nodes, hiding their children.
     */
	protected void collapseAllNodes() {
		for (int row = tree.getRowCount() - 1; row >= 0; row--) {
			tree.collapseRow(row);
		}
	}
	
	/**
     * Displays the tree viewer window, making it visible on the screen.
     */
	public void showViewer() {
		setVisible(true);
	}
	
	/**
     * Closes the tree viewer window, making it invisible and disposing of the frame.
     */
	public void closeViewer() {
		setVisible(false);
		dispose();
	}
}
