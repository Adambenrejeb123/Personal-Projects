#ifndef PARSER_H_
#define PARSER_H_

#include <vector>
#include <string>
#include <tuple>
#include <iostream>

/**
 * Splits a stream into a vector of strings.
 * Each occurance of the character @a delim is a splitting position.
 */
std::vector<std::string> split(std::istream& is, char delim);

using IndexedString = std::tuple<size_t/*index*/, 
                                 std::string/*content*/>;

/**
 * Parses a csv file. 
 * Returns a vector of IndexedString contained in said file.
 */
std::vector<IndexedString> parse(std::istream& is);

/**
 * Writes the sentence represented by the vector of IndexedString to a stream.
 */
void writeSentence(std::ostream& os, 
                  const std::vector<IndexedString>& strings);
                  

std::vector<IndexedString> parseFile(const std::string& filename);

#endif /* PARSER_H_ */

