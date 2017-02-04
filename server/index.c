#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <ctype.h>
//------------------------------------------------------------------------------------------------------------------------------------------------------------------

int updateSignalStrength(){
    
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------

int getMantissaForFolder(double mantissa){
    int mantissaForFolder;
    
    if (mantissa < 0.3){
        mantissaForFolder = 3;
    }
    else if ((mantissa > 0.3) && (mantissa < 0.6)) {
        mantissaForFolder = 6;
    }
    else {
        mantissaForFolder = 9;
    }
    
    return mantissaForFolder;
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------

const char * convertNegativeCoor(int coor){
    
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------

int * locateStrength(double x, double y){
    double step = 0.000150;
    static int coord_step_val[2];
    
    
//    determining in which range (0.0 .. 0.3 .. 0.6 .. 0.9) values of x and y are located
    if ((x > 0.3) && (x < 0.6)){
        x = x - 0.3;
    } else if ((x > 0.6) && (x < 0.999999)){
        x = x - 0.6;
    }
    
    if ((y > 0.3) && (y < 0.6)){
        y = y - 0.3;
    } else if ((y > 0.6) && (y < 0.999999)){
        y = y - 0.6;
    }
    
    coord_step_val[0] =(int)floor(x/step);
    coord_step_val[1] =(int)floor(y/step);
    
    return coord_step_val;
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------

double * convvertToNormalCoor(int conv_x, int conv_y, int x_decimal, int y_decimal, double x_mantissa, double y_mantissa){
    double step = 0.000150;
    static double origin_coordin[2];
    
//    getting double value from converted values
    origin_coordin[0] = conv_x * step;
    origin_coordin[1] = conv_y * step;
    
    
    //    determining in which range (0.0 .. 0.3 .. 0.6 .. 0.9) values of x and y are located
    if ((x_mantissa > 0.3) && (x_mantissa < 0.6)){
        origin_coordin[0] = origin_coordin[0] + 0.3;
    } else if ((x_mantissa > 0.6) && (x_mantissa < 0.999999)){
        origin_coordin[0] = origin_coordin[0] + 0.6;
    }
    
    if ((abs(y_mantissa) > 0.3) && (abs(y_mantissa) < 0.6)){
        origin_coordin[1] = origin_coordin[1] + 0.3;
    } else if ((abs(y_mantissa) > 0.6) && (abs(y_mantissa) < 0.999999)){
        origin_coordin[1] = origin_coordin[1] + 0.6;
    }
    
    
    if (x_decimal < 0){
        origin_coordin[0] = origin_coordin[0] * (-1);
    }
    if (y_decimal < 0){
        origin_coordin[1] = origin_coordin[1] * (-1);
    }
    
    
    origin_coordin[0] = x_decimal + origin_coordin[0];
    origin_coordin[1] = y_decimal + origin_coordin[1];
    
    
    return origin_coordin;
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------

char * generateFolderName(int x_decimal, int y_decimal, double x_mantissa, double y_mantissa){
    
    int mantissaForFolder_x, mantissaForFolder_y;
    char x_folder[40], y_folder[40];
    
    char *buffer = malloc (sizeof (char) * 1000);
    
//    replace negative sign with "n" if integer part is negative
    if (x_decimal < 0) {
        x_decimal = x_decimal * (-1);
        sprintf(x_folder, "n%d", x_decimal); // puts string into buffer
    } 
    else{
        sprintf(x_folder, "%d", x_decimal);
    }
    
    if (y_decimal < 0) {
        y_decimal = y_decimal * (-1);
        sprintf(y_folder, "n%d", y_decimal); // puts string into buffer
    } 
    else {
        sprintf(y_folder, "%d", y_decimal);
    }
    
    
//    mantissa part for folder name. (Ex.: 3|6|9)
    mantissaForFolder_x = getMantissaForFolder(x_mantissa);
    mantissaForFolder_y = getMantissaForFolder(y_mantissa);

//buffer contains folder name and path for storing data
    sprintf(buffer, "data/%s.%d_%s.%d.dat", x_folder, mantissaForFolder_x, y_folder, mantissaForFolder_y);
    
    return buffer;
    
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------




void createFileWitheData(char *fname, int x, int y, int s){
    int r = 2000, c = 2000, i, j;
    int *layer[r];
    for (i = 0; i < r; i++){
         layer[i] = (int *)malloc(c * sizeof(int));
    }
    
//    assigning values of array to 0
//    layer[i][j] is same as *(*(arr+i)+j)
    for (i = 0; i <  r; i++){
        for (j = 0; j < c; j++){
            layer[i][j] = 0;
        }
    }
    
    i, j = 0;
    FILE *fp;
    fp = fopen(fname, "w");
    layer[x][y] = s;
    
    if (fp) {
        for (i = 0; i < 2000; i++){
            for (j = 0; j < 2000; j++){
                fprintf(fp, "%d ", layer[i][j]);
            }
        }
        fclose(fp);
    } else {
        fprintf(stderr,"error opening file \"%s\"\n", fname);
        perror("error opening file.");
    }
}



void updateFileWitheData(char *fname, int x, int y, int s){
    
    int r = 2000, c = 2000, i, j;
    int *layer[r];
    FILE *fp;
    for (i = 0; i < r; i++){
         layer[i] = (int *)malloc(c * sizeof(int));
    }
    
    
    fp=fopen(fname, "r+");
    if (fp) {
        for(i = 0; i < 2000; i++) {
            for (j = 0 ; j < 2000; j++) {
                fscanf(fp, "%d", &layer[i][j]);
            }
        }
        
    } else {
        fprintf(stderr,"error opening file \"%s\"\n", fname);
        perror("error opening file.");
    }
    
//    writing back updated array
    i, j = 0;
    
//    if old value is 0 (not set), we write new value without any modifications
//    if old value is != 0 (already set), we save average of old and new value
    if (layer[x][y] !=0 ){
        layer[x][y] = (int)layer[x][y] * 0.2 + s * 0.8;
    } else {
        layer[x][y] = s;
    }
    
    if (fp) {
        for (i = 0; i < 2000; i++){
            for (j = 0; j < 2000; j++){
                fprintf(fp, "%d ", layer[i][j]);
            }
        }
        
    }
    else {
        fprintf(stderr,"error opening file \"%s\"\n", fname);
        perror("error opening file.");
    }
    
    
    fclose(fp);
}




int * get_better_signal(char *fname, int x, int y, int s){
    static int temp_bs_coord[3] = {0};
    
    int r = 2000, c = 2000, i, j;
    int *layer[r];
    FILE *fp;
    for (i = 0; i < r; i++){
         layer[i] = (int *)malloc(c * sizeof(int));
    }
    
    fp=fopen(fname, "r");
    
    if (fp) {
        for(i = 0; i < 2000; i++) {
            for (j = 0 ; j < 2000; j++) {
                fscanf(fp, "%d", &layer[i][j]);
            }
        }
        
    } else {
        fprintf(stderr,"error opening file \"%s\"\n", fname);
        perror("error opening file.");
    }
    
    i, j = 0;
    
    temp_bs_coord[0] = x;
    temp_bs_coord[1] = y;
    temp_bs_coord[2] = s;
//    printf("<br>original layer[i][j] - %d <br>", layer[x][y]);
    
    
    if (fp) {
        for (i = x-3; i < x+3; i++){
            for (j = y-3; j < y+3; j++){
                if ((i != x) || (j != y)){
                    if (layer[i][j] != 0 ){
//                        printf("layer[i][j] - %d <br>", layer[i][j]);
//                        printf("i - %d <br>", i);
//                        printf("j - %d <br>", j);
                        if (layer[i][j] > temp_bs_coord[2]){
                            temp_bs_coord[0] = i;
                            temp_bs_coord[1] = j;
                            temp_bs_coord[2] = layer[i][j];
                        }
                    }
                }
            }
        }
        
        
    }
    else {
        fprintf(stderr,"error opening file \"%s\"\n", fname);
        perror("error opening file.");
    }
    
    fclose(fp);
    
    return temp_bs_coord;
}






int main (void){
	printf("Content-type: text/html\n\n");
    
//    query saved in var query
    char query[200];
	char *data = malloc(200);
    data = getenv("QUERY_STRING");
    sscanf(data,"%s", query);
    
    int i = 0;
    int numOfQueryVal = 3;  // number of values in query
    
//    getting data from query
    char *token = strtok(query, "&");
    char *queryData[3];
    
    
//    displaying random image
//    printf("<img src='http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/voronoi-map-goal-distorted.png'><br />");
//    printf("http://www.wheredoyougo.net/map/ag93aGVyZS1kby15b3UtZ29yEQsSCE1hcEltYWdlGNL0_wIM.png");
    
    
/* Getting values in query inside queryData
 * queryData[0] - x
 * queryData[1] - y
 * queryData[2] - strength
 */
    
    while (token != NULL) {
        queryData[i++] = token;
        token = strtok (NULL, "&");
    }
    
    
    for (i = 0; i < numOfQueryVal; ++i) {
        if(strstr(queryData[i], "=") != NULL) {
            queryData[i] = queryData[i] + 2;
        }
    }
    
    
    
    double x, y;
    int s;
    
//    input data checking
    
//    if((isdigit(queryData[0])) && (isdigit(queryData[1])) && (isdigit(queryData[2]))) {
//      //valid input
//    }
//    else
//    {
//      //invalid input
//    }
    
//    retriving data from query
    x = atof(queryData[0]);
    y = atof(queryData[1]);
    s = atoi(queryData[2]);
    
//    printf("x = %f <br />", x);
//    printf("y = %f <br />", y);
//    printf("s = %d <br />", s);
    
    
//    getting decimal and mantissa of x
    double x_mantissa, x_temp_to_convert, y_mantissa, y_temp_to_convert;
    int x_decimal, y_decimal;
//    getting decimal and integer parts of x and y
    x_mantissa = modf(x, &x_temp_to_convert);
    y_mantissa = modf(y, &y_temp_to_convert);
    
//    casting to int decimal parts
    x_decimal = (int)x_temp_to_convert;
    y_decimal = (int)y_temp_to_convert;
    
//    make positive x-mantissa and y_mantissa if after splitting they have negative value 
    if (x_mantissa < 0) { x_mantissa = x_mantissa * (-1); }
    if (y_mantissa < 0) { y_mantissa = y_mantissa * (-1); }
//------------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    
/* converting mantissa part to 0.000150 base format
 * generating line with coordinates and strength
 * coorLine contains line
*/
    int *coord_step_val, *search_res;
    char *coorLine, *coorLine_strength, temp_coorline[30], temp_coorline_strength[30];
    coord_step_val = locateStrength(x_mantissa, y_mantissa);
    
//    printf("0 - %d <br /> 1 - %d <br />", coord_step_val[0], coord_step_val[1]);
    
    sprintf(temp_coorline, "%d|%d|", coord_step_val[0], coord_step_val[1]);

    
    coorLine = temp_coorline;

    
    
    
    
    
    //get the folder_name, where signal strength and corresponding coordinates are located
    char* folder_name = generateFolderName(x_decimal, y_decimal, x_mantissa, y_mantissa);
    
//    printf("folder_name: %s", folder_name);
    if( access( folder_name, F_OK ) != -1 ) {
//        printf("file exists");
        updateFileWitheData(folder_name, coord_step_val[0], coord_step_val[1], s);
    } else {
//        printf("file doesn't exist");
        createFileWitheData(folder_name, coord_step_val[0], coord_step_val[1], s);
    }
    
    
//    getting better signal strength
    int *temp_bs_coord;
    double *origin_bs_coord;
    temp_bs_coord = get_better_signal(folder_name, coord_step_val[0], coord_step_val[1], s);
    
    origin_bs_coord = convvertToNormalCoor(temp_bs_coord[0], temp_bs_coord[1], x_decimal, y_decimal, x_mantissa, y_mantissa);
    
    printf("<br> better signal strength <br> %f <br> %f <br> %d <br>", origin_bs_coord[0], origin_bs_coord[1], temp_bs_coord[2]);    
    
    
	return 0;
}