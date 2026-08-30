var match_data,home_team,away_team,home_team_name,away_team_name;
function secondsTimeSpanToHMS(s) {
  var h = Math.floor(s / 3600); //Get whole hours
  s -= h * 3600;
  var m = Math.floor(s / 60); //Get remaining minutes
  s -= m * 60;
  return h + ":" + (m < 10 ? '0' + m : m) + ":" + (s < 10 ? '0' + s : s); //zero padding on minutes and seconds
} 
function displayMatchTime() {
	processHockeyProcedures('READ_CLOCK',null);
}
function getPlayerMatchStats(playerId){
	var value='';
	if(match_data.events != null && match_data.events.length > 0)
	{
		for(var k = 0; k < match_data.events.length; k++){
			if(match_data.events[k].eventPlayerId == playerId){
				if(match_data.events[k].eventType == 'yellow'){
					value = value + 'Y';
				}else if(match_data.events[k].eventType == 'red'){
					value = value + 'R';
				}
			}else if(match_data.events[k].eventPlayerId == 0){
				if(match_data.events[k].offPlayerId == playerId){
					value = '(OFF) ' + value;
				}else if(match_data.events[k].onPlayerId == playerId){
					value = '(ON) ' + value;
				}
			}
			else{
				value = value + '';
			}
		}
	}else{
		value = value + '';
	}
	return value ;
}
function processWaitingButtonSpinner(whatToProcess) 
{
	switch (whatToProcess) {
	case 'START_WAIT_TIMER': 
		$('.spinner-border').show();
		$(':button').prop('disabled', true);
		break;	
	case 'END_WAIT_TIMER': 
		$('.spinner-border').hide();
		$(':button').prop('disabled', false);
		break;
	}
}
function afterPageLoad(whichPageHasLoaded)
{
	switch (whichPageHasLoaded) {
	case 'SETUP':
		$('#homeTeamId').select2();
		$('#awayTeamId').select2();
		$('#homeTeamFormationId').select2();
		$('#awayTeamFormationId').select2();
		$('#homeTeamJerseyColor').select2();
		$('#awayTeamJerseyColor').select2();
		$('#homeTeamGKJerseyColor').select2();
		$('#awayTeamGKJerseyColor').select2();
		break;
	case 'MATCH':
		addItemsToList('LOAD_EVENTS',null);
		break;
	}
}
function initialiseForm(whatToProcess, dataToProcess)
{
	switch (whatToProcess) {
	case 'TIME':
	
		if(match_data) {
			if(document.getElementById('match_time_hdr')) {
				document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
					secondsTimeSpanToHMS(match_data.clock.matchTotalSeconds);
			}
		}
		break;
	
/*	case 'MATCH':
	
		if(match_data) {
			document.getElementById('select_match_halves').value = match_data.clock.matchHalves;
		} else {
			document.getElementById('select_match_halves').selectedIndex = 0;
		}
		break;*/
		
	case 'SETUP':
		
		if(dataToProcess) {
			document.getElementById('matchFileName').value = dataToProcess.matchFileName;
			document.getElementById('tournament').value = dataToProcess.tournament;
			document.getElementById('matchIdent').value = dataToProcess.matchIdent;
			document.getElementById('matchId').value = dataToProcess.matchId;
			document.getElementById('groundId').value = dataToProcess.groundId;
			document.getElementById('homeSubstitutesPerTeam').value = dataToProcess.homeSubstitutesPerTeam;
			document.getElementById('awaySubstitutesPerTeam').value = dataToProcess.awaySubstitutesPerTeam;
			document.getElementById('homeTeamId').value = dataToProcess.homeTeamId;
			document.getElementById('awayTeamId').value = dataToProcess.awayTeamId;
			document.getElementById('homeTeamFormationId').value = dataToProcess.homeTeamFormationId;
			document.getElementById('awayTeamFormationId').value = dataToProcess.awayTeamFormationId;
			document.getElementById('homeTeamJerseyColor').value = dataToProcess.homeTeamJerseyColor;
			document.getElementById('awayTeamJerseyColor').value = dataToProcess.awayTeamJerseyColor;
			document.getElementById('homeTeamGKJerseyColor').value = dataToProcess.homeTeamGKJerseyColor;
			document.getElementById('awayTeamGKJerseyColor').value = dataToProcess.awayTeamGKJerseyColor;
			addItemsToList('LOAD_TEAMS',dataToProcess);
			document.getElementById('save_match_div').style.display = '';
		} else {
			document.getElementById('matchFileName').value = '';
			document.getElementById('tournament').value = '';
			document.getElementById('matchIdent').value = '';
			document.getElementById('matchId').value = '';
			document.getElementById('groundId').selectedIndex = 0;
			document.getElementById('homeSubstitutesPerTeam').selectedIndex = 0;
			document.getElementById('awaySubstitutesPerTeam').selectedIndex = 0;
			document.getElementById('homeTeamId').selectedIndex = 0;
			document.getElementById('awayTeamId').selectedIndex = 1;
			document.getElementById('homeTeamFormationId').selectedIndex = 0;
			document.getElementById('awayTeamFormationId').selectedIndex = 1;
			document.getElementById('homeTeamJerseyColor').selectedIndex = 0;
			document.getElementById('awayTeamJerseyColor').selectedIndex = 1;
			document.getElementById('homeTeamGKJerseyColor').selectedIndex = 0;
			document.getElementById('awayTeamGKJerseyColor').selectedIndex = 1;
			addItemsToList('LOAD_TEAMS',null);
			document.getElementById('save_match_div').style.display = 'none';
		}
		$('#homeTeamId').prop('selectedIndex', document.getElementById('homeTeamId').options.selectedIndex).change();
		$('#awayTeamId').prop('selectedIndex', document.getElementById('awayTeamId').options.selectedIndex).change();
		
		$('#homeTeamFormationId').prop('selectedIndex', document.getElementById('homeTeamFormationId').options.selectedIndex).change();
		$('#awayTeamFormationId').prop('selectedIndex', document.getElementById('awayTeamFormationId').options.selectedIndex).change();
		
		$('#homeTeamJerseyColor').prop('selectedIndex', document.getElementById('homeTeamJerseyColor').options.selectedIndex).change();
		$('#awayTeamJerseyColor').prop('selectedIndex', document.getElementById('awayTeamJerseyColor').options.selectedIndex).change();
		
		$('#homeTeamGKJerseyColor').prop('selectedIndex', document.getElementById('homeTeamGKJerseyColor').options.selectedIndex).change();
		$('#awayTeamGKJerseyColor').prop('selectedIndex', document.getElementById('awayTeamGKJerseyColor').options.selectedIndex).change();
		break;
	}
}
function uploadFormDataToSessionObjects(whatToProcess)
{
	var formData = new FormData();
	var url_path;

	$('input, select, textarea').each(
		function(index){  
			if($(this).is("select")) {
				formData.append($(this).attr('id'),$('#' + $(this).attr('id') + ' option:selected').val());  
			} else {
				formData.append($(this).attr('id'),$(this).val());  
			}	
		}
	);
	
	switch(whatToProcess.toUpperCase()) {
	case 'RESET_MATCH':
		url_path = 'reset_and_upload_match_setup_data';
		break;
	case 'SAVE_MATCH':
		url_path = 'upload_match_setup_data';
		break;
	}
	
	$.ajax({    
		headers: {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')},
        url : url_path,     
        data : formData,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',     
        success : function(data) {

        	switch(whatToProcess.toUpperCase()) {
			case 'RESET_MATCH_BEFORE_SETUP_MATCH':
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'RESET_MATCH':
        		alert('Match has been reset');
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'SAVE_MATCH':
        		document.setup_form.method = 'post';
        		document.setup_form.action = 'back_to_match';
        	   	document.setup_form.submit();
        		break;
        	}
        	
        },    
        error : function(e) {    
       	 	console.log('Error occured in uploadFormDataToSessionObjects with error description = ' + e);     
        }    
    });		
	
}
function processUserSelectionData(whatToProcess,dataToProcess){
	
	switch (whatToProcess) {
	case 'LOGGER_FORM_KEYPRESS':
		switch (dataToProcess) {
		case 190:
			processHockeyProcedures('POPULATE-ROAD-TO-FINAL');
			break;	
		case 89:
			processHockeyProcedures('POPULATE-PENALTY');
			break;
		case 66:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':
					processHockeyProcedures('POPULATE-HIGHLIGHT');
					break;
				case 'SANTOSH_TROPHY':
					processHockeyProcedures('POPULATE-HIGHLIGHT');
					break;
				case 'SUPER_CUP': case 'CONTINENTAL':
					processHockeyProcedures('POPULATE-PLAYOFFS');
					break;	
			}
			break;
		case 32:
			processHockeyProcedures('CLEAR-ALL');
			break;
		case 189:
			if(confirm('It will Also Delete Your Preview from Directory...\r\n\r\n Are You Sure To Animate Out?') == true){
				processHockeyProcedures('ANIMATE-OUT');
			}
			break;
		case 187:
			processHockeyProcedures('ANIMATE-OUT-SCOREBUG');
			break;
		case 112:
			processHockeyProcedures('POPULATE-SCOREBUG');
			break;
		case 73:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('SCOREBUG_OPTION',null);
			//processHockeyProcedures('APIDATA_GRAPHICS-OPTIONS'); 
			break;
		case 76:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					addItemsToList('SCOREBUG_OPTION_2',null); 
					break;
				case 'SUPER_CUP': case 'CONTINENTAL':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					addItemsToList('POINTS_TABLE2_OPTION',null);
					break;
			}
			break;
		case 79:
			processHockeyProcedures('ANIMATE-OUT-SCOREBUG_STAT');
			break;
		case 90:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					processHockeyProcedures('POPULATE-RED_CARD');
					break;
				case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					addItemsToList('RED_CARD_OPTION',null);
					break;
			}
			
			break;
		case 88:
			processHockeyProcedures('ANIMATE-OUT-RED_CARD');
			break;
		case 69:
			addItemsToList('EXTRA-TIME_OPTION',null);
			break;
		case 68:
			processHockeyProcedures('ANIMATE-OUT-EXTRA_TIME');
			break;
		case 67:
			addItemsToList('EXTRA-TIME-BOTH_OPTION',null);
			break;
		case 72:
			processHockeyProcedures('ANIMATE-IN-SPONSOR');
			break;
		case 74:
			processHockeyProcedures('ANIMATE-OUT-SPONSOR');
			break;
		case 113:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			processHockeyProcedures('NAMESUPER_GRAPHICS-OPTIONS');
			break;
		case 114:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('NAMESUPER_PLAYER-OPTIONS',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;
		case 115:
			processHockeyProcedures('POPULATE-FF-MATCHID');
			break;
		case 65:
			processHockeyProcedures('POPULATE-LT-MATCHID');
			break;
		case 86:
			processHockeyProcedures('POPULATE-FF-TEAMS');
			break;
		case 116:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('PLAYINGXI-OPTIONS',null);
			break;
		case 117:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					processHockeyProcedures('BUG_DB_GRAPHICS-OPTIONS');
					break;
				case 'SANTOSH_TROPHY':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					processHockeyProcedures('BUG_DB_GRAPHICS-OPTIONS');
					break;
			}
			break;
		case 118:
			processHockeyProcedures('POPULATE-L3-SCOREUPDATE');
			break;
		case 119:
			processHockeyProcedures('POPULATE-L3-MATCHSTATUS');
			break;
		case 120:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('NAMESUPER-CARD-OPTIONS',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;
		case 192:
			/*$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('AD-OPTIONS',null);*/
			//processHockeyProcedures('POPULATE-HERO-SPONSOR');
			processHockeyProcedures('POPULATE-QUAIFIERS');
			break;
		case 121:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('SUBSTITUTE-OPTIONS',null);
			break;
		case 122:
			switch ($('#selectedBroadcaster').val()){
			case 'I_LEAGUE':
				//alert('I_LEAGUE');
				processHockeyProcedures('POPULATE-L3-MATCHPROMO');
				break;
			 case 'SUPER_CUP': case 'CONTINENTAL':
				$("#select_event_div").hide();
				$("#match_configuration").hide();
				$("#hockey_div").hide();
				addItemsToList('FIXTURES-OPTIONS',null);
				break;
			}
			break;
		case 123:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			home_team = match_data.homeTeamId;
			away_team = match_data.awayTeamId;
			home_team_name = match_data.homeTeam.teamName4;
			away_team_name = match_data.awayTeam.teamName4;
			processHockeyProcedures('STAFF_GRAPHICS-OPTIONS');
			break;
		case 83:
			processHockeyProcedures('POPULATE-FF-MATCHSTATS');
			break;	
		case 82:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					//alert('I_LEAGUE');
					processHockeyProcedures('POPULATE-LT-BUG_REPLAY');
					break;
				 case 'SUPER_CUP': case 'CONTINENTAL':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					processHockeyProcedures('RESULT_PROMO_GRAPHICS-OPTIONS');
					break;
			}
			break;
		case 81:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			processHockeyProcedures('PROMO_GRAPHICS-OPTIONS');
			break;	
		case 191:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			processHockeyProcedures('SCOREBUGPROMO_GRAPHICS-OPTIONS');
			break;	
		/*case 190:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			processHockeyProcedures('LTPROMO_GRAPHICS-OPTIONS');
			break;	*/
		case 87:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					//alert('I_LEAGUE');
					processHockeyProcedures('POPULATE-DOUBLE_PROMO');
					break;
				 case 'SUPER_CUP': case 'CONTINENTAL':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					addItemsToList('DOUBLE_PROMO-OPTIONS',null);
					break;
			}
			break;
		case 80:
			switch ($('#selectedBroadcaster').val()){
				case 'HOCKEY_KHELO_INDIA':
					//alert('I_LEAGUE');
					processHockeyProcedures('POPULATE-POINTS_TABLE');
					break;
			}
			break;
		case 188:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('TOP_STATS-OPTIONS',null);
			break;
		case 70:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					addItemsToList('FORMATION-OPTIONS',null);
					break;
				case 'SUPER_CUP': case 'CONTINENTAL':
					processHockeyProcedures('ANIMATE-OUT-EXTRA_TIME_HALF');
					break;
			}
			break;
		case 71:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':
					//alert('I_LEAGUE');
					processHockeyProcedures('POPULATE-DOUBLE_SUBS');
					break;
				case 'SUPER_CUP': case 'CONTINENTAL':
					processHockeyProcedures('POPULATE-EXTRA_TIME_HALF');
					break;
			}
			break;
		case 219:
			processHockeyProcedures('HOME_GOAL');
			break;
		case 221:
			processHockeyProcedures('AWAY_GOAL');
			break;
		case 186:
			processHockeyProcedures('HOME_UNDO');
			break;
		case 222:
			processHockeyProcedures('AWAY_UNDO');
			break;
		case 75:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('SCOREBUG-CARD-OPTIONS',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;
		case 77:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('SCOREBUG-SUBSTITUTION-OPTIONS',null);
			break;
		case 85:
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#hockey_div").hide();
					addItemsToList('SINGLE_SUBSTITUTE-OPTIONS',null);
					break;
				case 'SUPER_CUP': case 'CONTINENTAL':
					processHockeyProcedures('POPULATE-CHANGE_PENALTY');
					break;
			}
			break;
			
		case 84:
			processHockeyProcedures('POPULATE-OFFICIALS');
			break;
		case 78:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#hockey_div").hide();
			addItemsToList('HEATMAP_PEAKDISTACE-OPTION',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;			
		}
		
		break;
	}
}
function processUserSelection(whichInput)
{	
	switch ($(whichInput).attr('name')) {
	case 'selectStatsType':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			if ($('#selectStatsType option:selected').val() == 'Formation_with_image') {
				formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation.sum';
			}else{
				formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation_NO_Image.sum';
			}
			break;
		case 'SANTOSH_TROPHY':
			if ($('#selectStatsType option:selected').val() == 'Formation_with_image') {
				formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Formation.sum';
			}else{
				formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Formation_NO_Image.sum';
			}
			break;
		}
		break;
	case 'overwrite_match_stats_index':

		document.getElementById('overwrite_match_stats_player_id').selectedIndex = 0;
		document.getElementById('overwrite_match_stats_type').value = '';
		document.getElementById('overwrite_match_stats_total_seconds').value = '';
	
		match_data.matchStats.forEach(function(ms,index,arr){
			if ($('#overwrite_match_stats_index option:selected').val() == ms.statsId) {
				document.getElementById('overwrite_match_stats_player_id').value = ms.playerId;
				document.getElementById('overwrite_match_stats_type').value = ms.statsType;
				document.getElementById('overwrite_match_stats_total_seconds').value = ms.totalMatchSeconds;
			}
		});

		break;

	case 'load_scene_btn':
	
		/*if(checkEmpty($('#vizIPAddress'),'IP Address Blank') == false
			|| checkEmpty($('#vizPortNumber'),'Port Number Blank') == false) {
			return false;
		}*/
    
	  	document.initialise_form.submit();
		break;
	
	case 'cancel_graphics_btn':
		$('#select_graphic_options_div').empty();
		document.getElementById('select_graphic_options_div').style.display = 'none';
		$("#select_event_div").show();
		$("#match_configuration").show();
		$("#hockey_div").show();
		break;
	case 'selectedBroadcaster':
		switch ($('#selectedBroadcaster :selected').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY':
			$('#vizPortNumber').attr('value','1980');
			$('label[for=vizScene], input#vizScene').hide();
			$('label[for=which_scene], select#which_scene').hide();
			$('label[for=which_layer], select#which_layer').hide();
			break;
		case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
			$('#vizPortNumber').attr('value','6100');
			$('label[for=vizScene], input#vizScene').hide();
			$('label[for=which_scene], select#which_scene').hide();
			$('label[for=which_layer], select#which_layer').hide();
			break;	
		}
		break;
	case 'homePlayers': case 'awayPlayers':
		$('#selected_player_name').html(whichInput.innerHTML);
		$('#selected_player_id').val(whichInput.value);
		document.getElementById('select_event_div').style.display = '';
		break;
	case 'log_teams_score_overwrite_btn': case 'log_match_stats_overwrite_btn': case 'log_match_subs_overwrite_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		switch ($(whichInput).attr('name')) {
		case 'log_teams_score_overwrite_btn': 
			processHockeyProcedures('LOG_OVERWRITE_TEAM_SCORE',whichInput);
			break;
		case 'log_match_stats_overwrite_btn':
			processHockeyProcedures('LOG_OVERWRITE_MATCH_STATS',whichInput);
			break;
		case 'log_match_subs_overwrite_btn':
			processHockeyProcedures('LOG_OVERWRITE_MATCH_SUBS',whichInput);
			break;
		}
		break;
	case 'number_of_undo_txt':
		if(whichInput.value < 0 && whichInput.value > match_data.events.length) {
			alert('Number of undos is invalid.\r\n Must be a positive number and less than the number of events available [' + match_data.events.length + ']');
			whichInput.selected = true;
			return false;
		}
		break;
	case 'selectTeam': case 'selectCaptianWicketKeeper':
		addItemsToList('POPULATE-PLAYER',match_data);
		break;
	case 'select_existing_hockey_matches':
		if(whichInput.value.toLowerCase().includes('new_match')) {
			initialiseForm('SETUP',null);
		} else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processHockeyProcedures('LOAD_SETUP',$('#select_existing_hockey_matches option:selected'));
		}
		break;
	case 'log_undo_btn':
		if(match_data.events.length > 0) {
			if($('#number_of_undo_txt').val() > match_data.events.length) {
				if(confirm('Number of undo [' + $('#number_of_undo_txt').val() + '] is bigger than number of events [' 
						+ match_data.events.length + ']. We will make both of them similiar') == false) {
					return false;
				}
				$('#number_of_undo_txt').val(match_data.events.length);
			}
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processHockeyProcedures('UNDO',$('#number_of_undo_txt'));
		} else {
			alert('No events found');
		}
		break;
	case 'log_replace_btn':
		processHockeyProcedures('REPLACE',match_data);
		break;
	case 'cancel_match_setup_btn':
		document.setup_form.method = 'post';
		document.setup_form.action = 'back_to_match';
	   	document.setup_form.submit();
		break;
	case 'matchFileName':
		if(document.getElementById('matchFileName').value) {
			document.getElementById('matchFileName').value = 
				document.getElementById('matchFileName').value.replace('.xml','') + '.xml';
		}
		break;
	case 'save_match_btn': case 'reset_match_btn':
		switch ($(whichInput).attr('name')) {
		case 'reset_match_btn':
	    	if (confirm('The setup selections of this match will be retained ' +
	    			'but the match data will be deleted permanently. Are you sure, you want to RESET this match?') == false) {
	    		return false;
	    	}
			break;
		}
		if (!checkEmpty(document.getElementById('matchFileName'),'Match Name')) {
			return false;
		} 
		if($('#homeTeamId option:selected').val() == $('#awayTeamId option:selected').val()) {
			alert('Both teams cannot be same. Please choose different home and away team');
			return false;
		}
		for(var tm=1;tm<=2;tm++) {
			for(var i=1;i<11;i++) {
				for(var j=i+1;j<=11;j++) {
					if(tm == 1) {
						if(document.getElementById('homePlayer_' + i).selectedIndex == document.getElementById('homePlayer_' + j).selectedIndex) {
							alert(document.getElementById('homePlayer_' + i).options[
								document.getElementById('homePlayer_' + i).selectedIndex].text.toUpperCase() + 
								' selected multiple times for HOME team');
							return false;
						}
					} else {
						if(document.getElementById('awayPlayer_' + i).selectedIndex == document.getElementById('awayPlayer_' + j).selectedIndex) {
							alert(document.getElementById('awayPlayer_' + i).options[
								document.getElementById('awayPlayer_' + i).selectedIndex].text.toUpperCase() + 
								' selected multiple times for AWAY team');
							return false;
						}
					}
				}
			}
		}
		switch ($(whichInput).attr('name')) {
		case 'save_match_btn': 
			uploadFormDataToSessionObjects('SAVE_MATCH');
			break;
		case 'reset_match_btn':
			processWaitingButtonSpinner('START_WAIT_TIMER');
			uploadFormDataToSessionObjects('RESET_MATCH');
			break;
		}
		break;
	case 'load_default_team_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		if($('#homeTeamId option:selected').val() == $('#awayTeamId option:selected').val()) {
			alert('Both teams cannot be same. Please choose different home and away team');
    		processWaitingButtonSpinner('END_WAIT_TIMER');
			return false;
		}
		processHockeyProcedures('LOAD_TEAMS',whichInput);
		document.getElementById('save_match_div').style.display = '';
		break;
	case 'setup_match_btn':
		document.hockey_form.method = 'post';
		document.hockey_form.action = 'setup';
	   	document.hockey_form.submit();
	   	processWaitingButtonSpinner('START_WAIT_TIMER');
		break;
	case 'load_match_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		processHockeyProcedures('LOAD_MATCH',$('#select_hockey_matches option:selected'));
		break;
	case 'log_event_btn':
		if(whichInput.id.toLowerCase() == 'undo') {
    		if(match_data == null || match_data.events.length <= 0) {
    			alert('No events found to perform undoes');
    			return false;
    		}
    		addItemsToList('LOAD_UNDO',match_data);
		} else if(whichInput.id.toLowerCase() == 'replace'){
			addItemsToList('LOAD_REPLACE',match_data);
			addItemsToList('POPULATE-OFF_PLAYER',match_data);
			addItemsToList('POPULATE-ON_PLAYER',match_data);
		} else if(whichInput.id.toLowerCase() == 'penalty'){
			processHockeyProcedures('RESET_PENALTY', null);
			addItemsToList('LOAD_PENALTY',match_data);
		}else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processHockeyProcedures('LOG_EVENT',whichInput);
		}
		break;
	case 'Home_goal_btn':
		processHockeyProcedures('LOG_EVENT',whichInput);
		break;	
	case 'cancel_undo_btn': case 'cancel_overwrite_btn': case 'cancel_event_btn': case 'cancel_replace_btn': case 'cancel_penalty_btn':
		document.getElementById('select_event_div').style.display = 'none';
		addItemsToList('LOAD_EVENTS',match_data); 
		processWaitingButtonSpinner('END_WAIT_TIMER');
		break;
	case 'select_teams':
		addItemsToList('POPULATE-OFF_PLAYER',match_data);
		addItemsToList('POPULATE-ON_PLAYER',match_data);
		break;
	case 'change_on':
		processHockeyProcedures('ANIMATE-CHANGE_ON');
		break;
	case 'change_on_formation':
		processHockeyProcedures('ANIMATE-CHANGE_ON_FORMATION');
		break;
	case 'change_on_formation_without_image':
		processHockeyProcedures('ANIMATE-CHANGE_ON_FORMATION_WITHOUT_IMAGE');
		break;	
	case 'populate_namesuper_btn': case 'populate_namesuper_player_btn': case 'populate_playingxi_btn': case 'populate_api_btn': case 'populate_bug_db_btn': case 'populate_namesuper_card_btn': 
	case 'populate_staff_btn': case 'populate_match_promo_btn':case 'populate_sponsor_btn': case 'populate_substitution_btn': case 'populate_formation_btn': case 'populate_scorebug_card_btn':
	case 'populate_scorebug_subs_btn': case 'populate_single_substitution_btn': case 'populate_playingxi_changeon_btn': case 'populate_points_table_btn': case 'populate_homesub_btn': 
	case 'populate_Away_btn': case 'populate_awaysub_btn': case 'populate_sub_btn': case 'populate_heatmap_btn': case 'populate_Top_Stats_btn': case 'populate_fixtures_btn':
	case 'populate_subchange_on_btn': case 'populate_double_promo_btn': case 'populate_ltmatch_promo_btn': case 'populate_scorebug_match_promo_btn': case 'populate_points_table2_btn':
	case 'populate_result_promo_btn':	
		processWaitingButtonSpinner('START_WAIT_TIMER');
		switch ($(whichInput).attr('name')) {
		case 'populate_subchange_on_btn':
			processHockeyProcedures('ANIMATE-SUB_CHANGE_ON');
			break;
		case 'populate_Away_btn':
			processHockeyProcedures('POPULATE-AWAYXI');
			break;
		case 'populate_awaysub_btn':
			processHockeyProcedures('POPULATE-AWAYSUB');
			break;
		case 'populate_homesub_btn':
			processHockeyProcedures('POPULATE-HOMESUB');
			break;
		case 'populate_sub_btn':
			processHockeyProcedures('POPULATE-SUBS_CHANGE_ON');
			break;
		case 'populate_heatmap_btn':
			processHockeyProcedures('POPULATE-L3-HEATMAP');
			break;
		case 'populate_Top_Stats_btn':
			processHockeyProcedures('POPULATE-L3-TOP_STATS');
			break;
		case 'populate_namesuper_btn':
			processHockeyProcedures('POPULATE-L3-NAMESUPER');
			break;
		case 'populate_namesuper_player_btn':
			processHockeyProcedures('POPULATE-L3-NAMESUPER-PLAYER');
			break;
		case 'populate_playingxi_btn':
			processHockeyProcedures('POPULATE-FF-PLAYINGXI');
			break;
		case 'populate_playingxi_changeon_btn':
			processHockeyProcedures('POPULATE-FF-PLAYINGXI_CHANGEON');
			break;
		case 'populate_bug_db_btn':
			processHockeyProcedures('POPULATE-L3-BUG-DB');
			break;
		case 'populate_namesuper_card_btn':
			processHockeyProcedures('POPULATE-L3-NAMESUPER-CARD');
			break;
		case 'populate_staff_btn':
			processHockeyProcedures('POPULATE-L3-STAFF');
			break;
		case 'populate_scorebug_match_promo_btn':
			processHockeyProcedures('POPULATE-SCOREBUG-PROMO');
			break;
		case 'populate_ltmatch_promo_btn':
			processHockeyProcedures('POPULATE-LT-PROMO');
			break;
		case 'populate_result_promo_btn':
			processHockeyProcedures('POPULATE-LT-RESULT');
			break;
		case 'populate_match_promo_btn':
			processHockeyProcedures('POPULATE-FF-PROMO');
			break;
		case 'populate_sponsor_btn':
			processHockeyProcedures('POPULATE-HERO-SPONSOR');
			break;
		case 'populate_substitution_btn':
			processHockeyProcedures('POPULATE-L3-SUBSTITUTE');
			break;
		case 'populate_single_substitution_btn':
			processHockeyProcedures('POPULATE-L3-SINGLE_SUBSTITUTE');
			break;
		case 'populate_scorebug_subs_btn':
			processHockeyProcedures('POPULATE-SCOREBUG-SUBS');
			break;
		case 'populate_formation_btn':
			processHockeyProcedures('POPULATE-FF-FORMATION');
			break;
		case 'populate_scorebug_card_btn':
			processHockeyProcedures('POPULATE-SCOREBUG-CARD');
			break;
		case 'populate_points_table_btn':
			processHockeyProcedures('POPULATE-POINTS_TABLE');
			break;
		case 'populate_points_table2_btn':
			processHockeyProcedures('POPULATE-POINTS_TABLE2');
			break;
		case 'populate_double_promo_btn':
			processHockeyProcedures('POPULATE-DOUBLE_PROMO');
			break;
		case 'populate_fixtures_btn':
			processHockeyProcedures('POPULATE-FIXTURES');
			break;
		}
		break;
	
	case 'populate_stats_btn':
		processHockeyProcedures('POPULATE-SCOREBUG_STATS');
		break;
	case 'populate_stats_two_btn':
		processHockeyProcedures('POPULATE-SCOREBUG_STATS_TWO');
		break;
	case 'populate_extra_time_btn':
		processHockeyProcedures('POPULATE-EXTRA_TIME');
		break;
	case 'populate_red_card_btn':
		processHockeyProcedures('POPULATE-RED_CARD');
		break;
	case 'populate_extra_time_both_btn':
		processHockeyProcedures('POPULATE-EXTRA_TIME_BOTH');
		break;
	default:
		switch ($(whichInput).attr('id')) {
		case 'overwrite_teams_total': case 'overwrite_match_time': 
			addItemsToList('LOAD_' + $(whichInput).attr('id').toUpperCase(),null);
			document.getElementById('select_event_div').style.display = '';
			break;
		default:
			if($(whichInput).attr('id').includes('_btn') && $(whichInput).attr('id').split('_').length >= 4) {
	    		switch ($(whichInput).attr('id').split('_')[1]) {
	    		case 'increment':
	    			$('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val(
						parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) + 1
					);
					break;
	    		case 'decrement':
					if(parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) > 0) {
		    			
						$('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
							+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val(
							parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
							+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) - 1
						);
						
					}
					break;
				}				
				processWaitingButtonSpinner('START_WAIT_TIMER');
				processHockeyProcedures('LOG_STAT',whichInput);
			}
			break;
		}
		break;
	}
}
function processHockeyProcedures(whatToProcess, whichInput)
{
	var value_to_process; 
	var prev_match_data = match_data;
	
	switch(whatToProcess) {
	case 'READ_CLOCK':
		//initialiseForm('UPDATE-MATCH-ON-OUTPUT-FORM');
		valueToProcess = $('#matchFileTimeStamp').val();
		//alert("1");
		break;
	case 'LOG_STAT':
		value_to_process = whichInput.id;
		break;
	case 'LOG_OVERWRITE_TEAM_SCORE': case 'LOG_OVERWRITE_MATCH_STATS': case 'LOG_OVERWRITE_MATCH_SUBS': 
		switch (whatToProcess) {
		case 'LOG_OVERWRITE_TEAM_SCORE':
			value_to_process = $('#overwrite_home_team_score').val() + ',' + $('#overwrite_away_team_score').val();
			break;
		case 'LOG_OVERWRITE_MATCH_STATS':
			value_to_process = $('#overwrite_match_stats_index option:selected').val() 
				+ ',' + $('#overwrite_match_stats_player_id option:selected').val()+ ',' + $('#overwrite_match_stats_type option:selected').val()
				+ ',' + $('#overwrite_match_stats_total_seconds').val();
			break;
		case 'LOG_OVERWRITE_MATCH_SUBS':
			value_to_process = $('#overwrite_match_sub_index option:selected').val() + ',' + $('#overwrite_match_player_id option:selected').val()
				+ ',' + $('#overwrite_match_subs_player_id option:selected').val();
			break;
		}
		break;
		
	case 'LOAD_TEAMS':
		value_to_process = $('#homeTeamId option:selected').val() + ',' + $('#awayTeamId option:selected').val();
		break;

	case 'LOAD_MATCH': case 'LOAD_SETUP':
		value_to_process = whichInput.val();
		break;
		
	case 'LOG_EVENT':
		value_to_process =  whichInput.id + ',' + $('#selected_player_id').val();
		break;
	
	case 'UNDO':
		value_to_process = $('#number_of_undo_txt').val();
		break;
	case 'REPLACE':
		value_to_process = $('#select_player option:selected').val() + ',' + $('#select_sub_player option:selected').val();
		break;
	case 'POPULATE-L3-HEATMAP':
		switch ($('#selectedBroadcaster').val()) {
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectHeatmappeakdistance option:selected').val() 
					+ ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectHeatmappeakdistance option:selected').val() 
					+ ',' + $('#selectPlayer option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-L3-TOP_STATS':
		switch ($('#selectedBroadcaster').val()) {
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectTopStats option:selected').val();
				break;	
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectTopStats option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-L3-NAMESUPER':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_GoalScorer.sum' + ',' + $('#selectNameSuper option:selected').val() ;
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_NameSuper.sum' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'HOCKEY_KHELO_INDIA':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT.sum' + ',' + 
					$('#selectNameSuper option:selected').val() ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper.sum' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectNameSuper option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-PENALTY':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_Penalty.sum';
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_Penalty.sum';
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/Penalty';
				break;
		}
		break;
	case 'POPULATE-L3-STAFF':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_HeadCoach.sum' + ',' + $('#selectStaff option:selected').val() ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper.sum' + ',' + $('#selectStaff option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectStaff option:selected').val() ;
				break;
		}
		break;
	case 'POPULATE-SCOREBUG-PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val();
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-LT-PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val();
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-LT-RESULT':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val();
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-FF-PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val();
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-L3-NAMESUPER-PLAYER':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_GoalScorer.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
					$('#selectCaptainWicketKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'HOCKEY_KHELO_INDIA':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
					$('#selectCaptainWicketKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
				$('#selectCaptainWicketKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectCaptainWicketKeeper option:selected').val() + ',' + 
					$('#selectPlayer option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectCaptainWicketKeeper option:selected').val() + ',' + 
					$('#selectPlayer option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-L3-NAMESUPER-CARD':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_NameSuper_Cards.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
					$('#selectCaptainWicketKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper_Cards.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
				$('#selectCaptainWicketKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + 
				$('#selectCaptainWicketKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + 
				$('#selectCaptainWicketKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
		}
		break;
	case 'POPULATE-SCOREBUG-CARD':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = $('#selectTeam option:selected').val() + ',' + $('#selectCaptainWicketKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-L3-ASTON-ADS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Aston_AD.sum' ;
			//alert(value_to_process);
			break;
		}
		break;
	case 'POPULATE-LT-BUG_REPLAY':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug_Replay.sum' ;
			//alert(value_to_process);
			break;
		case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Bug_Replay.sum';
			break;	
		}
		break;
	case 'POPULATE-L3-MATCHPROMO':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Promo.sum' ;
			break;
		}
		break;
	case 'POPULATE-QUAIFIERS':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/Qualifier_winners';
				break;
		}
		break;
	case 'POPULATE-FF-TEAMS':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-FF-MATCHID':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' ;
				break;
			case 'HOCKEY_KHELO_INDIA':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_MatchId.sum' ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchID.sum';
				break;
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/GameIntro';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/FullFrames';
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-LT-MATCHID':
		switch ($('#selectedBroadcaster').val()) {
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/LtGameIntro';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT';
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT';
				break;
		}
		break;
	case 'POPULATE-FF-MATCHSTATS':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Score_Goalers.sum' ;
			break;
			case 'HOCKEY_KHELO_INDIA':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_Score.sum' ;
			break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Score_Goalers.sum';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/FullFrames';
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-DOUBLE_PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Promo.sum' ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectDoublePromo option:selected').val() ;
				break;
		}
		break;
	case 'POPULATE-HIGHLIGHT':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum' ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Bug.sum';
				break;
		}
		break;
	case 'POPULATE-ROAD-TO-FINAL':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum' ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-PLAYOFFS':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum' ;
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames_Cut';
				break;
		}
		break;
	case 'POPULATE-FIXTURES':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectFixturesHeader option:selected').val() ;
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-POINTS_TABLE2':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectLeagueTable option:selected').val() ;
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-CONTINENTAL_POINTS_TABLE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/PointsTable.sum' ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/PointsTable.sum' + ',' + $('#selectLeagueTable option:selected').val();
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames';
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-POINTS_TABLE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/PointsTable.sum' ;
				break;
			case 'HOCKEY_KHELO_INDIA':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_PointsTable.sum';
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/PointsTable.sum' + ',' + $('#selectLeagueTable option:selected').val();
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames';
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-L3-SUBSTITUTE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_Subsitutes_Multi.sum' + ',' + $('#selectTeam option:selected').val()
					+ ',' + $('#selectStatsType option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_Substitutes_Multi.sum' + ',' + $('#selectTeam option:selected').val()
					+ ',' + $('#selectStatsType option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val()
					+ ',' + $('#selectStatsType option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val() ;
				//alert(value_to_process);
				break;	
		}
		break;
	case 'POPULATE-L3-SINGLE_SUBSTITUTE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_Subsitutes.sum' + ',' + $('#selectSingleSubTeam option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_Substitutes.sum' + ',' + $('#selectSingleSubTeam option:selected').val() ;
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-FF-PLAYINGXI':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/TeamLineUp_Subs.sum' + ',' + 
					$('#selectPlayingXI option:selected').val();
				break;
			case 'HOCKEY_KHELO_INDIA':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_LineUp.sum' + ',' + 
					$('#selectPlayingXI option:selected').val();
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/TeamLineUp_Subs.sum' + ',' + $('#selectPlayingXI option:selected').val();
				break;
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/LineUp' + ',' + $('#selectPlayingXI option:selected').val();
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectPlayingXI option:selected').val() + ',' + $('#selectPlayingXIType option:selected').val();
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectPlayingXI option:selected').val() + ',' + $('#selectPlayingXIType option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-FF-PLAYINGXI_CHANGEON':
		switch ($('#selectedBroadcaster').val()) {
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/TeamLineUp_Subs_Change.sum' + ',' + $('#selectPlayingXI option:selected').val();
				break;
		}
		break;
	case 'POPULATE-L3-BUG-DB':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = $('#bugdbScene').val() + ',' + $('#selectBugdb option:selected').val() ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = $('#bugdbScene').val() + ',' + $('#selectBugdb option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-L3-SCOREUPDATE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_ScoreUpdate.sum';
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_ScoreUpdate.sum';
				break;
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/LtGameIntro';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT';
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/LT';
				break;
		}
		break;
	case 'POPULATE-L3-MATCHSTATUS':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchStats.sum';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/FullFrames';
				break;
			case 'SUPER_CUP': case 'CONTINENTAL':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-SCOREBUG':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/ScoreBug.sum';
			//value_to_process = match_data.matchFileName + ',' + 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/ScoreBug.sum';
			break;
		case 'HOCKEY_KHELO_INDIA':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Footaball_Scorebug.sum';
			break;
		case 'SANTOSH_TROPHY':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/ScoreBug.sum';
			break;
		}
		break;
	case 'POPULATE-SCOREBUG_STATS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = $('#selectScorebugstats option:selected').val() ;
			break;
		case 'VIZ_TRI_NATION':
			value_to_process = $('#selectScorebugstats option:selected').val() + ',' + $('#selecthomedata').val() + ',' + $('#selectawaydata').val() ;
			break;
		case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = $('#selectScorebugstats option:selected').val() + ',' + $('#selecthomedata').val() + ',' + $('#selectawaydata').val() ;
			break;
		}
		break
	case 'POPULATE-SCOREBUG_STATS_TWO':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = $('#selectScorebugstatstwo option:selected').val() ;
			break;
		}
		break
	case 'POPULATE-EXTRA_TIME':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = $('#selectExtratime').val();
			break;
		}
		break;
	case 'POPULATE-RED_CARD':
		switch ($('#selectedBroadcaster').val()) {
		case 'VIZ_TRI_NATION':
			value_to_process = $('#selecthometeamredcard').val() + ',' + $('#selectawayteamredcard').val();
			break;
		case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = $('#selecthometeamredcard').val() + ',' + $('#selectawayteamredcard').val();
			break;
		}
		break;
	case 'POPULATE-EXTRA_TIME_BOTH':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = $('#selectExtratimeBoth').val();
			break;
		}
		break;
	case 'POPULATE-HERO-SPONSOR':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Aston_AD.sum' + ',' + $('#selectSponsor option:selected').val();
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Aston_AD.sum' + ',' + 'DESTINI';
			break;
		}
		break;
	case 'POPULATE-FF-FORMATION':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation.sum' + ',' + $('#selectTeam option:selected').val();
			value_to_process = formationScene + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val();
			break;
		case 'SANTOSH_TROPHY':
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation.sum' + ',' + $('#selectTeam option:selected').val();
			value_to_process = formationScene + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val();
			break;
		}
		break;
	case 'POPULATE-SCOREBUG-SUBS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val();
			break;	
		}
		break;
	case 'ANIMATE-CHANGE_ON_FORMATION':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation.sum' + ',' + $('#selectTeam option:selected').val();
			value_to_process = $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val();
			break;
		}
		break;
	case 'POPULATE-DOUBLE_SUBS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Subs_BothTeam.sum';
			break;
		}
		break;
	case 'POPULATE-OFFICIALS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_Officials.sum';
			break;
		case 'SANTOSH_TROPHY':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_Officials.sum';
			break;
		case 'VIZ_TRI_NATION':
			value_to_process = '/Default/LT';
			break;
		case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = '/Default/LT';
			break;
		}
		break;	
	case 'HOME_GOAL':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = 'HOME_GOAL';
			break;
		}
		break;
	case 'AWAY_GOAL':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = 'AWAY_GOAL';
			break;
		}
		break;
	case 'HOME_UNDO': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'VIZ_TRI_NATION':
			value_to_process = 'HOME_UNDO';
			break;
		}
		break;
	case 'AWAY_UNDO': 
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
			value_to_process = 'AWAY_UNDO';
			break;
		}
		break;	
	}
	
	if(match_data){
		if(whatToProcess != "LOAD_TEAMS"){
			value_to_process = match_data.matchFileName + ',' + value_to_process;
		}
	}

	$.ajax({    
        type : 'Get',     
        url : 'processHockeyProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + value_to_process, 
        dataType : 'json',
        success : function(data) {
			match_data = data;
			//alert(whatToProcess);
        	switch(whatToProcess) {
			case 'READ_CLOCK':
				if(match_data.clock) {
					if(document.getElementById('match_time_hdr')) {
						document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
							secondsTimeSpanToHMS(match_data.clock.matchTotalSeconds);
					}
				}
				
				if(data){
					if($('#matchFileTimeStamp').val() != data.matchFileTimeStamp) {
						document.getElementById('matchFileTimeStamp').value = data.matchFileTimeStamp;
						session_match = data;
						addItemsToList('LOAD_MATCH',data);
						addItemsToList('LOAD_EVENTS',data);
						document.getElementById('select_event_div').style.display = 'none';
					}
				}
				break;
			case 'POPULATE-SUBS_CHANGE_ON':
				processHockeyProcedures('ANIMATE-IN-SUBS_CHANGE_ON');	
				break;
			case 'POPULATE-FF-MATCHID': case 'POPULATE-FF-PLAYINGXI': case 'POPULATE-L3-MATCHSTATUS': case 'POPULATE-L3-MATCHPROMO': case 'POPULATE-FF-MATCHSTATS':
			case 'POPULATE-FF-PROMO': case 'POPULATE-DOUBLE_PROMO': case 'POPULATE-HERO-SPONSOR': case 'POPULATE-POINTS_TABLE': case 'POPULATE-FF-FORMATION':
			case 'POPULATE-DOUBLE_SUBS': case 'POPULATE-OFFICIALS': case 'POPULATE-HIGHLIGHT': case 'POPULATE-PENALTY': case 'POPULATE-FF-PLAYINGXI_CHANGEON':
			case 'POPULATE-LT-MATCHID': case 'POPULATE-HOMESUB': case 'POPULATE-AWAYXI': case 'POPULATE-AWAYSUB': case 'POPULATE-FF-TEAMS': case 'POPULATE-FIXTURES':
			case 'POPULATE-QUAIFIERS': case 'POPULATE-LT-PROMO': case 'POPULATE-POINTS_TABLE2': case 'POPULATE-PLAYOFFS': case 'POPULATE-LT-RESULT': case 'POPULATE-ROAD-TO-FINAL':
			case 'POPULATE-CONTINENTAL_POINTS_TABLE':	
				if(confirm('Animate In?') == true){
					switch(whatToProcess){
					case 'POPULATE-ROAD-TO-FINAL':
						processHockeyProcedures('ANIMATE-IN-ROAD-TO-FINAL');			
						break;
					case 'POPULATE-PLAYOFFS':
						processHockeyProcedures('ANIMATE-IN-PLAYOFFS');			
						break;
					case 'POPULATE-HOMESUB':
						processHockeyProcedures('ANIMATE-IN-HOMESUB');			
						break;
					case 'POPULATE-AWAYXI':
						processHockeyProcedures('ANIMATE-IN-AWAYXI');			
						break;
					case 'POPULATE-AWAYSUB':
						processHockeyProcedures('ANIMATE-IN-AWAYSUB');			
						break;
					case 'POPULATE-PENALTY':
						processHockeyProcedures('ANIMATE-IN-PENALTY');			
						break;
					case 'POPULATE-HIGHLIGHT':
						processHockeyProcedures('ANIMATE-IN-HIGHLIGHT');			
						break;
					case 'POPULATE-FF-TEAMS':
						processHockeyProcedures('ANIMATE-IN-FF_TEAMS');			
						break;
					case 'POPULATE-FF-MATCHID':
						processHockeyProcedures('ANIMATE-IN-MATCHID');			
						break;
					case 'POPULATE-LT-MATCHID':
						processHockeyProcedures('ANIMATE-IN-LT_MATCHID');			
						break;
					case 'POPULATE-FF-MATCHSTATS':
						processHockeyProcedures('ANIMATE-IN-MATCHSTATS');		
						break;
					case 'POPULATE-DOUBLE_PROMO':
						processHockeyProcedures('ANIMATE-IN-DOUBLE_PROMO');
						break;
					case 'POPULATE-FF-PLAYINGXI':
						processHockeyProcedures('ANIMATE-IN-PLAYINGXI');		
						break;
					case 'POPULATE-FF-PLAYINGXI_CHANGEON':
						processHockeyProcedures('ANIMATE-IN-PLAYINGXI_CHANGEON');		
						break;
					case 'POPULATE-L3-MATCHSTATUS':
						processHockeyProcedures('ANIMATE-IN-MATCHSTATUS');
						break;
					case 'POPULATE-L3-MATCHPROMO':
						processHockeyProcedures('ANIMATE-IN-MATCHPROMO');
						break;
					case 'POPULATE-LT-PROMO':
						processHockeyProcedures('ANIMATE-IN-LTPROMO');
						break;
					case 'POPULATE-LT-RESULT':
						processHockeyProcedures('ANIMATE-IN-RESULT');
						break;
					case 'POPULATE-FF-PROMO':
						processHockeyProcedures('ANIMATE-IN-PROMO');
						break;
					case 'POPULATE-HERO-SPONSOR':
						processHockeyProcedures('ANIMATE-IN-ASTON-ADS');
						break;
					case 'POPULATE-CONTINENTAL_POINTS_TABLE':
						processHockeyProcedures('ANIMATE-IN-POINTS_TABLE');
						break;
					case 'POPULATE-POINTS_TABLE':
						processHockeyProcedures('ANIMATE-IN-POINTS_TABLE');
						break;
					case 'POPULATE-POINTS_TABLE2':
						processHockeyProcedures('ANIMATE-IN-POINTS_TABLE2');
						break;
					case 'POPULATE-FIXTURES':
						processHockeyProcedures('ANIMATE-IN-FIXTURES');
						break;
					case 'POPULATE-QUAIFIERS':
						processHockeyProcedures('ANIMATE-IN-QUAIFIERS');
						break;
					case 'POPULATE-FF-FORMATION':
						processHockeyProcedures('ANIMATE-IN-FORMATION');
						break;
					case 'POPULATE-DOUBLE_SUBS':
						processHockeyProcedures('ANIMATE-IN-DOUBLE_SUBS');
						break;
					case 'POPULATE-OFFICIALS':
						processHockeyProcedures('ANIMATE-IN-OFFICIALS');
						break;
					}
				}
				break;
			case 'POPULATE-L3-HEATMAP':
				if(data.api_photo =='SUCCESS'){
					if(confirm('Animate In?') == true){
						switch(whatToProcess){
							case 'POPULATE-L3-HEATMAP':
							processHockeyProcedures('ANIMATE-IN-HEATMAP');				
							break;
						}
					}
				}else{
					alert('file does not exist!')
				}
				break;
				
			case 'POPULATE-SCOREBUG': case 'POPULATE-L3-NAMESUPER': case 'POPULATE-L3-NAMESUPER-PLAYER':  case 'POPULATE-L3-TOP_STATS':
			case 'POPULATE-L3-BUG-DB': case 'POPULATE-L3-SCOREUPDATE':  case 'POPULATE-L3-NAMESUPER-CARD':
			case 'POPULATE-L3-SUBSTITUTE':  case 'POPULATE-L3-STAFF':  case 'POPULATE-LT-BUG_REPLAY': case 'POPULATE-L3-SINGLE_SUBSTITUTE':
			
				if(confirm('Animate In?') == true){
					switch(whatToProcess){
					case 'POPULATE-SCOREBUG':
						processHockeyProcedures('ANIMATE-IN-SCOREBUG');				
						break;
					case 'POPULATE-L3-TOP_STATS':
						processHockeyProcedures('ANIMATE-IN-TOP_STATS');				
						break;
					case 'POPULATE-L3-NAMESUPER':
						processHockeyProcedures('ANIMATE-IN-NAMESUPERDB');				
						break;
					case 'POPULATE-L3-NAMESUPER-PLAYER':
						processHockeyProcedures('ANIMATE-IN-NAMESUPER');				
						break;
					case 'POPULATE-L3-NAMESUPER-CARD':
						processHockeyProcedures('ANIMATE-IN-NAMESUPER_CARD');				
						break;
					case 'POPULATE-L3-BUG-DB':
						processHockeyProcedures('ANIMATE-IN-BUG-DB');
						break;
					case 'POPULATE-L3-SCOREUPDATE':
						processHockeyProcedures('ANIMATE-IN-SCOREUPDATE');
						break;
					case 'POPULATE-LT-BUG_REPLAY':
						processHockeyProcedures('ANIMATE-IN-BUG_REPLAY');
						break;
					case 'POPULATE-L3-SUBSTITUTE':
						processHockeyProcedures('ANIMATE-IN-SUBSTITUTE');
						break;
					case 'POPULATE-L3-SINGLE_SUBSTITUTE':
						processHockeyProcedures('ANIMATE-IN-SINGLE_SUBSTITUTE');
						break;
					case 'POPULATE-L3-STAFF':
						processHockeyProcedures('ANIMATE-IN-STAFF');
						break;
					}
				}
				break;
			case 'NAMESUPER_GRAPHICS-OPTIONS':
				addItemsToList('NAMESUPER-OPTIONS',data);
				break;
			case 'STAFF_GRAPHICS-OPTIONS':
				//alert(home_team);
				addItemsToList('STAFF-OPTIONS',data);
				break;
			case 'RESULT_PROMO_GRAPHICS-OPTIONS':
				addItemsToList('RESULT_PROMO-OPTIONS',data);
				break;
			case 'PROMO_GRAPHICS-OPTIONS':
				addItemsToList('MATCH-PROMO-OPTIONS',data);
				break;
			case 'SCOREBUGPROMO_GRAPHICS-OPTIONS':
				addItemsToList('SCOREBUGPROMO-OPTIONS',data);
				break;
			case 'LTPROMO_GRAPHICS-OPTIONS':
				addItemsToList('LT_MATCH-PROMO-OPTIONS',data);
				break;	
			case 'BUG_DB_GRAPHICS-OPTIONS':
				addItemsToList('BUG_DB-OPTIONS',data);
				addItemsToList('POPULATE-BUG-SCENE',data);
				break;
			case 'APIDATA_GRAPHICS-OPTIONS':
				addItemsToList('APIDATA-OPTIONS',data);				
				break;				
    		case 'LOG_OVERWRITE_TEAM_SCORE': case 'LOG_OVERWRITE_MATCH_STATS': case 'LOG_OVERWRITE_MATCH_SUBS': 
    		case 'UNDO': case 'REPLACE': case 'HOME_UNDO': case 'AWAY_UNDO':
        		addItemsToList('LOAD_MATCH',data);
				addItemsToList('LOAD_EVENTS',data);
				document.getElementById('select_event_div').style.display = 'none';
        		break;
        	case 'LOAD_TEAMS':
        		addItemsToList('LOAD_TEAMS',data);
        		break;
        	case 'HOME_GOAL': case 'AWAY_GOAL':
        		addItemsToList('LOAD_MATCH',data);
        		break;	
			case 'LOG_EVENT': case 'LOAD_MATCH':
        		addItemsToList('LOAD_MATCH',data);
	        	switch(whatToProcess) {
	        	case 'LOAD_MATCH':
					document.getElementById('hockey_div').style.display = '';
					document.getElementById('select_event_div').style.display = 'none';
					setInterval(displayMatchTime, 500);
					break;
				}
        		break;
        	case 'LOAD_SETUP':
        		initialiseForm('SETUP',data);
        		break;
        	}
    		processWaitingButtonSpinner('END_WAIT_TIMER');
	    },    
	    error : function(e) {    
	  	 	console.log('Error occured in ' + whatToProcess + ' with error description = ' + e);     
	    }    
	});
}
function addItemsToList(whatToProcess, dataToProcess)
{
	var max_cols,div,linkDiv,anchor,row,cell,header_text,select,option,tr,th,thead,text,table,tbody,playerName,api_value_home,api_value_away;
	var cellCount = 0;
	var addSelect = false;
	
	switch (whatToProcess) {
	case 'POPULATE-PLAYER':
		$('#selectPlayer').empty();
		if(match_data.homeTeamId ==  $('#selectTeam option:selected').val()){
			match_data.homeSquad.forEach(function(hs,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
	                value: hs.playerId,
	                text: hs.jersey_number + ' - ' + hs.full_name
		        }))					
			});
			match_data.homeSubstitutes.forEach(function(hsub,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
					value: hsub.playerId,
					text: hsub.jersey_number + ' - ' + hsub.full_name + ' (SUB)'
				}))
			});
		}
		else {
			match_data.awaySquad.forEach(function(as,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
	                value: as.playerId,
	                text: as.jersey_number + ' - ' + as.full_name
		        }))					
			});
			match_data.awaySubstitutes.forEach(function(asub,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
					value: asub.playerId,
					text: asub.jersey_number + ' - ' + asub.full_name + ' (SUB)'
				}))
			});
		}
		
		break;
	
	case 'POPULATE-BUG-SCENE':
	
		$('#bugdbScene').empty();
		dataToProcess.forEach(function(bug,index,arr1){
			switch ($('#selectedBroadcaster').val().toUpperCase()) {
			case 'I_LEAGUE':
				if(bug.bugId == $('#selectBugdb option:selected').val()){
					if(bug.text2 == ''){
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum';
					}else{
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum';
					}
				}
				break;
			case 'SANTOSH_TROPHY':
				if(bug.bugId == $('#selectBugdb option:selected').val()){
					if(bug.text2 == ''){
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Bug.sum';
					}else{
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Bug.sum';
					}
				}
				break;	
			}
			
		});
		break;
	
	case 'NAMESUPER-OPTIONS': case "NAMESUPER_PLAYER-OPTIONS": case'PLAYINGXI-OPTIONS': case 'API-OPTIONS':case 'BUG_DB-OPTIONS': case 'NAMESUPER-CARD-OPTIONS': 
	case 'STAFF-OPTIONS': case 'MATCH-PROMO-OPTIONS':case 'AD-OPTIONS': case 'SUBSTITUTE-OPTIONS': case 'FORMATION-OPTIONS': case 'SCOREBUG-CARD-OPTIONS':
	case 'SCOREBUG-SUBSTITUTION-OPTIONS': case 'SINGLE_SUBSTITUTE-OPTIONS': case 'HEATMAP_PEAKDISTACE-OPTION': case 'LT_MATCH-PROMO-OPTIONS': case 'SCOREBUGPROMO-OPTIONS':
	case 'TOP_STATS-OPTIONS': case 'FIXTURES-OPTIONS': case 'POINT_TABLE-OPTIONS': case 'DOUBLE_PROMO-OPTIONS': case 'POINTS_TABLE2_OPTION': case 'RESULT_PROMO-OPTIONS':
	
		switch ($('#selectedBroadcaster').val().toUpperCase()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
		case 'HOCKEY_KHELO_INDIA':

			$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
			
			switch(whatToProcess){
				case 'DOUBLE_PROMO-OPTIONS':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
						case 'SUPER_CUP': case 'CONTINENTAL':
							select = document.createElement('select');
							select.id = 'selectDoublePromo';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'TODAY';
							option.text = 'TODAY';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'TOMORROW';
							option.text = 'TOMORROW';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							
							option = document.createElement('input');
				    		option.type = 'button';
				    		
				    		option.name = 'populate_double_promo_btn';
				    		option.value = 'Populate Double Promo';
				    		
				    		option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						    
						    row.insertCell(1).appendChild(div);
						    
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
					
						    div.append(option);
						    
						    row.insertCell(2).appendChild(div);
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
						}
					break;
				case 'FIXTURES-OPTIONS':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
						case 'SUPER_CUP': case 'CONTINENTAL':
						
						select = document.createElement('select');
						select.id = 'selectFixturesHeader';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = 'fixture';
						option.text = 'Fixture';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'result';
						option.text = 'Result';
						select.appendChild(option);
						
						row.insertCell(0).appendChild(select);
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_fixtures_btn';
			    		option.value = 'Populate Fixtures';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					    
					    row.insertCell(1).appendChild(div);
					    
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(2).appendChild(div);
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					}
						
					break;
				case 'POINTS_TABLE2_OPTION':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
						case 'SUPER_CUP': case 'CONTINENTAL':
							select = document.createElement('select');
							select.id = 'selectLeagueTable';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'SemiFinal1';
							option.text = 'Semi Final 1';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'SemiFinal2';
							option.text = 'Semi Final 2';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							
							option = document.createElement('input');
				    		option.type = 'button';
				    		
				    		option.name = 'populate_points_table2_btn';
				    		option.value = 'Populate Points-Table';
				    		
				    		option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						    
						    row.insertCell(1).appendChild(div);
						    
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
					
						    div.append(option);
						    
						    row.insertCell(2).appendChild(div);
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
					}
					break;
				case 'POINT_TABLE-OPTIONS':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
						case 'SUPER_CUP': case 'CONTINENTAL':
							select = document.createElement('select');
							select.id = 'selectLeagueTable';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'LeagueTableA';
							option.text = 'Group A League Table';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'LeagueTableB';
							option.text = 'Group B League Table';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'LeagueTableC';
							option.text = 'Group C League Table';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'LeagueTableD';
							option.text = 'Group D League Table';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							break;
						case 'SANTOSH_TROPHY': 
							select = document.createElement('select');
				
							select.id = 'selectLeagueTable';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'LeagueTableA';
							option.text = 'Group A League Table';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'LeagueTableB';
							option.text = 'Group B League Table';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							break;
					}
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_points_table_btn';
		    		option.value = 'Populate Points-Table';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(1).appendChild(div);
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(2).appendChild(div);
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'SCOREBUG-SUBSTITUTION-OPTIONS':					
					switch ($('#selectedBroadcaster').val().toUpperCase()){
					case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
						select = document.createElement('select');
						select.id = 'selectTeam';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = match_data.homeTeamId;
						option.text = match_data.homeTeam.teamName1;
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = match_data.awayTeamId;
						option.text = match_data.awayTeam.teamName1;
						select.appendChild(option);
						
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						select = document.createElement('select');
						select.id = 'selectStatsType';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = 'single';
						option.text = 'Single Substitution';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'double';
						option.text = 'Double Substitution';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'triple';
						option.text = 'Triple Substitution';
						select.appendChild(option);
						
					    select.setAttribute('onchange',"processUserSelection(this)");
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_sub_btn';
			    		option.value = 'ChangeOn';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
						div.append(option);
							    
					    row.insertCell(cellCount).appendChild(div);
						cellCount = cellCount + 1;
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_scorebug_subs_btn';
			    		option.value = 'Populate Subs';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY':
						select = document.createElement('select');
						select.id = 'selectTeam';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = match_data.homeTeamId;
						option.text = match_data.homeTeam.teamName1;
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = match_data.awayTeamId;
						option.text = match_data.awayTeam.teamName1;
						select.appendChild(option);
						
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						select = document.createElement('select');
						select.id = 'selectStatsType';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = 'single';
						option.text = 'Single Substitution';
						select.appendChild(option);
						
					    select.setAttribute('onchange',"processUserSelection(this)");
						row.insertCell(cellCount).appendChild(select);
						
						cellCount = cellCount + 1;
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_scorebug_subs_btn';
			    		option.value = 'Populate Subs';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					}
					break;
				case 'FORMATION-OPTIONS':
					switch ($('#selectedBroadcaster').val().toUpperCase()) {
					case 'I_LEAGUE':
						formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation_NO_Image.sum';
						break;
					case 'SANTOSH_TROPHY':
						formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Formation_NO_Image.sum';
						break;
					}
					select = document.createElement('select');
					
					select.id = 'selectTeam';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.id = 'selectStatsType';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'Formation_without_image';
					option.text = 'Formation Without Image';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Formation_with_image';
					option.text = 'Formation With Image';
					select.appendChild(option);
					
				    select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_formation_btn';
		    		option.value = 'Populate Formation';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
				   /* option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'change_on_formation';
		    		option.value = 'Change On';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");*/
				    
				    div = document.createElement('div');
				    div.append(option);
					
					row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'SINGLE_SUBSTITUTE-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectSingleSubTeam';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_single_substitution_btn';
		    		option.value = 'Populate Substitution';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;	
				case 'SUBSTITUTE-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectTeam';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.id = 'selectStatsType';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'single';
					option.text = 'Single Substitution';
					select.appendChild(option);
					
					/*option = document.createElement('option');
					option.value = 'double';
					option.text = 'Double Substitution';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'triple';
					option.text = 'Triple Substitution';
					select.appendChild(option);*/
					
				    select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_subchange_on_btn';
		    		option.value = 'ChangeOn';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
					div.append(option);
						    
				    row.insertCell(cellCount).appendChild(div);
					cellCount = cellCount + 1;
					
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_substitution_btn';
		    		option.value = 'Populate Substitution';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'RESULT_PROMO-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.matchnumber;
                    option.text = oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'MATCH-PROMO-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.matchnumber;
                    option.text = oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'SCOREBUGPROMO-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.matchnumber;
                    option.text = oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'LT_MATCH-PROMO-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.matchnumber;
                    option.text = oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;	
				case'PLAYINGXI-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectPlayingXI';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					switch ($('#selectedBroadcaster').val().toUpperCase()) {
						case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': 
						case 'CONTINENTAL':
						select = document.createElement('select');
						select.id = 'selectPlayingXIType';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = 'without_image';
						option.text = 'WITHOUT IMAGE';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'with_image';
						option.text = 'WITH IMAGE';
						select.appendChild(option);
						
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_homesub_btn';
			    		option.value = 'ChangeOn 1';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
						div.append(option);
							    
					    row.insertCell(cellCount).appendChild(div);
						cellCount = cellCount + 1;
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_Away_btn';
			    		option.value = 'ChangeOn 2';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
						div.append(option);
						
					    row.insertCell(cellCount).appendChild(div);
						cellCount = cellCount + 1;
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_awaysub_btn';
			    		option.value = 'ChangeOn 3';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
						div.append(option);
						
					    row.insertCell(cellCount).appendChild(div);
						cellCount = cellCount + 1;
						
						break;
					}
					
				    switch(whatToProcess){
						case'PLAYINGXI-OPTIONS':
							option = document.createElement('input');
				    		option.type = 'button';
				    		
				    		option.name = 'populate_playingxi_btn';
				    		option.value = 'Populate Teams LineUp';
				    		
				    		option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						    
						    row.insertCell(cellCount).appendChild(div);
						    cellCount = cellCount + 1;
							break;
					}
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case'NAMESUPER-OPTIONS':
					select = document.createElement('select');
					select.style = 'width:130px';
					select.id = 'selectNameSuper';
					select.name = select.id;
					
					dataToProcess.forEach(function(ns,index,arr1){
						option = document.createElement('option');
						option.value = ns.namesuperId;
						option.text = ns.subHeader ;
						select.appendChild(option);
					});
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					break;
			case 'STAFF-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectStaff';
				select.name = select.id;
				
				dataToProcess.forEach(function(st,index,arr1){
					if(st.clubId == home_team){
						option = document.createElement('option');
						option.value = st.staffId;
						option.text = st.name + " - " + home_team_name ;
						select.appendChild(option);
					}else if(st.clubId == away_team){
						option = document.createElement('option');
						option.value = st.staffId;
						option.text = st.name + " - " + away_team_name ;
						select.appendChild(option);
					}
				});
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				break;
			case 'AD-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectSponsor';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'xpulse';
				option.text = 'Xpulse 200';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'passion';
				option.text = 'Passion';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Glamour';
				option.text = 'Glamour';
				select.appendChild(option);

				option = document.createElement('option');
				option.value = 'Splendor';
				option.text = 'Splendor';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Destini';
				option.text = 'HeroDestini';
				select.appendChild(option);
				
				//select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
						
			case 'API-OPTIONS':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				//select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;

				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectStats';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'ball_possession';
				option.text = 'BALL POSSESSION';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'shots';
				option.text = 'SHOTS';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'shot_on_target';
				option.text = 'SHOTS ON TARGET';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'yellow_card';
				option.text = 'YELLOW CARD';
				select.appendChild(option);
				
				//select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
			case 'TOP_STATS-OPTIONS':
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectTopStats';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'Best Runner';
				option.text = 'Best Runner';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Best Sprinter';
				option.text = 'Best Sprinter';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Highest Distance';
				option.text = 'Highest Distance';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Team Top Speed';
				option.text = 'Team Top Speed';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				break;
			case 'HEATMAP_PEAKDISTACE-OPTION':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;

				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectHeatmappeakdistance';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'heatmap';
				option.text = 'Heat Map';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'peakdistance';
				option.text = 'Peak Distance';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectPlayer';
				select.name = select.id;
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
				
			case 'SCOREBUG-CARD-OPTIONS':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;

				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectCaptainWicketKeeper';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'player';
				option.text = 'Player';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'yellow_card';
				option.text = 'YELLOW CARD';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'red_card';
				option.text = 'RED CARD';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'yellow_red';
				option.text = '2YELLOW';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectPlayer';
				select.name = select.id;
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;	
			case 'NAMESUPER-CARD-OPTIONS':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;

				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectCaptainWicketKeeper';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'yellow';
				option.text = 'YELLOW CARD';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'red';
				option.text = 'RED CARD';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'yellow_red';
				option.text = '2YELLOW/RED';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectPlayer';
				select.name = select.id;
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
				
			case 'NAMESUPER_PLAYER-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectTeam';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
				
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
	
					select = document.createElement('select');
					select.style = 'width:100px';
					select.id = 'selectCaptainWicketKeeper';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'Player';
					option.text = 'Player';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Player_Today_Goal';
					option.text = 'Player Goals Today';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Player_Role';
					option.text = 'Player Role';
					select.appendChild(option);
	
					option = document.createElement('option');
					option.value = 'Player Of The Match';
					option.text = 'Hero Of The Match';
					select.appendChild(option);
					
					/*option = document.createElement('option');
					option.value = 'Goal_Scorer';
					option.text = 'Goal Scorer';
					select.appendChild(option);*/
					
					option = document.createElement('option');
					option.value = 'Captain';
					option.text = 'Captain';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Captain-GoalKeeper';
					option.text = 'Captain-GoalKeeper';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Goal_Keeper';
					option.text = 'GoalKeeper';
					select.appendChild(option);
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.style = 'width:100px';
					select.id = 'selectPlayer';
					select.name = select.id;
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
				break;
				
			case 'BUG_DB-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectBugdb';
				select.name = select.id;
				
				dataToProcess.forEach(function(bug,index,arr1){
					option = document.createElement('option');
					option.value = bug.bugId;
					option.text = bug.prompt;
					select.appendChild(option);
				});
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				switch(whatToProcess){
					case 'BUG_DB-OPTIONS':
						select = document.createElement('input');
						select.type = "text";
						select.id = 'bugdbScene';
						select.name = select.id;
						//select.value = 'D:/DOAD_In_House_Everest/Everest_Cricket/EVEREST_GPCL2022/Scenes/Bug_SingleLine.sum';
						
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						break;
					}
				break;
			}
			
			switch (whatToProcess) {
			case 'AD-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_sponsor_btn';
				option.value = 'Populate Sponsor';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'RESULT_PROMO-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_result_promo_btn';
				option.value = 'Populate Result';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'MATCH-PROMO-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_match_promo_btn';
				option.value = 'Populate Match Promo';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'SCOREBUGPROMO-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_scorebug_match_promo_btn';
				option.value = 'Populate Match Promo';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'LT_MATCH-PROMO-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_ltmatch_promo_btn';
				option.value = 'Populate Match Promo';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;	
			case 'NAMESUPER-CARD-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_namesuper_card_btn';
			    option.value = 'Populate Namesuper Card';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'TOP_STATS-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_Top_Stats_btn';
			    option.value = 'Populate Top Stats';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'HEATMAP_PEAKDISTACE-OPTION':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_heatmap_btn';
			    option.value = 'Populate Image';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'SCOREBUG-CARD-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_scorebug_card_btn';
			    option.value = 'Populate ScoreBug Card';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;	
			case 'STAFF-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_staff_btn';
			    option.value = 'Populate Staff';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
				
			case'NAMESUPER-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_namesuper_btn';
			    option.value = 'Populate Namesuper';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'NAMESUPER_PLAYER-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';	
				option.name = 'populate_namesuper_player_btn';
			    option.value = 'Populate Namesuper-Player';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'BUG_DB-OPTIONS':
				option = document.createElement('input');
		    	option.type = 'button';
				option.name = 'populate_bug_db_btn';
			    option.value = 'Populate Bug';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			/*case'PLAYINGXI-OPTIONS':
				option.name = 'populate_playingxi_btn';
		    	option.value = 'Populate Teams LineUp';
				break;*/	
			
		    
		}
			break;
		}
		break;
	/*case 'APIDATA-OPTIONS':
		var home_name,away_name;
		api_value_home = '';
		api_value_away = '';
		header_text = document.createElement('h6');
		header_text.innerHTML = 'DOAD API DATA';
		document.getElementById('select_graphic_options_div').appendChild(header_text);
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');

		table.appendChild(tbody);
		document.getElementById('select_graphic_options_div').appendChild(table);

		row = tbody.insertRow(tbody.rows.length);
		
		header_text = document.createElement('h6');
		if(dataToProcess.apiData.length > 0) {
			for(var i = 0; i <= dataToProcess.apiData.length -1; i++ ) {
				if(dataToProcess.apiData[i].team_id ==  dataToProcess.homeTeam.teamApiId) {
					home_name = dataToProcess.apiData[i].team_name;
					
					if(dataToProcess.apiData[i].param_name == 'Yellow card') {
						api_value_home =  'YELLOW: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Red card') {
						api_value_home = api_value_home + ' RED: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Offsides') {
						api_value_home = api_value_home + ' OFFSIDES: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Shots') {
						api_value_home = api_value_home + ' SHOTS: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Shots on target') {
						api_value_home = api_value_home + ' SHOTS ON TARGET: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Corner') {
						api_value_home =  api_value_home + ' CORNERS: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Tackles') {
						api_value_home =  api_value_home + ' TACKLES: ' + dataToProcess.apiData[i].value + ', ';
					}
				}
				
				
				if(dataToProcess.apiData[i].team_id == dataToProcess.awayTeam.teamApiId) {
					away_name = dataToProcess.apiData[i].team_name;
					
					if(dataToProcess.apiData[i].param_name == 'Yellow card') {
						api_value_away = ' YELLOW: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Red card') {
						api_value_away = api_value_away + ' RED: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Offsides') {
						api_value_away = api_value_away + ' OFFSIDES: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Shots') {
						api_value_away = api_value_away + ' SHOTS: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Shots on target') {
						api_value_away = api_value_away + ' SHOTS ON TARGET: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Corner') {
						api_value_away = api_value_away + ' CORNERS: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Tackles') {
						api_value_away = api_value_away + ' TACKLES: ' + dataToProcess.apiData[i].value + ', ';
					}
				}
			}
			header_text.innerHTML = header_text.innerHTML  + home_name + ' : ' + '[ ' + api_value_home + ' ]' + "<br>" + "<br>" 
									+ away_name  + ' : ' + '[ ' + api_value_away + ' ]';
			row.insertCell(0).appendChild(header_text);
			
		}
		break;*/	
	case 'SCOREBUG_OPTION': case 'EXTRA-TIME_OPTION': case 'SCOREBUG_OPTION_2': case 'EXTRA-TIME-BOTH_OPTION': case 'RED_CARD_OPTION':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':

			$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
			
			switch(whatToProcess){
				case 'RED_CARD_OPTION':
					select = document.createElement('input');
					select.type = "text";
					select.id = 'selecthometeamredcard';
					select.value = '';
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('input');
					select.type = "text";
					select.id = 'selectawayteamredcard';
					select.value = '';
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'EXTRA-TIME_OPTION':
					select = document.createElement('input');
					select.type = "text";
					select.id = 'selectExtratime';
					select.value = '';
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					break;
				case 'EXTRA-TIME-BOTH_OPTION':
					select = document.createElement('input');
					select.type = "text";
					select.id = 'selectExtratimeBoth';
					select.value = '';
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					break;
				case 'SCOREBUG_OPTION_2':
					select = document.createElement('select');
					select.style = 'width:130px';
					select.id = 'selectScorebugstatstwo';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'yellow_home';
					option.text = 'Yellow Home +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'yellow_away';
					option.text = 'yellow Away +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'red_home';
					option.text = 'Red Home +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'red_away';
					option.text = 'Red Away +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'corners_home';
					option.text = 'Corners Home +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'corners_away';
					option.text = 'Corners Away +1';
					select.appendChild(option);
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					break;
				
				case 'SCOREBUG_OPTION':
					switch ($('#selectedBroadcaster').val()){
						case 'VIZ_TRI_NATION': case 'SUPER_CUP': case 'CONTINENTAL':
							select = document.createElement('select');
							select.style = 'width:130px';
							select.id = 'selectScorebugstats';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'yellow';
							option.text = 'Yellow Card';
							select.appendChild(option);
		
							option = document.createElement('option');
							option.value = 'red';
							option.text = 'Red Card';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'off_side';
							option.text = 'Offside';
							select.appendChild(option);
			
							option = document.createElement('option');
							option.value = 'shots';
							option.text = 'Shots';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'shots_on_target';
							option.text = 'Shots on Target';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'possession';
							option.text = 'Possession';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'corners';
							option.text = 'Corners';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'tackles';
							option.text = 'Tackles';
							select.appendChild(option);
							
							select.setAttribute('onchange',"processUserSelection(this)");
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							
							select = document.createElement('input');
							select.type = "text";
							select.id = 'selecthomedata';
							select.value = '';
							
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							
							select = document.createElement('input');
							select.type = "text";
							select.id = 'selectawaydata';
							select.value = '';
							
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							break;
						case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': 
							select = document.createElement('select');
							select.style = 'width:130px';
							select.id = 'selectScorebugstats';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'yellow';
							option.text = 'Yellow Card';
							select.appendChild(option);
		
							option = document.createElement('option');
							option.value = 'red';
							option.text = 'Red Card';
							select.appendChild(option);
							
							/*option = document.createElement('option');
							option.value = 'off_side';
							option.text = 'Offside';
							select.appendChild(option);
			
							option = document.createElement('option');
							option.value = 'shots';
							option.text = 'Shots';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'shots_on_target';
							option.text = 'Shots on Target';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'possession';
							option.text = 'Possession';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'corners';
							option.text = 'Corners';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'tackles';
							option.text = 'Tackles';
							select.appendChild(option);*/
							
							select.setAttribute('onchange',"processUserSelection(this)");
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							break;
					}
					
					break;
				}
			
			option = document.createElement('input');
		    option.type = 'button';
			switch (whatToProcess) {
			case 'RED_CARD_OPTION':
				option.name = 'populate_red_card_btn';
		    	option.value = 'Populate Red Card';
		    	break;
			case 'EXTRA-TIME_OPTION':
				option.name = 'populate_extra_time_btn';
		    	option.value = 'Populate Extra Time';
				break;
			case 'EXTRA-TIME-BOTH_OPTION':
				option.name = 'populate_extra_time_both_btn';
		    	option.value = 'Populate Extra Time Both';
				break;
			case 'SCOREBUG_OPTION_2':
				option.name = 'populate_stats_two_btn';
			    option.value = 'Populate Stats';
				break;
			case 'SCOREBUG_OPTION':
			    option.name = 'populate_stats_btn';
			    option.value = 'Populate Stats';
				break;
			}
		    option.id = option.name;
		    option.setAttribute('onclick',"processUserSelection(this)");
		    
		    div = document.createElement('div');
		    div.append(option);

			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_graphics_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');
	
		    div.append(option);
		    
		    row.insertCell(cellCount).appendChild(div);
		    cellCount = cellCount + 1;
		    
			document.getElementById('select_graphic_options_div').style.display = '';

			break;
		}
		break;
	
	case 'LOAD_OVERWRITE_TEAMS_SCORE':

		$('#select_event_div').empty();

		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		max_cols = 1;
		for(var i=0; i<=max_cols; i++) {
			
		    option = document.createElement('input');
		    option.type = 'text';
		    header_text = document.createElement('label');

			switch (whatToProcess) {
			case 'LOAD_OVERWRITE_TEAMS_SCORE':
				switch(i) {
				case 0:
					header_text.innerHTML = match_data.homeTeam.teamName4 + ' Score';
					option.id = 'overwrite_home_team_score';
					option.value = match_data.homeTeamScore;
					break;
				case 1:
					header_text.innerHTML = match_data.awayTeam.teamName4 + ' Score';
					option.id = 'overwrite_away_team_score';
					option.value = match_data.awayTeamScore;
					break;
				}
				break;
			}
			
			header_text.htmlFor = option.id;
			row.insertCell(i).appendChild(header_text).appendChild(option);
		}

	    option = document.createElement('input');
	    option.type = 'button';
		switch (whatToProcess) {
		case 'LOAD_OVERWRITE_TEAMS_SCORE':
		    option.name = 'log_teams_score_overwrite_btn';
		    option.value = 'Log Team Score Overwrite';
			break;
		}
	    option.id = option.name;
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div = document.createElement('div');
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_overwrite_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);
	    
	    max_cols = max_cols + 1;
	    row.insertCell(max_cols).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		
		break;
	
	case 'LOAD_OVERWRITE_MATCH_SUB':
		$('#select_event_div').empty();

		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_sub_index';
		select.name = select.id;
		select.setAttribute('onchange',"processUserSelection(this)");
		if(match_data.events != null && match_data.events.length > 0){
			for(var i = 0; i < match_data.events.length; i++) {
				if(match_data.events[(match_data.events.length - 1) - i].eventType == 'replace') {
					option = document.createElement('option');
					option.value = match_data.events[(match_data.events.length - 1) - i].eventNumber;
				    option.text = match_data.events[(match_data.events.length - 1) - i].onPlayerId + ' - ' + match_data.events[(match_data.events.length - 1) - i].eventType;
				    select.appendChild(option);
				}
			}
		}
		header_text = document.createElement('label');
		header_text.innerHTML = 'SUBS';
		header_text.htmlFor = select.id;
		row.insertCell(0).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_player_id';
		
		option = document.createElement('option');
		option.value = '0';
		option.text = '';
		select.appendChild(option);
		
		match_data.homeSquad.forEach(function(hp,index,arr){
			option = document.createElement('option');
			option.value = hp.playerId;
		    option.text = hp.jersey_number + ' - ' + hp.full_name + ' ('+ match_data.homeTeam.teamName4 +')';
		    select.appendChild(option);
		});
		match_data.awaySquad.forEach(function(as,index,arr){
			option = document.createElement('option');
			option.value = as.playerId;
		    option.text = as.jersey_number + ' - ' + as.full_name + ' ('+ match_data.awayTeam.teamName4 +')';
		    select.appendChild(option);
		});
		
	    header_text = document.createElement('label');
		header_text.innerHTML = 'Player';
		header_text.htmlFor = select.id;
		row.insertCell(1).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_subs_player_id';
		
		option = document.createElement('option');
		option.value = '0';
		option.text = '';
		select.appendChild(option);
		
		match_data.homeSubstitutes.forEach(function(hsub,index,arr){
			option = document.createElement('option');
			option.value = hsub.playerId;
		    option.text = hsub.jersey_number + ' - ' + hsub.full_name + ' ('+ match_data.homeTeam.teamName4 +') - Sub';
		    select.appendChild(option);
		});
		match_data.awaySubstitutes.forEach(function(asub,index,arr){
			option = document.createElement('option');
			option.value = asub.playerId;
		    option.text = asub.jersey_number + ' - ' + asub.full_name + ' ('+ match_data.awayTeam.teamName4 +') - Sub';
		    select.appendChild(option);
		});
		
	    header_text = document.createElement('label');
		header_text.innerHTML = 'Sub Player';
		header_text.htmlFor = select.id;
		row.insertCell(2).appendChild(header_text).appendChild(select);
		
		option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'log_match_subs_overwrite_btn';
	    option.value = 'Log Match Subs Overwrite';
	    option.id = option.name;
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div = document.createElement('div');
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_overwrite_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);
	    
	    row.insertCell(3).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		break;
		
	case 'LOAD_OVERWRITE_MATCH_STATS':

		$('#select_event_div').empty();

		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);

		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_stats_index';
		select.name = select.id;
		select.setAttribute('onchange',"processUserSelection(this)");
		
		match_data.matchStats.forEach(function(ms,index,arr){
			option = document.createElement('option');
			option.value = ms.statsId;
		    option.text = ms.stats_type;
			if(ms.player.full_name) {
			    option.text = option.text + ' (' + ms.player.full_name + ')';
			}
		    option.text = option.text + ' [' + ms.totalMatchSeconds + ']';
		    select.appendChild(option);
		});
	    header_text = document.createElement('label');
		header_text.innerHTML = 'Stats';
		header_text.htmlFor = select.id;
		row.insertCell(0).appendChild(header_text).appendChild(select);

		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_stats_player_id';

		match_data.homeSquad.forEach(function(hp,index,arr){
			option = document.createElement('option');
			option.value = hp.playerId;
		    option.text = hp.jersey_number + ' - ' + hp.full_name + ' ('+ match_data.homeTeam.teamName4 +')';
		    select.appendChild(option);
		});
		match_data.homeSubstitutes.forEach(function(hsub,index,arr){
			option = document.createElement('option');
			option.value = hsub.playerId;
		    option.text = hsub.jersey_number + ' - ' + hsub.full_name + ' ('+ match_data.homeTeam.teamName4 +') - Sub';
		    select.appendChild(option);
		});
		match_data.awaySquad.forEach(function(as,index,arr){
			option = document.createElement('option');
			option.value = as.playerId;
		    option.text = as.jersey_number + ' - ' + as.full_name + ' ('+ match_data.awayTeam.teamName4 +')';
		    select.appendChild(option);
		});
		match_data.awaySubstitutes.forEach(function(asub,index,arr){
			option = document.createElement('option');
			option.value = asub.playerId;
		    option.text = asub.jersey_number + ' - ' + asub.full_name + ' ('+ match_data.awayTeam.teamName4 +') - Sub';
		    select.appendChild(option);
		});
		

	    header_text = document.createElement('label');
		header_text.innerHTML = 'Player';
		header_text.htmlFor = select.id;
		row.insertCell(1).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_stats_type';
	    
	    option = document.createElement('option');
		option.value = 'goal';
	    option.text = 'Goal';
	    select.appendChild(option);
	    
	    option = document.createElement('option');
		option.value = 'own_goal';
	    option.text = 'Own Goal';
	    select.appendChild(option);
		    
		option = document.createElement('option');
		option.value = 'penalty';
	    option.text = 'Penalty';
	    select.appendChild(option);
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Type';
		header_text.htmlFor = option.id;
		row.insertCell(2).appendChild(header_text).appendChild(select);
		
		match_data.matchStats.forEach(function(ms,index,arr){
			option = document.createElement('input');
			option.type = "text";
			option.id = 'overwrite_match_stats_total_seconds';
			option.value = ms.totalMatchSeconds;
		});
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Time';
		header_text.htmlFor = option.id;
		row.insertCell(3).appendChild(header_text).appendChild(option);

	    option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'log_match_stats_overwrite_btn';
	    option.value = 'Log Match Stats Overwrite';
	    option.id = option.name;
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div = document.createElement('div');
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_overwrite_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);
	    
	    row.insertCell(4).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		
		break;		
		
	case 'LOAD_TEAMS':

		//var otherSquadWithoutSubs, player_ids;
		
		$('#team_selection_div').empty();
		document.getElementById('team_selection_div').style.display = 'none';
		
		if (dataToProcess)
		{
			if(dataToProcess.homeSquad.length <=0 || dataToProcess.awaySquad.length <=0) {
				if(dataToProcess.homeSquad.length <=0) {
					alert(dataToProcess.homeTeam.teamName1 + ' has no players in the database');
				} else if(dataToProcess.awaySquad.length <=0) {
					alert(dataToProcess.awayTeam.teamName1 + ' has no players in the database');
				}
				return false;
			}
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			table.setAttribute('id', 'setup_teams');
			tr = document.createElement('tr');
			for (var j = 0; j <= 3; j++) {
			    th = document.createElement('th'); //column
			    switch (j) {
				case 0:
				    text = document.createTextNode(dataToProcess.homeTeam.teamName1); 
					break;
				case 1:
				    text = document.createTextNode(dataToProcess.homeTeam.teamName4 + ' captain/keeper'); 
					break;
				case 2:
				    text = document.createTextNode(dataToProcess.awayTeam.teamName1); 
					break;
				case 3:
				    text = document.createTextNode(dataToProcess.awayTeam.teamName4 + ' captain/keeper'); 
					break;
				}
			    th.appendChild(text);
			    tr.appendChild(th);
			}
			
			thead = document.createElement('thead');
			thead.appendChild(tr);
			table.appendChild(thead);

			tbody = document.createElement('tbody');
			max_cols = parseInt(10 + parseInt($('#homeSubstitutesPerTeam option:selected').val()));
			if(parseInt($('#homeSubstitutesPerTeam option:selected').val()) < parseInt($('#awaySubstitutesPerTeam option:selected').val())) {
				max_cols = parseInt(10 + parseInt($('#awaySubstitutesPerTeam option:selected').val()));
			}

			for(var i=0; i <= max_cols; i++) {
				row = tbody.insertRow(tbody.rows.length);
				for(var j=0; j<=3; j++) {
					addSelect = false;
					switch(j) {
					case 0: case 1:
						if(i <= parseInt(10 + parseInt($('#homeSubstitutesPerTeam option:selected').val()))) {
							addSelect = true;
						}
						break;
					case 2: case 3:
						if(i <= parseInt(10 + parseInt($('#awaySubstitutesPerTeam option:selected').val()))) {
							addSelect = true;
						}
						break;
					}

					if(addSelect == true) {
						select = document.createElement('select');
						select.style = 'width:75%';
						switch(j) {
						case 0: case 2:
							if(j==0) {
								select.name = 'selectHomePlayers';
								select.id = 'homePlayer_' + (i + 1);
							} else if(j==2) {
								select.name = 'selectAwayPlayers';
								select.id = 'awayPlayer_' + (i + 1);
							}
							if(j==0) {
								dataToProcess.homeSquad.forEach(function(hp,index,arr){
									option = document.createElement('option');
									option.value = hp.playerId;
								    option.text = hp.jersey_number + ' - ' + hp.full_name;
								    select.appendChild(option);
								});
								dataToProcess.homeSubstitutes.forEach(function(hp,index,arr){
									option = document.createElement('option');
									option.value = hp.playerId;
								    option.text = hp.jersey_number + ' - ' + hp.full_name;
								    select.appendChild(option);
								});
								dataToProcess.homeOtherSquad.forEach(function(hs,index,arr){
									option = document.createElement('option');
									option.value = hs.playerId;
								    option.text = hs.jersey_number + ' - ' + hs.full_name;
								    select.appendChild(option);
								});
								
							} else if (j==2) {
								
								dataToProcess.awaySquad.forEach(function(ap,index,arr){
									option = document.createElement('option');
									option.value = ap.playerId;
								    option.text = ap.jersey_number + ' - ' + ap.full_name;
								    select.appendChild(option);
								});
								dataToProcess.awaySubstitutes.forEach(function(ap,index,arr){
									option = document.createElement('option');
									option.value = ap.playerId;
								    option.text = ap.jersey_number + ' - ' + ap.full_name;
								    select.appendChild(option);
								});
								dataToProcess.awayOtherSquad.forEach(function(as,index,arr){
									option = document.createElement('option');
									option.value = as.playerId;
								    option.text = as.jersey_number + ' - ' + as.full_name;
								    select.appendChild(option);
								});
							}
						    select.selectedIndex = i;
							break;
						
						case 1: case 3:
						
							if(j==1) {
								select.name = 'selectHomeCaptainGoalKeeper';
								select.id = 'homeCaptainGoalKeeper_' + (i + 1);
							} else {
								select.name = 'selectAwayCaptainGoalKeeper';
								select.id = 'awayCaptainGoalKeeper_' + (i + 1);
							}
							for(var k=0; k<=3; k++) {
								option = document.createElement('option');
								switch (k) {
								case 0:
									option.value = '';
								    option.text = '';
									break;
								case 1:
									option.value = 'captain';
								    option.text = 'Captain';
									break;
								case 2:
									option.value = 'goal_keeper';
								    option.text = 'Goal Keeper';
									break;
								case 3:
									option.value = 'captain_goal_keeper';
								    option.text = 'Captain And Goal Keeper';
									break;
								}
							    select.appendChild(option);
							}
							if(i <= 10) {
								switch(j) {
								case 1: 
									select.value = dataToProcess.homeSquad[i].captainGoalKeeper;
									break;
								case 3:
									select.value = dataToProcess.awaySquad[i].captainGoalKeeper;
									break;
								}
							}
							if(i > 10 && (i-11) <= dataToProcess.homeSubstitutes.length -1){
								switch(j) {
								case 1:
									select.value = dataToProcess.homeSubstitutes[i-11].captainGoalKeeper;
									break;
								}
							}
							if(i > 10 && (i-11) <= dataToProcess.awaySubstitutes.length -1){
								switch(j) {
								case 3:
									select.value = dataToProcess.awaySubstitutes[i-11].captainGoalKeeper;
									break;
								}
							}
							break;
						}
						row.insertCell(j).appendChild(select);
						removeSelectDuplicates(select.id);
						$(select).select2();
					} else {
						select = document.createElement('label');
						row.insertCell(j).appendChild(select);
					}
				}
			}
		
			table.appendChild(tbody);
			document.getElementById('team_selection_div').appendChild(table);
			document.getElementById('team_selection_div').style.display = '';
		} 
		break;
		
	case 'POPULATE-OFF_PLAYER':
		
		$('#select_player').empty();
		
		if(dataToProcess.homeTeamId ==  $('#select_teams option:selected').val()){
			dataToProcess.homeSquad.forEach(function(hs,index,arr){
				$('#select_player').append(
					$(document.createElement('option')).prop({
	                value: hs.playerId,
	                text: hs.jersey_number + ' - ' + hs.full_name
		        }))					
			});
		}
		else {
			dataToProcess.awaySquad.forEach(function(as,index,arr){
				$('#select_player').append(
					$(document.createElement('option')).prop({
	                value: as.playerId,
	                text: as.jersey_number + ' - ' + as.full_name
		        }))					
			});
		}
		break;
		
	case 'POPULATE-ON_PLAYER':
		
		$('#select_sub_player').empty();
		if(dataToProcess.homeTeamId ==  $('#select_teams option:selected').val()){
			dataToProcess.homeSubstitutes.forEach(function(hsub,index,arr){
				$('#select_sub_player').append(
					$(document.createElement('option')).prop({
	                value: hsub.playerId,
	                text: hsub.jersey_number + ' - ' + hsub.full_name
		        }))					
			});
		}
		else {
			dataToProcess.awaySubstitutes.forEach(function(asub,index,arr){
				$('#select_sub_player').append(
					$(document.createElement('option')).prop({
	                value: asub.playerId,
	                text: asub.jersey_number + ' - ' + asub.full_name
		        }))					
			});
		}
		break;

	case 'LOAD_PENALTY':
		
		$('#select_event_div').empty();
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);

		for(var i=1; i<=2; i++) {
			div = document.createElement('div');
			div.style = 'text-align:center;';
			switch(i){
			case 1:
				text = 'home';
				break;
			case 2:
				text = 'away';
				break;
			}
			div.id = text + '_penalties_div';
			for(var j=0; j<=5; j++) {
				switch(j){
				case 0: case 3:
					header_text = document.createElement('label');
					header_text.htmlFor = div.id;
					option = document.createElement('input');
					option.type = "button";
					option.style = 'text-align:center;';
					option.setAttribute('onclick','processUserSelection(this)');
					switch(j){
					case 0:
						header_text.innerHTML = text.toUpperCase() + ' Hits: ';
						option.id = text + '_increment_penalties_hit_btn';
						break;
					case 3:
						header_text.innerHTML = 'Misses: ';
						option.id = text + '_increment_penalties_miss_btn';
						break;
					}
					option.value = "+";
					div.appendChild(header_text).appendChild(option);
					break;
				case 1: case 4:
	    			option = document.createElement('input');
					option.type = 'text';
					switch(j){
					case 1:
						option.id = text + '_penalties_hit_txt';
						break;
					case 4:
						option.id = text + '_penalties_miss_txt';
						break;
					}
					option.value = '0';
					option.style = 'width:10%;text-align:center;';
					div.appendChild(option);
					break;
				case 2: case 5:
					option = document.createElement('input');
					option.type = "button";
					option.style = 'text-align:center;';
					option.setAttribute('onclick','processUserSelection(this)');
					switch(j){
					case 2:
						option.id = text + '_decrement_penalties_hit_btn';
						break;
					case 5:
						option.id = text + '_decrement_penalties_miss_btn';
						break;
					}
					option.value = "-";
					div.appendChild(option);
				    div.append(document.createElement('br'));
					break;
				}
			}
			row.insertCell(i-1).appendChild(div);
		}

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_penalty_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.appendChild(option);

		row.insertCell(2).appendChild(div);
		
		table.appendChild(tbody);
		
		document.getElementById('select_event_div').appendChild(table);
		
		break;
				
	case 'LOAD_REPLACE':
		
		$('#select_event_div').empty();
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		select = document.createElement('select');
		select.id = 'select_teams';
		select.name = select.id;
		
		option = document.createElement('option');
		option.value = dataToProcess.homeTeamId;
		option.text = dataToProcess.homeTeam.teamName1;
		select.appendChild(option);
		
		option = document.createElement('option');
		option.value = dataToProcess.awayTeamId;
		option.text = dataToProcess.awayTeam.teamName1;
		select.appendChild(option);
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Teams: '
		header_text.htmlFor = select.id;
		select.setAttribute('onchange',"processUserSelection(this)");
		row.insertCell(0).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.id = 'select_player';
		select.name = select.id;
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Player: '
		header_text.htmlFor = select.id;
		row.insertCell(1).appendChild(header_text).appendChild(select);

	    select = document.createElement('select');
		select.id = 'select_sub_player';
		select.name = select.id;
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Sub-Player: '
		header_text.htmlFor = select.id;
		row.insertCell(2).appendChild(header_text).appendChild(select);
		
	    div = document.createElement('div');

	    option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'log_replace_btn';
	    option.id = option.name;
	    option.value = 'Replace Player';
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_replace_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);

	    row.insertCell(3).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		
		break;
		
	case 'LOAD_UNDO':

		$('#select_event_div').empty();
		
		if(dataToProcess.events.length > 0) {

			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
			row = tbody.insertRow(tbody.rows.length);
			
			select = document.createElement('select');
			select.id = 'select_undo';
			dataToProcess.events = dataToProcess.events.reverse();
			var max_loop = dataToProcess.events.length;
			if(max_loop > 5) {
				max_loop = 5;
			}
			for(var i = 0; i < max_loop; i++) {
				option = document.createElement('option');
				option.value = dataToProcess.events[i].eventNumber;
			    option.text = dataToProcess.events[i].eventNumber + '. ' + dataToProcess.events[i].eventType;
			    select.appendChild(option);
			}
			header_text = document.createElement('label');
			header_text.innerHTML = 'Last Five Events: '
			header_text.htmlFor = select.id;
			row.insertCell(0).appendChild(header_text).appendChild(select);

		    option = document.createElement('input');
		    option.type = 'text';
		    option.name = 'number_of_undo_txt';
		    option.value = '1';
		    option.id = option.name;
		    option.setAttribute('onblur','processUserSelection(this)');
			header_text = document.createElement('label');
			header_text.innerHTML = 'Number of undos: '
			header_text.htmlFor = option.id;
			row.insertCell(1).appendChild(header_text).appendChild(option);
			
		    div = document.createElement('div');

		    option = document.createElement('input');
		    option.type = 'button';
		    option.name = 'log_undo_btn';
		    option.id = option.name;
		    option.value = 'Undo Last Event';
		    option.setAttribute('onclick','processUserSelection(this);');
		    
		    div.append(option);

			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_undo_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');

		    div.append(document.createElement('br'));
		    div.append(option);

		    row.insertCell(2).appendChild(div);

			table.appendChild(tbody);
			document.getElementById('select_event_div').appendChild(table);

		} else {
			return false;
		}
		
		break;
	
	case 'LOAD_EVENTS':
		
		$('#select_event_div').empty();

		header_text = document.createElement('label');
		header_text.id = 'selected_player_name';
		header_text.innerHTML = '';
		document.getElementById('select_event_div').appendChild(header_text);
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		
		for(var iRow=0;iRow<=0;iRow++) {
			
			row = tbody.insertRow(tbody.rows.length);
			max_cols = 5;
			
			for(var iCol=0;iCol<=max_cols;iCol++) {
				
				cell = row.insertCell(iCol);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'log_event_btn';
				
				switch (iRow) {
				case 0:
					
					switch (iCol) {
					case 0:
						option.id = 'goal';
						option.value = 'Goal';
						break;
					/*case 1:
						option.id = 'card';
						option.value = 'Card';
						break;*/
					case 1:
						option.id = 'replace';
						option.value = 'Replace';
						break;
					case 2:
						option.id = 'undo';
						option.value = 'Undo';
						break;
					case 3:
						option.id = 'overwrite';
						option.value = 'Overwrite';
						break;
					case 4:
						option.id = 'penalty';
						option.value = 'Penalty';
						break;
					case 5:
						option.name = 'cancel_event_btn';
						option.id = option.name;
						option.value = 'Cancel';
					}
					
					break;
					
				}
				
				if(option.id) {
					
					switch (option.id) {
					case 'overwrite': case 'goal': case 'card': case 'stats':
						
						option.setAttribute('data-toggle', 'dropdown');
						option.setAttribute('aria-haspopup', 'true');
						option.setAttribute('aria-expanded', 'false');					
						
						div = document.createElement('div');
					    div.append(option);
					    div.className='dropdown';
					    
					    linkDiv = document.createElement('div');
					    linkDiv.id = option.id + '_div';
					    linkDiv.className='dropdown-menu';
					    linkDiv.setAttribute('aria-labelledby',option.id);

						switch (option.id) {
						case 'stats':
					
							for(var ibound=1; ibound<=8; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
								switch(ibound) {
								case 1:
								    anchor.id = 'off_side';
								    anchor.innerText = 'Off Side';
									break;
								case 2:
								    anchor.id = 'assists';
								    anchor.innerText = 'Assists';
									break;
								case 3:
								    anchor.id = 'shots';
								    anchor.innerText = 'Shots';
									break;
								case 4:
								    anchor.id = 'shots_on_target';
								    anchor.innerText = 'Shots On Target';
									break;
								case 5:
								    anchor.id = 'fouls';
								    anchor.innerText = 'Fouls';
									break;
								case 6:
								    anchor.id = 'corners';
								    anchor.innerText = 'Corners';
									break;
								case 7:
								    anchor.id = 'corners_converted';
								    anchor.innerText = 'Corners Converted';
									break;
								}
								switch(ibound){
									case 1: case 2: case 3: case 4: case 5: case 6: case 7:
										 anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processHockeyProcedures("LOG_EVENT",this);');
										break;
								}
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'overwrite': 
							
							for(var ibound=1; ibound<=3; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
							    switch(ibound) {
								case 1:
								    anchor.id = 'overwrite_team_score';
								    anchor.innerText = 'Team Score';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_TEAMS_SCORE",this);');
									break;
								case 2:
								    anchor.id = 'overwrite_match_stats';
								    anchor.innerText = 'Match Stats';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_MATCH_STATS",this);');
									break;
								case 3:
								    anchor.id = 'overwrite_match_substitute';
								    anchor.innerText = 'Match Subs';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_MATCH_SUB",this);');
									break;
								}
							    
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'goal':
						
							for(var ibound=1; ibound<=3; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
							    if(ibound == 1) {
								    anchor.id = 'goal';
								    anchor.innerText = 'Goal';
							    } else if(ibound == 2) {
								    anchor.id = 'own_goal';
								    anchor.innerText = 'Own goal';
							    }else{
									anchor.id = 'penalty';
								    anchor.innerText = 'Penalty';
								}
							    anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processHockeyProcedures("LOG_EVENT",this);');
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'card':
							
							for(var ibound=1; ibound<=2; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
								
								if(ibound == 1) {
								    anchor.id = 'yellow';
								    anchor.innerText = 'Yellow Card';
							    } else if(ibound == 2) {
								    anchor.id = 'red';
								    anchor.innerText = 'Red Card';
							    }
							    
							    anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processHockeyProcedures("LOG_EVENT",this);');
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
						}
					    div.append(linkDiv);				    
						cell.append(div);
						break;
						
					default:
					
						option.onclick = function() {processUserSelection(this)};
						cell.appendChild(option);
						
						break;
					
					}
				}
			}
		}
			
		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);

		break;
				
	case 'LOAD_MATCH':
		
		$('#hockey_div').empty();
		
		if (dataToProcess)
		{
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			tbody = document.createElement('tbody');

			table.appendChild(tbody);
			document.getElementById('hockey_div').appendChild(table);

			row = tbody.insertRow(tbody.rows.length);
			header_text = document.createElement('h6');
			header_text.id = 'match_time_hdr';
			header_text.innerHTML = 'Match Time: 00:00:00';
			row.insertCell(0).appendChild(header_text);
			
			if(dataToProcess.events != null && dataToProcess.events.length > 0) {
				max_cols = dataToProcess.events.length;
				if (max_cols > 20) {
					max_cols = 20;
				}
				header_text = document.createElement('h6');
				for(var i = 0; i < max_cols; i++) {
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId != 0){
						dataToProcess.homeSquad.forEach(function(hs,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == hs.playerId){
								playerName = ' {'+ hs.ticker_name +'}' ;
							}				
						});
						dataToProcess.awaySquad.forEach(function(as,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == as.playerId){
								playerName = ' {'+ as.ticker_name +'}';
							}				
						});
						dataToProcess.homeSubstitutes.forEach(function(hsub,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == hsub.playerId){
								playerName = ' {'+ hsub.ticker_name +'}';
							}		
						});
						dataToProcess.awaySubstitutes.forEach(function(asub,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == asub.playerId){
								playerName = ' {'+ asub.ticker_name +'}';
							}			
						});
					}else{
						playerName = '';
					}
					
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType) {
						if(header_text.innerHTML) {
							header_text.innerHTML = header_text.innerHTML + ', ' + dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType.replaceAll('_',' ') + playerName; // .match(/\b(\w)/g).join('')
						} else {
							header_text.innerHTML = dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType.replaceAll('_',' ') + playerName; // .match(/\b(\w)/g).join('')
						}
					}
				}
				header_text.innerHTML = 'Events: ' + header_text.innerHTML;
				row.insertCell(1).appendChild(header_text);
			}

			//Teams Score and other details
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			thead = document.createElement('thead');
			tr = document.createElement('tr');
			for (var j = 0; j <= 1; j++) {
			    th = document.createElement('th'); // Column
				th.scope = 'col';
			    switch (j) {
				case 0:
				    th.innerHTML = dataToProcess.homeTeam.teamName1 + ': ' + dataToProcess.homeTeamScore ;
					break;
				case 1:
					th.innerHTML = dataToProcess.awayTeam.teamName1 + ': ' + dataToProcess.awayTeamScore ;
					break;
				}
			    tr.appendChild(th);
			}
			thead.appendChild(tr);
			table.appendChild(thead);
			document.getElementById('hockey_div').appendChild(table);
			
			tbody = document.createElement('tbody');
			for(var i = 0; i <= dataToProcess.homeSquad.length - 1; i++) {
				row = tbody.insertRow(tbody.rows.length);
				for(var j = 1; j <= 2; j++) {
					anchor = document.createElement('a');
					switch(j){
					case 1:
						anchor.name = 'homePlayers';
						anchor.id = 'homePlayer_' + dataToProcess.homeSquad[i].playerId;
						anchor.value = dataToProcess.homeSquad[i].playerId;
						if(getPlayerMatchStats(dataToProcess.homeSquad[i].playerId) == ''){
							anchor.innerHTML = dataToProcess.homeSquad[i].jersey_number + ': ' + dataToProcess.homeSquad[i].full_name ;
						}else{
							anchor.innerHTML = dataToProcess.homeSquad[i].jersey_number + ': ' + dataToProcess.homeSquad[i].full_name 
								+ '  ['+ getPlayerMatchStats(dataToProcess.homeSquad[i].playerId) + ']';
						}
						break;
					case 2:
						anchor.name = 'awayPlayers';
						anchor.id = 'awayPlayer_' + dataToProcess.awaySquad[i].playerId;
						anchor.value = dataToProcess.awaySquad[i].playerId;
						if(getPlayerMatchStats(dataToProcess.awaySquad[i].playerId) == ''){
							anchor.innerHTML = dataToProcess.awaySquad[i].jersey_number + ': ' + dataToProcess.awaySquad[i].full_name ;
						}else{
							anchor.innerHTML = dataToProcess.awaySquad[i].jersey_number + ': ' + dataToProcess.awaySquad[i].full_name 
								+ '  ['+ getPlayerMatchStats(dataToProcess.awaySquad[i].playerId) + ']';
						}
						break;
					}
					anchor.setAttribute('onclick','processUserSelection(this);');
					anchor.setAttribute('style','cursor: pointer;');
					row.insertCell(j - 1).appendChild(anchor);
				}
			}				
			row = tbody.insertRow(tbody.rows.length);
			header_text = document.createElement('header');
			header_text.innerHTML = 'Substitutes';
			row.insertCell(0).appendChild(header_text);
			
			max_cols = dataToProcess.homeSubstitutes.length;
			if(dataToProcess.homeSubstitutes.length < dataToProcess.awaySubstitutes.length) {
				max_cols = dataToProcess.awaySubstitutes.length;
			}
			
			for(var i = 0; i <= max_cols-1; i++) {
				
				row = tbody.insertRow(tbody.rows.length);
				
				for(var j = 1; j <= 2; j++) {
					
					addSelect = false;
					
					switch(j) {
					case 0: case 1:
						if(i <= parseInt(dataToProcess.homeSubstitutes.length - 1)) {
							addSelect = true;
						}
						break;
					case 2: case 3:
						if(i <= parseInt(dataToProcess.awaySubstitutes.length - 1)) {
							addSelect = true;
						}
						break;
					}

					text = document.createElement('label');
					
					if(addSelect == true) {
					
						switch(j){
						case 1:
							
							text.name = 'homeSubstitutes';
							text.id = 'homeSubstitute_' + dataToProcess.homeSubstitutes[i].playerId;
							text.value = dataToProcess.homeSubstitutes[i].playerId;
							if(getPlayerMatchStats(dataToProcess.homeSubstitutes[i].playerId) == ''){
								text.innerHTML = dataToProcess.homeSubstitutes[i].jersey_number + ': ' + dataToProcess.homeSubstitutes[i].full_name;
							}else{
								text.innerHTML = dataToProcess.homeSubstitutes[i].jersey_number + ': ' + dataToProcess.homeSubstitutes[i].full_name 
									+ '  ['+ getPlayerMatchStats(dataToProcess.homeSubstitutes[i].playerId) + ']';
							}
							break;
							
						case 2:
							
							text.name = 'awaySubstitutes';
							text.id = 'awaySubstitute_' + dataToProcess.awaySubstitutes[i].playerId;
							text.value = dataToProcess.awaySubstitutes[i].playerId;
							if(getPlayerMatchStats(dataToProcess.awaySubstitutes[i].playerId) == '') {
								text.innerHTML = dataToProcess.awaySubstitutes[i].jersey_number + ': ' + dataToProcess.awaySubstitutes[i].full_name;
							}else{
								text.innerHTML = dataToProcess.awaySubstitutes[i].jersey_number + ': ' + dataToProcess.awaySubstitutes[i].full_name 
									+ '  ['+ getPlayerMatchStats(dataToProcess.awaySubstitutes[i].playerId) + ']';
							}
							break;
							
						}
					
						text.setAttribute('style','cursor: pointer;');
					
					}	
				
					row.insertCell(j - 1).appendChild(text);
				
				}
			}				

			table.appendChild(tbody);
			document.getElementById('hockey_div').appendChild(table);
			
		}
		break;
	}
}
function removeSelectDuplicates(select_id)
{
	var this_list = {};
	$("select[id='" + select_id + "'] > option").each(function () {
	    if(this_list[this.text]) {
	        $(this).remove();
	    } else {
	        this_list[this.text] = this.value;
	    }
	});
}
function checkEmpty(inputBox,textToShow) {

	var name = $(inputBox).attr('id');
	
	document.getElementById(name + '-validation').innerHTML = '';
	document.getElementById(name + '-validation').style.display = 'none';
	$(inputBox).css('border','');
	if(document.getElementById(name).value.trim() == '') {
		$(inputBox).css('border','#E11E26 2px solid');
		document.getElementById(name + '-validation').innerHTML = textToShow + ' required';
		document.getElementById(name + '-validation').style.display = '';
		document.getElementById(name).focus({preventScroll:false});
		return false;
	}
	return true;	
}	
