//rtl_fm -M nfm -s 22050 -f 173.255.000M -A fast -g 49.60 | multimon-ng -t raw -a POCSAG1200 -f alpha /dev/stdin >> /dev/stdout

#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdlib.h> 
#include <string.h>

void send(char* message) {
    struct sockaddr_in serv_addr;
    int sockfd, slen=sizeof(serv_addr);
    
    if ((sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1) {
        fprintf(stderr, "socket \n");
        exit(1);
    }

    bzero(&serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(50000);
    char addr[] = "192.168.1.255";
    
    if (inet_aton(addr, &serv_addr.sin_addr) == 0) {
        fprintf(stderr, "inet_aton() failed\n");
        exit(1);
    }

    if (sendto(sockfd, message, 1024, 0, (struct sockaddr*)&serv_addr, slen) == -1) {
        fprintf(stderr, "sendto() \n");
        exit(1);
    }

    close(sockfd);
}

