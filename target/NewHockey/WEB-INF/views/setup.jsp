<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>

  <meta charset="utf-8" name="viewport" content="width=device-width, initial-scale=1">
  <title>Setup</title>
	
	<script type="text/javascript" src="<c:url value="/webjars/jquery/3.7.1/jquery.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/webjars/bootstrap/5.3.3/js/bootstrap.bundle.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/javascript/index.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/webjars/select2/4.1.0-rc.0/dist/js/select2.min.js"/>"></script>

	<link rel="stylesheet" href="<c:url value="/webjars/select2/4.1.0-rc.0/dist/css/select2.min.css"/>">
	<link rel="stylesheet" href="<c:url value="/webjars/bootstrap/5.3.3/css/bootstrap.min.css"/>">
	
</head>
<body onload="afterPageLoad('SETUP');">
<form:form name="setup_form" method="POST" action="match" enctype="multipart/form-data"
	modelAttribute="session_match">
<div class="content py-5" style="background-color: #EAE8FF; color: #2E008B">
  <div class="container">
	<div class="row">
	 <div class="col-md-8 offset-md-2">
       <span class="anchor"></span>
         <div class="card card-outline-secondary">
           <div class="card-header">
             <h3 class="mb-0">Setup</h3>
             <h6>${licence_expiry_message}</h6>
           </div>
          <div class="card-body">
	         <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
			  		name="cancel_match_setup_btn" id="cancel_match_setup_btn" onclick="processUserSelection(this)">
		  		<i class="fas fa-window-close"></i> Back</button>
	         </div>
			  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="select_existing_hockey_matches" class="col-sm-4 col-form-label text-left">Select Hockey Match 
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
			      <select id="select_existing_hockey_matches" name="select_existing_hockey_matches" class="browser-default custom-select custom-select-sm"
			      		onchange="processUserSelection(this)">
				        <option value="new_match">New Match</option>
						<c:forEach items = "${match_files}" var = "match">
				          <option value="${match.name}">${match.name}</option>
						</c:forEach>
			      </select>
			    </div>
			  </div>
			  <div id="matchFileName_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="matchFileName" class="col-sm-4 col-form-label text-left">Match Filename <i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="matchFileName" name="matchFileName" class="form-control form-control-sm floatlabel" onblur="processUserSelection(this);"></input>
		              <label id="matchFileName-validation" style="color:red; display: none;"></label> 
			    </div>
			  </div>
			  <div id="tournament_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="tournament" class="col-sm-4 col-form-label text-left">Tournament/Series Name <i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="tournament" name="tournament" class="form-control form-control-sm floatlabel" 
		             	onblur="processUserSelection(this);"></input>
			    </div>
			  </div>
			  <div id="matchIdent_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="matchIdent" class="col-sm-4 col-form-label text-left">Match Ident <i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="matchIdent" name="matchIdent" 
		             	class="form-control form-control-sm floatlabel" onblur="processUserSelection(this);"></input>
			    </div>
			  </div>
			  <div id="matchId_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="matchId" class="col-sm-4 col-form-label text-left">Match ID <i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="matchId" name="matchId" 
		             	class="form-control form-control-sm floatlabel" onblur="processUserSelection(this);"></input>
			    </div>
			  </div>
			  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="groundId" class="col-sm-4 col-form-label text-left">Select Ground 
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
			      <select id="groundId" name="groundId" class="browser-default custom-select custom-select-sm"
			      		onchange="processUserSelection(this)">
						<c:forEach items = "${grounds}" var = "ground">
				          <option value="${ground.groundId}">${ground.fullname}</option>
						</c:forEach>
			      </select>
			    </div>
			  </div>
				  <div style="margin-bottom:5px;">
					<div class="row">
					  <div class="col-6 col-sm-3">
					    <label for="homeSubstitutesPerTeam" class="col-form-label text-left">Home Subs</label>
					      <select id="homeSubstitutesPerTeam" name="homeSubstitutesPerTeam" class="browser-default custom-select custom-select-sm">
					      		<c:forEach begin="0" end="15" varStatus="loop">
						          <option value="${loop.index}">${loop.index}</option>
								</c:forEach>
					      </select>
					  </div>
					  <div class="col-6 col-sm-3">
					    <label for="awaySubstitutesPerTeam" class="col-form-label text-left">Away Subs</label>
					      <select id="awaySubstitutesPerTeam" name="awaySubstitutesPerTeam" class="browser-default custom-select custom-select-sm">
					      		<c:forEach begin="0" end="15" varStatus="loop">
						          <option value="${loop.index}">${loop.index}</option>
								</c:forEach>
					      </select>
					  </div>
					</div>
				  </div>
			  	<table class="table table-striped table-bordered"> 
				  <thead>
			        <tr>
			        	<th>Select HOME Team Jersey Color: 
					      <select id="homeTeamJerseyColor" name="homeTeamJerseyColor" class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${teamcolor}" var = "teamcolor">
					          <option value="${teamcolor.colorType}">${teamcolor.colorType}</option>
							</c:forEach>
					      </select>
			        	</th>
			        	<th>Select AWAY Team Jersey Color: 
					      <select id="awayTeamJerseyColor" name="awayTeamJerseyColor" class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${teamcolor}" var = "teamcolor" varStatus="status">
							<c:choose>
								<c:when test="${status.last}">
									<option value="${teamcolor.colorType}" selected="selected">${teamcolor.colorType}</option>
								</c:when>
								<c:otherwise>
						          <option value="${teamcolor.colorType}">${teamcolor.colorType}</option>
								</c:otherwise>
							</c:choose>
							</c:forEach>
					      </select>
			        	</th>
				    </tr>
				  </thead>
				</table> 
				<table class="table table-striped table-bordered"> 
				  <thead>
			        <tr>
			        	<th>Select HOME Team GoalKeeper Jersey Color: 
					      <select id="homeTeamGKJerseyColor" name="homeTeamGKJerseyColor" class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${teamcolor}" var = "teamcolor">
					          <option value="${teamcolor.colorType}">${teamcolor.colorType}</option>
							</c:forEach>
					      </select>
			        	</th>
			        	<th>Select AWAY Team GoalKeeper Jersey Color: 
					      <select id="awayTeamGKJerseyColor" name="awayTeamGKJerseyColor" class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${teamcolor}" var = "teamcolor" varStatus="status">
							<c:choose>
								<c:when test="${status.last}">
									<option value="${teamcolor.colorType}" selected="selected">${teamcolor.colorType}</option>
								</c:when>
								<c:otherwise>
						          <option value="${teamcolor.colorType}">${teamcolor.colorType}</option>
								</c:otherwise>
							</c:choose>
							</c:forEach>
					      </select>
			        	</th>
				    </tr>
				  </thead>
				</table> 
			  	<table class="table table-striped table-bordered"> 
				  <thead>
			        <tr>
			        	<th>Select HOME Team Formation: 
					      <select id="homeTeamFormationId" name="homeTeamFormationId" class="browser-default custom-select custom-select-sm">
							<option value="0"> </option>
							<c:forEach items = "${formations}" var = "form">
					          <option value="${form.formId}">${form.formDescription}</option>
							</c:forEach>
					      </select>
			        	</th>
			        	<th>Select AWAY Team Formation: 
					      <select id="awayTeamFormationId" name="awayTeamFormationId" class="browser-default custom-select custom-select-sm">
							<option value="0"> </option>
							<c:forEach items = "${formations}" var = "form">
					          <option value="${form.formId}">${form.formDescription}</option>
							</c:forEach>
					      </select>
			        	</th>
				    </tr>
				  </thead>
				</table>
	        	<table class="table table-striped table-bordered"> 
				  <thead>
			        <tr>
			        	<th>Select HOME Team: 
					      <select id="homeTeamId" name="homeTeamId" class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${teams}" var = "team">
					          <option value="${team.teamId}">${team.teamName1}</option>
							</c:forEach>
					      </select>
			        	</th>
			        	<th>Select AWAY Team: 
					      <select id="awayTeamId" name="awayTeamId" class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${teams}" var = "team" varStatus="status">
								<c:choose>
									<c:when test="${status.last}">
							          <option value="${team.teamId}" selected="selected">${team.teamName1}</option>
									</c:when>
									<c:otherwise>
							          <option value="${team.teamId}">${team.teamName1}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
					      </select>
			        	</th>
			        	<th>
						    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
						  		name="load_default_team_btn" id="load_default_team_btn" onclick="processUserSelection(this)">
					  		<i class="fas fa-download"></i> Load Teams</button>
			        	</th>
				    </tr>
				  </thead>
				</table>
			  <div id="team_selection_div" class="text-center" style="display:none;">
	         </div>
	         <div id="save_match_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;display:none;">
			    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
			  		name="save_match_btn" id="save_match_btn" onclick="processUserSelection(this)">
		  		<i class="fas fa-download"></i> Save Match</button>
			    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
			  		name="reset_match_btn" id="reset_match_btn" onclick="processUserSelection(this)">
		  		<i class="fas fa-window-close"></i> Reset Match</button>
	         </div>
          </div>
         </div>
       </div>
    </div>
  </div>
</div>
</form:form>
</body>
</html>