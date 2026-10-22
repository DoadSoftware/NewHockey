<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" name="viewport" content="width=device-width, initial-scale=1">
  <title>Initialise Screen</title>
  
	<script type="text/javascript" src="<c:url value="/webjars/jquery/3.7.1/jquery.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/webjars/bootstrap/5.3.3/js/bootstrap.bundle.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/index.js"/>"></script>
	<link rel="stylesheet" href="<c:url value="/webjars/bootstrap/5.3.3/css/bootstrap.min.css"/>">

</head>
<body>
<form:form name="initialise_form" autocomplete="off" action="match" method="POST">
<div class="content py-5" style="background-color: #EAE8FF; color: #2E008B">
  <div class="container">
	<div class="row">
	 <div class="col-md-8 offset-md-2">
       <span class="anchor"></span>
         <div class="card card-outline-secondary">
           <div class="card-header">
             <h3 class="mb-0">Initialise</h3>
           </div>
          <div class="card-body">
          <div id="initialise_div" style="display:none;">
		   </div>
			  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="selectedBroadcaster" class="col-sm-4 col-form-label text-left">Select Broadcaster </label>
			    <div class="col-sm-6 col-md-6">
			      <select id="selectedBroadcaster" name="selectedBroadcaster" class="browser-default custom-select custom-select-sm" 
			      		onchange="processUserSelection(this)">
			      	  <option value="HOCKEY_KHELO_INDIA">CHHATTISGARH</option>
			      	  <option value="PUNJAB">PUNJAB</option>
			      </select>
			    </div>
			  </div>
			  <div id="vizPortNumber_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="vizPortNumber" class="col-sm-4 col-form-label text-left">Viz Port Number 
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="vizPortNumber" name="vizPortNumber" value="${session_configurations.portNumber}"
		             	class="form-control form-control-sm floatlabel"></input>
			    </div>
			  </div>
			  <div id="vizIPAddress_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="vizIPAddress" class="col-sm-4 col-form-label text-left">Viz IP Address 
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6"> 
		             <input type="text" id="vizIPAddress" name="vizIPAddress" value="${session_configurations.ipAddress}"
		             		class="form-control form-control-sm floatlabel" value="localhost"></input>
			    </div>
			  </div>
			 <!--  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="vizScene" class="col-sm-4 col-form-label text-left">Viz Scene
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>	
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="vizScene" name="vizScene" value="${session_configurations.vizscene}"
		             		class="form-control form-control-sm floatlabel"></input>
			    </div>
			  </div>-->
			  &nbsp;
		    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
		  		name="load_scene_btn" id="load_scene_btn" onclick="processUserSelection(this)">
		  		<i class="fas fa-film"></i> Load Scene</button>
	       </div>
	    </div>
       </div>
    </div>
  </div>
</div>
</form:form>
</body>
</html>