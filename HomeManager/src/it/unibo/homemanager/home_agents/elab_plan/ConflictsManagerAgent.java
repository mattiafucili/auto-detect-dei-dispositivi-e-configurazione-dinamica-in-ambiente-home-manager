/*
 * ConflictsManagerAgent.java
 *
 * Created on 22 ottobre 2006, 15.10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.unibo.homemanager.home_agents.elab_plan;

import alice.logictuple.*;
import alice.tucson.api.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.home_agents.PlanTupleBuilder;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.util.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class ConflictsManagerAgent extends AbstractTucsonAgent {

	private TucsonTupleCentreId myTid, casa;
	private TracePanel tp;
	private TupleArgument room;
	// private DeviceService ds = null;
	private ServiceFactory sf;
	private DeviceServiceInterface deviceService;
	private Database database;
	private double roomNewEnergy = 0;
	private Vector devs = null;
	private Hashtable userpref;
	private EnhancedSynchACC acc;

	/**
	 * Creates a new instance of ConflictsManagerAgent
	 */
	public ConflictsManagerAgent(String id, TucsonTupleCentreId tid, TupleArgument r, TucsonTupleCentreId casa,
			TracePanel tp, ServiceFactory sf) throws TucsonInvalidAgentIdException {
		super(id);
		this.myTid = tid;
		this.casa = casa;
		room = r;
		this.tp = tp;
		this.sf = sf;
		this.deviceService = sf.getDeviceServiceInterface();
		try {
			this.database = sf.getDatabaseInterface();
			// ds = new DeviceService();
		} catch (Exception ex) {
			Logger.getLogger(ConflictsManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void operationCompleted(ITucsonOperation ito) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public void operationCompleted(AbstractTupleCentreOperation atco) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	protected void main() {
		tp.appendText("-- CONFLICTS MANAGER AGENT STARTED.");
		acc = this.getContext();

		try {
			// get all preferences regarding room to make the plan about
			LogicTuple prefsTemplate = new LogicTuple("user_pref", new Var("X"), new Var("Y"), new Var("W"),
					new Var("V"));
			ITucsonOperation op_inAll = acc.inAll(myTid, prefsTemplate, Long.MAX_VALUE);
			List prefs = op_inAll.getLogicTupleListResult();
			tp.appendText("ConflictsManagerAgent: RECEIVED " + prefs.size() + " preferences.");
			System.err.println("ConflictsManagerAgent: RECEIVED " + prefs.size() + " preferences.");
			
			for (int i = 0; i < prefs.size(); i++)
				tp.appendText("ConflictsManagerAgent, RECEIVED pref: " + prefs.get(i));
			// determine how many users are in the room
			int nrUsers = prefs.size();
			// get all commands regarding room to make the plan about
			LogicTuple usrCmdTemplate = new LogicTuple("usr_cmd", new Var("X"), new Var("Y"), new Var("Z"),
					new Var("W"));
			op_inAll = acc.inAll(myTid, usrCmdTemplate, Long.MAX_VALUE);
			List cmds = op_inAll.getLogicTupleListResult();
			// tp.appendText("ConflictsManagerAgent, RECEIVED cmds: "+cmds);
			tp.appendText("ConflictsManagerAgent: RECEIVED " + cmds.size() + " commands.");
			System.err.println("ConflictsManagerAgent: RECEIVED " + cmds.size() + " commands.");
			for (int i = 0; i < cmds.size(); i++)
				tp.appendText("ConflictsManagerAgent, RECEIVED cmd: " + cmds.get(i));
			// determine how many commands have been sent to devices in the room
			int nrCmds = cmds.size();
			LogicTuple sendActTemplate = new LogicTuple("send_act", new Var("X"), new Var("Y"), new Var("W"),
					new Var("Z"), new Var("J"));
			op_inAll = acc.inAll(myTid, sendActTemplate, Long.MAX_VALUE);
			List acts = op_inAll.getLogicTupleListResult();
			tp.appendText("ConflictsManagerAgent: RECEIVED " + acts.size() + " acts.");
			System.err.println("ConflictsManagerAgent: RECEIVED " + acts.size() + " acts.");
			for (int i = 0; i < acts.size(); i++)
				tp.appendText("ConflictsManagerAgent, RECEIVED act: " + acts.get(i));
			// ready_new because all data related to current plan have been
			// taken
			LogicTuple barrierTemplate = new LogicTuple("barrier_size", new Var("X"));
			ITucsonOperation op_rd = acc.rd(myTid, barrierTemplate, null);
			LogicTuple barrier = op_rd.getLogicTupleResult();
			// SBLOCCA GLI ALTRI AGENTI IN ATTESA: CMD AGENT E ACT AGENT
			for (int i = 0; i < barrier.getArg(0).intValue(); i++)
				acc.out(myTid, new LogicTuple("ready_new", room), null);
			// SOLVE CONFLICTS
			// decide new temperature
			TupleArgument newTemp = null;
			if (nrUsers > 0) {
				newTemp = decideTemp(prefs, nrUsers);
				System.out.println("Conflicts: newTemp = " + newTemp);
			}

			// find devices in room
			devs = findAllDevInRoom(room.intValue());
			// decide commands for devices
			Vector planCmds = getCmds(prefs, nrUsers, cmds, nrCmds);
			System.out.println("Conflicts: planCmds.size() = " + planCmds.size());
			// decide if commands are to be executed basing on priority factor
			if (evalEnergy(planCmds)) {
				System.out.println("Conflicts - Sono nell'if..");
				// send all commands
				Vector tuples = buildCmdsTuple(planCmds);
				// System.err.println("EVAL ENERGY OK, tuples.size() =
				// "+tuples.size());
				for (int i = 0; i < tuples.size(); i++)
					acc.out(myTid, (LogicTuple) tuples.elementAt(i), null);
				// updating energy for the selected room
				acc.out(myTid, new LogicTuple("upd_energy", room, new Value(roomNewEnergy)), null);
				tp.appendText("Conflicts: new energy consumption for room " + room.intValue() + ": " + roomNewEnergy
						+ " kW.");
			} else {
				// QUI VERIFICA DEI DISPOSITIVI DA DISATTIVARE
				// LogicTuple ind_dev = rdp(casa, new
				// LogicTuple("indispensable_device", new Var("X")));
				// List l = ind_dev.getArg(0).toList();
				// for(int i=0;i<l.size();i++){
				// }
				Vector new_cmds = find_unn_devs(planCmds);
				if (new_cmds.isEmpty() || new_cmds.size() == planCmds.size()) {
					tp.appendText("Conflicts: cannot execute commands in room " + room.intValue() + ": " + roomNewEnergy
							+ " kW requested, HIGHER than AVAILABLE ENERGY. "
							+ "AND ALL DEVICES IN PLAN ARE UNNECESSARY");
				}
			}
			// newTemp is sent only if temp_mode is not "none"
			// and there are no users in the selected room.
			if (newTemp != null) {
				verify_and_act_temp(newTemp);
				LogicTuple tuple = new LogicTuple("new_temp", room, newTemp);
				acc.out(myTid, tuple, null);
				tp.appendText("Conflicts: newTemp for room " + room.intValue() + " = " + newTemp + "° C");
			} else
				tp.appendText("Conflicts: newTemp NOT computed.");

			acc.out(myTid, new LogicTuple("conflicts_end_act", room), null);
			tp.appendText("** CONFLICTS MANAGER AGENT FINISHED.");
		} catch (Exception ex) {
			ex.printStackTrace();
			tp.appendText("ConflictsManagerAgent: error in solving conflicts.");
		}
	}

	private boolean evalEnergy(Vector cmds) {
		double energy, totEnergy = 0;
		boolean res = false;
		try {
			// compute energy consumption of new commands
			roomNewEnergy = computeRoomEnergy(cmds);
			// read priority factor (energy is important only if
			// prio_factor is not users)
			// LogicTuple tuple = rd(casa,new LogicTuple("prio_factor",new
			// Var("X")));
			LogicTuple prioFactorTemplate = new LogicTuple("prio_factor", new Var("X"));
			ITucsonOperation op_rd = acc.rdp(casa, prioFactorTemplate, null);
			LogicTuple tuple = op_rd.getLogicTupleResult();
			System.out.println("evalEnergy: prio_factor - " + tuple);
			if (tuple.getArg(0).toString().compareTo("users") == 0)
				res = true;
			else {
				// read energy consumption in other rooms
				// tuple = rdp(casa, new LogicTuple("total_energy_cons", new
				// Var("X")));
				LogicTuple totEnergyConsTemplate = new LogicTuple("total_energy_cons", new Var("X"));
				ITucsonOperation op_rdp = acc.rdp(casa, totEnergyConsTemplate, null);
				tuple = op_rdp.getLogicTupleResult();
				System.err.println("total_energy_cons:" + tuple.getArg(0));
				// tuple = inp(myTid,new LogicTuple("rd_all",new
				// Value("energy",new Var("X"),
				// new Var("Y")),new Var("W")));
				// System.err.println(tuple);
				// compute total energy consumption in other rooms
				// LogicTuple totalEnergy = in(myTid,new
				// LogicTuple("other_rooms_energy",
				// tuple.getArg(1),room,new Var("X")));
				// totEnergy = totalEnergy.getArg(2).doubleValue();
				// evaluate if commands can be executed
				LogicTuple maxEnergyTemplate = new LogicTuple("max_energy", new Var("X"));
				op_rdp = acc.rdp(casa, maxEnergyTemplate, null);
				LogicTuple max_energy = op_rdp.getLogicTupleResult();// rdp(casa,
																		// new
																		// LogicTuple("max_energy",
																		// new
																		// Var("X")));
				LogicTuple avEnergyTemplate = new LogicTuple("av_energy", tuple.getArg(0), new Value(roomNewEnergy),
						max_energy.getArg(0), new Var("X"));
				System.err.println("in avEnergyTemplate begins..");
				ITucsonOperation op_in = acc.in(myTid, avEnergyTemplate, Long.MAX_VALUE);
				LogicTuple enTuple = op_in.getLogicTupleResult();
				System.err.println("in avEnergyTemplate ends! - " + enTuple);
				// in(myTid,new LogicTuple("av_energy",tuple.getArg(0),
				// new Value(roomNewEnergy), max_energy.getArg(0),new
				// Var("X")));
				energy = enTuple.getArg(3).doubleValue();
				System.out.println("** Energy: " + energy);
				if (energy >= 0) {
					res = true;
					acc.out(casa, new LogicTuple("upd_total_energy", new Value(roomNewEnergy)), null);
				}
				// il consumo � superiore alla quota richiesta: bisogna decidere
				// di eliminare alcune periferiche giudicate
				// non necessarie.
				else {
					res = false;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// System.err.println("--> evalEnergy: res = "+res);
		return res;
	}

	/**
	 * Compute the total energy consumption for the room.
	 */
	private double computeRoomEnergy(Vector cmds) {
		double en = 0;

		try {
			for (int i = 0; i < cmds.size(); i++) {
				Command c = (Command) cmds.elementAt(i);
				if (!c.getState().equals("off")) {
					Device d = deviceService.getDeviceById(database.getDatabase(), c.getIdDev());
					en += d.getDeviceEnergy();
				}
			}
			en += getEnergyFromOtherDevices(cmds);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("** computeRoomEnergy: energy = " + en);
		return en;
	}

	private double getEnergyFromOtherDevices(Vector cmds) {
		boolean present = false;
		int j = 0;
		double en = 0;

		try {
			for (int i = 0; i < devs.size(); i++) {
				while (!present && j < cmds.size()) {
					if (((Command) cmds.elementAt(j)).getIdDev() == ((Device) devs.elementAt(i)).getDeviceId())
						present = true;
					j++;
				}
				if (!present) {
					// LogicTuple state = rd(myTid,new LogicTuple("dev_curr_st",
					// new Value(((Device)devs.elementAt(i)).idDev),new
					// Var("X")));
					LogicTuple devCurrStatusTemplate = new LogicTuple("dev_curr_st",
							new Value(((Device) devs.elementAt(i)).getDeviceId()), new Var("X"));
					ITucsonOperation op_rdp = acc.rdp(myTid, devCurrStatusTemplate, null);
					if (op_rdp.isResultSuccess()) {
						LogicTuple state = op_rdp.getLogicTupleResult();
						if (!state.getArg(1).toString().equals("off"))
							en += ((Device) devs.elementAt(i)).getDeviceEnergy();
					}
				} else
					present = false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return en;
	}

	private Vector buildCmdsTuple(Vector cmds) {
		Vector tuples = new Vector();
		for (int i = 0; i < cmds.size(); i++) {
			Command c = (Command) cmds.elementAt(i);
			tuples.add(PlanTupleBuilder.getPlanCmdsTuple(c, room));
		}
		return tuples;
	}

	private TupleArgument decideTemp(List prefs, int size) {
		double nt = 0;

		try {
			// read current mode (heaters or air conditioners)
			LogicTuple tempTemplate = new LogicTuple("temp_mode", new Var("X"));
			ITucsonOperation op_rd = acc.rd(casa, tempTemplate, null);
			String tMode = op_rd.getLogicTupleResult().getArg(0).toString();
			if (tMode.equals("none"))
				return null;

			// List p = prefs.toList();

			// if(maxPrioPresent(prefs) >= 0){ // there's user with maximum
			// priority
			// System.err.println("TEMP: MAX PRIO PRESENT");
			// nt = PlanTupleBuilder.getTempArg(tMode,
			// findMaxPrioItem(prefs,maxPrioPresent(prefs)));
			// }
			// else {
			System.err.println("TEMP:");
			String userPrefsString = "[";

			for (int i = 0; i < size; i++) {
				userPrefsString += prefs.get(i);
				if (i != size - 1)
					userPrefsString += ",";
			}
			userPrefsString += "]";

			LogicTuple tupleTest = LogicTuple.parse("new_temperature(" + userPrefsString + ", " + size + ",X)");
			System.err.println("userPrefsString: " + userPrefsString);
			System.err.println("tupleTest: " + tupleTest);

			ITucsonOperation op_in = acc.in(casa, tupleTest, Long.MAX_VALUE);
			nt = op_in.getLogicTupleResult().getArg(2).doubleValue();
			System.err.println("nt: " + nt);

			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return nt == 0 ? null : new Value(nt);
	}

	private Vector getCmds(List prefs, int nrPref, List cmds, int nrCmds) {
		Vector planCmds = new Vector();

		try {
			Vector currState = new Vector(); // for consumption
			LogicTuple cmd = null;

			List p = prefs;// .toList();
			List cm = cmds;// .toList();

			// NO COMMANDS
			if (nrCmds == 0) {
				String[] acts = null;

				/**
				 * if there's the user with maximum priority, devices not
				 * involved in his preferences are turned off, others are turned
				 * on.
				 */
				if (nrPref > 1) { // there's user with maximum preference
					// System.err.println("CMD: MAX PRIO PRESENT");
					acts = findAllAct(p);
					// find activation preferences about this room
					Vector actions = Utilities.getActPreferencesInRoom(acts, room.intValue(), this.userpref);
					// turn off all the devices not involved in preference
					Vector offs = turnOthersOff(actions, devs);
					planCmds = Utilities.addVector(planCmds, offs);
					// send commands
					for (int i = 0; i < actions.size(); i++) {
						Command c = new Command(((ActPreference) actions.elementAt(i)).getDev(), "on");
						planCmds.add(c);
					}
				}

				/**
				 * if there's only one user in the room, we can suppose that all
				 * devices are turned off.
				 */
				else if (nrPref == 1) { // just one preference
					System.err.println("CMD: JUST ONE PREFERENCE");
					acts = findAct(LogicTuple.parse(p.get(0).toString()));
					// find activation preferences about this room
					Vector actions = Utilities.getActPreferencesInRoom(acts, room.intValue());
					// send commands
					for (int i = 0; i < actions.size(); i++) {
						Command c = new Command(((ActPreference) actions.elementAt(i)).getDev(), "on");
						System.out.println("##-- getCmds nr " + i + ": " + c.toString());
						planCmds.add(c);
					}
				}
				/**
				 * if there are more users, and no one has maximum priority, we
				 * suppose that the first who has entered the room has his
				 * preferences applied, so we send no commands.
				 */
				else { // more users, no one has maximum priority
					System.out.println("CMD: no commands.");
				}
			}

			// NO PREFERENCES
			else if (nrPref == 0) {
				System.err.println("CMD: NO PREFERENCES; cmds.size = " + nrCmds);
				/**
				 * if there are no users inside the room, we can send all the
				 * commands
				 */
				for (int i = 0; i < nrCmds; i++) {
					LogicTuple tt = LogicTuple.parse(cm.get(i).toString());
					Command c = Command.getCommandFromCmdTuple(tt);
					System.err.println("command " + i + ": " + c.toString());
					planCmds.add(c);
				}
			}

			// BOTH COMMANDS AND PREFERENCES
			else {
				/**
				 * if command is sent by the user who has the maximum priority,
				 * it must be executed.
				 */
				if (maxPrioCmd(cmds) >= 0) {
					System.err.println("CMD: BOTH, MAX PRIO");
					for (int i = 0; i < nrCmds; i++) {
						LogicTuple tt = LogicTuple.parse(cm.get(i).toString());
						Command c = Command.getCommandFromCmdTuple(tt);
						planCmds.add(c);
					}
				}
				/**
				 * if the command is sent by someone with the same priority of
				 * the users currently in the room, it's not executed.
				 */
				else {
					System.out.println("CMD: no commands.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return planCmds;
	}

	private Vector turnOthersOff(Vector activations, Vector devs) {
		boolean present;
		int j = 0;
		Vector cmds = new Vector();

		for (int i = 0; i < devs.size(); i++) {
			present = false;
			int idDev = ((Device) devs.elementAt(i)).getDeviceId();
			while (j < activations.size() && !present) {
				int idDAct = ((ActPreference) activations.elementAt(j)).getDev();
				if (idDev == idDAct)
					present = true;
				else
					j++;
			}
			if (!present) {
				try {
					Command c = new Command(idDev, "off");
					cmds.add(c);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return cmds;
	}

	private Vector findAllDevInRoom(int room) {
		Vector devs = new Vector();
		try {
			ArrayList devsList = deviceService.getDevicesInRoom(database.getDatabase(), room);
			devs.addAll(devsList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return devs;
	}

	private String[] findAllAct(List prefs) {
		String[] acts = null;
		Vector activations = new Vector();
		userpref = new Hashtable();
		for (int i = 0; i < prefs.size(); i++) {
			try {
				LogicTuple ac = LogicTuple.parse(prefs.get(i).toString());
				if (!ac.getArg(3).getName().equals("")) {
					String[] act = findAct(ac);
					ac = LogicTuple.parse(prefs.get(i).toString());
					userpref.put(ac.getArg(0), act);
					activations = Utilities.addArray(activations, act);
				}
			} catch (Exception ex) {
				Logger.getLogger(ConflictsManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		acts = new String[activations.size()];
		activations.copyInto(acts);
		return acts;
	}

	private String[] findAct(LogicTuple lt) {
		String[] devs = null;
		try {
			String a = PlanTupleBuilder.getActivateString(lt);
			devs = Utilities.getSinglesActPreferences(a);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return devs;
	}

	/**
	 * Returns the index of maximum priority user (if present), otherwise it
	 * returns -1
	 */
	private int maxPrioPresent(TupleArgument prefList) {
		int idx = 0;

		/*
		 * try { // find if between preferences there are also those of max
		 * priority user LogicTuple isMaxPrio = inp(myTid,new
		 * LogicTuple("is_max_prio_present", prefList,new Var("X")));
		 * System.err.println("maxPrioPresent: "+isMaxPrio); idx =
		 * isMaxPrio.getArg(1).intValue(); } catch(Exception ex) {
		 * ex.printStackTrace(); }
		 */
		return idx;
	}

	private LogicTuple findMaxPrioItem(TupleArgument elList, int idx) {
		LogicTuple lt = null;

		/*
		 * try { LogicTuple element = inp(myTid,new
		 * LogicTuple("find_item",elList, new Value(idx),new Var("X"))); lt =
		 * LogicTuple.parse(element.getArg(2).toString()); } catch(Exception ex)
		 * { ex.printStackTrace(); }
		 */
		return lt;
	}

	/**
	 * Returns the index of maximum priority user (if he sent commands),
	 * otherwise it returns -1
	 */
	private int maxPrioCmd(List cmdList) {
		int idx = 0;

		String cmdListString = "";
		for (int i = 0; i < cmdList.size(); i++) {
			cmdListString += cmdList;
			if (i < cmdList.size() - 1)
				cmdListString += ",";
		}
		// System.err.println("cmdListString: " + cmdListString);

		try {
			// find if between commands there are also those of max priority
			// user
			// LogicTuple isMaxPrio = inp(myTid,new
			// LogicTuple("is_max_prio_cmd", cmdListString,new Var("X")));
			LogicTuple isMaxPrio = LogicTuple.parse("is_max_prio_cmd(" + cmdListString + ", X)");
			ITucsonOperation op_in = acc.in(myTid, isMaxPrio, Long.MAX_VALUE);
			System.err.println("maxPrioCmd result: " + op_in.getLogicTupleResult());
			idx = op_in.getLogicTupleResult().getArg(1).intValue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return idx;
	}

	private Vector find_unn_devs(Vector cmds) {
		Vector new_cmds = new Vector();
		try {
			ITucsonOperation op_rdAll = acc.rdAll(this.casa, new LogicTuple("unnecessary_device", new Var("X")), null);
			List<LogicTuple> l = op_rdAll.getLogicTupleListResult();
			boolean res = false;
			for (int i = 0; i < cmds.size(); i++) {
				Command c = (Command) cmds.elementAt(i);
				if (!c.getState().equals("off")) {
					int j = 0;
					res = false;
					while (j < l.size() && !res) {
						LogicTuple un = LogicTuple.parse(l.get(j).toString());
						if (un.getArg(0).intValue() == c.getIdDev()) {
							res = true;
							Device dev = deviceService.getDeviceById(database.getDatabase(), c.getIdDev());
							this.tp.appendText("ENERGIA INSUFFICIENTE: ELIMINO DAI COMANDI LA PERIFERICA "
									+ dev.getDeviceName() + " PERCHE' CLASSIFICATA NON INDISPENSABILE!");
						}
						j++;
					}
					if (!res)
						new_cmds.add(c);
				} else
					new_cmds.add(c);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new_cmds;
	}

	private void verify_and_act_temp(TupleArgument newTemp) {
		try {
			LogicTuple extTempTemplate = new LogicTuple("external_temp", new Var("X"));
			ITucsonOperation op_rdp = acc.rdp(this.casa, extTempTemplate, null);
			double temp = op_rdp.getLogicTupleResult().getArg(0).doubleValue();
			LogicTuple tempModeTemplate = new LogicTuple("temp_mode", new Var("X"));
			op_rdp = acc.rdp(this.casa, tempModeTemplate, null);
			String temp_mode = op_rdp.getLogicTupleResult().getArg(0).getName();
			LogicTuple windowsCurrStatusTemplate = new LogicTuple("window_curr_st", this.room, new Var("X"));
			op_rdp = acc.rdp(myTid, windowsCurrStatusTemplate, null);
			String win_mode = op_rdp.getLogicTupleResult().getArg(1).getName();
			if (temp_mode.contains("heat")) {
				if (temp > newTemp.doubleValue())
					this.tp.appendText("I RADIATORI RIMANGONO SPENTI, APRO LE FINESTRE.");
				acc.out(this.myTid, new LogicTuple("window_mode", this.room, new Value("open")), null);
			}
			if (temp_mode.contains("warm")) {
				if (temp < newTemp.doubleValue())
					acc.out(this.myTid, new LogicTuple("window_mode", this.room, new Value("open")), null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
