/*∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗*
  @file: AvLTree.java
  @description: This file is the generic AVL tree implementation
  @author: Dr. Samuel Cho, Calvin Malaney
  @date: October 21, 2025
∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗*/

// CONSTRUCTION: with no initializer
//
// ******************PUBLIC OPERATIONS*********************
// void insert( x )       --> Insert x
// void remove( x )       --> Remove x (unimplemented)
// boolean contains( x )  --> Return true if x is present
// boolean remove( x )    --> Return true if x was present
// Comparable findMin( )  --> Return smallest item
// Comparable findMax( )  --> Return largest item
// boolean isEmpty( )     --> Return true if empty; else false
// void makeEmpty( )      --> Remove all items
// void printTree( )      --> Print tree in sorted order
// ******************ERRORS********************************
// Throws UnderflowException as appropriate

/**
 * Implements an AVL tree.
 * Note that all "matching" is based on the compareTo method.
 */
public class AvLTree<AnyType extends Comparable<? super AnyType>> {
    /**
     * Construct the tree.
     */
    public AvLTree( ) {
        root = null;
    }

    /**
     * Insert into the tree; duplicates are ignored.
     * @param x the item to insert.
     */
    public void insert( AnyType x ) {
        root = insert( x, root );
    }

    /**
     * Remove from the tree. Nothing is done if x is not found.
     * @param x the item to remove.
     */
    public void remove( AnyType x ) {
        root = remove( x, root );
    }

    /**
     * Internal method to remove from a subtree.
     * @param x the item to remove.
     * @param t the node that roots the subtree.
     * @return the new root of the subtree.
     */
    private AVlNode<AnyType> remove(AnyType x, AVlNode<AnyType> t ) {
        if (t == null) return null;

        int compare = x.compareTo(t.element);
        if (compare < 0) {
            t.left = remove( x, t.left );
        } else if (compare > 0) {
            t.right = remove( x, t.right );
        } else {
            // found node to remove
            if (t.left != null && t.right != null) {
                // replace with smallest in right subtree
                AVlNode<AnyType> min = findMin( t.right );
                t.element = min.element;
                t.right = remove(t.element, t.right);
            } else {
                // one child or none
                t = (t.left != null) ? t.left : t.right;
            }
        }
        // rebalance the tree on the way up
        return balance( t );
    }

    /**
     * Find the smallest item in the tree.
     * @return smallest item or null if empty.
     */
    public AnyType findMin( ) {
        if( isEmpty( ) )
            throw new UnderflowException( );
        return findMin( root ).element;
    }

    /**
     * Find the largest item in the tree.
     * @return the largest item of null if empty.
     */
    public AnyType findMax( ) {
        if( isEmpty( ) )
            throw new UnderflowException( );
        return findMax( root ).element;
    }

    /**
     * Find an item in the tree.
     * @param x the item to search for.
     * @return true if x is found.
     */
    public boolean contains( AnyType x ) {
        return contains( x, root );
    }

    /**
     * Make the tree logically empty.
     */
    public void makeEmpty( ) {
        root = null;
    }

    /**
     * Test if the tree is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty( ) {
        return root == null;
    }

    /**
     * Print the tree contents in sorted order.
     */
    public void printTree( ) {
        if( isEmpty( ) )
            System.out.println( "Empty tree" );
        else
            printTree( root );
    }

    private static final int ALLOWED_IMBALANCE = 1;

    // Assume t is either balanced or within one of being balanced
    private AVlNode<AnyType> balance(AVlNode<AnyType> t ) {
        if (t == null) return null;

        if( height(t.left) - height(t.right) > ALLOWED_IMBALANCE) {
            // left imbalance
            if (height(t.left.left) >= height(t.left.right)) {
                t = rotateWithLeftChild(t); // LL - sg. left rotation
            } else {
                t = doubleWithLeftChild(t); // LR - db. left-right rotation
            }
        } else if (height(t.right) - height(t.left) > ALLOWED_IMBALANCE) {
            // right heavy
            if (height(t.right.right) >= height(t.right.left))  {
                t = rotateWithRightChild(t); // RR - sg. right rotation
            } else {
                t = doubleWithRightChild(t); // RL - db. right-left rotation
            }
        } else {
            // already balanced - update height
            t.height = Math.max(height(t.left), height(t.right)) + 1;
        }
        return t;
    }

    /**
     * Public test hook: walks the tree and verifies AVL invariants.
     * Side effect: prints a message if a violation is detected.
     */
    public void checkBalance( ) {
        checkBalance( root );
    }

    /**
     * Recursively checks that:
     *  (1) Each node’s stored height equals max(leftHeight, rightHeight) + 1, and
     *  (2) The AVL balance condition holds: |height(left) − height(right)| ≤ 1.
     *
     * Returns the computed height of subtree t (using the class convention that null has height −1).
     * This return value is used by the parent call to validate its own height.
     */
    private int checkBalance( AVlNode<AnyType> t ) {
        if( t == null )
            return -1;

        if( t != null ) {
            int hl = checkBalance( t.left );
            int hr = checkBalance( t.right );
            if( Math.abs( height( t.left ) - height( t.right ) ) > 1 ||
                    height( t.left ) != hl || height( t.right ) != hr )
                System.out.println( "OOPS!!" );
        }

        return height( t );
    }

    /**
     * Internal method to insert into a subtree.
     * @param x the item to insert.
     * @param t the node that roots the subtree.
     * @return the new root of the subtree.
     */
    private AVlNode<AnyType> insert(AnyType x, AVlNode<AnyType> t ) {
        if (t == null) {
            return new AVlNode<>( x );
        }

        int compare = x.compareTo( t.element );
        if ( compare < 0 ) {
            t.left = insert( x, t.left );
        } else if ( compare > 0 ) {
            t.right = insert( x, t.right );
        } else {
            // duplicate - ignore it
            return t;
        }

        return balance( t );
    }

    /**
     * Internal method to find the smallest item in a subtree.
     * @param t the node that roots the tree.
     * @return node containing the smallest item.
     */
    private AVlNode<AnyType> findMin(AVlNode<AnyType> t ) {
        if (t == null) return null;
        while (t.left != null) t = t.left;
        return t;
    }

    /**
     * Internal method to find the largest item in a subtree.
     * @param t the node that roots the tree.
     * @return node containing the largest item.
     */
    private AVlNode<AnyType> findMax(AVlNode<AnyType> t ) {
        if (t == null) return null;
        while (t.right != null) t = t.right;
        return t;
    }

    /**
     * Internal method to find an item in a subtree.
     * @param x is item to search for.
     * @param t the node that roots the tree.
     * @return true if x is found in subtree.
     */
    private boolean contains( AnyType x, AVlNode<AnyType> t ) {
        while (t != null) {
            int compare = x.compareTo(t.element);
            if (compare < 0) t = t.left;
            else if (compare > 0) t = t.right;
            else return true;
        }
        return false;
    }

    /**
     * Internal method to print a subtree in (sorted) order.
     * @param t the node that roots the tree.
     */
    private void printTree( AVlNode<AnyType> t ) {
        if (t == null) return;
        printTree(t.left);
        System.out.println(t.element);
        printTree(t.right);
    }

    /**
     * Return the height of node t, or -1, if null.
     */
    private int height( AVlNode<AnyType> t ) {
        return t == null ? -1 : t.height;
    }

    /**
     * Rotate binary tree node with left child.
     * For AVL trees, this is a single rotation for case 1. (LL)
     * Update heights, then return new root.
     */
    private AVlNode<AnyType> rotateWithLeftChild(AVlNode<AnyType> k2 ) {
        AVlNode<AnyType> k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;

        // update heights - child first, then parent
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k1.height = Math.max(height(k1.left), height(k1.right)) + 1;

        return k1;
    }

    /**
     * Rotate binary tree node with right child.
     * For AVL trees, this is a single rotation for case 4.
     * Update heights, then return new root.
     */
    private AVlNode<AnyType> rotateWithRightChild(AVlNode<AnyType> k1 ) {
        AVlNode<AnyType> k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;

        // update heights
        k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;

        return k2;
    }

    /**
     * Double rotate binary tree node: first left child
     * with its right child; then node k3 with new left child.
     * For AVL trees, this is a double rotation for case 2.
     * Update heights, then return new root.
     */
    private AVlNode<AnyType> doubleWithLeftChild(AVlNode<AnyType> k3 ) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    /**
     * Double rotate binary tree node: first right child
     * with its left child; then node k1 with new right child.
     * For AVL trees, this is a double rotation for case 3.
     * Update heights, then return new root.
     */
    private AVlNode<AnyType> doubleWithRightChild(AVlNode<AnyType> k1 ) {
        k1.right = rotateWithLeftChild(k1.right);
        return rotateWithRightChild(k1);
    }

    private static class AVlNode<AnyType> {
        // Constructors
        AVlNode(AnyType theElement ) {
            this( theElement, null, null );
        }

        AVlNode(AnyType theElement, AVlNode<AnyType> lt, AVlNode<AnyType> rt ) {
            element  = theElement;
            left     = lt;
            right    = rt;
            height   = 0;
        }

        AnyType           element;      // The data in the node
        AVlNode<AnyType> left;         // Left child
        AVlNode<AnyType> right;        // Right child
        int               height;       // Height
    }

    /** The tree root. */
    private AVlNode<AnyType> root;
}
