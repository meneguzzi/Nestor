// position(X,Y)
// adjacent


chargeStation(5,5).

+waypoint(X,Y) : at(X,Y)
 <- true.

+waypoint(X,Y) : at(Xat, Yat) & (Xat \== X)
 <- !move(X,Y).

+waypoint(X,Y) : at(Xat, Yat) & (Yat \== Y)
 <- !move(X,Y).
 
+!move(X,Y) : at(X,Y)
 <- true.
 
+!move(X,Y) : at(Xat,Yat)
 <- ?canGo(X,Y,Xat,Yat);
    .print("I can go to the waypoint").
    

+!move(X,Y) : at(Xat,Yat)
 <- .print("I cannot go to the waypoint").

+?canGo(X,Y,Xat,Yat) : battery(Batt)
 <- ?distance(X,Y,Xat,Yat,Dist);
    (Dist < Batt).
    

+?distance(X,Y,Xat,Yat,Dist) : true
 <- act.distance(X,Y,Xat,Yat,Dist);
    .print("Distance is ", Dist).