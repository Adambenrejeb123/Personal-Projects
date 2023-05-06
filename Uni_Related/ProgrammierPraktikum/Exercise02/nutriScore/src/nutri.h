#ifndef NUTRI_H_
#define NUTRI_H_
#include <vector>
#include <string>
#include <tuple>
#include <iostream>

using Meal = std::tuple<std::string, char, size_t>;

/**
 * @brief getUnhealthy calculates and returns the more unhealthy of both given meals
 * @param mealA first meal to compare
 * @param mealB second meal to compare
 * @return the more unhealthy mealth of both
 */
const Meal& getUnhealthier(const Meal& mealA, const Meal& mealB);

/**
 * @brief analyzeMeals calculates the number of the given meals which were healthy and
 * what meal was the most unhealthy
 * @param os output stream to write result on
 * @param meals array of tuple containing information of the meals
 */
void analyzeMeals(std::ostream& os, const std::vector<Meal>& meals);


#endif /* NUTRI_H_ */

