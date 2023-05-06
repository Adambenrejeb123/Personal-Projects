/**
 * Main file. Should not be modified.
 * @file main.cpp
 */

#include "../src/parser.h"
#include <vector>
#include <string>
#include <sstream>
#include <cassert>

/**
 * Main test function. Calls runParser with the filename.
 */
int main() 
{
    auto entries = parseFile("../csv-file.csv");

    std::stringstream s;
    writeSentence(s, entries);
    std::string result = s.str();
    assert(result == std::string("Hallo, wie geht es?\n"));

    return 0;
}

