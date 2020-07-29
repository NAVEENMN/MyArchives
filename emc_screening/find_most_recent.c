/*
 *  find_recently_used_file.c
 *  Developed for EMC Interview Screeing test
 *  Created by Naveen Mysore on 2/5/15.
 *  URL : https://github.com/NAVEENMN/emc_screening
 */

/*
 * USAGE : gcc find_most_recent.c -o find_most_recent.out
 *         find_most_recent.out [path and filename] [path and filename]
 *
 * @desc : This program finds the most recently accessed file in O(n) run time
 *         It collects year, month, date, hour, minute and seconds of all files
 *         then we calculate epochs (time elapsed since 00:00:00 UTC till last file modified date/time) for each file 
 *	   we stored epoch`s in array and find the maximum epoch and its index
 *         uncomment all printf`s to see the execution
 */

/*
 * Program might fail:
 *    This program was tested on linux environment with gcc compiler, so the calls like strf on non linux 
 *    environment might return date and time in different format which might end the program
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>


/*
 *  This function extracts date and time information and calculates and retuens epochs of the file
 *  Input: @param1 - String - filename
 *         
 *  Return: long - epoch value of the file since last modified
 */
long get_epoch_of_file(char filename[]){
    char timeStr[ 100 ] = "";
    char temp[5];
    char tem[5];
    int month = 0;
    int year = 0;
    int date = 0;
    int minute = 0;
    int hour = 0;
    int seconds = 0;
    struct stat buf;
    struct tm t;
    time_t t_of_day;
    
    
    if (!stat(filename, &buf)){
        strftime(timeStr, 100, "%d-%m-%Y %H:%M:%S", localtime( &buf.st_mtime));
        //printf("\nLast modified date and time = %s\n", timeStr);
        // getting month
        temp[ 0 ] = timeStr[3];
        temp[ 1 ] = timeStr[4];
        temp[ 2 ] = '\0';
        strncpy(tem, temp, 3);
        month = atoi(tem);
        //getting year
        temp[ 0 ] = timeStr[6];
        temp[ 1 ] = timeStr[7];
        temp[ 2 ] = timeStr[8];
        temp[ 3 ] = timeStr[9];
        temp[ 4 ] = '\0';
        strncpy(tem, temp, 5);
        year = atoi(tem);
        //getting date
        temp[ 0 ] = timeStr[0];
        temp[ 1 ] = timeStr[1];
        temp[ 2 ] = '\0';
        strncpy(tem, temp, 3);
        date = atoi(tem);
        //getting hour
        temp[ 0 ] = timeStr[11];
        temp[ 1 ] = timeStr[12];
        temp[ 2 ] = '\0';
        strncpy(tem, temp, 3);
        hour = atoi(tem);
        //getting minute
        temp[ 0 ] = timeStr[14];
        temp[ 1 ] = timeStr[15];
        temp[ 2 ] = '\0';
        strncpy(tem, temp, 3);
        minute = atoi(tem);
        //getting second
        temp[ 0 ] = timeStr[17];
        temp[ 1 ] = timeStr[18];
        temp[ 2 ] = '\0';
        strncpy(tem, temp, 3);
        seconds = atoi(tem);
        
        t.tm_year = year;
        t.tm_mon = month;         
        t.tm_mday = date;         
        t.tm_hour = hour;
        t.tm_min = minute;
        t.tm_sec = seconds;
        t.tm_isdst = -1;     
        t_of_day = mktime(&t);
        // printf("seconds since the Epoch: %ld\n", (long) t_of_day);
        return (long) t_of_day;
    }else{
        printf("error getting date/time\n");
    }
    return (long) t_of_day;
}

int main(int argc, const char * argv[])
{
    FILE *file;
    char *file_names[argc-1];
    int file_counter = 0;
    long epochs[argc-1];
    long big_epoch = 0;
    int index = 0;
    if(	argc < 2){
	printf("usage : %s [path and filename] [path and filename] ... \n", argv[0] );
    }else{
	for(int i = 1; i < argc ; i++){
    		file_names[i] = argv[i];
	}
    	for(int i = 1; i < argc; i++){
        	file = fopen(file_names[i], "r");
        	if(file){
            		epochs[i] = get_epoch_of_file(file_names[i]);
            		fclose(file);
        	}else{
            		printf("file %s doesnot exists \n", file_names[i]);
            	file_counter += 1;
        	}
    	}
    	if(file_counter != argc-1){
        	for(int i = 0 ; i < argc ; i++ ){
                if (epochs[i] > big_epoch){
                    big_epoch = epochs[i];
                    index = i;
                }
            }
            printf("The most recent file is: %s\n", file_names[index]);
    	}else{
        	printf("All files were not found. \n");
    	}
   }
    return 0;
}

