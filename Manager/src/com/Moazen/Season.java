/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Moazen;


public class Season {
    public enum Type {
	    Winter,
	    Summer
	}

	Season(){this.season=Type.Winter;}
        Season(Type T){this.season=T;}
	public void setSeason(Type season){
            this.season=season;
        }
	public void setSeason(String season){
            if(season.equals("Winter"))
                    this.season=Type.Winter;
            else
                    this.season=Type.Summer;

        }

	public final Type type(){return seasonInt() ;}
	public final  Type seasonInt(){return season;}
	public final String seasonString(){
            String tmp;
            if(season.equals(Type.Winter))
                tmp="Winter";
            else
                tmp = "Summer";
            return tmp;
        }
	private Type season;

}

