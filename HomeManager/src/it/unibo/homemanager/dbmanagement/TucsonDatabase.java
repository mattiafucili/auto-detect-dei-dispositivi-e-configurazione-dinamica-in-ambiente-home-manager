package it.unibo.homemanager.dbmanagement;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import it.unibo.homemanager.dbmanagement.dbexceptions.NotFoundDbException;

import java.util.ArrayList;
import java.util.List;

public class TucsonDatabase extends Database{
	
    private TucsonTupleCentreId tc;
    //private TucsonContext context; db rbac_tc
    private TucsonAgentId agent;
    private EnhancedSynchACC acc;

    public TucsonDatabase() throws Exception {
    try {
	System.err.println("TucsonDatabase constructor called..");
		//RC: replaced database_tc with db
		System.out.println("Creo db_tc");
	    this.tc = new TucsonTupleCentreId("db", "localhost", "20504");
	    //this.context=Tucson.enterDefaultContext();
	    agent = new TucsonAgentId("tucsonDBAgent");
	} catch (Exception e) {
	    throw new Exception("DB: Impossibile connettersi al centro di tuple: " + e);
	}
    }
	
    public TucsonTupleCentreId getTucsonTupleCentreId()
    {
    	return this.tc;
    }
    
    //@SuppressWarnings("rawtypes")
    public List readCentreArray(TupleArgument template) throws Exception {
	List l = null;
	try {
		//LogicTuple lt = this.context.inp(this.tc, new LogicTuple("rd_all",template,new Var("Y")));
		//l = lt.getArg(1).toList();
		//return l;
	    Thread.sleep(250);
	    this.acc = TucsonMetaACC.getContext(agent);
	    LogicTuple ltTemplate = LogicTuple.parse(template.toString());
	    System.err.println("[readCentreArray(TA)@TucsonDatabase] ltTemplate: " + ltTemplate);
	    ITucsonOperation op_rdAll = this.acc.rdAll(tc, ltTemplate, Long.MAX_VALUE);
	    if(op_rdAll.isResultSuccess()) {
		l = op_rdAll.getLogicTupleListResult();
		System.err.println("[readCentreArray(TA)@TucsonDatabase] Successfully read " + l);
	    } else
		System.err.println("[readCentreArray(TA)@TucsonDatabase] rdAll(" + ltTemplate + ") failed!");
	    
	    this.acc.exit();
	    Thread.sleep(250);
	} catch (Exception ex) {
	    throw new Exception("Non � possbile leggere tutti i valori degli array. Eccezione: "+ex+ "\n ");
	}
	return l;
    }
    //@SuppressWarnings("rawtypes")
    public List readCentreArray(Value template) throws NotFoundDbException {
	List l = null;
	try {
			
			//LogicTuple lt = this.context.inp(this.tc, new LogicTuple("rd_all",template,new Var("Y")));
			//List l = lt.getArg(1).toList();
			//return l;
	    Thread.sleep(250);
	    this.acc = TucsonMetaACC.getContext(agent);
	    LogicTuple ltTemplate = LogicTuple.parse(template.toString());
	    System.err.println("[readCentreArray(V)@TucsonDatabase] ltTemplate: " + ltTemplate);
	    ITucsonOperation op_rdAll = this.acc.rdAll(tc, ltTemplate, Long.MAX_VALUE);
	    if(op_rdAll.isResultSuccess()) {
		l = op_rdAll.getLogicTupleListResult();
		System.err.println("[readCentreArray(V)@TucsonDatabase] Successfully read " + l);
	    } else
		System.err.println("[readCentreArray(V)@TucsonDatabase] rdAll(" + ltTemplate + ") failed!");
	    
	    this.acc.exit();
	    Thread.sleep(250);
	} catch (Exception ex) {
	    throw new NotFoundDbException("Non � possbile leggere tutti i valori. Eccezione: "+ex+ "\n ");
	}
	return l;
    }
	
    /*LETTURA DI TUTTI I GLI ELEMENTI DI UN CERTO TIPO*/
    //@SuppressWarnings("rawtypes")
    public List readCentre(Value template) throws Exception {
        List l = new ArrayList();
        try {
		//LogicTuple lt = this.context.inp(this.tc, new LogicTuple("rd_all",template,new Var("Y")));
		//List l = lt.getArg(1).toList();
		//return l;
	    Thread.sleep(250);
	    this.acc = TucsonMetaACC.getContext(agent);
	    LogicTuple ltTemplate = LogicTuple.parse(template.toString());
	    System.err.println("[readCentre@TucsonDatabase] ltTemplate: " + ltTemplate);
	    System.out.println("faccio una read all");
	    ITucsonOperation op_rdAll = this.acc.rdAll(tc, ltTemplate, Long.MAX_VALUE);
	    System.out.println("fatta read all");
	    if(op_rdAll.isResultSuccess()) {
	    	//l = op_rdAll.getLogicTupleListResult();
	    	System.out.println("read all SUCCESS");
	    	l.addAll(op_rdAll.getLogicTupleListResult());
	    	System.err.println("[readCentre@TucsonDatabase] Successfully read " + l);
	    } else
		System.err.println("[readCentre@TucsonDatabase] rdAll(" + ltTemplate + ") failed!");
	    this.acc.exit();
	    Thread.sleep(250);
	} catch (Exception ex) {
	    //throw new NotFoundDbException("Non è possbile leggere tutti i valori. Eccezione: "+ex+ "\n ");
	    ex.printStackTrace();
	}
	
        return l;
    }

    /*LETTURA SINGOLO ELEMENTO COMPATIBILE*/
    public LogicTuple read(LogicTuple template) throws Exception {
	LogicTuple res = null;
	try {
			//return context.rdp(tc, template);
	    System.err.println("[read@TucsonDatabase] template: " + template);
	    Thread.sleep(250);
	    this.acc = TucsonMetaACC.getContext(agent);
	    ITucsonOperation op_rdp = this.acc.rdp(tc, template, Long.MAX_VALUE);
	    if(op_rdp.isResultSuccess()) {
		res = op_rdp.getLogicTupleResult();
		System.err.println("[read@TucsonDatabase] Successfully read " + res);
	    } else
		System.err.println("[read@TucsonDatabase] rdp("+ template + ") failed!");
	    
	    this.acc.exit();
	    Thread.sleep(250);
	} catch(Exception ex) {
	    throw new NotFoundDbException("Impossibile leggere la tupla. Eccezione: "+ex+ "\n ");
	}
	return res;
    }


    /*INSERIMENTO ELEMENTO*/
    public void insert(LogicTuple template) throws Exception
    {
	try{
			//context.out(tc,template);
	    Thread.sleep(250);
	    this.acc = TucsonMetaACC.getContext(agent);
	    this.acc.out(tc, template, null);
	    this.acc.exit();
	    Thread.sleep(250);
	} catch(Exception e) {
	    throw new NotFoundDbException("Impossibile inserire i valori: Eccezione "+e+ "\n ");
	}
    }

    /*CANCELLAZIONE ELEMENTO*/
    public void delete(LogicTuple old) throws Exception {
	try{
			//context.inp(tc,old);
	    Thread.sleep(250);
	    this.acc = TucsonMetaACC.getContext(agent);
	    this.acc.inp(tc, old, Long.MAX_VALUE);
	    this.acc.exit();
	    Thread.sleep(250);
	} catch(Exception e) {
	    throw new NotFoundDbException("Impossibile modificare i valori: Eccezione "+e+ "\n ");
	}
    }

    @Override
    public Database getDatabase() {	
	return this;
    }
}
