/*
 *  find_recently_used_file.c
 *  Developed for EMC Interview Screeing test
 *
 *  Created by Naveen Mysore on 2/5/15.
 */

/*
 * USAGE : gcc find_most_recent.c -o find_most_recent.out
 *         find_most_recent.out [path and filename] [path and filename]
 *
 * @desc : This program finds the most recently accessed file in O(n) run time
 *         It collects year, month, date, hour, minute and seconds of all files
 *         then we collect the files in most recent year removing all other files
 *         in the remaning files we collect all most recent month files and so on.
 *         All information about a file is held in a node in linked list.
 *
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

typedef struct file_list *file_list_ptr;
typedef file_list_ptr file_ptr;
struct file_list{
    char* filename;
    file_ptr NEXT;
    int year;
    int month;
    int date;
    int hour;
    int minute;
    int second;
    int status;
};

/*
 *  This function creates a node and adds files information to it
 *  Input: @param1 - String - file name
 *         @param2 - Int - year
 *         @param3 - Int - month
 *         @param4 - Int - date
 *         @param5 - Int - hour
 *         @param6 - Int - minute
 *         @param6 - Int - seconds
 *  Return: file_ptr - pointer to new node
 */
file_ptr file_list_insert(char* filename, int year, int month, int date, int hour, int minute, int seconds){
    file_ptr new_node;
    file_ptr end_node;
    new_node = (file_ptr) malloc(sizeof(struct file_list));
    new_node -> filename = filename;
    new_node -> year = year;
    new_node -> month = month;
    new_node -> date = date;
    new_node -> hour = hour;
    new_node -> minute = minute;
    new_node -> second = seconds;
    new_node -> status = 1;
    end_node = (file_ptr) malloc(sizeof(struct file_list));
    end_node -> filename = "END";
    end_node -> NEXT = NULL;
    new_node -> NEXT = end_node;
    return new_node;
}

/*
 *  This function prints the file list
 *  Input: @param1 - file_ptr - pointer to head of the linked list
 *  Return: void
 */
void print_the_list(file_ptr head){
    file_ptr node;
    node = head -> NEXT;
    while(node -> NEXT != NULL){
        printf("filename: %s \n", node -> filename);
        //printf("modified on: %d : %d : %d \n", node -> month, node -> date, node -> year);
        //printf("modified at: %d : %d : %d \n", node -> hour, node -> minute, node -> second);
        //printf("----------------------------------\n");
        node = node -> NEXT;
    }
}

/*
 *  This function removed a node from file list
 *  Input: @param1 - file_ptr- pointer to previous node of the node which needs
 *                   to be removed
 *  Return: void
 */
void remove_a_node(file_ptr node){
    file_ptr next_node = node -> NEXT -> NEXT;
    free(node->NEXT);
    node -> NEXT = next_node;
}

/*
 *  This function counts the number of nodes in file list
 *  Input: @param1 - file_ptr - pointer to head of the linked list
 *  Return: INT - number of nodes in the list
 */
int number_of_files_in_list(file_ptr node){
    int count = 0;
    node = node -> NEXT;
    while(node -> NEXT != NULL){
        node = node -> NEXT;
        count += 1;
    }
    return count;
}

/*
 *  This function finds and removes all the files which are not recent
 *  Input: @param1 - file_ptr - pointer to head of the linked list
 *  Return: INT - number of nodes in the list
 */
void find_most_recent(file_ptr head){
    file_ptr node;
    int year = 0;
    int month = 0;
    int date = 0;
    int hour = 0;
    int minute = 0;
    int second = 0;
    
    /* Step followed in this order
     * Find most recent year -> remove all files from list which have year < recent year
     * Find most recent month -> remove all files from list which have month < recent month
     * Find most recent date -> remove all files from list which have date < recent date
     * Find most recent hour -> remove all files from list which have hour < recent hour
     * Find most recent minute -> remove all files from list which have minute < recent minute
     * Find most recent seconds -> remove all files from list which have seconds < recent seconds
     */
    
    /*
     * Knock out all files which are not from most recent year
     */
    node = head -> NEXT;
    while(node -> NEXT != NULL){
        if (node -> year > year) year = node -> year ; // finds the recent year which is maximum
        node = node -> NEXT;
    }
    node = head;
    while(node -> NEXT != NULL){
        if (node -> NEXT -> year < year && strncmp(node -> NEXT -> filename, "END", 3) != 0){
            //printf("removing file: %s - not in recent year\n", node-> NEXT -> filename);
            remove_a_node(node);
        }else{
            node = node -> NEXT;
        }
    }
    /*
     * Knock out all files which are not from most recent month
     */
    if(number_of_files_in_list(head) == 1) return;
    node = head -> NEXT;
    while(node -> NEXT != NULL){
        if (node -> month > month) month = node -> month ; // finds the recent month which is maximum
        node = node -> NEXT;
    }
    node = head;
    while(node -> NEXT != NULL){
        if (node -> NEXT -> month < month && strncmp(node -> NEXT -> filename, "END", 3) != 0){
            //printf("removing file: %s - not in recent month \n", node-> NEXT -> filename);
            remove_a_node(node);
        }else{
            node = node -> NEXT;
        }
    }
    /*
     * Knock out all files which are not from most recent date
     */
    if(number_of_files_in_list(head) == 1) return;
    node = head -> NEXT;
    while(node -> NEXT != NULL){
        if (node -> date > date) date = node -> date ; // finds the recent date which is maximum
        node = node -> NEXT;
    }
    node = head;
    while(node -> NEXT != NULL){
        if (node -> NEXT -> date < date && strncmp(node -> NEXT -> filename, "END", 3) != 0){
            //printf("removing file: %s - not in recent date\n", node-> NEXT -> filename);
            remove_a_node(node);
        }else{
            node = node -> NEXT;
        }
    }
    /*
     * Knock out all files which are not from most recent hour
     */
    if(number_of_files_in_list(head) == 1) return;
    node = head -> NEXT;
    while(node -> NEXT != NULL){
        if (node -> hour > hour) hour = node -> hour ; // finds the recent hour which is maximum
        node = node -> NEXT;
    }
    node = head;
    while(node -> NEXT != NULL){
        if (node -> NEXT -> hour < hour && strncmp(node -> NEXT -> filename, "END", 3) != 0){
            //printf("removing file: %s - not in recent hour\n", node-> NEXT -> filename);
            remove_a_node(node);
        }else{
            node = node -> NEXT;
        }
    }
    /*
     * Knock out all files which are not from most recent minute
     */
    if(number_of_files_in_list(head) == 1) return;
    node = head -> NEXT;
    while(node -> NEXT != NULL){
        if (node -> minute > minute) minute = node -> minute ; // finds the recent minute which is maximum
        node = node -> NEXT;
    }
    node = head;
    while(node -> NEXT != NULL){
        if (node -> NEXT -> minute < minute && strncmp(node -> NEXT -> filename, "END", 3) != 0){
            //printf("removing file: %s \n - not in recent minute", node-> NEXT -> filename);
            remove_a_node(node);
        }else{
            node = node -> NEXT;
        }
    }
    /*
     * Knock out all files which are not from most recent second
     */
    if(number_of_files_in_list(head) == 1) return;
    node = head -> NEXT;
    while(node -> NEXT != NULL){
        if (node -> second > second) second = node -> second ; // finds the recent second which is maximum
        node = node -> NEXT;
    }
    node = head;
    while(node -> NEXT != NULL){
        if (node -> NEXT -> second < second && strncmp(node -> NEXT -> filename, "END", 3) != 0){
            //printf("removing file: %s - not in recent second\n", node-> NEXT -> filename);
            remove_a_node(node);
        }else{
            node = node -> NEXT;
        }
    }
    
}

/*
 *  This function extracts date and time information and inserts to linked list
 *  Input: @param1 - String - filename
 *         @param2 - file_ptr - pointer to tail to linked list
 *  Return: file_ptr - pointer to new tail
 */
file_ptr insert_date_time_to_list(char filename[], file_ptr tail){
    file_ptr node;
    node = NULL;
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
        node = file_list_insert(filename, year, month, date, hour, minute, seconds);
        tail -> NEXT = node;
        
    }else{
        printf("error getting date/time\n");
    }
     return node;
}

int main(int argc, const char * argv[])
{
    file_ptr head;
    file_ptr tail;
    FILE *file;
    head = (file_ptr) malloc(sizeof(struct file_list));
    head -> filename = "HEAD";
    tail = head;
    char *file_names[argc-1];
    int file_counter = 0;
    if(	argc < 2){
	printf("usage : %s [path and filename] [path and filename] ... \n", argv[0] );
    }else{
	for(int i = 1; i < argc ; i++){
    		file_names[i] = argv[i];
	}
    	for(int i = 1; i < argc; i++){
        	file = fopen(file_names[i], "r");
        	if(file){
            		tail = insert_date_time_to_list(file_names[i], tail);
            		fclose(file);
        	}else{
            		printf("file %s doesnot exists \n", file_names[i]);
            	file_counter += 1;
        	}
    	}
    	//print_the_list(head);
    	if(file_counter != 4){
        	find_most_recent(head);
        	printf("The most recent file is: \n");
        	print_the_list(head);
    	}else{
        	printf("All files were not found. \n");
    	}
   }
    return 0;
}

