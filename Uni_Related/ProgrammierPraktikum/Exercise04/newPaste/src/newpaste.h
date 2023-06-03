
#include <fstream>
#include <iostream>
#include <string>
#include <vector>

std::vector<std::string> readFile(std::istream& is);
std::vector<std::string> readFile(const std::string& name);
void combineLines(std::vector<std::string>& resultLines, const std::vector<std::string>& fileLines);

