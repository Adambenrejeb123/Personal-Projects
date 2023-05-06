/**
 * @file mapcreator.cpp
 */
 
#include "mapcreator.h"
#include <algorithm>
#include <fstream>
#include <utility>
#include <string>
#include <vector>
#include <iostream>
#include <map>
#include <random>
#include <tuple>

static std::random_device rd;
static std::mt19937 re(rd());
            
std::tuple<std::string, std::map<char, char>> createCipherMap(std::istream& is)
{
    std::vector<unsigned char> encryptionCipher(256);
    std::iota(encryptionCipher.begin(), encryptionCipher.end(), 0);
    std::shuffle(encryptionCipher.begin(), encryptionCipher.end(), re);

    std::map<char, char> decryptionCipher;
    std::string ciphertext;
    for(char c; is.get(c);)
    {
        const char replacement = static_cast<char>(encryptionCipher[static_cast<unsigned char>(c)]);
        decryptionCipher.try_emplace(replacement, c - replacement);
        ciphertext += replacement;
    }

    return std::make_tuple(ciphertext, decryptionCipher);
}

