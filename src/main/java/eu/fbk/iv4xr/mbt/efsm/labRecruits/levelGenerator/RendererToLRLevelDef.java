package eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator;

/*
 * @author wish
 */
/*
 * changes are made to complain Java 8 for using evosuite 1.0.6
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//import eu.iv4xr.lrtools.levgen.Layout.LayoutItem;
//import eu.iv4xr.lrtools.levgen.Room.Direction;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Room.Direction;
import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Layout.LayoutItem;

public class RendererToLRLevelDef {
	
	// every tile in a Layout will correspond to a scalexscale rectangle area in Lab Recruits
	int scale = 10 ;
	int corridorWidth = 2 ;
	
	Random rnd = new Random(MBTProperties.LR_seed);
	
	static class TextBlock {
		List<StringBuilder> rows = new LinkedList<>() ;
		TextBlock(int nRows) {
			for (int k=0; k<nRows; k++) {
				rows.add(new StringBuilder()) ;
			}
		}
		TextBlock appendRight(TextBlock B) {
			for(int r=0; r<rows.size() ; r++) {
				//var tb = rows.get(r) ;
				StringBuilder tb = rows.get(r) ;
				tb.append(csvSeparator) ;
				tb.append(B.rows.get(r)) ;
			}
			return this ;
		}
		
		TextBlock appendBelow(TextBlock B) {
			rows.addAll(B.rows) ;
			return this ;
		}
		
		
		@Override
		public String toString() {
			StringBuilder ss = new StringBuilder() ;
			int k=0 ;
			//for(var r : rows) {
			for(StringBuilder r : rows) {
				if(k>0) ss.append("\n") ;
				ss.append(r) ;
				k++ ;
			}
			return ss.toString() ;
		}
	}
	
	static final String csvSeparator = "," ;
	static final String floorCode = "f" ;
	static final String wallCode = "w" ;
	static final String startLayerCode = "|" ;
	
	static String buttonCode(String id) {
		return "f:b^" + id ;
	}
	
	static String goalFlagCode(String id) {
		return "f:g^" + id;
	}
	
	static String agentCode(String id) {
		return "f:a^" + id ;
	}
	
	static String doorCode(String id, Direction orientation) {
		String dir = "" ;
		switch(orientation) {
		   case NORTH : dir = "n" ; break ;
		   case EAST  : dir = "e" ; break ;
		   case SOUTH : dir = "s" ; break ;
		   case WEST  : dir = "w" ; break ;
		}
		return "f:d>" + dir + "^" + id ;
	}
	
	/**
	 * Construct a 2D array of size x size of empty strings.
	 */
	String[][] emptyMap() {
		String[][] map = new String[scale][scale] ;
		for(int x=0; x<scale; x++) {
			for(int y=0; y<scale; y++) {
				map[x][y] = "" ;
			}
		}
		return map ;
	}
	
	static TextBlock mapToTextBlock(String[][] map) {
		int N = map.length ;
		TextBlock tb = new TextBlock(N) ;
		for(int row=0; row<N; row++) {
			//var buf = tb.rows.get(row) ;
			StringBuilder buf = tb.rows.get(row) ;
			for(int y=0; y<N; y++) {
				if (y>0) buf.append(csvSeparator) ;
				buf.append(map[y][row]) ;
			}
		}
		return tb ;
	}
	
	
	TextBlock renderRoom(Room R, int layerNr) {
		String[][] map = emptyMap() ;
		// walls and floor:
		for(int x=0;x<scale; x++) {
			for(int y=0; y<scale; y++) {
				if (x==0 || x==scale-1 || y==0 || y==scale-1) {
					// put a wall:
					map[x][y] = wallCode ;
				}
				else {
					if (layerNr==0) {
						// at layer 0 we have floors:
						map[x][y] = floorCode ;
					}
				}
			}
		}
		// place opening to connect to corridors:
		//for(var con : R.connections) {
		for(Pair<Corridor,Room.Direction> con : R.connections) {
			int x=0 ;  int xx=0 ;
			int y=0 ;  int yy=0 ;
			switch(con.snd) {
			   case EAST :  
				   x = scale-1 ; xx=x ;
				   y = scale/2 ; yy = y-1 ;
				   break ;
			   case WEST :
				   x = 0 ; xx=x ;
				   y = scale/2 ; yy = y-1 ;
				   break ;
			   case NORTH :
				   y = scale-1 ; yy=y ;
				   x = scale/2 ; xx = x-1 ;
				   break ;
			    case SOUTH :
				   y = 0 ; yy=y ;
				   x = scale/2 ; xx = x-1 ;
				   break ;
			}
			if(layerNr==0) {
				map[x][y] = floorCode ; map[xx][yy] = floorCode ;
			}
			else {
				map[x][y] = "" ; map[xx][yy] = "" ;
			}
		}
		
		// placing the buttons:
		if(layerNr==0) {
			List<Pair<Integer,Integer>> spots = new LinkedList<>() ;
			spots.add(new Pair(1,1)) ;
			spots.add(new Pair(1,scale-2)) ;
			spots.add(new Pair(scale-2,1)) ;
			spots.add(new Pair(scale-2,2)) ;
			spots.add(new Pair(scale-2,3)) ;
			spots.add(new Pair(scale-2,scale-3)) ;
			spots.add(new Pair(1,2)) ;
			spots.add(new Pair(1,3)) ;
			
			if (R.buttons.size() > spots.size()) throw new IllegalArgumentException() ; 
			
			//for(var b : R.buttons) {
			for(Button b : R.buttons) {
				//var loc = spots.get(rnd.nextInt(spots.size())) ;
				Pair<Integer,Integer> loc = spots.get(rnd.nextInt(spots.size())) ;
				map[loc.fst][loc.snd] = buttonCode(b.ID) ;
				spots.remove(loc) ;
			}
			
			for(GoalFlag gf : R.goalFlags) {
				Pair<Integer,Integer> loc = spots.get(rnd.nextInt(spots.size())) ;
				map[loc.fst][loc.snd] = goalFlagCode(gf.ID) ;
				spots.remove(loc) ;
			}
		}
		// placing a separator every 3 rooms?
		if(R.buttons.size()>1 && R.ID % 3 == 1) {
			int sepXstart = 3 ;
			int sepXend   = scale - 3 ;
			int sepY = scale/2 ;
			for(int x=sepXstart; x<sepXend ; x++) {
				map[x][sepY] = wallCode ;
			}
			map[scale/2][sepY+1] = wallCode ;
			map[scale/2][sepY-1] = wallCode ;
			map[scale/2-1][sepY-1] = wallCode ;
			
		}
		// place the agent, if there is one in the room. Make sure that he is placed on
		// the floor that is unoccupied:
		if (R.agent != null && layerNr==0) {
			map[2][2] = agentCode(R.agent.ID) ;
		}
		
		return mapToTextBlock(map) ;
	}
	
	TextBlock renderEmpty() {
		return mapToTextBlock(emptyMap()) ;
	}
	
	/**
	 * Making a floor area of scale x scale.
	 * @return
	 */
	TextBlock mkFloor() {
		String[][] map = emptyMap() ;
		for (int x=0; x<scale; x++) {
			for (int y=0; y<scale; y++) map[x][y] = floorCode ;
		}
		return mapToTextBlock(map) ;
	}
	
	private String[][] buildHorizontalCorridor(String[][] map, int startx, int endx, int layerNr) {
		int y = scale/2 ;
    	for(int x=startx; x < endx ; x++) {
    		map[x][y+1] = wallCode ;
    		map[x][y-2] = wallCode ;
    		if(layerNr==0) {
    			map[x][y]  = floorCode ;
    			map[x][y-1] = floorCode ;
    		}
    	}
    	return map ;
	}
	
	private String[][] buildVerticalCorridor(String[][] map, int starty, int endy, int layerNr) {
		int x = scale/2 ;
		for(int y=starty; y < endy; y++) {
	    	map[x-2][y] = wallCode ;
	    	map[x+1][y] = wallCode ;
	    	if (layerNr==0) {
	    		map[x-1][y] = floorCode ;
	    		map[x][y]   = floorCode ;
	    	}
	    }
		return map ;
	}
	
	private String[][] buildDoor(String[][] map, Door door, Direction whichBorder, int layerNr) {
		switch(whichBorder) {
		   case WEST : 
			    int y = scale/2 ;
			    map[0][y] = wallCode ;
	    		map[1][y] = wallCode ;
	    		if(layerNr==0) {
	    			map[0][y-1] = doorCode(door.ID,Direction.WEST) ;
	    		}
	    		else if (layerNr==2) map[0][y-1] = wallCode ;
	    		break ;
		   case EAST :
			    int eastBorder = scale-1 ; 
			    y = scale/2 ;
			    map[eastBorder][y] = wallCode ;
	    		map[eastBorder-1][y] = wallCode ;
	    		if(layerNr==0) {
	    			map[eastBorder][y-1] = doorCode(door.ID,Direction.EAST) ;
	    		}
	    		else if(layerNr==2) map[eastBorder][y-1] = wallCode ;
	    		break ;
		   case NORTH :
			    int northBorder = scale-1 ;
			    int x = scale/2 ;
			    map[x][northBorder] = wallCode ;
			    map[x][northBorder-1] = wallCode ;
			    if(layerNr==0) {
			    	map[x-1][northBorder] = doorCode(door.ID,Direction.NORTH) ;		
			    }
			    else if(layerNr==2) map[x-1][northBorder] = wallCode ;
			    break ;
		   case SOUTH :
			    x = scale/2 ;
			    map[x][0] = wallCode ;
			    map[x][1] = wallCode ;
			    if(layerNr==0) {
			    	map[x-1][0] = doorCode(door.ID,Direction.SOUTH) ;		
			    }
			    else if(layerNr==2) map[x-1][0] = wallCode ;
	    }
		return map ;
	}
	 
	TextBlock renderCorridor(LayoutItem corridor, int layerNr) {
		if (!(corridor.structure instanceof Corridor)) throw new IllegalArgumentException() ;
		//var map = emptyMap() ;
		String[][] map = emptyMap() ;
		// when level is 0, put a floor as foundation:
		if (layerNr==0) {
			for (int x=0; x<scale; x++) {
				for (int y=0; y<scale; y++) map[x][y] = floorCode ;
			}
		}
		
		switch(corridor.bending) {
		   case WEST_EAST :
			    map = buildHorizontalCorridor(map,0,scale,layerNr) ;
		    	if (corridor.door != null)  {
		    		map = buildDoor(map,corridor.door,Direction.WEST,layerNr) ;
		    	}
		    	break ;
		   case NORTH_SOUTH :
			   map = buildVerticalCorridor(map,0,scale,layerNr) ;
			   if (corridor.door != null) {
				   map = buildDoor(map,corridor.door,Direction.NORTH,layerNr) ;
			    }
			    break ;
			    
		   case SOUTH_AND_EAST:
			   map = buildVerticalCorridor(map,0,scale/2-1,layerNr) ;
			   map = buildHorizontalCorridor(map,scale/2+1,scale,layerNr) ;
			   // ... the bending
			   for(int x=scale/2-2; x<scale/2+1; x++) {
				   for(int y=scale/2-1; y<scale/2+2; y++) {
					   if(x==scale/2-2 || y==scale/2+1)
						   map[x][y] = wallCode ;
					   else
						   if(layerNr==0) map[x][y] = floorCode ;
				   }
			   }
			   if (corridor.door != null) { 
				   map = buildDoor(map,corridor.door,Direction.EAST,layerNr) ;
			   }
			   break ;
		   case SOUTH_AND_WEST:
			   map = buildVerticalCorridor(map,0,scale/2-1,layerNr) ;
			   map = buildHorizontalCorridor(map,0,scale/2-1,layerNr) ;
			   // ... the bending
			   for(int x=scale/2-1; x<scale/2+2; x++) {
				   for(int y=scale/2-1; y<scale/2+2; y++) {
					   if(x==scale/2+1 || y==scale/2+1)
						   map[x][y] = wallCode ;
					   else
						   if(layerNr==0) map[x][y] = floorCode ;
				   }
			   }
			   if (corridor.door != null) { 
				   map = buildDoor(map,corridor.door,Direction.SOUTH,layerNr) ;
			   }
			   break ;
		   case NORTH_AND_EAST:
			   map = buildVerticalCorridor(map,scale/2+1,scale,layerNr) ;
			   map = buildHorizontalCorridor(map,scale/2+1,scale,layerNr) ;
			   // ... the bending
			   for(int x=scale/2-2; x<scale/2+1; x++) {
				   for(int y=scale/2-2; y<scale/2+1; y++) {
					   if(x==scale/2-2 || y==scale/2-2)
						   map[x][y] = wallCode ;
					   else
						   if(layerNr==0) map[x][y] = floorCode ;
				   }
			   }
			   if (corridor.door != null) { 
				   map = buildDoor(map,corridor.door,Direction.NORTH,layerNr) ;
			   }
			   break ;
		   case NORTH_AND_WEST :
			   map = buildVerticalCorridor(map,scale/2+1,scale,layerNr) ;
			   map = buildHorizontalCorridor(map,0,scale/2-1,layerNr) ;
			   // ... the bending
			   for(int x=scale/2-1; x<scale/2+2; x++) {
				   for(int y=scale/2-2; y<scale/2+1; y++) {
					   if(x==scale/2+1 || y==scale/2-2)
						   map[x][y] = wallCode ;
					   else
						   if(layerNr==0) map[x][y] = floorCode ;
				   }
			   }
			   if (corridor.door != null) { 
				   map = buildDoor(map,corridor.door,Direction.WEST,layerNr) ;
			   }
			   break ;
		}
		return mapToTextBlock(map) ;
	}
	
	TextBlock renderLayoutItem(LayoutItem loItem, int layerNr) {
		if(loItem==null) {
			if (layerNr==0)
				return mkFloor() ;
			else
				return renderEmpty() ;
		}
		if(loItem.structure instanceof Room) return renderRoom((Room) loItem.structure,layerNr) ;
		return renderCorridor(loItem,layerNr) ;
	}
	
    private StringBuffer renderLayout(StringBuffer buf, Layout layout, int layerNr) {
		buf.append(startLayerCode) ;
		for(int row=0; row < layout.height; row++) {
			TextBlock tb = renderLayoutItem(layout.layout[0][row],layerNr) ;
			for(int x=1; x < layout.width; x++) {
				tb.appendRight(renderLayoutItem(layout.layout[x][row],layerNr)) ;
			}
			buf.append(tb.toString()) ;
			if(row < layout.height-1) buf.append("\n") ;
		}
		return buf ;
	}
    
    public String renderLayout(List<Button> buttons, Layout layout) {
    	StringBuffer buf = new StringBuffer() ;
    	// placing the buttons logic:
    	//for(var B : buttons) {
    	for(Button B : buttons) {
    		if (B.associatedDoors.size()>0) {
    			buf.append(B.ID) ;
    			//for(var D : B.associatedDoors) {
    			for(Door D : B.associatedDoors) {
    				buf.append(csvSeparator) ;
    				buf.append(D.ID) ;
    			}
    			buf.append("\n") ;
    		}
    	}
    	// generating three layers from the given layout, one for each elevation 1,2,3
    	buf = renderLayout(buf,layout,0) ; buf.append("\n") ;
    	buf = renderLayout(buf,layout,1) ; buf.append("\n") ;
    	buf = renderLayout(buf,layout,2) ; buf.append("\n") ;
    	return buf.toString() ;
    }
    
    public void saveAsLRLevelDef(String fname, List<Button> buttons, Layout layout) throws IOException {
    	BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
        writer.write(renderLayout(buttons,layout));
        writer.close();
    }
	
	public static void main(String[] args) throws IOException{
		 List<Room> rooms = Room.randomGen(5,3,4,1,false) ;
		 //for(var R : rooms) {
		 for(Room R : rooms) {
				System.out.println("======") ;
				System.out.println(R.toString()) ;
			}
		 
		 Layout layout = Layout.drawLayoutWithRetries(rooms) ;
		 System.out.println("") ;
	     System.out.println(layout.toString()) ;
	     
	     Room R = rooms.get(0) ;
	     //var renderer = new RendererToLRLevelDef() ;
	     RendererToLRLevelDef renderer = new RendererToLRLevelDef() ;
	     //var tb  = renderer.renderRoom(R,0) ;
	     TextBlock tb  = renderer.renderRoom(R,0) ;
	     //tb = renderer.renderEmpty() ;
	     System.out.println("" + tb) ;
	     System.out.println("") ;
	     Corridor cor = R.connections.get(0).fst ;
	     //var S0 = layout.findAllSegments(cor).get(0) ;
	     Pair<Integer,Integer> S0 = layout.findAllSegments(cor).get(0) ;
	     //var tb2 = renderer.renderCorridor(layout.layout[S0.fst][S0.snd],0) ;
	     TextBlock tb2 = renderer.renderCorridor(layout.layout[S0.fst][S0.snd],0) ;
	     //System.out.println("" + renderer.renderLayout(layout)) ;
	     renderer.saveAsLRLevelDef("mylevel.csv",new LinkedList<Button>(),layout);
	     
	}
	

}
