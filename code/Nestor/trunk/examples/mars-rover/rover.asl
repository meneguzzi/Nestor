// position(X,Y)
// adjacent

//at(0,0).
//battery(10).
chargeStation(5,5).

//**************************************************************
// Hack plans to get things working
//Wait till the first perception of position to get things going
+at(X,Y) [source(percept)] : not at(Z,W) [source(self)]
 <- .print("Now that I know where I am, I can start working");
 	+at(X,Y).

+battery(X) [source(percept)] : not battery(B) [source(self)]
 <- .print("Now that I know my battery, I can start moving");
 	+battery(X).
//**************************************************************
 
// Enough battery verification goal.
+?canGo(X,Y,Xat,Yat) : battery(Batt) [source(self)]
 <- rover.act.distance(X,Y,Xat,Yat,Dist);
	(Dist < Batt).

//Convert percepts for battery into beliefs
+battery(X)[source(percept)] : battery(B) [source(self)]
 <- //.print("Battery is now ", X);
    //.print("Battery is believed ", B);
    -battery(B);
    +battery(X).
//*****************************************************
// Plans to deal with battery
//And check if we are in a danger zone
@atomic
+battery(Batt) [source(self)] : at(X,Y) 
						   	  & chargeStation(Xcharge,Ycharge)
						      & not charging
 <- rover.act.distance(X,Y,Xcharge,Ycharge,Dist);
 	!checkCharge(Dist, Batt).
 	
//If we risk not reaching the charging station
+!checkCharge(Dist, Batt) : Dist >= Batt
 <- ?chargeStation(Xcharge,Ycharge);
 	.print("Battery critical, charging station is ",Dist," units away, battery is ",Batt);
 	!recharge(Xcharge,Ycharge);
	?battery(NewBatt);
	.print("Recharged, battery is now ",NewBatt).

+!checkCharge(Dist, Batt) : Dist < Batt
 <- .print("Battery is OK, charging station is ",Dist," units away, battery is ", Batt).

//To avoid adopting the recharge plan multiple times
+!recharge(Xcharge,Ycharge) : not charging
 <- .print("Moving to charge station");
 	+charging;
	!suspendGoals;
 	!move(Xcharge, Ycharge);
	charge;
	-charging;
	!resumeGoals.

//To avoid adopting the recharge plan multiple times
+!recharge(Xcharge,Ycharge) : charging
 <- .print("Already going to charge station.").

+!suspendGoals : true
 <- //.desire(waypoint(X,Y));
 	.print("Dropping intention to ",goToWaypoint(X,Y));
 	.drop_intention(goToWaypoint(X,Y));
	.print("Intention to ",goToWaypoint(X,Y)," dropped, releasing locks");
	-moving;
	.print("Locks released");
	!storeGoal(X,Y).

+!storeGoal(X,Y) : .ground(waypoint(X,Y))
 <- .print("Storing ", waypoint(X,Y));
 	+pendingWaypoint(X,Y).

+!storeGoal(X,Y) : true
 <- .print("No need to store a waypoint").

+!resumeGoals : pendingWaypoint(X,Y)
 <-	.print("Resuming intention to ", goToWaypoint(X,Y));
 	-pendingWaypoint(X,Y).

+!resumeGoals : true
 <- .print("No intentions to resume").

-pendingWaypoint(X,Y) : true
 <- .print("Reposting intention to ", goToWaypoint(X,Y));
 	!goToWaypoint(X,Y).
	
//*****************************************************
//*****************************************************
// These are the main goals of this agent, to reach
// waypoints
+waypoint(X,Y) : true
 <- .print("New waypoint ", waypoint(X,Y));
 	!goToWaypoint(X,Y).

+!goToWaypoint(X,Y) : at(X,Y)
 <- .print("I'm already at the waypoint").

+!goToWaypoint(X,Y) : not at(X,Y) & at(Xat,Yat) & not charging & not moving
 <- +moving;
 	.print("Going to waypoint ",waypoint(X,Y));
 	!move(X,Y);
	-moving.

+!goToWaypoint(X,Y) : not at(X,Y) & at(Xat,Yat) & moving
 <- .print("Waiting to reach previous waypoint.");
	.wait("-moving");
	.print("Done, will try to move to next waypoint");
 	!goToWaypoint(X,Y).
	
+!goToWaypoint(X,Y) : not at(X,Y) & at(Xat,Yat) & charging
 <- .print("Waiting to charge.");
	.wait("+battery(20)");
	.print("Done charging, resuming, ", goToWaypoint(X,Y));
 	!goToWaypoint(X,Y).
	
//*****************************************************

//*****************************************************
// Plans to move the agent around
+!move(X,Y) : at(X, Y)
 <- .print("Arrived ", at(X,Y));
    true.

+!move(X,Y) : at(Xat,Yat) & Xat > X
 <- .print("Moving from ",at(Xat,Yat)," to ",at(X,Y));
    -at(Xat,Yat);
 	+at(Xat - 1,Yat);
 	move(Xat - 1,Yat);
	.wait(500);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat) & Xat < X
 <- .print("Moving from ",at(Xat,Yat)," to ",at(X,Y));
    -at(Xat,Yat);
 	+at(Xat + 1,Yat);
 	move(Xat + 1,Yat);
	.wait(500);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat) & Yat > Y
 <- .print("Moving from ",at(Xat,Yat)," to ",at(X,Y));
    -at(Xat,Yat);
 	+at(Xat,Yat - 1);
 	move(Xat,Yat - 1);
	.wait(500);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat) & Yat < Y
 <- .print("Moving from ",at(Xat,Yat)," to ",at(X,Y));
    -at(Xat,Yat);
 	+at(Xat,Yat + 1);
 	move(Xat,Yat + 1);
	.wait(500);
 	!move(X,Y).
//*****************************************************

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