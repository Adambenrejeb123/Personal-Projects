
#include "floor.h"
#include <cmath>
#include <queue>
#include <stack>
#include <random>
#include <algorithm>
#include <utility>
#include <cassert>

std::random_device rd;

/**
 * @brief chooseRandomOp returns a random Op
 * @return Random Op
 */
Op chooseRandomOp()
{   
    Op result = Op::None;
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<int> distribution(0,1);

    int choice = distribution(gen);

    switch(choice){
        case 0:
            result= Op::Horizontal;
            break;
        case 1:
            result = Op::Vertical;
            break;

    }
    return result;
}

/**
 * @brief switchOp rotates a given Op
 * @param op
 */
void switchOp(Op& op)
{
    if(op == Op::Horizontal) op = Op::Vertical;
    if(op == Op::Vertical) op = Op::Horizontal;
}

/**
 * @brief operator << prints an Op as + or *.
 */
std::ostream& operator<<(std::ostream& os, Op op)
{
    switch(op)
    {
        case Op::None:       return os<<"";
        case Op::Horizontal: return os<<"+";
        case Op::Vertical:   return os<<"*";
        default: return os;
    }
}

/**
 * @brief Floor::setRoot sets the root node.
 * @param root
 */
void Floor::setRoot (Node* root)
{
    this->root = root;
}

/**
 * @brief Floor::Floor constructs an initial slicing tree (cellNodes and innerNodes) for a given set of cells.
 * @param cells
 */
Floor::Floor(const std::vector<Cell>& cells) : root(nullptr)
{
    // Fill the cellNodes vector with cell nodes
    for(int i = 0; i < cells.size(); i++) {
        cellNodes.push_back(std::make_unique<Node>(cells[i]));
    }
    
    while (cellNodes.size() > 1) {
        Op op = chooseRandomOp();
        auto l = std::move(cellNodes.back());
        cellNodes.pop_back();
        auto r = std::move(cellNodes.back());
        cellNodes.pop_back();
        auto innerNode = std::make_unique<Node>(op);
        innerNode->left = l.release();
        innerNode->right = r.release();
        innerNodes.push_back(std::move(innerNode));
        cellNodes.push_back(std::make_unique<Node>(*innerNodes.back()));
    }

    // Set the root node
    if (!cellNodes.empty()) {
        setRoot(cellNodes.back().get());
    }

    pack();
}



/**
 * @brief Floor::calcSize determines width and height of a packed (subtree) floorplan
 * @param node Root node of the (subtree) floorplan
 * @return A tuple of width and height
 */
std::tuple<size_t, size_t> Floor::calcSize(Node* node)
{
    if(node == nullptr){
        return std::make_tuple(0, 0);
    }

    if(node->op == Op::None) {
        return std::make_tuple(node->cell.getXlength(), node->cell.getYlength());
    }

    if(node->op == Op::Horizontal){
        return calcHorizontalSize(node);
    }

    if(node->op == Op::Vertical){
        return calcVerticalSize(node);
    }
    //If all goes well this line will not matter
    return std::make_tuple(0, 0);
}

/**
 * @brief Floor::calcVerticalSize determines the size of a vertical cut (sub)tree.
 * @param node Root node of the (sub)tree (assumed to be a vertical cut node)
 * @return A tuple of width and hight
 */
std::tuple<size_t, size_t> Floor::calcVerticalSize(Node* node)
{
    if(node == nullptr){
        return std::make_tuple(0, 0);
    }
    auto l = calcSize(node->left);
    auto r = calcSize(node->right);
    size_t w = std::get<0>(l) + std::get<0>(r);
    size_t h = std::max(std::get<1>(l), std::get<1>(r));
    if (node->left->op == Op::Vertical && node->right->op == Op::Vertical){
        h++;
    }

    return std::make_tuple(w, h);
}

/**
 * @brief Floor::calcHorizontalSize determines the size of a horizontal cut (sub)tree
 * @param node Root node of the (sub)tree (assumed to be a horizontal cut node)
 * @return A tuple of width and height
 */
std::tuple<size_t, size_t> Floor::calcHorizontalSize(Node* node)
{
    if (node == nullptr) {
        return std::make_tuple(0, 0);
    }
    auto l = calcSize(node->left);
    auto r = calcSize(node->right);
    size_t w = std::max(std::get<0>(l), std::get<0>(r));
    size_t h = std::get<1>(l) + std::get<1>(r);
    if (node->left->op == Op::Horizontal && node->right->op == Op::Horizontal) {
        w++;
    }

    return std::make_tuple(w, h);
}

/**
 * @brief Floor::pack resizes this->plan and fills it with the cell names at the occupied positions
 * @param coordinates
 */
void Floor::pack()
{
    this->plan.clear();
    auto [maxX, maxY] = this->calcSize(this->getRoot());
    this->plan.resize(maxY, std::vector<std::string>(maxX,"0"));
    if (this->plan.size())
        if (this - plan[0].size())
            this->addCells(Coordinates(0, 0), this->getRoot());
}

/**
 * @brief Floor::shiftRight performs a right shift rotation.
 * @param node
 */
void Floor::shiftRight(Node *node)
{
    assert(node->left);
    if(node->left->op == Op::None)
        return;
    auto c = node->left;
    if(node->parent)
    {
        if(node->parent->left == node)
            node->parent->left = c;
        if(node->parent->right == node)
            node->parent->right = c;
    }
    if(getRoot() == node)
        setRoot(c);
    c->right->parent = node;
    c->parent    = node->parent;
    node->left   = c->right;
    c->right     = node;
    node->parent = c;
}

/**
 * @brief Floor::shiftLeft erforms a left shift rotation.
 * @param node
 */
void Floor::shiftLeft(Node *node)
{
    assert(node->right);
    if(node->right->op == Op::None)
        return;
    auto c = node->right;
    if(node->parent)
    {
        if(node->parent->left == node)
            node->parent->left = c;
        if(node->parent->right == node)
            node->parent->right = c;
    }
    if(getRoot() == node)
        setRoot(c);
    c->left->parent = node;
    c->parent    = node->parent;
    node->right  = c->left;
    c->left      = node;
    node->parent = c;
}

/**
 * @brief Floor::modify applies an arbitrary modification on the slicing tree.
 */
void Floor::modify() {
   
    if(root == nullptr){
        return;
    }

    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<int> nodeway(0, cellNodes.size() - 1);
    std::uniform_int_distribution<int> opDist(0, 3);

    int index = nodeway(gen);
    Node* node = cellNodes[index].get();

    switch (opDist(gen))
    {
        case 0: 
            if(node->op != Op::None){
                node->op = chooseRandomOp();
            }
            break;
        case 1: 
            if(node->cell.getXlength() > 0 && node->cell.getYlength() > 0){
                node->cell.rotate();
            }
            break;
        case 2: 
            if(node->op != Op::None){
                std::swap(node->left, node->right);
            }
            break;
        case 3: 
            if(node->op != Op::None){
                std::uniform_int_distribution<int> shift(0, 1);
                bool left = (shift(gen) == 0);
                if(left){
                    shiftLeft(node);
                }
                else
                {
                    shiftRight(node);
                }
            }
            break;
        default:
            break;
    }
}

/**
 * @brief Floor::optimize is a simple monotonic optimizer.
 * The floorplan is modified randomly t times.
 * The smallest floorplan result is returned in this->plan.
 * @param t Number of iterations
 */
void Floor::optimize(unsigned long t) {
    
    if(root == nullptr){
        return; 
    }

    std::vector<std::vector<std::string>> optimization = plan; 
    size_t bestarea = calcArea(optimization); 
    size_t currentarea = bestarea; 

    for(int i = 0; i < t; i++){
        modify(); 

        pack();
        currentarea = calcArea(plan); 

        if (currentarea < bestarea) {
            bestarea = currentarea; 
            optimization = plan;
        }
    }

    plan = optimization; 
}






/**
 * @brief Floor::addCells fills the cells of a given subtree into the this->plan member.
 * @param coordinates
 * @param node
 */
void Floor::addCells(Coordinates coordinates, Node* node)
{
    if (!node)
        return;
    switch(node->op)
    {
        case Op::None:
        {
            size_t x = coordinates.x + node->cell.getXlength();
            size_t y = coordinates.y + node->cell.getYlength();

            for (size_t jy = coordinates.y; jy < y; ++jy)
                for (size_t ix = coordinates.x; ix < x; ++ix)
                    this->plan[jy][ix] = node->cell.getName();
            break;
        }
        case Op::Horizontal:
            assert(node->left && node->right);
            this->addCells(coordinates,node->left);
            this->addCells(Coordinates(coordinates.x,coordinates.y+std::get<1>(this->calcSize(node->left))),node->right);
            break;
        case Op::Vertical:
            assert(node->left && node->right);
            this->addCells(coordinates,node->left);
            this->addCells(Coordinates(coordinates.x+std::get<0>(this->calcSize(node->left)),coordinates.y),node->right);
            break;
    }
    return;
}

/**
 * @brief Floor::printPostOrder prints the slicing tree in post order.
 * @param node
 */
void Floor::printPostOrder(Node *node)
{
    if(!node)
        return;
    printPostOrder(node->left);
    printPostOrder(node->right);
    std::cout << node->cell.getName() << node->op << " " << std::flush;
}

/**
 * @brief Floor::calcArea returns the area of a given plan.
 * @param plan
 */
size_t Floor::calcArea(const std::vector <std::vector<std::string>>& plan)
{
    if(!plan.size())
        return 0;
    return plan.size() * plan[plan.size()-1].size();
}

/**
 * @brief operator << prints an ASCII art of a Floor::plan
 */
std::ostream& operator<<(std::ostream& os, const Floor& floor)
{
    for (const auto& line: floor.plan)
    {
        for (const auto& cell: line)
            os << cell << "  ";
        os << "\n";
    }
    return os;
}

