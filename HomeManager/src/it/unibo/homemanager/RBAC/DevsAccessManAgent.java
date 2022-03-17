/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.RBAC;

import alice.tucson.api.*;
import alice.logictuple.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

import it.unibo.homemanager.tablemap.User;

import java.util.List;

import it.unibo.homemanager.userinterfaces.TracePanel;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
/**
 *
 * @author Administrator
 */
public class DevsAccessManAgent extends AbstractTucsonAgent {
    
    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private TupleArgument room;
    private TucsonTupleCentreId rbac;
    private EnhancedSynchACC acc;

    private User us;

    private int UserId;
    private String role;
    private Calendar cal;
   
    private String aID;
    private String dev_name;
    private String dev_class;
    private String agentId;

    public DevsAccessManAgent(String id, TucsonTupleCentreId tid, TucsonTupleCentreId rbac_tc, TracePanel tp) throws TucsonInvalidAgentIdException {
        super(id);
        this.myTid = tid;
        this.rbac = rbac_tc;
        this.tp = tp;
    }

    @Override
    public void operationCompleted(ITucsonOperation ito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void operationCompleted(AbstractTupleCentreOperation atco) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void main() {
        tp.appendText("DEVSACCESSMANAGENT PER " + this.myName());
        this.acc = this.getContext();
        
        while(true){
        try{
            LogicTuple devReqTemplate = new LogicTuple("device_req", new Var("X"), new Var("Y"), new Var("Z"));
            ITucsonOperation op_in = acc.in(this.myTid, devReqTemplate, Long.MAX_VALUE);
            LogicTuple lt = op_in.getLogicTupleResult();
            this.agentId = lt.getArg(0).getName();
            this.UserId = lt.getArg(1).intValue();
            this.dev_name = lt.getArg(2).getName();
            	
	   if(this.dev_name.contains("'"))
		this.dev_name = this.dev_name.substring(1, this.dev_name.length()-1);
            System.err.println("[DevsAccessManAgent] Data: " + agentId + "||" + UserId + "||" + dev_name);
            //ass_class_devs('Home_Entertainment','PC Hp Pavilon')
            LogicTuple assClassTemplate = new LogicTuple("ass_class_devs", new Var("X"), new Value(this.dev_name));
            System.err.println("[DevsAccessManAgent] assClassTemplate: " + assClassTemplate);
	    ITucsonOperation op_rdp = acc.rdp(rbac, assClassTemplate, null);
            lt = op_rdp.getLogicTupleResult();
            
            if(op_rdp.isResultSuccess()) {
                //System.err.println("[DevsAccessManAgent] RDP successful!");
                this.dev_class = lt.getArg(0).getName();
                System.err.println("dev_class: " + this.dev_class);
            }
            else {
                //System.err.println("[DevsAccessManAgent] RDP unsuccessful!");
                //??? non c'è associazione dev-category al massimo!
                this.tp.appendText("COMANDO NON VALIDO!L'UTENTE NON POSSIEDE I DIRITTI NECESSARI!");
            }
            
            LogicTuple activeRoleTemplate = new LogicTuple("active_role", new Value(this.UserId), new Var("Y"));
            op_rdp = acc.rdp(rbac, activeRoleTemplate, null);
            
            if(op_rdp.isResultSuccess()) {
                role = op_rdp.getLogicTupleResult().getArg(1).getName();
                System.err.println("role: " + role);
                if(verify_dev_permission(role, this.dev_class)) {
                    System.out.println("Role " + role + " can access the device");
                    LogicTuple devRulesTemplate = new LogicTuple("dev_rule", new Var("A"), new Value(this.dev_class), new Value(role), new Var("B"), new Var("C"), new Var("D"), new Var("E"));
                    ITucsonOperation op_rdAll = acc.rdAll(this.rbac, devRulesTemplate, null);
                    if(op_rdAll.isResultSuccess()) {
                        //dev_rule(4,'Home_Entertainment','Figlio',['_'],['_','_'],['_','_'],['_','_'])
                        //dev_rule(1,'Home_Entertainment','Bambino',['Genitore'],['19:30','21:30'],['_','_'],['Monday','Friday'])
                        List<LogicTuple> l = op_rdAll.getLogicTupleListResult();
                        //System.err.println("YAY! Found " + l.size() + " rules!!!");
                        
                        this.cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"),Locale.ITALY);
                        this.cal.setFirstDayOfWeek(Calendar.MONDAY);
                        
                        LogicTuple rule;
                        boolean rule_respected = false;
                        int devUsage = -1;
                        for(int i=0; i<l.size();i++){
                            rule = l.get(i);
                            System.err.println("Rule: " + rule);
                            if(validate_presence(rule.getArg(3).toList())) {
                                System.err.println("ValidatePresence = true");
                                if((isDateIndifferent(rule.getArg(5).toList()) && validate_day(rule.getArg(6).toList(), this.cal)) ||
                                          validate_date(rule.getArg(5).toList(), this.cal)){
                                    if(validate_hour(rule.getArg(4).toList(), this.cal)){
                                        //acc.out(this.myTid, new LogicTuple("device_resp", new Value(this.agentId), new Value(this.UserId), new Value(this.dev_name), new Value(1)), null);
                                        devUsage = 1;
                                        rule_respected = true;
                                        break;
                                    }
                                }
                            }
                            else {
                                System.out.println("Ruoli presenti non compatibili con la regola corrente");
                                this.tp.appendText(this.myName() + ": RUOLI PRESENTI NON COMPATIBILI CON LA REGOLA CORRENTE");
                            }
                        }
                        if(!rule_respected)
                            System.err.println("Rules not respected :|");
                        acc.out(this.myTid, new LogicTuple("device_resp", new Value(this.agentId), new Value(this.UserId), new Value(this.dev_name), new Value(devUsage)), null);
                    }
                    else
                        System.err.println("rdAll operation didn't found anyone..");
                }
                else
                    System.err.println("Role " + role + " can't access the device");
            } else
                System.err.println("There's no currently active role for the user!");
            
            /*
                if(rules!=null){
                    List l = rules.getArg(1).toList();
                          this.cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"),Locale.ITALY);
                          this.cal.setFirstDayOfWeek(Calendar.MONDAY);
                          LogicTuple rule;
                          boolean rule_respected = false;
                          for(int i=0;i<l.size();i++){
                              rule = LogicTuple.parse(l.get(i).toString());
                              //SE SONO PRESENTI I RUOLI INDICATI NELLA REGOLA
                              if(validate_presence(rule.getArg(3).toList())){
                                  //SE LA DATA ATTUALE COINCIDE CON QUELLA ESPRESSA NEL VINCOLO
                                  //se la data � indifferente viene controllato il range sui giorni della settimana
                                  //altrimenti la data � predominante sui giorni della settimana
                                  if((isDateIndifferent(rule.getArg(5).toList()) && validate_day(rule.getArg(6).toList(), this.cal)) ||
                                          validate_date(rule.getArg(5).toList(), this.cal)){
                                            if(validate_hour(rule.getArg(4).toList(), this.cal)){
                                                out(this.myTid, new LogicTuple("device_resp", new Value(this.agentId),
                                                        new Value(this.UserId), new Value(this.dev_name), new Value(1)));
                                                rule_respected = true;
                                                break;
                                            }
                                  }else{
                                      System.out.println("Stato del giorno o dell'ora non compatibile con le condizioni attuali");
                                      this.tp.appendText(this.aId.toString()+": STATO DEL GIORNO O DELL'ORA NON COMPATIBILE CON LE" +
                                              "CONDIZIONI ATTUALI");
                                  }

                              }else{
                                  System.out.println("Ruoli presenti non compatibili con la regola corrente");
                                  this.tp.appendText(this.aId.toString()+": RUOLI PRESENTI NON COMPATIBILI CON LA REGOLA CORRENTE");
                              }
                          }
                          if(!rule_respected){
                              this.tp.appendText(this.aId.toString()+": NESSUN VINCOLO AMBIENTALE COMPATIBILE, ACCESSO AL DISPOSITIVO "+this.dev_name+" NEGATO!");
                              out(this.myTid, new LogicTuple("device_resp", new Value(this.agentId),
                                                        new Value(this.UserId), new Value(this.dev_name), new Value(-1)));
                          }
                }else{
                    this.tp.appendText(this.aId.toString()+": non sono stati trovati vincoli validi per il ruolo "+this.role+" all'uso " +
                            "del dispositivo "+this.dev_name);
                    //QUI CI DEVE ANDARE UNA FINESTRA DI AVVISO CHE NON SI HA IL DIRITTO DI ENTRARE NEL LOCALE
                          //PERCHE' NON SONO RISPETTATI I VINCOLI AMBIENTALI
                          javax.swing.JOptionPane.showMessageDialog(null,"ERRORE: NON SONO STATI RILEVATI VINCOLI AMBIENTALI PER LA CLASSE " +
                                  "DI DISPOSITIVI ASSOCIATI AL RUOLO!",
                    "",javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }else
            {
                this.tp.appendText(this.aId.toString()+": il ruolo "+this.role+" non � autorizzato all'uso del dispositivo "+this.dev_name);
                //QUI IL RUOLO DELL'UTENTE NON E' AUTORIZZATO ALL'USO DEL DISPOSITIVO
                out(this.myTid, new LogicTuple("device_resp", new Value(this.agentId),
                                                        new Value(this.UserId), new Value(this.dev_name), new Value(-1)));
            }*/
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    }

    private boolean validate_presence(List requiredRoles) {
        LogicTuple re = null;
        boolean res = false;
        
        try{
            re = LogicTuple.parse(requiredRoles.toString());
            System.err.println("ValidatePresence role: " + re.getArg(0).getName());
            if(re.getArg(0).getName().equals("_"))
                res = true;
            else {
                LogicTuple listCheckTemplate = new LogicTuple("list_checkPeople", new Var("A"), new Var("B"), new Var("C"), new Var("D"));
                ITucsonOperation op_rdAll = acc.rdAll(myTid, listCheckTemplate, null);
                
                if(op_rdAll.isResultSuccess()) {
                    boolean roleFound = false;
                    int nRoleFound = 0;
                    String role;
                    
                    List<LogicTuple> idsFound = op_rdAll.getLogicTupleListResult();
                    for(int i=0; i<requiredRoles.size(); i++) {
                        role = requiredRoles.get(i).toString();
                        System.err.println("[ValPre]role: " + role);
                        for(int j=0; j<idsFound.size() && !roleFound; j++) {
                            LogicTuple idTuple = LogicTuple.parse(idsFound.get(j).toString());
                            System.err.println("idTuple: " + idTuple);
                            LogicTuple idTupleTemplate = new LogicTuple("ass_user_roles", idTuple.getArg(1), new Var("Y"));
                            ITucsonOperation op_rd = acc.rd(rbac, idTupleTemplate, null);
                            if( op_rd.isResultSuccess() && op_rd.getLogicTupleResult().getArg(1).getName().equals(role)){
                                res = true;
                                //count++;
                            }
                        }
                    }
                } else
                    System.err.println("No people found!");
            /*LogicTuple lt = inp(myTid, new LogicTuple("rd_all", , new Var("Y")));
            if(lt!=null){
                boolean res = false;
                String role;
                List l = lt.getArg(1).toList();
                int count = 0;
                int j=0;
                for(int i=0;i<toList.size();i++){
                    role = toList.get(i).toString();

                    while(j<l.size() && !res){
                        LogicTuple pr = LogicTuple.parse(l.get(j).toString());
                        lt = rd(rbac, new LogicTuple("ass_user_roles", pr.getArg(1), new Var("Y")));
                        if(lt.getArg(1).getName().equals(role)){
                            res = true;
                            count++;
                        }
                        j++;
                    }
                }
                if(count == toList.size())
                    return true;
            }else return false;*/
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return res;
    }

    private boolean verify_dev_permission(String role, String dev_class) {
        boolean res = false;
        try {
	    System.err.println("[verify_dev_permission@DAM] role: " + role + " || dev_class: " + dev_class);
                LogicTuple assTemplate = new LogicTuple("ass_roles_perm", new Value(role), new Value(dev_class));
                ITucsonOperation op_rdp = acc.rdp(this.rbac, assTemplate, null);
                
                if(op_rdp.isResultSuccess())
                    res = true;
                
            } catch (Exception ex) {
               ex.printStackTrace();
    }
        return res;
    }

    private boolean isDateIndifferent(List date) {
        boolean res = false;
        
        String fDate = date.get(0).toString();
        String tDate = date.get(1).toString();
        
        if(fDate.equals("'_'") && tDate.equals("'_'"))
            res = true;
        
        return res;
    }

      

       private boolean validate_day(List date, Calendar cal) {
        String fDate = date.get(0).toString();
        String tDate = date.get(1).toString();
        if(fDate.equals("'_'") && tDate.equals("'_'")){
            return true;
        }else{
        int fDay = fromWeekDay_toInt(fDate);
        int tDay = fromWeekDay_toInt(tDate);
        if((cal.get(Calendar.DAY_OF_WEEK)-1> fDay && cal.get(Calendar.DAY_OF_WEEK)-1< tDay) || // il giorno attuale si trova nel range
                cal.get(Calendar.DAY_OF_WEEK)-1== fDay|| //il giorno attuale coincide con il primo giorno del range
                cal.get(Calendar.DAY_OF_WEEK)-1== tDay){ //il giorno attuale coincide con l'ultiamo giorno del range
            return true;
        }else
            return false;
        }
    }


        private int fromWeekDay_toInt(String fDate) {
            Hashtable<Integer,String> ht = new Hashtable();
            ht.put(1, "'Monday'");
            ht.put(2, "'Tuesday'");
            ht.put(3, "'Wednesday'");
            ht.put(4, "'Thursday'");
            ht.put(5, "'Friday'");
            ht.put(6, "'Saturday'");
            ht.put(7, "'Sunday'");

            for(int i=1;i<=ht.size();i++){
                if(ht.get(i).equals(fDate))
                    return i;
            }
        return -1;
    }

    private boolean validate_date(List date, Calendar cal) {
        boolean res = false;
        
        String fDate = date.get(0).toString();
        String tDate = date.get(1).toString();
        StringTokenizer st = new StringTokenizer(fDate, "/");
        String fday = st.nextToken();
        String fmonth = st.nextToken();
        String fyear = st.nextToken();
        StringTokenizer st2 = new StringTokenizer(tDate, "/");
        String tday = st2.nextToken();
        String tmonth = st2.nextToken();
        String tyear = st2.nextToken();
        Calendar fromD = Calendar.getInstance();
        Calendar toD = Calendar.getInstance();
        fromD.set(Integer.parseInt(fyear), Integer.parseInt(fmonth), Integer.parseInt(fday));
        toD.set(Integer.parseInt(tyear), Integer.parseInt(tmonth), Integer.parseInt(tday));

        if((cal.after(fromD) && cal.before(toD))
            ||
            (cal.get(Calendar.YEAR)==fromD.get(Calendar.YEAR) &&
             cal.get(Calendar.MONTH)==fromD.get(Calendar.MONTH) &&
             cal.get(Calendar.DAY_OF_MONTH)==fromD.get(Calendar.DAY_OF_MONTH))
            ||
            (cal.get(Calendar.YEAR)==toD.get(Calendar.YEAR) &&
             cal.get(Calendar.MONTH)==toD.get(Calendar.MONTH) &&
             cal.get(Calendar.DAY_OF_MONTH)==toD.get(Calendar.DAY_OF_MONTH))){
                res = true;
            }
            
        return res;
    }

    private boolean validate_hour(List date, Calendar cal) {
        String fHour = date.get(0).toString().replace("'", "");
        String tHour = date.get(1).toString().replace("'", "");

        if(fHour.equals("_") && tHour.equals("_"))
            return true;

        StringTokenizer st = new StringTokenizer(fHour, ":");
        int fH = Integer.parseInt(st.nextToken());
        int fM = Integer.parseInt(st.nextToken());
        StringTokenizer st2 = new StringTokenizer(tHour, ":");
        int tH = Integer.parseInt(st2.nextToken());
        int tM = Integer.parseInt(st2.nextToken());

        if(cal.get(Calendar.HOUR_OF_DAY)>fH && cal.get(Calendar.HOUR_OF_DAY)<tH ||
                cal.get(Calendar.HOUR_OF_DAY)==fH && cal.get(Calendar.MINUTE)>fM ||
                cal.get(Calendar.HOUR_OF_DAY)==tH && cal.get(Calendar.MINUTE)<tM){
            return true;
        }else
            return false;
    }
}
