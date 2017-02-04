/* Example from http://en.wikibooks.org/wiki/C_Programming/Networking_in_UNIX */
/* Adapted by Rainer Schoenen 2013 */
/* compile: gcc -o server server.c */
/* gcc -pthread -o server_threaded server_threaded.c */
/* Useful: ulimit -a */
/* netstat -nap | grep 7777 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <pthread.h>

#define NUM_THREADS     5
#define PORTNUM 7777

// global, for quick results
int connectionsocket;

struct thread_data {
   int  thread_id;
   int  socket;
};
struct thread_data thread_data_array[NUM_THREADS];

void *ThreadFunction(void *threadarg)
{
   long tid;
   char msg[] = "This is the thread #### speaking !\n";
   struct thread_data *my_data;
   my_data = (struct thread_data *) threadarg;
   tid = my_data->thread_id;
   printf("This is thread #%ld: starting\n", tid);
   int localsocket = my_data->socket;
   sprintf(msg,"This is the thread #%ld speaking\n", tid);
   send(localsocket, msg, strlen(msg), 0); 
   sleep(5); // mimick 5 seconds latency
   close(localsocket);
   printf("This is thread #%ld: done.\n", tid);
   pthread_exit(NULL);
}


int main(int argc, char *argv[])
{
    pthread_t threads[NUM_THREADS];
    long t=0; // thread id
    char msg[] = "This is the server speaking !\n";
 
    struct sockaddr_in dest; /* socket info about the machine connecting to us */
    struct sockaddr_in serv; /* socket info about our server */
    int listeningsocket;     /* socket used to listen for incoming connections */
    socklen_t socksize = sizeof(struct sockaddr_in);
 
    memset(&serv, 0, sizeof(serv));           /* zero the struct before filling the fields */
    serv.sin_family = AF_INET;                /* set the type of connection to TCP/IP */
    serv.sin_addr.s_addr = htonl(INADDR_ANY); /* set our address to any interface */
    serv.sin_port = htons(PORTNUM);           /* set the server port number */    
 
    listeningsocket = socket(AF_INET, SOCK_STREAM, 0);
 
    /* bind serv information to mysocket */
    bind(listeningsocket, (struct sockaddr *)&serv, sizeof(struct sockaddr));
 
    /* start listening, allowing a queue of up to N pending connection */
    listen(listeningsocket, NUM_THREADS);

    do {
        connectionsocket = accept(listeningsocket, (struct sockaddr *)&dest, &socksize); // blocking wait
        printf("Incoming connection from %s.\n", inet_ntoa(dest.sin_addr));
        //send(consocket, msg, strlen(msg), 0); 
        if (++t < NUM_THREADS) {
            printf("In main: creating thread %ld\n", t);
            thread_data_array[t].thread_id = t;
            thread_data_array[t].socket = connectionsocket;
            int rc = pthread_create(&threads[t], NULL, ThreadFunction, (void *)&thread_data_array[t]);
            if (rc) {
               printf("ERROR; return code from pthread_create() is %d\n", rc);
               exit(-1);
            }
        } else {
            printf("ERROR; no more threads\n");
            exit(-1);
        }
    } while(connectionsocket);
 
    //close(consocket);
    close(listeningsocket);
    /* Last thing that main() should do */
    pthread_exit(NULL);
    return EXIT_SUCCESS;
}
