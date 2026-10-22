package com.hockey.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;
import com.hockey.model.Clock;
import com.hockey.model.Configurations;
import com.hockey.model.Event;
import com.hockey.model.EventFile;
import com.hockey.model.Fixture;
import com.hockey.model.Match;
import com.hockey.model.MatchStats;
import com.hockey.model.Player;
import com.hockey.service.HockeyService;
import com.hockey.util.HockeyFunctions;
import com.hockey.util.HockeyUtil;
import com.opencsv.exceptions.CsvException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hockey.broadcaster.HOCKEY_KHELO_INDIA;
import com.hockey.broadcaster.PUNJAB;
import com.hockey.containers.Scene;
import com.hockey.containers.ScoreBug;

@Controller
public class IndexController 
{
	@Autowired
	HockeyService hockeyService;
	
	public static String expiry_date = "2026-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static Clock session_clock = new Clock();
	public static Configurations session_configurations;
	public static Match session_match;
	public static EventFile session_event;
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static HOCKEY_KHELO_INDIA session_khelo_india;
	public static PUNJAB session_punjab;
	public static List<Scene> session_selected_scenes;
	public static ObjectMapper objectMapper = new ObjectMapper();
	public static List<Fixture> session_fixture = new ArrayList<>();
	
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) 
		throws IOException, JAXBException 
	{
		
		if(current_date == null || current_date.isEmpty()) {
			current_date = HockeyFunctions.getOnlineCurrentDate();
		}
		model.addAttribute("session_viz_scenes", new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + 
				HockeyUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".via") && pathname.isFile();
		    }
		}));

		model.addAttribute("match_files", new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + 
				HockeyUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		
		if(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.CONFIGURATIONS_DIRECTORY + 
				HockeyUtil.OUTPUT_XML).exists()) {
			session_configurations = (Configurations)JAXBContext.newInstance(Configurations.class).createUnmarshaller().unmarshal(
					new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.CONFIGURATIONS_DIRECTORY + 
							HockeyUtil.OUTPUT_XML));
		} else {
			session_configurations = new Configurations();
			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.CONFIGURATIONS_DIRECTORY + 
							HockeyUtil.OUTPUT_XML));
		}
		
		model.addAttribute("session_configurations",session_configurations);
	
		return "initialise";
	}
	
	@RequestMapping(value = {"/setup"}, method = RequestMethod.POST)
	public String setupPage(ModelMap model) throws JAXBException, IllegalAccessException, 
		InvocationTargetException, IOException, ParseException  
	{
		model.addAttribute("match_files", new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + 
				HockeyUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		model.addAttribute("session_match", session_match);
		model.addAttribute("teams", hockeyService.getTeams());
		model.addAttribute("formations", hockeyService.getFormations());
		model.addAttribute("teamcolor", hockeyService.getTeamColors());
		model.addAttribute("grounds", hockeyService.getGrounds());
		model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));

		return "setup";
	}

	@RequestMapping(value = {"/match"}, method = {RequestMethod.POST,RequestMethod.GET})
	public String footballMatchPage(ModelMap model,
		@RequestParam(value = "selectedBroadcaster", required = false, defaultValue = "") String selectedBroadcaster,
		@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss,
		@RequestParam(value = "vizPortNumber", required = false, defaultValue = "") String vizPortNumber,
		@RequestParam(value = "vizScene", required = false, defaultValue = "") String vizScene)
			throws IOException, ParseException, JAXBException, InterruptedException  
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {

			session_selected_broadcaster = selectedBroadcaster;
			session_selected_scenes = new ArrayList<Scene>();
			if(!vizIPAddresss.trim().isEmpty() && !vizPortNumber.trim().isEmpty()) {
				//System.out.println("Broad : " + session_selected_broadcaster + " Port : " + vizPortNumber);
				session_socket = new Socket(vizIPAddresss, Integer.valueOf(vizPortNumber));
				switch (session_selected_broadcaster.toUpperCase()) {
				case HockeyUtil.HOCKEY_KHELO_INDIA:
					session_selected_scenes.add(new Scene("","FULL-FRAMERS"));
					session_selected_scenes.add(new Scene("","OVERLAYS"));
					session_selected_scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
					session_selected_scenes.get(1).scene_load(session_socket, session_selected_broadcaster);
					session_khelo_india = new HOCKEY_KHELO_INDIA();
					session_khelo_india.scorebug = new ScoreBug();
					break;
				case "PUNJAB":
					session_selected_scenes.add(new Scene("","FULL-FRAMERS"));
					session_selected_scenes.add(new Scene("","OVERLAYS"));
					session_selected_scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
					session_selected_scenes.get(1).scene_load(session_socket, session_selected_broadcaster);
					session_punjab = new PUNJAB();
					session_punjab.scorebug = new ScoreBug();
					break;
				}
			}
			
			model.addAttribute("match_files", new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + 
					HockeyUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".json") && pathname.isFile();
			    }
			}));

			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			session_match = new Match();
			session_event = new EventFile();
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0)
				session_event.setEvents(new ArrayList<Event>());
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getClock() == null) 
				session_match.setClock(new Clock());
			
			session_configurations.setBroadcaster(selectedBroadcaster);
			session_configurations.setVizscene(vizScene);
			session_configurations.setIpAddress(vizIPAddresss);
			
			if(!vizPortNumber.trim().isEmpty()) {
				session_configurations.setPortNumber(Integer.valueOf(vizPortNumber));
			}

			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.CONFIGURATIONS_DIRECTORY + 
							HockeyUtil.OUTPUT_XML));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);
			model.addAttribute("session_event", session_event);
			model.addAttribute("session_configurations", session_configurations);
			model.addAttribute("session_socket", session_socket);
			model.addAttribute("session_khelo_india", session_khelo_india);
			model.addAttribute("session_selected_scenes", session_selected_scenes);
			
			return "match";
		}
	}
	
	@RequestMapping(value = {"/back_to_match"}, method = RequestMethod.POST)
	public String backToMatchPage(ModelMap model) throws ParseException
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {
		
			model.addAttribute("match_files", new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + 
					HockeyUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".json") && pathname.isFile();
			    }
			}));
			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);

			return "match";
		
		}
	}	
	
	@RequestMapping(value = {"/upload_match_setup_data", "/reset_and_upload_match_setup_data"}
		,method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String uploadFormDataToSessionObjects(HttpServletRequest request) 
			throws IllegalAccessException, InvocationTargetException, JAXBException, IOException
	{
		if (request.getRequestURI().contains("upload_match_setup_data") 
				|| request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
			
			List<Player> home_squad = new ArrayList<Player>(); List<Player> away_squad = new ArrayList<Player>();
			List<Player> home_substitutes = new ArrayList<Player>(); List<Player> away_substitutes = new ArrayList<Player>();

	   		boolean reset_all_variables = false;
			if(request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
				reset_all_variables = true;
			} else if(request.getRequestURI().contains("upload_match_setup_data")) {
				for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					if(entry.getKey().equalsIgnoreCase("select_existing_hockey_matches") && entry.getValue()[0].equalsIgnoreCase("new_match")) {
						reset_all_variables = true;
						break;
					}
				}
			}
			if(reset_all_variables == true) {
				session_match = new Match(); 
				session_event = new EventFile();
				session_event.setEvents(new ArrayList<Event>());
				session_match.setMatchStats(new ArrayList<MatchStats>());
			}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	   			if(entry.getKey().contains("_")) {
   					if(entry.getKey().split("_")[0].equalsIgnoreCase(HockeyUtil.HOME + HockeyUtil.PLAYER)) {
   						switch (Integer.parseInt(entry.getKey().split("_")[1])) {
   						case 1: case 2: case 3: case 4: case 5: case 6:
   						case 7: case 8: case 9: case 10: case 11:
   		   					home_squad.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), HockeyUtil.PLAYER));
   							break;
   						default:
   		   					home_substitutes.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), HockeyUtil.SUBSTITUTE));
   							break;
   						}
   					} else if(entry.getKey().split("_")[0].equalsIgnoreCase(HockeyUtil.AWAY + HockeyUtil.PLAYER)) {
   						switch (Integer.parseInt(entry.getKey().split("_")[1])) {
   						case 1: case 2: case 3: case 4: case 5: case 6:
   						case 7: case 8: case 9: case 10: case 11:
   		   					away_squad.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), HockeyUtil.PLAYER));
   							break;
   						default:
   		   					away_substitutes.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), HockeyUtil.SUBSTITUTE));
   							break;
   						}
   					}
	   			} else {
	   				BeanUtils.setProperty(session_match, entry.getKey(), entry.getValue()[0]);
	   			}
	   		}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	   			if(entry.getKey().contains("_")) {
	   				if(entry.getKey().split("_")[0].equalsIgnoreCase(HockeyUtil.HOME + HockeyUtil.CAPTAIN 
	   						+ HockeyUtil.GOAL_KEEPER.replace("_", ""))) {
	   					for(Player plyr:home_squad) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   					for(Player plyr:home_substitutes) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   				} else if(entry.getKey().split("_")[0].equalsIgnoreCase(HockeyUtil.AWAY + HockeyUtil.CAPTAIN 
	   						+ HockeyUtil.GOAL_KEEPER.replace("_", ""))) {
	   					for(Player plyr:away_squad) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   					for(Player plyr:away_substitutes) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
   					}
	   			}
	   		}
			
			System.out.println("params = " + request.getParameterMap().keySet());
			System.out.println("matchFileName = " + session_match.getMatchFileName());
			if (session_match.getMatchFileName() == null || session_match.getMatchFileName().isEmpty()) {
			    return "{\"error\":\"matchFileName missing\"}";
			}
			

			session_match.setHomeSquad(home_squad);
			session_match.setAwaySquad(away_squad);
			
			Collections.sort(session_match.getHomeSquad());
			Collections.sort(session_match.getAwaySquad());

			session_match.setHomeSubstitutes(home_substitutes);
			session_match.setAwaySubstitutes(away_substitutes);
			
			Collections.sort(session_match.getHomeSubstitutes());
			Collections.sort(session_match.getAwaySubstitutes());
			
			session_match.setHomeOtherSquad(HockeyFunctions.getPlayersFromDB(hockeyService, HockeyUtil.HOME, session_match));
			session_match.setAwayOtherSquad(HockeyFunctions.getPlayersFromDB(hockeyService, HockeyUtil.AWAY, session_match));

//			new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).createNewFile();
//			new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()).createNewFile();
//			String json_file_name = session_match.getMatchFileName();
			System.out.println("FILE - " + HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName());
			new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			
			session_match = HockeyFunctions.populateMatchVariables(hockeyService, session_match);

			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
		}
		session_match.setEvents(session_event.getEvents());
		return objectMapper.writeValueAsString(session_match);
	}
	
	@RequestMapping(value = {"/processHockeyProcedures.html"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processHockeyProcedures(
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess)
					throws JAXBException, IllegalAccessException, InvocationTargetException, IOException, NumberFormatException, InterruptedException, 
						SAXException, ParserConfigurationException, CsvException
	{	
		Event this_event = new Event();
//		if(!whatToProcess.equalsIgnoreCase(HockeyUtil.LOAD_TEAMS)) {
//			if(valueToProcess.contains(",")) {
//				if(session_match.getMatchFileName() == null || session_match.getMatchFileName().isEmpty()) {
//					session_match = (Match) JAXBContext.newInstance(Match.class).createUnmarshaller().unmarshal(
//							new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY+ valueToProcess.split(",")[0]));
//					
//					session_event = (EventFile) JAXBContext.newInstance(EventFile.class).createUnmarshaller().unmarshal(
//							new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + valueToProcess.split(",")[0]));
//					
//					session_match.setEvents(session_event.getEvents());
//					session_match = HockeyFunctions.populateMatchVariables(hockeyService,session_match);
//				}
//			}
//		}
		if(!whatToProcess.equalsIgnoreCase(HockeyUtil.LOAD_TEAMS)) {
		    if(valueToProcess.contains(",")) {
		        String fileName = valueToProcess.split(",")[0];
		        if((session_match.getMatchFileName() == null || session_match.getMatchFileName().isEmpty())
		                && !fileName.equals("null") && !fileName.equals("undefined")) {
		            String base = HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY;
		            session_match = objectMapper.readValue(new File(base + HockeyUtil.MATCHES_DIRECTORY + fileName), Match.class);
		            session_match.setMatchFileName(fileName);
		            session_event = objectMapper.readValue(new File(base + HockeyUtil.EVENT_DIRECTORY + fileName), EventFile.class);
		            session_match.setEvents(session_event.getEvents());
		            session_match = HockeyFunctions.populateMatchVariables(hockeyService, session_match);
		        }
		    }
		}
		switch (whatToProcess.toUpperCase()) {
		case HockeyUtil.LOG_STAT:

			if(valueToProcess.toUpperCase().contains(HockeyUtil.PENALTIES)) {
				if(valueToProcess.split(",")[1].split("_")[1].toUpperCase().contains(HockeyUtil.INCREMENT)) {
					if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(HockeyUtil.HOME)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(HockeyUtil.HIT)) {
							session_match.setHomePenaltiesHits(session_match.getHomePenaltiesHits() + 1);
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(HockeyUtil.MISS)) {
							session_match.setHomePenaltiesMisses(session_match.getHomePenaltiesMisses() + 1);
						}
					}else if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(HockeyUtil.AWAY)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(HockeyUtil.HIT)) {
							session_match.setAwayPenaltiesHits(session_match.getAwayPenaltiesHits() + 1);
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(HockeyUtil.MISS)) {
							session_match.setAwayPenaltiesMisses(session_match.getAwayPenaltiesMisses() + 1);
						}
					}
				}else if(valueToProcess.split(",")[1].split("_")[1].toUpperCase().contains(HockeyUtil.DECREMENT)) {
					if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(HockeyUtil.HOME)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(HockeyUtil.HIT)) {
							if(session_match.getHomePenaltiesHits() > 0) {
								session_match.setHomePenaltiesHits(session_match.getHomePenaltiesHits() - 1);
							}
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(HockeyUtil.MISS)) {
							if(session_match.getHomePenaltiesMisses() > 0) {
								session_match.setHomePenaltiesMisses(session_match.getHomePenaltiesMisses() - 1);
							}
						}
					}else if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(HockeyUtil.AWAY)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(HockeyUtil.HIT)) {
							if(session_match.getAwayPenaltiesHits() > 0) {
								session_match.setAwayPenaltiesHits(session_match.getAwayPenaltiesHits() - 1);
							}
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(HockeyUtil.MISS)) {
							if(session_match.getAwayPenaltiesMisses() > 0) {
								session_match.setAwayPenaltiesMisses(session_match.getAwayPenaltiesMisses() - 1);
							}
						}
					}
				}
			}
			
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			
			switch (session_selected_broadcaster) {
			case HockeyUtil.HOCKEY_KHELO_INDIA:
				session_khelo_india.ProcessGraphicOption(whatToProcess,session_match, session_clock,hockeyService,session_socket, 
						session_selected_scenes, valueToProcess);
				break;
			case HockeyUtil.HOCKEY_PUNJAB:
				session_punjab.ProcessGraphicOption(whatToProcess,session_match, session_clock,hockeyService,session_socket, 
						session_selected_scenes, valueToProcess);
				break;	
			}
			//session_i_league.ProcessGraphicOption(whatToProcess,session_match, session_clock,footballService,session_socket, session_selected_scenes, valueToProcess);
			return objectMapper.writeValueAsString(session_match);
			
		case "RESET_PENALTY":
			
			session_match.setHomePenaltiesHits(0);
			session_match.setHomePenaltiesMisses(0);
			session_match.setAwayPenaltiesHits(0);
			session_match.setAwayPenaltiesMisses(0);

			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			
			return objectMapper.writeValueAsString(session_match);
			
		case "POPULATE-FF-MATCHID_SINGLEPROMO": case "DOUBLE_PROMO-OPTIONS":
			session_fixture = HockeyFunctions.processAllFixtures(hockeyService);
			return objectMapper.writeValueAsString(session_fixture).toString();
		
		case "NAMESUPER_GRAPHICS-OPTIONS": case "BUG_DB_GRAPHICS-OPTIONS": case "STAFF_GRAPHICS-OPTIONS": case "PROMO_GRAPHICS-OPTIONS": case "LTPROMO_GRAPHICS-OPTIONS":
		case "SCOREBUGPROMO_GRAPHICS-OPTIONS":	case "RESULT_PROMO_GRAPHICS-OPTIONS":
			switch (session_selected_broadcaster) {
			case HockeyUtil.HOCKEY_KHELO_INDIA:
				return session_khelo_india.ProcessGraphicOption(whatToProcess,session_match,session_clock, 
						hockeyService,session_socket, session_selected_scenes, valueToProcess).toString();
			case HockeyUtil.HOCKEY_PUNJAB:
				return session_punjab.ProcessGraphicOption(whatToProcess,session_match,session_clock, 
						hockeyService,session_socket, session_selected_scenes, valueToProcess).toString();	
			}
			
			return objectMapper.writeValueAsString(session_match);
			
		case HockeyUtil.REPLACE:
			
			Player store_player = new Player();
			for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
				
				if(session_match.getHomeSquad().get(i).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					store_player = session_match.getHomeSquad().get(i);
					session_match.getHomeSquad().remove(i);
					for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
						if(session_match.getHomeSubstitutes().get(j).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
							session_match.getHomeSubstitutes().remove(j);
							session_match.getHomeSubstitutes().add(j, store_player);
						}
					}
				}
			}
			for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
				
				if(session_match.getAwaySquad().get(i).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					store_player = session_match.getAwaySquad().get(i);
					session_match.getAwaySquad().remove(i);
					for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
						if(session_match.getAwaySubstitutes().get(j).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
							session_match.getAwaySubstitutes().remove(j);
							session_match.getAwaySubstitutes().add(j, store_player);
						}
					}
				}
			}
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, 0, session_match.getClock().getMatchHalves(), 
					0,whatToProcess, "replace", Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),0));
			
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			
			session_match.setEvents(session_event.getEvents());
			
			return objectMapper.writeValueAsString(session_match);
				
		case HockeyUtil.LOG_EVENT:
			
			if(!valueToProcess.trim().isEmpty() && valueToProcess.contains(",") == true) {
				
				if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
					session_match.setMatchStats(new ArrayList<MatchStats>());
				if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
					session_match.setEvents(new ArrayList<Event>());
				
				switch (valueToProcess.split(",")[1].toUpperCase()) {
				case HockeyUtil.GOAL: case HockeyUtil.OWN_GOAL: case HockeyUtil.PENALTY: case HockeyUtil.YELLOW: case HockeyUtil.RED:
				case HockeyUtil.SHOTS_ON_TARGET: case HockeyUtil.SHOTS: case HockeyUtil.CORNERS_CONVERTED: case HockeyUtil.CORNERS:
				case HockeyUtil.ASSISTS: case HockeyUtil.OFF_SIDE: case HockeyUtil.FOULS: case HockeyUtil.GREEN:
					
					session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, Integer.valueOf(valueToProcess.split(",")[2]), 
							session_match.getClock().getMatchHalves(),valueToProcess.split(",")[1], 1, session_match.getClock().getMatchTotalMilliSeconds()));
					
					for(Player plyr : session_match.getHomeSquad()) {
						if(plyr.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							switch (valueToProcess.split(",")[1].toUpperCase()) {
							case HockeyUtil.GOAL: case HockeyUtil.PENALTY:
								session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
								break;
							case HockeyUtil.OWN_GOAL: 
								session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
								break;
							}
						}
					}
					for(Player plyr : session_match.getHomeSubstitutes()) {
						if(plyr.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							switch (valueToProcess.split(",")[1].toUpperCase()) {
							case HockeyUtil.GOAL: case HockeyUtil.PENALTY:
								session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
								break;
							case HockeyUtil.OWN_GOAL: 
								session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
								break;
							}
						}
					}
					for(Player plyr : session_match.getAwaySquad()) {
						if(plyr.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							switch (valueToProcess.split(",")[1].toUpperCase()) {
							case HockeyUtil.GOAL: case HockeyUtil.PENALTY:
								session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
								break;
							case HockeyUtil.OWN_GOAL: 
								session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
								break;
							}
						}
					}
					for(Player plyr : session_match.getAwaySubstitutes()) {
						if(plyr.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							switch (valueToProcess.split(",")[1].toUpperCase()) {
							case HockeyUtil.GOAL: case HockeyUtil.PENALTY:
								session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
								break;
							case HockeyUtil.OWN_GOAL: 
								session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
								break;
							}
						}
					}
					break;
				}

				if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
					session_event.setEvents(new ArrayList<Event>());
				
				session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, Integer.valueOf(valueToProcess.split(",")[2]), 
						session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, valueToProcess.split(",")[1], 0,0,1));
				
			}

			session_match = HockeyFunctions.populateMatchVariables(hockeyService, session_match);
			
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			
			session_match.setEvents(session_event.getEvents());
			
			return objectMapper.writeValueAsString(session_match);

		case "HOME_GOAL":
			
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
				session_match.setEvents(new ArrayList<Event>());
			
			session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, session_match.getHomeSquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(),"Home_Goal", 1, session_match.getClock().getMatchTotalMilliSeconds()));
			
			session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
			
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
				session_event.setEvents(new ArrayList<Event>());
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, session_match.getHomeSquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, "Home_Goal", 0,0,1));

			session_match = HockeyFunctions.populateMatchVariables(hockeyService, session_match);
			
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			
			session_match.setEvents(session_event.getEvents());
			
			return objectMapper.writeValueAsString(session_match);
			
		case "AWAY_GOAL":
			
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
				session_match.setEvents(new ArrayList<Event>());
			
			session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, session_match.getAwaySquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(),"AWAY_GOAL", 1, session_match.getClock().getMatchTotalMilliSeconds()));
			
			session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
			
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
				session_event.setEvents(new ArrayList<Event>());
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, session_match.getAwaySquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, "AWAY_GOAL", 0,0,1));

			session_match = HockeyFunctions.populateMatchVariables(hockeyService, session_match);
			
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
		
			session_match.setEvents(session_event.getEvents());
			
			return objectMapper.writeValueAsString(session_match);	

		case "LOG_OVERWRITE_MATCH_SUBS":
			//System.out.println(valueToProcess);
			if(valueToProcess.contains(",")) {
				int overwrite_palyer_off_id = 0,overwrite_palyer_on_id = 0;
				Player sub_store_player = new Player();
				if(session_event.getEvents() != null) {
					for(Event evnt : session_event.getEvents()) {
						if(evnt.getEventNumber() == Integer.valueOf(valueToProcess.split(",")[1])) {
							if(Integer.valueOf(valueToProcess.split(",")[3]) > 0 && Integer.valueOf(valueToProcess.split(",")[2]) == 0) {
								overwrite_palyer_off_id = evnt.getOnPlayerId();
								overwrite_palyer_on_id = Integer.valueOf(valueToProcess.split(",")[3]);
								
								evnt.setOnPlayerId(overwrite_palyer_on_id);
								
							}else if(Integer.valueOf(valueToProcess.split(",")[3]) == 0 && Integer.valueOf(valueToProcess.split(",")[2]) > 0) {
								overwrite_palyer_off_id = Integer.valueOf(valueToProcess.split(",")[2]);
								overwrite_palyer_on_id = evnt.getOffPlayerId();
								
								evnt.setOffPlayerId(overwrite_palyer_off_id);
								
							}else if(Integer.valueOf(valueToProcess.split(",")[3]) > 0 && Integer.valueOf(valueToProcess.split(",")[2]) > 0) {
								overwrite_palyer_off_id = Integer.valueOf(valueToProcess.split(",")[2]);
								overwrite_palyer_on_id = Integer.valueOf(valueToProcess.split(",")[3]);
								
								evnt.setOnPlayerId(overwrite_palyer_on_id);
								evnt.setOffPlayerId(overwrite_palyer_off_id);
								
							}
							
						}
					}
				}
				//System.out.println("ON - " + overwrite_palyer_on_id + " OFF - " + overwrite_palyer_off_id);
				for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
					if(session_match.getHomeSquad().get(i).getPlayerId() == overwrite_palyer_off_id) {
						sub_store_player = session_match.getHomeSquad().get(i);
						session_match.getHomeSquad().remove(i);
						for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
							if(session_match.getHomeSubstitutes().get(j).getPlayerId() == overwrite_palyer_on_id) {
								session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
								session_match.getHomeSubstitutes().remove(j);
								session_match.getHomeSubstitutes().add(j, sub_store_player);
							}
						}
					}
				}
				for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
					if(session_match.getAwaySquad().get(i).getPlayerId() == overwrite_palyer_off_id) {
						sub_store_player = session_match.getAwaySquad().get(i);
						session_match.getAwaySquad().remove(i);
						for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
							if(session_match.getAwaySubstitutes().get(j).getPlayerId() == overwrite_palyer_on_id) {
								session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
								session_match.getAwaySubstitutes().remove(j);
								session_match.getAwaySubstitutes().add(j, sub_store_player);
							}
						}
					}
				}
			}
			
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			return objectMapper.writeValueAsString(session_match);
		case "LOG_OVERWRITE_MATCH_STATS":
		
			if(valueToProcess.contains(",")) {
				if(session_match.getMatchStats() != null) {
					for(MatchStats ms : session_match.getMatchStats()) {
						if(ms.getStatsId() == Integer.valueOf(valueToProcess.split(",")[1])) {
							ms.setPlayerId(Integer.valueOf(valueToProcess.split(",")[2]));
							ms.setStats_type(valueToProcess.split(",")[3]);
							ms.setTotalMatchSeconds(Long.valueOf(valueToProcess.split(",")[4]));
						}
					}
				}
				if(session_event.getEvents() != null) {
					for(Event evnt : session_event.getEvents()) {
						if(evnt.getStatsId() == Integer.valueOf(valueToProcess.split(",")[1])) {
							evnt.setEventPlayerId(Integer.valueOf(valueToProcess.split(",")[2]));
							evnt.setEventLog("LOG_EVENT");
							evnt.setEventType(valueToProcess.split(",")[3]);
						}
					}
				}
			}

			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			
			session_match = HockeyFunctions.populateMatchVariables(hockeyService, session_match);
			session_match.setEvents(session_event.getEvents());

			return objectMapper.writeValueAsString(session_match);
		
		case HockeyUtil.LOG_OVERWRITE_TEAM_SCORE: 
			
			session_match.setHomeTeamScore(Integer.valueOf(valueToProcess.split(",")[1]));
			session_match.setAwayTeamScore(Integer.valueOf(valueToProcess.split(",")[2]));

			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);

			return objectMapper.writeValueAsString(session_match);
		
		case "HOME_UNDO":
		
			if(session_event.getEvents() != null && 1 <= session_event.getEvents().size()) {
				
				for(int jUndo=1;jUndo<=1;jUndo++) {

					this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
					switch (this_event.getEventLog().toUpperCase()) {
					case "HOME_GOAL":
						switch (this_event.getEventType().toUpperCase()) {
						case "HOME_GOAL":
							this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
							for(Player plyr : session_match.getHomeSquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "HOME_GOAL":
										session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							for(Player plyr : session_match.getAwaySquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "HOME_GOAL":
										session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							
							break;
						}
						break;
					}
				}
			}
			
	
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
		
			session_match.setEvents(session_event.getEvents());
			return objectMapper.writeValueAsString(session_match);
			
		case "AWAY_UNDO":

			if(session_event.getEvents() != null && 1 <= session_event.getEvents().size()) {
				
				for(int jUndo=1;jUndo<=1;jUndo++) {

					this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
					
					switch (this_event.getEventLog().toUpperCase()) {
					case "AWAY_GOAL":
						switch (this_event.getEventType().toUpperCase()) {
						case "AWAY_GOAL":
							this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
							for(Player plyr : session_match.getHomeSquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "AWAY_GOAL":
										session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							for(Player plyr : session_match.getAwaySquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "AWAY_GOAL":
										session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							break;
						}
						break;
					}
				}
			}
			
	
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
		
			session_match.setEvents(session_event.getEvents());
			return objectMapper.writeValueAsString(session_match);	
			
		case HockeyUtil.UNDO:

			if(valueToProcess.contains(",")) {
				if(session_event.getEvents() != null && Integer.valueOf(valueToProcess.split(",")[1]) <= session_event.getEvents().size()) {
					for(int iUndo=1;iUndo<=Integer.valueOf(valueToProcess.split(",")[1]);iUndo++) {

						this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
						
						switch (this_event.getEventLog().toUpperCase()) {
						case HockeyUtil.LOG_EVENT:
							switch (this_event.getEventType().toUpperCase()) {
							case HockeyUtil.GOAL: case HockeyUtil.OWN_GOAL: case HockeyUtil.PENALTY: case HockeyUtil.YELLOW: case HockeyUtil.RED:
							case HockeyUtil.ASSISTS: case HockeyUtil.SHOTS: case HockeyUtil.SHOTS_ON_TARGET: case HockeyUtil.OFF_SIDE: case HockeyUtil.FOULS:
							case HockeyUtil.CORNERS_CONVERTED: case HockeyUtil.CORNERS: case HockeyUtil.SAVES: case HockeyUtil.GREEN:
								this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
								session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
								for(Player plyr : session_match.getHomeSquad()) {
									if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
										switch (this_event.getEventType().toUpperCase()) {
										case HockeyUtil.GOAL: case HockeyUtil.PENALTY:
											session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
											break;
										case HockeyUtil.OWN_GOAL: 
											session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
											break;
										}
									}
								}
								for(Player plyr : session_match.getHomeSubstitutes()) {
									if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
										switch (this_event.getEventType().toUpperCase()) {
										case HockeyUtil.GOAL: case HockeyUtil.PENALTY:
											session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
											break;
										case HockeyUtil.OWN_GOAL: 
											session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
											break;
										}
									}
								}
								for(Player plyr : session_match.getAwaySquad()) {
									if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
										switch (this_event.getEventType().toUpperCase()) {
										case HockeyUtil.GOAL: case HockeyUtil.PENALTY:
											session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
											break;
										case HockeyUtil.OWN_GOAL: 
											session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
											break;
										}
									}
								}
								for(Player plyr : session_match.getAwaySubstitutes()) {
									if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
										switch (this_event.getEventType().toUpperCase()) {
										case HockeyUtil.GOAL: case HockeyUtil.PENALTY:
											session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
											break;
										case HockeyUtil.OWN_GOAL: 
											session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
											break;
										}
									}
								}
								break;
							}
							break;
						case HockeyUtil.REPLACE:
							ArrayList<Player> undo_store_player = new ArrayList<Player>();
							for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
								if(session_match.getHomeSquad().get(i).getPlayerId() == this_event.getOnPlayerId()) {
									undo_store_player.add(session_match.getHomeSquad().get(i));
									session_match.getHomeSquad().remove(i);
									for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
										if(session_match.getHomeSubstitutes().get(j).getPlayerId() == this_event.getOffPlayerId()) {
											session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
											session_match.getHomeSubstitutes().remove(j);
											session_match.getHomeSubstitutes().add(j, undo_store_player.get(0));
											undo_store_player.remove(0);
										}
									}
								}
							}
							for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
								if(session_match.getAwaySquad().get(i).getPlayerId() == this_event.getOnPlayerId()) {
									undo_store_player.add(session_match.getAwaySquad().get(i));
									session_match.getAwaySquad().remove(i);
									for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
										if(session_match.getAwaySubstitutes().get(j).getPlayerId() == this_event.getOffPlayerId()) {
											session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
											session_match.getAwaySubstitutes().remove(j);
											session_match.getAwaySubstitutes().add(j, undo_store_player.get(0));
											undo_store_player.remove(0);
										}
									}
								}
							}
							break;
						}
						session_event.getEvents().remove(this_event);
					}
				}
			}
			
			
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			session_match.setEvents(session_event.getEvents());
			return objectMapper.writeValueAsString(session_match);
			
		case HockeyUtil.LOAD_TEAMS:
			if(!valueToProcess.trim().isEmpty()) {
				
				session_match.setHomeTeam(hockeyService.getTeam(HockeyUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwayTeam(hockeyService.getTeam(HockeyUtil.TEAM, valueToProcess.split(",")[1]));
				
				session_match.setHomeSquad(hockeyService.getPlayers(HockeyUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwaySquad(hockeyService.getPlayers(HockeyUtil.TEAM, valueToProcess.split(",")[1]));
				
				session_match.setHomeSubstitutes(hockeyService.getPlayers(HockeyUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwaySubstitutes(hockeyService.getPlayers(HockeyUtil.TEAM, valueToProcess.split(",")[1]));
				
				session_match.setHomeOtherSquad(hockeyService.getPlayers(HockeyUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwayOtherSquad(hockeyService.getPlayers(HockeyUtil.TEAM, valueToProcess.split(",")[1]));
			}
			
			return objectMapper.writeValueAsString(session_match);

		case HockeyUtil.LOAD_MATCH: case HockeyUtil.LOAD_SETUP:

			session_match = new ObjectMapper().readValue(new File(HockeyUtil.SPORTS_DIRECTORY +HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.MATCHES_DIRECTORY + 
					valueToProcess), Match.class);
			session_match.setMatchFileName(valueToProcess);
			switch (whatToProcess.toUpperCase()) {
			case HockeyUtil.LOAD_MATCH:
				
				if(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + valueToProcess).exists()) {
					session_event = objectMapper.readValue(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY
					        + HockeyUtil.EVENT_DIRECTORY + valueToProcess), EventFile.class);
				} else {
					session_event = new EventFile();
					new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.EVENT_DIRECTORY + valueToProcess).createNewFile();
				}

				if(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.CLOCK_JSON).exists()) {
					session_clock = new ObjectMapper().readValue(new File(HockeyUtil.SPORTS_DIRECTORY + 
							HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.CLOCK_JSON), Clock.class);
					session_match.setClock(session_clock);
				} else {
					session_match.setClock(new Clock());
				}
				break;
				
			}
			
			switch (whatToProcess.toUpperCase()) {
			case HockeyUtil.LOAD_SETUP:
				session_match.setHomeOtherSquad(HockeyFunctions.getPlayersFromDB(hockeyService, HockeyUtil.HOME, session_match));
				session_match.setAwayOtherSquad(HockeyFunctions.getPlayersFromDB(hockeyService, HockeyUtil.AWAY, session_match));
				break;
			}
			session_match = HockeyFunctions.populateMatchVariables(hockeyService,session_match);

			session_match.setEvents(session_event.getEvents());

			return objectMapper.writeValueAsString(session_match);			

		case "READ_CLOCK":
			if(new File(HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.CLOCK_JSON).exists()) {
				session_clock = new ObjectMapper().readValue(new File(HockeyUtil.SPORTS_DIRECTORY + 
						HockeyUtil.HOCKEY_DIRECTORY + HockeyUtil.CLOCK_JSON), Clock.class);

				session_match.setClock(session_clock);
				
				switch (session_selected_broadcaster) {
				case HockeyUtil.HOCKEY_KHELO_INDIA:
					session_khelo_india.updateScoreBug(session_selected_scenes,session_match, session_socket);
					break;
				case HockeyUtil.HOCKEY_PUNJAB:
					session_punjab.updateScoreBug(session_selected_scenes,session_match, session_socket);
					break;	
				}
			}
			return objectMapper.writeValueAsString(session_match);
						
		default:
			switch (session_selected_broadcaster) {
			case HockeyUtil.HOCKEY_KHELO_INDIA:
				session_khelo_india.ProcessGraphicOption(whatToProcess,session_match, session_clock,hockeyService,session_socket, 
						session_selected_scenes, valueToProcess);
				return new ObjectMapper().writeValueAsString(session_khelo_india).toString();
			case HockeyUtil.HOCKEY_PUNJAB:
				session_punjab.ProcessGraphicOption(whatToProcess,session_match, session_clock,hockeyService,session_socket, 
						session_selected_scenes, valueToProcess);
				return new ObjectMapper().writeValueAsString(session_punjab).toString();	
			}
			return objectMapper.writeValueAsString(session_match);
		}
	}
	private void saveMatchAndEvents() throws IOException {
	    String base = HockeyUtil.SPORTS_DIRECTORY + HockeyUtil.HOCKEY_DIRECTORY;
	    objectMapper.writeValue(new File(base + HockeyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), session_match);
	    objectMapper.writeValue(new File(base + HockeyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()), session_event);
	}
	
}