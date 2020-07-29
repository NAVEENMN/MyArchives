#include<iostream>
/*
 * 	reverse a string
 */

using namespace std;

void reverse(char *str){

}


int main(){
	string *data;
	char *ptr;
	cout<<"enter a string";
	cin >>	data;
	cout << data;
	cout<<endl;
	ptr = &data;
	reverse(ptr);
	return 0;
}
