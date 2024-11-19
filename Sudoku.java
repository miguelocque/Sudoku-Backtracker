/**
 * Sudoku.java
 * 
 * Implementation of a class that represents a Sudoku puzzle and solves
 * it using recursive backtracking.
 *
 * Computer Science 112, Boston University
 *
 * your name: Miguel Ocque - mocque@bu.edu  
 *
 */

import java.io.*;   // allows us to read from a file
import java.util.*;

public class Sudoku {    
    // The current contents of the cells of the puzzle. 
    private int[][] grid;
    
    /*
     * Indicates whether the value in a given cell is fixed 
     * (i.e., part of the initial configuration).
     * valIsFixed[r][c] is true if the value in the cell 
     * at row r, column c is fixed, and false otherwise.
     */
    private boolean[][] valIsFixed;
    
    /*
     * This 3-D array allows us to determine if a given subgrid (i.e.,
     * a given 3x3 region of the puzzle) already contains a given
     * value.  We use 2 indices to identify a given subgrid:
     *
     *    (0,0)   (0,1)   (0,2)
     *
     *    (1,0)   (1,1)   (1,2)
     * 
     *    (2,0)   (2,1)   (2,2)
     * 
     * For example, subgridHasVal[0][2][5] will be true if the subgrid
     * in the upper right-hand corner already has a 5 in it, and false
     * otherwise.
     */
    private boolean[][][] subgridHasVal;
    
    /*** ADD YOUR ADDITIONAL FIELDS HERE. ***/

    // a 2D boolean array that will determine if a given column has a value or not
    private boolean [][] colHasVal;

    // a 2D boolean array that will determine if a given row has a value or not. 
    private boolean [][] rowHasVal;
    
    /* 
     * Constructs a new Puzzle object, which initially
     * has all empty cells.
     */
    public Sudoku() {
        this.grid = new int[9][9];
        this.valIsFixed = new boolean[9][9];     
        
        /* 
         * Note that the third dimension of the following array is 10,
         * because we need to be able to use the possible values 
         * (1 through 9) as indices.
         */
        this.subgridHasVal = new boolean[3][3][10];        

        /*** INITIALIZE YOUR ADDITIONAL FIELDS HERE. ***/

        // second dimension is 10 for same reason as above
        this.colHasVal = new boolean[9][10];
        this.rowHasVal = new boolean[9][10];

    }
    
    /*
     * Place the specified value in the cell with the specified
     * coordinates, and update the state of the puzzle accordingly.
     */
    public void placeVal(int val, int row, int col) {
        this.grid[row][col] = val;
        this.subgridHasVal[row/3][col/3][val] = true;
        
        /*** UPDATE YOUR ADDITIONAL FIELDS HERE. ***/

        // adds the given value in our current col
        this.colHasVal[col][val] = true;

        // adds the given value in our current col
        this.rowHasVal[row][val] = true;
    }
        
    /*
     * remove the specified value from the cell with the specified
     * coordinates, and update the state of the puzzle accordingly.
     */
    public void removeVal(int val, int row, int col) {
        this.grid[row][col] = 0;
        this.subgridHasVal[row/3][col/3][val] = false;
        
        /*** UPDATE YOUR ADDITIONAL FIELDS HERE. ***/


        // removes the current value in our current col
        this.colHasVal[col][val] = false;

        // removes the current value in our current row
        this.rowHasVal[row][val] = false;
    }  
        
    /*
     * read in the initial configuration of the puzzle from the specified 
     * Scanner, and use that config to initialize the state of the puzzle.  
     * The configuration should consist of one line for each row, with the
     * values in the row specified as integers separated by spaces.
     * A value of 0 should be used to indicate an empty cell.
     * 
     * You should not change this method.
     */
    public void readConfig(Scanner input) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = input.nextInt();
                this.placeVal(val, r, c);
                if (val != 0) {
                    this.valIsFixed[r][c] = true;
                }
            }
            input.nextLine();
        }
    }
                
    /*
     * Displays the current state of the puzzle.
     * You should not change this method.
     */        
    public void printGrid() {
        for (int r = 0; r < 9; r++) {
            this.printRowSeparator();
            for (int c = 0; c < 9; c++) {
                System.out.print("|");
                if (this.grid[r][c] == 0) {
                    System.out.print("   ");
                } else {
                    System.out.print(" " + this.grid[r][c] + " ");
                }
            }
            System.out.println("|");
        }
        this.printRowSeparator();
    }
        
    // A private helper method used by display() 
    // to print a line separating two rows of the puzzle.
    private static void printRowSeparator() {
        for (int i = 0; i < 9; i++) {
            System.out.print("----");
        }
        System.out.println("-");
    }
    
    /*** ADD ANY ADDITIONAL METHODS HERE. ***/


    // helper method that determines if a particular number is allowed to be
    // placed in a specific spot. 
    private boolean isAllowed(int val, int row, int col) {

        // checking 3 things here:
        // 1: the value is not in a given col
        // 2: the value is not in a given row
        // 3: the value is not in a given subgrid.

        //if (this.valIsFixed[row][col] != true) {
            if (this.colHasVal[col][val] != true) {
                if (this.rowHasVal[row][val] != true) {
                    if (this.subgridHasVal[row / 3][col / 3][val] != true) {
                        // if we get here, then we know that none of the 4 conditions are true
                        return true;
                    }
                }
            }
        //}
        // else, if any of the conditions is true, we automatically return false
        return false;
    }

         
    /*
     * This is the key recursive-backtracking method.  Returns true if
     * a solution has already been found, and false otherwise.
     * 
     * Each invocation of the method is responsible for finding the
     * value of a single cell of the puzzle. The parameter n
     * is the number of the cell that a given invocation of the method
     * is responsible for. We recommend that you consider the cells
     * one row at a time, from top to bottom and left to right,
     * which means that they would be numbered as follows:
     *
     *     0  1  2  3  4  5  6  7  8
     *     9 10 11 12 13 14 15 16 17
     *    18 ...
     */
    private boolean solveRB(int n) {

        // if n goes beyond the number of squares, means we've come to a solution
        if(n == 81) {
            return true;
        }

        for (int val = 1; val < 10; val++) {
            // checking if the value is fixed; if it is, go into the next recursive call
            if (this.valIsFixed[n/9][n % 9]) {
                // next recursive call
                if (this.solveRB(n + 1)) {
                    // if it returns true, we've found the right solution for the cell
                    return true;
                }

                // if we get here, that means we have to backtrack
                return false;
            }

            // checking if the current value is allowed at the current row/col
            if (this.isAllowed(val, n / 9, n % 9)) {
                // place the value at the current location
                this.placeVal(val, n / 9, n % 9);

                // going into the recursive call
                if (this.solveRB(n + 1)) {
                    // if it returns true, we've found a solution for the current stack frame
                    return true;
                }

                // otherwise, we haven't yet found a solution, and have to backtrack; thus we remove 
                // the value at the current location
                this.removeVal(val, n / 9, n % 9);
            }
        }

        // if we're here, we've tried every value for the current position; no solution found. 
        return false;
    } 
    
    /*
     * public "wrapper" method for solveRB().
     * Makes the initial call to solveRB, and returns whatever it returns.
     */
    public boolean solve() { 
        boolean foundSol = this.solveRB(0);
        return foundSol;
    }
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Sudoku puzzle = new Sudoku();
        
        System.out.print("Enter the name of the puzzle file: ");
        String filename = scan.nextLine();
        
        try {
            Scanner input = new Scanner(new File(filename));
            puzzle.readConfig(input);
        } catch (IOException e) {
            System.out.println("error accessing file " + filename);
            System.out.println(e);
            System.exit(1);
        }
        
        System.out.println();
        System.out.println("Here is the initial puzzle: ");
        puzzle.printGrid();
        System.out.println();
        
        if (puzzle.solve()) {
            System.out.println("Here is the solution: ");
        } else {
            System.out.println("No solution could be found.");
            System.out.println("Here is the current state of the puzzle:");
        }
        puzzle.printGrid();  
        scan.close();
    }    
}
