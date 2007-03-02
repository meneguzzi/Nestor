
+!goalA : true
	<- +beliefA;
	   +beliefB.

+!goalA : true
	<- +beliefA;
	   +beliefB;
	   -eventA.
	   
+!goalB : true
    <- +beliefB;
       !goalB2;
       -eventB.

+!goalB2 : true
	<- +eventB2.