/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shefra.prayertimes.moazen;
/**
 *
 * @author aziz
 */ 
public class Calender {
        public  enum  Type {
	    UmmAlQuraUniv,
	    EgytionGeneralAuthorityofSurvey,
	    UnivOfIslamicScincesKarachi,
	    IslamicSocietyOfNorthAmerica,
	    MuslimWorldLeague
	}
        private Type calender ;
	public Calender(){this.calender=Type.UmmAlQuraUniv;}//default constructor
        public Calender(Type T){ this.calender= T;}//one-argument constructor

	public void setCalender(Type T){this.calender=T;}
	public void setCalender(String string){

            if (string.equals("UmmAlQuraUniv"))
                calender=Type.UmmAlQuraUniv;
 
            else if (string.equals("EgytionGeneralAuthorityofSurvey"))
                calender=Type.EgytionGeneralAuthorityofSurvey;

            else if (string.equals("UnivOfIslamicScincesKarachi"))
                calender=Type.UnivOfIslamicScincesKarachi;

            else if (string.equals("IslamicSocietyOfNorthAmerica"))
                calender=Type.IslamicSocietyOfNorthAmerica;

            else
                calender=Type.MuslimWorldLeague;

        }

	public final Type type (){return this.calenderInt();}
	public final Type calenderInt(){return this.calender;}
	public final String calenderString(){
            String tmp;
            if( calender == Type.UmmAlQuraUniv )
                tmp="UmmAlQuraUniv";

            else if( calender == Type.EgytionGeneralAuthorityofSurvey )
                tmp="EgytionGeneralAuthorityofSurvey";

            else if( calender == Type.UnivOfIslamicScincesKarachi )
                tmp="UnivOfIslamicScincesKarachi";

            else if( calender == Type.IslamicSocietyOfNorthAmerica )
                tmp="IslamicSocietyOfNorthAmerica";

            else
                tmp="MuslimWorldLeague";

            return tmp;
            }
        }

