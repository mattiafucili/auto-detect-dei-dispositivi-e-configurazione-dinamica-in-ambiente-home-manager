package it.unibo.homemanager.tablemap;
/*
 * User.java
 *
 * Created on 4 giugno 2004, 18.31
 * 
 */


/**
 *
 * @author  Claudia Fontan
 *
 *
 *  Classe che fornisce le necessarie
 *  funzionalit� di gestione di creazione, cancellazione,
 *  aggiornamento degli utenti
 */



public class User {

  public int idUser, temp_heat, temp_ac;
  public String firstname, surname, username, pwd, activate;
  public String role;

  
  public User(int id, String fn, String sn, String un, String p, String r,
          int th, int tac, String act) {                  

    idUser = id;
    firstname = fn;
    surname = sn;
    username = un;
    pwd = p;
    role = r;
    temp_heat = th;
    temp_ac = tac;
    activate = act;

  }
  
  public String getRoleString() {
      return this.role;
  }
  
  public char getRoleChar() {
      return this.role.charAt(1);
  }
  
  public String toString() {
      return "Firstname: "+this.firstname+"\n"+
              "Surname: "+this.surname+"\n"+
              "Role: "+(role.charAt(1))+"\n"+
              "Desired air conditioner temperature: "+this.temp_ac+" °C\n"+
              "Desired heater temperature: "+this.temp_heat+" °C\n"+
              "Desired Users to activate: "+(activate)+"\n";
  }
 
}
