#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>


#define PATH "generated/index.txt"
#define FILE_INPUT "This is olololo"

int main (void){
	printf("Content-type: text/html\n\n");
	printf("CGI-Program has started\n");
	size_t writeResult = 0;
	char fileInput[] = FILE_INPUT;
    
    char m[200];
	char *data = malloc(200);
    data=getenv("QUERY_STRING");
    sscanf(data,"%s", m);
    printf("%s", m);
    
//    FILE *pGenerated = fopen(PATH, "w+");
//    
//	if(pGenerated == NULL){
//		printf("Could not open file!\n");
//		return;
//	}
//    
//	writeResult = fwrite(fileInput, strlen(fileInput), 
//                         strlen(fileInput), pGenerated);
//	if(writeResult != strlen(fileInput)){
//		printf("Error while fwrite occured\n");
//		fclose(pGenerated);
//		return;
//	}
//	fclose(pGenerated);
	return 0;
}