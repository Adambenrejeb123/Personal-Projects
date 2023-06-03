#include "newpaste.h"
#include <string>
#include <vector>
#include <iostream>
#include <fstream>

void combineLines(std::vector<std::string>& fileLines, const std::vector<std::string>& newLines) {
    //For the layout lets say size is 20 
    int maxLength = 20;
    int condition1 = 0;
    int condition2 = 0;
    while(condition1 < fileLines.size() && condition2 < newLines.size()){
        std::string& line1 = fileLines[condition1];
        std::string& line2 = const_cast<std::string&>(newLines[condition2]);
        int length1 = line1.length();
        int length2 = line2.length();
        if (length1 < maxLength && length1+length2 <= maxLength){
            line1 += std::string(maxLength-length1, ' ');
            line1 += line2;
            condition2++;
        }
        condition1++;
    }
    while(condition2 < newLines.size()){
        fileLines.push_back(newLines[condition2++]);
    }
    //Checking to see if there is a name or not
    if(fileLines.size() % 2 != 0){
        fileLines.push_back("");
    }
}

//Wir Schreiben hier 2 readFiles fur beide falle
std::vector<std::string> readFile(std::istream& is){
    std::vector<std::string> lines;
    std::string line;
    while (std::getline(is, line)) {
        lines.push_back(line);
    }
    return lines;
}
//Is there even a file here?
std::vector<std::string> readFile(const std::string& name){
    std::ifstream file(name);
    if (!file) {
        std::cerr << "No file found"<< std::endl;
        exit(1);
    }
    return readFile(file);
}


