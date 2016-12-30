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
    
    coord_step_val[0] =(int)floor(x/step);
    coord_step_val[1] =(int)floor(y/step);
    
    return coord_step_val;
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

void editFile(char *folder_name, char *coorLine) {
    FILE *fp;
    
//    printf("%s", coorLine);
    
    fp = fopen(folder_name, "a+");
    
    
    if (fp) {
        fprintf(fp, "%s\n", coorLine);
        fclose(fp);
    } else {
        fprintf(stderr,"error opening file \"%s\"\n", folder_name);
        perror("error opening file.");
    }
        
}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------

void getData(int number) {
    FILE *fp;
    char buffer[255];
    
    sprintf(buffer, "data/%d.txt", number);
    fp = fopen(buffer, "r");
    fscanf(fp, "%s", buffer);
    printf("1 : %s\n", buffer );

    fgets(buffer, 255, (FILE*)fp);
    printf("2: %s\n", buffer );

    
    fgets(buffer, 255, (FILE*)fp);
    printf("3: %s\n", buffer );
    fclose(fp);
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
    
    
    fp=fopen(fname, "wr");
    
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
    layer[x][y] = layer[x][y] * 0.2 + s * 0.8;
    printf("<br />s in arr before mod = %d <br /> ", layer[x][y]);
//    layer[x][y] = s;
    
    printf("s in arr = %d <br />", layer[x][y]);
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






int main (void){
	printf("Content-type: text/html\n\n");
//	printf("CGI-Program has started <br />");
    
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
    

    if((isdigit(queryData[0])) && (isdigit(queryData[1])) && (isdigit(queryData[2]))) {
      //valid input
    }
    else
    {
      //invalid input
    }
    
//    retriving data from query
    x = atof(queryData[0]);
    y = atof(queryData[1]);
    s = atoi(queryData[2]);
    
    printf("x = %f <br />", x);
    printf("y = %f <br />", y);
    printf("s = %d <br />", s);
    
    
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
    
    printf("0 - %d <br /> 1 - %d <br />", coord_step_val[0], coord_step_val[1]);
    
    sprintf(temp_coorline, "%d|%d|", coord_step_val[0], coord_step_val[1]);

    
    coorLine = temp_coorline;

    
    
    
    
    
    //get the folder_name, where signal strength and corresponding coordinates are located
    char* folder_name = generateFolderName(x_decimal, y_decimal, x_mantissa, y_mantissa);
    
    
    if( access( folder_name, F_OK ) != -1 ) {
        printf("file exists");
        updateFileWitheData(folder_name, coord_step_val[0], coord_step_val[1], s);
    } else {
        printf("file doesn't exist");
        createFileWitheData(folder_name, coord_step_val[0], coord_step_val[1], s);
    }
    
    
    
    
    
	return 0;
}