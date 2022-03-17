/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.RBAC;

import alice.logictuple.*;
import alice.tucson.api.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.ServiceFactory;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.tablemap.Room;
import it.unibo.homemanager.tablemap.Sensor;
import it.unibo.homemanager.tablemap.User;
import it.unibo.homemanager.tablemap.Visitor;
import it.unibo.homemanager.userinterfaces.TracePanel;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Administrator
 */
public class RoomAccessManAgent extends AbstractTucsonAgent {

    private TucsonTupleCentreId myTid;
    private TracePanel tp;
    private TupleArgument room;
    private TucsonTupleCentreId rbac, casa;
    private Sensor sens;
    private User us;
    private Visitor vis;
    private Room rm;

    private int UserId;
    private String role;
    private Calendar cal;
    private EnhancedSynchACC acc;
    private ServiceFactory sf;
    private Database ds;
    
    public RoomAccessManAgent(String id, TracePanel tp, TucsonTupleCentreId tid, TucsonTupleCentreId rbac_tc, Sensor s, TucsonTupleCentreId casa_tc, ServiceFactory sf) throws TucsonInvalidAgentIdException {
        super(id);
        this.myTid = tid;
        this.rbac = rbac_tc;
        this.casa = casa_tc;
        this.sens = s;
        this.tp = tp;
	this.sf = sf;
	try {
	    this.ds = sf.getDatabaseInterface();
	} catch (Exception ex) {
	    Logger.getLogger(RoomAccessManAgent.class.getName()).log(Level.SEVERE, null, ex);
	}
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
        tp.appendText("-- ROOM ACCESS MANAGER "+sens.name+" STARTED.");
        this.acc = this.getContext();

        try{
            while(true){
                ITucsonOperation op_in = acc.in(this.myTid,
                        new LogicTuple("detection_req",
                                new Value("entry"),
                                new Value(sens.idSens), //sensor
                                new Var("Y"),           //id person
                                new Var("Z")),          //room
                        Long.MAX_VALUE);
                
                LogicTuple detectionReq = op_in.getLogicTupleResult();
                this.rm = sf.getRoomServiceInterface().getRoomById(ds.getDatabase(), detectionReq.getArg(3).intValue());
                //si verifica se il locale è protetto da permessi
                if(verify_room_permission(this.rm.name)){
                   /*lt = in(myTid, new LogicTuple("biometric_relief", new Value(aId.toString()),
                           new Var("X"), // person's id
                           new Var("Y"))); // score*/

                   //if(!lt.getArg(1).getName().equals("unknown")){
                    if(detectionReq.getArg(2).intValue()>0) {
                        //this.UserId = retrive_user(lt.getArg(1).getName());
                        this.UserId = detectionReq.getArg(2).intValue();
                        retrive_user(this.UserId);

                        ///////////////////////////////////////////////////////////////////
                        ////SOLO IN AMBITO DI SIMULAZIONI
                        //////////////////////////////////////////////////////////////////

                       System.err.println(this.us.firstname + " " + this.us.surname + " " + this.UserId);

                       ITucsonOperation op_rdp = acc.rdp(this.rbac, new LogicTuple("active_role", new Value(this.UserId), new Var("Y")), null);
                       role = op_rdp.getLogicTupleResult().getArg(1).getName();
                       //si verifica che il ruolo rivestito dall'utente abbia il diritto di accedere al locale in questione
                       if(verify_permissions(role, this.rm.name)){
                            //se l'utente possiede tutti i diritti, devono essere controllati 
                            //i vincoli ambientali relativi al locale e al ruolo.
                            //I vincoli relativi al ruolo e al locale vengono letti e controllati:
                            //se risultano non soddisfatti o non essercene la transizione fallisce
                            ITucsonOperation op_rdAll = acc.rdAll(this.rbac,
                                    new LogicTuple("room_rule", new Var("A"), new Value(this.rm.name),
                                    new Value(role), new Var("B"), new Var("C"), new Var("D"), new Var("E")), null);
                            
                            if(op_rdAll.isResultSuccess()){
                                List<LogicTuple> rulesList = op_rdAll.getLogicTupleListResult();
                                this.cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"),Locale.ITALY);
                                this.cal.setFirstDayOfWeek(Calendar.MONDAY);
                                LogicTuple rule;
                                boolean rule_respected = false;
                                for(int i=0;i<rulesList.size();i++){
                                    rule = rulesList.get(i);
                                    //SE LO STATO ATTUALE COINCIDE CON QUELLO ESPRESSO NEL VINCOLO
                                    if(validate_state(rule.getArg(3).getName())){
                                        //SE LA DATA ATTUALE COINCIDE CON QUELLA ESPRESSA NEL VINCOLO
                                        //se la data � indifferente viene controllato il range sui giorni della settimana
                                        //altrimenti la data � predominante sui giorni della settimana
                                        if((isDateIndifferent(rule.getArg(5).toList()) && validate_day(rule.getArg(6).toList(), this.cal)) ||
                                                validate_date(rule.getArg(5).toList(), this.cal)){
                                                  if(validate_hour(rule.getArg(4).toList(), this.cal)){
                                                      acc.out(this.myTid, new LogicTuple("detection", new Value("entry"),new Value(this.sens.idSens), new Value(this.UserId), new Value(this.rm.idRoom)), null);
                                                      rule_respected = true;
                                                      break;
                                                  }
                                        }else{
                                            System.out.println("Stato del giorno o dell'ora non compatibile con le condizioni attuali");
                                        }

                                    }else{
                                        System.out.println("Stato del vincolo non compatibile con lo stato attuale");
                                    }
                                }
                                if(!rule_respected){
                                    javax.swing.JOptionPane.showMessageDialog(null,"ACCESSO NEGATO: NON SONO STATI TROVATI VINCOLI AMBIENTALI VALIDI!",
                                    "",javax.swing.JOptionPane.WARNING_MESSAGE);
                                }

                            }else{
                                this.tp.appendText(this.myName()+"-"+this.rm.name+": non sono stati trovati vincoli validi per " +
                                        "l'ingresso del ruolo "+this.role);
                                //QUI CI DEVE ANDARE UNA FINESTRA DI AVVISO CHE NON SI HA IL DIRITTO DI ENTRARE NEL LOCALE
                                //PERCHE' NON SONO RISPETTATI I VINCOLI AMBIENTALI
                                javax.swing.JOptionPane.showMessageDialog(null,"ERRORE: NON SONO STATI RILEVATI VINCOLI AMBIENTALI PER IL LOCALE ASSOCIATI AL RUOLO!",
                          "",javax.swing.JOptionPane.ERROR_MESSAGE);
                            }
                        }else{
                             this.tp.appendText(this.myName()+"-"+this.rm.name+": ruolo "+this.role+" non autorizzato all'ingresso");
                             //QUI AVVISO CHE NON SI HA UN RUOLO CHE PERMETTE DI ENTRARE NEL LOCALE
                                javax.swing.JOptionPane.showMessageDialog(null,"IL RUOLO "+role+" NON E' ABILITATO ALL'INGRESSO NEL LOCALE",
                          "",javax.swing.JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                             //IL LOCALE E' AFFETTO DA PERMESSO E L'UTENTE E' UNO SCONOSCIUTO
                             javax.swing.JOptionPane.showMessageDialog(null,"RILEVATO SCONOSCIUTO!AVVERTO GLI ABITANTI DELLA CASA",
                          "",javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    this.tp.appendText(this.rm.name+" non richiede vincoli per l'ingresso.");
                   //E' STATO VERIFICATO CHE IL LOCALE NON E' TRA QUELLI PROTETTI QUINDI L'UTENTE TUTTAVIA VIENE SIMULATO
                   //UGUALMENTE IL RICONOSCIMENTO: SE L'UTENTE � UNO SCONOSCIUTO IL SISTEMA PROVVEDE AD INFORMARE GLI
                   //ABITANTI

                    //////////////////////////////////////////////////////////////////
                    ////SOLO IN AMBITO DI SIMULAZIONI
                    //////////////////////////////////////////////////////////////////
                    if(detectionReq.getArg(2).intValue()<0)
                        javax.swing.JOptionPane.showMessageDialog(null,"RILEVATO SCONOSCIUTO!AVVERTO GLI ABITANTI DELLA CASA",
                        "",javax.swing.JOptionPane.WARNING_MESSAGE);
                    else {
                        this.UserId = detectionReq.getArg(2).intValue();
                        acc.out(this.myTid,
                                new LogicTuple("detection", 
                                   new Value("entry"),
                                   new Value(this.sens.idSens),
                                   new Value(this.UserId),
                                   new Value(this.rm.idRoom)),
                                null);
                    }
                   /*lt = in(myTid, new LogicTuple("biometric_relief", new Value(aId.toString()),
                           new Var("X"), // person's id
                           new Var("Y"))); // score

                    if(lt.getArg(1).getName().equals("unknown")){
                     javax.swing.JOptionPane.showMessageDialog(null,"RILEVATO SCONOSCIUTO!AVVERTO GLI ABITANTI DELLA CASA",
                        "",javax.swing.JOptionPane.WARNING_MESSAGE);
                       }else{
                           this.UserId = retrive_user(lt.getArg(1).getName());
                           acc.out(this.myTid, new LogicTuple("detection", 
                                   new Value("entry"),
                                   new Value(this.sens.idSens),
                                   new Value(this.UserId),
                                   new Value(this.rm.idRoom)),
                                   null);
                       }*/
               }
            }
        }catch(Exception ex){
            tp.appendText("-- ROOM ACCESS MANAGER "+sens.name+" error in processing the request.");
            ex.printStackTrace();
        }
    }

    private boolean isDateIndifferent(List date) {
         String fDate = date.get(0).toString();
        String tDate = date.get(1).toString();
        if(fDate.equals("'_'") && tDate.equals("'_'")){
            return true;
    }
        else return false;
    }

    /*private int retrive_user(String username) {
        int id = 0;
        try {
            try {
                this.us = UserService.getUser(DbService.getDatabase(), username);
                if(this.us!=null)
                    id = this.us.idUser;
                else
                    id = -1;
            } catch (ResultSetDbException ex) {
                Logger.getLogger(RoomAccessManAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NotFoundDbException ex) {
            Logger.getLogger(RoomAccessManAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        return id;
    }*/
    
    private void retrive_user(int userId) {
        try {
	    Database d = sf.getDatabaseInterface().getDatabase();
	    this.us = sf.getUserServiceInterface().getUserById(ds.getDatabase(), userId);
        } catch (Exception ex) {
            Logger.getLogger(RoomAccessManAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean validate_date(List date, Calendar cal) {
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
                return true;
            }
            else
        return false;
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

    private boolean validate_hour(List date, Calendar cal) {
     
        String fHour = date.get(0).toString().replace("'", "");

        String tHour = date.get(1).toString().replace("'", "");
           if(fHour.equals("_") && tHour.equals("_")){
            return true;
        }
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

    private boolean validate_state(String name) {
        boolean result = false;
        try{
            ITucsonOperation op_rdp = acc.rdp(this.myTid, new LogicTuple("state", new Var("X")), null);
            LogicTuple state = op_rdp.getLogicTupleResult();
            
            if(name.equals(state.getArg(0).getName()) || name.equals("_"))
                result = true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    private boolean verify_permissions(String role, String room_name) {
        ITucsonOperation op_rdp = null;
        try {
            op_rdp = acc.rdp(this.rbac, new LogicTuple("ass_roles_perm", new Value(role), new Value(room_name)), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return op_rdp.isResultSuccess();
    }

    private boolean verify_room_permission(String room_name) {
        ITucsonOperation op_rdp = null;
        try {
            op_rdp  = acc.rdp(this.rbac, new LogicTuple("permission", new Value(room_name)), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //if(res!=null) return true;
        //else return false;
        return op_rdp.isResultSuccess();
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

}
