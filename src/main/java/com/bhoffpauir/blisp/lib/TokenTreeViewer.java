package com.bhoffpauir.blisp.lib;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A viewer class for displaying tokens as a hierarchical tree structure.
 * This class extends {@link TreeViewer} and is designed to visualize
 * a list of tokens, often representing source code or expressions, in
 * a tree format. The tree is structured based on parentheses and tokens
 * in the provided list, where each token and parenthesis level gets
 * represented as nodes in the tree.
 * 
 * <p>
 * The viewer provides the following features:
 * <ul>
 *   <li>A tree structure for tokens with nested parentheses</li>
 *   <li>Automatic initialization of tree expansion after creation</li>
 *   <li>Support for a customizable title for the tree viewer</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Usage example:
 * </p>
 * <pre>
 * List&lt;Object&gt; tokens = List.of("(", "print", "(", "+", "2", "2", ")", "\"hi\"", ")");
 * TokenTreeViewer viewer = new TokenTreeViewer(tokens);
 * viewer.showViewer();
 * </pre>
 * 
 * @see TreeViewer
 */
@SuppressWarnings("serial")
public class TokenTreeViewer extends TreeViewer {
	private final List<Object> tokens;
	
	/**
     * Constructs a new {@link TokenTreeViewer} with a default title and the specified list of tokens.
     *
     * @param tokens The list of tokens to be displayed in the tree.
     */
	public TokenTreeViewer(List<Object> tokens) {
		this("Token Tree View", tokens);
	}
	
	/**
     * Constructs a new {@link TokenTreeViewer} with a custom title and the specified list of tokens.
     *
     * @param title The title for the tree viewer window.
     * @param tokens The list of tokens to be displayed in the tree.
     */
	public TokenTreeViewer(String title, List<Object> tokens) {
		super(title);
		this.tokens = tokens;
		init();
		expandAllNodes();
	}

	/**
     * Creates the root node for the tree, which represents the list of tokens.
     * This method is called from the base {@link TreeViewer} class.
     *
     * @return The root node of the token tree.
     */
	@Override
	protected DefaultMutableTreeNode createRoot() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Tokens");
		buildTree(root, tokens, 0);
		return root;
	}

	/**
     * Recursively builds the tree structure based on the given tokens. Parentheses are treated as
     * separate child nodes, and individual tokens are added as leaves.
     *
     * @param parent The parent node to which the current token will be added.
     * @param tokens The list of tokens to process.
     * @param index The current position in the list of tokens to process.
     * @return The updated index after the tokens have been processed.
     */
	private int buildTree(DefaultMutableTreeNode parent, List<Object> tokens, int index) {
		while (index < tokens.size()) {
            Object token = tokens.get(index);
            if (token.equals("(")) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode("()");
                parent.add(child);
                index = buildTree(child, tokens, index + 1);
            } else if (token.equals(")")) {
                return index + 1;
            } else {
                parent.add(new DefaultMutableTreeNode(token));
                index++;
            }
        }
        return index;
	}
}
