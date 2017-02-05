#include<stdio.h>
#include <windows.h>
int main(){
	int a;
	char s[10];
	printf("1\n");
	scanf("%d", &a);
	getchar();
	Sleep(10000);
	gets(s);
	printf("2\n");
	printf("%s\n", s);
	printf("%d\n", a);
	getchar();
	return 0;
}