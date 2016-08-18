<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", -1); //evita o caching no servidor proxy
%>

<html>
<head>
<title>TopWar</title>
<META NAME="description" 	CONTENT="">


<style type="text/css">
div {
	cursor: pointer;
	font-family: sans-serif;
}

A:link {text-decoration: none; 
		color: #0084B4;}
A:visited {text-decoration: none;
		color: #0084B4;} 
A:active {text-decoration: none;
		color: #0084B4;}
A:hover {text-decoration: underline overline; color: black;}

#link {
	border-style: solid;
	border-width: 1px;
	border-color: #B8CFE5;
	text-align: left;
	padding: 3px;
	padding-left: 10px;
	padding-right: 10px;
	cursor: pointer;
	font-family: sans-serif;
	color: #0084B4;
	text-decoration: none;
	margin: 3px;
	font-size: 16px;
}

#pop{
     display:none;
     position:absolute;
	 top:28%;
	 left:35%;
	 margin-left:-150px;
	 margin-top:-100px;
	 padding-left: 20px;
	 padding-right: 20px;
	 padding:10px;
	 width:550px;
	 height:450px;
	 border:1px solid #B8CFE5;
    }

img {
	border-style: none;
	border-width: 0px;
	border-color: #5C5E5D;
	padding: 10px;
	padding-left: 0px;
	padding-right: 0px;
}

#title {
	border-style: none;
	text-align: left;
	font-family: Arial, sans-serif;
	font-size: 24px;
	font-weight: bold;
	line-height: 24px;
	margin-bottom: 15px;
}

#main{
	padding: 3px;
	padding-left: 20px;
	padding-right: 20px;
	border-style: none;
	position: relative;
	float: right;	
}

#shots{
	border-style: solid;
	border-width: 1px;
	border-color: #B8CFE5;
	text-align: center;
	padding: 3px;
	padding-left: 20px;
	padding-right: 20px;
	cursor: pointer;
	font-family: sans-serif;
}

#shotsPromo{
	border-style: solid;
	border-width: 1px;
	border-color: #B8CFE5;
	text-align: center;
}

#description {
	color: #666666;
	font-size: 13px;
	font-style: italic;
}
</style>
<script type="text/javascript" src="highslide/highslide-full.js"></script>
<link rel="stylesheet" type="text/css" href="highslide/highslide.css" />

<!--
	2) Optionally override the settings defined at the top
	of the highslide.js file. The parameter hs.graphicsDir is important!
-->

<script type="text/javascript">
	
	hs.graphicsDir = 'highslide/graphics/';
	hs.align = 'center';
	hs.transitions = ['expand', 'crossfade'];
	hs.outlineType = 'rounded-white';
	hs.fadeInOut = true;
	hs.dimmingOpacity = 0.75;

	// define the restraining box
	hs.useBox = true;
	hs.width = 800;
	hs.height = 600;

	// Add the controlbar
	hs.addSlideshow({
		//slideshowGroup: 'group1',
		interval: 5000,
		repeat: false,
		useControls: true,
		fixedControls: 'fit',
		overlayOptions: {
			opacity: 1,
			position: 'bottom center',
			hideOnMouseOut: true
		}
	});


	
</script>

</head>
<body>
<center>
<table >
	<tr> 
	<td>
	<div id="main">
		<div id="title">	
			<span >TopWar <br>
				<span id="description">Guerra Estrategica</span>
			</span>
			<a id="link" href="http://sowbreira.appspot.com/" target="_BLANK">
				Sowbreira
			</a>
			<a id="link" 	href="topwar_en.jsp">
				English
			</a>
			<a id="link" style="text-align: right;" onclick="document.getElementById('pop').style.display='block';">
				Como Jogar
			</a>				
			<a id="link" href="http://www.java.com/" target="_BLANK">
				Java
			</a>
		</div>
		<div id="shots" class="highslide-gallery">
			<a href="tw1.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="tw1.jpg" width="130" height="120" /></a>
			<a href="tw2.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="tw2.jpg" width="130" height="120" /></a>
			<a href="tw3.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="tw3.jpg" width="130" height="120" /></a>
			<a href="tw4.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="tw4.jpg" width="130" height="120" /></a><br>
			<a href="tw5.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="tw5.jpg" width="130" height="120" /></a>
			<a href="tw6.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="tw6.jpg" width="130" height="120" /></a>
			<a href="tw7.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="tw7.jpg" width="130" height="120" /></a>
			<a href="tw8.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="tw8.jpg" width="130" height="120" /></a><br>
		</div>
		<div style="text-align: center;">
			<div style="text-align: center;	">
				<a id="link"  href="topwarOnline.jnlp" style="text-align: left;">
					Online
					<img src="webstart.png" border="0">
				</a>	
			&nbsp;&nbsp;&nbsp;&nbsp;
				<a id="link" href="https://b4116f288ced0da9ffafef28a149df8144e98bd7.googledrive.com/host/0B1bELj5XAV79flRUMkFyMFNFanl0Tlc4bHU5YzJQU0JYelN3VmpqYkxGUklOTjFYX1BlYzg/topwar/topwar.html" style="text-align: left;">
					Single
					<img src="webstart.png" border="0">
				</a>				
			<div>	
		</div>
		<br>
		<div id="shotsPromo" class="highslide-gallery">
			<table style="text-align: center;padding-left: 35px">
			<tr>
			<td  style="text-align: center;padding-top: 10px;">
				<a id="link" href="../../mesa11" style="text-align: center;" target="_BLANK">
					F1-mane 
				</a><br>
				<a href="./../f1mane/fm1.jpg" style="padding: 5px" onclick="return hs.expand(this)"> 
					<img  src="./../f1mane/fm1.jpg" width="130" height="120" />
				</a>
				<a href="./../f1mane/fm2.jpg" style="padding: 5px" onclick="return hs.expand(this)"> 
					<img  src="./../f1mane/fm2.jpg" width="130" height="120" />
				</a>
			</td>
			<td  style="text-align: center;padding-top: 10px;">				
				<a id="link" href="../../mesa11" style="text-align: center;" target="_BLANK">
					Mesa-11
				</a><br>
				<a href="./../mesa11/m11-1.jpg" style="padding: 5px" onclick="return hs.expand(this)"> 
					<img  src="./../mesa11/m11-1.jpg" width="130" height="120" />
				</a>
				<a href="./../mesa11/m11-2.jpg" style="padding: 5px" onclick="return hs.expand(this)"> 
					<img  src="./../mesa11/m11-2.jpg" width="130" height="120" />
				</a>
			</td></tr>
			</table>
		</div>		
	</div>
	</td>
	</tr>
</table>
</center>
		<div id="pop" style="background-color: white;">
	    	XXXXXXXXXXXXXXXXXX
	    	<a href="#" style="position:absolute; left:92%" onclick="document.getElementById('pop').style.display='none';">[X]</a>
			<p style="color: #0084B4;">
				Dentro do jogo:	
			</p>
			<UL>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			</UL>
			<p style="color: #0084B4;">
				No jogo online:	
			</p>
			<UL>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>			   
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			   <LI >XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</LI>
			</UL>								
		</div>
</body>
</html>