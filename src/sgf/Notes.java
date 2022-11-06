package sgf;
/*
 * gnogo adds first variaton as a child (he uses child for next).
 * so his unparse is screwey. that's why it won't work for me.
 *
 * so my tree is correct. but i have trouble printing it out!
 *
 * my tree was wrong! sgf is not a binary tree.
 * when converting a tree to a binary tree, the first variation must be a child.
 * (see knuth volume 1 page 333).
 * otherwise, you end up with the problems that i had.
 *
 * the print is funny, but it does work.
 * 
 * check out family order (knuth volume 1 page 350).
 * no luck. does not appear to be good for anything here.
 *
 * now i need to add the logic that find the moves and grafts!
 */
class Notes {}
