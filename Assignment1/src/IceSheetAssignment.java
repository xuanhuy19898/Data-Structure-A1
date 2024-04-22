/**
 * XUAN HUY PHAM
 * 000899551
 * assignment 1
 * jan 29, 2024
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IceSheetAssignment {

    public static void main(String[] args) {
        String filename = "src/ICESHEETS.TXT";
        IceSheet[] iceSheets = readIceSheets(filename);

        //initialize all points 0
        int totalFracturePoints = 0;
        int maxFracturePoints = 0;
        int sheetWithMaxFracturePoints = 0;
        int totalCrackPoints = 0;


        // loop through ice sheets to calculate fracture and crack points
        for (int i = 0; i < iceSheets.length; i++) {
            //find the number of fracture points in the current ice sheet
            int fracturePoints = findFracturePoints(iceSheets[i]);
            totalFracturePoints += fracturePoints; // add the num of fracture points to the total count

            //check if the current ice sheet has more fracture points than the max fracture points found
            if (fracturePoints > maxFracturePoints) {
                //if it does, update the max fracture points and record the index of the sheet
                maxFracturePoints = fracturePoints;
                sheetWithMaxFracturePoints = i + 1;
            }
        }

        // print total fracture points and the number of fracture points on the sheet with the most fracture points
        System.out.println("\nPART A \n=============================================");
        System.out.println("Total number of fracture points: " + totalFracturePoints);
        System.out.println(maxFracturePoints + " Fracture points on sheet " + sheetWithMaxFracturePoints + " is the maximum");


        //find the total number of crack points and summary
        System.out.println("\nPART B \n=============================================");
        System.out.println("SUMMARY OF ALL CRACK POINTS:");
        for (int i = 0; i < iceSheets.length; i++) {
            //find the number of crack points in the current ice sheet
            int crackPoints = findCrackPoints(iceSheets[i].getData());
            totalCrackPoints += crackPoints; //add to the total num of crack points count
            System.out.println("Sheet " + (i + 1) + ": crack points = " + crackPoints);
        }

        // print total crack points and fraction of crack points to fracture points
        System.out.println("Total Crack Points: " + totalCrackPoints);
        double fractionOfCrackPoints = (double) totalCrackPoints / totalFracturePoints;
        System.out.printf("Fraction of Fracture Points that are also Crack Points: %.3f\n", fractionOfCrackPoints);

    }


    /**
     * this function reads data from file containing info about ice sheets and constructs IceSheet objects
     * @param filename name of the file
     * @return an array of objects constructed from the file
     */
    public static IceSheet[] readIceSheets(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            //read the first line to get the number of ice sheets
            int numSheets = Integer.parseInt(br.readLine().trim());
            //create an array to store IceSheet objects
            IceSheet[] iceSheets = new IceSheet[numSheets];

            //loop over each ice sheet
            for (int i = 0; i < numSheets; i++) {
                //read the dimensions of the ice sheet
                String[] dimensions = br.readLine().trim().split(" ");
                int rows = Integer.parseInt(dimensions[0]);//number of rows
                int cols = Integer.parseInt(dimensions[1]);//number of cols
                //create a 2d array
                int[][] iceSheetData = new int[rows][cols];
                for (int j = 0; j < rows; j++) {
                    //read data from each row
                    String[] values = br.readLine().trim().split(" ");
                    //iterate over each column of the ice sheet
                    for (int k = 0; k < cols; k++) {
                        //parse the value in the corresponding position of the array
                        iceSheetData[j][k] = Integer.parseInt(values[k]);
                    }
                }
                //create the IceSheet object
                iceSheets[i] = new IceSheet(iceSheetData);
            }
            return iceSheets;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * calculate the number of fracture points of an IceSheet
     * @param iceSheet the ice sheet
     * @return the number of fracture points in that ice sheet
     */
    public static int findFracturePoints(IceSheet iceSheet) {
        int fracturePoints = 0;
        //iterate over each row of the ice sheet
        for (int[] row : iceSheet.getData()) {
            for (int htValue : row) {
                //check if the height is <= 200 and is divisible evenly by 50
                if (htValue <= 200 && htValue % 50 == 0) {
                    fracturePoints++;
                }
            }
        }
        return fracturePoints;
    }


    /**
     * calculate the number of crack points in an ice sheet presented by a 2d array of height values
     * @param iceSheetData the 2d array that represents data of the ice sheet
     * @return number of crack points found
     */
    public static int findCrackPoints(int[][] iceSheetData) {
        int crackPoints = 0;
        for (int i = 0; i < iceSheetData.length; i++) {
            for (int j = 0; j < iceSheetData[0].length; j++) {
                //check if it's a crack points
                if (iceSheetData[i][j] <= 200 && iceSheetData[i][j] % 50 == 0) {
                    // check the neighboring points if it's a crack point
                    if (isCrackPoint(iceSheetData, i, j)) {
                        crackPoints++;
                    }
                }
            }
        }
        return crackPoints;
    }

    /**
     * check if the specified cell in the ice sheet represented a crack points based on its neighboring point
     * @param iceSheetData the 2d array that represents data of the ice sheet
     * @param row the row index of the cell
     * @param col the col index of the cell
     * @return true if the cell is a crack point
     */
    public static boolean isCrackPoint(int[][] iceSheetData, int row, int col) {
        //2 arrays are used to represent the relative positions of neighboring cells in a grid
        //each element in the array represents a change in the row and column indices of the current cell to reach one of its neighboring cells
        //in the array "dx", this array represents the change in the row index
        //-1 represents moving one cell upwards
        //0 represents staying in the same row
        //1 represents moving one cell downwards
        //
        //in the array "dy", this array represents the change in the column index
        //-1 represents moving one cell to the left
        //0 represents staying in the same column
        //1 represents moving one cell to the right
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};


        for (int k = 0; k < 8; k++) {
            //calculate the coordinates of the neighboring cell
            int newRow = row + dx[k];
            int newCol = col + dy[k];

            //check if the neighboring cell is valid and its height value is divisible by 10
            if (isValidPoint(iceSheetData, newRow, newCol) && iceSheetData[newRow][newCol] % 10 == 0) {
                return true;
            }
        }
        return false;//it is not a crack point if no neighboring cells meet the criteria
    }


    /**
     * check whether a row and column index corresponds to a valid point within the 2d array
     * @param iceSheetData the 2d array containing data of ice sheet
     * @param row the row index
     * @param col the col index
     * @return true if the point is valid
     */
    public static boolean isValidPoint(int[][] iceSheetData, int row, int col) {
        //check if the row index is within the bounds of the array
        boolean validRow = row >= 0 && row < iceSheetData.length;
        //check if the column index is within the bounds of the array
        boolean validCol = col >= 0 && col < iceSheetData[0].length;
        //true if both row and column indices are valid, otherwise will be false
        return validRow && validCol;
    }
}

/*
    represent an ice sheet
 */
class IceSheet {
    private int[][] data;//store the height data of the ice sheet

    /**
     *
     * construct an ice sheet
     * @param data the 2d array with data of ice sheet
     */
    public IceSheet(int[][] data) {
        this.data = data;
    }

    /**
     * retrieve the height of the ice sheet
     * @return the 2d array that represents the data of ice sheet
     */
    public int[][] getData() {
        return data;
    }
} 
 
 

 