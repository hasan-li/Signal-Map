#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include "data.h"


int main (void){
	printf("Content-type: text/html\n\n");
	printf("CGI-Program has started <br />");
    
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
        printf("%s <br />", queryData[i]);
        
    }
    
    
    
    
//    getting decimal and mantissa of x
    double x, y, x_mantissa, x_decimal;

    x = atof(queryData[0]);
    x_mantissa = modf(x, &x_decimal);

    printf("Integral part = %lf\n", x_decimal);
    printf("Fraction Part = %lf \n", x_mantissa);
    
    
    
	return 0;
}