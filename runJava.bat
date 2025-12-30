::java --enable-preview -cp bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar equipment.Coordinates
#java -DnotEclipse=true --enable-preview -cp bin;bin\default;bin\test;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar;lib\java-getopt-1.0.13.jar %*
java -DnotEclipse=true --enable-preview -cp bin;bin\main;bin\test;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar;lib\java-getopt-1.0.13.jar %*
