#ifndef FLOORPLAN_CELL_H
#define FLOORPLAN_CELL_H

#include <string>

/**
 * @brief Coordinates are points in a 2-dim. space.
 */
struct Coordinates
{
public:
    size_t x;
    size_t y;
    Coordinates (size_t x, size_t y) : x(x), y(y) { }
    Coordinates () { }
    bool operator< ( const Coordinates& c);
};

/**
 * @brief A Cell is a rectangular area in a floorplan.
 * @see Floor
 */
class Cell
{
private:
    unsigned xlength;
    unsigned ylength;
    std::string name;
public:
    Cell (unsigned xlength, unsigned ylength, std::string name) : xlength(xlength), ylength(ylength), name(name) { }
    unsigned int getXlength() const;
    unsigned int getYlength() const;
    void rotate();
    const std::string& getName () const;
    friend bool operator== (const Cell& c1, const Cell& c2);
};

#endif //FLOORPLAN_CELL_H

