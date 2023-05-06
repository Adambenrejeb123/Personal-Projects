/**
 * @file nutri.cpp
 */
#include "nutri.h"

using Meal = std::tuple<std::string, char, size_t>;

/*
the function will take 2 Meals and decides which one is unhealthier
1- E gets doubled
2- D does not
3- compare and return the biggest number
*/
const Meal& getUnhealthier(const Meal& mealA, const Meal& mealB) 
{
    char TypeofA = std::get<1>(mealA);
    char TypeofB = std::get<1>(mealB);
    int sizeofA = std::get<2>(mealA);
    int sizeofB = std::get<2>(mealB);
    if(TypeofA == 'E') sizeofA *= 2;
    if(TypeofB == 'E') sizeofB *= 2;
    if(sizeofA > sizeofB) return mealA;
    if(sizeofA < sizeofB) return mealB;
    else return mealA; 
}

void analyzeMeals(std::ostream& os, const std::vector<Meal>& meals)
{
    int healthycounter = 0;
    std::string unhealthiername;
    int unhealthierIndex = 0;
    if(meals.empty()){
        std::cout << "The meal list was empty" << "\n";
    }
    else {
        for(int i = 0 ; i < meals.size(); i++){
            if(std::get<1>(meals[i]) != 'E' && std::get<1>(meals[i]) != 'D' && std::get<1>(meals[i]) != 'C'){
                healthycounter++;
            }
            else if(std::get<1>(meals[i]) != 'A' && std::get<1>(meals[i]) != 'B' && std::get<1>(meals[i]) != 'C'){
                if(unhealthiername.empty()){
                    unhealthiername = std::get<0>(meals[i]);
                    unhealthierIndex = i;
                }
                else{
                    unhealthiername = std::get<0>(getUnhealthier(meals[unhealthierIndex],meals[i]));
                    if(unhealthiername == std::get<0>(meals[i])){
                        unhealthierIndex = i;
                    }
                }
            }
        }
    }
    std::cout << "from " << meals.size() <<" meals ," << healthycounter << " of them are healthy" << "\n";
    if(unhealthiername.empty()) std::cout << "There are no unhealthy meals! keep at it!" << "\n";
    else {
        std::cout << "We recommend not eating " << unhealthiername << " for a while as it was already consumed " << std::get<2>(meals[unhealthierIndex]) << " times" << "\n";
    }
}

