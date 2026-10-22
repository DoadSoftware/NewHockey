<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" name="viewport" content="width=device-width, initial-scale=1">
  <title>Hockey</title>

	<script type="text/javascript" src="<c:url value="/webjars/jquery/3.7.1/jquery.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/webjars/bootstrap/5.3.3/js/bootstrap.bundle.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/index.js"/>"></script>
	<link rel="stylesheet" href="<c:url value="/webjars/bootstrap/5.3.3/css/bootstrap.min.css"/>">

	<style>
		.side-wrap {
			position: fixed;
			top: 50%;
			transform: translateY(-50%);
			width: 230px;
			display: flex;
			flex-direction: column;
			gap: 15px;
			z-index: 1000;
		}
		.side-wrap.left  { left: 10px; }
		.side-wrap.right { right: 10px; }
	
		.side-panel {
			padding: 10px;
			border: 2px solid #2E008B;
			border-radius: 10px;
			background-color: #FEFEFE;
		}
		.side-panel .btn-grid {
			display: grid;
			grid-template-columns: 1fr 1fr;
			gap: 8px;
		}
		.side-panel .btn {
			background-color: #2E008B;
			color: #FEFEFE;
			font-size: 0.75rem;
			padding: 4px 2px;
			white-space: nowrap;
		}
	</style>

  <script type="text/javascript">
	$(document).on("keydown", function(e){
		var tag = e.target.tagName;
		if (tag === "INPUT" || tag === "TEXTAREA" || tag === "SELECT" || e.target.isContentEditable) {
			return; // let the key work normally, don't call processUserSelectionData
		}
		if (e.which >= 112 && e.which <= 123) { // Suppress default behaviour of F1 to F12
			e.preventDefault();
		}else if (e.which == 32) {
			e.preventDefault();
		}
		processUserSelectionData('LOGGER_FORM_KEYPRESS',e.which);
	});
  </script>  
</head>
<body onload="afterPageLoad('MATCH');">
	<!-- LEFT SIDE (HOME) -->
	<div class="side-wrap left d-none d-xl-flex">
		<div class="side-panel">
			<div class="btn-grid">
				<button type="button" class="btn btn-sm" name="side_btn" id="homeYellow1In" onclick="processUserSelection(this)">Yellow 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="homeYellow2In" onclick="processUserSelection(this)">Yellow 2</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="homeRed1In" onclick="processUserSelection(this)">Red 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="homeRed2In" onclick="processUserSelection(this)">Red 2</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="homeGreen1In" onclick="processUserSelection(this)">Green 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="homeGreen2In" onclick="processUserSelection(this)">Green 2</button>
			</div>
		</div>
		<div class="side-panel">
			<div class="btn-grid">
				<button type="button" class="btn btn-sm" name="side_btn" id="outhomeYellow1" onclick="processUserSelection(this)">Out Yellow 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outhomeYellow2" onclick="processUserSelection(this)">Out Yellow 2</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outhomeRed1" onclick="processUserSelection(this)">Out Red 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outhomeRed2" onclick="processUserSelection(this)">Out Red 2</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outhomeGreen1" onclick="processUserSelection(this)">Out Green 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outhomeGreen2" onclick="processUserSelection(this)">Out Green 2</button>
			</div>
		</div>
	</div>

	<!-- RIGHT SIDE (AWAY) -->
	<div class="side-wrap right d-none d-xl-flex">
		<div class="side-panel">
			<div class="btn-grid">
				<button type="button" class="btn btn-sm" name="side_btn" id="awayYellow1In" onclick="processUserSelection(this)">Yellow 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="awayYellow2In" onclick="processUserSelection(this)">Yellow 2</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="awayRed1In" onclick="processUserSelection(this)">Red 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="awayRed2In" onclick="processUserSelection(this)">Red 2</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="awayGreen1In" onclick="processUserSelection(this)">Green 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="awayGreen2In" onclick="processUserSelection(this)">Green 2</button>
			</div>
		</div>
		<div class="side-panel">
			<div class="btn-grid">
				<button type="button" class="btn btn-sm" name="side_btn" id="outawayYellow1" onclick="processUserSelection(this)">Out Yellow 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outawayYellow2" onclick="processUserSelection(this)">Out Yellow 2</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outawayRed1" onclick="processUserSelection(this)">Out Red 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outawayRed2" onclick="processUserSelection(this)">Out Red 2</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outawayGreen1" onclick="processUserSelection(this)">Out Green 1</button>
				<button type="button" class="btn btn-sm" name="side_btn" id="outawayGreen2" onclick="processUserSelection(this)">Out Green 2</button>
			</div>
		</div>
	</div>
<form:form name="hockey_form" autocomplete="off" action="match" method="POST" 
	modelAttribute="session_match">
<div class="content py-5" style="background-color: #EAE8FF; color: #2E008B">
  <div class="container">
	<div class="row">
	 <div class="col-md-8 offset-md-2">
       <span class="anchor"></span>
         <div class="card card-outline-secondary">
           <div class="card-header">
			  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
	          </div>
           </div>
          <div class="card-body">
          <div id="select_graphic_options_div" style="display:none;">
			  </div>
			  <div class="panel-group" id="match_configuration">
			    <div class="panel panel-default">
			      <div class="panel-heading">
			        <h5 class="panel-title">
			          <a data-bs-toggle="collapse" href="#load_setup_match">Configuration</a>
			        </h5>
			      </div>
			      <div id="load_setup_match" class="panel-collapse collapse">
					<div class="panel-body">
					    <div class="col-sm-4 col-md-4">
<!-- 						    <button style="background-color:#2E008B;color:#FEFEFE;display:none;" class="btn btn-sm" type="button"
						  		name="open_clock_btn" id="open_clock_btn" 
						  		onclick="window.open('clock','_blank')">Open Clock</button> -->
						    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
						  		name="setup_match_btn" id="setup_match_btn" onclick="processUserSelection(this)">
						  		<i class="fas fa-tools"></i> Setup Match</button>
						 </div>
					    <div class="col-sm-8 col-md-8">
						    <label for="select_hockey_matches" class="col-sm-5 col-form-label text-left">Select Hockey Match</label>
						      <select id="select_hockey_matches" name="select_hockey_matches" 
						      		class="browser-default custom-select custom-select-sm">
									<c:forEach items = "${match_files}" var = "match">
							          <option value="${match.name}">${match.name}</option>
									</c:forEach>
						      </select>
						    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
						  		name="load_match_btn" id="load_match_btn" onclick="processUserSelection(this)">
						  		<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" style="display:none"></span>
						  		<i class="fas fa-download"></i> Load Match</button>
					    </div>
				    </div>
			      </div>
			    </div>
			  </div> 
		    <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			  <div id="select_event_div" style="display:none;"></div>
			  <div id="hockey_div" style="display:none;"></div>
           </div>
          </div>
         </div>
       </div>
    </div>
  </div>
 </div>
 <input type="hidden" id="selected_player_id" name="selected_player_id"></input>
 <%-- <input type="hidden" id="which_keypress" name="which_keypress" value="${session_match.which_key_press}"/>--%>
 <input type="hidden" name="selectedBroadcaster" id="selectedBroadcaster" value="${session_selected_broadcaster}"/>
 <input type="hidden" id="matchFileTimeStamp" name="matchFileTimeStamp" value="${session_match.matchFileTimeStamp}"></input>
</form:form>
</body>
</html>