import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.util.tools.ArrayUtils;

/**
 * Model for Choco v.3
 */
public class SudokuSolver {

    public static void main(String[] args) {
        //fields n grid
        final int n = 9;
        Solver solver = new Solver();
        Chatterbox.showStatistics(solver);
        IntVar[][] cols, rows, sectors;
        //create new model;
        rows = new IntVar[n][n];
        cols = new IntVar[n][n];
        sectors = new IntVar[n][n];
        int [][] grid = new int[][]{
                {0, 0, 0, 2, 0, 0, 0, 0, 0},
                {0, 8, 0, 0, 3, 0, 0, 7, 0},
                {3, 0, 0, 5, 0, 4, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 2, 8},
                {8, 3, 0, 0, 1, 0, 0, 0, 0},
                {0, 4, 0, 7, 2, 0, 3, 5, 1},
                {0, 7, 0, 0, 5, 6, 0, 0, 4},
                {0, 0, 3, 0, 0, 0, 0, 0, 0},
                {2, 0, 5, 4, 0, 1, 6, 0, 3}
        };
        //fill rows and columns
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //read values from data grid
                if (grid[i][j] > 0) {
                    //if value is over 0, it should be copied
                    rows[i][j] = VariableFactory.fixed(grid[i][j], solver);
                } else {
                    //if value is 0, it should be computed
                    rows[i][j] = VariableFactory.enumerated("c_" + i + "_" + j, 1, n, solver);
                }
                //copy value to cols array
                cols[j][i] = rows[i][j];
            }
        }

        //compute sectors
        //Sudoku game has 9 sectors 3x3, to simplify we'll made 9 arrays with 9 values, every array for 1 sector.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    //iterate over every sector and copy values to arrays
                    sectors[j + k * 3][i] = rows[k * 3][i + j * 3];
                    sectors[j + k * 3][i + 3] = rows[1 + k * 3][i + j * 3];
                    sectors[j + k * 3][i + 6] = rows[2 + k * 3][i + j * 3];
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            solver.post(IntConstraintFactory.alldifferent(rows[i], "AC"));
            //we should choose algorithm, we chose "AC", look into documentation
            //the same for columns and sectors
            solver.post(IntConstraintFactory.alldifferent(cols[i], "AC"));
            solver.post(IntConstraintFactory.alldifferent(sectors[i], "AC"));
        }
        //set search strategy
        solver.set(IntStrategyFactory.minDom_LB(ArrayUtils.append(rows)));  //set smallest values first
        System.out.println("Solving sudoku grid!\n");
        //Just solve it!
        if (solver.findSolution()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n\t");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    sb.append(rows[i][j].getValue()).append("  ");
                }
                sb.append("\n\t");
            }
            System.out.println(sb);
        } else {
            System.out.println("No Solution!");
        }
    }
}