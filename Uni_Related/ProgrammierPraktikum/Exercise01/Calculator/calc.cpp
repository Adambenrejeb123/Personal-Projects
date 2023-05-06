#include <string>
#include <iostream>
using namespace std;
int main()
{
    int numberA;
    int numberB;
    std::string operation;    

    cout << "Please Write your first number:" << "\n";
    cin >> numberA;
    //Integer Control on Number A
    if(cin.fail()){
        cout << "Input was Ivalid" << endl;
        return 1;
    }
    cout << "Please Write your second number:" << "\n";
    cin >> numberB;
    //Integer Control on Number B
      if(cin.fail()){
        cout << "Input was Ivalid" << endl;
        return 1;
    }
    cout << "For and addition write add and for a substraction write sub" << "\n";
    cin >> operation;
    while(operation != "sub" && operation != "add" ){
        cout << "Please enter a valid input:" << "\n";
        cin >> operation;
    }

    if(operation == "sub"){
        cout <<"Result:" << numberA - numberB<<"\n";
    }
    else cout<<"Result:" << numberB + numberA<< "\n";
    return 0;
}


