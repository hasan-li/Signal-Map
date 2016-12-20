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
int * search_in_file(char *fname, char *str, int s) {
	FILE *fp;
    int line_num = 1;
	int find_result = 0;
	char temp[1024];
    
    
    printf("<br />in search_in_file");
    printf("<br />in fname: %s", fname);
    printf("<br />in str: %s", str);
    printf("<br />in s: %d", s);
    
    /*
     * ret[0] - 0|1
     * ret[1] - old value of signal strength (line_s)
     * ret[2] - line num
     * ret[4] - error if 1
    */
    static int ret[4];
    
//	gcc user
	if((fp = fopen(fname, "r")) == NULL) {
		ret[3] = 1;
        return ret;
	}

	while(fgets(temp, 1024, fp) != NULL) {
		if((strstr(temp, str)) != NULL) {
            
//            printf("%s", temp);
            
            
//            splitting the line by | to get strength
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
                
                printf("<br />line_s == s");
                
                ret[0] = 1;
                ret[1] = 0;
                ret[2] = 0;
            } else {
                ret[0] = 1;
                ret[1] = line_s;
                ret[2] = line_num;
                
                printf("<br />line_s != s");
            }
            find_result++;
		}
        line_num++;
	}

	if(find_result == 0) {
        ret[0] = 0;
        ret[1] = 0;
        ret[2] = 0;
	}
	
	//Close the file if still open.
	if(fp) {
		fclose(fp);
	}
    
    
   	return ret;
}




void removeLineFromFile(char *filename, int del_line){
    FILE *fp1, *fp2;
    char c;
    int temp = 1;
    
    printf("<br /> del_line: %d", del_line);
    
    //open file in read mode
    fp1 = fopen(filename, "r");
    c = getc(fp1);
    printf("<br /> c1: %c", c);
    //until the last character of file is obtained
    while (c != EOF) {
        printf("<br /> c: %c", c);
        //print current character and read next character
        c = getc(fp1);
    }
    //PROBLEM HERE: ADDING RANDOM CHAR AT THE END OF FILE
    
    //rewind
    rewind(fp1);
    //open new file in write mode
    fp2 = fopen("data/copy.dat", "w");
    c = getc(fp1);
    printf("<br /> c2: %c", c);
    rewind(fp1);
    while (c != EOF) {
        c = getc(fp1);
        printf("<br />inside while. c: %c, temp: %d", c, temp);
        if (c == '\n'){
            temp++;
        }
        //except the line to be deleted
        if (temp != del_line) {
            //copy all lines in file copy.data
            putc(c, fp2); 
        }
    }
    //close both files
    fclose(fp1);
    fclose(fp2);
    //remove original file
    remove(filename);
    //rename the file data/copy.dat to original name
    rename("data/copy.dat", filename);
    printf("<br /> The contents of file after being modified are as  follows:\n");
    fp1 = fopen(filename, "r");
    c = getc(fp1);
    while (c != EOF) {
        printf("%c", c);
        c = getc(fp1);
    }
    fclose(fp1);
    
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
    int *coord_step_val, *search_res;
    char *coorLine, *coorLine_strength, temp_coorline[30], temp_coorline_strength[30];
    coord_step_val = locateStrength(x_mantissa, y_mantissa);
    
    sprintf(temp_coorline, "%d|%d|", coord_step_val[0], coord_step_val[1]);

    
    coorLine = temp_coorline;

    
    
    
    
    
    //get the folder_name, where signal strength and corresponding coordinates are located
    char* folder_name = generateFolderName(x_decimal, y_decimal, x_mantissa, y_mantissa);
    
    
    
    
    
    search_res = search_in_file(folder_name, coorLine, s);
    /*
     * search_res[0] - 0|1
     * search_res[1] - old value of signal strength (line_s)
     * search_res[2] - line num
    */
    
    
    printf("<br /> new search_res[0]: %d", search_res[0]);
    printf("<br /> new search_res[1]: %d", search_res[1]);
    printf("<br /> new search_res[2]: %d", search_res[2]);
    
    if (search_res[0]){
        if (search_res[1] != 0){
            printf("<br /> match found");
            removeLineFromFile(folder_name, search_res[2]);
            
            //averaging signal strength
            s = 0.8*s + 0.2*search_res[1];
            printf("<br /> new s: %d", s);
            
            //addind new value of coorLine with contcatenated s
            sprintf(temp_coorline_strength, "%d|%d|%d", coord_step_val[0], coord_step_val[1], s);
            coorLine_strength = temp_coorline_strength;
            printf("<br /> new coorLine_strength: %s", coorLine_strength);
            editFile(folder_name, coorLine_strength); 
        }
    }
    else {
        
        sprintf(temp_coorline_strength, "%d|%d|%d", coord_step_val[0], coord_step_val[1], s);
        coorLine_strength = temp_coorline_strength;
        editFile(folder_name, coorLine_strength);
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