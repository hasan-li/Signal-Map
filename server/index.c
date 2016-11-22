#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include "data.h"

int main (void){
	printf("Content-type: text/html\n\n");
	printf("CGI-Program has started\n");
    
    char m[200];
	char *data = malloc(200);
    data=getenv("QUERY_STRING");
    sscanf(data,"%s", m);
    printf("%s", m);
    
    printf("%f", x_n9[1]);
    
    
    
	return 0;
}