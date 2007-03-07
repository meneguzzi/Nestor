// position(X,Y)
// adjacent

at(0,0).
chargeStation(5,5).

/*+at(X,Y) : true
 <- .print("Test ", at(X,Y)).*/

+waypoint(X,Y) : at(X, Y)
 <- ?at(Xat,Yat);
 	.print("Location is ", at(Xat,Yat));
    true.

+waypoint(X,Y) : not at(X,Y) & at(Xat,Yat)
 <- .print("Location is ", at(Xat,Yat));
 	!move(X,Y).

/*
+!move(X,Y) : at(X, Y)
 <- .print("Arrived ", at(X,Y));
    true.


+!move(X,Y) : at(Xat,Yat) & Xat > X
 <- .print("Moving from ",at(Xat,Yat)," to ",at(X,Y));
    -at(Xat,Yat);
 	+at(Xat - 1,Yat);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat) & Xat < X
 <- .print("Moving from ",at(Xat,Yat)," to ",at(X,Y));
    -at(Xat,Yat);
 	+at(Xat + 1,Yat);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat) & Yat > Y
 <- .print("Moving from ",at(Xat,Yat)," to ",at(X,Y));
    -at(Xat,Yat);
 	+at(Xat,Yat - 1);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat) & Yat < Y
 <- .print("Moving from ",at(Xat,Yat)," to ",at(X,Y));
    -at(Xat,Yat);
 	+at(Xat,Yat + 1);
 	!move(X,Y).
*/

/*
//Test for move operator
+!move(X,Y) : true
 <- .print("Invoking move operator");
    move(X,Y).
*/

/*
//Tests for ability to move to a position
+!move(X,Y) : at(Xat,Yat)
 <- ?canGo(X,Y,Xat,Yat);
    .print("I can go to the waypoint").*/


/*+!move(X,Y) : at(Xat,Yat)
 <- .print("I cannot go to the waypoint").*/

+?canGo(X,Y,Xat,Yat) : battery(Batt)
 <- ?distance(X,Y,Xat,Yat,Dist);
    (Dist < Batt).


+?distance(X,Y,Xat,Yat,Dist) : true
 <- rover.act.distance(X,Y,Xat,Yat,Dist);
    .print("Distance is ", Dist).