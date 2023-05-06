#ifndef DECIPHERER_H_
#define DECIPHERER_H_

#include <string>
#include <map>			

/**
 * Deciphers codedMessage using the map paramater as a cipher key
 */
std::string decipherMessage(const std::string& codedMessage, const std::map<char, char>& cipher);

/**
 * removes the least times appearing character from the given string and returns it
 */
std::string removeErrors(const std::string& messageWithErrors);

#endif

