/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.homemanager.agents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.swing.JPanel;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.userinterfaces.TracePanel;
import it.unibo.homemanager.userinterfaces.ViewManageAgentPanel;
import it.unibo.homemanager.userinterfaces.ViewShopperAgentPanel;
import it.unibo.homemanager.util.MailUtility;

/**
 *
 * @author s0000590338
 */
public class TucsonShopperAgent extends AbstractTucsonAgent implements TucsonAgentInterface {

	private String name; 
    private ViewShopperAgentPanel viewshopperPanel;
    private TucsonTupleCentreId fridge_tc;
    private TracePanel tp;
    private EnhancedSynchACC acc;
    private ArrayList<String> listProduct;
    private boolean there_are_buy = false;
    
    public TucsonShopperAgent(String id,TracePanel tp, TucsonTupleCentreId fridge_tc, Database database) throws TucsonInvalidAgentIdException {
        super(id);
        this.name = id;
        this.tp = tp;
        this.fridge_tc = fridge_tc;
        
    }

    public void setPanel(ViewManageAgentPanel maPanel)
    {
    	this.viewshopperPanel=new ViewShopperAgentPanel(maPanel, this, tp);
    }
    
    public void show() {
        viewshopperPanel.init();
    }

    public void hide() {
        this.viewshopperPanel.hidePanel();
    }

    public JPanel getInterface() {
        return this.viewshopperPanel;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void operationCompleted(AbstractTupleCentreOperation atco) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void operationCompleted(ITucsonOperation ito) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void main() {
        tp.appendText("-- SHOPPER AGENT STARTED.");
        this.acc = this.getContext();
        //Riempio la lista dei prodotti
        try {
            getList();
        } catch (Exception ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while (true) {
                there_are_buy = checkBuyTuple();
                Thread.sleep(1000*2);
                if (there_are_buy == true) {
                    tp.appendText("trovato ordine da fare");
                    String list = inpTupleBuy();
                    Thread.sleep(1000*2);
                    sendMail(getStringToBuyMail(list));
                    tp.appendText("Was generated a purchase order");
                    insertOrderTuple(list);
                }
                Thread.sleep(1000 * 60 * 2);
                getList();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            tp.appendText("TucsonAgent ShoppeAgent: error in device activity.");
        }
    }

    @SuppressWarnings("unused")
	private String createElementByTupleString(String s) {
        String s1 = s.substring(s.indexOf("(") + 1, s.length() - 1);
        StringTokenizer t = new StringTokenizer(s1, ",");
        if (t.countTokens() == 5) {
            int idUser = Integer.parseInt(t.nextToken());
            String nameProduct = (t.nextToken());
            int quantityActual = Integer.parseInt(t.nextToken());
            int quantityPref = Integer.parseInt(t.nextToken());
            String[] dateString = t.nextToken().split("\\_");
            //year-month-day
            String[] ymd = dateString[0].split("\\-");
            String yearString = ymd[0].substring(1, ymd[0].length());
            int year = Integer.parseInt(yearString);
            int month = Integer.parseInt(ymd[1]);
            int day = Integer.parseInt(ymd[2]);
            //hour.second.milliSecond
            String[] hsms = dateString[1].split("\\.");
            int hour = Integer.parseInt(hsms[0]);
            int second = Integer.parseInt(hsms[1]);
            String milliSecondString = hsms[2].substring(0, hsms[2].length() - 1);
            int millisecond = Integer.parseInt(milliSecondString);

            int quantity = quantityPref - quantityActual + 1;
            return nameProduct + "-" + quantity;
        }
        return "";
    }

    public void getList() throws InvalidVarNameException, Exception {
        listProduct = new ArrayList<String>();
        List<LogicTuple> l = null;
        String name_template = "scarcity";
        LogicTuple template = new LogicTuple(name_template, new Var("X"), new Var("Y"), new Var("A"), new Var("B"), new Var("C"));
        ITucsonOperation op_rdAll = this.acc.rdAll(fridge_tc, template, Long.MAX_VALUE);
        if (op_rdAll.isResultSuccess()) {
            l = op_rdAll.getLogicTupleListResult();
            for (int i = 0; i < l.size(); i++) {
                String product = l.get(i).toString();
                listProduct.add(createElementByTupleString(product));
            }
        } else {
            System.err.println("[readCentreArray(TA)@TucsonFridge] rdAll(" + template + ") failed!");
        }
        viewshopperPanel.fillList();
    }

    private void sendMail(String list) {

        String host ="smtp.gmail.com";
        String us = "harmanymancini@gmail.com";
        String pass = "";

        String mitt = "harmanymancini@gmail.com";
        String[] toAddr = new String[2];/*new String[3];*/
        toAddr[0] = "harmanymancini@gmail.com";
        toAddr[1]="carminemancini@libero.it";
        //toAddr[2] = "roberta.calegari@unibo.it";
        String oggetto = "Shopping list";
        try {
            MailUtility.sendMail(host, us, pass, toAddr, mitt, oggetto, list);
        } catch (MessagingException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void insert(int threshold) {
        Value value_threshold = new Value(threshold);
        Value id_user = new Value(0);
        LogicTuple template = new LogicTuple("threshold", id_user, value_threshold);
        try {
            LogicTuple old_template=new LogicTuple("threshold",id_user,new Var("X"));
            @SuppressWarnings("unused")
			ITucsonOperation op_inp = this.acc.inp(fridge_tc, old_template, Long.MAX_VALUE);
            
            @SuppressWarnings("unused")
			ITucsonOperation op_out = this.acc.out(fridge_tc, template, Long.MAX_VALUE);
        } catch (TucsonOperationNotPossibleException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnreachableNodeException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OperationTimeOutException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidVarNameException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getThreshold() {
        String name_template = "threshold";
        LogicTuple template;
        int threshold = 0;
        try {
            template = new LogicTuple(name_template, new Value(0), new Var("Y"));
            ITucsonOperation op_rd = this.acc.rd(fridge_tc, template, Long.MAX_VALUE);
            if (op_rd.isResultSuccess()) {
                LogicTuple l = op_rd.getLogicTupleResult();
                threshold = getThresholdValueByString(l.toString());
            }
        } catch (InvalidVarNameException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TucsonOperationNotPossibleException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnreachableNodeException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OperationTimeOutException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return threshold;
    }

    private int getThresholdValueByString(String s) {
        String s1 = s.substring(s.indexOf("(") + 1, s.length() - 1);
        StringTokenizer t = new StringTokenizer(s1, ",");
        int threshold = 0;
        if (t.countTokens() == 2) {
            @SuppressWarnings("unused")
			int idUser = Integer.parseInt(t.nextToken());
            threshold = Integer.parseInt(t.nextToken());
        }
        return threshold;
    }

    private boolean checkBuyTuple() {
        String name_template = "buy";
        LogicTuple template;
        try {
            template = new LogicTuple(name_template, new Var("X"), new Var("Y"), new Var("K"));
            ITucsonOperation op_rd = this.acc.rd(fridge_tc, template, Long.MAX_VALUE);
            LogicTuple t=op_rd.getLogicTupleResult();
            if (t!=null) {
                return true;
            } else {
                return false;
            }

        } catch (InvalidVarNameException ex) {
            return false;
        } catch (TucsonOperationNotPossibleException ex) {
            return false;
        } catch (UnreachableNodeException ex) {
            return false;
        } catch (OperationTimeOutException ex) {
            return false;
        }
    }

    private void insertOrderTuple(String list) {
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd'_'hh.mm.ss");
        Value temp = new Value(df.format(d));
        Value buy = new Value(list);
        int num_order = getNumOrder()+1;
        Value numOrder = new Value(num_order);
        @SuppressWarnings("unused")
		LogicTuple template = new LogicTuple("order_buy", numOrder, buy, temp);
        /*
        try {
            ITucsonOperation op_rd = this.acc.out(fridge_tc, template, Long.MAX_VALUE);
        } catch (TucsonOperationNotPossibleException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnreachableNodeException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OperationTimeOutException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }

   
    public ArrayList<String> getListProduct() {
        return this.listProduct;
    }

    private String getStringOrderByTuple(String tuple_order) {
        String result = "";
        String s1 = tuple_order.substring(tuple_order.indexOf("(") + 1, tuple_order.length() - 1);
        StringTokenizer t = new StringTokenizer(s1, ",");
        if (t.countTokens() == 3) {
            String numOrder = t.nextToken();
            String list = t.nextToken();
            String date=t.nextToken();
            result=numOrder+" "+list+" "+date;
        }
        result = result.replaceAll("'", "");
        return result;
    }

    //Non utilizzata
    private String inpTupleBuy() {
        String list = "";
        String name_template = "buy";
        try {
            LogicTuple template = new LogicTuple(name_template, new Var("X"), new Var("Y"), new Var("K"));
            ITucsonOperation op_inp = this.acc.inp(fridge_tc, template, Long.MAX_VALUE);
            if (op_inp.isResultSuccess()) {
                LogicTuple tuple=op_inp.getLogicTupleResult();
                list = getStringListBuyByTuple(tuple.toString());
                System.out.println("Richiesta inp effettuata con successo.");
            } else {
                System.err.println("[readCentreArray(TA)@TucsonFridge] inp failed!");
            }
        } catch (InvalidVarNameException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TucsonOperationNotPossibleException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnreachableNodeException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OperationTimeOutException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    private int getNumOrder() {
        listProduct = new ArrayList<String>();
        List<LogicTuple> l = null;
        String name_template = "order_buy";
        LogicTuple template;
        try {
            template = new LogicTuple(name_template, new Var("X"), new Var("Y"), new Var("J"));
            ITucsonOperation op_rdAll = this.acc.rdAll(fridge_tc, template, Long.MAX_VALUE);
            if (op_rdAll.isResultSuccess()) {
                l = op_rdAll.getLogicTupleListResult();
                return l.size();
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public ArrayList<String> getListOrderToString(){
        ArrayList<String> orders=new ArrayList<String>();
        List<LogicTuple> l = null;
        String name_template = "order_buy";
        try {
            LogicTuple template = new LogicTuple(name_template, new Var("X"), new Var("Y"), new Var("A"));
            ITucsonOperation op_rdAll = this.acc.rdAll(fridge_tc, template, Long.MAX_VALUE);
            if (op_rdAll.isResultSuccess()) {
                l = op_rdAll.getLogicTupleListResult();
                for (int i = 0; i < l.size(); i++) {
                    String order = l.get(i).toString();
                    orders.add(getStringOrderByTuple(order));
                }
            } else {
                System.err.println("[readCentreArray(TA)@TucsonFridge] rdAll(" + template + ") failed!");
            }
        } catch (InvalidVarNameException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnreachableNodeException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OperationTimeOutException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TucsonOperationNotPossibleException ex) {
            Logger.getLogger(TucsonShopperAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orders;
    }

    private String getStringListBuyByTuple(String s) {
    String result = "";
        String s1 = s.substring(s.indexOf("(") + 1, s.length() - 1);
        StringTokenizer t = new StringTokenizer(s1, ",");
        if (t.countTokens() == 3) {
            String list = t.nextToken();
            result=list;
        }
        result = result.replaceAll("'", "");
        return result;  
    }
    
    public String getStringToBuyMail(String s){
        String result="";
        String [] s1=s.split(";");
        for(String s2: s1){
            result+=s2+"\n";
        }
        return result;
           
    }
    
}
