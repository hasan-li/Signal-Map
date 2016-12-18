#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>

//----------------------------------------------------------------

int updateSignalStrength(){
    
}
//----------------------------------------------------------------

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
//----------------------------------------------------------------

const char * convertNegativeCoor(int coor){
    
}
//----------------------------------------------------------------

int * locateStrength(double x, double y, int s){
    double step = 0.000150;
    static int coord_step_val[2];
    
    coord_step_val[0] =(int)floor(x/step);
    coord_step_val[1] =(int)floor(y/step);
    
    return coord_step_val;
}
//----------------------------------------------------------------

void editFile(float x, float y, int s) {
    FILE *fp;
    
    char buffer[200]; // assuming POSIX
    int temp_x;
    char x_folder[40], y_folder[40];
    
    printf("x = %f <br />", x);
    printf("y = %f <br />", y);
    printf("s = %d <br />", s);
    
//    getting decimal and mantissa of x
    double x_mantissa, x_temp_to_convert, y_mantissa, y_temp_to_convert;
    int mantissaForFolder_x, mantissaForFolder_y, x_decimal, y_decimal;
        
//    getting decimal and integer parts of x and y
    x_mantissa = modf(x, &x_temp_to_convert);
    y_mantissa = modf(y, &y_temp_to_convert);
    
//    casting to int decimal parts
    x_decimal = (int)x_temp_to_convert;
    y_decimal = (int)y_temp_to_convert;
    
    
//    make positive x-mantissa and y_mantissa if after splitting they have negative value 
    if (x_mantissa < 0) { x_mantissa = x_mantissa * (-1); }
    if (y_mantissa < 0) { y_mantissa = y_mantissa * (-1); }
    
    
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
    sprintf(buffer, "data/%s.%d_%s.%d.txt", x_folder, mantissaForFolder_x, y_folder, mantissaForFolder_y);
    
    char coorLine[30];
    int *coord_step_val;
    coord_step_val = locateStrength(x_mantissa, y_mantissa, 100); // fraught with problems
    printf("%d <br /> %d", coord_step_val[0], coord_step_val[1]);
    
    
    sprintf(coorLine, "%d|%d|%d*", coord_step_val[0], coord_step_val[1], s);
//    printf("%s", coorLine);
    
    fp = fopen(buffer, "a+");
    
    
    if (fp) {
        fprintf(fp, "\n");
        fprintf(fp, "%s\n", coorLine);
        
        fclose(fp);
    } else {
        fprintf(stderr,"error opening file \"%s\"\n",buffer);
        perror("error opening file.");
    }
        
}
//----------------------------------------------------------------

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
//----------------------------------------------------------------





int search_in_file(char *fname, char *str) {
	FILE *fp;
	int line_num = 1;
	int find_result = 0;
	char temp[512];
	
//	gcc users
	if((fp = fopen(fname, "r")) == NULL) {
		return(-1);
	}

	while(fgets(temp, 512, fp) != NULL) {
		if((strstr(temp, str)) != NULL) {
			printf("A match found on line: %d\n", line_num);
			printf("\n%s\n", temp);
			find_result++;
		}
		line_num++;
	}

	if(find_result == 0) {
		printf("\nSorry, couldn't find a match.\n");
	}
	
	//Close the file if still open.
	if(fp) {
		fclose(fp);
	}
   	return(0);
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
    
    

//    getting values in query inside queryData
//    * queryData[0] - x
//    * queryData[1] - y
//    * queryData[2] - strength

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
    x = atof(queryData[0]);
    y = atof(queryData[1]);
    s = atoi(queryData[2]);
    
    
    
    
    search_in_file("data/32.3_n145.3.txt", "1709|810|100");
//    editFile(x, y, s);
//    getData(x);
    
    
	return 0;
}