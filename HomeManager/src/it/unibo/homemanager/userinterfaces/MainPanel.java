package it.unibo.homemanager.userinterfaces;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import alice.tucson.api.TucsonTupleCentreId;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.agents.TucsonAgentInterface;
import it.unibo.homemanager.detection.AgentManager;
import it.unibo.homemanager.detection.AgentsFactory;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.detection.DeviceListener;
import it.unibo.homemanager.detection.DeviceManagerAgent;
import it.unibo.homemanager.tablemap.User;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private TracePanel tracePanel;
	
	private Map<String, TucsonTupleCentreId> tupleCentres;

	private JPanel cards;
	private CardLayout cardLayout;
	private User user = null;

	private AccessPanel accessPanel;
	private IssueCmdPanel issueCmdPanel;
	private InitAdminPanel initAdminPanel;
	private InitOrdinaryPanel initOrdinaryPanel;
	private InfoPanel infoPanel;
	private PolicyPanel policyPanel;
	private VisitorsPanel visitorsPanel;
	private ViewPlanPanel viewPlanPanel;
	private ViewPolicyPanel viewPolicyPanel;
	private ManageProfilePanel manageProfilePanel;
	private ManageUsersPanel manageUsersPanel;

	private ViewManageAgentPanel butlerPanel;

	private RBACManager rbacPanel;
	private ManRoles manRolesPanel;
	private ManRolesPerm manRolesPermPanel;
	private ManUsersRoles manUsersRolesPanel;
	private ManPerm manPermPanel;
	private ManRBACConstr manRBACConstrPanel;
	private ManDevicesConstr manDevicesConstrPanel;
	private ManRoomsConstr manRoomsConstrPanel;
	private ViewRBACPolicy viewRBACPolicyPanel;
	private ViewUserRBAC viewUserRBACPanel;
	private RBACStatistics rbacStatisticsPanel;

	private final static String ACCESS_PANEL = "Log In";
	private final static String INIT_ADMIN_PANEL = "Welcome Administrator";
	private final static String INIT_ORDINARY_PANEL = "Welcome User";
	private final static String ISSUE_CMD_PANEL = "Issue Command";
	private final static String INFO_PANEL = "View Info";
	private final static String POLICY_PANEL = "Decide policy";
	private final static String VISITORS_PANEL = "Manage Visitors";
	private final static String VIEW_PLAN_PANEL = "View Plan";
	private final static String VIEW_POLICY_PANEL = "View Policy";
	private final static String MANAGE_PROFILE_PANEL = "Manage Profile";
	private final static String MANAGE_USERS_PANEL = "Manage Users";

	private final static String BUTLER = "BUTLER";

	private final static String MANAGE_RBAC_PANEL = "Manage RBAC";
	private final static String ROLES_PANEL = "Manage ROLES";
	private final static String ROLES_PERMS_PANEL = "Manage Ass. ROLES-PERM.";
	private final static String USER_ROLES_PANEL = "Manage Ass. USER-ROLES";
	private final static String PERMS_PANEL = "Manage PERMISSIONS";
	private final static String RBAC_CONSTR_PANEL = "Manage RBAC CONSTRAINTS";
	private final static String DEV_CONSTR_PANEL = "Manage DEVICE CONSTRAINTS";
	private final static String ROOMS_CONSTR_PANEL = "Manage ROOMS CONSTRAINTS";
	private final static String VIEW_RBAC_PANEL = "Manage RBAC POLICY";
	private final static String VIEW_RBAC_USR_PANEL = "Manage RBAC USER POLICY";
	private final static String RBAC_STATISTICS = "Manage RBAC STATISTICS";

	public MainPanel() {
		lookAndFeel();
	}

	public MainPanel(TracePanel tracePanel, Vector<TucsonTupleCentreId> tupleCentresList, int roomId, int sensorId, 
						TucsonTupleCentreId casa_tc, TucsonTupleCentreId rbac_tc, ServiceFactory serviceFactory) {
		this.tracePanel = tracePanel;
		
		lookAndFeel();
		initTupleCentres(tupleCentresList);
		
		cardLayout = new CardLayout();
		cards = new JPanel(cardLayout);

		accessPanel = new AccessPanel(this, tracePanel, roomId, sensorId, tupleCentresList.elementAt(2), rbac_tc, casa_tc, serviceFactory);
		cards.add(accessPanel, ACCESS_PANEL);
		issueCmdPanel = new IssueCmdPanel(this, tupleCentresList, serviceFactory);
		cards.add(issueCmdPanel, ISSUE_CMD_PANEL);
		initAdminPanel = new InitAdminPanel(this, tupleCentresList, casa_tc, rbac_tc);
		cards.add(initAdminPanel, INIT_ADMIN_PANEL);
		initOrdinaryPanel = new InitOrdinaryPanel(this, casa_tc, rbac_tc);
		cards.add(initOrdinaryPanel, INIT_ORDINARY_PANEL);
		infoPanel = new InfoPanel(this, serviceFactory);
		cards.add(infoPanel, INFO_PANEL);
		policyPanel = new PolicyPanel(this, casa_tc, serviceFactory);
		cards.add(policyPanel, POLICY_PANEL);
		visitorsPanel = new VisitorsPanel(this, serviceFactory);
		cards.add(visitorsPanel, VISITORS_PANEL);
		viewPlanPanel = new ViewPlanPanel(this, tupleCentresList, serviceFactory);
		cards.add(viewPlanPanel, VIEW_PLAN_PANEL);
		viewPolicyPanel = new ViewPolicyPanel(this, casa_tc, serviceFactory);
		cards.add(viewPolicyPanel, VIEW_POLICY_PANEL);
		manageProfilePanel = new ManageProfilePanel(this, rbac_tc, serviceFactory);
		cards.add(manageProfilePanel, MANAGE_PROFILE_PANEL);
		manageUsersPanel = new ManageUsersPanel(this, tupleCentresList, serviceFactory);
		cards.add(manageUsersPanel, MANAGE_USERS_PANEL);

		butlerPanel = new ViewManageAgentPanel(this);
		cards.add(butlerPanel, BUTLER);

		rbacPanel = new RBACManager(this);
		cards.add(rbacPanel, MANAGE_RBAC_PANEL);
		manRolesPanel = new ManRoles(this, rbac_tc, rbacPanel);
		cards.add(manRolesPanel, ROLES_PANEL);
		manRolesPermPanel = new ManRolesPerm(this, rbac_tc, rbacPanel);
		cards.add(manRolesPermPanel, ROLES_PERMS_PANEL);
		manUsersRolesPanel = new ManUsersRoles(this, rbac_tc, rbacPanel, serviceFactory);
		cards.add(manUsersRolesPanel, USER_ROLES_PANEL);
		manPermPanel = new ManPerm(this, rbac_tc, rbacPanel, serviceFactory);
		cards.add(manPermPanel, PERMS_PANEL);
		manRBACConstrPanel = new ManRBACConstr(this, rbac_tc, rbacPanel);
		cards.add(manRBACConstrPanel, RBAC_CONSTR_PANEL);
		manDevicesConstrPanel = new ManDevicesConstr(this, rbac_tc, rbacPanel);
		cards.add(manDevicesConstrPanel, DEV_CONSTR_PANEL);
		manRoomsConstrPanel = new ManRoomsConstr(this, rbac_tc, rbacPanel, serviceFactory);
		cards.add(manRoomsConstrPanel, ROOMS_CONSTR_PANEL);
		viewRBACPolicyPanel = new ViewRBACPolicy(this, rbac_tc, rbacPanel, serviceFactory);
		cards.add(viewRBACPolicyPanel, VIEW_RBAC_PANEL);
		viewUserRBACPanel = new ViewUserRBAC(this, rbac_tc, casa_tc);
		cards.add(viewUserRBACPanel, VIEW_RBAC_USR_PANEL);
		rbacStatisticsPanel = new RBACStatistics(this, rbac_tc, rbacPanel, serviceFactory);
		cards.add(rbacStatisticsPanel, RBAC_STATISTICS);

		AgentManager agentManager = AgentManager.getInstance();
		agentManager.initComponents(serviceFactory, butlerPanel, tracePanel, tupleCentres);
		
		for ( TucsonAgentInterface agent : agentManager.getAgents() )
			cards.add(agent.getInterface(), agent.getName());

		cards.setLocation(0, 0);
		add(cards);
		loadAccessPanel();
		
		try {
			DeviceManagerAgent deviceManagerAgent = DeviceManagerAgent.getInstance();
			deviceManagerAgent.addListener(new DeviceListener() {
				@Override
				public void onNewDevice(Device device) {
					onNewDeviceEvent(device);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onNewDeviceEvent(Device device) {
		if (device.getDeviceType().equals("s")) {
			AgentManager agentManager = AgentManager.getInstance();
			TucsonAgentInterface agent = AgentsFactory.createAgent(device, tupleCentres, tracePanel, agentManager.getDatabase(), butlerPanel);
			if (agent != null) {
				agentManager.addAgent(agent);
				agentManager.goAgent(agent.getName());
				cards.add(agent.getInterface(), agent.getName());
			}
		}
	}

	private void lookAndFeel() {
		Color darkBlue = new Color(0, 0, 102);
		Border etchedBorder = BorderFactory.createEtchedBorder(null, darkBlue);
		Font font = new Font("Courier New", 1, 12);
		Color superDarkBlue = new Color(0, 0, 51);
		Border border = BorderFactory.createTitledBorder(etchedBorder, "Home Manager", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, font, superDarkBlue);
		setBorder(border);
		setBounds(0, 0, 538, 343);
		Color violet = new Color(204, 204, 255);
		setBackground(violet);
	}

	private void initTupleCentres(Vector<TucsonTupleCentreId> tupleCentresList) {
		tupleCentres = new HashMap<String, TucsonTupleCentreId>();
		for (int i = 0; i < tupleCentresList.size(); i++) {
			TucsonTupleCentreId agent = tupleCentresList.elementAt(i);
			if (agent.getName().equals("meteo_tc"))
				tupleCentres.put("meteo_tc", agent);
			if (agent.getName().equals("twitter_tc"))
				tupleCentres.put("twitter_tc", agent);
			if (agent.getName().equals("device_manager_tc"))
				tupleCentres.put("device_manager_tc", agent);
			if (agent.getName().equals("usage_manager_tc"))
				tupleCentres.put("usage_manager_tc", agent);
			if (agent.getName().equals("mixer_tc"))
				tupleCentres.put("mixer_tc", agent);
			if (agent.getName().equals("mixer_container_tc"))
				tupleCentres.put("mixer_container_tc", agent);
			if (agent.getName().equals("fridge_tc"))
				tupleCentres.put("fridge_tc", agent);
			if (agent.getName().equals("oven_tc"))
				tupleCentres.put("oven_tc", agent);
			if (agent.getName().equals("pantry_tc"))
				tupleCentres.put("pantry_tc", agent);
		}
	}
	
	private void loadAccessPanel() {
		accessPanel.init();
		butlerPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		cardLayout.show(cards, ACCESS_PANEL);
	}

	void loadInitRolePanel(User usr) {
		user = usr;
		System.err.println("[loadInitRolePanel@MainPanel] user: " + user.toString());
		if (user.getRoleChar() == 'A')
			loadInitAdminPanel();
		else if (user.getRoleChar() == 'O')
			loadInitOrdinaryPanel();
	}

	private void loadInitAdminPanel() {
		initAdminPanel.init(user);
		butlerPanel.hidePanel();
		issueCmdPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, INIT_ADMIN_PANEL);
	}

	private void loadInitOrdinaryPanel() {
		initOrdinaryPanel.init(user);
		butlerPanel.hidePanel();
		issueCmdPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		initAdminPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, INIT_ORDINARY_PANEL);
	}

	void loadIssueCmdPanel() {
		issueCmdPanel.init(user);
		butlerPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, ISSUE_CMD_PANEL);
	}

	void logOut(ActionEvent evt) {
		loadAccessPanel();
	}

	void loadManRBACPanel() {
		rbacPanel.init(user);
		butlerPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, MANAGE_RBAC_PANEL);
	}

	void loadManRolePanel() {
		manRolesPanel.init(user);
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, ROLES_PANEL);
	}

	void loadManUsRolPanel() {
		manRolesPanel.hidePanel();
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.init(user);
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, USER_ROLES_PANEL);
	}

	void loadManRolPermPanel() {
		manRolesPanel.hidePanel();
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.init(user);
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, ROLES_PERMS_PANEL);
	}

	void loadManPermPanel() {
		manRolesPanel.hidePanel();
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.init(user);
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, PERMS_PANEL);
	}

	void loadManRBACContrPanel() {
		manRolesPanel.hidePanel();
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.init(user);
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, RBAC_CONSTR_PANEL);
	}

	void loadManDevContrPanel() {
		manRolesPanel.hidePanel();
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.init(user);
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, DEV_CONSTR_PANEL);
	}

	void loadManRoomContrPanel() {
		manRolesPanel.hidePanel();
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.init(user);
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, ROOMS_CONSTR_PANEL);
	}

	void loadViewRBACPanel() {
		manRolesPanel.hidePanel();
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.init(user);
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, VIEW_RBAC_PANEL);
	}

	void loadRBACStatisticsPanel() {
		manRolesPanel.hidePanel();
		butlerPanel.hidePanel();
		rbacPanel.hidePanel();
		issueCmdPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.init(user);
		cardLayout.show(cards, RBAC_STATISTICS);
	}

	void loadInfoPanel() {
		infoPanel.init(user);
		butlerPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		issueCmdPanel.hidePanel();
		policyPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, INFO_PANEL);
	}

	void loadVisitorsPanel() {
		visitorsPanel.init(user);
		butlerPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		issueCmdPanel.hidePanel();
		policyPanel.hidePanel();
		infoPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, VISITORS_PANEL);
	}

	void loadPolicyPanel() {
		policyPanel.init(user);
		butlerPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		issueCmdPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, POLICY_PANEL);
	}

	void loadViewPlanPanel() {
		viewPlanPanel.init(user);
		butlerPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		issueCmdPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, VIEW_PLAN_PANEL);
	}

	void loadViewPolicyPanel() {
		viewPolicyPanel.init(user);
		butlerPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		issueCmdPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, VIEW_POLICY_PANEL);
	}

	void loadManageProfilePanel() {
		manageProfilePanel.init(user);
		butlerPanel.hidePanel();
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		issueCmdPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, MANAGE_PROFILE_PANEL);
	}

	void loadManageUsersPanel() {
		initAdminPanel.hidePanel();
		butlerPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		issueCmdPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.init(user);
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.hidePanel();
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, MANAGE_USERS_PANEL);
	}

	void loadViewUsersRBACPanel() {
		initAdminPanel.hidePanel();
		butlerPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		issueCmdPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		viewUserRBACPanel.init(user);
		rbacStatisticsPanel.hidePanel();
		cardLayout.show(cards, VIEW_RBAC_USR_PANEL);
	}

	public void loadAgentPanel(TucsonAgentInterface agent) {
		butlerPanel.hidePanel();
		agent.show();
	}

	public void loadButlerPanel() {
		initAdminPanel.hidePanel();
		initOrdinaryPanel.hidePanel();
		accessPanel.hidePanel();
		infoPanel.hidePanel();
		policyPanel.hidePanel();
		issueCmdPanel.hidePanel();
		visitorsPanel.hidePanel();
		viewPlanPanel.hidePanel();
		viewPolicyPanel.hidePanel();
		manageProfilePanel.hidePanel();
		manageUsersPanel.hidePanel();
		rbacPanel.hidePanel();
		manRolesPanel.hidePanel();
		manRolesPermPanel.hidePanel();
		manUsersRolesPanel.hidePanel();
		manPermPanel.hidePanel();
		manRBACConstrPanel.hidePanel();
		manDevicesConstrPanel.hidePanel();
		manRoomsConstrPanel.hidePanel();
		viewRBACPolicyPanel.hidePanel();
		butlerPanel.init(user);
		cardLayout.show(cards, BUTLER);
	}
}