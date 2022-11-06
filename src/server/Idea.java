package server;
// maybe server can be real dumb
// and just pass actions 
// start up with some kind of handshake
// white sends genmove
// black must be listening in passive mode.
// black makes a move
// white gets the move.
// white makes black's move on his board.
// and switches to passive mode
// ...
// maybe have 2 sockets one for active and one for passive?
// both start up in passive mode
// white sends a genmove
// black sees genmove and moves
// black sends a genmove and waits
// white receives move and records it.
// white sees genmove and moves ...
// the above may work. one side needs to be the guy with the server socket who accepts.
// acceptor side has server socket listening
// connector side connects twice. one socket is used for passive, the other for active.
// acceptor side accepts twice twice. one socket is used for passive, the other for active.
// if my move
//		passive wait for genmove
// 		allow me to move
//		wait for move completed
// else
// 		active send genmove
// 		active wait for genmove to return
//		make opponents move
// how do we restore or connect to a game in progress?
// much later (september 2021). not sure what works.
// seems like we did have a server working with two windows - not sure.
// if not then maybe try to restore a saved game
// and then connect the requestor to it as one of the colors.
// then wait for the other player to connect
// or use a random bot initially til we get something to work.
// 9/12/21
// we do have something that works. start the server and connect two guis.
// this works, but is a real hack as anyone can do anything.
public class Idea { public static void main(String[] args) {} }
