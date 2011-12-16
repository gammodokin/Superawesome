package com.awesome.srpg;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.awesome.script.dynamic.Agent;
import com.awesome.script.dynamic.Team;
import com.awesome.srpg.object.Unit;
import com.awesome.srpg.strategy.UnitCorner;

public class BattleRecord {

	public final UnitCorner wonCorner;

	private Map<Unit, Integer> unitTimeMap;

	//	Map<Unit, DynamicScripting.Team> unitTeamMap = new HashMap<Unit, DynamicScripting.Team>();
	private Map<Unit, Agent> unitAgentMap = new HashMap<Unit, Agent>();
	private final Team learnTeam;
	//	DynamicScripting.Agent[] cs;

	List<Agent> learnAgents = new LinkedList<Agent>();
	List<Agent> notLearnAgents = new LinkedList<Agent>();

	public BattleRecord(UnitCorner wonCorner, List<Unit> units, Map<Unit, Integer> unitTimeMap) {
		super();
		this.wonCorner = wonCorner;
		this.unitTimeMap = unitTimeMap;

		for(Unit u : units) {
			int time = unitTimeMap.containsKey(u) ? unitTimeMap.get(u) : Agent.D_MAX;
			Agent a = new Agent(u.getStatus().getCurrentHp(), u.getStatus().getMaxHp(), time);

			unitAgentMap.put(u, a);

			if(u.getStatus().getCorner() == UnitCorner.LEARNER)
				learnAgents.add(a);
			else
				notLearnAgents.add(a);
		}

		learnTeam = new Team(
				wonCorner == UnitCorner.LEARNER,
				learnAgents.size(), learnAgents.toArray(new Agent[0]),
				notLearnAgents.size(), notLearnAgents.toArray(new Agent[0]));

		//		boolean won = wonUnits.contains(u);

		SRPG.learnerWinLog += (wonCorner == UnitCorner.LEARNER ? 1 : 0) + "\n";
	}

	public Team getLearnTeam() { return learnTeam; }

	public Agent getAgent(Unit u) { return unitAgentMap.get(u); }

	public List<Agent> getNotLearners() { return notLearnAgents; }

}
