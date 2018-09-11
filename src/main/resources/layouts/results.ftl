<html>
<head>
    <style>
        div.gallery {
            margin: 5px;
            border: 1px solid #ccc;
            float: left;
            width: 450px;
            height: 600px;
            overflow-y: scroll;
        }

        div.gallery:hover {
            border: 1px solid #777;
        }

        div.gallery img {
            width: 100%;
            height: width;
            object-fit: contain;
        }

        div.desc {
            padding: 15px;
            text-align: center;
        }
    </style>
    <title>results</title>
</head>

<body style="background-color: lightskyblue">

<br>
<h1 style="font-size: 300%; color: papayawhip; text-align: center">Results</h1>

<!--h2 style="text-align: center">some shelters or rescue nearby</h2> -->

<!--
<div>
    <iframe width="450" height="450" frameborder="0" style="border:0; float:middle"
     src="https://www.google.com/maps/embed/v1/search?key= { }&q=cat%20adoption%20in%20  {map_zipcode}" allowfullscreen></iframe>
</div>

<br>
-->

<#list cats as cat>
    <div class="gallery">
        <img src=${cat.img} alt="https://i.pinimg.com/originals/58/9e/17/589e1715812b7b8b903abf8463bcab81.png" width="300" height="200">
         <h3 style="text-align: center">${cat.name}</h3>
         <div class="desc">${cat.description}</div>
    </div>
</#list>


</body>
</html>