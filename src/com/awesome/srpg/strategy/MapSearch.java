package com.awesome.srpg.strategy;

import java.util.ArrayList;

public class MapSearch {

	private double[][] map;
	private double[][] nakedMap;

	private int[][] height;
	//private Field map;
	private boolean[][] zoc;
	private Coord<Integer> current;
	private Tile[][] tileMap;
	private Coord<Integer>[][][] wayMap;

	private ArrayList<Tile> openList;
	private ArrayList<Tile> closedList;

	private final int OBSTACLE = -10;
	// mapには移動コストが入っている。0以下は移動不可。-10以下は障害物。

	@SuppressWarnings("unchecked")
	public MapSearch(int mapWidth, int mapHeight){//Field[][] map) {
		short[][] map = new short[mapHeight][mapWidth];

		this.map = new double[map.length][map[0].length];
		nakedMap = new double[map.length][map[0].length];
		for(int y = 0; y < map.length; y++)
			for(int x = 0; x < map[y].length; x++)
				this.map[y][x] = nakedMap[y][x] = 1;//map[y][x].fd.cost;

		this.height = new int[map.length][map[0].length];
		for(int y = 0; y < map.length; y++)
			for(int x = 0; x < map[y].length; x++)
			this.height[y][x] = 0;//map[y][x].height;

		zoc = new boolean[map.length][map[0].length];

		tileMap = new Tile[map[0].length][map.length];

		// どう見ても異常なし
		wayMap = (Coord<Integer>[][][])new Coord<?>[map[0].length][map.length][];

		openList = new ArrayList<Tile>();
		closedList = new ArrayList<Tile>();

		init();
	}

	public void plotObject(int x, int y) {
		map[y][x] = OBSTACLE;
	}

	public void plotZOC(Coord<Integer> c) {
		plotZOC(c.x, c.y);
	}

	public void plotZOC(int x, int y) {
		zoc[y][x] = true;
	}

	public void setCurrentPos(int x, int y) {
		setCurrentPos(new Coord<Integer>(x, y));
	}

	public void setCurrentPos(Coord<Integer> current) {
		this.current = current;
	}

	/*public boolean search(Coord<Integer> target, int range, boolean f) {
		if(f)
			return searchFloat(target, range);
		else
			return search(target, range);
	}*/

	public boolean search(int targetX, int targetY, int range, int jump) {
		return search(new Coord<Integer>(targetX, targetY), range, jump);
	}

	public boolean search(Coord<Integer> target, int range, int jump) {
		if(!insideMap(target.x, target.y) ||
				map[target.y][target.x] <= 0)
			return false;
		init();

		openList.add(tileMap[current.x][current.y]);

		//boolean zocFlag = false;
		while(!openList.isEmpty()) {
			Tile currentT = getLeastScoreTile(openList);

			if(currentT.cost > range)
				break;

			if(currentT.x == target.x && currentT.y == target.y) {

				@SuppressWarnings("unchecked") // どう見ても異常なし
				Coord<Integer>[] way = (Coord<Integer>[])new Coord<?>[currentT.count];

				Tile t = currentT;
				for(int i = way.length - 1; i >= 0; t = t.parent, i--)
					way[i] = new Coord<Integer>(t.x, t.y);

				wayMap[currentT.x][currentT.y] = way;
				return true;
			}

			closedList.add(currentT);

			if(!(current.x == currentT.x && current.y == currentT.y) && zoc[currentT.y][currentT.x])
				continue;
				/*if(zocFlag)
					continue;
				else
					zocFlag = zoc[currentT.y][currentT.x];
			}*/


//			int neighborX = (currentT.y % 2 == 0 ? currentT.x - 1 : currentT.x + 1);
			Tile[] neighbor = {
					pickTile(currentT.x + 1, currentT.y),
					pickTile(currentT.x - 1, currentT.y),
					pickTile(currentT.x, currentT.y + 1),
					pickTile(currentT.x, currentT.y - 1),
					};

			for(Tile t : neighbor) {
				if(t == null)
					continue;
				int difH = height[t.y][t.x] - height[currentT.y][currentT.x];
				if(!openList.contains(t) && !closedList.contains(t) && map[t.y][t.x] > 0
						&& jump >= difH && difH >= -(jump + 1)) {
					openList.add(t);
					t.parent = currentT;
					t.count = t.parent.count + 1;
					t.cost = t.parent.cost + map[t.y][t.x] + (difH > 0 ? difH/2f : 0);
					t.score = (int)t.cost + distanse(currentT.x, currentT.y, target.x, target.y);
				}
			}
		}

		return false;
	}

	public boolean[][] search(Range range, Coord<Integer> target) {
		boolean[][] availables = new boolean[map[0].length][map.length];
		switch(range.at) {
		case ARCH:
			for(int y = 0; y < map.length; y++)
				for(int x = 0; x < map[y].length; x++)
					availables[x][y] = searchFloat(x, y, range);
			break;
		case RAY:
			for(int y = 0; y < map.length; y++)
				for(int x = 0; x < map[y].length; x++)
					availables[x][y] = raySearch(new Coord<Integer>(x, y), range);
			break;
		case PIERCE:
			for(int y = 0; y < map.length; y++)
				for(int x = 0; x < map[y].length; x++)
					availables[x][y] = searchPierce(new Coord<Integer>(x, y), range);
			break;
		case INTERVAL:
			if(target == null)
				break;

			availables = searchIntervals(target, current, range, true);
			break;
		default:
			availables = null;
		System.err.println("Illegal RanageType : " + range.at);
		}
		return availables;
	}

//	public boolean[][] search(Range range, Coord<Integer> target) {
//		boolean[][] availables;
//
//		switch(range.at) {
//		case INTERVAL:
//			availables = searchIntervals(new Coord<Integer>(x, y), target, true);
//			break;
//		default:
//			availables = search(range);
//		}
//
//		return availables;
//	}

	public boolean searchFloat(int x, int y, Range range) {
		if(!insideMap(x, y))
			return false;

		int d = distanse(current.x, current.y, x, y);
		int dh = Math.abs(height[y][x] - height[current.y][current.x]);

		if((d <= range.max && dh <= range.maxH)
				&& ( d >= range.min || dh >= range.minH))
			return true;

		return false;
	}

	public boolean searchFloat(Coord<Integer> target, Range range) {
		return searchFloat(target.x, target.y, range);
	}

	public boolean raySearch(Coord<Integer> target, Range range) {
		return raySearch(target, range, map);
	}
	private static int obstacleHeight = 4;
	private static int unitHeight = 2;
	private boolean raySearch(Coord<Integer> target, Range range, double[][] map) {
		/*if(Math.abs(height[target.y][target.x] - height[current.y][current.x]) > rangeH
				|| distanse(current.x, current.y, target.x, target.y) > range
				|| !insideMap(target.x, target.y))*/
		if(!searchFloat(target.x, target.y, range))
			return false;

		int hexSize = 128;
		double r = hexSize/2 * 1.732/2 * 1.2;

		Coord<Double> currentC = getCoordByHex(current.x, current.y, hexSize);
		Coord<Double> targetC = getCoordByHex(target.x, target.y, hexSize);

		double thetaL = Math.atan2(targetC.y - currentC.y, targetC.x - currentC.x);

		for(int y = 0; y < map.length; y++)
			for(int x = 0; x < map[y].length; x++) {
				if(/*map[y][x] > OBSTACLE
						|| */(target.x == x && target.y == y)
						|| (current.x == x && current.y == y)
						|| distanse(current.x, current.y, x, y) > range.max)
					continue;

				Coord<Double> barC = getCoordByHex(x, y, hexSize);

				int offsetH = 0;
				if(map[y][x] <= OBSTACLE)
					offsetH = obstacleHeight;

				int difH = height[target.y][target.x] - height[current.y][current.x];
				int h = height[y][x]  + offsetH - unitHeight;// - (difH < 0 ? height[target.y][target.x] : height[current.y][current.x]);
				if((barC.x - currentC.x) * difH / (targetC.x - currentC.x) + height[current.y][current.x] > h
						|| (barC.y - currentC.y) * difH / (targetC.y - currentC.y) + height[current.y][current.x] > h)
				continue;

				double theta = Math.atan2(barC.y - currentC.y, barC.x - currentC.x) - thetaL;

				if(Math.cos(theta) < 0 || Math.cos(thetaL - Math.atan2(targetC.y - barC.y, targetC.x - barC.x)) < 0)
					continue;

				if(Math.hypot(barC.x - currentC.x, barC.y - currentC.y) * Math.abs(Math.sin(theta)) < r)
					return false;
			}
		return true;
		/*Cood<Double> currentC;
		Cood<Double> targetC = getHexCood(target.x, target.y);

		Cood<Integer> currentHex = new Cood<Integer>(current.x - current.y / 2, current.y);
		int count = 1;
		while(currentHex.x + currentHex.y / 2 != target.x || currentHex.y != target.y) {
			if(count > 1 && insideMap(currentHex.x + currentHex.y / 2, currentHex.y)
					&& (map[currentHex.y][currentHex.x + currentHex.y / 2] <= OBSTACLE || count > range))
				return false;

			currentC = getHexCood(currentHex.x + currentHex.y / 2, currentHex.y);
			double dir = Math.atan2(targetC.y - currentC.y, targetC.x - currentC.x);

			double d30 = Math.PI / 6;
			if(dir <= -d30 * 5 || d30 * 5 < dir){
				currentHex.x--;
			}else if(-d30 * 5 < dir && dir <= -d30 * 3) {
				currentHex.y--;
			}else if(-d30 * 3 < dir && dir <= -d30) {
				currentHex.y--;
				currentHex.x++;
			}else if(-d30 < dir && dir <= d30) {
				currentHex.x++;
			}else if(d30 < dir && dir <= d30 * 3) {
				currentHex.y++;
			}else {
				currentHex.y++;
				currentHex.x--;
			}

			count++;
		}
		return true;*/
	}

	private boolean searchPierce(Coord<Integer> target, Range range) {
		return raySearch(target, range, nakedMap);
	}

	// Piercing可能なエリアをターゲットに入れる
	private boolean[][] searchIntervals(Coord<Integer> current, Coord<Integer> target, Range range, boolean pierceUnit) {
		Range span = new Range(RangeType.ARCH, 0, distanse(current, target),
				0, Math.abs(height[current.y][current.x] - height[target.y][target.x]));

		boolean[][] availables = new boolean[map[0].length][map.length];
		availables[target.x][target.y] = true;

		if(!searchFloat(target.x, target.y, span))
			return availables;

		int hexSize = 128;
		double r = hexSize/2 * 1.732/2 * (1.2 + range.max);

		Coord<Double> currentC = getCoordByHex(current.x, current.y, hexSize);
		Coord<Double> targetC = getCoordByHex(target.x, target.y, hexSize);

		double thetaL = Math.atan2(targetC.y - currentC.y, targetC.x - currentC.x);

		for(int y = 0; y < map.length; y++)
			for(int x = 0; x < map[y].length; x++) {
				if(/*map[y][x] > OBSTACLE
						|| */(target.x == x && target.y == y)
						|| (current.x == x && current.y == y)
						|| distanse(current.x, current.y, x, y) > span.max)
					continue;

				Coord<Double> barC = getCoordByHex(x, y, hexSize);

//				if(range.max < 1) {
//					if(isOnRay(x, y, current, target, currentC, targetC, barC, -unitHeight)
//							|| !isOnRay(x, y, current, target, currentC, targetC, barC, obstacleHeight))
//						continue;
//				}

//				if(Math.abs(height[y][x] - height[current.y][current.x]) > range.maxH
//						&& Math.abs(height[y][x] - height[target.y][target.x]) > range.maxH)
//					continue;

				double theta = Math.atan2(barC.y - currentC.y, barC.x - currentC.x) - thetaL;

				if(Math.cos(theta) < 0 || Math.cos(thetaL - Math.atan2(targetC.y - barC.y, targetC.x - barC.x)) < 0)
					continue;

				if(Math.hypot(barC.x - currentC.x, barC.y - currentC.y) * Math.abs(Math.sin(theta)) < r)
					availables[x][y] = true;
			}
		return availables;
	}

	private boolean isOnRay(int x, int y, Coord<Integer> current, Coord<Integer> target, Coord<Double> currentC, Coord<Double> targetC, Coord<Double> barC, int offsetH) {
		int difH = height[target.y][target.x] - height[current.y][current.x];
		int h = height[y][x] + offsetH - unitHeight;// - (difH < 0 ? height[target.y][target.x] : height[current.y][current.x]);
		if((barC.x - currentC.x) * difH / (targetC.x - currentC.x) + height[current.y][current.x] > h
				|| (barC.y - currentC.y) * difH / (targetC.y - currentC.y) + height[current.y][current.x] > h)
			return false;

		return true;
	}

	private Coord<Double> getCoordByHex(Coord<Integer> c) {
		return getCoordByHex(c.x, c.y);
	}
	private Coord<Double> getCoordByHex(double x, double y) {
		return getCoordByHex(x, y, 128);// 128は理論上は1でもいいはずだが、数値が小さいとatan2()が正常に動かない。
	}
	private Coord<Double> getCoordByHex(double x, double y, int hexSize) {
		return new Coord<Double>(x * hexSize + (y % 2 == 0 ? 0 : hexSize / 2), y * hexSize * 3 / 4f);
	}

	public Coord<Integer>[] getWay(int x, int y) {
		return wayMap[x][y];
	}

	public static int distanse(Coord<Integer> c0, Coord<Integer> c1) {
		return distanse(c0.x.intValue(), c0.y.intValue(), c1.x.intValue(), c1.y.intValue());
	}
	public static int distanse(int x0, int y0, int x1, int y1) {
//		x0 = toDoubleXCoord(x0, y0);//x0 * 2 + (y0 % 2 == 0 ? -1 : 0);
//		x1 = toDoubleXCoord(x1, y1);//x1 * 2 + (y1 % 2 == 0 ? -1 : 0);
//		int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
//		if(dy > dx)
//			return dy;
//		else
//			return (dx + dy) / 2;
		return Math.abs(x1 - x0) + Math.abs(y1 - y0);
	}

//	private static Coord<Integer> toVectorCoord(Coord<Integer> c) {
//		c.x = toVectorCoord(c.x, c.y);
//		return c;
//	}
	// x座標を返す
//	private static int toVectorCoord(int x, int y) {
//		return x - y / 2;
//	}
//	private static int toDoubleXCoord(int x, int y) {
//		return x * 2 + (y % 2 == 0 ? -1 : 0);
//	}

	private void init() {
		openList.clear();
		closedList.clear();
		for(int y = 0; y < map.length; y++)
			for(int x = 0; x < map[y].length; x++)
				tileMap[x][y] = new Tile(x, y);
	}

	private Tile getLeastScoreTile(ArrayList<Tile> al) {
		Tile min = al.get(0);
		for(Tile t : al)
			if(min.score > t.score)
				min = t;
		al.remove(min);
		return min;
	}

	private Tile pickTile(int x, int y) {
		if(!insideMap(x, y))
			return null;
		return tileMap[x][y];
	}

	private boolean insideMap(int x, int y) {
		return 0 <= x && x < map[0].length &&
			0 <= y && y < map.length;
	}

	private class Tile {
		Tile parent;
		int x, y;
		double cost;
		int score;
		int count;

		Tile(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

}