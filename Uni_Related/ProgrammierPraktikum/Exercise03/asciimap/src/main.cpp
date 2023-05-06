/**
 * Main file. Should not be modified.
 * @file main.cpp
 */
 
#include <fstream>
#include <utility>
#include <string>
#include <vector>
#include <iostream>
#include <map>
#include <tuple>
#include "mapcreator.h"
#include "decipherer.h"


/**
 * Main function. Takes the name of a txt-file as first commandline argument, 
 * uses "asciiart.txt" if none is specified. Calls encoding, decoding and error-removal functions
 * then writes the final message to the console.
 */
int main(int argv, char** argc)
{
    std::vector<std::string> arguments(argc, argc + argv);

    if(arguments.size() == 1)
    {
        arguments.emplace_back("../asciiart.txt");
    }
    
    std::ifstream fs(arguments[1]);
    if(!fs.is_open())
    {
        std::cout << "File " << arguments[1] << " not found\n";
    }
    else
    {
        auto cipher = createCipherMap(fs);
        const std::string messageWithErrors = decipherMessage(std::get<0>(cipher), std::get<1>(cipher));
#if false
        std::cout << std::get<0>(cipher);
        std::cout << messageWithErrors;
#endif
        std::string finalMessage = removeErrors(messageWithErrors);
        std::cout << finalMessage;
    }
    return 0;
}

