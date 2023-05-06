#include <iostream>
#include <string>

int main()
{
    std::string name;

    std::cout << "What is your given name? ";
    std::cin >> name;
    
    for(int count = 0; count < 10; ++count)
        std::cout<< "Hello " << name << "\n";
return 0;
}

