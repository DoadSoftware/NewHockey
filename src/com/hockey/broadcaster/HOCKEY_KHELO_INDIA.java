package com.hockey.broadcaster;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.text.SimpleDateFormat;
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
import com.fasterxml.jackson.databind.ObjectMapper;

public class HOCKEY_KHELO_INDIA extends Scene{
	
	public String session_selected_broadcaster = "HOCKEY_KHELO_INDIA";
	
	public PrintWriter print_writer;
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "",which_stats_on_screen = "";
	public boolean is_infobar = false;
	private String logo_path = "C:\\Images\\KHELO_INDIA\\Logos\\";
	//private String colors_path = "C:\\Images\\KHELO_INDIA\\Colours\\";
	private String icon_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_Khelo_India_2023\\Icons\\";
	private String status;
	private String slashOrDash = "-";
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	private static ObjectMapper objectMapper  = new ObjectMapper();
	
	public final static String HOCKEY_LOGO = "IMAGE*/Default/Essentials/Logos/";
	public final static String HOCKEY_BASE1 = "IMAGE*/Default/Essentials/Base1/";
	public final static String HOCKEY_BASE2 = "IMAGE*/Default/Essentials/Base2/";
	public final static String HOCKEY_TEXT1 = "IMAGE*/Default/Essentials/Text1/";
	public final static String HOCKEY_TEXT2 = "IMAGE*/Default/Essentials/Text2/";
	public final static String HOCKEY_ICONS = "IMAGE*/Default/Essentials/Icons/";
	public final static String HOCKEY_LOCAL_PHOTO_PATH = "C:\\\\Images\\\\BAN_AFG_SERIES\\\\Photos\\\\";
	public final static String HOCKEY_PHOTO_PATH = "\\c\\Images\\BAN_AFG_SERIES\\Photos\\";
	public final static String HOCKEY_SPONSOR = "IMAGE*/Default/Essentials/Spnsor/";
	
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
			/*if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				System.out.println("hello");
				penalties = new ArrayList<String>();
			}*/
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");
			for(String pen : penalties)
			{	
				if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");
					
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penaltiesremove = new ArrayList<String>();
//					penalties.remove(penalties.size() - 1);
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
		} else {
			if(penalties == null) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
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
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
					
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
			
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
					
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
		case "POPULATE-L3-SCOREUPDATE": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-L3-NAMESUPER-CARD": case "POPULATE-L3-SUBSTITUTE": case "POPULATE-L3-MATCHPROMO":
		case "POPULATE-L3-STAFF": case "POPULATE-FF-MATCHSTATS": case "POPULATE-FF-PROMO": case "POPULATE-DOUBLE_PROMO": case "POPULATE-POINTS_TABLE": 
		case "POPULATE-OFFICIALS": case "POPULATE-PENALTY": case "POPULATE-L3-SINGLE_SUBSTITUTE": case "POPULATE-SCOREBUG_STATS": case "POPULATE-SCOREBUG-CARD":
		case "POPULATE-SCOREBUG-GOAL_SCORER": case "POPULATE-LT-MATCHID":
//			switch(whatToProcess.toUpperCase()) {
//			//case "POPULATE-SCOREBUG_STATS": case "POPULATE-RED_CARD": case "POPULATE-EXTRA_TIME": case "POPULATE-SPONSOR": case "POPULATE-SCOREBUG_STATS_TWO":
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
			case "POPULATE-L3-NAMESUPER":
				for(NameSuper ns : hockeyService.getNameSupers()) {
					  if(ns.getNamesuperId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateNameSuper(session_socket, valueToProcess.split(",")[1], ns, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-NAMESUPER-PLAYER":
				populateNameSuperPlayer(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-NAMESUPER-CARD":
				populateNameSuperCard(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHID":
				populateMatchId(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-MATCHSTATS":
				populateMatchStats(session_socket,valueToProcess.split(",")[1], hockeyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI":
				populatePlayingXI(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),hockeyService.getFormations(), 
						hockeyService.getTeams(),match, session_selected_broadcaster);
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
			case "POPULATE-L3-MATCHPROMO":
				populateMatchPromo(session_socket, valueToProcess.split(",")[1], match,hockeyService.getFixtures(),hockeyService.getTeams(),hockeyService.getGrounds(), session_selected_broadcaster);
				break;
			case "POPULATE-FF-PROMO":
				populateMatchPromoSingle(session_socket, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),hockeyService.getTeams(),
						hockeyService.getFixtures(),hockeyService.getGrounds(),match , session_selected_broadcaster);
				break;
			case "POPULATE-DOUBLE_PROMO":
				populateMatchDoublePromo(session_socket, valueToProcess.split(",")[1], match,hockeyService.getFixtures(),hockeyService.getTeams(),hockeyService.getGrounds(), session_selected_broadcaster);
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
		case "PROMO_GRAPHICS-OPTIONS":
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
				break;
			case "PLAYER_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Penalty_Card CONTINUE\0");
				break;
			case "GOAL_SCORER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Goal_Scorer CONTINUE\0");
				break;	
			}
			break;
		case "ANIMATE-IN-SCOREBUG": 
		case "CLEAR-ALL": 
		case "ANIMATE-OUT":
		case "ANIMATE-IN-SPONSOR": case "ANIMATE-OUT-SPONSOR": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-NAMESUPER": 
		case "ANIMATE-IN-NAMESUPERDB": case "ANIMATE-IN-BUG-DB": case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-NAMESUPER_CARD":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-MATCHPROMO": case "ANIMATE-IN-STAFF": case "ANIMATE-IN-MATCHSTATS": case "ANIMATE-IN-PROMO": 
		case "ANIMATE-IN-DOUBLE_PROMO": case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-IN-POINTS_TABLE": case "ANIMATE-IN-OFFICIALS":case "ANIMATE-IN-PENALTY": 
		case "ANIMATE-IN-SINGLE_SUBSTITUTE": case "ANIMATE-IN-SCOREBUG_STATS": case "ANIMATE-IN-SCOREBUG-CARD": case "ANIMATE-IN-SCOREBUG-GOAL_SCORER":
		case "ANIMATE-IN-LT_MATCHID":
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-LT_MATCHID":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ident$Essentials START\0");
				which_graphics_onscreen = "LT_MATCHID";
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
			case "ANIMATE-IN-PENALTY":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PENALTY";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_lowerthird_Player$Essentials START\0");
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-NAMESUPERDB":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials START\0");
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-MATCHID":
//				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-MATCHSTATS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PLAYINGXI":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-BUG-DB":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "BUG-DB";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				TimeUnit.MILLISECONDS.sleep(500);
				if(match.getHomeTeamScore() > 0 || match.getAwayTeamScore() > 0) {
					if(match.getHomeTeamScore() > 4 || match.getAwayTeamScore() > 4) {
						processAnimation(session_socket, "Scorer3Line_In", "START", session_selected_broadcaster, 2);
					}else if(match.getHomeTeamScore() > 2 || match.getAwayTeamScore() > 2) {
						processAnimation(session_socket, "Scorer2Line_In", "START", session_selected_broadcaster, 2);
					}else {
						processAnimation(session_socket, "Scorer1Line_In", "START", session_selected_broadcaster, 2);
					}
				}
				which_graphics_onscreen = "SCOREUPDATE";
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
			case "ANIMATE-IN-MATCHPROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHPROMO";
				break;
			case "ANIMATE-IN-STAFF":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials START\0");
				which_graphics_onscreen = "STAFF";
				break;
			case "ANIMATE-IN-PROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PROMO";
				break;
			case "ANIMATE-IN-DOUBLE_PROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "DOUBLE_PROMO";
				break;
			case "ANIMATE-IN-POINTS_TABLE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "POINTS_TABLE";
				break;
			case "ANIMATE-IN-SCOREBUG":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Essentials START\0");
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
			case "ANIMATE-IN-SPONSOR":
				processAnimation(session_socket, "SponsorIn", "START", session_selected_broadcaster,1);
				break;
			case "ANIMATE-IN-OFFICIALS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "OFFICIALS";
				break;
			case "CLEAR-ALL":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_lowerthird_Player SHOW 0.0\0");
				which_graphics_onscreen = "";
				break;
			
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Essentials CONTINUE\0");
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
				}
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
				case "MATCHID": case "PLAYINGXI": case "POINTS_TABLE": case "MATCHSTATS":
					processAnimation(session_socket, "In", "CONTINUE", session_selected_broadcaster,2);
					which_graphics_onscreen = "";
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
				case "BUG-DB": case "SCOREUPDATE": case "MATCHSTATUS": case "NAMESUPER_CARD":
				case "SUBSTITUTE": case "MATCHPROMO": case "PROMO": case "DOUBLE_PROMO": case "OFFICIALS": 
				case "PENALTY": case "SINGLE_SUBSTITUTE": 
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials CONTINUE\0");
					which_graphics_onscreen = "";
					break;	
				}
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

	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String viz_sence_path,Match match, String selectedbroadcaster) throws IOException
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
			}
		}
		return scorebug;
	}
	
	public ScoreBug populateScoreBugStats(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String whichstats,String homedata,String awaydata,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
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
		}
		return scorebug;
	}
	
	public ScoreBug populateScoreBugCard(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,int whichTeam,String WhichCard,int whichPlayer,String showphoto,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
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
										match.getHomeTeam().getTeamBadge() + "\\\\" + hs.getPhoto() + ".png" + "\0");
								
							}else {
								
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
										match.getHomeTeam().getTeamBadge() + "\\\\" + hs.getPhoto() + ".png" + "\0");
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
										match.getHomeTeam().getTeamBadge() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
								
							}else {
								
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
										match.getHomeTeam().getTeamBadge() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
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
										match.getHomeTeam().getTeamBadge() + "\\\\" + as.getPhoto() + ".png" + "\0");
								
							}else {
								
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
										match.getHomeTeam().getTeamBadge() + "\\\\" + as.getPhoto() + ".png" + "\0");
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
										match.getHomeTeam().getTeamBadge() + "\\\\" + asub.getPhoto() + ".png" + "\0");
								
							}else {
								
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
								print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
										match.getHomeTeam().getTeamBadge() + "\\\\" + asub.getPhoto() + ".png" + "\0");
							}
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 1\0");
						}else {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Penalty_Card$Away_Team$Player_Image$Select_image*FUNCTION*Omo*vis_con SET 0\0");
						}
					}
				}
			}
		}
		return scorebug;
	}
	
	public ScoreBug populateScoreBugScorer(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,int whichTeam,int whichPlayer,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			if(whichTeam == match.getHomeTeamId()) {
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Select_Team*FUNCTION*Omo*vis_con SET 1\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
						match.getHomeTeam().getTeamBadge() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
						match.getHomeTeam().getTeamBadge() + "\0");
				
				for(Player hs : match.getHomeSquad()) {
					if(whichPlayer == hs.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + hs.getTicker_name() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
					}
				}
				
				for(Player hsub : match.getHomeSubstitutes()) {
					if(whichPlayer == hsub.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + hsub.getTicker_name() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + hsub.getJersey_number() + "\0");
					}
				}
			}else {
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Select_Team*FUNCTION*Omo*vis_con SET 2\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
						match.getAwayTeam().getTeamBadge() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
						match.getAwayTeam().getTeamBadge() + "\0");
				
				for(Player as : match.getAwaySquad()) {
					if(whichPlayer == as.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + as.getTicker_name() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PC*GEOM*TEXT SET " + as.getJersey_number() + "\0");
					}
				}
				
				for(Player asub : match.getAwaySubstitutes()) {
					if(whichPlayer == asub.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + asub.getTicker_name() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$txt_PC*GEOM*TEXT SET " + asub.getJersey_number() + "\0");
					}
				}
			}
		}
		return scorebug;
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
			
			for(VariousText vt : hockeyserv.getVariousTexts()) {
				if(vt.getVariousType().equalsIgnoreCase("LT_IDENT") && vt.getUseThis().equalsIgnoreCase("Yes")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Info*GEOM*TEXT SET " 
							+ vt.getVariousText() +"\0");
				}else {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Ident$Center$txt_Info*GEOM*TEXT SET " 
							+ "LIVE FROM " + match.getVenueName() +"\0");
				}
			}
		}
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
		
	}
	public void populateNameSuperPlayer(Socket session_socket,String viz_scene, int TeamId, String captainGoalKeeper, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			String Home_or_Away="";
			int l = 4;
			
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(TeamId == match.getHomeTeamId()) {
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Team_Logo$img_Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
						+ match.getHomeTeam().getTeamBadge() +"\0");
				
				Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						
						if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
									match.getHomeTeam().getTeamBadge() + "\\\\" + hs.getPhoto() + ".png" + "\0");
							
						}else {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
									match.getHomeTeam().getTeamBadge() + "\\\\" + hs.getPhoto() + ".png" + "\0");
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
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
									match.getHomeTeam().getTeamBadge() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
							
						}else {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
									match.getHomeTeam().getTeamBadge() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
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
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
									match.getAwayTeam().getTeamBadge() + "\\\\" + as.getPhoto() + ".png" + "\0");
							
						}else {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
									match.getAwayTeam().getTeamBadge() + "\\\\" + as.getPhoto() + ".png" + "\0");
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
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
									match.getAwayTeam().getTeamBadge() + "\\\\" + asub.getPhoto() + ".png" + "\0");
							
						}else {
							
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird_Player$Main_Logo$Logo*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
									match.getAwayTeam().getTeamBadge() + "\\\\" + asub.getPhoto() + ".png" + "\0");
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
		}
	}
	public void populateNameSuperCard(Socket session_socket,String viz_scene, int TeamId, String cardType, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
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
			case HockeyUtil.YELLOW:
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case HockeyUtil.RED:
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 92.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
		}
	}
	public void populateMatchId(Socket session_socket,String viz_scene, Match match, String session_selected_broadcaster,int whichSide,HockeyService hockeyService) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$FF_Gfx*FUNCTION*Omo*vis_con SET 1 \0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Match_Number_Out$Number$txt_Match_Number*GEOM*TEXT SET "+ match.getMatchIdent() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET "+ match.getHomeTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET "+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Date_Out*ACTIVE SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "DEHRADUN" +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
					+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+ "IT PARK" +"\0");
			
			
			for(VariousText vt : hockeyService.getVariousTexts()) {
				if(vt.getVariousType().equalsIgnoreCase("MATCHIDENTFOOTER") && vt.getUseThis().equalsIgnoreCase("YES") ) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
							+ "$Venue_Out$txt_Venue*GEOM*TEXT SET "+ "DEHRADUN" +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Match_Id$Data"
							+ "$Venue_Out$txt_Location*GEOM*TEXT SET "+ "IT PARK" +"\0");
				}
			}
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
	
	public void populatePlayingXI(Socket session_socket,String viz_scene, int TeamId,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int i,row_id = 0,row_id_sub=0,l=4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "HOCKEY" + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead01 " + "STARTING XI" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead02 " + "SUBSTITUTES" + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "HOCKEY" + 
					HockeyUtil.PNG_EXTENSION + ";");
			
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + match.getHomeTeam().getTeamName1() + ";");
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer0"+ row_id + " " + hs.getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(C)" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(GK)" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(C)(GK)" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				for(int k=1;k<=11;k++) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubVisibility0"+ k + " " + "0" + ";");
				}
				
				for(i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubVisibility0"+ row_id_sub + " " + "1" + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubs0"+ row_id_sub + " " + match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole0"+ row_id_sub + " " + "(GK)" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole0"+ row_id_sub + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}else if(TeamId == match.getAwayTeamId()){
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + match.getAwayTeam().getTeamName1() + ";");
				
				for(Player hs : match.getAwaySquad()) {
					row_id = row_id + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer0"+ row_id + " " + hs.getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(C)" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(GK)" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + "(C)(GK)" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole0"+ row_id + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				for(int k=1;k<=11;k++) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubVisibility0"+ k + " " + "0" + ";");
				}
				
				for(i = 0; i <= match.getAwaySubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubVisibility0"+ row_id_sub + " " + "1" + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubs0"+ row_id_sub + " " + match.getAwaySubstitutes().get(i).getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole0"+ row_id_sub + " " + "(GK)" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole0"+ row_id_sub + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
			
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
			TimeUnit.SECONDS.sleep(1);
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
	public void populateScoreUpdate(Socket session_socket,String viz_scene,HockeyService hockeyService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l=4;
			String h1="",h2="",h3="",a1="",a2="",a3="";
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					HockeyUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					HockeyUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.HALF)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + clock.getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.FULL)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + clock.getMatchHalves().toUpperCase() + " TIME"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.FIRST)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "FIRST HALF"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.SECOND)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "SECOND HALF"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "EXTRA TIME 1"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "EXTRA TIME 2"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getHomeTeamScore() == 0 ) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "0" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getHomeTeamScore() > 0 && match.getHomeTeamScore() <= 2) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getHomeTeamScore() > 2 && match.getHomeTeamScore() <= 4) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "3" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			if(match.getAwayTeamScore() == 0 ) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "0" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getAwayTeamScore() > 0 && match.getAwayTeamScore() <= 2) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getAwayTeamScore() > 2 && match.getAwayTeamScore() <= 4) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "3" + ";");
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
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i < 2) { 
					h1 = h1 + home_stats.get(i); 
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers1 " + h1 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					h2 = h2 + home_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers2 " + h2 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					h3 = h3 + home_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers3 " + h3 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i < 2) { 
					a1 = a1 + away_stats.get(i); 
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers1 " + a1 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					a2 = a2 + away_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers2 " + a2 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					a3 = a3 + away_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers3 " + a3 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 58.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
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
	public void populateMatchPromo(Socket session_socket,String viz_scene,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int count = 0 , count1 = 0,row_id = 0,l = 4 ;
			String groun = "";
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "UPCOMING MATCHES" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(Fixture fx :fixture) {
				if(match.getMatchFileName().replace(".xml", "").equalsIgnoreCase(fx.getMatchfilename())) {
					count = fx.getMatchnumber();
					count1 = count + 4;
				}
			}
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vMatches " + "1" + ";");
			
			for(;count < count1;) {
			//if(count < count1) {
				row_id = row_id + 1;
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeLogo" + row_id + " " + logo_path + team.get(fixture.get(count).getHometeamid() - 1).getTeamName4().toUpperCase() + 
						HockeyUtil.PNG_EXTENSION+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayLogo" + row_id + " " + logo_path + team.get(fixture.get(count).getAwayteamid() - 1).getTeamName4().toUpperCase() + 
						HockeyUtil.PNG_EXTENSION+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName" + row_id + " " + team.get(fixture.get(count).getHometeamid() - 1).getTeamName1().toUpperCase() + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName" + row_id + " " + team.get(fixture.get(count).getAwayteamid() - 1).getTeamName1().toUpperCase() + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fixture.get(count).getVenue())) {
						groun = ground.get(j).getCity();
					}
				}
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo" + row_id + " " + fixture.get(count).getMatchfilename().toUpperCase() + " - " + fixture.get(count).getDate() + " (" + fixture.get(count).getTime() + ") LIVE FROM " + groun + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				count = count + 1;
				
			}
			
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 85.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
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
					+ st.getRole().toUpperCase() + "," + team.get(st.getClubId() - 1).getTeamName1().toUpperCase() +"\0");
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
	public void populateMatchDoublePromo(Socket session_socket,String viz_scene,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int row_id = 1 ,l=4;
			String Date = "",grou = "";
			Calendar cal = Calendar.getInstance();
			
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "TOMORROW'S MATCHES" + ";");
			
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vMatches " + "0" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int i = 0; i <= fixture.size()-1; i++) {
				if(fixture.get(i).getDate().equalsIgnoreCase(Date)) {
					for(int j = 0; j <= ground.size()-1; j++) {
						if(ground.get(j).getGroundId() == Integer.valueOf(fixture.get(i).getVenue())) {
							grou = ground.get(j).getFullname();
						}
					}
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeLogo" + row_id + " " + logo_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toUpperCase() + 
							HockeyUtil.PNG_EXTENSION+ ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayLogo" + row_id + " " + logo_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toUpperCase() + 
							HockeyUtil.PNG_EXTENSION+ ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamFirstName" + row_id + " " + team.get(fixture.get(i).getHometeamid() - 1).getTeamName2().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamLastName" + row_id + " " + team.get(fixture.get(i).getHometeamid() - 1).getTeamName3().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamFirstName" + row_id + " " + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName2().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamLastName" + row_id + " " + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName3().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo" + row_id + " " + fixture.get(i).getMatchfilename().toUpperCase() + " - " + "(" + fixture.get(i).getTime() + ") LIVE FROM " + grou + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					row_id = row_id +1;
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 85.0;");
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
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					HockeyUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					HockeyUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(String pen : penalties)
			{
				if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.INCREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 2" + ";");
				}
				
				
				if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.HOME + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(HockeyUtil.AWAY + "_" + HockeyUtil.DECREMENT + "_" + "PENALTIES" + "_" + HockeyUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 40.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
}