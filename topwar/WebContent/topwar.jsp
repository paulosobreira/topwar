<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", -1); //evita o caching no servidor proxy
%>

<html>
<head>
<title>TopWar</title>
<META NAME="description"
	CONTENT="">

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
	padding-left: 20px;
	padding-right: 20px;
	cursor: pointer;
	font-family: sans-serif;
	color: #0084B4;
	text-decoration: none;
}

#pop{
     display:none;
     position:absolute;
	 top:30%;
	 left:30%;
	 margin-left:-150px;
	 margin-top:-100px;
	 padding-left: 20px;
	 padding-right: 20px;
	 padding:10px;
	 width:500px;
	 height:400px;
	 border:1px solid #B8CFE5;
    }

img {
	border-style: none;
	border-width: 0px;
	border-color: #5C5E5D;
	padding: 5px;
}

#title {
	border-style: none;
	text-align: left;
	font-family: Arial, sans-serif;
	font-size: 24px;
	font-weight: bold;
	line-height: 24px;
	height: 100px;
}

#adds {
	font-family: Arial, sans-serif;
	font-size: 24px;
	font-weight: bold;
	position: relative;
	float :left;
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
	padding: 0px;
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
	<div id="adds">
		<script type="text/javascript"><!--
		google_ad_client = "pub-1471236111248665";
		/* 120x600, criado 14/06/10 */
		google_ad_slot = "5219714006";
		google_ad_width = 120;
		google_ad_height = 600;
		//-->
		</script>
		<script type="text/javascript"
		src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
		</script>
	</div>
	</td>
	<td>
	<div id="main">
		<div id="title">	
			<span >TopWar
				<h1 id="description">Guerra Estrategica</h1>
			</span>
			<a id="link" style="position: absolute; left: 150px; top: 10px;font-size: 16px;"
				href="http://twitter.com/mesa11" style="text-align: right;"
				target="_BLANK">Twitter</a>
			<a id="link" style="position: absolute; left: 250px; top: 10px;font-size: 16px;"
				href="http://sowbreira.appspot.com/" style="text-align: right;"
				target="_BLANK">Site Autor</a>
			<a id="link" style="position: absolute; left: 370px; top: 10px;font-size: 16px;"
				href="http://www.java.com/" style="text-align: right;"
				target="_BLANK">Instale o Java</a>		
			<a id="link" style="position: absolute; left: 150px; top: 50px;font-size: 16px;"
				style="text-align: right;" onclick="document.getElementById('pop').style.display='block';">
				Como Jogar</a>				
			<a id="link" style="position: absolute; left: 300px; top: 50px;font-size: 16px;"
				href="mailto:sowbreira@gmail.com" style="text-align: right;"
				target="_BLANK">XXXXXXXXXXXXXXXXXXX</a>
			<br>					
		</div>
		<div id="pop" style="background-color: #F0F0F0;">
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
		<div id="shots" class="highslide-gallery">
			<a href="m11-1.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="m11-1.jpg" width="130" height="120" /></a>
			<a href="m11-2.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="m11-2.jpg" width="130" height="120" /></a>
			<a href="m11-3.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="m11-3.jpg" width="130" height="120" /></a>
			<a href="m11-4.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="m11-4.jpg" width="130" height="120" /></a><br>
			<a href="m11-5.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="m11-5.jpg" width="130" height="120" /></a>
			<a href="m11-6.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="m11-6.jpg" width="130" height="120" /></a>
			<a href="m11-8.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="m11-8.jpg" width="130" height="120" /></a>
			<a href="m11-10.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img src="m11-10.jpg" width="130" height="120" /></a><br>
		</div>
		<div style="text-align: center;">
			<a id="link"  href="TopWarOnline.jnlp" style="text-align: left;">
				Jogar Online  
				<img src="webstart.png" border="0">
			</a>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<a id="link" href="topwar.jnlp" style="text-align: left;">
				Jogar Offline
				<img src="webstart.png" border="0">
			</a> 

		</div><br>
		<div id="shotsPromo" class="highslide-gallery">
			<table style="text-align: center;padding: 15px">
			<tr>
			<td colspan="2">Veja Tambem</td>
			</tr>
			<tr><td>
				<a id="link" 
					href="../../f1mane" style="text-align: center;"
					target="_BLANK">  F1-mane </a><br>
				<a href="./../f1mane/fm1.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img  src="./../f1mane/fm1.jpg" width="130" height="120" /></a>
				<a href="./../f1mane/fm2.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img  src="./../f1mane/fm2.jpg" width="130" height="120" /></a>
			</td>
			<td>				
				<a id="link" 
					href="../../mesa11" style="text-align: center;"
					target="_BLANK">  Mesa-11 </a><br>
				<a href="../../mesa11/m11-1.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img  src="../../mesa11/m11-1.jpg" width="130" height="120" /></a>
				<a href="../../mesa11/m11-2.jpg" style="padding: 5px" onclick="return hs.expand(this)"> <img  src="../../mesa11/m11-2.jpg" width="130" height="120" /></a>
			</td></tr>
			</table>
		</div>		
	</div>
	</td>
	</tr>
</table>
</center>
</body>
</html>