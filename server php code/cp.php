<?php
//define("ACTION_RUN","run");
$fname="hello2.c";
$f=fopen($fname,"w");

if($f==false)
{
		echo("error");
		exit();
		
}

 
/*$action = $_POST["action"];
if(isset($action))
{
	if($action==ACTION_RUN){
	$GLOBALS['text']=$_POST["code"];
	echo $GLOBALS['text'];
	}
}
*/
$text=$_POST['code'];
$f2=fwrite($f,$text);




$process = proc_open('gcc hello2.c -o hello2.exe',
    array(
        1 => array("pipe", "w"),  //stdout
        2 => array("pipe", "w")   // stderr
    ), $pipes);

$e=null;
$e=stream_get_contents($pipes[2]);
	header('Content-Type: application/json');
if($e==null)
{
	$y=exec('hello2.exe');
	$z="";
$textjson=<<<END
{
	"output":"$y",
	"error":"$z"
}
END;

	echo $textjson;

session_start();
$msg=$textjson;

$_SESSION['firstmsg']=$msg;

}
else
{

	$y="";
$z=$e;
$x=str_replace('"','\"',$z);
$textjson=<<<END
{
	"output":"$y",
	"error":"$x"
}
END;
	echo $textjson;

session_start();
$msg=$textjson;

$_SESSION['firstmsg']=$msg;

}
?>