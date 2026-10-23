package com.hockey.broadcaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import com.hockey.containers.Scene;
import com.hockey.containers.ScoreBug;
import com.hockey.controller.IndexController;
import com.hockey.model.*;
import com.hockey.service.HockeyService;
import com.hockey.util.HockeyFunctions;
import com.hockey.util.HockeyUtil;
import com.opencsv.exceptions.CsvException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HOCKEY_KHELO_INDIA extends Scene{
	
	public String session_selected_broadcaster = "HOCKEY_KHELO_INDIA";
	
	@JsonIgnore
	public PrintWriter print_writer;
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "",which_stats_on_screen = "",sponsor_on_screen = "",red_card_on_screen = "";
	public boolean is_infobar = false;
	private String logo_path = "IMAGE*/Default/Essentials/Logos/";
	//private String colors_path = "C:\\Images\\KHELO_INDIA\\Colours\\";
	private String icon_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_Khelo_India_2023\\Icons\\";
	public String status;
	private String slashOrDash = "-";
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	private static ObjectMapper objectMapper  = new ObjectMapper();
	public List<Fixture> fixTures;
	public Fixture fixture;
	
	public final static String HOCKEY_LOGO = "IMAGE*/Default/Essentials/Logos/";
	public final static String HOCKEY_BASE1 = "IMAGE*/Default/Essentials/Base1/";
	public final static String HOCKEY_BASE2 = "IMAGE*/Default/Essentials/Base2/";
	public final static String HOCKEY_TEXT1 = "IMAGE*/Default/Essentials/Text1/";
	public final static String HOCKEY_TEXT2 = "IMAGE*/Default/Essentials/Text2/";
	public final static String HOCKEY_ICONS = "IMAGE*/Default/Essentials/Icons/";
	public final static String HOCKEY_JERSEY = "IMAGE*/Default/Essentials/Jersey/";
	public final static String HOCKEY_SPONSOR = "IMAGE*/Default/Essentials/Spnsor/";
	public final static String HOCKEY_LOCAL_PHOTO_PATH = "C:\\\\Images\\\\Hockey\\\\Photos\\\\";
	public final static String HOCKEY_PHOTO_PATH = "\\c\\Images\\Hockey\\Photos\\";
	
	
	public HOCKEY_KHELO_INDIA() {
		super();
	}
	
	public ScoreBug updateScoreBug(List<Scene> scenes, Match match, Socket session_socket) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true,scorebug, session_socket, scenes.get(0).getScene_path(),match, session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(String whatToProcess,Match match,Clock clock, HockeyService hockeyService,Socket session_socket,
			List<Scene> scenes, String valueToProcess) throws InterruptedException, NumberFormatException, MalformedURLException, IOException, CsvException, JAXBException{
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		if (which_graphics_onscreen == "PENALTY")
		{
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			
			System.out.println("valueToProcess.split(\",\")[1] =================" + valueToProcess.split(",")[1]);
			/*if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				System.out.println("hello");
				penalties = new ArrayList<String>();
			}*/
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$Center$txt_Score"
					+ "*GEOM*TEXT SET "+ match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() +"\0");
			for(String pen : penalties)
			{	
				if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 2\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 1\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 2\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 1\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
					
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penaltiesremove = new ArrayList<String>();
//					penalties.remove(penalties.size() - 1);
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
//			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
//				penalties = new ArrayList<String>();
//				penaltiesremove = new ArrayList<String>();
//			}
//			if (match.getHomePenaltiesHits() + match.getHomePenaltiesMisses()
//			        + match.getAwayPenaltiesHits() + match.getAwayPenaltiesMisses() == 0) {
//			    penalties = new ArrayList<String>();
//			    penaltiesremove = new ArrayList<String>();
//			}
			if (match.getHomePenaltiesHits() + match.getHomePenaltiesMisses()
			        + match.getAwayPenaltiesHits() + match.getAwayPenaltiesMisses() == 0) {
			    penalties = new ArrayList<String>();
			    penaltiesremove = new ArrayList<String>();
			}
		} else {
			if(penalties == null) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
//			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
//				penalties = new ArrayList<String>();
//				penaltiesremove = new ArrayList<String>();
//			}
			if (match.getHomePenaltiesHits() + match.getHomePenaltiesMisses()
			        + match.getAwayPenaltiesHits() + match.getAwayPenaltiesMisses() == 0) {
			    penalties = new ArrayList<String>();
			    penaltiesremove = new ArrayList<String>();
			}
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				if(match.getHomePenaltiesHits() == match.getAwayPenaltiesHits()) {
					penalties = new ArrayList<String>();
				}
			}
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");

			for(String pen : penalties)
			{
				//System.out.println("ELSE LOOP - " + iHomeCont);
				if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 2\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 1\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 2\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 1\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");

//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
					
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");

//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
			
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
					
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG":
		case "POPULATE-L3-NAMESUPER": case "POPULATE-L3-NAMESUPER-PLAYER": case "POPULATE-FF-MATCHID": case "POPULATE-FF-PLAYINGXI": case "POPULATE-L3-BUG-DB":
		case "POPULATE-L3-SCOREUPDATE": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-L3-NAMESUPER-CARD": case "POPULATE-L3-SUBSTITUTE": case "POPULATE-LT-PROMO":
		case "POPULATE-L3-STAFF": case "POPULATE-FF-MATCHSTATS": case "POPULATE-FF-PROMO": case "POPULATE-DOUBLE_PROMO": case "POPULATE-POINTS_TABLE": 
		case "POPULATE-OFFICIALS": case "POPULATE-PENALTY": case "POPULATE-L3-SINGLE_SUBSTITUTE": case "POPULATE-SCOREBUG_STATS": case "POPULATE-SCOREBUG-CARD":
		case "POPULATE-SCOREBUG-GOAL_SCORER": case "POPULATE-LT-MATCHID": case "POPULATE-FF-PLAYINGXISUB": case "POPULATE-FF-MATCHSUMMARY": case "POPULATE-FF-PLAYINGXIBOTHTEAMS":
		case "POPULATE-FF-MATCHID_IMAGE":
		case "POPULATE-LAST_GOAL":	case "POPULATE-RED_CARD": case "POPULATE-QUARTER": case "POPULATE-LT-PENALTY_SCORES":
		case "POPULATE-FF_PROMO":	case "POPULATE-FF-MATCHSTAT": case "POPULATE-CHANGE_PENALTY":

//			switch(whatToProcess.toUpperCase()) {
//			//case "POPULATE-SCOREBUG_STATS":  case "POPULATE-EXTRA_TIME": case "POPULATE-SPONSOR": case "POPULATE-SCOREBUG_STATS_TWO":
//			//case "POPULATE-SCOREBUG-CARD":	case "POPULATE-SCOREBUG-SUBS": case "POPULATE-EXTRA_TIME_BOTH":
//				//break;
//			case "POPULATE-SCOREBUG":
//				scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
//				break;
//			default:
//				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
//				scenes.get(1).scene_load(session_socket,session_selected_broadcaster);
//				break;
//			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-CHANGE_PENALTY":
				populateResetPenalty(session_socket,match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-QUARTER":
				populateQuarters(false,session_socket,match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-RED_CARD":
				populateScoreBugRedCards(false,session_socket,Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-LAST_GOAL":	
				populateScoreBugLastGoal(false,session_socket,match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-SCOREBUG-GOAL_SCORER":
				populateScoreBugScorer(false,scorebug,session_socket, Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),match, session_selected_broadcaster);
				break;
			case "POPULATE-SCOREBUG-CARD":
				populateScoreBugCard(false,scorebug,session_socket, Integer.valueOf(valueToProcess.split(",")[1]),valueToProcess.split(",")[2],Integer.valueOf(valueToProcess.split(",")[3]),valueToProcess.split(",")[4],match, session_selected_broadcaster);
				break;
			case "POPULATE-SCOREBUG_STATS":
				populateScoreBugStats(false,scorebug,session_socket, valueToProcess.split(",")[1],valueToProcess.split(",")[2],valueToProcess.split(",")[3],match, session_selected_broadcaster);
				break;
			case "POPULATE-SCOREBUG":
				populateScoreBug(false,scorebug,session_socket, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			case "POPULATE-LT-MATCHID":
				populateLtMatchId(session_socket, match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-LT-PENALTY_SCORES":
				populatePenLtMatchId(session_socket, match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-L3-NAMESUPER":
				for(NameSuper ns : hockeyService.getNameSupers()) {
					  if(ns.getNamesuperId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateNameSuper(session_socket, valueToProcess.split(",")[1], ns, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-NAMESUPER-PLAYER":
				populateNameSuperPlayer(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), 
						valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]),valueToProcess.split(",")[5],valueToProcess.split(",")[6], match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-NAMESUPER-CARD":
				populateNameSuperCard(session_socket, Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHID":
				populateMatchId(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-MATCHID_IMAGE":
				populateMatchIdImage(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,1,hockeyService, hockeyService.getTeams());
				break;
			case "POPULATE-FF-MATCHSTAT":	
				populateMatchStatsFF(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF_PROMO":
				populateFFMatchPromo(session_socket,Integer.valueOf(valueToProcess.split(",")[1]), match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-MATCHSTATS":
				populateMatchStats(session_socket,valueToProcess.split(",")[1], hockeyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI":
				populatePlayingXI(session_socket, Integer.valueOf(valueToProcess.split(",")[1]),hockeyService.getFormations(), 
						hockeyService.getTeams(),match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-PLAYINGXIBOTHTEAMS":
				populatePlayingXIBOTH(session_socket,hockeyService.getFormations(), 
						hockeyService.getTeams(),match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-PLAYINGXISUB":
				populatePlayingXISUB(session_socket, Integer.valueOf(valueToProcess.split(",")[1]),hockeyService.getFormations(), 
						hockeyService.getTeams(),match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-L3-BUG-DB":
				for(Bugs bug : hockeyService.getBugs()) {
					  if(bug.getBugId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateBugsDB(session_socket, valueToProcess.split(",")[1], bug, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-SCOREUPDATE":
				populateScoreUpdate(session_socket, valueToProcess.split(",")[1], hockeyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHSUMMARY":
				populateScoreUpdateFF(session_socket, valueToProcess.split(",")[1], hockeyService,match,clock, session_selected_broadcaster,1);
				break;
			case "POPULATE-PENALTY":
				populateLtPenalty(session_socket, valueToProcess.split(",")[1],valueToProcess, hockeyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-L3-MATCHSTATUS":
				populateMatchStatus(session_socket, valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SUBSTITUTE":
				populateSubstitute(session_socket, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],
						hockeyService.getAllPlayer(),hockeyService.getTeams(), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SINGLE_SUBSTITUTE":
				populateSingleSubstitute(session_socket, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),
						hockeyService.getAllPlayer(),hockeyService.getTeams(), match, session_selected_broadcaster);
				break;
			case "POPULATE-LT-PROMO":
				populateMatchPromo(session_socket, valueToProcess.split(",")[2], match,hockeyService.getFixtures(),hockeyService.getTeams(),hockeyService.getGrounds(), session_selected_broadcaster);
				break;
			case "POPULATE-FF-PROMO":
				populateMatchPromoSingle(session_socket, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),hockeyService.getTeams(),
						hockeyService.getFixtures(),hockeyService.getGrounds(),match , session_selected_broadcaster);
				break;
			case "POPULATE-DOUBLE_PROMO":
				populateMatchDoublePromo(session_socket, valueToProcess.split(",")[1], match,HockeyFunctions.processAllFixtures(hockeyService),hockeyService.getTeams(),hockeyService.getGrounds(), session_selected_broadcaster,1);
				break;
			case "POPULATE-POINTS_TABLE":
				LeagueTable league_table1 = null;
				LeagueTable league_table2 = null;
				if(new File(HockeyUtil.KHELO_INDIA_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML").exists()) {
					league_table1 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(HockeyUtil.KHELO_INDIA_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML"));
				}
				if(new File(HockeyUtil.KHELO_INDIA_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML").exists()) {
					league_table2 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(HockeyUtil.KHELO_INDIA_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML"));
				}
				
				populatePointsTable(session_socket, valueToProcess.split(",")[1],league_table1.getLeagueTeams(),league_table2.getLeagueTeams(),
						hockeyService.getTeams(),session_selected_broadcaster,match);
				break;
			case "POPULATE-L3-STAFF":
				for(Staff st : hockeyService.getStaffs()) {
					  if(st.getStaffId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateStaff(session_socket, valueToProcess.split(",")[1], st,hockeyService.getTeams(), match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-OFFICIALS":
				populateOfficials(session_socket, valueToProcess.split(",")[1],hockeyService.getOfficials(),match, session_selected_broadcaster);
				break;
			}
			
		case "NAMESUPER_GRAPHICS-OPTIONS": 
			return objectMapper.writeValueAsString(hockeyService.getNameSupers());
		case "BUG_DB_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(hockeyService.getBugs());
		case "STAFF_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(hockeyService.getStaffs());
		case "PROMO_GRAPHICS-OPTIONS": case "LTPROMO_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(HockeyFunctions.processAllFixtures(hockeyService));
		case "ANIMATE-HOME_WIPE":
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Wipes$Select_Wipe*FUNCTION*Omo*vis_con SET 1\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Wipes START\0");
			break;
		case "ANIMATE-AWAY_WIPE":
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Wipes$Select_Wipe*FUNCTION*Omo*vis_con SET 2\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Wipes START\0");
			break;	
		case "ANIMATE-OUT-SCOREBUG_STAT":
			switch(which_stats_on_screen) {
			case "PENALTY_CORNER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Penalty_Corners CONTINUE\0");
				TimeUnit.MILLISECONDS.sleep(500);
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Penalty_Corners SHOW 0.0\0");
				break;
			case "PLAYER_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Penalty_Card CONTINUE\0");
				TimeUnit.MILLISECONDS.sleep(500);
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Penalty_Card SHOW 0.0\0");
				break;
			case "GOAL_SCORER": case "LAST_GOAL":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Goal_Scorer CONTINUE\0");
				TimeUnit.MILLISECONDS.sleep(500);
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Goal_Scorer SHOW 0.0\0");
				break;	
			}
			break;
		case "TICKER_UP":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Push CONTINUE\0");
			break;
		case "TICKER_DOWN":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Push START\0");
			break;	
		case "ANIMATE-OUT-RED_CARD":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Cards CONTINUE\0");
			TimeUnit.MILLISECONDS.sleep(500);
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Cards SHOW 0.0\0");
			break;
		case "ANIMATE-IN-SCOREBUG":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Essentials START\0");
//			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
			is_infobar = true;
			scorebug.setScorebug_on_screen(true);
			break;
		case "CLEAR-ALL":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug SHOW 0.0\0");

			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ident$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_lowerthird_Player$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Quaters$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_ScoreLine$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Penalty$Essentials SHOW 0.0\0");

			print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out SHOW 0.0\0");
			which_graphics_onscreen = "";
			which_stats_on_screen = "";
			scorebug.setScorebug_on_screen(false);
			is_infobar = false;
			break;
		case "ANIMATE-OUT":
			switch(which_graphics_onscreen) {
			case "MATCHID": case "PLAYINGXI": case "POINTS_TABLE": case "MATCHSTATS":  case "PLAYINGXISUB": case "MATCHSUMMARY":
			case "SINGLEPROMO":	 case "MATCHSTAT": case "DOUBLE_PROMO": case "PLAYINGXIBOTHTEAMS": case "MATCHID_IMAGE":
			//	processAnimation(session_socket, "In", "CONTINUE", session_selected_broadcaster,2);
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out CONTINUE \0");
				which_graphics_onscreen = "";
				TimeUnit.MILLISECONDS.sleep(1000);
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out SHOW 0.0\0");
				break;
			
			case "NAMESUPERDB": case "STAFF":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials CONTINUE\0");
				which_graphics_onscreen = "";
				break;
			case "NAMESUPER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_lowerthird_Player$Essentials CONTINUE\0");
				which_graphics_onscreen = "";
				break;
			case "LT_MATCHID":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ident$Essentials CONTINUE\0");
				which_graphics_onscreen = "";
				break;
			case "QUARTER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Quaters$Essentials CONTINUE\0");
				which_graphics_onscreen = "";
				break;
			case "SCOREUPDATE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_ScoreLine$Essentials CONTINUE\0");
				which_graphics_onscreen = "";
				break;
			case "PENALTY":	
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Penalty$Essentials CONTINUE\0");
				which_graphics_onscreen = "";
				break;	
			case "BUG-DB": case "MATCHSTATUS": case "NAMESUPER_CARD":
			case "SUBSTITUTE": case "MATCHPROMO": case "PROMO": case "OFFICIALS": 
			case "SINGLE_SUBSTITUTE": 
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials CONTINUE\0");
				which_graphics_onscreen = "";
				break;	
			}
			
			TimeUnit.MILLISECONDS.sleep(1000);
			
			if(scorebug.isScorebug_on_screen() == true) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Push CONTINUE\0");
			}
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ident$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_lowerthird_Player$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Quaters$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_ScoreLine$Essentials SHOW 0.0\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Penalty$Essentials SHOW 0.0\0");
			break;
		case "ANIMATE-OUT-SCOREBUG":
			if(is_infobar == true) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Essentials CONTINUE\0");
				is_infobar = false;
				scorebug.setScorebug_on_screen(false);
				sponsor_on_screen = "";
			}
			break;
		case "ANIMATE-IN-SPONSOR":
			if(sponsor_on_screen.equalsIgnoreCase("SPONSOR")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Sponsor CONTINUE\0");
				sponsor_on_screen = "";
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Sponsor$Select_Sponsor*FUNCTION*Omo*vis_con SET 2\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Sponsor START\0");
				sponsor_on_screen = "SPONSOR";
			}
			break;
		case "ANIMATE-IN-SCOREBUG-RED_CARD":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Cards START\0");
			red_card_on_screen = "RED_CARD";
			break;
		case "ANIMATE-IN-SCOREBUG-LAST_GOAL":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Goal_Scorer START\0");
			which_stats_on_screen = "LAST_GOAL";
			break;
		case "ANIMATE-IN-SCOREBUG-GOAL_SCORER":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Goal_Scorer START\0");
			which_stats_on_screen = "GOAL_SCORER";
			break;
		case "ANIMATE-IN-SCOREBUG-CARD":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Penalty_Card START\0");
			which_stats_on_screen = "PLAYER_CARD";
			break;
		case "ANIMATE-IN-SCOREBUG_STATS":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Penalty_Corners START\0");
			which_stats_on_screen = "PENALTY_CORNER";
			break;	
		case "ANIMATE-OUT-SPONSOR": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-NAMESUPER": 
		case "ANIMATE-IN-NAMESUPERDB": case "ANIMATE-IN-BUG-DB": case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-NAMESUPER_CARD":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-LTPROMO": case "ANIMATE-IN-STAFF": case "ANIMATE-IN-MATCHSTATS": case "ANIMATE-IN-PROMO": 
		case "ANIMATE-IN-DOUBLE_PROMO": case "ANIMATE-IN-POINTS_TABLE": case "ANIMATE-IN-OFFICIALS":case "ANIMATE-IN-PENALTY": 
		case "ANIMATE-IN-SINGLE_SUBSTITUTE": case "ANIMATE-IN-PLAYINGXIBOTHTEAMS":	case "ANIMATE-IN-MATCHID_IMAGE":
		case "ANIMATE-IN-LT_MATCHID": case "ANIMATE-IN-PLAYINGXISUB": case "ANIMATE-IN-MATCHSUMMARY":
		case "ANIMATE-IN-QUARTER": case "ANIMATE-IN-PENALTY_SCORES":
		case "ANIMATE-IN-SINGLEPROMO": case "ANIMATE-IN-MATCHSTAT":	

			if(scorebug.isScorebug_on_screen() == true) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Push START\0");
			}
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-QUARTER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Quaters$Essentials START\0");
				which_graphics_onscreen = "QUARTER";
				break;
			
			case "ANIMATE-IN-LT_MATCHID": case "ANIMATE-IN-PENALTY_SCORES": case "ANIMATE-IN-LTPROMO":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ident$Essentials START\0");
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
				which_graphics_onscreen = "LT_MATCHID";
				break;
			
			case "ANIMATE-IN-PENALTY":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Penalty$Essentials START\0");
				which_graphics_onscreen = "PENALTY";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_lowerthird_Player$Essentials START\0");
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-NAMESUPERDB":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials START\0");
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-MATCHID":
//				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-PLAYINGXIBOTHTEAMS":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "PLAYINGXIBOTHTEAMS";
				break;
			case "ANIMATE-IN-MATCHID_IMAGE":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				which_graphics_onscreen = "MATCHID_IMAGE";
				break;
			case "ANIMATE-IN-MATCHSTAT":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "MATCHSTAT";
				break;
			case "ANIMATE-IN-SINGLEPROMO":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "SINGLEPROMO";
				break;
			case "ANIMATE-IN-MATCHSTATS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PLAYINGXI":
		//		processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-PLAYINGXISUB":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "PLAYINGXISUB";
				break;
			case "ANIMATE-IN-BUG-DB":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "BUG-DB";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_ScoreLine$Essentials START\0");
				TimeUnit.MILLISECONDS.sleep(500);
				
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-MATCHSUMMARY":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "MATCHSUMMARY";
				break;
			case "ANIMATE-IN-MATCHSTATUS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATUS";
				break;
			case "ANIMATE-IN-SUBSTITUTE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SUBSTITUTE";
				break;
			case "ANIMATE-IN-SINGLE_SUBSTITUTE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SINGLE_SUBSTITUTE";
				break;
			case "ANIMATE-IN-STAFF":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials START\0");
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
				which_graphics_onscreen = "STAFF";
				break;
			case "ANIMATE-IN-PROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PROMO";
				break;
			case "ANIMATE-IN-DOUBLE_PROMO":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "DOUBLE_PROMO";
				break;
			case "ANIMATE-IN-POINTS_TABLE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "POINTS_TABLE";
				break;
			case "ANIMATE-IN-OFFICIALS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "OFFICIALS";
				break;
			}
			break;
			}
		return null;
	}
	
	public void processAnimation(Socket session_socket, String animationName,String animationCommand, String which_broadcaster,int which_layer) throws IOException
	{
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		switch(which_broadcaster.toUpperCase()) {
		case "HOCKEY_KHELO_INDIA":
			switch(which_layer) {
			case 1:
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			case 2:
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			}
			break;
		}
	}
	public String toString() {
		return "Doad [status=" + status + ", slashOrDash=" + slashOrDash + "]";
	}

	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String viz_sence_path, Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$HomeTeam$Left_Score_Out$txt_Score*GEOM*TEXT SET " 
					+ match.getHomeTeamScore() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$AwayTeam$Right_Score_Out$txt_Score*GEOM*TEXT SET " 
					+ match.getAwayTeamScore() +"\0");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET Q1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET Q2\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET Q3\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET Q4\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET HT\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET FT\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("end1")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET END Q1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("end2")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET END Q2\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET ET1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$Center$txt_Q*GEOM*TEXT SET ET2\0");
			}
			
			if(is_this_updating == false) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$HomeTeam$Left_Score_Out$txt_Name*GEOM*TEXT SET " 
						+ match.getHomeTeam().getTeamName4() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$AwayTeam$Right_Score_Out$txt_Name*GEOM*TEXT SET " 
						+ match.getAwayTeam().getTeamName4() +"\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$HomeTeam$LOGO_ANIM$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
						+ match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$AwayTeam$LOGO_ANIM$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
						+ match.getAwayTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$HomeTeam$Left_Score_Out$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
						+ match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$AwayTeam$Right_Score_Out$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
						+ match.getAwayTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$HomeTeam$Left_Score_Out$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
						+ match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$AwayTeam$Right_Score_Out$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
						+ match.getAwayTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$HomeTeam$Left_Score_Out$img_Jersey*TEXTURE*IMAGE SET " + HOCKEY_JERSEY
						+ match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Main_Scorebug$AwayTeam$Right_Score_Out$img_Jersey*TEXTURE*IMAGE SET " + HOCKEY_JERSEY
						+ match.getAwayTeam().getTeamBadge() +"\0");
			}
		}
		return scorebug;
	}
	public String populateScoreBugStats(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String whichstats,String homedata,String awaydata,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			if(scorebug.isScorebug_on_screen() == true) {
				print_writer = new PrintWriter(session_socket.getOutputStream(), true);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$Home_Team$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
						match.getHomeTeam().getTeamBadge() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$Home_Team$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
						match.getHomeTeam().getTeamBadge() + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$Away_Team$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
						match.getAwayTeam().getTeamBadge() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$Away_Team$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
						match.getAwayTeam().getTeamBadge() + "\0");
				
				switch(whichstats.toUpperCase()) {
				case "YELLOW":
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$txt_Q*GEOM*TEXT SET YELLOW CARD\0");
					break;
				case "RED":
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$txt_Q*GEOM*TEXT SET RED CARD\0");
					break;
				case "OFF_SIDE":
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$txt_Q*GEOM*TEXT SET OFF SIDE\0");
					break;
				case "SHOTS":
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$txt_Q*GEOM*TEXT SET SHOTS\0");
					break;
				case "SHOTS_ON_TARGET":
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$txt_Q*GEOM*TEXT SET SHOTS ON TARGET\0");
					break;
				case "POSSESSION":
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$txt_Q*GEOM*TEXT SET POSSESSION\0");
					break;
				case "TACKLES":
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$txt_Q*GEOM*TEXT SET TACKLES\0");
					break;	
				}
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$Home_Team$txt_PC*GEOM*TEXT SET " 
						+ homedata +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Corners_Grp$Away_Team$txt_PC*GEOM*TEXT SET " 
						+ awaydata +"\0");
				
				print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_ScoreBug$Essentials 1.140 anim_ScoreBug$Essentials$In_Out 1.140 anim_ScoreBug$Essentials$In_Out$In 0.800 "
						+ "anim_ScoreBug$Penalty_Corners 1.100 anim_ScoreBug$Penalty_Corners$In_Out 1.100 anim_ScoreBug$Penalty_Corners$In_Out$In 0.460\0");
			}else {
				return this.status = "ERROR: No ScoreBug";
			}
		}
		return this.status = "OK";
	}
	public String populateScoreBugCard(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,int whichTeam,String WhichCard,int whichPlayer,String showphoto,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			if(scorebug.isScorebug_on_screen() == true) {
				print_writer = new PrintWriter(session_socket.getOutputStream(), true);
				if(whichTeam == match.getHomeTeamId()) {
					switch(WhichCard) {
					case "yellow_card":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$First$Select_Card_type*FUNCTION*Omo*vis_con SET 1\0");
						break;
					case "red_card":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$First$Select_Card_type*FUNCTION*Omo*vis_con SET 0\0");
						break;
					case "green_card":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$First$Select_Card_type*FUNCTION*Omo*vis_con SET 2\0");
						break;
					case "yellow_red":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Select_Cards*FUNCTION*Omo*vis_con SET 1\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$First$Select_Card_type*FUNCTION*Omo*vis_con SET 1\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Second$Select_Card_type*FUNCTION*Omo*vis_con SET 1\0");
						break;
					case "player":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
						break;	
					}
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Select_Team*FUNCTION*Omo*vis_con SET 1\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
							match.getHomeTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
							match.getHomeTeam().getTeamBadge() + "\0");
					
					for(Player hs : match.getHomeSquad()) {
						if(whichPlayer == hs.getPlayerId()) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + hs.getTicker_name() + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
							
							if(showphoto.equalsIgnoreCase("with_Photo")) {
								
								if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
											match.getHomeTeam().getTeamName4() + "\\\\" + hs.getPhoto() + ".png" + "\0");
									
								}else {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
											match.getHomeTeam().getTeamName4() + "\\\\" + hs.getPhoto() + ".png" + "\0");
								}
								
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 1\0");
							}else {
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 0\0");
							}
						}
					}
					
					for(Player hsub : match.getHomeSubstitutes()) {
						if(whichPlayer == hsub.getPlayerId()) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + hsub.getTicker_name() + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + hsub.getJersey_number() + "\0");
							
							if(showphoto.equalsIgnoreCase("with_Photo")) {
								
								if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
											match.getHomeTeam().getTeamName4() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
									
								}else {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
											match.getHomeTeam().getTeamName4() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
								}
								
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 1\0");
							}else {
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Home_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 0\0");
							}
						}
					}
				}else {
					
					switch(WhichCard) {
					case "yellow_card":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$First$Select_Card_type*FUNCTION*Omo*vis_con SET 1\0");
						break;
					case "red_card":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$First$Select_Card_type*FUNCTION*Omo*vis_con SET 0\0");
						break;
					case "green_card":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$First$Select_Card_type*FUNCTION*Omo*vis_con SET 2\0");
						break;
					case "yellow_red":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Select_Cards*FUNCTION*Omo*vis_con SET 1\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$First$Select_Card_type*FUNCTION*Omo*vis_con SET 1\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Second$Select_Card_type*FUNCTION*Omo*vis_con SET 1\0");
						break;
					case "player":
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
						break;	
					}
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Select_Team*FUNCTION*Omo*vis_con SET 2\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
							match.getAwayTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
							match.getAwayTeam().getTeamBadge() + "\0");
					
					for(Player as : match.getAwaySquad()) {
						if(whichPlayer == as.getPlayerId()) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + as.getTicker_name() + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Data$txt_PC*GEOM*TEXT SET " + as.getJersey_number() + "\0");
							
							if(showphoto.equalsIgnoreCase("with_Photo")) {
								
								if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
											match.getAwayTeam().getTeamName4() + "\\\\" + as.getPhoto() + ".png" + "\0");
									
								}else {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
											match.getAwayTeam().getTeamName4() + "\\\\" + as.getPhoto() + ".png" + "\0");
								}
								
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 1\0");
							}else {
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 0\0");
							}
						}
					}
					
					for(Player asub : match.getAwaySubstitutes()) {
						if(whichPlayer == asub.getPlayerId()) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + asub.getTicker_name() + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Data$txt_PC*GEOM*TEXT SET " + asub.getJersey_number() + "\0");
							
							if(showphoto.equalsIgnoreCase("with_Photo")) {
								
								if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
											match.getAwayTeam().getTeamName4() + "\\\\" + asub.getPhoto() + ".png" + "\0");
									
								}else {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
											match.getAwayTeam().getTeamName4() + "\\\\" + asub.getPhoto() + ".png" + "\0");
								}
								
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 1\0");
							}else {
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 0\0");
							}
						}
					}
				}
				print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_ScoreBug$Essentials 1.140 anim_ScoreBug$Essentials$In_Out 1.140 anim_ScoreBug$Essentials$In_Out$In 0.800 "
						+ "anim_ScoreBug$Penalty_Card 1.020 anim_ScoreBug$Penalty_Card$In_Out 1.020 anim_ScoreBug$Penalty_Card$In_Out$In 0.900\0");
			}else {
				return this.status = "ERROR: No ScoreBug";
			}
		}
		return this.status = "OK";
	}
	public String populateScoreBugScorer(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,int whichTeam,int whichPlayer,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			long time = 0;
			String tim = "";
			
			if(scorebug.isScorebug_on_screen() == true) {
				print_writer = new PrintWriter(session_socket.getOutputStream(), true);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer*ACTIVE SET 1\0");
				
				if(whichTeam == match.getHomeTeamId()) {
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Header$txt_Header*GEOM*TEXT SET GOAL SCORER\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Select_Team*FUNCTION*Omo*vis_con SET 1\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
							match.getHomeTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
							match.getHomeTeam().getTeamBadge() + "\0");
					
					for(Player hs : match.getHomeSquad()) {
						for(int i = match.getMatchStats().size()-1; i >= 0; i--) {
							if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) || 
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL) ||
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)) {
								if(match.getMatchStats().get(i).getPlayerId() == hs.getPlayerId()) {
									if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter1")) {
										time = ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter2")) {
										time = 15 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter3")) {
										time = 30 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter4")) {
										time = 45 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}
									
									tim = " (" + time + "')";
								}
							}
						}
						
						if(whichPlayer == hs.getPlayerId()) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + hs.getTicker_name() + tim + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
						}
					}
					
					for(Player hsub : match.getHomeSubstitutes()) {
						for(int i = match.getMatchStats().size()-1; i >= 0; i--) {
							if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) || 
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL) ||
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)) {
								if(match.getMatchStats().get(i).getPlayerId() == hsub.getPlayerId()) {
									if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter1")) {
										time = ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter2")) {
										time = 15 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter3")) {
										time = 30 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter4")) {
										time = 45 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}
									
									tim = " (" + time + "')";
								}
							}
						}
						
						if(whichPlayer == hsub.getPlayerId()) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + hsub.getTicker_name() + tim + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + hsub.getJersey_number() + "\0");
						}
					}
				}else {
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Header$txt_Header*GEOM*TEXT SET GOAL SCORER\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Select_Team*FUNCTION*Omo*vis_con SET 2\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
							match.getAwayTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
							match.getAwayTeam().getTeamBadge() + "\0");
					
					for(Player as : match.getAwaySquad()) {
						for(int i = match.getMatchStats().size()-1; i >= 0; i--) {
							if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) || 
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL) ||
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)) {
								if(match.getMatchStats().get(i).getPlayerId() == as.getPlayerId()) {
									if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter1")) {
										time = ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter2")) {
										time = 15 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter3")) {
										time = 30 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter4")) {
										time = 45 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}
									
									tim = " (" + time + "')";
								}
							}
						}
						
						if(whichPlayer == as.getPlayerId()) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + as.getTicker_name() + tim + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PC*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						}
					}
					
					for(Player asub : match.getAwaySubstitutes()) {
						for(int i = match.getMatchStats().size()-1; i >= 0; i--) {
							if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) || 
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL) ||
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)) {
								if(match.getMatchStats().get(i).getPlayerId() == asub.getPlayerId()) {
									if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter1")) {
										time = ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter2")) {
										time = 15 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter3")) {
										time = 30 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter4")) {
										time = 45 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}
									
									tim = " (" + time + "')";
								}
							}
						}
						
						if(whichPlayer == asub.getPlayerId()) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + asub.getTicker_name() + tim + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PC*GEOM*TEXT SET " + asub.getJersey_number() + "\0");
						}
					}
				}
				print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_ScoreBug$Essentials 1.140 anim_ScoreBug$Essentials$In_Out 1.140 anim_ScoreBug$Essentials$In_Out$In 0.800 "
						+ "anim_ScoreBug$Goal_Scorer 1.040 anim_ScoreBug$Goal_Scorer$In_Out 1.040 anim_ScoreBug$Goal_Scorer$In_Out$In 0.900\0");
			}else {
				return this.status = "ERROR: No ScoreBug";
			}
		}
		return this.status = "OK";
	}
	public String populateScoreBugLastGoal(boolean is_this_updating, Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null"; 
		} else {
			
			long time = 0;
			String tim = "";
			
			if(scorebug.isScorebug_on_screen() == true) {
				print_writer = new PrintWriter(session_socket.getOutputStream(), true);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer*ACTIVE SET 1\0");
				
				for(int i = match.getMatchStats().size()-1; i >= 0; i--) {
					if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) || 
							match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL) ||
							match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)) {
						for(Player pl : hockeyserv.getAllPlayer()) {
							if(pl.getPlayerId() == match.getMatchStats().get(i).getPlayerId()) {
								if(match.getHomeTeamId() == pl.getTeamId()) {
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Header$txt_Header*GEOM*TEXT SET LAST SCORER\0");
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Select_Team*FUNCTION*Omo*vis_con SET 1\0");
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
											match.getHomeTeam().getTeamBadge() + "\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
											match.getHomeTeam().getTeamBadge() + "\0");
									
									if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter1")) {
										time = ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter2")) {
										time = 15 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter3")) {
										time = 30 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter4")) {
										time = 45 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}
									
									tim = " (" + time + "')";
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + pl.getTicker_name() + tim + "\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + pl.getJersey_number() + "\0");
								}else {
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Header$txt_Header*GEOM*TEXT SET LAST SCORER\0");
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Select_Team*FUNCTION*Omo*vis_con SET 2\0");
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
											match.getAwayTeam().getTeamBadge() + "\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
											match.getAwayTeam().getTeamBadge() + "\0");
									
									if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter1")) {
										time = ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter2")) {
										time = 15 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter3")) {
										time = 30 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter4")) {
										time = 45 + ((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1;
									}
									
									tim = " (" + time + "')";
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + pl.getTicker_name() + tim + "\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PC*GEOM*TEXT SET " + pl.getJersey_number() + "\0");
								}
								break;
							}
						}
						break;
					}
				}
				print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_ScoreBug$Essentials 1.140 anim_ScoreBug$Essentials$In_Out 1.140 anim_ScoreBug$Essentials$In_Out$In 0.800 "
						+ "anim_ScoreBug$Goal_Scorer 1.040 anim_ScoreBug$Goal_Scorer$In_Out 1.040 anim_ScoreBug$Goal_Scorer$In_Out$In 0.900\0");
			}else {
				return this.status = "ERROR: No ScoreBug";
			}
		}
		return this.status = "OK";
	}
	public String populateScoreBugRedCards(boolean is_this_updating, Socket session_socket,int home, int away,Match match, String selectedbroadcaster,HockeyService hockeyserv) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			if(scorebug.isScorebug_on_screen() == true) {
				print_writer = new PrintWriter(session_socket.getOutputStream(), true);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Cards$Home_Team$Select_Cards*FUNCTION*Omo*vis_con SET " + home + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Cards$Away_Team$Select_Cards*FUNCTION*Omo*vis_con SET " + away + "\0");
				
				
				print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_ScoreBug$Essentials 1.140 anim_ScoreBug$Essentials$In_Out 1.140 anim_ScoreBug$Essentials$In_Out$In 0.800 "
						+ "anim_ScoreBug$Cards 0.480 anim_ScoreBug$Cards$In_Out 0.480 anim_ScoreBug$Cards$In_Out$In 0.420\0");
			}else {
				return this.status = "ERROR: No ScoreBug";
				
			}
		}
		return this.status = "OK";
	}

	public void populateLtMatchId(Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
					+ match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
					+ match.getHomeTeam().getTeamBadge() +"\0");
			
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
					+ match.getAwayTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
					+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$LOGO_ANIM$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$LOGO_ANIM$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$Team_Grp$txt_Name*GEOM*TEXT SET " 
					+ match.getHomeTeam().getTeamName2() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$Team_Grp$txt_Name02*GEOM*TEXT SET " 
					+ match.getHomeTeam().getTeamName3() +"\0");
			
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$Team_Grp$txt_Name*GEOM*TEXT SET " 
					+ match.getAwayTeam().getTeamName2() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$Team_Grp$txt_Name02*GEOM*TEXT SET " 
					+ match.getAwayTeam().getTeamName3() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Info*GEOM*TEXT SET " 
					+ match.getMatchIdent() + " - FROM " + match.getVenueName() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Vs*GEOM*TEXT SET vs\0");
			
		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_Ident$Essentials 1.140 anim_Ident$Essentials$In_Out 1.140 "
				+ "anim_Ident$Essentials$In_Out$In 0.880 \0");
	}
	public ScoreBug populateQuarters(boolean is_this_updating, Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null"; 
		} else {
			
			int i = 0;
			
			List<Quarter> quarters = readQuarters("C:\\Sports\\Hockey\\Quarters.txt");

			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
	        print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Name_Out$StatsHead$Select*FUNCTION*Omo*vis_con SET " + (quarters.size() - 1) + "\0");
	        
	        print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Name_Out$Text_Data$Quaters$Select*FUNCTION*Omo*vis_con SET " + (quarters.size() - 1) + "\0");
	        print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Designation_Out$Text_Data$Quaters$Select*FUNCTION*Omo*vis_con SET " + (quarters.size() - 1) + "\0");
	        
	        print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$HomeTeam_Logo$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() + "\0");
        	print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$AwayTeam_Logo$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() + "\0");
        	
        	
	        print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Name_Out$Text_Data$txt_Name*GEOM*TEXT SET " + match.getHomeTeam().getTeamName2() + "\0");
        	print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Designation_Out$Text_Data$txt_Name*GEOM*TEXT SET " + match.getAwayTeam().getTeamName2() + "\0");
        	
	        for (Quarter q : quarters) {
	        	i = i + 1;
	        	print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Name_Out$Text_Data$Quaters$Select$" + getQuarterName(i) + "$txt_Value*GEOM*TEXT SET " + q.home + "\0");
	        	print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Designation_Out$Text_Data$Quaters$Select$" + getQuarterName(i) + "$txt_Value*GEOM*TEXT SET " + q.away + "\0");
	        }
	        
	        print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Name_Out$Text_Data$Quaters_Total$Total$txt_Value*GEOM*TEXT SET " + match.getHomeTeamScore() + "\0");
        	print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Quaters$Designation_Out$Text_Data$Quaters_Total$Total$txt_Value*GEOM*TEXT SET " + match.getAwayTeamScore() + "\0");
		}
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_LT_Quaters$Essentials 1.140 anim_LT_Quaters$Essentials$In_Out 1.140 "
				+ "anim_LT_Quaters$Essentials$In_Out$In 0.880 \0");
		
		return scorebug;
	}
	public void populateNameSuper(Socket session_socket,String viz_scene, NameSuper ns ,Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			
			if(ns.getFirstname() == null) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Name_Out$txt_Name*GEOM*TEXT SET " 
						+ ns.getSurname().toUpperCase() +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(ns.getSurname() == null) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Name_Out$txt_Name*GEOM*TEXT SET " 
						+ ns.getFirstname().toUpperCase() +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Name_Out$txt_Name*GEOM*TEXT SET " 
						+ ns.getFirstname().toUpperCase() + " " + ns.getSurname().toUpperCase() +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			if(ns.getSponsor() != null) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
						+ ns.getSponsor() +"\0");
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
						+ "TLogo" +"\0");
			}
			
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Designation_Out$txt_Designation*GEOM*TEXT SET " 
					+ ns.getSubLine().toUpperCase() +"\0");
			TimeUnit.MILLISECONDS.sleep(l);
		}
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_LowerThird$Essentials 1.140 anim_LowerThird$Essentials$In_Out 1.140 "
				+ "anim_LowerThird$Essentials$In_Out$In 0.880 \0");
		
	}
	public void populateNameSuperPlayer(Socket session_socket,String viz_scene, int TeamId, String captainGoalKeeper, int playerId,String cardType,String Photo, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			String Home_or_Away="";
			int l = 4;
			
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(Photo.equalsIgnoreCase("WITH_PHOTO")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Select_Logo_Image*FUNCTION*Omo*vis_con SET 1\0");
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Select_Logo_Image*FUNCTION*Omo*vis_con SET 0\0");
			}
			
			if(TeamId == match.getHomeTeamId()) {
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Team_Logo$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
						+ match.getHomeTeam().getTeamBadge() +"\0");
				
				Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						
						if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
									match.getHomeTeam().getTeamName4() + "\\\\" + hs.getPhoto() + ".png" + "\0");
							
						}else {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
									match.getHomeTeam().getTeamName4() + "\\\\" + hs.getPhoto() + ".png" + "\0");
						}
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$txt_Number*GEOM*TEXT SET " 
								+ hs.getJersey_number() +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$txt_Name*GEOM*TEXT SET " 
								+ hs.getFull_name().toUpperCase() +"\0");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
									+ match.getHomeTeam().getTeamName1().toUpperCase() +"\0");
							
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
									+ hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() +"\0");
						
						}
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$txt_Number*GEOM*TEXT SET " 
								+ hsub.getJersey_number() +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$txt_Name*GEOM*TEXT SET " 
								+ hsub.getFull_name().toUpperCase() +"\0");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
									match.getHomeTeam().getTeamName4() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
							
						}else {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
									match.getHomeTeam().getTeamName4() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
						}

						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
									+ match.getHomeTeam().getTeamName1().toUpperCase() +"\0");
							
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
									+ hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() +"\0");
						}
					}
				}
			}
			else {

				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Team_Logo$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
						+ match.getAwayTeam().getTeamBadge() +"\0");
				
				Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$txt_Number*GEOM*TEXT SET " 
								+ as.getJersey_number() +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$txt_Name*GEOM*TEXT SET " 
								+ as.getFull_name().toUpperCase() +"\0");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
									match.getAwayTeam().getTeamName4() + "\\\\" + as.getPhoto() + ".png" + "\0");
							
						}else {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
									match.getAwayTeam().getTeamName4() + "\\\\" + as.getPhoto() + ".png" + "\0");
						}

						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
									+ match.getAwayTeam().getTeamName1().toUpperCase() +"\0");
							TimeUnit.MILLISECONDS.sleep(l);
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
									+ as.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName4().toUpperCase() +"\0");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$txt_Number*GEOM*TEXT SET " 
								+ asub.getJersey_number() +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$txt_Name*GEOM*TEXT SET " 
								+ asub.getFull_name().toUpperCase() +"\0");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
									match.getAwayTeam().getTeamName4() + "\\\\" + asub.getPhoto() + ".png" + "\0");
							
						}else {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$img_PlayerImage*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
									match.getAwayTeam().getTeamName4() + "\\\\" + asub.getPhoto() + ".png" + "\0");
						}

						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
									+ match.getAwayTeam().getTeamName1().toUpperCase() +"\0");
							TimeUnit.MILLISECONDS.sleep(l);
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
									+ asub.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName4().toUpperCase() +"\0");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
				}
			}
			
			switch(captainGoalKeeper.toUpperCase())
			{
			case "CAPTAIN":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
						+ captainGoalKeeper.toUpperCase() + " , " + Home_or_Away +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "PLAYER OF THE MATCH":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
						+ "HERO OF THE MATCH " +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "GOAL_KEEPER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
						+ "GOAL KEEPER" + " , " + Home_or_Away +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "PLAYER_TODAY_GOAL":
				int player_goal_count=0;
				for(MatchStats ms : match.getMatchStats()) {
					if(ms.getStats_type().equalsIgnoreCase("goal")) {
						if(ms.getPlayerId() == playerId) {
							player_goal_count = player_goal_count + 1;
						}
					}else if(ms.getStats_type().equalsIgnoreCase("penalty")) {
						if(ms.getPlayerId() == playerId) {
							player_goal_count = player_goal_count + 1;
						}
					}
				}
				if(player_goal_count != 0) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
							+ "GOALS TODAY - " + player_goal_count +"\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}else {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
							+ Home_or_Away +"\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}
				
				break;
			case "GOAL_SCORER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
						+ "GOAL SCORER" + " , " + Home_or_Away +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "CAPTAIN-GOALKEEPER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Designation_Out$txt_Designation*GEOM*TEXT SET " 
						+ "CAPTAIN & GOAL KEEPER" + " , " + Home_or_Away +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}
			
			
			switch(cardType.toUpperCase())
			{
			case "NO_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 0\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "GREEN":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$First$Select*FUNCTION*Omo*vis_con SET 2\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case HockeyUtil.YELLOW:
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$First$Select*FUNCTION*Omo*vis_con SET 1\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case HockeyUtil.RED:
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$First$Select*FUNCTION*Omo*vis_con SET 0\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 2\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$First$Select*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$Second$Select*FUNCTION*Omo*vis_con SET 1\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}
		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_lowerthird_Player$Essentials 1.140 anim_lowerthird_Player$Essentials$In_Out 1.140 "
				+ "anim_lowerthird_Player$Essentials$In_Out$In 0.880 \0");
	}
	public void populateNameSuperCard(Socket session_socket, int TeamId, String cardType, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
						HockeyUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + "\\Colour1" + HockeyUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + "\\Colour2" + HockeyUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hs.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + hs.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + hs.getRole().toUpperCase() + " - " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hsub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + hsub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + hsub.getRole().toUpperCase() + " - " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
			}
			else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
						HockeyUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + "\\Colour1" + HockeyUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + "\\Colour2" + HockeyUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + as.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + as.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + as.getRole().toUpperCase() + " - " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + asub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + asub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + asub.getRole().toUpperCase() + " - " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
			}
			
			switch(cardType.toUpperCase())
			{
			case "GREEN":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$First$Select*FUNCTION*Omo*vis_con SET 2\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case HockeyUtil.YELLOW:
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$First$Select*FUNCTION*Omo*vis_con SET 1\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case HockeyUtil.RED:
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$First$Select*FUNCTION*Omo*vis_con SET 0\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards*FUNCTION*Omo*vis_con SET 2\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$First$Select*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Name_Out$Select_Cards$Second$Select*FUNCTION*Omo*vis_con SET 1\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}
		}
	}
	
	public void populateMatchIdImage(Socket session_socket,String viz_scene, Match match, String session_selected_broadcaster,int whichSide,HockeyService hockeyService, List<Team> team) throws InterruptedException, IOException
	{	
		String previewCommand ="";
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 2 \0");
			
			
			if(match.getMatchIdent().contains("FINAL")) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "FINAL " +"\0");
				
			}else if(match.getMatchIdent().contains("SEMI") && match.getMatchId() == 1) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "S-FINAL  1" +"\0");
				
			}else if(match.getMatchIdent().contains("SEMI") && match.getMatchId() == 2) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "S-FINAL  2" +"\0");
			}else {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "MATCH " + match.getMatchId() +"\0");
			}
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
					+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
					+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() +"\0");
			
			for(Player hsh : match.getHomeSquad()) {
				if(hsh.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN) || hsh.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN_GOAL_KEEPER)) {
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Left_Logo_Out$img_Player_Team1*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH + match.getHomeTeam().getTeamName4() + "\\\\" + 
								"Blank" + HockeyUtil.PNG_EXTENSION +"\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Left_Logo_Out$img_Player_Team1*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH + match.getHomeTeam().getTeamName4() + "\\\\" + 
								hsh.getPhoto() + HockeyUtil.PNG_EXTENSION +"\0");
						
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Left_Logo_Out$img_Player_Team1*TEXTURE*IMAGE SET "+ "\\\\" + IndexController.session_configurations.getIpAddress()
								+  HOCKEY_PHOTO_PATH + match.getHomeTeam().getTeamName4() + "\\\\" + "Blank" + HockeyUtil.PNG_EXTENSION +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Left_Logo_Out$img_Player_Team1*TEXTURE*IMAGE SET "+ "\\\\" + IndexController.session_configurations.getIpAddress()
								+  HOCKEY_PHOTO_PATH + match.getHomeTeam().getTeamName4() + "\\\\" + hsh.getPhoto() + HockeyUtil.PNG_EXTENSION +"\0");
					}
				}
			}
			
			for(Player hsa : match.getAwaySquad()) {
				if(hsa.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN) || hsa.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN_GOAL_KEEPER)) {
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Right_Logo_Out$img_Player_Team2*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH + match.getAwayTeam().getTeamName4() + "\\\\" +
								"Blank" + HockeyUtil.PNG_EXTENSION +"\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Right_Logo_Out$img_Player_Team2*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH + match.getAwayTeam().getTeamName4() + "\\\\" +
								hsa.getPhoto() + HockeyUtil.PNG_EXTENSION +"\0");
						
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Right_Logo_Out$img_Player_Team2*TEXTURE*IMAGE SET "+ "\\\\" + IndexController.session_configurations.getIpAddress() +
								HOCKEY_PHOTO_PATH + match.getAwayTeam().getTeamName4() + "\\\\" + "Blank" + HockeyUtil.PNG_EXTENSION +"\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Right_Logo_Out$img_Player_Team2*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() 
								+ HOCKEY_PHOTO_PATH + match.getAwayTeam().getTeamName4() + "\\\\" + hsa.getPhoto() + HockeyUtil.PNG_EXTENSION +"\0");
					}
				}
			}
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
					+ "$Date_Out*ACTIVE SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
					+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "SARDAR VALLABHBHAI PATEL" +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
					+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+ "INTERNATIONAL HOCKEY STADIUM, RAIPUR, CHHATTISGARH" +"\0");
			
			
			for(VariousText vt : hockeyService.getVariousTexts()) {
				if(vt.getVariousType().equalsIgnoreCase("MATCHIDENTFOOTER") && vt.getUseThis().equalsIgnoreCase("YES") ) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
							+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ vt.getVariousText() +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
							+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+ vt.getVariousText1() +"\0");
				}
			}
			
			previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$Final_Vs 2.540 anim_FullFrame$In_Out$Final_Vs$InOut 2.540 anim_FullFrame$In_Out$Final_Vs$InOut$In 2.540 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		}
	}
	
	public void populateMatchId(Socket session_socket,String viz_scene, Match match, String session_selected_broadcaster,int whichSide,HockeyService hockeyService) throws InterruptedException, IOException
	{
		String previewCommand = "";
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 1 \0");
			
			
			if(match.getMatchIdent().contains("FINAL")) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "FINAL " +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "" +"\0");
				
			}else if(match.getMatchIdent().contains("SEMI") && match.getMatchId() == 1) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "S-FINAL  " +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "1" +"\0");
				
			}else if(match.getMatchIdent().contains("SEMI") && match.getMatchId() == 2) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "S-FINAL  " +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "2" +"\0");
			}else {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "MATCH" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ match.getMatchId() +"\0");
			}
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Date_Out*ACTIVE SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "SARDAR VALLABHBHAI PATEL" +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+ "INTERNATIONAL HOCKEY STADIUM, RAIPUR, CHHATTISGARH" +"\0");
			
			
			for(VariousText vt : hockeyService.getVariousTexts()) {
				if(vt.getVariousType().equalsIgnoreCase("MATCHIDENTFOOTER") && vt.getUseThis().equalsIgnoreCase("YES") ) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
							+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ vt.getVariousText() +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
							+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+ vt.getVariousText1() +"\0");
				}
			}
			previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$Match_Id 2.540 anim_FullFrame$In_Out$Match_Id$In_Out 2.540 anim_FullFrame$In_Out$Match_Id$In_Out$In 2.540 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
			
		}
	}
	public void populateMatchStatsFF(Socket session_socket, String viz_scene, Match match,
	        String session_selected_broadcaster, int whichSide, HockeyService hockeyService)
	        throws InterruptedException, IOException
	{	
		String previewCommand = "";
	    if (match == null) {
	        this.status = "ERROR: Match is null";
	    } else {

	        String sideBase = "-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide;

	        print_writer.println(sideBase + "$FF_Gfx*FUNCTION*Omo*vis_con SET 7 \0");

	        print_writer.println(sideBase + "$Match_Stats$Data"
	                + "$Score_Out$Score$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore() + "\0");

	        print_writer.println(sideBase + "$Match_Stats$Data"
	                + "$Match_Summary_Out$Summary$Summary*GEOM*TEXT SET " + "STATS" + "\0");

	        print_writer.println(sideBase + "$Match_Stats$Data"
	                + "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET " + logo_path + match.getHomeTeam().getTeamBadge() + "\0");

	        print_writer.println(sideBase + "$Match_Stats$Data"
	                + "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET " + logo_path + match.getAwayTeam().getTeamBadge() + "\0");

	        String filePath = "C:/Sports/Hockey/TournamentStats.txt";
	        if (!Files.exists(Paths.get(filePath))) {
	            this.status = "ERROR: TournamentStats.txt not found";
	            return;
	        }

	        List<String[]> rows = new ArrayList<>();
	        for (String line : Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8)) {
	            line = line.trim();
	            if (line.isEmpty()) continue;

	            String[] p = line.split(",");
	            if (p.length >= 3) {
	                rows.add(new String[] { p[0].trim(), p[1].trim(), p[2].trim() }); // home, label, away
	            }
	        }

	        String statsBase = sideBase + "$Match_Stats$Data$Stats_Out$Stats";

	        for (int i = 0; i < rows.size() && i < 5; i++) {
	            String home  = rows.get(i)[0];
	            String label = rows.get(i)[1];
	            String away  = rows.get(i)[2];
	            String grp   = statsBase + (i + 1) + "_Grp";

	            print_writer.println(grp + "$txt_Stat_Head*GEOM*TEXT SET " + label + "\0");

	            if (label.equalsIgnoreCase("CARDS")) {
	                String[] h = home.split("/");
	                String[] a = away.split("/");

	                for (int c = 1; c <= 3; c++) {
	                    String homeVal = (c <= h.length) ? h[c - 1].trim() : "0";
	                    String awayVal = (c <= a.length) ? a[c - 1].trim() : "0";

	                    print_writer.println(grp + "$Home$Card_Grp" + c + "$txt_Card01"
	                            + "*GEOM*TEXT SET " + homeVal + "\0");

	                    print_writer.println(grp + "$Away$Card_Grp" + c + "$txt_Card01"
	                            + "*GEOM*TEXT SET " + awayVal + "\0");
	                }
	            }else {
	                print_writer.println(grp + "$txt_Home_Stat_Value*GEOM*TEXT SET " + home + "\0");
	                print_writer.println(grp + "$txt_Away_Stat_Value*GEOM*TEXT SET " + away + "\0");
	            }
	        }
	        
	        previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$Match_Stats 2.540 anim_FullFrame$In_Out$Match_Stats$In_Out 2.540 anim_FullFrame$In_Out$Match_Stats$In_Out$In 2.540 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
	    }
	}
	public void populateFFMatchPromo(Socket session_socket,int teamID, Match match, String session_selected_broadcaster,int whichSide,HockeyService hockeyService) throws InterruptedException, IOException
	{	
		String previewCommand="";
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			System.out.println("team id is ============" + teamID);
			
			whichSide = 1;
			String match_name="";
			String newDate = "", date_data = "";
			String[] dateSuffix = {
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
					
					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
					
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
					
					"th", "st"
			};

			fixture = HockeyFunctions.processAllFixtures(hockeyService).stream().filter(fix -> fix.getMatchnumber() == teamID).
					findAny().orElse(null);
			
			if(fixture.getMatchfilename().equalsIgnoreCase("FINAL")) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "FINAL" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "" +"\0");
				
			}else if(fixture.getMatchfilename().contains("SEMI FINAL 1")) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "S-FINAL   " +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "1" +"\0");
				
			}else if(fixture.getMatchfilename().contains("SEMI FINAL 2")) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "S-FINAL   " +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "2" +"\0");
			}else if(fixture.getMatchfilename().contains("EXHIBITION")) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "WOMENS   " +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "" +"\0");
			}
			else {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "MATCH" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ (fixture.getMatchnumber()-1) +"\0");
			}
			
			match_name = (fixture.getMatchnumber() > 9 ? fixture.getMatchfilename() : "MATCH " + fixture.getMatchnumber());
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 1 \0");
			
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + 
					fixture.getHome_Team().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET "+ logo_path + 
					fixture.getAway_Team().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Date_Out*ACTIVE SET 0\0");
			
			Calendar cal_mumbai = Calendar.getInstance();
			cal_mumbai.add(Calendar.DATE, +1);
			if(fixture.getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal_mumbai.getTime()))) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
						+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "TOMORROW AT " + fixture.getTime() +"\0");
				for(Ground gn:hockeyService.getGrounds()) {
					if(gn.getGroundId() == fixture.getVenue()) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
								+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+  gn.getCity() +"\0");
					}
				}
			}else {
				cal_mumbai.add(Calendar.DATE, -1);
				if(fixture.getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal_mumbai.getTime()))) {
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
							+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "UP-NEXT" +"\0");
					for(Ground gn:hockeyService.getGrounds()) {
						if(gn.getGroundId() == fixture.getVenue()) {
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
									+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+  gn.getCity() +"\0");
						}
					}
					
				}else {
					newDate = fixture.getDate().split("-")[0];
					if(Integer.valueOf(newDate) < 10) {
						newDate = newDate.replaceFirst("0", "");
					}
					date_data = HockeyFunctions.ordinal(Integer.valueOf(newDate)) + " " + Month.of(Integer.valueOf(fixture.getDate().split("-")[1]));
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
							+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ date_data + " / " + fixture.getTime()  +"\0");
					for(Ground gn:hockeyService.getGrounds()) {
						if(gn.getGroundId() == fixture.getVenue()) {
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
									+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+  gn.getCity() +"\0");
						}
					}
				}
			}
			previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$Match_Id 2.540 anim_FullFrame$In_Out$Match_Id$In_Out 2.540 anim_FullFrame$In_Out$Match_Id$In_Out$In 2.540 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		}
	}	
	public void populateSubstitute(Socket session_socket,String viz_scene,int Team_id,String Num_Of_Subs,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + team.get(Team_id - 1).getTeamName4() + 
					HockeyUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "SUBSTITUTIONS" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int i = 0; i<=match.getEvents().size()-1; i++) { 
				if(match.getEvents().get(i).getEventType().equalsIgnoreCase("replace")) {
					if(Team_id ==plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
						if(match.getHomeTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						}else if(match.getAwayTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						} 
					} 
				}
			}
			
			switch(Num_Of_Subs.toUpperCase())
			{
			case "SINGLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
				
				break;
			case "DOUBLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
				
				break;
				
			case "TRIPLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2C " + logo_path + "Red_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1C " + logo_path + "Green_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
				
				break;
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 36.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateSingleSubstitute(Socket session_socket,String viz_scene,int Team_id,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws IOException, InterruptedException {
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + team.get(Team_id - 1).getTeamName4() + 
					HockeyUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "SUBSTITUTIONS" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int i = 0; i<=match.getEvents().size()-1; i++) { 
				if(match.getEvents().get(i).getEventType().equalsIgnoreCase("replace")) {
					if(Team_id ==plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
						if(match.getHomeTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						}else if(match.getAwayTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						} 
					} 
				}
			}
			
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 0;");
			//TimeUnit.MILLISECONDS.sleep(l);
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 0;");
			//TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2 " + logo_path + "Red_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
		
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1 " + logo_path + "Green_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
				
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 36.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populatePlayingXIBOTH(Socket session_socket,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster
			,int whichSide,HockeyService hockeyService) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			 String previewCommand ="";
			 int row_idh=0;
			 int row_ida=0;
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 9 \0");

			
			
			if(match.getMatchIdent().contains("EXHIBITION")) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
						+ "$Match_Summary_Out$Druk-Trial-Medium*GEOM*TEXT SET "+ "TEAM" +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
						+ "$Match_Summary_Out$Summary$Summary*GEOM*TEXT SET "+ "LIST" +"\0");
			}else {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
						+ "$Match_Summary_Out$Druk-Trial-Medium*GEOM*TEXT SET "+ "M-" + match.getMatchId() +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
						+ "$Match_Summary_Out$Summary$Summary*GEOM*TEXT SET "+ "SQUADS" +"\0");
			}
			
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
						+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
						+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() +"\0");
				
				
				for(Player hs:match.getHomeSquad()) {
					row_idh =row_idh+1;
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
								+ "$Stats_Out$HomeTeam$Stats"+ row_idh +"$txt_Name*GEOM*TEXT SET "+ hs.getFull_name()+ " (C)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
								+ "$Stats_Out$HomeTeam$Stats"+ row_idh +"$txt_Name*GEOM*TEXT SET "+ hs.getFull_name()+ " (GK)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
								+ "$Stats_Out$HomeTeam$Stats"+ row_idh +"$txt_Name*GEOM*TEXT SET "+ hs.getFull_name()+ " (C)(GK)" +"\0");
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
								+ "$Stats_Out$HomeTeam$Stats"+ row_idh +"$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					}
					
				}
				
				for(Player as:match.getAwaySquad()) {
					row_ida =row_ida+1;
					if(as.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
								+ "$Stats_Out$AwayTeam$Stats"+ row_ida +"$txt_Name*GEOM*TEXT SET "+ as.getFull_name()+ " (C)" +"\0");
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
								+ "$Stats_Out$AwayTeam$Stats"+ row_ida +"$txt_Name*GEOM*TEXT SET "+ as.getFull_name()+ " (GK)" +"\0");
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
								+ "$Stats_Out$AwayTeam$Stats"+ row_ida +"$txt_Name*GEOM*TEXT SET "+ as.getFull_name()+ " (C)(GK)" +"\0");
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
								+ "$Stats_Out$AwayTeam$Stats"+ row_ida +"$txt_Name*GEOM*TEXT SET "+ as.getFull_name() +"\0");
					}
				}
				
			previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$TeamList_Both 2.540 anim_FullFrame$In_Out$TeamList_Both$In_Out 2.540 anim_FullFrame$In_Out$TeamList_Both$In_Out$In 2.100 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
			
		}
	}
	
	
	public void populatePlayingXI(Socket session_socket,int TeamId,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster
			,int whichSide,HockeyService hockeyService) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			 String previewCommand ="";
			 int row_id=0;
			 boolean hc=false;
			 boolean ac=false;
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 4 \0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
					+ "$Assistant_Coach*ACTIVE SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
					+ "$Head_Coach*ACTIVE SET 0\0");
			
			
			for(Staff st: hockeyService.getStaffs()) {
				if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("HEAD COACH")) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$Head_Coach$txt_Head_Coach*GEOM*TEXT SET "+ "HEAD COACH" +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$Head_Coach$txt_Header*GEOM*TEXT SET "+ st.getName() +"\0");
					hc=true;
				}
				if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("ASSISTANT COACH")) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$Assistant_Coach$txt_Head_Coach*GEOM*TEXT SET "+ "ASSISTANT COACH" +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$Assistant_Coach$txt_Header*GEOM*TEXT SET "+ st.getName() +"\0");
					ac=true;
				}
			}
			
			
			if(ac == true) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
						+ "$Assistant_Coach*ACTIVE SET 1\0");
			}
			if(hc) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
						+ "$Head_Coach*ACTIVE SET 1\0");
			}
			
			
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$LOGO_ANIM"
						+ "$noname$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() +"\0");
				
				
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
						+ "$Header_Out$txt_Header*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName1() +"\0");
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id +"$Player$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id +"$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH +
								match.getHomeTeam().getTeamName4() +  "\\\\"  +hs.getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id +"$Player$img_Player_Team1*TEXTURE*IMAGE SET "+ "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id +"$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() +  "\\\\"  +hs.getPhoto() + ".png" +"\0");
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Base1*TEXTURE*IMAGE SET "+ HOCKEY_BASE1 + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Base2*TEXTURE*IMAGE SET "+ HOCKEY_BASE2 + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Text1*TEXTURE*IMAGE SET "+ HOCKEY_TEXT1 + match.getHomeTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Text2*TEXTURE*IMAGE SET "+ HOCKEY_TEXT2 + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Text1$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name*GEOM*TEXT SET "+ hs.getJersey_number() +"\0");
					
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(GK)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)(GK)" +"\0");
					}else {
						String role = hs.getRole().trim();
						String shortRole = "";

						if (role.equalsIgnoreCase("Goalkeeper")) {
						    shortRole = "GK";
						} else if (role.equalsIgnoreCase("Defender") || role.equalsIgnoreCase("Deferder")) {
						    shortRole = "DF";
						} else if (role.equalsIgnoreCase("Midfielder")) {
						    shortRole = "MF";
						} else if (role.equalsIgnoreCase("Forward")) {
						    shortRole = "FW";
						}

						if (!shortRole.isEmpty()) {
						    print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
						            + "$noname$Players$Player" + row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET " + shortRole + "\0");
						}						
					}
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getHomeTeamFormationId()) {
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
							
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
							
						}
					}
					
				}
				
			}else if(TeamId == match.getAwayTeamId()){
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$LOGO_ANIM"
						+ "$noname$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
						+ "$Header_Out$txt_Header*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName1() +"\0");
				
				for(Player hs : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id +"$Player$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id +"$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH +
								match.getAwayTeam().getTeamName4() +  "\\\\"  +hs.getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id +"$Player$img_Player_Team1*TEXTURE*IMAGE SET "+ "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id +"$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4() +  "\\\\"  +hs.getPhoto() + ".png" +"\0");
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Base1*TEXTURE*IMAGE SET "+ HOCKEY_BASE1 + match.getAwayTeam().getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Base2*TEXTURE*IMAGE SET "+ HOCKEY_BASE2 + match.getAwayTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Text1*TEXTURE*IMAGE SET "+ HOCKEY_TEXT1 + match.getAwayTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Text2*TEXTURE*IMAGE SET "+ HOCKEY_TEXT2 + match.getAwayTeam().getTeamBadge() +"\0");	
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Text1$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
							+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name*GEOM*TEXT SET "+ hs.getJersey_number() +"\0");
					
					
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(GK)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)(GK)" +"\0");
					}else {
						String role = hs.getRole().trim();
						String shortRole = "";

						if (role.equalsIgnoreCase("Goalkeeper")) {
						    shortRole = "GK";
						} else if (role.equalsIgnoreCase("Defender") || role.equalsIgnoreCase("Deferder")) {
						    shortRole = "DF";
						} else if (role.equalsIgnoreCase("Midfielder")) {
						    shortRole = "MF";
						} else if (role.equalsIgnoreCase("Forward")) {
						    shortRole = "FW";
						}

						if (!shortRole.isEmpty()) {
						    print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
						            + "$noname$Players$Player" + row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET " + shortRole + "\0");
						}
					}
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getAwayTeamFormationId()) {
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
							
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
									+ "$noname$Players$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
							
						}
					}
				}
				
			}
			previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$Playing_11 2.540 anim_FullFrame$In_Out$Playing_11$InOut 2.540 anim_FullFrame$In_Out$Playing_11$InOut$In 2.100 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
			
		}
	}
	public void populatePlayingXISUB(Socket session_socket,int TeamId,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster
			,int whichSide,HockeyService hockeyService) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			int row_id=0,row_id_sub=0;
			boolean hc=false;
			boolean ac=false;
			String previewCommand ="";
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 5 \0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
					+ "$Head_Coach_Out$Head_Coach*ACTIVE SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
					+ "$Head_Coach_Out$Assistant_Coach*ACTIVE SET 0\0");
			
			
			for(Staff st: hockeyService.getStaffs()) {
				if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("HEAD COACH")) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Head_Coach_Out$Head_Coach$txt_Head_Coach*GEOM*TEXT SET "+ "HEAD COACH" +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Head_Coach_Out$Head_Coach$txt_Header*GEOM*TEXT SET "+ st.getName() +"\0");
					hc=true;
				}
				if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("ASSISTANT COACH")) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Head_Coach_Out$Assistant_Coach$txt_Head_Coach*GEOM*TEXT SET "+ "ASSISTANT COACH" +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Head_Coach_Out$Assistant_Coach$txt_Header*GEOM*TEXT SET "+ st.getName() +"\0");
					ac=true;
				}
			}
			
			
			if(ac == true) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						+ "$Head_Coach_Out$Assistant_Coach*ACTIVE SET 1\0");
			}
			if(hc) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						+ "$Head_Coach_Out$Head_Coach*ACTIVE SET 1\0");
			}			
			
			
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$LOGO_ANIM"
						+ "$noname$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						+ "$Head_Coach_Out$Header$txt_Header*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName1() +"\0");
				
				
				for(int i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) { 
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() +  "\\\\"  +match.getHomeSubstitutes().get(i).getPhoto() + ".png" +"\0");
						
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() +  "\\\\"  +match.getHomeSubstitutes().get(i).getPhoto() + ".png" +"\0");
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Substitute_Players*FUNCTION*Grid*num_col SET " + row_id_sub + " \0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text1$txt_Name*GEOM*TEXT SET "+ match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() +"\0");
				
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name*GEOM*TEXT SET "+ match.getHomeSubstitutes().get(i).getJersey_number() +"\0");
					
//					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
//							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name*GEOM*TEXT SET "+ "" +"\0");
					if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)" +"\0");
					}else if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(GK)" +"\0");
					}else if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)(GK)" +"\0");
					}else {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
//								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "" +"\0");
						
						String subRole = match.getHomeSubstitutes().get(i).getRole().trim();
						String subShortRole = "";

						if (subRole.equalsIgnoreCase("Goalkeeper")) subShortRole = "GK";
						else if (subRole.equalsIgnoreCase("Defender") || subRole.equalsIgnoreCase("Deferder")) subShortRole = "DF";
						else if (subRole.equalsIgnoreCase("Midfielder")) subShortRole = "MF";
						else if (subRole.equalsIgnoreCase("Forward")) subShortRole = "FW";

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						        + "$Substitute_Players$Player" + row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET " + subShortRole + "\0");
						
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Base1*TEXTURE*IMAGE SET "+ HOCKEY_BASE1 + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Base2*TEXTURE*IMAGE SET "+ HOCKEY_BASE2 + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text1*TEXTURE*IMAGE SET "+ HOCKEY_TEXT1 + match.getHomeTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2*TEXTURE*IMAGE SET "+ HOCKEY_TEXT2 + match.getHomeTeam().getTeamBadge() +"\0");
				
				}
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) { 
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() +  "\\\\"  + hs.getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() +  "\\\\"  + hs.getPhoto() + ".png" +"\0");
					}
					
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Base1*TEXTURE*IMAGE SET "+ HOCKEY_BASE1 + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Base2*TEXTURE*IMAGE SET "+ HOCKEY_BASE2 + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Text1*TEXTURE*IMAGE SET "+ HOCKEY_TEXT1 + match.getHomeTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Text2*TEXTURE*IMAGE SET "+ HOCKEY_TEXT2 + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Text1$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Text2$txt_Name*GEOM*TEXT SET "+ hs.getJersey_number() +"\0");
					
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(GK)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)(GK)" +"\0");
					}else {
						String role = hs.getRole().trim();
						String shortRole = "";

						if (role.equalsIgnoreCase("Goalkeeper")) shortRole = "GK";
						else if (role.equalsIgnoreCase("Defender") || role.equalsIgnoreCase("Deferder")) shortRole = "DF";
						else if (role.equalsIgnoreCase("Midfielder")) shortRole = "MF";
						else if (role.equalsIgnoreCase("Forward")) shortRole = "FW";

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						        + "$Players$Player" + row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET " + shortRole + "\0");
						
					}
				}
				

				
				for(Formation form : formation) {
					if(form.getFormId() == match.getHomeTeamFormationId()) {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						
					}
				}
				
			}else if(TeamId == match.getAwayTeamId()){
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$LOGO_ANIM"
						+ "$noname$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						+ "$Head_Coach_Out$Header$txt_Header*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName1() +"\0");
				
				for(int i = 0; i <= match.getAwaySubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Substitute_Players*FUNCTION*Grid*num_col SET " + row_id_sub + " \0");
					
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) { 
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4() +  "\\\\"  +match.getAwaySubstitutes().get(i).getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4() +  "\\\\"  +match.getAwaySubstitutes().get(i).getPhoto() + ".png" +"\0");
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text1$txt_Name*GEOM*TEXT SET "+ match.getAwaySubstitutes().get(i).getFull_name().toUpperCase() +"\0");
				
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name*GEOM*TEXT SET "+ match.getAwaySubstitutes().get(i).getJersey_number() +"\0");
					
//					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
//							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name*GEOM*TEXT SET "+ "" +"\0");
					if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)" +"\0");
					}else if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(GK)" +"\0");
					}else if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)(GK)" +"\0");
					}else {
						String awaySubRole = match.getAwaySubstitutes().get(i).getRole().trim();
						String awaySubShortRole = "";

						if (awaySubRole.equalsIgnoreCase("Goalkeeper")) awaySubShortRole = "GK";
						else if (awaySubRole.equalsIgnoreCase("Defender") || awaySubRole.equalsIgnoreCase("Deferder")) awaySubShortRole = "DF";
						else if (awaySubRole.equalsIgnoreCase("Midfielder")) awaySubShortRole = "MF";
						else if (awaySubRole.equalsIgnoreCase("Forward")) awaySubShortRole = "FW";

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						        + "$Substitute_Players$Player" + row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET " + awaySubShortRole + "\0");
						
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Base1*TEXTURE*IMAGE SET "+ HOCKEY_BASE1 + match.getAwayTeam().getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Base2*TEXTURE*IMAGE SET "+ HOCKEY_BASE2 + match.getAwayTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text1*TEXTURE*IMAGE SET "+ HOCKEY_TEXT1 + match.getAwayTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2*TEXTURE*IMAGE SET "+ HOCKEY_TEXT2 + match.getAwayTeam().getTeamBadge() +"\0");
					
				
				}
				
				for(Player hs : match.getAwaySquad()) {
					row_id = row_id + 1;
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) { 
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4() +  "\\\\"  + hs.getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4() +  "\\\\"  + hs.getPhoto() + ".png" +"\0");
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Base1*TEXTURE*IMAGE SET "+ HOCKEY_BASE1 + match.getAwayTeam().getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Base2*TEXTURE*IMAGE SET "+ HOCKEY_BASE2 + match.getAwayTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Text1*TEXTURE*IMAGE SET "+ HOCKEY_TEXT1 + match.getAwayTeam().getTeamBadge() +"\0");	
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Text2*TEXTURE*IMAGE SET "+ HOCKEY_TEXT2 + match.getAwayTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Text1$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
							+ "$Players$Player"+ row_id + "$img_Text2$txt_Name*GEOM*TEXT SET "+ hs.getJersey_number() +"\0");
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(GK)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)(GK)" +"\0");
					}else {
						String role = hs.getRole().trim();
						String shortRole = "";

						if (role.equalsIgnoreCase("Goalkeeper")) shortRole = "GK";
						else if (role.equalsIgnoreCase("Defender") || role.equalsIgnoreCase("Deferder")) shortRole = "DF";
						else if (role.equalsIgnoreCase("Midfielder")) shortRole = "MF";
						else if (role.equalsIgnoreCase("Forward")) shortRole = "FW";

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						        + "$Players$Player" + row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET " + shortRole + "\0");
						
					}
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getAwayTeamFormationId()) {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						
					}
				}
			}
			
			previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$Substitutes 2.540 anim_FullFrame$In_Out$Substitutes$InOut 2.540 anim_FullFrame$In_Out$Substitutes$InOut$In 2.100 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		}
	}
	public void populateBugsDB(Socket session_socket,String viz_scene, Bugs bug ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			if(bug.getText1() != null && bug.getText2() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText1().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + bug.getText2().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "" +";");
				
			}else if(bug.getText1() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText1().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "" +";");
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText2().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + "" +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "" +";");
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 32.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateScoreUpdateFF(Socket session_socket,String viz_scene,HockeyService hockeyService,Match match,Clock clock, 
			String session_selected_broadcaster,int whichSide) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			String previewCommand="";
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 6 \0");
			
			
			
			for(VariousText vt : hockeyService.getVariousTexts()) {
				if(vt.getVariousType().equalsIgnoreCase("MATCHSUMMARYHEADER") && vt.getUseThis().equalsIgnoreCase("YES")) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
							+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ vt.getVariousText() +"\0");
				}else {
					if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "QUARTER 1" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "QUARTER 2" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "QUARTER 3" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "QUARTER 4" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "HALF TIME" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "FULL TIME" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "EXTRA TIME 1" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "EXTRA TIME 2" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("END1")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "END OF QUARTER 1" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("END3")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
								+ "$Score_Out$Status$txt_Status*GEOM*TEXT SET "+ "END OF QUARTER 3" +"\0");
					}
				}
			}
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
					+ "$Score_Out$Score$txt_Score*GEOM*TEXT SET "+ match.getHomeTeamScore() + " - "+ match.getAwayTeamScore() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
					+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
					+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() +"\0");
			
			
			for(int i=1;i<=4;i++) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
						+ "$Goals_Left_Out$txt_Goal"+ i +"*GEOM*TEXT SET "+ "" +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
						+ "$Goals_Right_Out$txt_Goal"+ i +"*GEOM*TEXT SET "+ "" +"\0");
			}
			
			
			
			int l=4;
			String h1="",h2="",h3="",a1="",a2="",a3="";
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			List<Integer> plyr_ids = new ArrayList<Integer>();
			boolean plyr_exist = false;
 			String stats_txt = "",stats_txt_og = "";
			
			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
				
				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY))) {
					
					plyr_exist = false;
					for(Integer plyr_id : plyr_ids) {
						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) 
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY))) {
							plyr_exist = true;
							break;
						}
					}

					if(plyr_exist == false) {
						plyr_ids.add(match.getMatchStats().get(i).getPlayerId());
						stats_txt = hockeyService.getPlayer(HockeyUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							HockeyFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								HockeyFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL)
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY))) {

								stats_txt = stats_txt + "," + 
										HockeyFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ HockeyFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}
						switch (HockeyFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case HockeyUtil.HOME:
							home_stats.add(stats_txt);
							break;
						case HockeyUtil.AWAY:
							away_stats.add(stats_txt);
							break;
						}
					}
				}else if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL)) {
					stats_txt_og = hockeyService.getPlayer(HockeyUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							HockeyFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
							HockeyFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						/*for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL))) {

								stats_txt_og = stats_txt_og + "," + 
									FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ FootballFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}*/
						switch (HockeyFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case HockeyUtil.HOME:
							home_stats.add(stats_txt_og);
							break;
						case HockeyUtil.AWAY:
							away_stats.add(stats_txt_og);
							break;
						}
				}
			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i < 2) { 
					h1 = h1 + home_stats.get(i); 
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
							+ "$Goals_Left_Out$txt_Goal1*GEOM*TEXT SET "+ h1 +"\0");
				}else if(i < 4) {
					h2 = h2 + home_stats.get(i);
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
							+ "$Goals_Left_Out$txt_Goal2*GEOM*TEXT SET "+ h2 +"\0");
				}else if(i < 6){
					h3 = h3 + home_stats.get(i);
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
							+ "$Goals_Left_Out$txt_Goal3*GEOM*TEXT SET "+ h3 +"\0");
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i < 2) { 
					a1 = a1 + away_stats.get(i); 
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
							+ "$Goals_Right_Out$txt_Goal1*GEOM*TEXT SET "+ a1 +"\0");
				}else if(i < 4) {
					a2 = a2 + away_stats.get(i);
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
							+ "$Goals_Right_Out$txt_Goal2*GEOM*TEXT SET "+ a2 +"\0");
				}else if(i < 6){
					a3 = a3 + away_stats.get(i);
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Summary$Data"
							+ "$Goals_Right_Out$txt_Goal3*GEOM*TEXT SET "+ a3 +"\0");
				}
			}
			
			previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$Match_Summary 2.540 anim_FullFrame$In_Out$Match_Summary$In_Out 2.540 anim_FullFrame$In_Out$Match_Summary$In_Out$In 2.100 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		}
	}
	public void populateScoreUpdate(Socket session_socket,String viz_scene,HockeyService hockeyService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l=4;
			String h1="",h2="",h3="",a1="",a2="",a3="";
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Team_Grp$txt_Name"
					+ "*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName2() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Team_Grp$txt_Name02"
					+ "*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName3() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Team_Grp$txt_Name"
					+ "*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName2() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Team_Grp$txt_Name02"
					+ "*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName3() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$LOGO_ANIM$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$LOGO_ANIM$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
					+ match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
					+ match.getHomeTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
					+ match.getAwayTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
					+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$Center$txt_Score"
					+ "*GEOM*TEXT SET "+ match.getHomeTeamScore() + "-" + match.getAwayTeamScore() +"\0");
			
			if(match.getHomeTeamScore() == 0 ) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Select_data"
						+ "*FUNCTION*Omo*vis_con SET 0\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getHomeTeamScore() > 0 && match.getHomeTeamScore() <= 2) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Select_data"
						+ "*FUNCTION*Omo*vis_con SET 1\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getHomeTeamScore() > 2 && match.getHomeTeamScore() <= 4) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Select_data"
						+ "*FUNCTION*Omo*vis_con SET 2\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Select_data"
						+ "*FUNCTION*Omo*vis_con SET 3\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			if(match.getAwayTeamScore() == 0 ) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Select_data"
						+ "*FUNCTION*Omo*vis_con SET 0\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getAwayTeamScore() > 0 && match.getAwayTeamScore() <= 2) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Select_data"
						+ "*FUNCTION*Omo*vis_con SET 1\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getAwayTeamScore() > 2 && match.getAwayTeamScore() <= 4) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Select_data"
						+ "*FUNCTION*Omo*vis_con SET 2\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Select_data"
						+ "*FUNCTION*Omo*vis_con SET 3\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			List<Integer> plyr_ids = new ArrayList<Integer>();
			boolean plyr_exist = false;
 			String stats_txt = "",stats_txt_og = "";
			
			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
				
				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY))) {
					
					plyr_exist = false;
					for(Integer plyr_id : plyr_ids) {
						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) 
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY))) {
							plyr_exist = true;
							break;
						}
					}

					if(plyr_exist == false) {
						plyr_ids.add(match.getMatchStats().get(i).getPlayerId());
						stats_txt = hockeyService.getPlayer(HockeyUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							HockeyFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								HockeyFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL)
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY))) {

								stats_txt = stats_txt + "," + 
										HockeyFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ HockeyFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}
						switch (HockeyFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case HockeyUtil.HOME:
							home_stats.add(stats_txt);
							break;
						case HockeyUtil.AWAY:
							away_stats.add(stats_txt);
							break;
						}
					}
				}else if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL)) {
					stats_txt_og = hockeyService.getPlayer(HockeyUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							HockeyFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
							HockeyFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						/*for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL))) {

								stats_txt_og = stats_txt_og + "," + 
									FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ FootballFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}*/
						switch (HockeyFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case HockeyUtil.HOME:
							home_stats.add(stats_txt_og);
							break;
						case HockeyUtil.AWAY:
							away_stats.add(stats_txt_og);
							break;
						}
				}
			}
			
//			for(int i=0;i<=home_stats.size()-1;i++) {
//				if(i < 2) { 
//					h1 = h1 + home_stats.get(i); 
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers1 " + h1 + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 4) {
//					h2 = h2 + home_stats.get(i);
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers2 " + h2 + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 6){
//					h3 = h3 + home_stats.get(i);
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers3 " + h3 + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//				}
//			}
//			
//			for(int i=0;i<=away_stats.size()-1;i++) {
//				if(i < 2) { 
//					a1 = a1 + away_stats.get(i); 
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers1 " + a1 + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 4) {
//					a2 = a2 + away_stats.get(i);
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers2 " + a2 + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 6){
//					a3 = a3 + away_stats.get(i);
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers3 " + a3 + ";");
//					TimeUnit.MILLISECONDS.sleep(l);
//				}
//			}
			
			for (int i = 0; i < home_stats.size() && i < 6; i += 2) {
			    int slot = (i / 2) + 1; // Data1, Data2, Data3
			    String first  = home_stats.get(i);
			    String second = (i + 1 < home_stats.size()) ? home_stats.get(i + 1) : "";

			    String base = "-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Data" + slot;

			    print_writer.println(base + "$txt_Info01*GEOM*TEXT SET " + first + "\0");
			    print_writer.println(base + "$txt_Info02*GEOM*TEXT SET " + second + "\0");
			    print_writer.flush();

			    TimeUnit.MILLISECONDS.sleep(l);
			}
			
			for (int i = 0; i < away_stats.size() && i < 6; i += 2) {
			    int slot = (i / 2) + 1; // Data1, Data2, Data3
			    String first  = away_stats.get(i);
			    String second = (i + 1 < away_stats.size()) ? away_stats.get(i + 1) : "";

			    String base = "-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Data" + slot;

			    print_writer.println(base + "$txt_Info01*GEOM*TEXT SET " + first + "\0");
			    print_writer.println(base + "$txt_Info02*GEOM*TEXT SET " + second + "\0");
			    print_writer.flush();

			    TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_LT_ScoreLine$Essentials 1.140 anim_LT_ScoreLine$Essentials$In_Out 1.140 "
					+ "anim_LT_ScoreLine$Essentials$In_Out$In 0.880 \0");
		}
	}
	public void populateMatchStatus(Socket session_socket,String viz_scene,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, CsvException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l = 4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getMatchIdent().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					HockeyUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					HockeyUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getClock().getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getClock().getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "FIRST HALF" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "SECOND HALF" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "EXTRA TIME 1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "EXTRA TIME 2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead1 " + HockeyUtil.SHOTS + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead2 " + HockeyUtil.SHOTS_ON_TARGET.replace("_", " ") + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead3 " + HockeyUtil.RED + " CARDS" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead4 " + HockeyUtil.YELLOW + " CARDS" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead5 " + "TACKLES" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead6 " + "OFFSIDES" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead7 " + HockeyUtil.CORNERS + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead8 " + "POSSESSION (%)" + ";");
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 100.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchPromo(Socket session_socket,String fixt,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			String match_name="",newDate = "",date_data = "",text = "";
			
			String[] dateSuffix = {
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
					
					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
					
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
					
					"th", "st"
			};
			
			Calendar cal_bengal = Calendar.getInstance();
			cal_bengal.add(Calendar.DATE, +1);
			
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Vs*GEOM*TEXT SET vs\0");
		
			for(Fixture fixtures :fixture) {
				if(fixtures.getMatchnumber() == Integer.valueOf(fixt)) {
					if(fixtures.getMatchnumber() > 10) {
						match_name = fixtures.getMatchfilename();
					}else {
						match_name = "MATCH " + (fixtures.getMatchnumber()-1);
					}
					
					if(fixtures.getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal_bengal.getTime()))) {
						
						text = "TOMORROW - " + ground.get(fixtures.getVenue() - 1).getFullname();
						
					}else {
						cal_bengal.add(Calendar.DATE, -1);
						if(fixtures.getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal_bengal.getTime()))) {
							text = "UP NEXT - " + match_name;
						}else {
							newDate = fixtures.getDate().split("-")[0];
							if(Integer.valueOf(newDate) < 10) {
								newDate = newDate.replaceFirst("0", "");
							}
							date_data = newDate + dateSuffix[Integer.valueOf(newDate)] + " " + 
									Month.of(Integer.valueOf(fixtures.getDate().split("-")[1]));
							
							text = date_data + " - " + ground.get(fixtures.getVenue() - 1).getFullname();
							
						}
					}
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
							+ team.get(fixtures.getHometeamid() - 1).getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
							+ team.get(fixtures.getHometeamid() - 1).getTeamBadge() +"\0");
					
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
							+ team.get(fixtures.getAwayteamid() - 1).getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
							+ team.get(fixtures.getAwayteamid() - 1).getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$LOGO_ANIM$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
							+ team.get(fixtures.getHometeamid() - 1).getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$LOGO_ANIM$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
							+ team.get(fixtures.getAwayteamid() - 1).getTeamBadge() +"\0");
					
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$Team_Grp$txt_Name*GEOM*TEXT SET " 
							+ team.get(fixtures.getHometeamid() - 1).getTeamName2() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$Team_Grp$txt_Name02*GEOM*TEXT SET " 
							+ team.get(fixtures.getHometeamid() - 1).getTeamName3() +"\0");
					
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$Team_Grp$txt_Name*GEOM*TEXT SET " 
							+ team.get(fixtures.getAwayteamid() - 1).getTeamName2() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$Team_Grp$txt_Name02*GEOM*TEXT SET " 
							+ team.get(fixtures.getAwayteamid() - 1).getTeamName3() +"\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Info*GEOM*TEXT SET " 
							+ text +"\0");
				}
			}
		}
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_Ident$Essentials 1.140 anim_Ident$Essentials$In_Out 1.140 "
				+ "anim_Ident$Essentials$In_Out$In 0.880 \0");
		
	}
	public void populateMatchDoublePromo(Socket session_socket,String day,Match match,List<Fixture> fix,List<Team> team,List<Ground> ground, String session_selected_broadcaster,int whichSide) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			String previewCommand="";
			System.out.println("DAyyyyyyyyy" +    day);
			int row_id = 1, row = 0;
			String Date = "", match_name = "", newDate = "";

			Calendar cal = Calendar.getInstance();

			String[] dateSuffix = { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",

					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",

					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",

					"th", "st" };
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 8 \0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
					+ "$Match1$Date_Out*ACTIVE SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
					+ "$Match2$Date_Out*ACTIVE SET 0\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
					+ "$Match1$Venue_Out$txt_Location*GEOM*TEXT SET "+ "INTERNATIONAL HOCKEY STADIUM" +"\0");
			
			
			
			if (day.toUpperCase().equalsIgnoreCase("TODAY")) {
				Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
						+ "$Match1$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "SARDAR VALLABHBHAI PATEL" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
						+ "$Match2$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "UP NEXT" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
						+ "$Match2$Venue_Out$txt_Location*GEOM*TEXT SET "+ "LIVE FROM RAIPUR, CHHATTISGARH" +"\0");
				
			} else if (day.toUpperCase().equalsIgnoreCase("TOMORROW")) {
				cal.add(Calendar.DATE, +1);
				Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
						+ "$Match1$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "SARDAR VALLABHBHAI PATEL" +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
						+ "$Match2$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "TOMORROW'S MATCHES" +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
						+ "$Match2$Venue_Out$txt_Location*GEOM*TEXT SET "+ "FROM RAIPUR, CHHATTISGARH" +"\0");

			} else if (day.toUpperCase().equalsIgnoreCase("DAY_AFTER_TOMORROW")) {
				cal.add(Calendar.DATE, +2);
				Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());

				newDate = Date.split("-")[0];
				if (Integer.valueOf(newDate) < 10) {
					newDate = newDate.replaceFirst("0", "");
				}
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
						+ "$Match1$Venue_Out$txt_Venue*GEOM*TEXT SET "+ newDate + dateSuffix[Integer.valueOf(newDate)] + " "
						+ Month.of(Integer.valueOf(Date.split("-")[1])) +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Side" + whichSide + "$FF_Gfx$Match_Id_Double"
						+ "$Match2$Venue_Out$txt_Venue*GEOM*TEXT SET "+ newDate + dateSuffix[Integer.valueOf(newDate)] + " "
						+ Month.of(Integer.valueOf(Date.split("-")[1])) +"\0");
			}

			for (int i = 0; i <= fix.size() - 1; i++) {
				if (fix.get(i).getDate().equalsIgnoreCase(Date)) {
					row = row + 1;
					
					print_writer.println(
							"-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side1$Match_Id_Double$Match" + row
									+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET " + logo_path
									+ fix.get(i).getHome_Team().getTeamBadge() + "\0");
					
					print_writer.println(
							"-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side1$Match_Id_Double$Match" + row
									+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET " + logo_path
									+ fix.get(i).getAway_Team().getTeamBadge() + "\0");
					
					
					
					if( fix.get(i).getMatchfilename().contains("FINAL")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "FINAL  " +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "" +"\0");
						
					}else if(fix.get(i).getMatchfilename().contains("SEMI FINAL 1")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "S-FINAL   " +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "1" +"\0");
					}else if(fix.get(i).getMatchfilename().contains("SEMI FINAL 2")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "S-FINAL   " +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "2" +"\0");
					}else if(fix.get(i).getMatchfilename().contains("EXHIBITION")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "WOMENS   " +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ "" +"\0");
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Match$Druk-Trial-Medium*GEOM*TEXT SET "+ "MATCH" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id_Double$Match" + row
								+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ (fix.get(i).getMatchnumber()-1) +"\0");
					}
					row_id = row_id + 1;
				}
			}
			previewCommand = "anim_FullFrame$In_Out 2.540 anim_FullFrame$In_Out$Match_Id_Double 2.540 anim_FullFrame$In_Out$Match_Id_Double$In_Out 2.540 anim_FullFrame$In_Out$Match_Id_Double$In_Out$In 2.100 "
					+ "anim_FullFrame$In_Out$BG 2.540 anim_FullFrame$In_Out$BG$In_Out 2.540 anim_FullFrame$In_Out$BG$In_Out$In 1.730";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Fullframes " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		}
	}
	public void populateStaff(Socket session_socket,String viz_scene, Staff st,List<Team> team ,Match match, String selectedbroadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Name_Out$txt_Name*GEOM*TEXT SET " 
					+ st.getName().toUpperCase() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ team.get(st.getClubId() - 1).getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Designation_Out$txt_Designation*GEOM*TEXT SET " 
					+ st.getRole().toUpperCase() + ", " + team.get(st.getClubId() - 1).getTeamName1().toUpperCase() +"\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_LowerThird$Essentials 1.140 anim_LowerThird$Essentials$In_Out 1.140 "
					+ "anim_LowerThird$Essentials$In_Out$In 0.880 \0");
		}
	}
	public void populateMatchStats(Socket session_socket,String viz_scene,HockeyService hockeyService, Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "HOCKEY" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubHeader 2;");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + match.getMatchIdent() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "HOCKEY" + 
					HockeyUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vVSOptions 0;");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam01 " + match.getHomeTeam().getTeamName1() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam02 " + match.getAwayTeam().getTeamName1() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScoreHome " + match.getHomeTeamScore() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScoreAway " + match.getAwayTeamScore() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo " + "LIVE FROM " + match.getVenueName().toUpperCase() + ";");
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchPromoSingle(Socket session_socket,String viz_sence_path, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {			
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l =4;
			String grounds = "";
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + fix.get(match_number - 1).getMatchfilename().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + TM.getTeamName4().toUpperCase() + HockeyUtil.PNG_EXTENSION + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName "+ TM.getTeamName1().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + TM.getTeamName4().toUpperCase() + HockeyUtil.PNG_EXTENSION + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName "+ TM.getTeamName1().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			Calendar cal = Calendar.getInstance();
			//cal.add(Calendar.DATE, +1);
			if(fix.get(match_number - 1).getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()))) {
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fix.get(match_number - 1).getVenue())) {
						grounds = ground.get(j).getFullname();
					}
				}
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "UP NEXT " + "(" + fix.get(match_number - 1).getTime() + ") - LIVE FROM " + grounds + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fix.get(match_number - 1).getVenue())) {
						grounds = ground.get(j).getFullname();
					}
				}
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + fix.get(match_number - 1).getDate() + " (" + fix.get(match_number - 1).getTime() + ") - LIVE FROM " + grounds + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 196.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
				
		}
	}
	public void populatePointsTable(Socket session_socket,String viz_sence_path,List<LeagueTeam> point_table1,List<LeagueTeam> point_table2, List<Team> team,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		int row_no=0,l=4;
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "HOCKEY POINTS TABLE" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vTableType 0;");
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + "GROUP A" + ";");
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText02 " + "GROUP B" + ";");
		
		print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "HOCKEY" + 
				HockeyUtil.PNG_EXTENSION + ";");
		
		for(int i = 0; i <= point_table1.size() - 1 ; i++) {
			row_no = row_no + 1;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam0" + row_no + " " + point_table1.get(i).getTeamName() + ";");
			if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tQ0" + row_no + " " + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tQ0" + row_no + " " + "(Q)" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
				
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer0" + row_no + " " + point_table1.get(i).getPlayed() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tWon0" + row_no + " " + point_table1.get(i).getWon() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tLost0" + row_no + " " + point_table1.get(i).getDrawn() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTied0" + row_no + " " + point_table1.get(i).getLost() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPTS0" + row_no + " " + point_table1.get(i).getPoints() + ";");
			TimeUnit.MILLISECONDS.sleep(l);

		}
		
		for(int j = 0; j <= point_table2.size() - 1 ; j++) {
			row_no = row_no + 1;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam0" + row_no + " " + point_table2.get(j).getTeamName() + ";");
			if(point_table2.get(j).getQualifiedStatus().trim().equalsIgnoreCase("")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tQ0" + row_no + " " + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tQ0" + row_no + " " + "(Q)" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
				
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer0" + row_no + " " + point_table2.get(j).getPlayed() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tWon0" + row_no + " " + point_table2.get(j).getWon() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tLost0" + row_no + " " + point_table2.get(j).getDrawn() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTied0" + row_no + " " + point_table2.get(j).getLost() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPTS0" + row_no + " " + point_table2.get(j).getPoints() + ";");
			TimeUnit.MILLISECONDS.sleep(l);

		}
		print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Infobar*CONTAINER SET ACTIVE 0;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
		print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
		TimeUnit.SECONDS.sleep(1);
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Infobar*CONTAINER SET ACTIVE 1;");
		print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		
	}
	public void populateOfficials(Socket session_socket,String viz_scene,List<Officials> officials,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);

			int l=4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName1 " + officials.get(0).getReferee().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName2 " + officials.get(0).getFourthOfficial().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName3 " + officials.get(0).getAssistantReferee2().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName4 " + officials.get(0).getAssistantReferee1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 50.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateLtPenalty(Socket session_socket,String viz_scene,String valueToProcess,HockeyService hockeyService,Match match,Clock clock, String session_selected_broadcaster) 
			throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l=4;
			int iHomeCont = 0, iAwayCont = 0;
			
			for(int i=1; i<=5; i++) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(i)
				+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(i)
				+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
			}
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
					+ match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
					+ match.getHomeTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
					+ match.getAwayTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
					+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$LOGO_ANIM$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$LOGO_ANIM$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");

			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Team_Grp$txt_Name"
					+ "*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName2() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Team_Grp$txt_Name02"
					+ "*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName3() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Team_Grp$txt_Name"
					+ "*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName2() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Team_Grp$txt_Name02"
					+ "*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName3() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$Center$txt_Score"
					+ "*GEOM*TEXT SET "+ match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() +"\0");
			
			for(String pen : penalties)
			{
				if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 2\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 1\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 2\0");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 1\0");
				}
				
				
				if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(iHomeCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(iAwayCont)
					+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_LT_Penalty$Essentials 1.140 anim_LT_Penalty$Essentials$In_Out 1.140 "
					+ "anim_LT_Penalty$Essentials$In_Out$In 0.800\0");
		}
	}
	public void populatePenLtMatchId(Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
					+ match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
					+ match.getHomeTeam().getTeamBadge() +"\0");
			
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2
					+ match.getAwayTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1
					+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$LOGO_ANIM$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$LOGO_ANIM$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$Team_Grp$txt_Name*GEOM*TEXT SET " 
					+ match.getHomeTeam().getTeamName2() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$HomeTeam$Team_Grp$txt_Name02*GEOM*TEXT SET " 
					+ match.getHomeTeam().getTeamName3() +"\0");
			
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$Team_Grp$txt_Name*GEOM*TEXT SET " 
					+ match.getAwayTeam().getTeamName2() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$AwayTeam$Team_Grp$txt_Name02*GEOM*TEXT SET " 
					+ match.getAwayTeam().getTeamName3() +"\0");
			
			for(VariousText vt : hockeyserv.getVariousTexts()) {
				if(vt.getVariousType().equalsIgnoreCase("LT_IDENTPENALTY") && vt.getUseThis().equalsIgnoreCase("Yes")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Info*GEOM*TEXT SET " 
							+ vt.getVariousText() +"\0");
				}else {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Info*GEOM*TEXT SET " 
							+ "LIVE FROM " + match.getVenueName() +"\0");
				}
			}
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Vs*GEOM*TEXT SET " 
					+ match.getHomeTeamScore() + "-" + match.getAwayTeamScore() +"\0");
		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays  C:/Temp/Preview.tga anim_Ident$Essentials 1.140 anim_Ident$Essentials$In_Out 1.140 "
				+ "anim_Ident$Essentials$In_Out$In 0.880 \0");
	}
	
	public void populateResetPenalty(Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null"; 
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			for(int i=1; i<=5; i++) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(i)
				+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(i)
				+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
			}
		}
	}
	// inside HOCKEY_KHELO_INDIA class

	public static class Quarter {
	    public String name;
	    public int home;
	    public int away;

	    public Quarter(String name, int home, int away) {
	        this.name = name;
	        this.home = home;
	        this.away = away;
	    }
	}
	

	public static List<Quarter> readQuarters(String filePath) throws IOException {
	    List<Quarter> quarters = new ArrayList<>();
	    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            line = line.trim();
	            if (line.isEmpty()) break;   // blank line -> stop reading
	            String[] p = line.split(",");
	            if (p.length < 3) continue;
	            quarters.add(new Quarter(
	                    p[1].trim(),
	                    Integer.parseInt(p[0].trim()),
	                    Integer.parseInt(p[2].trim())));
	        }
	    }
	    return quarters;
	}
	public static String getQuarterName(int number) {
	    String[] names = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth"};
	    if (number >= 1 && number <= names.length) {
	        return names[number - 1];
	    }
	    return number + "th"; // fallback for numbers beyond 10
	}
}