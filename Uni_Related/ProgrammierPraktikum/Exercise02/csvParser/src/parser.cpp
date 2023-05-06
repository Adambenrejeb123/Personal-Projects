/**
 * @file parser.cpp
 */
#include "parser.h"
#include <sstream>
#include <fstream>
#include <utility>
#include <string>

std::vector<std::string> split(std::istream& is, char delim)
{
    std::vector<std::string> result;
    std::string linebuffer;
    while(std::getline(is,linebuffer)){
         std::string SeperatedString;
        for(int i = 0; i < linebuffer.size(); i++){
            if(linebuffer[i] == delim){
                result.push_back(SeperatedString);
                SeperatedString = "";
            } 
            else SeperatedString += linebuffer[i];
        }
        result.push_back(SeperatedString);
    }
    return result;
}

std::vector<IndexedString> parse(std::istream& is)
{
    std::vector<IndexedString> strings;
    bool IndexToggle = true;
    int Index =0;
    std::vector<std::string> NotIndexed = split(is,';');
    //Strings vector is filled but is not ordered according to the index
    for(int i = 0; i < NotIndexed.size(); i++){
        if(IndexToggle){
            Index = std::stoi(NotIndexed[i]);
            IndexToggle = false;
            continue;
        }
        IndexedString placeholder = std::make_tuple(Index,NotIndexed[i]);
        strings.push_back(placeholder);
        IndexToggle = true;
    }
    return strings;
}

void writeSentence(std::ostream& os, const std::vector<IndexedString>& strings)
{   
   int VectorSize = strings.size();
   size_t counter = 0;
   for(int i = 0; i < VectorSize; i++){
    for(int j = 0; j < VectorSize; j++){
        if(std::get<0>(strings[j]) == counter){
            os << std::get<1>(strings[j]) <<" ";
            counter++;
        }
    }
   }
}



/**
 * Parses the given input file.
 */
std::vector<IndexedString> parseFile(const std::string& filename)
{
    std::ifstream fs(filename);
    if(!fs.is_open())
    {
        std::cout<<"File "<<filename<<" not found\n";
        return {};
    }
    std::vector<IndexedString> entries = parse(fs);
    return entries;
}


