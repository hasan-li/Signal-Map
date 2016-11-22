#include <stdio.h>

int main(){
    
    double step = 0.000005;  //degrees between pixels
    double minx = -90.000000;
    double maxx = 90.000000;
    double miny = -180.000000;
    double maxy = 180.000000;
    
    double coordinates[2592000000000000];
    
    FILE *f = fopen("file.txt", "w");
    
    for (double i = miny; i <= maxy; i+=step){
//        printf("%f; ", i);
        fprintf(f, "%f;", i);
        coordinates[]
    }
    
    fclose(f);
}
