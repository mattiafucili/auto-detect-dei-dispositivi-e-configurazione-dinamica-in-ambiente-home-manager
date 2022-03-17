package it.unibo.homemanager.tablemap.TucsonService;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import it.unibo.homemanager.dbmanagement.Database;
import it.unibo.homemanager.dbmanagement.TucsonDatabase;
import it.unibo.homemanager.dbmanagement.dbexceptions.DuplicatedRecordDbException;
import it.unibo.homemanager.dbmanagement.dbexceptions.ResultSetDbException;
import it.unibo.homemanager.detection.Device;
import it.unibo.homemanager.tablemap.Conversion;
import it.unibo.homemanager.tablemap.ServicesInterfaces.DeviceServiceInterface;

public class TucsonDeviceService implements DeviceServiceInterface {

    private String nome_template;

    public TucsonDeviceService() {
        nome_template="device";
    }

    public Device getDevice(Database database, String name, int roomId) throws Exception {
        Device dev = null;
        TucsonDatabase db= (TucsonDatabase)database;

        /*creazione del template corretto da cercare*/
        Value nome= new Value(name);
        Value ri= new Value(roomId);
        LogicTuple template= new LogicTuple(nome_template,new Var("X"),nome,new Var("Y"),new Var("Z"),new Var("A"),ri);

        LogicTuple lt=db.read(template);
        dev=this.createDevicebyLogicTuple(lt);
        return dev;
    }

    public Device getDeviceById(Database database, int id) throws Exception {
        Device dev = null;
        TucsonDatabase db = (TucsonDatabase)database;
        Value ri= new Value(id);
        LogicTuple template= new LogicTuple(nome_template,ri,new Var("X"),new Var("Y"),new Var("Z"),new Var("A"),new Var("B"));
	System.err.println("[getDeviceById@TucsonDeviceService] template: " + template);

        LogicTuple lt=db.read(template);
	System.err.println("[getDeviceById@TucsonDeviceService] lt: " + lt);
        dev=this.createDevicebyLogicTuple(lt);

        return dev;
    }

    public Device insertDevice(Database database, String n, float e, String p, String t, int ri) throws Exception{
        Device dev;

        dev = new Device(0,n,e,t,p,ri);
			
        boolean exist = false;
        TucsonDatabase db= (TucsonDatabase)database;
	/* controllo che il dispositivo non sia gi� presente (nome e stanza) */
	Value nome=new Value(dev.getDeviceName());
	Value roomi= new Value(dev.getDeviceRoomId());
	LogicTuple template;
	try {
            template = new LogicTuple(nome_template,new Var("X"),nome,new Var("Y"),new Var("Z"),new Var("A"),roomi);
            if(db.read(template)!=null)
                exist=true;

            if (exist)
                throw new DuplicatedRecordDbException("Device: insert(): Tentativo di inserimento "+
						"di un dispositivo gi� presente.");

            /*inserimento del dispositivo*/
            Value newId=new Value(this.calcId(db)+1);
            if((this.calcId(db)+1)==0)
                throw new Exception("Problema calcolo id");

            Value energy= new Value(dev.getDeviceEnergy());
            Value c= new Value((dev.getDeviceType()));
            Value par= new Value((dev.getDeviceParameters()));
	    dev.setDeviceId(newId.intValue());
            LogicTuple lt= new LogicTuple(nome_template,newId,nome,energy,par,c,roomi);
            db.insert(lt);
	} catch (Exception e1) {
            e1.printStackTrace();
        }
	return dev;
    }

    public Device updateDevice(Database database,Device dev) {

        try{  
            TucsonDatabase db= (TucsonDatabase)database;
            Value oi= new Value(dev.getDeviceId()); 
            LogicTuple template = new LogicTuple(nome_template,oi,new Var("X"),new Var("Y"),new Var("Z"),new Var("A"),new Var("B"));
            if (db.read(template)==null)
                throw new ResultSetDbException("Device: update(): Tentativo di aggiornare "+
								"un dispositivo non esistente.");
		db.delete(template);
		Value e= new Value(dev.getDeviceEnergy());
		Value c= new Value(dev.getDeviceType());
		Value p= new Value(dev.getDeviceParameters());
		Value n=new Value(dev.getDeviceName());
		Value ri= new Value(dev.getDeviceRoomId());
		LogicTuple lt= new LogicTuple(nome_template,oi,n,e,p,c,ri); 
		db.insert(lt);
	} catch (Exception e) {
            e.printStackTrace();
        }
	
        return dev;
    }

    public Device deleteDevice(Database database,int id) throws Exception{
        Device dev;
        dev = this.getDeviceById(database, id);
        TucsonDatabase db = (TucsonDatabase)database;
			
	try {
            if(dev.getDeviceName()==null)
                throw new ResultSetDbException("Device: delete(): Tentativo di eliminazione "+
							"di un dispositivo non esistente.");
				
		Value newId=new Value(dev.getDeviceId());
		Value e= new Value(dev.getDeviceEnergy());
		Value c= new Value(dev.getDeviceType());
		Value p= new Value(dev.getDeviceParameters());
		Value n=new Value(dev.getDeviceName());
		Value ri= new Value(dev.getDeviceRoomId());
		LogicTuple template= new LogicTuple(nome_template,newId,n,e,p,c,ri); 
		db.delete(template);
	} catch (Exception e1) {
            e1.printStackTrace();
        }

        return dev;
    }

    /* consente di visualizzare tutti i dispositivi presenti */
    @SuppressWarnings({ "rawtypes"})
    public ArrayList<Device> getDevices(Database database) throws Exception {
        Device dev;
        TucsonDatabase db= (TucsonDatabase)database;
	ArrayList<Device> Devices = new ArrayList<Device>();    
	Value template= new Value(nome_template,new Var("X"),new Var("Y"),new Var("Z"),new Var("A"),new Var("B"),new Var("C"));
	List l=db.readCentre(template);

	if(l.isEmpty())
            return Devices;

	for(int i=0;i<l.size();i++)
	{
	    dev=this.createDevicebyString(l.get(i).toString());
	    Devices.add(dev);
	}

	return Devices;
    }

    /* consente di visualizzare tutti i dispositivi presenti in una stanza */
    public ArrayList<Device> getDevicesInRoom(Database database,int room) throws Exception {
	Device dev;
	ArrayList<Device> Devices = new ArrayList<Device>(); 
	TucsonDatabase db= (TucsonDatabase)database;
	Value ri= new Value(room);
	Value template= new Value(nome_template,new Var("X"),new Var("Y"),new Var("Z"),new Var("A"),new Var("B"),ri);
	@SuppressWarnings("rawtypes")
	List l=db.readCentre(template);

	if(l.isEmpty())
	    return Devices;

	for(int i=0;i<l.size();i++)
	{
	    dev=this.createDevicebyString(l.get(i).toString());
	    Devices.add(dev);
	}
	
	return Devices;
    }

    /*	AGGIUNTA METODO PER PASSARE IL NUMERO
	NB: si suppone che i nomi siano significativi se ci sono pi�
	dispositivi con lo stesso nome esso deve contenere anche 1,2...
    */
    public int getDeviceId(Database database, String cerca) throws Exception
    {
	Device dev = null;
	TucsonDatabase db= (TucsonDatabase)database;
	Value ri= new Value(cerca);
	LogicTuple template= new LogicTuple(nome_template,new Var("X"),ri,new Var("Y"),new Var("Z"),new Var("A"),new Var("B"));
	LogicTuple lt=db.read(template);
	dev=this.createDevicebyLogicTuple(lt);

	return dev.getDeviceId();
    }
		

    private int calcId(TucsonDatabase db) {   
	try {
	    int id=0;
	    Database database=db.getDatabase();
	    ArrayList<Device> v= this.getDevices(database);
	    for(int i=0;i<v.size();i++)
	    //{ 
		if(((Device)v.get(i)).getDeviceId()>id)
		    id=((Device)v.get(i)).getDeviceId();
	    //}
	    return id;
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	return -1;
    }
		
    /* Costruttore che da una stringa completa letta dal centro di tuple 
     * con read_all 
     * ricavi i singoli parametri per creare un device
     */
    private Device createDevicebyString(String s) {
	String s1=s.substring(s.indexOf("(")+1,s.length()-1);
	StringTokenizer t= new StringTokenizer(s1,",");
	Device dev=null;
	if(t.countTokens()==6) {
	    int idDev= Integer.parseInt(t.nextToken());
	    String name= Conversion.getDatabaseString(t.nextToken());
	    float energy= Float.parseFloat(t.nextToken());
	    String param= Conversion.getDatabaseString(t.nextToken());
	    String type= Conversion.getDatabaseString((t.nextToken()));
	    int roomId=Integer.parseInt(t.nextToken());
	    dev= new Device(idDev,name,energy,type,param,roomId);
	}
			
	return dev;
    }
		

    private Device createDevicebyLogicTuple(LogicTuple r) {
	Device dev=null;
	try {
	    int idDev = r.getArg(0).intValue();
	    String name = Conversion.getDatabaseString(r.getArg(1).toString());
	    float energy = r.getArg(2).floatValue();
	    String param = Conversion.getDatabaseString(r.getArg(3).toString());
	    String type =Conversion.getDatabaseString (r.getArg(4).toString());
	    int roomId = r.getArg(5).intValue();
	    dev= new Device(idDev,name,energy,type,param,roomId);
				
	} catch (Exception sqle) {}
	return dev;
    }
}
