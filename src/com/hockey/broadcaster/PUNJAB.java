package com.hockey.broadcaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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

public class PUNJAB extends Scene{
	
	public String session_selected_broadcaster = "PUNJAB";
	
	@JsonIgnore
	public PrintWriter print_writer;
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "",which_stats_on_screen = "",sponsor_on_screen = "",red_card_on_screen = "",referral_on_screen = "",vtp = "";
	public int whichSide = 1;
	public boolean is_infobar = false;
	private String eventlogo_path = "IMAGE*/Default/EventLogo";
	//private String colors_path = "C:\\Images\\KHELO_INDIA\\Colours\\";
	private String icon_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_Khelo_India_2023\\Icons\\";
	public String status;
	private String slashOrDash = "-",hbase = "",htext = "",abase = "",atext = "";
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	private static ObjectMapper objectMapper  = new ObjectMapper();
	public List<Fixture> fixTures;
	public Fixture fixture;
	
	public int HomePreviousScore = 0, HomeCurrentScore = 0,AwayPreviousScore = 0, AwayCurrentScore = 0;
	
	public final static String HOCKEY_LOGO = "IMAGE*/Default/Assests/Flags/";
	public final static String HOCKEY_BASE1 = "IMAGE*/Default/Assests/Base1/";
	public final static String HOCKEY_BASE2 = "IMAGE*/Default/Assests/Base2/";
	public final static String HOCKEY_TEXT1 = "IMAGE*/Default/Assests/Text1/";
	public final static String HOCKEY_TEXT2 = "IMAGE*/Default/Assests/Text2/";
	public final static String HOCKEY_ICONS = "IMAGE*/Default/Assests/Icons/";
	public final static String HOCKEY_JERSEY = "IMAGE*/Default/Assests/Jersey/";
	public final static String HOCKEY_SPONSOR = "IMAGE*/Default/Assests/Spnsor/";
	public final static String HOCKEY_LOCAL_PHOTO_PATH = "C:\\\\Images\\\\Hockey\\\\Photos\\\\";
	public final static String HOCKEY_PHOTO_PATH = "\\c\\Images\\Hockey\\Photos\\";
	private static final String MATCHES_FOLDER = "C:/Sports/Hockey/Matches";
	
	private boolean homeScoreChanged = false;
	private boolean awayScoreChanged = false;
	
	public PUNJAB() {
		super();
	}
	
	public ScoreBug updateScoreBug(List<Scene> scenes, Match match, Socket session_socket) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true,scorebug, session_socket, scenes.get(0).getScene_path(),match, session_selected_broadcaster,1);
		}
		
//		if (scorebug.isScorebug_on_screen()) {
//		    String scenePath = scenes.get(0).getScene_path();
//
//		    // detect change BEFORE populateScoreBug updates the previous scores
//		    boolean scoreChanged = match.getHomeTeamScore() != HomePreviousScore
//		                        || match.getAwayTeamScore() != AwayPreviousScore;
//
//		    if (scoreChanged) {
//		        // side 2 gets new value, animation fires
//		        scorebug = populateScoreBug(true, scorebug, session_socket, scenePath, match, session_selected_broadcaster, 2);
//
//		        TimeUnit.MILLISECONDS.sleep(1500);
//
//		        // side 1 gets new value
//		        scorebug = populateScoreBug(true, scorebug, session_socket, scenePath, match, session_selected_broadcaster, 1);
//
//		        // reset only the animations that played
//		        if (homeScoreChanged) {
//		            print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Change$Score$Team1 SHOW 0.0\0");
//		            homeScoreChanged = false;
//		        }
//		        if (awayScoreChanged) {
//		            print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Change$Score$Team2 SHOW 0.0\0");
//		            awayScoreChanged = false;
//		        }
//		    } else {
//		        // no score change: single refresh on the on-air side (period, referral etc.), no side swap, no animation
//		        scorebug = populateScoreBug(true, scorebug, session_socket, scenePath, match, session_selected_broadcaster, 1);
//		    }
//		}
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
		case "POPULATE-FF-MATCHID_IMAGE": case "POPULATE-SCORECARD": case "POPULATE-FF-HEADTOHEAD_DATA": case "POPULATE-FF-POINTS_EXCEL_DATA":
		case "POPULATE-LAST_GOAL":	case "POPULATE-RED_CARD": case "POPULATE-QUARTER": case "POPULATE-LT-PENALTY_SCORES": case "POPULATE-FF-TOP_SCORER_EXCEL_DATA":
		case "POPULATE-FF_PROMO":	case "POPULATE-FF-MATCHSTAT": case "POPULATE-CHANGE_PENALTY": case "POPULATE-SCORETEAM": case "POPULATE-TOP_TXT_DATA":
		case "POPULATE-FF-PLAYINGXI_WITH_SUBSANDCOACH": case "POPULATE-HIGHLIGHT": case "POPULATE-PLAYING-CHANGE":
	
			
		case "HOME_YELLOW1": case "HOME_YELLOW2": case "HOME_RED1": case "HOME_RED2": case "HOME_GREEN1": case "HOME_GREEN2":
		case "AWAY_YELLOW1": case "AWAY_YELLOW2": case "AWAY_RED1": case "AWAY_RED2": case "AWAY_GREEN1": case "AWAY_GREEN2":
			
		case "HOME_YELLOW1_OUT": case "HOME_YELLOW2_OUT": case "HOME_RED1_OUT": case "HOME_RED2_OUT": case "HOME_GREEN1_OUT": case "HOME_GREEN2_OUT":
		case "AWAY_YELLOW1_OUT": case "AWAY_YELLOW2_OUT": case "AWAY_RED1_OUT": case "AWAY_RED2_OUT": case "AWAY_GREEN1_OUT": case "AWAY_GREEN2_OUT":

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
			case "HOME_YELLOW1": case "HOME_YELLOW2": case "HOME_RED1": case "HOME_RED2": case "HOME_GREEN1": case "HOME_GREEN2":
			case "AWAY_YELLOW1": case "AWAY_YELLOW2": case "AWAY_RED1": case "AWAY_RED2": case "AWAY_GREEN1": case "AWAY_GREEN2":
				populateScoreCard(session_socket,match, session_selected_broadcaster,hockeyService,whatToProcess);
				break;
			case "HOME_YELLOW1_OUT": case "HOME_YELLOW2_OUT": case "HOME_RED1_OUT": case "HOME_RED2_OUT": case "HOME_GREEN1_OUT": case "HOME_GREEN2_OUT":
			case "AWAY_YELLOW1_OUT": case "AWAY_YELLOW2_OUT": case "AWAY_RED1_OUT": case "AWAY_RED2_OUT": case "AWAY_GREEN1_OUT": case "AWAY_GREEN2_OUT":
				populateScoreCardOut(session_socket,match, session_selected_broadcaster,hockeyService,whatToProcess);
				break;
			case "POPULATE-SCORETEAM":
				populateScorebugTeams(session_socket,match, session_selected_broadcaster,hockeyService,Integer.valueOf(valueToProcess.split(",")[1]));
				break;
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
				populateScoreBug(false,scorebug,session_socket, valueToProcess.split(",")[1],match, session_selected_broadcaster,1);
				break;
			case "POPULATE-LT-MATCHID":
				populateLtMatchId(session_socket, match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-LT-PENALTY_SCORES":
				populatePenLtMatchId(session_socket, match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-HIGHLIGHT":
				populateHighlight(session_socket, match, session_selected_broadcaster,hockeyService);
				break;
			case "POPULATE-TOP_TXT_DATA":
				writeScorersFile("C:/Sports/Hockey/Matches", "C:/Sports/Hockey/TopScorers.txt");
				break;
			case "POPULATE-L3-NAMESUPER":
				for(NameSuper ns : hockeyService.getNameSupers()) {
					  if(ns.getNamesuperId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateNameSuper(session_socket, valueToProcess.split(",")[1], ns, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-NAMESUPER-PLAYER":
				if(!which_graphics_onscreen.isEmpty()) {
					whichSide = 2;
				}else {
					whichSide = 1;
				}
				vtp = valueToProcess;
				
				populateNameSuperPlayer(session_socket, whichSide,valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), 
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
			case "POPULATE-FF-HEADTOHEAD_DATA":
				populateMatchHeadToHead(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-POINTS_EXCEL_DATA":
				populateMatchPointsExcelData(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-TOP_SCORER_EXCEL_DATA":
				populateTopScorerExcelData(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF_PROMO":
				populateFFMatchPromo(session_socket,Integer.valueOf(valueToProcess.split(",")[1]), match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-MATCHSTATS":
				populateMatchStats(session_socket,valueToProcess.split(",")[1], hockeyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI":
				if(!which_graphics_onscreen.isEmpty()) {
					whichSide = 2;
				}else {
					whichSide = 1;
				}
				vtp = valueToProcess;
				populatePlayingXI(session_socket, Integer.valueOf(valueToProcess.split(",")[1]),hockeyService.getFormations(), 
						hockeyService.getTeams(),match, session_selected_broadcaster,whichSide,hockeyService);
				break;
			case "POPULATE-FF-PLAYINGXIBOTHTEAMS":
				populatePlayingXIBOTH(session_socket,hockeyService.getFormations(), 
						hockeyService.getTeams(),match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-PLAYINGXISUB":
				populatePlayingXISUB(session_socket, Integer.valueOf(valueToProcess.split(",")[1]),hockeyService.getFormations(), 
						hockeyService.getTeams(),match, session_selected_broadcaster,1,hockeyService);
				break;
			case "POPULATE-FF-PLAYINGXI_WITH_SUBSANDCOACH":
				populatePlayingXISUBAndCoach(session_socket, Integer.valueOf(valueToProcess.split(",")[1]),
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
				populateSubstitute(session_socket, Integer.valueOf(valueToProcess.split(",")[1]),
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
				if(!which_graphics_onscreen.isEmpty()) {
					whichSide = 2;
				}else {
					whichSide = 1;
				}
				vtp = valueToProcess;
				populateMatchDoublePromo(session_socket, valueToProcess.split(",")[1], match,HockeyFunctions.processAllFixtures(hockeyService),hockeyService.getTeams(),
						hockeyService.getGrounds(), session_selected_broadcaster,whichSide);
				break;
			case "POPULATE-POINTS_TABLE":
				LeagueTable league_table1 = null;
				if(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTable" + ".XML").exists()) {
					league_table1 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTable" + ".XML"));
				}
				
				populatePointsTable(session_socket, valueToProcess.split(",")[1],league_table1.getLeagueTeams(),"",
						hockeyService.getTeams(),session_selected_broadcaster,match,1);
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
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType*FUNCTION*Omo*vis_con SET 1\0");
			if(referral_on_screen == "SOCIAL") {
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker_Info CONTINUE\0");
				referral_on_screen = "";
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$01=Social_Media$txt_Info1*GEOM*TEXT SET @asiahockey\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$01=Social_Media$txt_Info2*GEOM*TEXT SET asia_hockey\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker_Info START\0");
				referral_on_screen = "SOCIAL";
			}
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
			case "GOAL_SCORER": case "LAST_GOAL":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker_Info CONTINUE\0");
				break;	
			}
			which_stats_on_screen = "";
			break;
		case "ANIMATE-OUT-RED_CARD":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Cards CONTINUE\0");
			break;
		case "ANIMATE-OUT-SCORETEAM":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker_Info CONTINUE\0");
			break;
		case "ANIMATE-IN-HIGHLIGHT":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_BugMatchScore START\0");
			which_graphics_onscreen = "HIGHLIGHT";
			break;
		case "ANIMATE-IN-SCOREBUG": 
		case "CLEAR-ALL": 
		case "ANIMATE-OUT":
			/* case "ANIMATE-IN-SPONSOR": */ case "ANIMATE-OUT-SPONSOR": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-NAMESUPER": 
		case "ANIMATE-IN-NAMESUPERDB": case "ANIMATE-IN-BUG-DB": case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-NAMESUPER_CARD":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-LTPROMO": case "ANIMATE-IN-STAFF": case "ANIMATE-IN-MATCHSTATS": case "ANIMATE-IN-PROMO": 
		case "ANIMATE-IN-DOUBLE_PROMO": case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-IN-POINTS_TABLE": case "ANIMATE-IN-OFFICIALS":case "ANIMATE-IN-PENALTY": 
		case "ANIMATE-IN-SINGLE_SUBSTITUTE": case "ANIMATE-IN-SCOREBUG_STATS": case "ANIMATE-IN-SCOREBUG-CARD": case "ANIMATE-IN-SCOREBUG-GOAL_SCORER":
		case "ANIMATE-IN-PLAYINGXIBOTHTEAMS":	case "ANIMATE-IN-MATCHID_IMAGE": case "ANIMATE-IN-HEADTOHEAD_DATA": case "ANIMATE-IN-POINTS_EXCEL_DATA":
		case "ANIMATE-IN-LT_MATCHID": case "ANIMATE-IN-PLAYINGXISUB": case "ANIMATE-IN-MATCHSUMMARY": case "ANIMATE-IN-SCOREBUG-LAST_GOAL":
		case "ANIMATE-IN-SCOREBUG-RED_CARD": case "ANIMATE-IN-QUARTER": case "ANIMATE-IN-PENALTY_SCORES":
		case "ANIMATE-IN-SINGLEPROMO": case "ANIMATE-IN-MATCHSTAT": case "ANIMATE-IN-REFERRAL": case "ANIMATE-IN-SCORECARD":
		case "ANIMATE-IN-FF-PLAYINGXI_WITH_SUBSANDCOACH": case "ANIMATE-IN-TOP_SCORER_EXCEL_DATA":

			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-SCORECARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker_Info START\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*TeamLoop START\0");
				break;
			case "ANIMATE-IN-REFERRAL":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Refferals START\0");
				
				break;
			case "ANIMATE-IN-QUARTER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Quaters$Essentials START\0");
				which_graphics_onscreen = "QUARTER";
				break;
			case "ANIMATE-IN-SCOREBUG-RED_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Cards START\0");
				red_card_on_screen = "RED_CARD";
				break;
			case "ANIMATE-IN-LT_MATCHID": case "ANIMATE-IN-PENALTY_SCORES": case "ANIMATE-IN-LTPROMO":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_MatchID START\0");
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
				which_graphics_onscreen = "LT_MATCHID";
				break;
			case "ANIMATE-IN-SCOREBUG-LAST_GOAL":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker_Info START\0");
				which_stats_on_screen = "LAST_GOAL";
				break;
			case "ANIMATE-IN-SCOREBUG-GOAL_SCORER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker_Info START\0");
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
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Penalty$Essentials START\0");
				which_graphics_onscreen = "PENALTY";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				
				if(whichSide == 2) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Change_Lowerthird START\0");
					TimeUnit.MILLISECONDS.sleep(2000);
					
					populateNameSuperPlayer(session_socket, whichSide,vtp.split(",")[1], Integer.valueOf(vtp.split(",")[2]), 
							vtp.split(",")[3], Integer.valueOf(vtp.split(",")[4]),vtp.split(",")[5],vtp.split(",")[6], match, session_selected_broadcaster);
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lowerthird SHOW 1.240\0");
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Change_Lowerthird SHOW 0.0\0");
					
				}else {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lowerthird START\0");
				}
				
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-NAMESUPERDB": case "ANIMATE-IN-STAFF":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_UmpireSuper START\0");
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-MATCHID":
//				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Ident$In_Out START \0");
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
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out START \0");
				which_graphics_onscreen = "MATCHSTAT";
				break;
			case "ANIMATE-IN-HEADTOHEAD_DATA":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out START \0");
				which_graphics_onscreen = "HEADTOHEAD_DATA";
				break;
			case "ANIMATE-IN-POINTS_EXCEL_DATA": case "ANIMATE-IN-TOP_SCORER_EXCEL_DATA":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out START \0");
				which_graphics_onscreen = "POINTS_EXCEL_DATA";
				break;
			case "ANIMATE-IN-SINGLEPROMO":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Ident$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "SINGLEPROMO";
				break;
			case "ANIMATE-IN-MATCHSTATS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PLAYINGXI":
				
				if(whichSide == 2) {
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*change_Formation START\0");
					TimeUnit.MILLISECONDS.sleep(2000);
					
					populatePlayingXI(session_socket, Integer.valueOf(vtp.split(",")[1]),hockeyService.getFormations(), 
							hockeyService.getTeams(),match, session_selected_broadcaster,1,hockeyService);
					
					
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Essentials$In SHOW 1.960\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Essentials$Out SHOW 1.960\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Header$In SHOW 1.960\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Header$Out SHOW 1.960\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Players$In SHOW 1.960\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Players$Out SHOW 1.960\0");
					
					TimeUnit.MILLISECONDS.sleep(500);
					
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*change_Formation SHOW 0.0\0");
				}else {
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*change_Formation SHOW 0.0\0");
					
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Essentials$In START \0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Essentials$Out START \0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Header$In START \0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Header$Out START \0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Players$In START \0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Players$Out START \0");
				}
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-PLAYINGXISUB":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrame$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "PLAYINGXISUB";
				break;
			case "ANIMATE-IN-FF-PLAYINGXI_WITH_SUBSANDCOACH":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out START \0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				which_graphics_onscreen = "PLAYINGXI_WITH_SUBSANDCOACH";
				break;
	
			case "ANIMATE-IN-BUG-DB":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "BUG-DB";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_MatchID START\0");
				TimeUnit.MILLISECONDS.sleep(500);
				
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-MATCHSUMMARY":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out START \0");
				which_graphics_onscreen = "MATCHSUMMARY";
				break;
			case "ANIMATE-IN-MATCHSTATUS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATUS";
				break;
			case "ANIMATE-IN-SUBSTITUTE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lt_LineUp START\0");
				which_graphics_onscreen = "SUBSTITUTE";
				break;
			case "ANIMATE-IN-SINGLE_SUBSTITUTE":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SINGLE_SUBSTITUTE";
				break;
			case "ANIMATE-IN-PROMO":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PROMO";
				break;
			case "ANIMATE-IN-DOUBLE_PROMO":
				
				if(whichSide == 2) {
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*change_Fullframes START\0");
					TimeUnit.MILLISECONDS.sleep(2000);
					
					populateMatchDoublePromo(session_socket, vtp.split(",")[1], match,HockeyFunctions.processAllFixtures(hockeyService),hockeyService.getTeams(),
							hockeyService.getGrounds(), session_selected_broadcaster,1);
					
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out SHOW 1.800\0");
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*change_Fullframes SHOW 0.0\0");
				}else {
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out START\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Loop START \0");
				}
				
				which_graphics_onscreen = "DOUBLE_PROMO";
				break;
			case "ANIMATE-IN-POINTS_TABLE":
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out START \0");
				which_graphics_onscreen = "POINTS_TABLE";
				break;
				
			case "ANIMATE-IN-SCOREBUG":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker$In_Out START\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Refferals START\0");
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Loop START\0");
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
//			case "ANIMATE-IN-SPONSOR":
//				if(sponsor_on_screen.equalsIgnoreCase("SPONSOR")) {
//					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Sponsor CONTINUE\0");
//					sponsor_on_screen = "";
//				}else {
//					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Sponsor$Select_Sponsor*FUNCTION*Omo*vis_con SET 2\0");
//					
//					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_ScoreBug$Sponsor START\0");
//					sponsor_on_screen = "SPONSOR";
//				}
//				break;
			case "ANIMATE-IN-OFFICIALS":
				processAnimation(session_socket, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "OFFICIALS";
				break;
			case "CLEAR-ALL":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Change SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Refferals SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker_Info SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*TeamLoop SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Bug SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_BugMatchScore SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lt_LineUp SHOW 0.0\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_MatchID SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lowerthird SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Change_Lowerthird SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_UmpireSuper SHOW 0.0\0");

				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Ident$In_Out SHOW 0.0\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out SHOW 0.0\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*change_Fullframes SHOW 0.0\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out SHOW 0.0\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*change_Formation SHOW 0.0\0");
				which_graphics_onscreen = "";
				which_stats_on_screen = "";
				scorebug.setScorebug_on_screen(false);
				is_infobar = false;
				break;
			
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Ticker$In_Out CONTINUE\0");
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
					sponsor_on_screen = "";
				}
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
				
				case "HIGHLIGHT":
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_BugMatchScore CONTINUE\0");
					which_graphics_onscreen = "";
					break;
				 case "POINTS_TABLE": case "MATCHSTATS":  case "PLAYINGXISUB": case "MATCHSUMMARY": case "PLAYINGXI_WITH_SUBSANDCOACH":
				 case "MATCHSTAT": case "DOUBLE_PROMO": case "PLAYINGXIBOTHTEAMS": case "MATCHID_IMAGE": case "HEADTOHEAD_DATA": case "POINTS_EXCEL_DATA":
				//	processAnimation(session_socket, "In", "CONTINUE", session_selected_broadcaster,2);
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out CONTINUE \0");
					which_graphics_onscreen = "";
					TimeUnit.MILLISECONDS.sleep(1000);
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out SHOW 0.0\0");
					break;
				case "MATCHID": case "SINGLEPROMO":
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Ident$In_Out CONTINUE \0");
					which_graphics_onscreen = "";which_graphics_onscreen = "";
					break;
				case "PLAYINGXI":
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Essentials$Out CONTINUE \0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Header$Out CONTINUE \0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Players$Out CONTINUE \0");
					which_graphics_onscreen = "";
					TimeUnit.MILLISECONDS.sleep(1000);
					
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Essentials SHOW 0.0\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Header SHOW 0.0\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out$Players SHOW 0.0\0");
					break;
				
				case "NAMESUPERDB": case "STAFF":
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_UmpireSuper CONTINUE\0");
					which_graphics_onscreen = "";
					break;
				case "NAMESUPER":
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lowerthird CONTINUE\0");
					which_graphics_onscreen = "";
					break;
				case "LT_MATCHID":
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_MatchID CONTINUE\0");
					which_graphics_onscreen = "";
					break;
				case "QUARTER":
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Quaters$Essentials CONTINUE\0");
					which_graphics_onscreen = "";
					break;
				case "SCOREUPDATE":
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_MatchID CONTINUE\0");
					which_graphics_onscreen = "";
					break;
				case "PENALTY":	
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LT_Penalty$Essentials CONTINUE\0");
					which_graphics_onscreen = "";
					break;
				case "SUBSTITUTE":
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lt_LineUp CONTINUE\0");
					which_graphics_onscreen = "";
					break;
				case "BUG-DB": case "MATCHSTATUS": case "NAMESUPER_CARD":
				case "MATCHPROMO": case "PROMO": case "OFFICIALS": 
				case "SINGLE_SUBSTITUTE": 
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_LowerThird$Essentials CONTINUE\0");
					which_graphics_onscreen = "";
					break;	
				}
				
				TimeUnit.MILLISECONDS.sleep(1000);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_MatchID SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lowerthird SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Change_Lowerthird SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_UmpireSuper SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Lt_LineUp SHOW 0.0\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Bug SHOW 0.0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_BugMatchScore SHOW 0.0\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out SHOW 0.0\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Ident$In_Out SHOW 0.0\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_FullFrames$In_Out SHOW 0.0\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*anim_Formation$In_Out SHOW 0.0\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*change_Formation SHOW 0.0\0");

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
		case "PUNJAB":
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
	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String viz_sence_path, Match match, String selectedbroadcaster,int whichSide) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			HomeCurrentScore = match.getHomeTeamScore();
			AwayCurrentScore = match.getAwayTeamScore();
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			if(is_this_updating == false) {
				HomePreviousScore = match.getHomeTeamScore();
				AwayPreviousScore = match.getAwayTeamScore();
				
				homeScoreChanged = false;
				awayScoreChanged = false;
				
				if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
					hbase = HOCKEY_BASE1;
					htext = HOCKEY_TEXT1;
				}else if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
					hbase = HOCKEY_BASE2;
					htext = HOCKEY_TEXT2;
				}
				
				if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
					abase = HOCKEY_BASE1;
					atext = HOCKEY_TEXT1;
				}else if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
					abase = HOCKEY_BASE2;
					atext = HOCKEY_TEXT2;
				}
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Team1$img_Text*TEXTURE*IMAGE SET " 
						+ htext + match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Team1$img_Base*TEXTURE*IMAGE SET " 
						+ hbase + match.getHomeTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Team2$img_Text*TEXTURE*IMAGE SET " 
						+ atext + match.getAwayTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Team2$img_Base*TEXTURE*IMAGE SET " 
						+ abase + match.getAwayTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Team1$txt_TeamName*GEOM*TEXT SET " 
						+ match.getHomeTeam().getTeamName4() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Team2$txt_TeamName*GEOM*TEXT SET " 
						+ match.getAwayTeam().getTeamName4() +"\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$Referral$Team1$select_Refferal*FUNCTION*Omo*vis_con SET 1\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$Referral$Team2$select_Refferal*FUNCTION*Omo*vis_con SET 1\0");
			}
			
			
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Score$score$Team1$Side" + whichSide + "$txt_Score*GEOM*TEXT SET " 
					+ match.getHomeTeamScore() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Score$score$Team2$Side" + whichSide + "$txt_Score*GEOM*TEXT SET " 
					+ match.getAwayTeamScore() +"\0");
			
			// Independent checks: both teams can animate in the same update
//			if (HomePreviousScore != HomeCurrentScore) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Change$Score$Team1 START\0");
//				HomePreviousScore = HomeCurrentScore;
//				homeScoreChanged = true;
//			}
//			if (AwayPreviousScore != AwayCurrentScore) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Change$Score$Team2 START\0");
//				AwayPreviousScore = AwayCurrentScore;
//				awayScoreChanged = true;
//			}
			
			String[] referral = readReferral("C:\\Sports\\Hockey\\Referral.txt");
			String homeReferral = referral[0];
			String awayReferral = referral[1];
			
			if(homeReferral.equalsIgnoreCase("1")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$Referral$Team1$select_Refferal*FUNCTION*Omo*vis_con SET 1\0");
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$Referral$Team1$select_Refferal*FUNCTION*Omo*vis_con SET 2\0");
			}
			
			if(awayReferral.equalsIgnoreCase("1")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$Referral$Team2$select_Refferal*FUNCTION*Omo*vis_con SET 1\0");
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$Referral$Team2$select_Refferal*FUNCTION*Omo*vis_con SET 2\0");
			}
			
//			if (match.getMatchStats() != null) {
//				for (MatchStats ms : match.getMatchStats()) {
//					String type = ms.getStats_type();
//					int teamId = ms.getPlayer().getTeamId();
//					boolean isHome = teamId == match.getHomeTeamId();
//					boolean isAway = teamId == match.getAwayTeamId();
//
//					if (type.equalsIgnoreCase("yellow")) {
//						if (isHome) hyc++; else if (isAway) ayc++;
//					} else if (type.equalsIgnoreCase("red")) {
//						if (isHome) hrc++; else if (isAway) arc++;
//					} else if (type.equalsIgnoreCase("green")) {
//						if (isHome) hgc++; else if (isAway) agc++;
//					}
//				}
//			}
//			// ---- Play card animations only when count changes ----
//			playCard(print_writer, "Team1", "Green",  hgc);
//			playCard(print_writer, "Team1", "Yellow", hyc);
//			playCard(print_writer, "Team1", "Red",    hrc);
//
//			playCard(print_writer, "Team2", "Green",  agc);
//			playCard(print_writer, "Team2", "Yellow", ayc);
//			playCard(print_writer, "Team2", "Red",    arc);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET Q1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET Q2\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET Q3\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET Q4\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET HT\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET FT\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("end1")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET END Q1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("end2")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET END Q2\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET ET1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$ScoreTicker$ScoreTicker$Period$txt_Quarter*GEOM*TEXT SET ET2\0");
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
											match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamGKJerseyColor() + "\\\\" + hs.getPhoto() + ".png" + "\0");
									
								}else {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
											match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamGKJerseyColor() + "\\\\" + hs.getPhoto() + ".png" + "\0");
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
											match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamGKJerseyColor() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
									
								}else {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Home_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
											match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamGKJerseyColor() + "\\\\" + hsub.getPhoto() + ".png" + "\0");
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
											match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamGKJerseyColor() + "\\\\" + as.getPhoto() + ".png" + "\0");
									
								}else {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
											match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamGKJerseyColor() + "\\\\" + as.getPhoto() + ".png" + "\0");
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
											match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamGKJerseyColor() + "\\\\" + asub.getPhoto() + ".png" + "\0");
									
								}else {
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Away_Team$Player_Image$img_Player*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
											match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamGKJerseyColor() + "\\\\" + asub.getPhoto() + ".png" + "\0");
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
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
									|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION")) {
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
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
									|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION")) {
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
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
									|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION")) {
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
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + as.getTicker_name() + tim + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						}
					}
					
					for(Player asub : match.getAwaySubstitutes()) {
						for(int i = match.getMatchStats().size()-1; i >= 0; i--) {
							if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) || 
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL) ||
									match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
									|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION")) {
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
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PlayerName*GEOM*TEXT SET " + asub.getTicker_name() + tim + "\0");
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$txt_PC*GEOM*TEXT SET " + asub.getJersey_number() + "\0");
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
				
				
				for(int i =2; i<=5; i++) {
//					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$TEXT-" + i + "*ACTIVE SET 0\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$BG-" + i + "*ACTIVE SET 0\0");
				}
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType*FUNCTION*Omo*vis_con SET 0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$TEXT-1$select_Jersey*FUNCTION*Omo*vis_con SET 0\0");
				
				for(int i = match.getMatchStats().size()-1; i >= 0; i--) {
					if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) || 
							match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL) ||
							match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
							|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION")) {
						for(Player pl : hockeyserv.getAllPlayer()) {
							if(pl.getPlayerId() == match.getMatchStats().get(i).getPlayerId()) {
								if(match.getHomeTeamId() == pl.getTeamId()) {
									
									MatchStats stats = match.getMatchStats().get(i);   // use your actual type name
									String half = stats.getMatchHalves();
									long minute = quarterMinute(stats.getTotalMatchSeconds() / 1000);

									if (half.equalsIgnoreCase("quarter1")) {
									    time = minute;
									} else if (half.equalsIgnoreCase("quarter2")) {
									    time = 15 + minute;
									} else if (half.equalsIgnoreCase("quarter3")) {
									    time = 30 + minute;
									} else if (half.equalsIgnoreCase("quarter4")) {
									    time = 45 + minute;
									}

									tim = " (" + time + "')";
//									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Header$txt_Header*GEOM*TEXT SET LAST SCORER\0");
//									
//									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Select_Team*FUNCTION*Omo*vis_con SET 1\0");
//									
//									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
//											match.getHomeTeam().getTeamBadge() + "\0");
//									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Home_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
//											match.getHomeTeam().getTeamBadge() + "\0");
									
									if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
										hbase = HOCKEY_BASE1;
										htext = HOCKEY_TEXT1;
									}else if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
										hbase = HOCKEY_BASE2;
										htext = HOCKEY_TEXT2;
									}
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$TEXT-1$img_Text*TEXTURE*IMAGE SET " + htext + 
											match.getHomeTeam().getTeamBadge() + "\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$BG-1$img_Base*TEXTURE*IMAGE SET " + hbase + 
											match.getHomeTeam().getTeamBadge() + "\0");
									
//									if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter1")) {
//										time = (((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1);
//									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter2")) {
//										time = 15 + (((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1);
//									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter3")) {
//										time = 30 + (((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1);
//									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter4")) {
//										time = 45 + (((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1);
//									}
//									
//									tim = " (" + time + "')";
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$TEXT-1$txt_Text*GEOM*TEXT SET " + pl.getJersey_number() + " " + pl.getTicker_name() + tim + "\0");
								}else {
									MatchStats stats = match.getMatchStats().get(i);   // use your actual type name
									String half = stats.getMatchHalves();
									long minute = quarterMinute(stats.getTotalMatchSeconds() / 1000);

									if (half.equalsIgnoreCase("quarter1")) {
									    time = minute;
									} else if (half.equalsIgnoreCase("quarter2")) {
									    time = 15 + minute;
									} else if (half.equalsIgnoreCase("quarter3")) {
									    time = 30 + minute;
									} else if (half.equalsIgnoreCase("quarter4")) {
									    time = 45 + minute;
									}

									tim = " (" + time + "')";
									
//									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Header$txt_Header*GEOM*TEXT SET LAST SCORER\0");
//									
//									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Select_Team*FUNCTION*Omo*vis_con SET 2\0");
//									
//									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$img_Base1*TEXTURE*IMAGE SET " + HOCKEY_BASE1 + 
//											match.getAwayTeam().getTeamBadge() + "\0");
//									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreBug$Goal_Scorer$Away_Team$Player_Data$img_Base2*TEXTURE*IMAGE SET " + HOCKEY_BASE2 + 
//											match.getAwayTeam().getTeamBadge() + "\0");
									
									if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
										abase = HOCKEY_BASE1;
										atext = HOCKEY_TEXT1;
									}else if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
										abase = HOCKEY_BASE2;
										atext = HOCKEY_TEXT2;
									}
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$TEXT-1$img_Text*TEXTURE*IMAGE SET " + atext + 
											match.getAwayTeam().getTeamBadge() + "\0");
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$BG-1$img_Base*TEXTURE*IMAGE SET " + abase + 
											match.getAwayTeam().getTeamBadge() + "\0");
									
//									if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter1")) {
//										time = (((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1);
//									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter2")) {
//										time = 15 + (((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1);
//									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter3")) {
//										time = 30 + (((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1);
//									}else if(match.getMatchStats().get(i).getMatchHalves().equalsIgnoreCase("quarter4")) {
//										time = 45 + (((match.getMatchStats().get(i).getTotalMatchSeconds()/1000)/60) + 1);
//									}
//									
//									tim = " (" + time + "')";
									
									print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType$00=Info$TEXT-1$txt_Text*GEOM*TEXT SET " + pl.getJersey_number() + " " + pl.getTicker_name() + tim + "\0");
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
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$Sponsor*ACTIVE SET 0\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$Team1$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$Team2$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$txt_Team1*GEOM*TEXT SET " 
					+ match.getHomeTeam().getTeamName1() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$txt_Team2*GEOM*TEXT SET " 
					+ match.getAwayTeam().getTeamName1() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$ScoreGrp$txt_Score*GEOM*TEXT SET vs\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info*FUNCTION*Omo*vis_con SET 0\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET " 
					+ match.getMatchIdent() + " - FROM " + match.getVenueName() +"\0");
			
		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_MatchID$In_Out 1.240 anim_MatchID$In_Out$In 1.240 \0");
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
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line1$txt_Text1*GEOM*TEXT SET " 
						+ ns.getSurname().toUpperCase() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line1$txt_Text2*GEOM*TEXT SET \0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(ns.getSurname() == null) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line1$txt_Text1*GEOM*TEXT SET " 
						+ ns.getFirstname().toUpperCase() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line1$txt_Text2*GEOM*TEXT SET \0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line1$txt_Text1*GEOM*TEXT SET " 
						+ ns.getFirstname().toUpperCase() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line1$txt_Text2*GEOM*TEXT SET " + 
						ns.getSurname().toUpperCase() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
//			if(ns.getSponsor() != null) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
//						+ ns.getSponsor() +"\0");
//			}else {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_lowerthird$Main_Logo$Logo*TEXTURE*IMAGE SET " + HOCKEY_LOGO
//						+ "TLogo" +"\0");
//			}
			
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line2$txt_Info*GEOM*TEXT SET " + 
					ns.getSubLine().toUpperCase() + "\0");
			TimeUnit.MILLISECONDS.sleep(l);
		}
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_UmpireSuper$In_Out 1.000 anim_UmpireSuper$In_Out$In 1.000 \0");
		
	}
	public void populateNameSuperPlayer(Socket session_socket,int whichSide,String viz_scene, int TeamId, String captainGoalKeeper, int playerId,String cardType,String Photo, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			String Home_or_Away="";
			int l = 4;
			
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers*FUNCTION*Omo*vis_con SET 1\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Jersey$select_Jersey*FUNCTION*Omo*vis_con SET 1\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$select_TextType*FUNCTION*Omo*vis_con SET 1\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$select_TextType$Text2$select_Flag*FUNCTION*Omo*vis_con SET 1\0");
			
			if(TeamId == match.getHomeTeamId()) {
				
				if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
					hbase = HOCKEY_BASE1;
					htext = HOCKEY_TEXT1;
				}else if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
					hbase = HOCKEY_BASE2;
					htext = HOCKEY_TEXT2;
				}
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Text2$img_Flag*TEXTURE*IMAGE SET " 
						+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$img_Base*TEXTURE*IMAGE SET " 
						+ hbase + match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$txt_Info*TEXTURE*IMAGE SET " 
						+ htext + match.getHomeTeam().getTeamBadge() +"\0");
				
				Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Jersey$txt_Jersey*GEOM*TEXT SET " 
								+ hs.getJersey_number() +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Text2$txt_Name*GEOM*TEXT SET " 
								+ hs.getFull_name().toUpperCase() +"\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
									+ match.getHomeTeam().getTeamName1().toUpperCase() +"\0");
							
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
									+ hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() +"\0");
						}
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Jersey$txt_Jersey*GEOM*TEXT SET " 
								+ hsub.getJersey_number() +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Text2$txt_Name*GEOM*TEXT SET " 
								+ hsub.getFull_name().toUpperCase() +"\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
									+ match.getHomeTeam().getTeamName1().toUpperCase() +"\0");
							
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
									+ hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() +"\0");
						}
					}
				}
			}
			else {

				if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
					abase = HOCKEY_BASE1;
					atext = HOCKEY_TEXT1;
				}else if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
					abase = HOCKEY_BASE2;
					atext = HOCKEY_TEXT2;
				}
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Text2$img_Flag*TEXTURE*IMAGE SET " 
						+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$img_Base*TEXTURE*IMAGE SET " 
						+ abase + match.getAwayTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$txt_Info*TEXTURE*IMAGE SET " 
						+ atext + match.getAwayTeam().getTeamBadge() +"\0");
				
				Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Jersey$txt_Jersey*GEOM*TEXT SET " 
								+ as.getJersey_number() +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Text2$txt_Name*GEOM*TEXT SET " 
								+ as.getFull_name().toUpperCase() +"\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
									+ match.getAwayTeam().getTeamName1().toUpperCase() +"\0");
							
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
									+ as.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName4().toUpperCase() +"\0");
						}
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Jersey$txt_Jersey*GEOM*TEXT SET " 
								+ asub.getJersey_number() +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Data$Side" + whichSide + "$Text2$txt_Name*GEOM*TEXT SET " 
								+ asub.getFull_name().toUpperCase() +"\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
									+ match.getAwayTeam().getTeamName1().toUpperCase() +"\0");
							
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
									+ asub.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName4().toUpperCase() +"\0");
						}
					}
				}
			}
			
			switch(captainGoalKeeper.toUpperCase())
			{
			case "CAPTAIN":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
						+ captainGoalKeeper.toUpperCase() + " , " + Home_or_Away +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "PLAYER_OF_THE_MATCH":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
						+ "PLAYER OF THE MATCH " +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "GOAL_KEEPER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
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
					}else if(ms.getStats_type().equalsIgnoreCase("penalty_Conversion")) {
						if(ms.getPlayerId() == playerId) {
							player_goal_count = player_goal_count + 1;
						}
					}
				}
				if(player_goal_count != 0) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
							+ "GOALS TODAY - " + player_goal_count +"\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}else {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
							+ Home_or_Away +"\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}
				
				break;
			case "GOAL_SCORER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
						+ "GOAL SCORER" + " , " + Home_or_Away +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "CAPTAIN-GOALKEEPER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
						+ "CAPTAIN & GOAL KEEPER" + " , " + Home_or_Away +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "STRIKER_OF_THE_MATCH":	
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
						+ "STRIKER OF THE MATCH" +"\0");
			case "2_MIN_SUSPENSION":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
						+ "2 MIN SUSPENSION" +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;	
			case "5_MIN_SUSPENSION":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
						+ "5 MIN SUSPENSION" +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "10_MIN_SUSPENSION":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$BottomLIne$Side" + whichSide + "$txt_Info*GEOM*TEXT SET " 
						+ "10 MIN SUSPENSION" +"\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;	
			}
			
			
			switch(cardType.toUpperCase())
			{
			case "NO_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Card$Side" + whichSide + "$select_Card*FUNCTION*Omo*vis_con SET 0\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "GREEN":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Card$Side" + whichSide + "$select_Card*FUNCTION*Omo*vis_con SET 1\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case HockeyUtil.YELLOW:
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Card$Side" + whichSide + "$select_Card*FUNCTION*Omo*vis_con SET 2\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case HockeyUtil.RED:
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Card$Side" + whichSide + "$select_Card*FUNCTION*Omo*vis_con SET 3\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LowerThird$select_ToplineNumbers$TopLine1$Card$Side" + whichSide + "$select_Card*FUNCTION*Omo*vis_con SET 2\0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}
		}
		
		if(whichSide == 1) {
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_Lowerthird$In_Out 1.240 anim_Lowerthird$In_Out$Essentials 1.240 "
					+ "anim_Lowerthird$In_Out$Essentials$In 1.240 anim_Lowerthird$In_Out$Cards 1.240 anim_Lowerthird$In_Out$Cards$In 1.240 \0");
		}else {
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga Change_Lowerthird 0.780 Change_Lowerthird$TopLine 0.600 "
					+ "Change_Lowerthird$Cards 0.780 Change_Lowerthird$BottomLine 0.600\0");
		}
	}
	public void populateNameSuperCard(Socket session_socket, int TeamId, String cardType, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + HOCKEY_LOGO + match.getHomeTeam().getTeamName4() + 
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
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + HOCKEY_LOGO + match.getAwayTeam().getTeamName4() + 
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
					+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
					+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
			
			for(Player hsh : match.getHomeSquad()) {
				if(hsh.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN) || hsh.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN_GOAL_KEEPER)) {
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Left_Logo_Out$img_Player_Team1*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH + 
								"Blank" + HockeyUtil.PNG_EXTENSION +"\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Left_Logo_Out$img_Player_Team1*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH + 
								hsh.getPhoto() + HockeyUtil.PNG_EXTENSION +"\0");
						
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Left_Logo_Out$img_Player_Team1*TEXTURE*IMAGE SET "+ "\\\\" + IndexController.session_configurations.getIpAddress()
								+  HOCKEY_PHOTO_PATH + "Blank" + HockeyUtil.PNG_EXTENSION +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Left_Logo_Out$img_Player_Team1*TEXTURE*IMAGE SET "+ "\\\\" + IndexController.session_configurations.getIpAddress()
								+  HOCKEY_PHOTO_PATH + hsh.getPhoto() + HockeyUtil.PNG_EXTENSION +"\0");
					}
				}
			}
			
			for(Player hsa : match.getAwaySquad()) {
				if(hsa.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN) || hsa.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN_GOAL_KEEPER)) {
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Right_Logo_Out$img_Player_Team2*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH +
								"Blank" + HockeyUtil.PNG_EXTENSION +"\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Right_Logo_Out$img_Player_Team2*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH +
								hsa.getPhoto() + HockeyUtil.PNG_EXTENSION +"\0");
						
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Right_Logo_Out$img_Player_Team2*TEXTURE*IMAGE SET "+ "\\\\" + IndexController.session_configurations.getIpAddress() +
								HOCKEY_PHOTO_PATH +"Blank" + HockeyUtil.PNG_EXTENSION +"\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Final_Vs$Data"
								+ "$Logos_Vs$Right_Logo_Out$img_Player_Team2*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() 
								+ HOCKEY_PHOTO_PATH + hsa.getPhoto() + HockeyUtil.PNG_EXTENSION +"\0");
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
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header-UD$FF_Title_SEL_UD$txt_Title"
					+ "*GEOM*TEXT SET " + match.getMatchIdent() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data$Flags$Team1"
					+ "$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data$Flags$Team1"
					+ "$txt_TeamName*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName1() +"\0");
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data$Flags$Team2"
					+ "$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data$Flags$Team2"
					+ "$txt_TeamName*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName1() +"\0");
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
					+ "$txt_Footer*GEOM*TEXT SET "+ match.getVenueName() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
					+ "$txt_Hashtag*GEOM*TEXT SET #HeroACT2026\0");
			
			
//			for(VariousText vt : hockeyService.getVariousTexts()) {
//				if(vt.getVariousType().equalsIgnoreCase("MATCHIDENTFOOTER") && vt.getUseThis().equalsIgnoreCase("YES") ) {
//					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
//							+ "$txt_Footer*GEOM*TEXT SET "+ vt.getVariousText() +"\0");
//					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
//							+ "$txt_Hashtag*GEOM*TEXT SET "+ vt.getVariousText1() +"\0");
//				}
//			}
			
			previewCommand = "anim_Ident$In_Out 1.280 anim_Ident$In_Out$In 1.280";
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
			
		}
	}
	
	
	public void populateTopScorerExcelData(Socket session_socket, String viz_scene, Match match,
	        String session_selected_broadcaster, int whichSide, HockeyService hockeyService)
	        throws InterruptedException, IOException
	{
	    String previewCommand = "";
	    if (match == null) {
	        this.status = "ERROR: Match is null";
	        return;
	    }

	    print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle*FUNCTION*Omo*vis_con SET 3 \0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side" + whichSide + "$select_Flag*FUNCTION*Omo*vis_con SET 0\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
				+ "$txt_Title*GEOM*TEXT SET HERO TOP SCORERS\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
				+ "$Tournament_Logo*TEXTURE*IMAGE SET " + eventlogo_path + "EventLogo" + "\0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics*FUNCTION*Omo*vis_con SET 5\0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Group*GEOM*TEXT SET "+ "" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Play*GEOM*TEXT SET "+ "FG" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Win*GEOM*TEXT SET "+ "PC" +"\0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Draw*GEOM*TEXT SET "+ "PS" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Lost*GEOM*TEXT SET "+ "" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_GoalDifference*GEOM*TEXT SET "+ "TOTAL" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Points*GEOM*TEXT SET "+ "" +"\0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$select_LineNumber*FUNCTION*Omo*vis_con SET 5\0");
		
		List<Scorers> scorers = new Scorers().getScorers("C:/Sports/Hockey/IndividualGoalScorers.json", hockeyService);
		//List<Scorers> scorers = readIndividualGoalScorers("C:/Sports/Hockey/IndividualGoalScorers.json");
//	    List<Scorer> scorers = getAllGoalScorers("C:/Sports/Hockey/Matches");

	    for (int i = 0; i < Math.min(scorers.size(), 5); i++) {   // top 5, change as needed
	        Scorers s = scorers.get(i);
	        
	        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Standings$select_LineNumber$Line" + (i+1) + "$select_Highlight*FUNCTION*Omo*vis_con SET 0\0");
	        
	        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$Text$txt_TeamName*GEOM*TEXT SET "+ s.name.toUpperCase() +"\0");
	        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$Text$txt_Play*GEOM*TEXT SET "+ s.fieldGoals +"\0");
	        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$Text$txt_Win*GEOM*TEXT SET "+ s.pcGoals +"\0");
	        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$Text$txt_Draw*GEOM*TEXT SET "+ s.psGoals +"\0");
	        
	        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$Text$txt_GoalDifference*GEOM*TEXT SET "+ s.total +"\0");
	        
	        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$Text$txt_Lost*GEOM*TEXT SET \0");
	        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$Text$txt_Points*GEOM*TEXT SET \0");
	        
//	        for(Team tm : hockeyService.getTeams()) {
//				if(tm.getTeamId() == s.teamId) {
//					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
//							+ "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO 
//							+ tm.getTeamBadge() +"\0");
//				}
//			}
	        
	        for (Team tm : hockeyService.getTeams()) {
			    if (s.teamCode.equalsIgnoreCase(tm.getTeamName4())) {   // use whichever Team getter holds IND/MAS/CHN
			        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
			            + "$select_LineNumber$Line" + (i+1) + "$select_Highlight$Dehighlight$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO
			            + tm.getTeamBadge() + "\0");
			        break;
			    }
			}
	        
	        // s.name(), s.team(), s.fieldGoals(), s.pcGoals(), s.psGoals(), s.total()
	    }
	    
	    previewCommand = "anim_FullFrames$In_Out 1.800 anim_FullFrames$In_Out$Essentials 1.800 anim_FullFrames$In_Out$Essentials$In 1.800 anim_FullFrames$In_Out$Header$In 1.800 "
				+ "anim_FullFrames$In_Out$Flags 1.800 anim_FullFrames$In_Out$Flags$In 1.800 anim_FullFrames$In_Out$Main 1.800 anim_FullFrames$In_Out$Main$In 1.800";
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
	}
	
	public void populateMatchPointsExcelData(Socket session_socket, String viz_scene, Match match,
	        String session_selected_broadcaster, int whichSide, HockeyService hockeyService)
	        throws InterruptedException, IOException
	{	
		
		System.out.println("coming insde");
		String previewCommand = "";
	    if (match == null) {
	        this.status = "ERROR: Match is null";
	    } else {
	    	List<Scorer> top = getAllGoalScorers(MATCHES_FOLDER);

	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "*FUNCTION*Omo*vis_con SET 3\0");
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "$TitleCenter$txt_Title*GEOM*TEXT SET "+ "LEADING SCORERS" +"\0");
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "$TitleCenter$Tournament_Logo*ACTIVE SET 0\0");
			
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side1$select_Flag"
					+ "*FUNCTION*Omo*vis_con SET 1\0");
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side1"
					+ "$Flag1$Flag_UD$img_Flag*TEXTURE*IMAGE SET "+ eventlogo_path +"\0");
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "*FUNCTION*Omo*vis_con SET 10\0");
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics$TopScorer$txt_Footer"
					+ "*ACTIVE SET 0\0");
	    	
	    	for (int i = 0; i < top.size() && i < 3; i++) {
	    	    Scorer s = top.get(i);
	    	    System.out.println((i + 1) + ". " + s.name + " | " + s.team + " | Goals: " + s.total);
	    	    
	    	    print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
						+ "$TopScorer$"+ (i+1) +"$txt_Position*GEOM*TEXT SET "+  s.name + "\0");
	    	    print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
						+ "$TopScorer$"+ (i+1) +"$img_Text$txt_PlayerName*GEOM*TEXT SET "+  s.team + "\0");
	    	    print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
						+ "$TopScorer$"+ (i+1) +"$txt_Value*GEOM*TEXT SET "+  s.total + "\0");
	    	    print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
						+ "$TopScorer$"+ (i+1) +"$txt_Title*GEOM*TEXT SET "+  "GOALS" + "\0");
	    	    
	    	    for(Team tm:hockeyService.getTeams()) {
	    	    	if(tm.getTeamName1().equalsIgnoreCase(s.team)) {
	    	    		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
	    						+ "$TopScorer$"+ (i+1) +"$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + tm.getTeamBadge() +"\0");	
	    	    		
	    	    		for(Player pl:hockeyService.getAllPlayer()) {
	    	    			if(pl.getPlayerId() == s.playerId) {
	    	    				if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
	    	    					
	    	    					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
	    		    						+ "$TopScorer$"+ (i+1) +"$img_Player*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
	    	    					
	    	    					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
	    		    						+ "$TopScorer$"+ (i+1) +"$img_Player*TEXTURE*IMAGE SET "+ HOCKEY_LOCAL_PHOTO_PATH + 
	    	    					tm.getTeamName4() + "\\\\" + "Jersey_1" + "\\\\" + pl.getPhoto() + ".png" +"\0");
	    							
	    						}else {
	    							
	    							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
	    		    						+ "$TopScorer$"+ (i+1) +"$img_Player*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
	    	    					
	    	    					
	    	    					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
	    		    						+ "$TopScorer$"+ (i+1) +"$img_Player*TEXTURE*IMAGE SET "+ "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
	    	    					tm.getTeamName4() + "\\\\"  + "Jersey_1" +  "\\\\" + pl.getPhoto() + ".png" +"\0");
	    			}	
	    	    }
	    	  }
	    	}
	     }
	   }
	    	previewCommand = "anim_FullFrames$In_Out 1.800 anim_FullFrames$In_Out$Essentials 1.800 anim_FullFrames$In_Out$Essentials$In 1.800 anim_FullFrames$In_Out$Header$In 1.800 "
					+ "anim_FullFrames$In_Out$Flags 1.800 anim_FullFrames$In_Out$Flags$In 1.800 anim_FullFrames$In_Out$Main 1.800 anim_FullFrames$In_Out$Main$In 1.800";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
	  }
	}
	
	
	public void populateMatchHeadToHead(Socket session_socket, String viz_scene, Match match,
	        String session_selected_broadcaster, int whichSide, HockeyService hockeyService)
	        throws InterruptedException, IOException
	{	
		String previewCommand = "";
	    if (match == null) {
	        this.status = "ERROR: Match is null";
	    } else {
	    	
	    	// ---- CHANGE 1: file reading moved to the top ----
	    	String sideBase = "-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$MatchStatsNew";

			String filePath = "C:/Sports/Hockey/HeadToHead.txt";
			if (!Files.exists(Paths.get(filePath))) {
			    this.status = "ERROR: HeadToHead.txt not found";
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
	    	
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "*FUNCTION*Omo*vis_con SET 3\0");
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "$TitleCenter$txt_Title*GEOM*TEXT SET HEAD TO HEAD\0");
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "$TitleCenter$Tournament_Logo*ACTIVE SET 1\0");
			
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side1$select_Flag"
					+ "*FUNCTION*Omo*vis_con SET 0\0");
	    	
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Team1$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Team1$txt_Team*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() +"\0");
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Team2$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Team2$txt_Team*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "*FUNCTION*Omo*vis_con SET 11\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$StatsAll$Line6*ACTIVE SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$StatsAll$Line1*ACTIVE SET 0\0");
			
			 final int OMO_STATS = 0; 
				final int OMO_CARDS = 1;

				for (int i = 1; i < rows.size() && i < 6; i++) {
				    String home  = rows.get(i)[0];
				    String label = rows.get(i)[1];
				    String away  = rows.get(i)[2];

				    String lineBase = sideBase + "$Line" + (i + 1) + "$MatchStatsData";
				    boolean isCards = label.trim().equalsIgnoreCase("CARDS");

				    String team1 = lineBase + "$Team1$select_StatType";
				    String team2 = lineBase + "$Team2$select_StatType";

				    print_writer.println(lineBase + "$txt_Title*GEOM*TEXT SET " + label + "\0");

				    if (isCards) {
				        // "1/4/1" -> green=1, yellow=4, red=1
				        String[] h = home.split("/");
				        String[] a = away.split("/");

				        String[] names = { "txt_Green", "txt_Yellow", "txt_Red" };
				        for (int k = 0; k < 3; k++) {
				            String hv = k < h.length ? h[k].trim() : "0";
				            String av = k < a.length ? a[k].trim() : "0";

				            print_writer.println(team1 + "$Cards$" + names[k] + "*GEOM*TEXT SET " + hv + "\0");
				            print_writer.println(team2 + "$Cards$" + names[k] + "*GEOM*TEXT SET " + av + "\0");
				        }
				    } else {
				        print_writer.println(team1 + "$txt_Stats*GEOM*TEXT SET " + home + "\0");
				        print_writer.println(team2 + "$txt_Stats*GEOM*TEXT SET " + away + "\0");
				    }

				    int omo = isCards ? OMO_CARDS : OMO_STATS;
				    print_writer.println(team1 + "*FUNCTION*Omo*vis_con SET " + omo + "\0");
				    print_writer.println(team2 + "*FUNCTION*Omo*vis_con SET " + omo + "\0");
				}
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$txt_Footer*GEOM*TEXT SET "+ "#HeroACT2026" + "\0");
	        
			previewCommand = "anim_FullFrames$In_Out 1.800 anim_FullFrames$In_Out$Essentials 1.800 anim_FullFrames$In_Out$Essentials$In 1.800 anim_FullFrames$In_Out$Header$In 1.800 "
					+ "anim_FullFrames$In_Out$Flags 1.800 anim_FullFrames$In_Out$Flags$In 1.800 anim_FullFrames$In_Out$Main 1.800 anim_FullFrames$In_Out$Main$In 1.800";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
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
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "*FUNCTION*Omo*vis_con SET 3\0");
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "$TitleCenter$txt_Title*GEOM*TEXT SET MATCH STATS\0");
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "$TitleCenter$Tournament_Logo*ACTIVE SET 1\0");
			
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side1$select_Flag"
					+ "*FUNCTION*Omo*vis_con SET 0\0");
	    	
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Team1$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Team1$txt_Team*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() +"\0");
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Team2$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Team2$txt_Team*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() +"\0");
	    	
			
//			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//					+ "$TitleCenter$txt_Title*GEOM*TEXT SET "+ "MATCH STATS" +"\0");
	    	
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "*FUNCTION*Omo*vis_con SET 11\0");
			
//			if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "QUARTER 1" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "QUARTER 2" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "QUARTER 3" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "QUARTER 4" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "HALF TIME" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "FULL TIME" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "EXTRA TIME 1" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "EXTRA TIME 2" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("END1")) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "END OF QUARTER 1" + "\0");
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("END3")) {
//				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
//						+ "$MatchStats$txt_GamePart*GEOM*TEXT SET "+  "END OF QUARTER 3" + "\0");
//			}
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Line1$MatchStatsData$Team1$select_StatType*FUNCTION*Omo*vis_con SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Line1$MatchStatsData$Team2$select_StatType*FUNCTION*Omo*vis_con SET 0\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Line1$MatchStatsData$txt_Title*GEOM*TEXT SET GOALS\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Line1$MatchStatsData$Team1$txt_Stats*GEOM*TEXT SET "+ match.getHomeTeamScore() + "\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$Line1$MatchStatsData$Team2$txt_Stats*GEOM*TEXT SET "+ match.getAwayTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$txt_Footer*GEOM*TEXT SET #HeroACT2026\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$StatsAll$Line1*ACTIVE SET 1\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "$MatchStatsNew$StatsAll$Line6*ACTIVE SET 1\0");
			
			
			
			String sideBase = "-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$MatchStatsNew";

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

			final int OMO_STATS = 0; 
			final int OMO_CARDS = 1;

			for (int i = 0; i < rows.size() && i < 6; i++) {
			    String home  = rows.get(i)[0];
			    String label = rows.get(i)[1];
			    String away  = rows.get(i)[2];

			    String lineBase = sideBase + "$Line" + (i + 2) + "$MatchStatsData";
			    boolean isCards = label.trim().equalsIgnoreCase("CARDS");

			    String team1 = lineBase + "$Team1$select_StatType";
			    String team2 = lineBase + "$Team2$select_StatType";

			    print_writer.println(lineBase + "$txt_Title*GEOM*TEXT SET " + label + "\0");

			    if (isCards) {
			        // "1/4/1" -> green=1, yellow=4, red=1
			        String[] h = home.split("/");
			        String[] a = away.split("/");

			        String[] names = { "txt_Green", "txt_Yellow", "txt_Red" };
			        for (int k = 0; k < 3; k++) {
			            String hv = k < h.length ? h[k].trim() : "0";
			            String av = k < a.length ? a[k].trim() : "0";

			            print_writer.println(team1 + "$Cards$" + names[k] + "*GEOM*TEXT SET " + hv + "\0");
			            print_writer.println(team2 + "$Cards$" + names[k] + "*GEOM*TEXT SET " + av + "\0");
			        }
			    } else {
			        print_writer.println(team1 + "$txt_Stats*GEOM*TEXT SET " + home + "\0");
			        print_writer.println(team2 + "$txt_Stats*GEOM*TEXT SET " + away + "\0");
			    }

			    int omo = isCards ? OMO_CARDS : OMO_STATS;
			    print_writer.println(team1 + "*FUNCTION*Omo*vis_con SET " + omo + "\0");
			    print_writer.println(team2 + "*FUNCTION*Omo*vis_con SET " + omo + "\0");
			}
			
			previewCommand = "anim_FullFrames$In_Out 1.800 anim_FullFrames$In_Out$Essentials 1.800 anim_FullFrames$In_Out$Essentials$In 1.800 anim_FullFrames$In_Out$Header$In 1.800 "
					+ "anim_FullFrames$In_Out$Flags 1.800 anim_FullFrames$In_Out$Flags$In 1.800 anim_FullFrames$In_Out$Main 1.800 anim_FullFrames$In_Out$Main$In 1.800";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
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
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header-UD$FF_Title_SEL_UD$txt_Title"
					+ "*GEOM*TEXT SET "+ "MATCH " +fixture.getMatchnumber() +"\0");
			
			match_name = (fixture.getMatchnumber() > 9 ? fixture.getMatchfilename() : "MATCH " + fixture.getMatchnumber());
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data$Flags$Team1"
					+ "$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + hockeyService.getTeams().get(fixture.getHometeamid() - 1).getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data$Flags$Team1"
					+ "$txt_TeamName*GEOM*TEXT SET "+ hockeyService.getTeams().get(fixture.getHometeamid() - 1).getTeamName1() +"\0");
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data$Flags$Team2"
					+ "$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + hockeyService.getTeams().get(fixture.getAwayteamid() - 1).getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data$Flags$Team2"
					+ "$txt_TeamName*GEOM*TEXT SET "+ hockeyService.getTeams().get(fixture.getAwayteamid() - 1).getTeamName1() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
					+ "$txt_Hashtag*GEOM*TEXT SET #HeroACT2026\0");
			
//			for(VariousText vt : hockeyService.getVariousTexts()) {
//				if(vt.getVariousType().equalsIgnoreCase("MATCHIDENTFOOTER") && vt.getUseThis().equalsIgnoreCase("YES") ) {
//					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
//							+ "$txt_Hashtag*GEOM*TEXT SET "+ vt.getVariousText1() +"\0");
//				}else {
//					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
//							+ "$txt_Hashtag*GEOM*TEXT SET "+ fixture.getVenue() +"\0");
//				}
//			}
			
			Calendar cal_mumbai = Calendar.getInstance();
			cal_mumbai.add(Calendar.DATE, +1);
			if(fixture.getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal_mumbai.getTime()))) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
						+ "$txt_Footer*GEOM*TEXT SET "+ "TOMORROW AT " + fixture.getTime() +"\0");
			}else {
				cal_mumbai.add(Calendar.DATE, -1);
				if(fixture.getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal_mumbai.getTime()))) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
							+ "$txt_Footer*GEOM*TEXT SET "+ "UP-NEXT" +"\0");
				}else {
					newDate = fixture.getDate().split("-")[0];
					if(Integer.valueOf(newDate) < 10) {
						newDate = newDate.replaceFirst("0", "");
					}
					date_data = HockeyFunctions.ordinal(Integer.valueOf(newDate)) + " " + Month.of(Integer.valueOf(fixture.getDate().split("-")[1]));
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_MatchId$Header$Data"
							+ "$txt_Footer*GEOM*TEXT SET "+  date_data + " / " + fixture.getTime() +"\0");
				}
			}
			previewCommand = "anim_Ident$In_Out 1.280 anim_Ident$In_Out$In 1.280";
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		}
	}	
	
	public void populateSubstitute(Socket session_socket,int TeamId,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
				hbase = HOCKEY_BASE1;
				htext = HOCKEY_TEXT1;
			}else if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
				hbase = HOCKEY_BASE2;
				htext = HOCKEY_TEXT2;
			}
			
			if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
				abase = HOCKEY_BASE1;
				atext = HOCKEY_TEXT1;
			}else if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
				abase = HOCKEY_BASE2;
				atext = HOCKEY_TEXT2;
			}
			
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$TEAM$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$TEAM$TeamAll$ANIM-Line$img_Base*TEXTURE*IMAGE SET " + 
						hbase + match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$TEAM$TeamAll$ANIM-Line$img_Base*TEXTURE*IMAGE SET " + 
						hbase + match.getHomeTeam().getTeamBadge() +"\0");
				
				for(int i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
					
					System.out.println("PHOTO ================ " + match.getHomeSubstitutes().get(i).getPhoto());
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber*FUNCTION*Grid*num_col SET " + (i+1) + "\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Flags$select-ActiveFlag*FUNCTION*Omo*vis_con SET 1\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Flags$select-ActiveFlag$select_Number*FUNCTION*Omo*vis_con SET 1\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Group1$img_Text*TEXTURE*IMAGE SET " + 
							htext + match.getHomeTeam().getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Group1$img_Base*TEXTURE*IMAGE SET " + 
							hbase + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Flags$txt_Number*GEOM*TEXT SET " + match.getHomeSubstitutes().get(i).getJersey_number() + "\0");
					
					
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$Photo-1-s*TEXTURE*IMAGE SET " + 
								HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$img_Player*TEXTURE*IMAGE SET " + 
								HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$Photo-1-s*TEXTURE*IMAGE SET " + 
								HOCKEY_LOCAL_PHOTO_PATH + match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamJerseyColor() + "\\\\" + match.getHomeSubstitutes().get(i).getPhoto() + ".png" +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$img_Player*TEXTURE*IMAGE SET " + 
								HOCKEY_LOCAL_PHOTO_PATH + match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamJerseyColor() + "\\\\" + match.getHomeSubstitutes().get(i).getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$Photo-1-s*TEXTURE*IMAGE SET " + 
								"\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$img_Player*TEXTURE*IMAGE SET " + 
								"\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$Photo-1-s*TEXTURE*IMAGE SET " + 
								"\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamJerseyColor() + "\\\\" + match.getHomeSubstitutes().get(i).getPhoto() + ".png" +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$img_Player*TEXTURE*IMAGE SET " + 
								"\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamJerseyColor() + "\\\\" + match.getHomeSubstitutes().get(i).getPhoto() + ".png" +"\0");
					}
					
					if(match.getHomeSubstitutes().get(i).getSurname() != null) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$txt_Name*GEOM*TEXT SET " + match.getHomeSubstitutes().get(i).getFirstname() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$txt_Info*GEOM*TEXT SET " + match.getHomeSubstitutes().get(i).getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$txt_Name*GEOM*TEXT SET " + match.getHomeSubstitutes().get(i).getFirstname() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$txt_Info*GEOM*TEXT SET \0");
					}
				}
			}else if(TeamId == match.getAwayTeamId()){
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$TEAM$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$TEAM$TeamAll$ANIM-Line$img_Base*TEXTURE*IMAGE SET " + 
						abase + match.getAwayTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$TEAM$TeamAll$ANIM-Line$img_Base*TEXTURE*IMAGE SET " + 
						abase + match.getAwayTeam().getTeamBadge() +"\0");
				
				for(int i = 0; i <= match.getAwaySubstitutesPerTeam()-1; i++) {
					
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber*FUNCTION*Grid*num_col SET " + (i+1) + "\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Flags$select-ActiveFlag*FUNCTION*Omo*vis_con SET 1\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Flags$select-ActiveFlag$select_Number*FUNCTION*Omo*vis_con SET 1\0");
					
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Flags$txt_Number*GEOM*TEXT SET " + match.getAwaySubstitutes().get(i).getJersey_number() + "\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Group1$img_Text*TEXTURE*IMAGE SET " + 
							atext + match.getAwayTeam().getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Group1$img_Base*TEXTURE*IMAGE SET " + 
							abase + match.getAwayTeam().getTeamBadge() +"\0");
					
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$Photo-1-s*TEXTURE*IMAGE SET " + 
								HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$img_Player*TEXTURE*IMAGE SET " + 
								HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$Photo-1-s*TEXTURE*IMAGE SET " + 
								HOCKEY_LOCAL_PHOTO_PATH + match.getAwayTeam().getTeamName4()  + "\\\\" + match.getAwayTeamJerseyColor() + "\\\\" + match.getAwaySubstitutes().get(i).getPhoto() + ".png" +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$img_Player*TEXTURE*IMAGE SET " + 
								HOCKEY_LOCAL_PHOTO_PATH + match.getAwayTeam().getTeamName4()  + "\\\\" + match.getAwayTeamJerseyColor() + "\\\\" + match.getAwaySubstitutes().get(i).getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$Photo-1-s*TEXTURE*IMAGE SET " + 
								"\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$img_Player*TEXTURE*IMAGE SET " + 
								"\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$Photo-1-s*TEXTURE*IMAGE SET " + 
								"\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4()  + "\\\\" + match.getAwayTeamJerseyColor() + "\\\\" +match.getAwaySubstitutes().get(i).getPhoto() + ".png" +"\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$Image$img_Player*TEXTURE*IMAGE SET " + 
								"\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4()  + "\\\\" + match.getAwayTeamJerseyColor() + "\\\\" +match.getAwaySubstitutes().get(i).getPhoto() + ".png" +"\0");
					}
					
					
					if(match.getAwaySubstitutes().get(i).getSurname() != null) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$txt_Name*GEOM*TEXT SET " + match.getAwaySubstitutes().get(i).getFirstname() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$txt_Info*GEOM*TEXT SET " + match.getAwaySubstitutes().get(i).getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$txt_Name*GEOM*TEXT SET " + match.getAwaySubstitutes().get(i).getFirstname() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LineUp$select_PlayerNumber$Pos-" + (i+1) + "$txt_Info*GEOM*TEXT SET \0");
					}
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_Lt_LineUp$In_Out 1.520 anim_Lt_LineUp$In_Out$In 1.520 "
					+ "anim_Lt_LineUp$In_Out$In$1 1.040 anim_Lt_LineUp$In_Out$In$2 1.160 anim_Lt_LineUp$In_Out$In$3 1.280 anim_Lt_LineUp$In_Out$In$4 1.420 anim_Lt_LineUp$In_Out$In$5 1.520 "
					+ "anim_Lt_LineUp$In_Out$In$6 1.520 anim_Lt_LineUp$In_Out$In$7 1.520\0");
			
		}
	}
	public void populateSingleSubstitute(Socket session_socket,String viz_scene,int Team_id,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws IOException, InterruptedException {
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + HOCKEY_LOGO + team.get(Team_id - 1).getTeamName4() + 
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
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2 " + HOCKEY_LOGO + "Red_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
		
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1 " + HOCKEY_LOGO + "Green_Arrow" + HockeyUtil.PNG_EXTENSION + ";");
				
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

			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
					+ "$Match_Summary_Out$Druk-Trial-Medium*GEOM*TEXT SET "+ "TEAM" +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
					+ "$Match_Summary_Out$Summary$Summary*GEOM*TEXT SET "+ "LIST" +"\0");
			
			
		
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
						+ "$Logos_Vs$Left_Logo_Out$img_Logo_Left*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$TeamList_Both$Data"
						+ "$Logos_Vs$Right_Logo_Out$img_Logo_Right*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
				
				
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
				
			previewCommand = "anim_Formation$In_Out 1.960 anim_Formation$In_Out$Essentials 1.960 anim_Formation$In_Out$Essentials$In 1.960 "
					+ "anim_Formation$In_Out$Header 1.960 anim_Formation$In_Out$Header$In 0.800 anim_Formation$In_Out$Players 1.960 anim_Formation$In_Out$Players$In 1.940";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
			
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
			 
			 for(int i = 1; i <=11; i++ ) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
							+ "$Player" + i + "*TRANSFORMATION*POSITION*X SET " + "0" + "\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
							+ "$Player" + i + "*TRANSFORMATION*POSITION*Y SET " + "0" + "\0");
				}
			
			 if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
					hbase = HOCKEY_BASE1;
					htext = HOCKEY_TEXT1;
				}else if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
					hbase = HOCKEY_BASE2;
					htext = HOCKEY_TEXT2;
				}
				
				if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
					abase = HOCKEY_BASE1;
					atext = HOCKEY_TEXT1;
				}else if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
					abase = HOCKEY_BASE2;
					atext = HOCKEY_TEXT2;
				}
				
			if(TeamId == match.getHomeTeamId()) {
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$Text-1"
						+ "$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$Text-1"
						+ "$img_Text$txt_TeamName*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName1() +"\0");
				
				for(Staff st: hockeyService.getStaffs()) {
					if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("HEAD COACH")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$Text-2"
								+ "$txt_Info*GEOM*TEXT SET "+ "HEAD COACH: " + st.getName()+"\0");
					}
				}
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					//forbase
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$L3$Text-1"
							+ "$img_Text*TEXTURE*IMAGE SET "+ htext + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$L3$BG_Left"
							+ "$img_Base*TEXTURE*IMAGE SET "+ hbase + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$L3$Bg_Right"
							+ "$img_Base*TEXTURE*IMAGE SET "+ hbase + match.getHomeTeam().getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
							+ "$Player"+ row_id + "$Info-P1$Text-P1$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
							+ "$Player"+ row_id + "$Info-P1$Text-P1$txt_Number*GEOM*TEXT SET "+ hs.getJersey_number() +"\0");
					
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
								+ "$Player"+ row_id + "$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
								+ "$Player"+ row_id + "$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH +
								match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamJerseyColor() + "\\\\" + hs.getPhoto() + ".png" +"\0");
						
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
								+ "$Player"+ row_id + "$img_Player*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
								+ "$Player"+ row_id + "$img_Player*TEXTURE*IMAGE SET "  + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamJerseyColor() + "\\\\" + hs.getPhoto() + ".png" +"\0");
						
					}
					
					
//					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
//								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)" +"\0");
//					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
//								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(GK)" +"\0");
//					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
//								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)(GK)" +"\0");
//					}else {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
//								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "" +"\0");
//					}
					
				}

				
				for(Formation form : formation) {
					if(form.getFormId() == match.getHomeTeamFormationId()) {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						
					}
				}
				
			}else if(TeamId == match.getAwayTeamId()){
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$Text-1"
						+ "$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$Text-1"
						+ "$img_Text$txt_TeamName*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName1() +"\0");
				
				for(Staff st: hockeyService.getStaffs()) {
					if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("HEAD COACH")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$Text-2"
								+ "$txt_Info*GEOM*TEXT SET "+ "HEAD COACH: " + st.getName()+"\0");
					}
				}
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$L3$Text-1"
						+ "$img_Text*TEXTURE*IMAGE SET "+ atext + match.getAwayTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$L3$BG_Left"
						+ "$img_Base*TEXTURE*IMAGE SET "+ abase + match.getAwayTeam().getTeamBadge() +"\0");
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Header$Side" + whichSide + "$L3$Bg_Right"
						+ "$img_Base*TEXTURE*IMAGE SET "+ abase + match.getAwayTeam().getTeamBadge() +"\0");
				
				for(Player hs : match.getAwaySquad()) {
					row_id = row_id + 1;
					//forbase
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
							+ "$Player"+ row_id + "$Info-P1$Text-P1$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
							+ "$Player"+ row_id + "$Info-P1$Text-P1$txt_Number*GEOM*TEXT SET "+ hs.getJersey_number() +"\0");
					
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
								+ "$Player"+ row_id + "$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
								+ "$Player"+ row_id + "$img_Player*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH +
								match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamJerseyColor() + "\\\\" +hs.getPhoto() + ".png" +"\0");
						
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
								+ "$Player"+ row_id + "$img_Player*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide 
								+ "$Player"+ row_id + "$img_Player*TEXTURE*IMAGE SET "  + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamJerseyColor() + "\\\\" + hs.getPhoto() + ".png" +"\0");
						
					}
					
					
					
//					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
//								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)" +"\0");
//					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
//								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(GK)" +"\0");
//					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
//								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "(C)(GK)" +"\0");
//					}else {
//						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11$Data"
//								+ "$noname$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "" +"\0");
//					}
					
					
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getAwayTeamFormationId()) {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_Formation$Formation$Player-Grp$Side" + whichSide
								+ "$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						
					}
				}
				
			}
			
			if(whichSide == 1) {
				previewCommand = "anim_Formation$In_Out 1.960 anim_Formation$In_Out$Essentials 1.960 anim_Formation$In_Out$Essentials$In 1.960 "
						+ "anim_Formation$In_Out$Header 1.960 anim_Formation$In_Out$Header$In 0.800 anim_Formation$In_Out$Players 1.960 anim_Formation$In_Out$Players$In 1.940";
			}else {
				previewCommand = "change_Formation 1.460 change_Formation$Header 1.100 change_Formation$Header$Change_Out 0.300 "
						+ "change_Formation$Header$Change_In 1.100 change_Formation$Players 1.460 change_Formation$Players$Change_Out 0.400 change_Formation$Players$Change_In 1.460";
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
			
		}
	}
	public void populatePlayingXISUBAndCoach(Socket session_socket,int TeamId, List<Team> team ,Match match, String session_selected_broadcaster
			,int whichSide,HockeyService hockeyService) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			int row_id=0,row_id_sub=0;
			boolean hc=false;
			boolean ac=false;
			String previewCommand ="";
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle*FUNCTION*Omo*vis_con SET 3 \0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side" + whichSide + "$select_Flag*FUNCTION*Omo*vis_con SET 1\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
					+ "$Tournament_Logo*ACTIVE SET 0\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics*FUNCTION*Omo*vis_con SET 3 \0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
					+ "$select_Graphics$LineUP$Starting11$txt_Header*GEOM*TEXT SET "+ "STARTING LINEUP" +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
					+ "$select_Graphics$LineUP$Substitues_Coach$txt_Header*GEOM*TEXT SET "+ "SUBSTITUTES" +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
					+ "$select_Coach*FUNCTION*Omo*vis_con SET 0\0");
			
			for(int i = 0; i <= 10; i++) {
				row_id_sub = row_id_sub + 1;
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
						+ "$P"+ row_id_sub + "$txt_Name*GEOM*TEXT SET "+ "" +"\0");
			
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
						+ "$P"+ row_id_sub + "$txt_JerseyNumber*GEOM*TEXT SET "+ "" +"\0");
				
			}
			row_id_sub = 0;
			for(int i = 0; i <= 10; i++) {
				row_id_sub = row_id_sub + 1;
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
						+ "$P"+ row_id_sub + "$txt_Name*GEOM*TEXT SET "+ "" +"\0");
			
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
						+ "$P"+ row_id_sub + "$txt_JerseyNumber*GEOM*TEXT SET "+ "" +"\0");
			}
			row_id_sub = 0;
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$Flags$Side" + whichSide + "$select_Flag$Flag1"
						+ "$Flag_UD$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
						+ "$txt_Title*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() + "\0");
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
								+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() + " (C)" +  "\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
								+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() + " (GK)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
								+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() + " (C)(GK)" +"\0");
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
								+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
							+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_JerseyNumber*GEOM*TEXT SET "+ hs.getJersey_number() +"\0");
				}
				
				for(int i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
								+ "$P"+ row_id_sub + "$txt_Name*GEOM*TEXT SET "+ match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() + " (GK)" +"\0");
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
								+ "$P"+ row_id_sub + "$txt_Name*GEOM*TEXT SET "+ match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() +"\0");
					}
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
							+ "$P"+ row_id_sub + "$txt_JerseyNumber*GEOM*TEXT SET "+ match.getHomeSubstitutes().get(i).getJersey_number() +"\0");
				}
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
						+ "$select_Coach*FUNCTION*Omo*vis_con SET 1\0");
				for(Staff st: hockeyService.getStaffs()) {
					if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("HEAD COACH")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
								+ "$Coach$txt_CoachHead*GEOM*TEXT SET "+ "HEAD COACH" +"\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
								+ "$Coach$txt_Coach_Name*GEOM*TEXT SET "+ st.getName() +"\0");
						hc=true;
					}
					if(hc == false) {
						if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("ASSISTANT COACH")) {
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
									+ "$Coach$txt_CoachHead*GEOM*TEXT SET "+ "ASSISTANT COACH" +"\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
									+ "$Coach$txt_Coach_Name*GEOM*TEXT SET "+ st.getName() +"\0");
							ac=true;
						}
					}
				}
				
				
			}else if(TeamId == match.getAwayTeamId()){
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$Flags$Side" + whichSide + "$select_Flag$Flag1"
						+ "$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
						+ "$txt_Title*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() + "\0");
				for(Player hs : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
								+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() + " (C)" +  "\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
								+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() + " (GK)" +"\0");
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
								+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() + " (C)(GK)" +"\0");
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
								+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_Name*GEOM*TEXT SET "+ hs.getFull_name() +"\0");
					}
					
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide 
							+ "$select_Graphics$LineUP$Starting11$P"+ row_id + "$txt_JerseyNumber*GEOM*TEXT SET "+ hs.getJersey_number() +"\0");
				}
				
				for(int i = 0; i <= match.getAwaySubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					
					if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(HockeyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
								+ "$P"+ row_id_sub + "$txt_Name*GEOM*TEXT SET "+ match.getAwaySubstitutes().get(i).getFull_name().toUpperCase() + " (GK)" +"\0");
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
								+ "$P"+ row_id_sub + "$txt_Name*GEOM*TEXT SET "+ match.getAwaySubstitutes().get(i).getFull_name().toUpperCase() +"\0");
					}
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Substitues_Coach"
							+ "$P"+ row_id_sub + "$txt_JerseyNumber*GEOM*TEXT SET "+ match.getAwaySubstitutes().get(i).getJersey_number() +"\0");
				}
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
						+ "$select_Coach*FUNCTION*Omo*vis_con SET 1\0");
				for(Staff st: hockeyService.getStaffs()) {
					if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("HEAD COACH")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
								+ "$Coach$txt_CoachHead*GEOM*TEXT SET "+ "HEAD COACH" +"\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
								+ "$Coach$txt_Coach_Name*GEOM*TEXT SET "+ st.getName() +"\0");
						hc=true;
					}
					if(hc == false) {
						if(st.getClubId() == TeamId && st.getRole().equalsIgnoreCase("ASSISTANT COACH")) {
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
									+ "$Coach$txt_CoachHead*GEOM*TEXT SET "+ "ASSISTANT COACH" +"\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$LineUP$Coach_All"
									+ "$Coach$txt_Coach_Name*GEOM*TEXT SET "+ st.getName() +"\0");
							ac=true;
						}
					}
				}
			}
			
			previewCommand = "anim_FullFrames$In_Out 1.800 anim_FullFrames$In_Out$Essentials 1.800 anim_FullFrames$In_Out$Essentials$In 1.800 anim_FullFrames$In_Out$Header$In 1.800 "
					+ "anim_FullFrames$In_Out$Flags 1.800 anim_FullFrames$In_Out$Flags$In 1.800 anim_FullFrames$In_Out$Main 1.800 anim_FullFrames$In_Out$Main$In 1.800";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
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
			
			if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
				hbase = HOCKEY_BASE1;
				htext = HOCKEY_TEXT1;
			}else if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
				hbase = HOCKEY_BASE2;
				htext = HOCKEY_TEXT2;
			}
			
			if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
				abase = HOCKEY_BASE1;
				atext = HOCKEY_TEXT1;
			}else if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
				abase = HOCKEY_BASE2;
				atext = HOCKEY_TEXT2;
			}
			
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
						+ "$noname$img_Logo_Left*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
						+ "$Head_Coach_Out$Header$txt_Header*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName1() +"\0");
				
				
				for(int i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					if(IndexController.session_configurations.getIpAddress().equalsIgnoreCase("LOCALHOST")) { 
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + "Blank" + ".png" +"\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + HOCKEY_LOCAL_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamGKJerseyColor() + "\\\\" + match.getHomeSubstitutes().get(i).getPhoto() + ".png" +"\0");
						
					}else {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamGKJerseyColor() + "\\\\" + match.getHomeSubstitutes().get(i).getPhoto() + ".png" +"\0");
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
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "" +"\0");
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
								match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamGKJerseyColor() + "\\\\" + hs.getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getHomeTeam().getTeamName4() + "\\\\" + match.getHomeTeamGKJerseyColor() + "\\\\" + hs.getPhoto() + ".png" +"\0");
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
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "" +"\0");
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
								+ "$Players$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8Y() + "\0");
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
						+ "$noname$img_Logo_Left*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
				
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
								match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamGKJerseyColor() + "\\\\" + match.getAwaySubstitutes().get(i).getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamGKJerseyColor() + "\\\\" + match.getAwaySubstitutes().get(i).getPhoto() + ".png" +"\0");
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
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Substitute_Players$Player"+ row_id_sub + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "" +"\0");
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
								match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamGKJerseyColor() + "\\\\" + hs.getPhoto() + ".png" +"\0");
						
					}else {
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" +  IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + "Blank.png\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Player_Team1*TEXTURE*IMAGE SET " + "\\\\" + IndexController.session_configurations.getIpAddress() + HOCKEY_PHOTO_PATH + 
								match.getAwayTeam().getTeamName4() + "\\\\" + match.getAwayTeamGKJerseyColor() + "\\\\" + hs.getPhoto() + ".png" +"\0");
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
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$Data_All$Main$Side" + whichSide + "$Playing_11_And_Substitutes$Data"
								+ "$Players$Player"+ row_id + "$img_Text2$txt_Name02*GEOM*TEXT SET "+ "" +"\0");
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
								+ "$Players$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8Y() + "\0");
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
			
			previewCommand = "anim_FullFrame$In_Out 1.800 anim_FullFrame$In_Out$Essentials 1.800 anim_FullFrame$In_Out$Essentials$In 1.800 anim_FullFrame$In_Out$Header$In 1.800 "
					+ "anim_FullFrame$In_Out$Flags 1.800 anim_FullFrame$In_Out$Flags$In 1.800 anim_FullFrame$In_Out$Main 1.800 anim_FullFrame$In_Out$Main$In 1.800";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/anim_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
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
			
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "*FUNCTION*Omo*vis_con SET 3\0");
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "$TitleCenter$txt_Title*GEOM*TEXT SET "+ "MATCH SUMMARY" +"\0");
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$FF_Title_SEL$Side1$select_HeaderStyle"
					+ "$TitleCenter$Tournament_Logo*ACTIVE SET 1\0");
			
	    	
	    	print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side1$select_Flag"
					+ "*FUNCTION*Omo*vis_con SET 1\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side1"
					+ "$Flag1$Flag_UD$img_Flag*TEXTURE*IMAGE SET "+ eventlogo_path +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics"
					+ "*FUNCTION*Omo*vis_con SET 7\0");
			
	
			
			
			
			for(VariousText vt : hockeyService.getVariousTexts()) {
				if(vt.getVariousType().equalsIgnoreCase("MATCHSUMMARYHEADER") && vt.getUseThis().equalsIgnoreCase("YES")) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
							+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ vt.getVariousText() +"\0");
				}else {
					if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "QUARTER 1" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "QUARTER 2" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "QUARTER 3" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "QUARTER 4" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "HALF TIME" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "FULL TIME" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "EXTRA TIME 1" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "EXTRA TIME 2" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("END1")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "END OF QUARTER 1" +"\0");
					}else if(match.getClock().getMatchHalves().equalsIgnoreCase("END3")) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
								+ "$Results_with_Scorers_V2$txt_Title*GEOM*TEXT SET "+ "END OF QUARTER 3" +"\0");
					}
				}
			}
			
			//hometeam
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Results_with_Scorers_V2$Team1$txt_TeamName*GEOM*TEXT SET "+ match.getHomeTeam().getTeamName1() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Results_with_Scorers_V2$Team1$txt_Score*GEOM*TEXT SET "+ match.getHomeTeamScore() +"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Results_with_Scorers_V2$Team1$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() +"\0");
			
			
			//awayteam
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Results_with_Scorers_V2$Team2$txt_TeamName*GEOM*TEXT SET "+ match.getAwayTeam().getTeamName1()  +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Results_with_Scorers_V2$Team2$txt_Score*GEOM*TEXT SET "+ match.getAwayTeamScore()+"\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Results_with_Scorers_V2$Team2$img_Flag*TEXTURE*IMAGE SET "+ HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() +"\0");
			
			
			for(int i=1;i<=8;i++) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
						+ "$Results_with_Scorers_V2$Team1$Scorers$txt_Scorer"+ i +"*GEOM*TEXT SET "+ "" +"\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
						+ "$Results_with_Scorers_V2$Team2$Scorers$txt_Scorer"+ i +"*GEOM*TEXT SET "+ "" +"\0");
			}
			
			
			
			int l=4;
			String h1="",h2="",h3="",a1="",a2="",a3="";
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			List<Integer> plyr_ids = new ArrayList<Integer>();
			boolean plyr_exist = false;
 			String stats_txt = "",stats_txt_og = "";
			
			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
//			case "PENALTY_CONVERSION":
				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION"))) {
					
					plyr_exist = false;
					for(Integer plyr_id : plyr_ids) {
						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) 
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION"))) {
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
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION"))) {

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
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
						+ "$Results_with_Scorers_V2$Team1$Scorers$txt_Scorer"+ (i+1) +"*GEOM*TEXT SET "+ home_stats.get(i) +"\0");
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
						+ "$Results_with_Scorers_V2$Team2$Scorers$txt_Scorer"+ (i+1) +"*GEOM*TEXT SET "+ away_stats.get(i) +"\0");
			}
			
			if(viz_scene.equalsIgnoreCase("without")) {
				for(int i=1;i<=8;i++) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
							+ "$Results_with_Scorers_V2$Team1$Scorers$txt_Scorer"+ (i+1) +"*GEOM*TEXT SET "+ "" +"\0");
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
							+ "$Results_with_Scorers_V2$Team2$Scorers$txt_Scorer"+ (i+1) +"*GEOM*TEXT SET "+ "" +"\0");
				}
			}
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Results_with_Scorers_V2$txt_Footer*GEOM*TEXT SET #HeroACT2026\0");
			
			previewCommand = "anim_FullFrames$In_Out 1.800 anim_FullFrames$In_Out$Essentials 1.800 anim_FullFrames$In_Out$Essentials$In 1.800 anim_FullFrames$In_Out$Header$In 1.800 "
					+ "anim_FullFrames$In_Out$Flags 1.800 anim_FullFrames$In_Out$Flags$In 1.800 anim_FullFrames$In_Out$Main 1.800 anim_FullFrames$In_Out$Main$In 1.800";
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		}
	}
	public void populateScoreUpdate(Socket session_socket,String viz_scene,HockeyService hockeyService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l=4;
			String h1="",h2="",h3="",a1="",a2="",a3="";
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$Sponsor*ACTIVE SET 0\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$Team1$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ match.getHomeTeam().getTeamBadge() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$Team2$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO
					+ match.getAwayTeam().getTeamBadge() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$txt_Team1*GEOM*TEXT SET " 
					+ match.getHomeTeam().getTeamName1() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$txt_Team2*GEOM*TEXT SET " 
					+ match.getAwayTeam().getTeamName1() +"\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$ScoreGrp$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + "\0");
			
			System.out.println("viz_scene ==== " + viz_scene);
			if(viz_scene.equalsIgnoreCase("with")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info*FUNCTION*Omo*vis_con SET 1\0");
				
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info*FUNCTION*Omo*vis_con SET 0\0");
				
				if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET QUARTER 1\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET QUARTER 2\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET QUARTER 3\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET QUARTER 4\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET HALF TIME\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET FULL TIME\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("end1")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET END OF QUARTER 1\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("end3")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET END OF QUARTER 3\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET EXTRA TIME 1\0");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET EXTRA TIME 2\0");
				}
			}
//			if(match.getHomeTeamScore() == 0 ) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Select_data"
//						+ "*FUNCTION*Omo*vis_con SET 0\0");
//				TimeUnit.MILLISECONDS.sleep(l);
//			}else if(match.getHomeTeamScore() > 0 && match.getHomeTeamScore() <= 2) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Select_data"
//						+ "*FUNCTION*Omo*vis_con SET 1\0");
//				TimeUnit.MILLISECONDS.sleep(l);
//			}else if(match.getHomeTeamScore() > 2 && match.getHomeTeamScore() <= 4) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Select_data"
//						+ "*FUNCTION*Omo*vis_con SET 2\0");
//				TimeUnit.MILLISECONDS.sleep(l);
//			}else {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Select_data"
//						+ "*FUNCTION*Omo*vis_con SET 3\0");
//				TimeUnit.MILLISECONDS.sleep(l);
//			}
//			
//			if(match.getAwayTeamScore() == 0 ) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Select_data"
//						+ "*FUNCTION*Omo*vis_con SET 0\0");
//				TimeUnit.MILLISECONDS.sleep(l);
//			}else if(match.getAwayTeamScore() > 0 && match.getAwayTeamScore() <= 2) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Select_data"
//						+ "*FUNCTION*Omo*vis_con SET 1\0");
//				TimeUnit.MILLISECONDS.sleep(l);
//			}else if(match.getAwayTeamScore() > 2 && match.getAwayTeamScore() <= 4) {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Select_data"
//						+ "*FUNCTION*Omo*vis_con SET 2\0");
//				TimeUnit.MILLISECONDS.sleep(l);
//			}else {
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Select_data"
//						+ "*FUNCTION*Omo*vis_con SET 3\0");
//				TimeUnit.MILLISECONDS.sleep(l);
//			}
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			List<Integer> plyr_ids = new ArrayList<Integer>();
			boolean plyr_exist = false;
 			String stats_txt = "",stats_txt_og = "";
			
			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
				
				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION"))) {
					
					plyr_exist = false;
					for(Integer plyr_id : plyr_ids) {
						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.GOAL) 
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.OWN_GOAL)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION"))) {
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
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(HockeyUtil.PENALTY)
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase("PENALTY_CONVERSION"))) {

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
			
			if(!home_stats.isEmpty()) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$Recenlty_Scorer$Team1$txt_Scorer*GEOM*TEXT SET " 
						+ home_stats.get(home_stats.size() - 1) +"\0");
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$Recenlty_Scorer$Team1$txt_Scorer*GEOM*TEXT SET \0");
			}
			
			if(!away_stats.isEmpty()) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$Recenlty_Scorer$Team2$txt_Scorer*GEOM*TEXT SET " 
						+ away_stats.get(away_stats.size() - 1) +"\0");
			}else {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$Recenlty_Scorer$Team2$txt_Scorer*GEOM*TEXT SET \0");
			}
			
			
//			for (int i = 0; i < home_stats.size() && i < 6; i += 2) {
//			    int slot = (i / 2) + 1; // Data1, Data2, Data3
//			    String first  = home_stats.get(i);
//			    String second = (i + 1 < home_stats.size()) ? home_stats.get(i + 1) : "";
//
//			    String base = "-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$HomeTeam$Left_Team_Out$Top_Info$Data" + slot;
//
//			    print_writer.println(base + "$txt_Info01*GEOM*TEXT SET " + first + "\0");
//			    print_writer.println(base + "$txt_Info02*GEOM*TEXT SET " + second + "\0");
//			    print_writer.flush();
//
//			    TimeUnit.MILLISECONDS.sleep(l);
//			}
//			
//			for (int i = 0; i < away_stats.size() && i < 6; i += 2) {
//			    int slot = (i / 2) + 1; // Data1, Data2, Data3
//			    String first  = away_stats.get(i);
//			    String second = (i + 1 < away_stats.size()) ? away_stats.get(i + 1) : "";
//
//			    String base = "-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_ScoreLine$AwayTeam$Right_Team_Out$Top_Info$Data" + slot;
//
//			    print_writer.println(base + "$txt_Info01*GEOM*TEXT SET " + first + "\0");
//			    print_writer.println(base + "$txt_Info02*GEOM*TEXT SET " + second + "\0");
//			    print_writer.flush();
//
//			    TimeUnit.MILLISECONDS.sleep(l);
//			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_MatchID$In_Out 1.240 anim_MatchID$In_Out$In 1.240 \0");
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
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + HOCKEY_LOGO + match.getHomeTeam().getTeamName4() + 
					HockeyUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + HOCKEY_LOGO + match.getAwayTeam().getTeamName4() + 
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
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$Sponsor*ACTIVE SET 0\0");
			
			String match_name="",newDate = "",date_data = "",text = "";
			
			String[] dateSuffix = {
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
					
					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
					
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
					
					"th", "st"
			};
			
			Calendar cal_bengal = Calendar.getInstance();
			cal_bengal.add(Calendar.DATE, +1);
			
			for(Fixture fixtures :fixture) {
				if(fixtures.getMatchnumber() == Integer.valueOf(fixt)) {
					if(fixtures.getMatchnumber() > 9) {
						match_name = fixtures.getMatchfilename();
					}else {
						match_name = "MATCH " + fixtures.getMatchnumber();
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
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$Team1$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO
							+ team.get(fixtures.getHometeamid() - 1).getTeamBadge() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$Team2$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO
							+ team.get(fixtures.getAwayteamid() - 1).getTeamBadge() +"\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$txt_Team1*GEOM*TEXT SET " 
							+ team.get(fixtures.getHometeamid() - 1).getTeamName1() +"\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$TOP$txt_Team2*GEOM*TEXT SET " 
							+ team.get(fixtures.getAwayteamid() - 1).getTeamName1() +"\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$ScoreGrp$txt_Score*GEOM*TEXT SET vs\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info*FUNCTION*Omo*vis_con SET 0\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_Lt_MatchId$MatchID$BOTTOM$select_Info$BaseInfo$txt_Info*GEOM*TEXT SET " 
							+ text +"\0");
				}
			}
		}
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_MatchID$In_Out 1.240 anim_MatchID$In_Out$In 1.240 \0");
		
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
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle*FUNCTION*Omo*vis_con SET 3 \0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side" + whichSide + "$select_Flag*FUNCTION*Omo*vis_con SET 0\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
					+ "$txt_Title*GEOM*TEXT SET " + match.getTournament() + "\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
					+ "$Tournament_Logo*TEXTURE*IMAGE SET " + eventlogo_path + "EventLogo" + "\0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics*FUNCTION*Omo*vis_con SET 2 \0");
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures$Line_GRP$select_LineNumber*FUNCTION*Omo*vis_con SET  6\0");
			
			if (day.equalsIgnoreCase("TODAY")) {
			    Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());

			    boolean hasTodayMatch = false;
			    boolean anyComplete   = false;

			    for (int i = 0; i < fix.size(); i++) {
			        if (fix.get(i).getDate().equalsIgnoreCase(Date)) {
			            hasTodayMatch = true;

			            String winner = fix.get(i).getWinnerteam();
			            String margin = fix.get(i).getMargin();

			            // a match is complete when both winner and margin have a value
			            if (winner != null && !winner.trim().isEmpty()
			                    && margin != null && !margin.trim().isEmpty()) {
			                anyComplete = true;
			                break; // one complete match is enough
			            }
			        }
			    }

			    if (hasTodayMatch) {
			        String header = anyComplete ? "TODAY'S FIXTURE AND RESULT" : "TODAY'S FIXTURE";

			        print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide
			                + "$select_Graphics$Fixtures$Line_GRP$txt_Header*GEOM*TEXT SET " + header + "\0");
			    }
			} else if (day.toUpperCase().equalsIgnoreCase("TOMORROW")) {
				cal.add(Calendar.DATE, +1);
				Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
						+ "$Line_GRP$txt_Header*GEOM*TEXT SET TOMORROW'S FIXTURE\0");
			} else if (day.toUpperCase().equalsIgnoreCase("DAY_AFTER_TOMORROW")) {
				cal.add(Calendar.DATE, +2);
				Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());

				newDate = Date.split("-")[0];
				if (Integer.valueOf(newDate) < 10) {
					newDate = newDate.replaceFirst("0", "");
				}
				
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
						+ "$Line_GRP$txt_Header*GEOM*TEXT SET " + newDate + dateSuffix[Integer.valueOf(newDate)] + " " + 
						Month.of(Integer.valueOf(Date.split("-")[1])) + " 2026\0");
			}

			for (int i = 0; i <= fix.size() - 1; i++) {
				if (fix.get(i).getDate().equalsIgnoreCase(Date)) {
					
					row = row + 1;
					
					if(fix.get(i).getMatchnumber() > 9) {
						match_name = fix.get(i).getMatchfilename();
					}else {
						match_name = "MATCH " + fix.get(i).getMatchnumber();
					}
					
					if(fix.get(i).getWinnerteam() != null && fix.get(i).getMargin() !=null) {
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$select_Info*FUNCTION*Omo*vis_con SET 2\0");

						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$Results$V*GEOM*TEXT SET -\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET FULL TIME\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$Results$txt_Score1*GEOM*TEXT SET " + fix.get(i).getMargin().split("-")[0] + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$Results$txt_Score2*GEOM*TEXT SET " + fix.get(i).getMargin().split("-")[1] + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$Results$txt_Team1*GEOM*TEXT SET " + fix.get(i).getHome_Team().getTeamName1() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$Results$txt_Team2*GEOM*TEXT SET " + fix.get(i).getAway_Team().getTeamName1() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$Results$img_Flag1*TEXTURE*IMAGE SET " + HOCKEY_LOGO + fix.get(i).getHome_Team().getTeamBadge() + "\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row+1) + "$Results$img_Flag2*TEXTURE*IMAGE SET "  + HOCKEY_LOGO + fix.get(i).getAway_Team().getTeamBadge() + "\0");
						
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row ) + "$select_Info*FUNCTION*Omo*vis_con SET 0\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row) + "$select_Info$Time$txt_Info1*GEOM*TEXT SET\0");
						print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
								+ "$Line_GRP$Line" + (row) + "$select_Info$Time$txt_Info2*GEOM*TEXT SET "+ match_name + "\0");
					}else {
						if(match.getMatchFileName().replace(".json", "").equalsIgnoreCase(fix.get(i).getMatchfilename())) {
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$select_Info*FUNCTION*Omo*vis_con SET 2\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Results$V*GEOM*TEXT SET -\0");
							
							if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET QUARTER 1\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET QUARTER 2\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET QUARTER 3\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET QUARTER 4\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET HALF TIME\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET FULL TIME\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET EXTRA TIME 1\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET EXTRA TIME 2\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase("END1")) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET END OF QUARTER 1\0");
							}else if(match.getClock().getMatchHalves().equalsIgnoreCase("END3")) {
								print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
										+ "$Line_GRP$Line" + (row+1) + "$Results$txt_GamePart*GEOM*TEXT SET END OF QUARTER 3\0");
							}
							
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Results$txt_Score1*GEOM*TEXT SET " + match.getHomeTeamScore() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Results$txt_Score2*GEOM*TEXT SET " + match.getAwayTeamScore() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Results$txt_Team1*GEOM*TEXT SET " +  match.getHomeTeam().getTeamName1() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Results$txt_Team2*GEOM*TEXT SET " +  match.getAwayTeam().getTeamName1() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Results$img_Flag1*TEXTURE*IMAGE SET " + HOCKEY_LOGO + match.getHomeTeam().getTeamBadge() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Results$img_Flag2*TEXTURE*IMAGE SET "  + HOCKEY_LOGO + match.getAwayTeam().getTeamBadge() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row) + "$select_Info*FUNCTION*Omo*vis_con SET 0\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row) + "$select_Info$Time$txt_Info1*GEOM*TEXT SET\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row) + "$select_Info$Time$txt_Info2*GEOM*TEXT SET "+ match_name + "\0");
						}else {
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$select_Info*FUNCTION*Omo*vis_con SET 1\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Fixture$V*GEOM*TEXT SET -\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Fixture$txt_Team1*GEOM*TEXT SET " +  fix.get(i).getHome_Team().getTeamName1() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Fixture$txt_Team2*GEOM*TEXT SET " +  fix.get(i).getAway_Team().getTeamName1() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Fixture$img_Flag1*TEXTURE*IMAGE SET " + HOCKEY_LOGO + fix.get(i).getHome_Team().getTeamBadge() + "\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row+1) + "$Fixture$img_Flag2*TEXTURE*IMAGE SET "  + HOCKEY_LOGO + fix.get(i).getAway_Team().getTeamBadge() + "\0");
							
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row) + "$select_Info*FUNCTION*Omo*vis_con SET 0\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row) + "$select_Info$Time$txt_Info1*GEOM*TEXT SET\0");
							print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
									+ "$Line_GRP$Line" + (row) + "$select_Info$Time$txt_Info2*GEOM*TEXT SET "+ match_name + "\0");
						}
					}
					row = row + 1;
				}
			}
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Fixtures"
					+ "$txt_Footer*GEOM*TEXT SET #HeroACT2026\0");
			
			if(whichSide == 1) {
				previewCommand = "anim_FullFrames$In_Out 1.800 anim_FullFrames$In_Out$Essentials 1.800 anim_FullFrames$In_Out$Essentials$In 1.800 anim_FullFrames$In_Out$Header$In 1.800 "
						+ "anim_FullFrames$In_Out$Flags 1.800 anim_FullFrames$In_Out$Flags$In 1.800 anim_FullFrames$In_Out$Main 1.800 anim_FullFrames$In_Out$Main$In 1.800";
			}else {
				previewCommand = "change_Fullframes 1.980 change_Fullframes$Essentials 1.380 change_Fullframes$Header 1.300 change_Fullframes$Flags 1.300 "
						+ "change_Fullframes$Main 1.980";
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		}
	}
	public void populateStaff(Socket session_socket,String viz_scene, Staff st,List<Team> team ,Match match, String selectedbroadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line1$txt_Text1*GEOM*TEXT SET " 
					+ st.getName().toUpperCase() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line1$txt_Text2*GEOM*TEXT SET \0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_UmpireSuper$Line2$txt_Info*GEOM*TEXT SET " + 
					st.getRole().toUpperCase() + ", " + team.get(st.getClubId() - 1).getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_UmpireSuper$In_Out 1.000 anim_UmpireSuper$In_Out$In 1.000 \0");
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
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + HOCKEY_LOGO + TM.getTeamName4().toUpperCase() + HockeyUtil.PNG_EXTENSION + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName "+ TM.getTeamName1().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + HOCKEY_LOGO + TM.getTeamName4().toUpperCase() + HockeyUtil.PNG_EXTENSION + ";");
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
	
	public void populatePointsTable(Socket session_socket,String viz_sence_path,List<LeagueTeam> point_table1,String point_table2
			, List<Team> team,String session_selected_broadcaster,Match match,int whichSide) throws InterruptedException, IOException 
	{		
		String previewCommand ="";
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		int row_no=0,l=4, omo_num = 0;
		String cont_name = "", data = "";
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle*FUNCTION*Omo*vis_con SET 3 \0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags*ACTIVE SET 0\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Flags$Side" + whichSide + "$select_Flag*FUNCTION*Omo*vis_con SET 1\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
				+ "$txt_Title*GEOM*TEXT SET POINTS TABLE\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Header-UD$Header$FF_Title_SEL$Side" + whichSide + "$select_HeaderStyle$TitleCenter"
				+ "$Tournament_Logo*TEXTURE*IMAGE SET " + eventlogo_path + "EventLogo" + "\0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side1$select_Graphics*FUNCTION*Omo*vis_con SET 5\0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Group*GEOM*TEXT SET "+ "" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Play*GEOM*TEXT SET "+ "P" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Win*GEOM*TEXT SET "+ "W" +"\0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Draw*GEOM*TEXT SET "+ "L" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Lost*GEOM*TEXT SET "+ "D" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_GoalDifference*GEOM*TEXT SET "+ "GD" +"\0");
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$TitleGrp$txt_Points*GEOM*TEXT SET "+ "PTS" +"\0");
		
		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
				+ "$select_LineNumber*FUNCTION*Omo*vis_con SET 6\0");
		
		for(int i = 0; i <= point_table1.size() - 1 ; i++) {
			row_no = row_no + 1;
			
			if (match.getHomeTeam().getTeamBadge().toUpperCase().contains(point_table1.get(i).getTeamName().toUpperCase())
					|| match.getHomeTeam().getTeamName1().toUpperCase().contains(point_table1.get(i).getTeamName().toUpperCase())
					|| match.getAwayTeam().getTeamBadge().toUpperCase().contains(point_table1.get(i).getTeamName().toUpperCase())
					|| match.getAwayTeam().getTeamName1().toUpperCase().contains(point_table1.get(i).getTeamName().toUpperCase())) {
				omo_num = 1;
				cont_name = "$Highlight";
				data = "$img_Text1";
			} else {
				omo_num = 0;
				cont_name = "$Dehighlight";
				data = "$img_Text2";
			}
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics"
					+ "$Standings$select_LineNumber$Line" + (i+1) + "$select_Highlight*FUNCTION*Omo*vis_con SET " + omo_num + "\0");
			
			for(Team tm : team) {
				if(tm.getTeamName1().equalsIgnoreCase(point_table1.get(i).getTeamName())) {
					print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
							+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$img_Flag*TEXTURE*IMAGE SET " + HOCKEY_LOGO 
							+ tm.getTeamBadge() +"\0");
				}
			}
			
			if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
						+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$Text$txt_TeamName*GEOM*TEXT SET "+ point_table1.get(i).getTeamName() +"\0");
			}else {
				print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
						+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$Text$txt_TeamName*GEOM*TEXT SET "+ point_table1.get(i).getTeamName() + " (Q)" +"\0");
			}
			
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$Text$txt_Play*GEOM*TEXT SET "+ point_table1.get(i).getPlayed() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$Text$txt_Win*GEOM*TEXT SET "+ point_table1.get(i).getWon() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$Text$txt_Draw*GEOM*TEXT SET "+  point_table1.get(i).getLost() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$Text$txt_Lost*GEOM*TEXT SET "+ point_table1.get(i).getDrawn() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$Text$txt_GoalDifference*GEOM*TEXT SET "+ point_table1.get(i).getGD() +"\0");
			print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$gfx_FullFrames$Main$Side" + whichSide + "$select_Graphics$Standings"
					+ "$select_LineNumber$Line" + row_no + "$select_Highlight$" + cont_name + "$Text$txt_Points*GEOM*TEXT SET "+ point_table1.get(i).getPoints() +"\0");
			

		}
		
		previewCommand = "anim_FullFrames$In_Out 1.800 anim_FullFrames$In_Out$Essentials 1.800 anim_FullFrames$In_Out$Essentials$In 1.800 anim_FullFrames$In_Out$Header$In 1.800 "
				+ "anim_FullFrames$In_Out$Flags 1.800 anim_FullFrames$In_Out$Flags$In 1.800 anim_FullFrames$In_Out$Main 1.800 anim_FullFrames$In_Out$Main$In 1.800";
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_FullFrames " + "C:/Temp/Preview.tga " + previewCommand + "\0");
		
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
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_Ident$Essentials 1.140 anim_Ident$Essentials$In_Out 1.140 "
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
	public String populateScorebugTeams(Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv,int team) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null"; 
		} else {
			
			String pName = "";
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
				hbase = HOCKEY_BASE1;
				htext = HOCKEY_TEXT1;
			}else if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
				hbase = HOCKEY_BASE2;
				htext = HOCKEY_TEXT2;
			}
			
			if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
				abase = HOCKEY_BASE1;
				atext = HOCKEY_TEXT1;
			}else if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
				abase = HOCKEY_BASE2;
				atext = HOCKEY_TEXT2;
			}
			
			if(scorebug.isScorebug_on_screen() == true) {
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$Info$select_InfoType*FUNCTION*Omo*vis_con SET 2\0");
				
				if(team == match.getHomeTeamId()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$img_Text*TEXTURE*IMAGE SET " + htext
							+ match.getHomeTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$img_Base*TEXTURE*IMAGE SET " + hbase
							+ match.getHomeTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head1$img_Text*TEXTURE*IMAGE SET " + htext
							+ match.getHomeTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head1$img_Base*TEXTURE*IMAGE SET " + hbase
							+ match.getHomeTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head2$img_Text*TEXTURE*IMAGE SET " + htext
							+ match.getHomeTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head2$img_Base*TEXTURE*IMAGE SET " + hbase
							+ match.getHomeTeam().getTeamBadge() + "\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head1$txt_Head*GEOM*TEXT SET STARTING XI\0");
					
					for(Player hs : match.getHomeSquad()) {
						pName = pName + ", " + hs.getTicker_name();
					}
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head1$txt_LineUp*GEOM*TEXT SET " + pName.replaceFirst(", ", "") + "\0");
					
					pName = "";
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head2$txt_Head*GEOM*TEXT SET SUBSTITUTES\0");
					if(match.getHomeSubstitutes() != null) {
						for(Player hsub : match.getHomeSubstitutes()) {
							pName = pName + ", " + hsub.getTicker_name();
						}
					}
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head2$txt_Subs*GEOM*TEXT SET " + pName.replaceFirst(", ", "") + "\0");
					
				}else {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$img_Text*TEXTURE*IMAGE SET " + atext
							+ match.getAwayTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$img_Base*TEXTURE*IMAGE SET " + abase
							+ match.getAwayTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head1$img_Text*TEXTURE*IMAGE SET " + atext
							+ match.getAwayTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head1$img_Base*TEXTURE*IMAGE SET " + abase
							+ match.getAwayTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head2$img_Text*TEXTURE*IMAGE SET " + atext
							+ match.getAwayTeam().getTeamBadge() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head2$img_Base*TEXTURE*IMAGE SET " + abase
							+ match.getAwayTeam().getTeamBadge() + "\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head1$txt_Head*GEOM*TEXT SET PLAYING XI\0");
					
					for(Player as : match.getAwaySquad()) {
						pName = pName + ", " + as.getTicker_name();
					}
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head1$txt_LineUp*GEOM*TEXT SET " + pName.replaceFirst(", ", "") + "\0");
					
					pName = "";
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head2$txt_Head*GEOM*TEXT SET SUBSTITUTES\0");
					if(match.getAwaySubstitutes() != null) {
						for(Player asub : match.getAwaySubstitutes()) {
							pName = pName + ", " + asub.getTicker_name();
						}
					}
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_ScoreTicker$02=TeamCrawler$Head2$txt_Subs*GEOM*TEXT SET " + pName.replaceFirst(", ", "") + "\0");
					
				}
				
				return this.status = "OK";
			}else {
				return this.status = "Error: Scorebug is not on Screen";
			}
		}
		return "";
	}
	public void populateScoreCard(Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv,String which_Card) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null"; 
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			switch(which_Card) {
			case "HOME_YELLOW1":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$YellowCard_1 START\0");
				break;
			case "HOME_YELLOW2":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$YellowCard_2 START\0");
				break;
			case "HOME_RED1":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$RedCard_1 START\0");
				break;
			case "HOME_RED2":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$RedCard_2 START\0");
				break;
			case "HOME_GREEN1":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$GreenCard_1 START\0");
				break;
			case "HOME_GREEN2":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$GreenCard_2 START\0");
				break;
				
			case "AWAY_YELLOW1":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$YellowCard_1 START\0");
				break;
			case "AWAY_YELLOW2":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$YellowCard_2 START\0");
				break;
			case "AWAY_RED1":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$RedCard_1 START\0");
				break;
			case "AWAY_RED2":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$RedCard_2 START\0");
				break;
			case "AWAY_GREEN1":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$GreenCard_1 START\0");
				break;
			case "AWAY_GREEN2":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$GreenCard_2 START\0");
				break;
			}
			for(int i=1; i<=5; i++) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$HomeTeam$Left_Team_Out$Top_Info$" + getQuarterName(i)
				+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$gfx_LT_Penalty$AwayTeam$Right_Team_Out$Top_Info$" + getQuarterName(i)
				+ "$Select*FUNCTION*Omo*vis_con SET 0\0");
			}
		}
	}
	
	public void populateScoreCardOut(Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv,String which_Card) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null"; 
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		     
			switch(which_Card) {
			case "HOME_YELLOW1_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$YellowCard_1 CONTINUE\0");
				break;
			case "HOME_YELLOW2_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$YellowCard_2 CONTINUE\0");
				break;
			case "HOME_RED1_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$RedCard_1 CONTINUE\0");
				break;
			case "HOME_RED2_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$RedCard_2 CONTINUE\0");
				break;
			case "HOME_GREEN1_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$GreenCard_1 CONTINUE\0");
				break;
			case "HOME_GREEN2_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team1$GreenCard_2 CONTINUE\0");
				break;
				
			case "AWAY_YELLOW1_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$YellowCard_1 CONTINUE\0");
				break;
			case "AWAY_YELLOW2_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$YellowCard_2 CONTINUE\0");
				break;
			case "AWAY_RED1_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$RedCard_1 CONTINUE\0");
				break;
			case "AWAY_RED2_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$RedCard_2 CONTINUE\0");
				break;
			case "AWAY_GREEN1_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$GreenCard_1 CONTINUE\0");
				break;
			case "AWAY_GREEN2_OUT":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*anim_Cards$Team2$GreenCard_2 CONTINUE\0");
				break;
			}
		}
	}
	
	public void populateHighlight(Socket session_socket,Match match, String selectedbroadcaster,HockeyService hockeyserv) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
				hbase = HOCKEY_BASE1;
				htext = HOCKEY_TEXT1;
			}else if(match.getHomeTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
				hbase = HOCKEY_BASE2;
				htext = HOCKEY_TEXT2;
			}
			
			if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_1")) {
				abase = HOCKEY_BASE1;
				atext = HOCKEY_TEXT1;
			}else if(match.getAwayTeamJerseyColor().equalsIgnoreCase("Jersey_2")) {
				abase = HOCKEY_BASE2;
				atext = HOCKEY_TEXT2;
			}
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter1")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET Q1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter2")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET Q2\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter3")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET Q3\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("quarter4")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET Q4\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET HT\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET FT\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("end1")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET END OF Q1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("end2")) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET END OF Q2\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA1)) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET ET1\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(HockeyUtil.EXTRA2)) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Clock$txt_GamePart*GEOM*TEXT SET ET2\0");
			}
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Team1$img_Text*TEXTURE*IMAGE SET " + htext + match.getHomeTeam().getTeamBadge() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Team1$img_Base*TEXTURE*IMAGE SET " + hbase + match.getHomeTeam().getTeamBadge() + "\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Team2$img_Text*TEXTURE*IMAGE SET " + atext + match.getAwayTeam().getTeamBadge() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Team2$img_Base*TEXTURE*IMAGE SET " + abase + match.getAwayTeam().getTeamBadge() + "\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Team1$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName4() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Team2$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName4() + "\0");
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Score$Team1$Side1$txt_Score*GEOM*TEXT SET " 
					+ match.getHomeTeamScore() +"\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Bug_MatchScore$Score$Team2$Side1$txt_Score*GEOM*TEXT SET " 
					+ match.getAwayTeamScore() +"\0");
		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/gfx_Overlays C:/Temp/Preview.tga anim_BugMatchScore$In_Out 0.500\0");
	}
	// inside PUNJAB class

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
	
	public String[] readReferral(String filePath) throws IOException {
	    for (String line : Files.readAllLines(Paths.get(filePath))) {
	        line = line.trim();
	        if (line.isEmpty()) continue;

	        int first = line.indexOf('-');
	        int last = line.lastIndexOf('-');
	        if (first == -1 || first == last) continue; // invalid format

	        String home = line.substring(0, first).trim();   // left data
	        String away = line.substring(last + 1).trim();   // right data
	        return new String[] { home, away };
	    }
	    return new String[] { "0", "0" }; // empty/invalid file
	}
	public static List<String[]> readTeamPointsExcel(String filePath) throws IOException {
	    List<String[]> rows = new ArrayList<>();

	    if (!Files.exists(Paths.get(filePath))) {
	        return rows;                                   // empty list if file missing
	    }

	    try (InputStream is = Files.newInputStream(Paths.get(filePath));
	         Workbook wb = WorkbookFactory.create(is)) {

	        Sheet sheet = wb.getSheetAt(0);                // first sheet
	        DataFormatter fmt = new DataFormatter();       // reads numbers/text as plain strings

	        for (int r = 1; r <= sheet.getLastRowNum(); r++) {   // r = 1 skips the header row
	            Row row = sheet.getRow(r);
	            if (row == null) continue;

	            String team = fmt.formatCellValue(row.getCell(0)).trim();
	            if (team.isEmpty()) continue;

	            rows.add(new String[] {
	                team,                                          // [0] Team
	                fmt.formatCellValue(row.getCell(1)).trim(),    // [1] Player
	                fmt.formatCellValue(row.getCell(2)).trim(),    // [2] FG
	                fmt.formatCellValue(row.getCell(3)).trim(),    // [3] PC
	                fmt.formatCellValue(row.getCell(4)).trim(),    // [4] PS
	                fmt.formatCellValue(row.getCell(5)).trim()     // [5] Total
	            });
	        }
	    }
	    return rows;
	}
	
//	public record Scorer(int playerId, String name, String team, int goals) {}
//
//	private static final ObjectMapper MAPPER = new ObjectMapper();
//	private static final String[] SQUAD_KEYS = { "homeSquad", "homeSubstitutes", "awaySquad",
//			"awaySubstitutes", "homeOtherSquad", "awayOtherSquad" };

	
//	public static List<Scorer> getAllGoalScorers(String folderPath) {
//		Map<Integer, Integer> goalsById = new LinkedHashMap<>();   
//		Map<Integer, String[]> infoById = new HashMap<>();         
//
//		for (File f : getJsonFiles(folderPath)) {
//			JsonNode root = readMatchFile(f);
//			if (root == null) continue;
//
//			addPlayerInfo(root, infoById);
//			addGoals(root, goalsById);
//		}
//
//		List<Scorer> result = new ArrayList<>();
//		for (Map.Entry<Integer, Integer> e : goalsById.entrySet()) {
//			String[] info = infoById.get(e.getKey());
//			String name = (info != null) ? info[0] : "PLAYER " + e.getKey();
//			String team = (info != null) ? info[1] : "";
//			result.add(new Scorer(e.getKey(), name, team, e.getValue()));
//		}
//
//		result.sort((a, b) -> b.goals() != a.goals()
//				? Integer.compare(b.goals(), a.goals())
//				: a.name().compareTo(b.name()));
//		return result;
//	}
//
//	private static File[] getJsonFiles(String folderPath) {
//		File[] files = new File(folderPath).listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
//		return files == null ? new File[0] : files;
//	}
//
//	private static JsonNode readMatchFile(File f) {
//		try {
//			return MAPPER.readTree(f);
//		} catch (IOException e) {
//			return null;
//		}
//	}
//
//	// HELPER 3: collect playerId -> {name, team} from one match file
//	private static void addPlayerInfo(JsonNode root, Map<Integer, String[]> infoById) {
//		int homeId = root.path("homeTeam").path("teamId").asInt();
//		String homeName = root.path("homeTeam").path("teamName1").asText();
//		String awayName = root.path("awayTeam").path("teamName1").asText();
//
//		for (String key : SQUAD_KEYS) {
//			for (JsonNode p : root.path(key)) {
//				String team = (p.path("teamId").asInt() == homeId) ? homeName : awayName;
//				infoById.putIfAbsent(p.path("playerId").asInt(),
//						new String[] { p.path("full_name").asText().trim(), team });
//			}
//		}
//	}
//
//	// HELPER 4: add the goals of one file to the running totals (own goals excluded)
//	private static void addGoals(JsonNode root, Map<Integer, Integer> goalsById) {
//		for (JsonNode ms : root.path("matchStats")) {
//			String type = ms.path("stats_type").asText();
//			if (!type.equalsIgnoreCase(HockeyUtil.GOAL) && !type.equalsIgnoreCase(HockeyUtil.PENALTY)) continue;
//
//			goalsById.merge(ms.path("playerId").asInt(), ms.path("statsCount").asInt(1), Integer::sum);
//		}
//	}
	
//	public class Scorers {
//	    public String name, teamCode;
//	    public int teamId, fieldGoals, pcGoals, psGoals, total;
//	}
//
//	public List<Scorers> readIndividualGoalScorers(String jsonPath) throws IOException {
//	    List<Scorers> list = new ArrayList<>();
//	    JsonNode arr = new ObjectMapper().readTree(new File(jsonPath)).path("individualGoalScorers");
//
//	    for (JsonNode n : arr) {
//	        Scorers s = new Scorers();
//	        s.name       = n.path("player").asText();
//	        s.teamCode   = n.path("team").asText();
//	        s.fieldGoals = n.path("fieldGoals").asInt();
//	        s.pcGoals    = n.path("penaltyCornerGoals").asInt();
//	        s.psGoals    = n.path("penaltyStrokeGoals").asInt();
//	        s.total      = n.path("goals").asInt();
//	        list.add(s);
//	    }
//	    list.sort(Comparator.comparingInt((Scorers s) -> s.total).reversed());
//	    return list;
//	}
	
	public static class Scorers {
	    public String name, teamCode;
	    public int teamId, fieldGoals, pcGoals, psGoals, total;

	    // ---------- 1. read JSON ----------
	    public List<Scorers> readIndividualGoalScorers(String jsonPath) throws IOException {
	        List<Scorers> list = new ArrayList<>();
	        JsonNode arr = new ObjectMapper().readTree(new File(jsonPath)).path("individualGoalScorers");

	        for (JsonNode n : arr) {
	            Scorers s = new Scorers();
	            s.name       = n.path("player").asText();
	            s.teamCode   = n.path("team").asText();
	            s.fieldGoals = n.path("fieldGoals").asInt();
	            s.pcGoals    = n.path("penaltyCornerGoals").asInt();
	            s.psGoals    = n.path("penaltyStrokeGoals").asInt();
	            s.total      = n.path("goals").asInt();
	            list.add(s);
	        }
	        list.sort(Comparator.comparingInt((Scorers s) -> s.total).reversed());
	        return list;
	    }

	    // ---------- 2. name key (word order does not matter) ----------
	    // "SINGH Harmanpreet" and "HARMANPREET SINGH" both become "HARMANPREET SINGH"
	    private static String nameKey(String name) {
	        String n = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");   // remove accents
	        String[] parts = n.toUpperCase().replaceAll("[^A-Z0-9 ]", " ").trim().split("\\s+");
	        Arrays.sort(parts);
	        return String.join(" ", parts);
	    }

	    // ---------- 3. match JSON names with DB players ----------
	    public void matchWithDb(List<Scorers> list, HockeyService hockeyService) {
	        Map<String, Player> dbPlayers = new HashMap<>();   // key -> Player

	        for (Player pl : hockeyService.getAllPlayer()) {
	            String full = pl.getFull_name();               // <-- change to your real getter
	            if (full != null && !full.isBlank()) {
	                dbPlayers.putIfAbsent(nameKey(full), pl);
	            }
	        }

	        for (Scorers s : list) {
	            Player pl = dbPlayers.get(nameKey(s.name));
	            if (pl != null) {
	                s.name = pl.getFull_name().trim();         // DB name
	                // s.teamId = pl.getTeamId();              // optional, if your Player class has it
	            } else {
	                System.out.println("NOT FOUND IN DB: " + s.name + " (" + s.teamCode + ")");
	            }
	        }
	    }

	    // ---------- 4. usage ----------
	    public List<Scorers> getScorers(String jsonPath, HockeyService hockeyService) throws IOException {
	        List<Scorers> list = readIndividualGoalScorers(jsonPath);
	        matchWithDb(list, hockeyService);
	        return list;
	    }
	}
	public record Scorer(int playerId, String name, int teamId, String team,
	        int fieldGoals, int pcGoals, int psGoals, int total) {}

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String[] SQUAD_KEYS = { "homeSquad", "homeSubstitutes", "awaySquad",
	        "awaySubstitutes", "homeOtherSquad", "awayOtherSquad" };

	private static final String FG_TYPE = "goal";                 // normal / field goal
	private static final String PC_TYPE = "penalty_Conversion";   // penalty corner goal
	private static final String PS_TYPE = "penalty";              // penalty stroke goal

	public static List<Scorer> getAllGoalScorers(String folderPath) {
	    Map<Integer, int[]> goalsById = new LinkedHashMap<>();       // {fg, pc, ps}
	    Map<Integer, String[]> infoById = new HashMap<>();           // {name, teamName, teamId}
	    Map<Integer, Integer> firstMatchById = new HashMap<>();      // playerId -> first file order he scored in

	    File[] files = getJsonFiles(folderPath);   // folder order, top to bottom (no sorting)

	    int order = 0;
	    for (File f : files) {
	        order++;
	        JsonNode root = readMatchFile(f);
	        if (root == null) continue;

	        addPlayerInfo(root, infoById);
	        addGoals(root, goalsById, firstMatchById, order);
	    }

	    List<Scorer> result = new ArrayList<>();
	    for (Map.Entry<Integer, int[]> e : goalsById.entrySet()) {
	        int id = e.getKey();
	        int[] g = e.getValue();
	        String[] info = infoById.get(id);

	        String name = (info != null) ? info[0] : "PLAYER " + id;
	        String team = (info != null) ? info[1] : "";
	        int teamId  = (info != null) ? Integer.parseInt(info[2]) : -1;

	        result.add(new Scorer(id, name, teamId, team, g[0], g[1], g[2], g[0] + g[1] + g[2]));
	    }

	    result.sort(Comparator
	            .comparingInt(Scorer::fieldGoals).reversed()
	            .thenComparing(Comparator.comparingInt(Scorer::pcGoals).reversed())
	            .thenComparing(Comparator.comparingInt(Scorer::psGoals).reversed())
	            .thenComparingInt(s -> firstMatchById.get(s.playerId()))   // still tied: earlier file first
	            .thenComparing(Scorer::name));
	    
//	    result.sort(Comparator
//	            .comparingInt(Scorer::total).reversed()
//	            .thenComparingInt(s -> firstMatchById.get(s.playerId()))   // scored in an earlier file = higher
//	            .thenComparing(Scorer::name));
	    return result;
	}

	private static File[] getJsonFiles(String folderPath) {
	    File[] files = new File(folderPath).listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
	    return files == null ? new File[0] : files;
	}

	private static JsonNode readMatchFile(File f) {
	    try {
	        return MAPPER.readTree(f);
	    } catch (IOException e) {
	        return null;
	    }
	}

	// playerId -> {name, teamName, teamId}
	private static void addPlayerInfo(JsonNode root, Map<Integer, String[]> infoById) {
	    int homeId = root.path("homeTeam").path("teamId").asInt();
	    String homeName = root.path("homeTeam").path("teamName1").asText();
	    String awayName = root.path("awayTeam").path("teamName1").asText();

	    for (String key : SQUAD_KEYS) {
	        for (JsonNode p : root.path(key)) {
	            int teamId = p.path("teamId").asInt();
	            String team = (teamId == homeId) ? homeName : awayName;
	            infoById.putIfAbsent(p.path("playerId").asInt(),
	                    new String[] { p.path("full_name").asText().trim(), team, String.valueOf(teamId) });
	        }
	    }
	}

	// adds one file's goals into {fg, pc, ps} per player and remembers the first file he scored in
	private static void addGoals(JsonNode root, Map<Integer, int[]> goalsById,
	        Map<Integer, Integer> firstMatchById, int order) {
	    for (JsonNode ms : root.path("matchStats")) {
	        String type = ms.path("stats_type").asText();

	        int idx;
	        if (type.equalsIgnoreCase(FG_TYPE))      idx = 0;
	        else if (type.equalsIgnoreCase(PC_TYPE)) idx = 1;
	        else if (type.equalsIgnoreCase(PS_TYPE)) idx = 2;
	        else continue;

	        int pid = ms.path("playerId").asInt();
	        goalsById.computeIfAbsent(pid, k -> new int[3])[idx] += ms.path("statsCount").asInt(1);
	        firstMatchById.merge(pid, order, Math::min);
	    }
	}
	
	public static void writeScorersFile(String folderPath, String outputFilePath) throws IOException {
	    List<Scorer> scorers = getAllGoalScorers(folderPath);   // already sorted

	    List<String> lines = new ArrayList<>();
	    for (int i = 0; i < scorers.size(); i++) {
	        Scorer s = scorers.get(i);
	        lines.add("playerId=" + s.playerId()
	                + ",Name=" + s.name()
	                + ",FudgeFactor=" + (i + 1));               // rank in sorted order: 1 = top scorer
	    }

	    Files.write(Paths.get(outputFilePath), lines, StandardCharsets.UTF_8);
	}
	
	private static long quarterMinute(long seconds) {   // seconds = remaining time
	    long elapsed = 900 - seconds;
	    elapsed = Math.max(0, Math.min(900, elapsed));
	    long minute = (elapsed / 60) + 1;
	    return Math.max(1, Math.min(15, minute));        // never 0, never 16
	}
}