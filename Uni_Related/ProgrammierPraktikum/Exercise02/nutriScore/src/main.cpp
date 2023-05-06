/**
 * Main file. Should not be modified.
 * @file main.cpp
 */

#include "nutri.h"
#include <vector>
#include <string>
#include <tuple>

using Meal = std::tuple<std::string, char, size_t>;

/**
 * Main function. Creates multiple vectors of meals and calls the analyzeMeals function for each
 */
int main()
{
    std::vector<Meal> meals;
    meals.emplace_back("Vollkorn Fischstaebchen", 'A', 5);
    meals.emplace_back("Broccoli-Nudelauflauf", 'B', 6);
    meals.emplace_back("Kartoffelsalat", 'C', 5);
    meals.emplace_back("Schwaebische Festtagssuppe", 'D' ,5);
    meals.emplace_back("Nougatcreme", 'E' , 4);
    meals.emplace_back("Milchschnitte", 'E', 3);
    analyzeMeals(std::cout, meals);
    meals.emplace_back("Backfisch-Stäbchen", 'C', 15);
    meals.emplace_back("Schoko-Sahne-Pudding", 'D',9);
    meals.emplace_back("Rahm-Spinat", 'A', 42);
    analyzeMeals(std::cout, meals);
    std::vector<Meal> moreMeals;
    moreMeals.emplace_back("Broccoli-Nudelauflauf", 'B', 6);
    moreMeals.emplace_back("Rahm-Spinat", 'A', 42);
    analyzeMeals(std::cout, moreMeals);

    return 0;
}

