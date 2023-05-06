/**
 * Main file. Should not be modified.
 * @file main.cpp
 */

#include "parser.h"
#include <vector>

/**
 * Main function. Takes the name of the csv-file as first commandline argument.
 * Calls runParser with the filename.
 */
int main(int argv, char** argc)
{
    std::vector<std::string> arguments(argc, argc + argv);

    if(arguments.size() > 1) {
        auto entries = parseFile(arguments[1]);
        writeSentence(std::cout, entries);
    } else {
        std::cout << "No file specified." << std::endl;
    }

    return 0;
}

