#include <fstream>
#include <iostream>
#include <string>
#include <vector>
#include "newpaste.h"

int main(int argc, char* argv[]) {
//Vector for combination
  std::vector<std::string> resultvector;
  for (int i = 1; i < argc; i++){
    std::vector<std::string> lines = readFile(argv[i]);
    combineLines(resultvector, lines);
  }
//Printing and Itirating
  for(auto itr = resultvector.begin(); itr != resultvector.end(); itr ++){
  std::cout << *itr << std::endl;
  }
  return 0;
}


