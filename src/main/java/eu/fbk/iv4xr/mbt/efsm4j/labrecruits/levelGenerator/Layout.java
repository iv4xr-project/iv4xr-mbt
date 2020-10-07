package eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator;

/*
 * @author wish
 */
/*
 * changes are made to complain Java 8 for using evosuite 1.0.6
 */

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

//import eu.iv4xr.lrtools.levgen.Room.Direction;
import eu.fbk.iv4xr.mbt.efsm4j.labrecruits.levelGenerator.Room.Direction;

/**
 * A layout is essentially a 2D, tiled, plannar graph over rooms. More precisely, it
 * is a 2-dimensional array representing a flat rectangle world made of tiles.
 * It represents a coarse grained map of how rooms are placed, and connected by corridors. 
 * Each room has the size of one tile.
 * 
 * Corridors may bend to connect rooms over this world of tiles.
 *
 */
public class Layout {
	
	public enum Bending {WEST_EAST, NORTH_SOUTH, SOUTH_AND_EAST, SOUTH_AND_WEST, NORTH_AND_EAST, NORTH_AND_WEST } 
	
	class LayoutItem {
		WalledStructure structure ;
		Bending bending = null ;
		/**
		 * Only when this layout-item is a corridor, then this would be a door to be placed
		 * in this segment of the corridor, guarding the corridor.
		 */
		Door door = null ;
		LayoutItem(WalledStructure s) {
			structure = s ;
		}
		LayoutItem(WalledStructure s, Bending b) {
			structure = s ; bending = b ;
		}
		
	}
	
	int width ;
	int height ;
	LayoutItem[][] layout ;
	int scale = 5 ;

	/**
	 * Create an empty layout of the specified dimension. Width = x-axis, height = y-axis.
	 */
	public Layout(int width, int height) {
		this.width = width ;
		this.height = height ;
		layout = new LayoutItem[width][height] ;
		for (int w=0; w<width; w++) {
			for (int h=0; h<height; h++) {
				layout[w][h] = null ;
			}
		}
	}
	
	public int width()  { return width ; }
	public int height() { return height ; }
	
	public Pair<Integer,Integer> getCenter() {
		return new Pair(width/2,height/2) ;
	}
	
	Pair<Integer,Integer> diplacement(Direction ... ds) {
		int x = 0 ;
		int y = 0 ;
		for (int k=0; k<ds.length; k++) {
			switch(ds[k]) {
			  case EAST : x++ ; break ;
			  case WEST : x-- ; break ;
			  case NORTH : y++ ; break ;  
			  case SOUTH : y-- ; break ;
			  default : throw new IllegalArgumentException() ;
			}
		}
		return new Pair(x,y) ;
	}
	
	/**
	 * Place a room in the given coordinate.
	 */
	public Layout place(Room room, int x, int y) {
		if(layout[x][y] != null) throw new IllegalArgumentException("The spot is already occupied!") ;
		layout[x][y] = new LayoutItem(room) ;
		return this ;
	}
	
	/**
	 * Place a corridor at the given coordinate with the given bending. Note that a corridor
	 * can span for multiple tiles. So, this method only places a single-segment of the corridor.
	 */
	public Layout place(Corridor cor, Bending bending, int x, int y) {
		if(layout[x][y] != null) throw new IllegalArgumentException("The spot is already occupied!") ;
		layout[x][y] = new LayoutItem(cor,bending) ;
		return this ;
	}
	
	Pair<Integer,Integer> find(Room struct) {
		for (int w=0; w<width; w++) {
			for (int h=0; h<height; h++) {
				if (layout[w][h] == null) continue ;
				if (layout[w][h].structure == struct) return new Pair(w,h) ;
			}
		}
		return null ;
	}
	
	/**
	 * Find all segments of a corridor.
	 */
	List<Pair<Integer,Integer>> findAllSegments(Corridor cor) {
		List<Pair<Integer,Integer>> segments = new LinkedList<>() ;
		for (int x=0;x<width; x++) {
			for(int y=0; y<height; y++) {
				//var E = layout[x][y] ;
				LayoutItem E = layout[x][y] ;
				if (E==null) continue ;
				if (E.structure == cor) {
					segments.add(new Pair(x,y)) ;
				}
			}
		}
		return segments ;
	}
	 
	public Layout place(Room anchor, WalledStructure structure, Bending bending, Direction ... dirs) {
		//var anchor_ = find(anchor) ;
		Pair<Integer,Integer> anchor_ = find(anchor) ;
		if (anchor_ == null) throw new IllegalArgumentException() ;
		int x = anchor_.fst ;
		int y = anchor_.snd ;
		//var displ = diplacement(dirs) ;
		Pair<Integer,Integer> displ = diplacement(dirs) ;
		x += displ.fst ;
		y += displ.snd ;

		if (x<0 || x>=width)  throw new IllegalArgumentException() ;
		if (y<0 || y>=height) throw new IllegalArgumentException() ;
		if (structure instanceof Room) 
			return place((Room) structure,x,y) ;
		else
			return place((Corridor) structure, bending, x,y) ;
	}
	
	/**
	 * Place a room in a certain position relative to the given anchor. The relative position is
	 * given in terms of a series of directions (e.g. EAST EAST NORTH).
	 */
	public Layout place(Room anchor, Room room, Direction ... dirs) {
		return place(anchor,room,null,dirs) ;
	}
	
	char emptySpaceCode = ' ' ;
	char corridorCode = 'o' ;
	char doorCode = '/' ;
	char wallCode = '#' ;
	
	char[][] asciiDraw(LayoutItem S) {
		char[][] map = new char[scale][scale] ;
		for (int x=0; x<scale; x++) {
			for (int y=0; y<scale; y++)  {
				map[x][y] = emptySpaceCode ;
			}
		}
		// empty structure:
		if (S==null) {
			return map ;
		}
		// a room:
		if (S.structure instanceof Room) {
			Room R = (Room) S.structure ;
			for (int x=0; x<scale; x++) {
				for (int y=0; y<scale; y++)  {
					if (x==0 || x==scale-1 || y==0 ||  y==scale-1) {
						map[x][y] = wallCode ;
					}
				}
			}
			// putting the id of the room too ... pffff
			String id = "" + R.ID ;
			for (int x=0; x<id.length(); x++) {
				map[x+1][1] = id.charAt(x) ;						
			}	
			return map ;
		}
		// else it is a corridor
		Corridor cor = (Corridor) S.structure ;
		// calculate the 'center' of the map:
		int centerX = scale/2 ;
		int centerY = scale/2 ;
		switch(S.bending) {
		   case WEST_EAST :
			   for(int x=0; x<scale;x++) {
				   if(x==0 && S.door!=null) map[x][centerY] = doorCode ;
				   else map[x][centerY] = corridorCode ; 
			   }
			   break ;
		   case NORTH_SOUTH :
			   for(int y=0; y<scale;y++)  { 
				   if(y==0 && S.door!=null) map[centerX][y] = doorCode ;
				   else map[centerX][y] = corridorCode ; }
			   break ;
		   case NORTH_AND_EAST :
			   for(int y=centerY; y<scale; y++)  {
				   if(y==centerY && S.door!=null) map[centerX][y] = doorCode ;
				   else map[centerX][y] = corridorCode ;
			   }
			   for(int x=centerX; x<scale; x++)  map[x][centerY] = corridorCode ;
			   break ;
		   case NORTH_AND_WEST :
			   for(int y=centerY; y<scale; y++)  {
				   if(y==centerY && S.door!=null) map[centerX][y] = doorCode ;
				   else map[centerX][y] = corridorCode ;
			   }
			   for(int x=0; x<=centerX; x++)  map[x][centerY] = corridorCode ;
			   break ;   
		   case SOUTH_AND_EAST :
			   for(int y=0; y<=centerY; y++)  { 
				   if(y==0 && S.door!=null) map[centerX][y] = doorCode ;
				   else map[centerX][y] = corridorCode ;
			   }
			   for(int x=centerX; x<scale; x++)  map[x][centerY] = corridorCode ;
			   break ;  
		   case SOUTH_AND_WEST :
			   for(int y=0; y<=centerY; y++)  map[centerX][y] = corridorCode ;
			   for(int x=0; x<=centerX; x++)  {
				   if(x==0 && S.door!=null) map[x][centerY] = doorCode ;
				   else map[x][centerY] = corridorCode ;
			   }
			   break ;    
		}
		return map ;
	}

	@Override
	public String toString() {
		char[][] map = new char[width*scale][height*scale] ;
		char roomCode = '#' ;
		char corridorCode = '#' ;
		char emptyspaceCode = ' ' ;
		// ascii-draw every cell in the layout:
		for(int x = 0 ;x<width; x++) {
			for(int y = 0; y<height; y++) {
				// to draw the cell at x,y ... we will draw a scaled-out box:
				char[][] cellSprite = asciiDraw(layout[x][y]) ;
				for (int kx=0; kx<scale; kx++) {
					int xx = scale*x + kx ;
					for (int ky=0; ky<scale; ky++) {
						int yy = scale*y + ky ;
						map[xx][yy] = cellSprite[kx][ky] ;
					}
					 
				}
				
			}
		}
		StringBuilder buf = new StringBuilder() ;
		for (int y=0; y<height*scale ; y++) {
			for (int x=0; x<width*scale; x++) {
				buf.append(map[x][y]) ;
			}
			buf.append('\n') ;
		}
		return buf.toString() ;
	}
	
	
	Layout placeCorridor(Room from, Corridor cor, List<Pair<Integer,Integer>> route) {
		Room R1 = from ;
		Room R2 = R1.getConnectedRoom(cor) ;
		//var location_R1 = find(R1) ;
		//var location_R2 = find(R2) ;
		Pair<Integer,Integer> location_R1 = find(R1) ;
		Pair<Integer,Integer> location_R2 = find(R2) ;		
		int x = location_R1.fst ;
		int y = location_R1.snd ;
		int r2_x = location_R2.fst ;
		int r2_y = location_R2.snd ;
		
		// set the direction of R2's connector:
		//var pLast = route.get(route.size() - 1) ;
		Pair<Integer,Integer> pLast = route.get(route.size() - 1) ;
		Direction R2dir ;
		if (r2_x == pLast.fst) {
			R2dir = pLast.snd > r2_y ? Direction.NORTH : Direction.SOUTH ;
		}
		else {
			R2dir = pLast.fst > r2_x ? Direction.EAST : Direction.WEST ;
		}
		R2.getConnection(cor).snd = R2dir ;
				
		// set the direction of R1's connector:
		//var p = route.get(0) ;
		Pair<Integer,Integer> p = route.get(0) ;
		Direction dir ;
		if (x == p.fst) {
			dir = p.snd > y ? Direction.NORTH : Direction.SOUTH ;
			
		}
		else {
			dir = p.fst > x ? Direction.EAST : Direction.WEST ;
		}
		R1.getConnection(cor).snd = dir  ;
		
		// now let's place the corridor, following the given path:
		x = p.fst ;
		y = p.snd ;
		route.add(location_R2) ; // adding R2 as an artificial last node in the path
		for(int k=1; k<route.size(); k++) {
			p = route.get(k) ;
			Bending bend = null ;
			if (x == p.fst) {
				// so, p is to the N or S of the previous p
				switch(dir) {
				   case NORTH : 
				   case SOUTH : bend = Bending.NORTH_SOUTH ; break ;
				   case EAST  : bend = p.snd > y ? Bending.NORTH_AND_WEST : Bending.SOUTH_AND_WEST ;
				                dir  = p.snd > y ? Direction.NORTH : Direction.SOUTH ;
				                break ;
				   case WEST  : bend = p.snd > y ? Bending.NORTH_AND_EAST : Bending.SOUTH_AND_EAST ;
	                            dir  = p.snd > y ? Direction.NORTH : Direction.SOUTH ;
	                            break ;             
				}
			}
			else {
				// so, p is to the E or W of the previous p
				switch(dir) {
				   case EAST  :
				   case WEST  : bend = Bending.WEST_EAST ; break ;
				   case NORTH : bend = p.fst > x ? Bending.SOUTH_AND_EAST : Bending.SOUTH_AND_WEST ;
				   	            dir  = p.fst > x ? Direction.EAST : Direction.WEST ;
				   	            break ;
				   case SOUTH : bend = p.fst > x ? Bending.NORTH_AND_EAST : Bending.NORTH_AND_WEST ;
	   	                        dir  = p.fst > x ? Direction.EAST : Direction.WEST ;
	   	                        break ;
				}
			}
			place(cor,bend,x,y) ;
			x = p.fst ;
			y = p.snd ;
		}
		return this ;
	}
	
	/**
	 * Find a route between the given rooms. The method returns null it can't find a route.
	 */
	private List<Pair<Integer,Integer>> findRoute(Room from, Room to) {
		//var R1 = from ;
		//var R2 = to ;
		//var location1 = find(R1) ;
		//var location2 = find(R2) ;
		
		Room R1 = from ;
		Room R2 = to ;
		Pair<Integer,Integer> location1 = find(R1) ;
		Pair<Integer,Integer> location2 = find(R2) ;
		
		if (location1==null || location2==null) throw new IllegalArgumentException() ;
		
		// We will first use a heuristic to get a route from R1 to R2. If that fails,
		// we fall back to a greedy approach.
		
		// Heuristic:
		int x_delta = location2.fst - location1.fst ;
		int y_delta = location2.snd - location1.snd ;
		Direction R1_connector = null ;
		Direction R2_connector = null ;
		// case1: R1 and R2 are either on the same row, or on the same column:
		if (x_delta == 0 || y_delta==0) {
			// check if a N--S or E--W route is possible:
			if (x_delta == 0) {
				R1_connector = y_delta>0 ? Direction.NORTH : Direction.SOUTH ;
				R2_connector = y_delta>0 ? Direction.SOUTH : Direction.NORTH ;
			}
			else {
				R1_connector = x_delta>0 ? Direction.EAST : Direction.WEST ;
				R2_connector = x_delta>0 ? Direction.WEST : Direction.EAST ;
			}
			//var route = findRoute_worker(from,to,R1_connector,R2_connector) ;
			
			List<Pair<Integer,Integer>> route = findRoute_worker(from,to,R1_connector,R2_connector) ;
			
			if (route != null) return route ;
			
			// ok.. so those didn't work. Let's try N/S - N/S route, or E/W - E/W route:
			if (x_delta == 0) {			
				route = findRoute_worker(from,to,Direction.EAST,Direction.EAST) ;			
				if (route != null) return route ;
				route = findRoute_worker(from,to,Direction.WEST,Direction.WEST) ;
				if (route != null) return route ;
			}
			else { // y-delta = 0				
				route = findRoute_worker(from,to,Direction.NORTH,Direction.NORTH) ;				
				if (route != null) return route ;
				route = findRoute_worker(from,to,Direction.SOUTH,Direction.SOUTH) ;				
				if (route != null) return route ;
			}
		}
		else {
			// R1 and R2 are not on the same row nor same column
			// First try E/W --> N/S bending
			R1_connector = x_delta>0 ? Direction.EAST : Direction.WEST ;
			R2_connector = y_delta>0 ? Direction.SOUTH : Direction.NORTH ;
			//var route = findRoute_worker(from,to,R1_connector,R2_connector) ;
			List<Pair<Integer,Integer>> route = findRoute_worker(from,to,R1_connector,R2_connector) ;
			if (route != null) return route ;
	
			// that didn't work, so we try N/S --> E/W bending:
			R1_connector = y_delta>0 ? Direction.NORTH : Direction.SOUTH ;
			R2_connector = x_delta>0 ? Direction.WEST : Direction.EAST ;
			route = findRoute_worker(from,to,R1_connector,R2_connector) ;
			if (route != null) return route ;
			
			// try few other bends
			R1_connector = x_delta>0 ? Direction.EAST : Direction.WEST ;
			R2_connector = x_delta>0 ? Direction.WEST : Direction.EAST ;
			route = findRoute_worker(from,to,R1_connector,R2_connector) ;
			if (route != null) return route ;
			
			R1_connector = y_delta>0 ? Direction.NORTH : Direction.SOUTH ;
			R2_connector = y_delta>0 ? Direction.SOUTH : Direction.NORTH ;
			route = findRoute_worker(from,to,R1_connector,R2_connector) ;
			if (route != null) return route ;
			
		}
	
		// none of the above work :|
		// we fall back to the greedy approach of just quantifying over all possible combinations
		// of directions, and see what works:
		Direction[] allDirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST } ;
		for(Direction R1dir : allDirs) {
			for(Direction R2dir : allDirs) {
				//var route = findRoute_worker(from,to,R1_connector,R2_connector) ;
				List<Pair<Integer,Integer>> route = findRoute_worker(from,to,R1_connector,R2_connector) ;
				if (route != null) return route ;
			}
		}
		return null ;
	}
	
	private List<Pair<Integer,Integer>> findRoute_worker(Room R1, Room R2, Direction connectorR1, Direction connectorR2) {
		//var R1_connection = R1.getFreeConnection(connectorR1) ;
		//var R2_connection = R2.getFreeConnection(connectorR2) ;
		Pair<Corridor,Direction> R1_connection = R1.getFreeConnection(connectorR1) ;
		Pair<Corridor,Direction> R2_connection = R2.getFreeConnection(connectorR2) ;
		if (R1_connection == null || R2_connection == null) return null ;
		
		//var location1 = find(R1) ;
		//var location2 = find(R2) ;
		Pair<Integer,Integer> location1 = find(R1) ;
		Pair<Integer,Integer> location2 = find(R2) ;
		
		// start (x,y) at room R1, shifted by the starting R1-direction
		int x = location1.fst ; 
		int y = location1.snd ;
		switch(connectorR1) {
		  case EAST  : x++ ; break ;
		  case WEST  : x-- ; break ;
		  case NORTH : y++ ; break ;
		  case SOUTH : y-- ; break ;
		}
		if (layout[x][y] != null) {
			// if that place is occupied, that no route in that direction is possible anyway
			return null ;
		}
		
		// the goal (x,y) at room R2, shifted by the end direction at R2:
		int goal_x = location2.fst ;
		int goal_y = location2.snd ;
		switch(connectorR2) {
		  case EAST  : goal_x++ ; break ;
		  case WEST  : goal_x-- ; break ;
		  case NORTH : goal_y++ ; break ;
		  case SOUTH : goal_y-- ; break ;
		}
		if (layout[goal_x][goal_y] != null) {
			// if that place is occupied, that no route in that direction is possible anyway
			return null ;
		}
		
		
		// will only search for a path op to this length...
		//var maxPathLength = width + height ;
		int maxPathLength = width + height ;
		
		List<Pair<Integer,Integer>> route = new LinkedList<>() ;
		route.add(new Pair(x,y)) ;
		boolean found = findRoute_worker2(route,x,y,goal_x,goal_y,maxPathLength) ;
		if(!found) return null ;
		return route ;		
	}
	
	private boolean findRoute_worker2(List<Pair<Integer,Integer>> pathSofar, int x, int y, int goal_x, int goal_y, int maxLength) {
		if (x==goal_x && y==goal_y)       return true ;
		if (pathSofar.size() > maxLength) return false ;
		
		// get neigbors which are free and not yet in the path
		
		List<Pair<Integer,Integer>> neighbors = new LinkedList<>() ;
		if (x+1<width)  neighbors.add(new Pair(x+1,y)) ;
		if (0<=x-1)     neighbors.add(new Pair(x-1,y)) ;
		if (y+1<height) neighbors.add(new Pair(x,y+1)) ;
		if (0<=y-1)     neighbors.add(new Pair(x,y-1)) ;
		neighbors = neighbors.stream()
		  .filter(P -> !memberOf(pathSofar,P.fst, P.snd) && layout[P.fst][P.snd] == null)
		  .collect(Collectors.toList());
		// if no such neighbors exists... we can't make a path:
		if (neighbors.isEmpty()) return false ;
		
		// sort the neighbors ascendingly, by their distance to the goal location:
		neighbors.sort((n1,n2) -> 
		          Integer.compare(dist(n1.fst,n1.snd,goal_x,goal_y), 
				                  dist(n2.fst,n2.snd,goal_x,goal_y))) ;
		
		//for(var p : neighbors) {
		for(Pair<Integer,Integer> p : neighbors) {
			pathSofar.add(p) ;
			//var found = findRoute_worker2(pathSofar,p.fst,p.snd,goal_x,goal_y,maxLength) ;
			boolean found = findRoute_worker2(pathSofar,p.fst,p.snd,goal_x,goal_y,maxLength) ;
			if (found) {
				return true ;
			}
			else {
				pathSofar.remove(p) ;
			}
		}
		return false ;
	}
	
	// manhattan distance
	static int dist(int x1, int y1, int x2, int y2) {
		return Math.abs(x2 - x1) + Math.abs(y2 - y1) ;
	}
	
	static boolean memberOf(List<Pair<Integer,Integer>> U, int x, int y) {
		//for(var u : U) {
		for(Pair<Integer,Integer> u : U) {
			if (u.fst == x && u.snd == y) return true ;
		}
		return false ;
	}
	
	static List<Pair<Integer,Integer>> shuffle(List<Pair<Integer,Integer>> z) {
		int N = z.size() ;
		List<Pair<Integer,Integer>> zz = new LinkedList<>() ;
		zz.addAll(z) ;
		z.clear() ;
		Random rnd = new Random(998217) ;
		for(int i=0; i<N; i++) {
			//var P = zz.get(rnd.nextInt(zz.size())) ;
			Pair<Integer,Integer> P = zz.get(rnd.nextInt(zz.size())) ;
			z.add(P) ;
			zz.remove(P) ;
		}
		return z ;
	}
	
	private Pair<Integer,Integer> bottomleft() {
		int bottomleft_x = 0 ;
		int bottomleft_y = 0 ;
		boolean found = false ;
		// find left-most column which is occupied:
		for (int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				if(layout[x][y] != null) {
					bottomleft_x = x ;
					found = true ;
					break ;
				}
			}
			if (found) break ;
		}
		// find lowest row which is occupied:
		found = false ;
		for (int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				if(layout[x][y] != null) {
					bottomleft_y = y ;
					found = true ;
					break ;
				}
			}
			if (found) break ;
		}
		return new Pair(bottomleft_x, bottomleft_y) ;
	}
	
	private Pair<Integer,Integer> topright() {
		int topright_x = 0 ;
		int topright_y = 0 ;
		// find rightmost column that is occupied:
		boolean found = false ;
		for(int x=width-1 ; 0<=x; x--) {
			for (int y=height-1; 0<=y; y--) {
				if (layout[x][y] != null) {
					topright_x = x ;
					found = true ;
					break ;
				}
			}
			if (found) break ;
		}
		// find highest row that is occupied:
		found = false ;
		for(int y=height-1 ; 0<=y; y--) {
			for (int x=width-1; 0<=x; x--) {
				if (layout[x][y] != null) {
					topright_y = y ;
					found = true ;
					break ;
				}
			}
			if (found) break ;
		}
		return new Pair(topright_x,topright_y) ;
	}
	
	/**
	 * Minimize the map by removing empty margins.
	 */
	public Layout clip() {
		//var bl = bottomleft() ;
		Pair<Integer,Integer> bl = bottomleft() ;
		int bottomleft_x = bl.fst ;
		int bottomleft_y = bl.snd ;
		//var tr = topright() ;
		Pair<Integer,Integer> tr = topright() ;
		int topright_x = tr.fst ;
		int topright_y = tr.snd ;
		int width_min  = topright_x - bottomleft_x ;
		int height_min = topright_y - bottomleft_y ;
		Layout lz = new Layout(width_min+1,height_min+1) ;
		for(int x=0; x<lz.width; x++) {
			for(int y=0; y<lz.height; y++) {
				lz.layout[x][y] = layout[x + bottomleft_x][y + bottomleft_y] ;
			}
		}
		return lz ;
	}
	
	/**
	 * Sometimes we have to place a room which is not connected to any room placed so far.
	 * This method will find an empty spot for it.
	 */
	private Pair<Integer,Integer> findASpot() {
		//var bl = bottomleft() ;
		//var tr = topright() ;
		Pair<Integer,Integer> bl = bottomleft() ;
		Pair<Integer,Integer> tr = topright() ;
		// try either right or left of the occupied rectangle
		int x = tr.fst + 3 ;
		if (x>=width) x = bl.fst - 3 ;
		if (x>=0) {
			// so ...the chosen is within the map
			int h = tr.snd - bl.snd ;
			int y = bl.snd + h/2 ;
			return new Pair(x,y) ;
		}
		// the chosen x is negative...
		// try above or below the occupied triangle:
		int w = tr.fst - bl.fst ;
		x = bl.fst + w/2 ;
		int y = tr.snd + 3 ;
		if (y>=width) y = bl.snd - 3 ;
		if (y>=0) {
			return new Pair(x,y) ;
		}
		// uhm... can't find a location. Can be improved of course.. For now just give up:
		return null ;
	}
	
	
	public static Layout drawLayoutWithRetries(List<Room> rooms) {
		int maxNumberOfRetries = 10 ;
		for (int k=0; k<maxNumberOfRetries; k++) {
			Layout layout = drawLayout(rooms) ;
			if (layout != null) return layout ;
		}
		return null ;
	}
	
	public static Layout drawLayoutWithRetries(Room ...rooms) {
		List<Room> rooms_ = new LinkedList<>() ;
		for (int i=0; i<rooms.length; i++)  rooms_.add(rooms[i]) ;
		return drawLayoutWithRetries(rooms_) ;
	}
	
	public static Layout drawLayout(List<Room> rooms) {
			
		List<Room> worklist = new LinkedList<>() ;
		worklist.addAll(rooms) ;
		// sort them descendingly by the number of connections:
		worklist.sort((R1,R2) -> Integer.compare(R2.connections.size(), R1.connections.size())) ;
		
		int width  = 4*rooms.size() + 1 ;
		int height = 4*rooms.size() + 1 ;
		Layout layout = new Layout(width,height) ;
		int centerX = 2*rooms.size() ;
		int centerY = 2*rooms.size() ;
		
		// rooms which are already placed in the Layout
		List<Room> placedRooms = new LinkedList<>() ;
		// rooms which are placed, and whose all their connections are also placed:
		List<Room> doneRooms = new LinkedList<>() ;
		// corridors which are placed in the Layout:
		List<Corridor> placedCorridors = new LinkedList<>() ;
		
		
		// put the first room, which we will then use as the starting anchor to place
		// other rooms
		
		Room R = worklist.get(0) ;
		layout.place(R,centerX,centerY) ;
		placedRooms.add(R) ;
		while (!worklist.isEmpty()) {
		    R = worklist.remove(0) ;
		    // Cover first the case when R is not placed yet. This can only be the case
		    // when R forms its own "island", disconnected from other islands placed
		    // so far.
		    // In this case we need to find some free place to put R.
		    if (!placedRooms.contains(R)) {
		    	//var location = layout.findASpot() ;
		    	Pair<Integer,Integer> location = layout.findASpot() ;
		    	if (location == null) return null ;
		    	layout.place(R,location.fst,location.snd) ;
		    }
		    
		    
		    //var location0 = layout.find(R) ;
		    Pair<Integer,Integer> location0 = layout.find(R) ;
		    int x0 = location0.fst ;
		    int y0 = location0.snd ;
		    
		    // Heuristic: place all neighbors of R, when they are not placed yet.
		    // Then place the corridors leading to them.
		    // Then, we move them to the top of the working list.
			//for(var connection : R.connections) {
		    for(Pair<Corridor,Room.Direction> connection : R.connections) {
				if (placedCorridors.contains(connection.fst)) continue ;
				
				Room R2 = R.getConnectedRoom(connection.fst) ;
				
				// case-1 R2 is already placed:
				if (placedRooms.contains(R2)) {
					// try to connect R1 to R2:
					//var route = layout.findRoute(R,R2) ;
					
					List<Pair<Integer,Integer>> route = layout.findRoute(R,R2) ;
					
					if (route != null) {
						layout.placeCorridor(R,connection.fst,route) ;
						placedCorridors.add(connection.fst) ;
						// move R2 to the head of worklist
						worklist.remove(R2) ;
						worklist.add(0,R2);
						continue ;
					}
					else {
						// we fail:
						return null ;
					}
				}
				
				// case-2 R2 is not placed yet:
				boolean placingR2sucessful = false ;
				
				List<Pair<Integer,Integer>> neighbors = new LinkedList<>() ;
				if (x0+2<width)  neighbors.add(new Pair(x0+2,y0)) ;
				if (x0-2>=0)     neighbors.add(new Pair(x0-2,y0)) ;
				if (y0+2<height) neighbors.add(new Pair(x0,y0+2)) ;
				if (y0-2>=0)     neighbors.add(new Pair(x0,y0-2)) ;
				
				shuffle(neighbors) ;
				
				//for (var nn : neighbors) {
				for (Pair<Integer,Integer> nn : neighbors) {
					if (layout.layout[nn.fst][nn.snd] != null) continue ;
					layout.place(R2,nn.fst,nn.snd) ;
					//var route = layout.findRoute(R,R2) ;
					List<Pair<Integer,Integer>> route = layout.findRoute(R,R2) ;
					if (route != null) {
						layout.placeCorridor(R,connection.fst,route) ;
						placedRooms.add(R2) ;
						placedCorridors.add(connection.fst) ;
						placingR2sucessful = true ;
						break ;
					}
					layout.layout[nn.fst][nn.snd] = null ;
				}
				if(!placingR2sucessful) {
					// fail to place R2. We then simply fail:
					return null ;
				}
				// move R2 to the head of worklist
				worklist.remove(R2) ;
				worklist.add(0,R2);
			}
			// filter worklist:
			for(Room Z : worklist) {
				if(!placedRooms.contains(Z)) continue ;
				boolean allConnectionsWerePlaced = true ;
				//for(var con : Z.connections) {
				for(Pair<Corridor,Room.Direction> con : Z.connections) {
					if (!placedCorridors.contains(con.fst)) allConnectionsWerePlaced = false ;
				}
				if (allConnectionsWerePlaced) {
					doneRooms.add(Z) ;
 				}
			}
			doneRooms.add(R) ;
			worklist = worklist.stream().filter(Z -> ! doneRooms.contains(Z)).collect(Collectors.toList()) ;
		}
		
		// minimize the layout:
		Layout result = layout.clip() ;
		// now place the doors:
		Set<Corridor> allCorridors = new HashSet<>() ;
		for(Room Q : rooms) {
			allCorridors.addAll(Q.connections.stream().map(C -> C.fst).collect(Collectors.toList())) ;
		}
		for(Corridor cor : allCorridors) {
			List<Pair<Integer,Integer>> segments = new LinkedList<>() ;
			segments.addAll(result.findAllSegments(cor)) ;
			// we'll just arrange the doors linearly over the segments:
			if (cor.doors.size() > segments.size()) {
				System.err.println("### currently I can't place " 
			            + cor.doors.size() 
			            + " doors in a corridor of "
						+ segments.size() + " cells long.") ;
				return null ;
			}
			int k=0 ;
			for(Door d : cor.doors) {
				//var position = segments.get(k) ;
				Pair<Integer,Integer> position = segments.get(k) ;
				result.layout[position.fst][position.snd].door = d ;
				k++ ;
			}
		}
		return result ;
		
	}
	
	
	//=====================================
	// few test stuffs
	//=====================================
	
	static void testASCIIprint() {
		//var R0 = new Room(0) ;
		//var R1 = new Room(1) ;
		//var R2 = new Room(2) ;
		//var R3 = new Room(3) ;
		Room R0 = new Room(0) ;
		Room R1 = new Room(1) ;
		Room R2 = new Room(2) ;
		Room R3 = new Room(3) ;
		Corridor.connect(R0,R1) ;
		Corridor.connect(R0,R2) ;
		Corridor.connect(R0,R3) ;
		Corridor.connect(R1,R3) ;
		Layout layout = new Layout(5,4) ;
		layout.place(R0,2,1) ;
		layout.place(R0,R1,Direction.WEST,Direction.WEST) ;
		layout.place(R0,R2,Direction.NORTH,Direction.NORTH) ;
		layout.place(R0,R3,Direction.EAST,Direction.EAST) ;
		
		layout.place(R0,R0.getConnection(R1).fst,Bending.WEST_EAST, Direction.EAST) ;
		layout.place(R0,R0.getConnection(R3).fst,Bending.WEST_EAST, Direction.WEST) ;
		layout.place(R0,R0.getConnection(R2).fst,Bending.NORTH_SOUTH, Direction.NORTH) ;

		layout.place(R1,R1.getConnection(R3).fst,Bending.NORTH_AND_EAST, Direction.SOUTH) ;
		layout.place(R1,R1.getConnection(R3).fst,Bending.WEST_EAST, Direction.SOUTH, Direction.EAST) ;
		layout.place(R1,R1.getConnection(R3).fst,Bending.WEST_EAST, Direction.SOUTH, Direction.EAST, Direction.EAST) ;
		layout.place(R1,R1.getConnection(R3).fst,Bending.WEST_EAST, Direction.SOUTH, Direction.EAST, Direction.EAST, Direction.EAST) ;
		layout.place(R3,R1.getConnection(R3).fst,Bending.NORTH_AND_WEST, Direction.SOUTH) ;
		
	     System.out.println("") ;
	     System.out.println(layout.toString()) ;
	}
	
	static void testAutoLayout_1() {
		 List<Room> rooms = Room.randomGen(6,1,1,1,false) ;
		 //for(var R : rooms) {
		 for(Room R : rooms) {
				System.out.println("======") ;
				System.out.println(R.toString()) ;
			}
		 
		 Layout layout = drawLayout(rooms) ;
		 System.out.println("") ;
	     System.out.println(layout.toString()) ;
	}
	
	static void testAutoLayout_2() {
		 List<Room> rooms = Room.randomGen(5,2,1,1,false) ;
		 //for(var R : rooms) {
		 for(Room R : rooms) {
				System.out.println("======") ;
				System.out.println(R.toString()) ;
			}
		 
		 Layout layout = drawLayout(rooms) ;
		 System.out.println("") ;
	     System.out.println(layout.toString()) ;
	}
	
	static void testAutoLayout_3() {
		 List<Room> rooms = Room.randomGen(6,3,1,1,false) ;
		 //for(var R : rooms) {
		 for(Room R : rooms) {
				System.out.println("======") ;
				System.out.println(R.toString()) ;
			}
		 
		 Layout layout = drawLayoutWithRetries(rooms) ;
		 System.out.println("") ;
	     System.out.println(layout.toString()) ;
	}
	
	// quick test
	public static void main(String[] args) {
		//testASCIIprint() ;
		// testAutoLayout_1() ;
		// testAutoLayout_2() ;
		testAutoLayout_3() ;

	}

}
