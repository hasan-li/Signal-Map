#include <stdio.h>
int main(){
	double step = 0.000005;
	double minx = -90.000000;
	double maxx = 90.000000;
	double miny = -180.000000;
	double  maxy = 180.000000;
	double i=0;
	for (i=miny; i <= maxy; i=i+step){
		printf("miny %f \n", i);
	}
	return 0;
}
