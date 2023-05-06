/**
 * @file decipherer.cpp
 */

#include "decipherer.h"
#include <string>
#include <iostream>
#include <map>
#include <tuple>
#include <utility>

std::string decipherMessage(const std::string& codedMessage, const std::map<char, char>& cipher)
{
    int sizeOfString = codedMessage.size() ;
    std::string result = "";
    for(int i = 0; i < sizeOfString; i++){
        char value = cipher.at(codedMessage[i]);
        int sumOfASCII = static_cast<int>(codedMessage[i]) + static_cast<int>(value);
        result += static_cast<char>(sumOfASCII);
    }
    return result;
}

std::string removeErrors(const std::string& messageWithErrors)
{
    
    // TODO: Implement here (find wrong character)!
    
    std::string result;
    std::map<char,int> Frequency;
    for(int i = 0 ; i < messageWithErrors.size(); i++){
        int counter=0;
        for(int j = 0 ; j < messageWithErrors.size(); j++){
            if(messageWithErrors[i] == messageWithErrors[j]) counter++;
        }
        if(Frequency.find(messageWithErrors[i]) != Frequency.end()){
            //Element exists
            continue;
        }
        else{
            //Element does not exist already
            Frequency.emplace(std::make_pair(messageWithErrors[i],counter));
        }
    }
    int Minimum = 0;
    char MinChar;
    //finding the least frequent element
    for(auto itr = Frequency.begin();itr != Frequency.end();itr++){
        if(itr == Frequency.begin()){
        MinChar = itr->first;
        Minimum = itr->second;
        }
        else if(Minimum > itr->second){
            MinChar = itr->first;
            Minimum = itr->second;
        }
    }
    // TODO: Implement here (correct message)!
    for(int f = 0 ; f < messageWithErrors.size();f++){
        if(messageWithErrors[f] != MinChar ){
            result += messageWithErrors[f];
        }
    }
    return result;
}

