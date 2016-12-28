# Signal-Map
GANTT chart for project members, please add it and manage
https://drive.google.com/open?id=0B_C6bMaekulYcFJKcjUwaGtyejQ



## Coordinate System

## Example:
       long.: 32km (53.571948, 9.735556; 53.558490, 10.221014)
       lat.:  30km (53.681894, 10.000601; 53.415086, 9.980688)
    
    
### Step is 22m:
       22m = (53.561544, 10.017497; 53.561396, 10.017734) - (00.000000, 00.000000; 00.000150, 00.000150)

### Displaying layer on map
       53.672998, 9.784830-------------------26.35 km--------------------53.672998, 10.184830
            	x	                                        						x
	            |                                       							|
	            |                                       							|
	            |                                       							|
            24.46 km          		        	Hamburg     		             24.46 km
	            |                                       							|
                |                                       							|
                |                                       							|              
	            x							                                        x
      53.452998, 9.784830--------------------26.49 km---------------------53.452998, 10.184830



### Format of file name to store data: 
    xx.xm_yy.ym.dat

Note: xm and ym can only have values 3|6|9.

Dat file stores values in following format:

    64 52 34 -45 -82 441 12 0 121 7 -14 .. 0 121 0 21 -147
    64 52 34 -45 -82 441 12 0 121 7 -14 .. 0 121 0 21 -147
    64 52 34 -45 -82 441 12 0 121 7 -14 .. 0 121 0 21 -147
    64 52 34 -45 -82 441 12 0 121 7 -14 .. 0 121 0 21 -147
    64 52 34 -45 -82 441 12 0 121 7 -14 .. 0 121 0 21 -147
    64 52 34 -45 -82 441 12 0 121 7 -14 .. 0 121 0 21 -147
    
    
Each dat file is 2000*2000 matrix with 4,000,000 values of signal strength. 

Value of strength can be found from mantissa part of x and y:

    int * locateStrength(double x, double y){
        double step = 0.000150;
        static int coord_step_val[2];

        coord_step_val[0] =(int)floor(x/step);
        coord_step_val[1] =(int)floor(y/step);

        return coord_step_val;
    }
    
It takes less than 5ms to create new file with one value or to update existing file.


#TODOs

1. Optimize code
2. Add security check on server side