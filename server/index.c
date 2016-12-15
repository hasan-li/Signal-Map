#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>


void editFile(int x, float xm, int y, float ym, int s) {
//void editFile(int x) {
    FILE *fp;
    
    char buffer[200]; // assuming POSIX
    int temp_x;
    char x_folder[40], tempx[40];
    if (x < 0){
        x = x * (-1);
        sprintf(tempx, "%d", x);
        strcpy(tempx, "n");
        strcat(x_folder, tempx);
    }
    else {
        sprintf(tempx, "%d", x);
    }
    printf("<br /> inside func after oper %s ", tempx);
    
    strcpy(buffer, "data/");
    strcat(buffer, tempx);
    strcat(buffer, ".txt");
    
    
//    sprintf(buffer, "data/%s.txt", tempx);
    printf("<br /> %s ", buffer);
    
    fp = fopen(buffer, "w+");
    
//    fp = fopen("data/32.txt", "w+");
    
    if (fp) {
//        fprintf(fp, "This is testing...\n");
//        fputs("This is testing for fputs...\n", fp);
        
        fprintf(fp, "%f|%d|%f|%d\n", xm, y, ym, s);
        
        fclose(fp);
    } else {
        fprintf(stderr,"error opening file \"%s\"\n",buffer);
        perror("error opening file.");
    }
}



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



int updateSignalStrength(){
    
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
    printf("http://www.wheredoyougo.net/map/ag93aGVyZS1kby15b3UtZ29yEQsSCE1hcEltYWdlGNL0_wIM.png");
    
    

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
    
    
    
    
    
//    getting decimal and mantissa of x
    double x, y, x_mantissa, x_decimal, y_mantissa, y_decimal;
    int s;
    x = atof(queryData[0]);
    x_mantissa = modf(x, &x_decimal);
    
    y = atof(queryData[1]);
    y_mantissa = modf(y, &y_decimal);
    
    s = atoi(queryData[2]);
    
    printf("%d <br />", s);

    
//    printf("Integral part = %lf\n", x_decimal);
//    printf("Fraction Part = %lf \n", x_mantissa);
    
    
    int int_x, int_y;
    
    int_x = (int)x_decimal;
    int_y = (int)y_decimal;
//    printf("decimal part = %d\n", int_x);
//    printf("decimal part = %d\n", int_y);
    
    
    
    
    
    editFile(int_x, x_mantissa, int_y, y_mantissa, s);
//    editFile(int_x);
//    getData(x);
    
    
	return 0;
}