#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include "data.h"


//float removeSubstring(char *s,const char *toremove) {
//    float *ret;
//    while( s=strstr(s,toremove) ){
//        ret = (float) memmove(s,s+strlen(toremove),1+strlen(s+strlen(toremove)));
//    }
//    return ret;
//}

int main (void){
	printf("Content-type: text/html\n\n");
	printf("CGI-Program has started <br />");
    
    char m[200];
	char *data = malloc(200);
    data=getenv("QUERY_STRING");
    sscanf(data,"%s", m);
//    printf("%s", m);
    
    
    int i = 0;
    char *token = strtok(m, "&");
    char *queryData[3];
    
    
    
//    printf( " %s\n", token );
    
    
    while (token != NULL) {
        queryData[i++] = token;
        token = strtok (NULL, "&");
    }
    
    
    const char ch = '=';
    char *ret[3];
    
    for (i = 0; i < 2; ++i) {
        if(strstr(queryData[i], "=") != NULL) {
            queryData[i] = queryData[i] + 2;
        }
        printf("%s <br />", queryData[i]);
        
    }
    
    
    
    
    
    
    
	return 0;
}