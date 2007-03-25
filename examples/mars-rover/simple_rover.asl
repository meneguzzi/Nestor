// position(X,Y)
// adjacent

//at(0,0).
//battery(10).
chargeStation(10,10).

//**************************************************************
// Hack plans to get things working
//Wait till the first perception of position to get things going
@pat[atomic]
+at(X,Y) [source(percept)] : not at(Z,W) [source(self)]
 <- .print("Now that I know where I am, I can start working");
 	+at(X,Y).

@pbattery[atomic]
+battery(X) [source(percept)] : not battery(B) [source(self)]
 <- .print("Now that I know my battery, I can start moving");
 	+battery(X).

//This plan has got to be before the battery check to avoid the agent
//missing a waypoint
+!move(X,Y) : at(X, Y)
 <- .print("Arrived ", at(X,Y));
    true.
//**************************************************************
// Enough battery verification goal.
+?canGo(X,Y,Xat,Yat) : battery(Batt) [source(self)]
 <- rover.act.distance(X,Y,Xat,Yat,Dist);
	(Dist < Batt).

//Convert percepts for battery into beliefs
@pbattery1[atomic]
+battery(X)[source(percept)] : battery(B) [source(self)]
 <- //.print("Battery is now ", X);
    //.print("Battery is believed ", B);
    -battery(B);
    +battery(X).
//*****************************************************
// Plans to deal with battery
//And check if we are in a danger zone
/*
@pbattery2[atomic] // To ensure battery is handled without interruption
+battery(Batt) [source(self)] : at(X,Y) 
						   	  & chargeStation(Xcharge,Ycharge)
						      & not charging
 <- rover.act.distance(X,Y,Xcharge,Ycharge,Dist);
 	!checkCharge(Dist, Batt).
*/
	
//If we risk not reaching the charging station
@pcheckcharge1[atomic]
+!checkCharge(Dist, Batt) : Dist >= Batt
 <- ?chargeStation(Xcharge,Ycharge);
 	.print("Battery critical, charging station is ",Dist," units away, battery is ",Batt);
 	!recharge(Xcharge,Ycharge).

@pcheckcharge2[atomic]
+!checkCharge(Dist, Batt) : Dist < Batt
 <- true.//.print("Battery is OK, charging station is ",Dist," units away, battery is ", Batt).

@precharge[atomic]
//To avoid adopting the recharge plan multiple times
+!recharge(Xcharge,Ycharge) : not charging
 <- .print("Moving to charge station");
 	+charging;
	!suspendGoals;
 	!move(Xcharge, Ycharge);	
	charge;
	-charging;
	.print("Just finished charging, checking for pending waypoints.");
 	!queryWaypoints.

//To avoid adopting the recharge plan multiple times
+!recharge(Xcharge,Ycharge) : charging
 <- .print("Already going to charge station.").

//I assume that we already have waypoints if the agent was moving
@psuspendGoals[atomic]
+!suspendGoals : moving
 <- .print("Dropping intention to ",goToWaypoint(X,Y));
 	.drop_intention(goToWaypoint(_,_));
	.drop_desire(goToWaypoint(_,_));
	.drop_intention(move(_,_));
	.drop_desire(move(_,_));
	.drop_intention(doMove(_,_));
	.drop_desire(doMove(_,_));
	.print("Intention to ",goToWaypoint(X,Y)," dropped, releasing locks");
	.print("Locks released");
	-moving.

+!suspendGoals : not moving
 <- .print("No intentions to be suspended.").
 
//*****************************************************
//*****************************************************
// These are the main goals of this agent, to reach
// waypoints
//When a waypoint is received, store it and go for it

/*
@pwaypoint1[atomic]
+waypoint(X,Y) [source(percept)] : not moving & not charging
 <- .print("New waypoint ", waypoint(X,Y));
 	+waypoint(X,Y);
 	!goToWaypoint(waypoint(X,Y)).
*/

@pwaypoint2[atomic]
+waypoint(X,Y) [source(percept)] : true
 <- .print("New waypoint ", waypoint(X,Y), " storing it.");
 	+waypoint(X,Y).

/*
+!queryWaypoints : waypoint(X,Y) [source(self)] & not moving
 <- .print("Found a pending waypoint, ", waypoint(X,Y), " trying to deal with it.");
 	!!goToWaypoint(waypoint(X,Y));
	.print("Done querying waypoints").

+!queryWaypoints : moving
 <- .print("I'm already moving, it's pointless to query for more waypoints.").

+!queryWaypoints : not waypoint(X,Y) [source(self)] & endSimulation [source(self)]
 <- .print("It seems I've visited the last waypoint, ending simulation.");
	!endSimulation.
	
+!queryWaypoints : not waypoint(X,Y) [source(self)]
 <- .print("No pending waypoints.").
 */

+!goToWaypoint(W) : charging | moving
 <- .print("Tried to go to ",W," but I'm busy, something is wrong here").

//@pgoToWaypoint[atomic]
+!goToWaypoint(waypoint(X,Y)) : not charging & not moving
 <- +moving;
 	.print("Going to waypoint ",waypoint(X,Y));
 	!move(X,Y);
	.print("Finished move to waypoint ",waypoint(X,Y), " releasing locks");
	-waypoint(X,Y);
	-moving.
	//;
	//!queryWaypoints.
	
//*****************************************************

//*****************************************************
// Plans to move the agent around
/* Moved this plan up to avoid having the agent miss a waypoint it already visited
+!move(X,Y) : at(X, Y)
 <- .print("Arrived ", at(X,Y));
    true.
*/

+!move(X,Y) : at(Xat,Yat)[source(self)] & Xat > X
 <- 
 	!doMove(Xat - 1,Yat);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat)[source(self)] & Xat < X
 <- 
 	!doMove(Xat + 1,Yat);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat)[source(self)] & Yat > Y
 <- 
 	!doMove(Xat,Yat - 1);
 	!move(X,Y).

+!move(X,Y) : at(Xat,Yat)[source(self)] & Yat < Y
 <- 
 	!doMove(Xat,Yat + 1);
 	!move(X,Y).

@pDoMove[atomic]
+!doMove(X,Y) : at(A,B) [source(self)]
 <- .print("Moving from ",at(A,B)," to ",at(X,Y)); 
 	.wait(50);
	-at(A,B);
	+at(X,Y);
 	move(X,Y).