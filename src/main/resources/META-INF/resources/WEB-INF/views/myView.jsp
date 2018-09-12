<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<html>
<head>	
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
  
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css" integrity="sha384-Smlep5jCw/wG7hdkwQ/Z5nLIefveQRIY9nfy6xoR1uRYBtpZgI6339F5dgvm/e9B" crossorigin="anonymous">
  
  <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
  
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
    
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/js/bootstrap.min.js" integrity="sha384-o+RDsa0aLu++PJvFqy8fFScvbHFLtbvScb8AjopnFD+iEQ7wo/CG0xlczd+2O/em" crossorigin="anonymous"></script>
	

	<title>Magnus Capital Equity Trading Platform</title>
	
	<link href="css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
	<h1>Magnus Capital Equity Trading Platform</h1>
	<hr>
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
	    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
	        <span class="navbar-toggler-icon"></span>
	    </button>
	    <div class="collapse navbar-collapse" id="navbarNav">
	    	<ul class="navbar-nav">
	        	<li>
	            	<a href="#"><span class="glyphicon glyphicon-home"></span></a>
	            </li>
	             <li class="nav-item">
	                <a class="nav-link" href="/transactions">Transaction history</a>
	             </li>	                                                
	        </ul>
	    </div>
	</nav>   
	<br>
	<br>                                                                                                   
	<div class="container">           
	      <table class="table">
	          <thead class="table-info">
	                <tr>
	                	<th scope="col">Ticker</th>
	                    <th scope="col">Open</th>
	                    <th scope="col">High</th>
	                    <th scope="col">Low</th>
	                    <th scope="col">Close</th>
	                    <th scope="col">Volume</th>
	                    </tr>
	          </thead>
	          <tbody>
	          	<tr>
	            	<td>AAPL</td>
	                <td>1</td>
	                <td>1</td>
	                <td>1</td>
	                <td>1</td>
	                <td>7000</td>
	            </tr> 
	          </tbody>
	      </table>
	      </div>                                                                                                                                          
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="js/bootstrap.min.js"></script>
</body>
</html>