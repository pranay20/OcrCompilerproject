# OcrCompilerproject
This is android-PHP project in which the android app takes image of short C code snippet as input. The input image 
can be capturedfrom android camera or taken from gallery. Using OCR technology, text is extracted from input image. 
This text is sent to PHP page on server side which saves this text in a .c file. After that PHP page executes gcc compiler 
and the output is returned & displyed to user on android app. This app can also take text as input from clipboard 
and send it to server. Teseract OCR API is used to implement OCR technology. This project is configured to run on localhost 
using XAMPP server to execute PHP part of project. The IP of localhost machine must 192.168.43.88 because android app 
is using only that IP address to send and receive data from & to server.
