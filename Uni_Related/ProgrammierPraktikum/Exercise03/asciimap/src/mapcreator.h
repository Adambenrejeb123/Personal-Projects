#ifndef MAP_CREATOR_H_
#define MAP_CREATOR_H_

#include <map>
#include <string>
#include <tuple>

/**
 * creates a coded message and a map for decoding the message
 */
std::tuple<std::string, std::map<char, char>> createCipherMap(std::istream& is);

#endif

