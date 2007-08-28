chargeStation(10,10).
visitedWaypoints([]).
distance(0).
previousDistance(0).
wastedDistance(0).
chargeDistance(0).
numberOfCharges(0).
droppedGoals(0).

//**************************************************************
// Hack plans to get things working
//Wait till the first perception of position to get things going
@pat[atomic]
+at(X,Y) [source(percept)] : not at(Z,W) [source(self)]
 <- .print("Now that I know where I am, I can start working - Motivated Agent");
 	+at(X,Y).

@pbattery[atomic]
+battery(X) [source(percept)] : not battery(B) [source(self)]
 <- .print("Now that I know my battery, I can start moving - Motivated Agent");
 	+battery(X).

//This plan has got to be before the battery check to avoid the agent
//missing a waypoint
+!move(X,Y) : at(X, Y)
 <- .print("Arrived ", at(X,Y));
    true.

//**************************************************************
//Goals to check gathered information

+!endSimulation : not waypoint(_,_) [source(self)]
 <- .print("Finished going through waypoints");
 	?visitedWaypoints(Waypoints);
 	.length(Waypoints, L);
	.print("Visited ",L," waypoints");
	?distance(Distance);
	.print("Total distance: ", Distance);
	?wastedDistance(Wasted);
	.print("Wasted Distance: ", Wasted);
	?numberOfCharges(Charges);
	.print("Recharged ",Charges," times");
	?chargeDistance(ChargeDistance);
	.print("Moved ",ChargeDistance," to recharge");
	?droppedGoals(Dropped);
	.print("Dropped ", Dropped, " goals");
	rover.act.recordStats(L, Distance, Wasted, ChargeDistance, Dropped, "stats");
	.print("**********************************");
	.stopMAS. 

+endSimulation [source(percept)]: true
 <- +endSimulation.


//**************************************************************
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
	?distance(Distance);
 	!move(Xcharge, Ycharge);	
	charge;
	?distance(Distance2);
	?chargeDistance(ChargeDistance);
	-+chargeDistance(ChargeDistance + (Distance2 - Distance));
	-charging;
	.print("Just finished charging, checking for pending waypoints.").

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
	org.kcl.nestor.mot.act.drop_motivation("navigate");
	.print("Intention to ",goToWaypoint(X,Y)," dropped, releasing locks");
	.print("Locks released");
	?distance(Distance);
	?previousDistance(PreviousDistance);
	WastedDistance = (Distance - PreviousDistance);
	.print("Wasted: ",WastedDistance);
	?wastedDistance(Wasted);
	-+wastedDistance(Wasted + (Distance - PreviousDistance));
	?droppedGoals(Dropped);
	-+droppedGoals(Dropped+1);
	-moving.

+!suspendGoals : not moving
 <- .print("No intentions to be suspended.").
 
//*****************************************************
//*****************************************************
// These are the main goals of this agent, to reach
// waypoints
// When a waypoint is received, store it and go for it

@pwaypoint2[atomic]
+waypoint(X,Y) [source(percept)] : true
 <- .print("New waypoint ", waypoint(X,Y), " storing it.");
 	+waypoint(X,Y).

+!goToWaypoint(W) : charging | moving
 <- .print("Tried to go to ",W," but I'm busy, something is wrong here");
 	org.kcl.nestor.mot.act.drop_motivation("navigate");
 	.print("Dropping motivation for a later time").

//@pgoToWaypoint[atomic]
+!goToWaypoint(waypoint(X,Y)) : not charging & not moving
 <- +moving;
 	//.print("Going to waypoint ",waypoint(X,Y));
 	?battery(Batt);
 	.print("Going to waypoint ",waypoint(X,Y)," - battery is ",battery(Batt));
 	
 	?distance(D);
	-+previousDistance(D);
 	
 	!move(X,Y);
	.print("Finished move to waypoint ",waypoint(X,Y), " releasing locks");
	-waypoint(X,Y);
	-moving.
	//;
	//!queryWaypoints.

-waypoint(X,Y) : true
 <- .print("Removing ",waypoint(X,Y)," from pending waypoints");
 	?visitedWaypoints(W);
	-+visitedWaypoints([waypoint(X,Y) | W]);
	?visitedWaypoints(Nw);
	?distance(D);
	.length(Nw,Nvw);
	.print(Nvw, " waypoints visited so far, ",Nw,", distance covered so far, ",D).
	
//*****************************************************

//*****************************************************
// Plans to move the agent around

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
 <- //.print("Moving from ",at(A,B)," to ",at(X,Y)); 
 	.wait(30);
	-at(A,B);
	+at(X,Y);
 	move(X,Y);
 	?distance(D);
	-+distance(D+1).