/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shefra.prayertimes.moazen;
/**
 *
 * @author aziz
 */ 
public class Mazhab {
        public enum Type {
	    Default,
	    Hanafi
	}

	Mazhab(){this.mazhab=Type.Default;}
        Mazhab(Type T){this.mazhab=T;}
	public void setMazhab(Type mazhab){
            this.mazhab=mazhab;
        } 
	public void setMazhab(String mazhab){
            if(mazhab.equals("Default"))
                    this.mazhab=Type.Default;
            else
                    this.mazhab=Type.Hanafi;

        }

	public final Type type(){return mazhabInt() ;}
	public final  Type mazhabInt(){return mazhab;}
	public final String mazhabString(){
            String tmp;
            if(mazhab.equals(Type.Default))
                tmp="Default";
            else
                tmp = "Hanafi";
            return tmp;
        }
	private Type mazhab;

}

