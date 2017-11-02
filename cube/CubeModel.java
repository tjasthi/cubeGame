package cube;
import java.util.Observable;
import static java.lang.System.arraycopy;


/** Models an instance of the Cube puzzle: a cube with color on some sides
 *  sitting on a cell of a square grid, some of whose cells are colored.
 *  Any object may register to observe this model, using the (inherited)
 *  addObserver method.  The model notifies observers whenever it is modified.
 *  @author P. N. Hilfinger
 */
class CubeModel extends Observable {

    /** A blank cube puzzle of size 4. */
    CubeModel() {
        initialize(4, 0, 0, new boolean[4][4], _facePainted);
    }

    /** A copy of CUBE. */
    CubeModel(CubeModel cube) {
        initialize(cube);
    }

    /** Initialize puzzle of size SIDExSIDE with the cube initially at
     *  ROW0 and COL0, with square r, c painted iff PAINTED[r][c], and
     *  with face k painted iff FACEPAINTED[k] (see isPaintedFace).
     *  Assumes that
     *    * SIDE > 2.
     *    * PAINTED is SIDExSIDE.
     *    * 0 <= ROW0, COL0 < SIDE.
     *    * FACEPAINTED has length 6.
     */
    void initialize(int side, int row0, int col0, boolean[][] painted,
                    boolean[] facePainted) {
        _side = side;
        _row0 = row0;
        _col0 = col0;
        moves = 0;
        _painted = painted;
        _facePainted = facePainted;
    }

    /** Initialize puzzle of size SIDExSID with the cube initially at
     *  ROW0 and COL0, with square r, c painted iff PAINTED[r][c].
     *  The cube is initially blank.
     *  Assumes that
     *    * SIDE > 2.
     *    * PAINTED is SIDExSIDE.
     *    * 0 <= ROW0, COL0 < SIDE.
     */
    void initialize(int side, int row0, int col0, boolean[][] painted) {
        initialize(side, row0, col0, painted, new boolean[6]);
    }

    /** Initialize puzzle to be a copy of CUBE. */
    void initialize(CubeModel cube) {
        _side = cube.side();
        _row0 = cube.cubeRow();
        _col0 = cube.cubeCol();
        _painted = cube._painted;
        arraycopy(cube._facePainted, 0, _facePainted, 0, 6);
        moves = cube.moves();
        setChanged();
        notifyObservers();
    }

    /** Move the cube to (ROW, COL), if that position is on the board and
     *  vertically or horizontally adjacent to the current cube position.
     *  Transfers colors as specified by the rules.
     *  Throws IllegalArgumentException if preconditions are not met.
     */
    void move(int row, int col) {
        if ((0 <= row && row < _side) && (0 <= col && col < _side)) {
            if ((Math.abs(_row0 - row) == 1) && (_col0 - col == 0)) {
                moveVertical(_facePainted, _row0 - row);
            } else if ((_row0 - row == 0) && ((Math.abs(_col0 - col) == 1))) {
                moveHorizontal(_facePainted, _col0 - col);
            } else {
                throw new IllegalArgumentException("Illegal cube movement!");
            }
            _row0 = row;
            _col0 = col;
            swap(_col0, _row0, _painted, _facePainted);
            moves += 1;
        } else {
            throw new IllegalArgumentException("Square out of range!");
        }
        setChanged();
        notifyObservers();
    }

    /** Reassigns cube faces for horizontal movement.
     * @param direction cube moves left if -1, cube moves right if 1
     * @param facePainted array of boolean values
     *                    to refer to coloring of faces of a cube **/
    void moveHorizontal(boolean[] facePainted, int direction) {
        boolean left = facePainted[2];
        boolean right = facePainted[3];
        boolean bottom = facePainted[4];
        boolean top = facePainted[5];
        if (direction == -1) {
            facePainted[2] = bottom;
            facePainted[3] = top;
            facePainted[4] = right;
            facePainted[5] = left;
        } else {
            facePainted[2] = top;
            facePainted[3] = bottom;
            facePainted[4] = left;
            facePainted[5] = right;
        }
    }

    /** Reassigns cube faces for vertical movement.
     * @param direction cube moves up if -1, cube moves down if 1
     * @param facePainted array of boolean values
     *                    to refer to coloring of faces of a cube **/
    void moveVertical(boolean[] facePainted, int direction) {
        boolean front = facePainted[0];
        boolean back = facePainted[1];
        boolean bottom = facePainted[4];
        boolean top = facePainted[5];
        if (direction == 1) {
            facePainted[0] = top;
            facePainted[1] = bottom;
            facePainted[4] = front;
            facePainted[5] = back;
        } else {
            facePainted[0] = bottom;
            facePainted[1] = top;
            facePainted[4] = back;
            facePainted[5] = front;
        }
    }

    /** Switches the color of the bottom face of cube and the tile of board.
     * @param col0 column placement of the cube
     * @param row0 row placement of the cube
     * @param painted multidimensional array with
     *                boolean values referring to color
     * @param facePainted array with boolean values
     *                    referring to painted faces of the cube **/
    void swap(int col0, int row0, boolean[][] painted,
              boolean[] facePainted) {
        boolean tempColor = facePainted[4];
        facePainted[4] = painted[row0][col0];
        painted[row0][col0] = tempColor;
    }

    /** Return the number of squares on a side. */
    int side() {
        return _side;
    }

    /** Return true iff square ROW, COL is painted.
     *  Requires 0 <= ROW, COL < board size. */
    boolean isPaintedSquare(int row, int col) {
        if ((row >= _side) || (col >= _side)) {
            throw new IllegalArgumentException("Square out of range!");
        }
        return _painted[row][col];
    }

    /** Return current row of cube. */
    int cubeRow() {
        return _row0;
    }

    /** Return current column of cube. */
    int cubeCol() {
        return _col0;
    }

    /** Return the number of moves made on current puzzle. */
    int moves() {
        return moves;
    }

    /** Return true iff face #FACE, 0 <= FACE < 6, of the cube is painted.
     *  Faces are numbered as follows:
     *    0: Vertical in the direction of row 0 (nearest row to player).
     *    1: Vertical in the direction of last row.
     *    2: Vertical in the direction of column 0 (left column).
     *    3: Vertical in the direction of last column.
     *    4: Bottom face.
     *    5: Top face.
     */
    boolean isPaintedFace(int face) {
        return _facePainted[face];
    }

    /** Return true iff all faces are painted. */
    boolean allFacesPainted() {
        for (int x = 0; x < 6; x += 1) {
            if (!isPaintedFace(x)) {
                return false;
            }
        }
        return true;
    }
    /** Stores the dimension length of the board. */
    private int _side;
    /** Stores the row number where cube is located. */
    private int _row0;
    /** Stores the column number where cube is located. */
    private int _col0;
    /** Stores the number of moves made. */
    private int moves;
    /** Checks if given square of board is painted. */
    private boolean[][] _painted;
    /** Checks if given side of cube is painted. */
    private boolean[] _facePainted = new boolean[6];
}
