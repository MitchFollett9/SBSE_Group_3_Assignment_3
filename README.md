# SBSE_Group_3_Assignment_3

Instructions on running the search program for part 4:

1. run the build "gradle build"
2. if on windows, you can run the part-4.ps1 file with ./part-4.ps1
3. this will run the given file a set amount of times and output it into a few files with the names outputX.txt
4. it will also output, in order information about each run, including information on the best patch and the equivilent patch in the form:

Best patch found: | DEL 8 |
Found at step: 829
Best execution time: 1182800.0 (ns) 
Equivilent patch index 9
Equivilent patch patch | DEL 8 |

5. To change what program to run, edit the line in part-4.ps1: $javaArgs = "examples/locoGP/SortHeap.java"


Instructions on running the search program for part 5:

1. run the build "gradle build"
2. if on windows, you can run the part-5.ps1 file with ./part-5.ps1
3. this will run the part 5 file (ToThePower) a set amount of times and output it into a few files with the names outputX.txt
4. it will also output, in order information about each run, including information on the best patch and the equivilent patch in the same form as part 4