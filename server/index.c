#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>

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




/*
 * looks for a string inside given file
 * returns 0 if string was not found
 * returns 1 if string was found
*/
int search_in_file(char *fname, char *str, int s) {
	FILE *fp;
    int line_num = 1;
	int find_result = 0;
	char temp[1024];
    int ret = 0;
    
//	gcc user
	if((fp = fopen(fname, "r")) == NULL) {
		return(-1);
	}

	while(fgets(temp, 1024, fp) != NULL) {
		if((strstr(temp, str)) != NULL) {
            printf("%s", temp);
            
            
//            splitting the lie by | to get strength
            int i = 0;
            char *p = strtok (temp, "|");
            char *array[4];

            while (p != NULL){
                array[i++] = p;
                p = strtok (NULL, "|");
            }
            
            /* casting and comparing the strength of 
             * signal which we get to strength which 
             * we already have in our file
             */
            int line_s = (int)atof(array[2]);
            
            if (line_s == s){
                ret = line_num;
                find_result++;
            } else {
              ret = 0;  
            }
		}
        line_num++;
	}

	if(find_result == 0) {
        ret = 0;
	}
	
	//Close the file if still open.
	if(fp) {
		fclose(fp);
	}
    
    
   	return ret;
}




void removeLineFromFile(char *fname, char *str, int s){
    FILE *fileptr1, *fileptr2;
    char filename[40];
    char ch;
    int delete_line, temp = 1;
    
    filename = "data/32.txt";
    
    //open file in read mode
    fileptr1 = fopen(filename, "r");
    ch = getc(fileptr1);
    while (ch != EOF) {
        printf("%c", ch);
        ch = getc(fileptr1);
    }
    //rewind
    rewind(fileptr1);
    printf(" \n Enter line number of the line to be deleted:");
    scanf("%d", &delete_line);
    //open new file in write mode
    fileptr2 = fopen("replica.dat", "w");
    ch = getc(fileptr1);
    while (ch != EOF)
    {
        ch = getc(fileptr1);
        if (ch == '\n')
        {
            temp++;
        }
        //except the line to be deleted
        if (temp != delete_line)
        {
            //copy all lines in file replica.c
            putc(ch, fileptr2);
        }
    }
    fclose(fileptr1);
    fclose(fileptr2);
    remove(filename);
    //rename the file replica.c to original name
    rename("replica.c", filename);
    printf("\n The contents of file after being modified are as follows:\n");
    fileptr1 = fopen(filename, "r");
    ch = getc(fileptr1);
    while (ch != EOF)
    {
        printf("%c", ch);
        ch = getc(fileptr1);
    }
    fclose(fileptr1);
    
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
    int *coord_step_val;
    char *coorLine, *coorLine_strength, temp_coorline[30], temp_coorline_strength[30];
    coord_step_val = locateStrength(x_mantissa, y_mantissa);
    
    sprintf(temp_coorline, "%d|%d|", coord_step_val[0], coord_step_val[1]);
    sprintf(temp_coorline_strength, "%d|%d|%d", coord_step_val[0], coord_step_val[1], s);
    
    coorLine = temp_coorline;
    coorLine_strength = temp_coorline_strength;
    
    
    
    
    
    //get the folder_name, where signal strength and corresponding coordinates are located
    char* folder_name = generateFolderName(x_decimal, y_decimal, x_mantissa, y_mantissa);
    
    
    
    
    if (search_in_file(folder_name, coorLine, s)){
        int line_num = search_in_file(folder_name, coorLine, s);
        removeLineFromFile();
    }
    else {
        editFile(folder_name, coorLine);
    }
    
    
    
    
//    int line_exists = search_in_file(folder_name, "55555");
    
//    int temp_unt = search_in_file(folder_name, "55555");
    
//    printf("%d", temp_unt);
    
//    if (temp_unt == 0){
//        editFile(x, y, s);
//    } else {
//        printf("exists");
//    }
//    editFile(x, y, s);
//    getData(x);
    
    
	return 0;
}