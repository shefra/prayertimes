/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
package com.shefra.prayertimes.moazen;

public final class PrayerTime {

	// Shared variable used in Calculation Algorithm
	protected static double DegToRad = 0.017453292;
	protected static double RadToDeg = 57.29577951;

	protected Coordinate m_coordinate;
	protected Date m_date;
	protected Calender m_calender;
	protected Mazhab m_mazhab;
	protected Season m_season;
  
	protected double fajr;
	protected double shrouk;
	protected double zuhr;
	protected double asr;
	protected double maghrib; 
	protected double isha;
        protected double dec; 

	public PrayerTime(){
            this.m_coordinate = new Coordinate(0,0,0);
            this.m_date =new Date(0,0,0);
        }
        public PrayerTime(Coordinate co, Date da, Calender ca, Mazhab ma, Season se){
            this.m_coordinate = co ;
            this.m_date = da;
            this.m_calender = ca;
            this.m_mazhab = ma;
            this.m_season = se;
        }
	public PrayerTime(double lot,double lat,int zone,int day,int month,int year, Calender ca, Mazhab ma, Season se){
            this.m_coordinate = new Coordinate(lot,lat,zone);
            this.m_date = new Date(day,month,year);
            this.m_calender = ca;
            this.m_mazhab = ma;
            this.m_season = se;
        
        }
	public PrayerTime(double lot,double lat,int zone,int day,int month,int year, String calender, String mazhab, String season){
            this.m_coordinate = new Coordinate(lot,lat,zone);
            this.m_date = new Date(day,month,year);
            setCalender(calender);
            setMazhab(mazhab);
            setSeason(season);
        }
	public PrayerTime(double lot,double lat,int zone,int day,int month,int year){
            m_coordinate = new Coordinate(lot,lat,zone);
            m_date = new Date(day,month,year);
            m_calender = new Calender();
            m_mazhab = new Mazhab();
            m_season = new Season();
        }

	// Calculate Prayer times.
	// Must use firstly.
	public void calculate(){
            int year = m_date.year;
            int month = m_date.month;
            int day = m_date.day;

            double longitude = m_coordinate.longitude;
            double latitude = m_coordinate.latitude;
            int zone = m_coordinate.zone;

            double julianDay=(367*year)-(int)(((year+(int)((month+9)/12))*7)/4)+(int)(275*month/9)+day-730531.5;


            double sunLength=280.461+0.9856474*julianDay;
            sunLength=removeDublication(sunLength);


            double middleSun=357.528+0.9856003*julianDay;
            middleSun=removeDublication(middleSun);
            double lambda=sunLength+1.915*Math.sin(middleSun*DegToRad)+0.02*Math.sin(2*middleSun*DegToRad);
            lambda=removeDublication(lambda);

            double obliquity=23.439-0.0000004*julianDay;
            double alpha=RadToDeg*Math.atan(Math.cos(obliquity*DegToRad)*Math.tan(lambda*DegToRad));

            if (lambda > 90 && lambda < 180)
                alpha+=180;

            else if (lambda > 180 && lambda < 360)
                alpha+=360;


            double ST=100.46+0.985647352*julianDay;
            ST=removeDublication(ST);


            dec=RadToDeg*Math.asin(Math.sin(obliquity*DegToRad)*Math.sin(lambda*DegToRad));

            double noon=alpha-ST;
    
            if (noon < 0)
                noon+=360;

            double UTNoon=noon-longitude;

            double localNoon=(UTNoon/15)+zone;

            zuhr=localNoon; 				//Zuhr Time.

            maghrib=localNoon+equation(-0.8333)/15;  	// Maghrib Time

            shrouk=localNoon-equation(-0.8333)/15;   	// Shrouk Time


            double fajrAlt = 0;
            double ishaAlt = 0;
            if (this.m_calender.type() == Calender.Type.UmmAlQuraUniv) {
                fajrAlt=-18.5;
            } else if (this.m_calender.type() == Calender.Type.EgytionGeneralAuthorityofSurvey) {
                fajrAlt=-19.5;
                ishaAlt=-17.5;
            } else if (m_calender.type() == Calender.Type.MuslimWorldLeague) {
                fajrAlt=-18;
                ishaAlt=-17;
            } else if (m_calender.type() == Calender.Type.IslamicSocietyOfNorthAmerica) {
                fajrAlt=ishaAlt=-15;
            } else if (m_calender.type() == Calender.Type.UnivOfIslamicScincesKarachi) {
                fajrAlt=ishaAlt=-18;
            }

            fajr=localNoon-equation(fajrAlt)/15;  	// Fajr Time

            isha=localNoon+equation(ishaAlt)/15;  	// Isha Time


            if( m_calender.type() == Calender.Type.UmmAlQuraUniv )
                isha=maghrib+1.5;

            double asrAlt;

            if( m_mazhab.type() == Mazhab.Type.Hanafi)
                asrAlt=90-RadToDeg*Math.atan(2+Math.tan(Math.abs(latitude-dec)*DegToRad));
            else
                asrAlt=90-RadToDeg*Math.atan(1+Math.tan(Math.abs(latitude-dec)*DegToRad));

            asr=localNoon+equation(asrAlt)/15;		// Asr Time.

            // Add one hour to all times if the season is Summmer.
            if( m_season.type() == Season.Type.Summer) {
                fajr+=1;
                shrouk+=1;
                zuhr+=1;
                asr+=1;
                maghrib+=1;
                isha+=1;
            }
}



	// Prayer Time
	public Time zuhrTime(){ 
        Time t = new Time(this.zuhr,true);
        return t;
    }
	public Time asrTime(){ 
		Time t = new Time(this.asr);
		return t;
	}
	public Time fajrTime(){ 
		Time t = new Time(this.fajr,true); 
		return t;
	}
	public Time ishaTime(){ 
		Time t = new Time(this.isha); 
		return t;
	}
	public Time maghribTime(){
		Time t = new Time(this.maghrib);
		return t;
	}
	public Time shroukTime(){ 
		Time t = new Time(this.shrouk,true); 
		return t;
	}



	// getter and setter functions.

	public Calender calender(){return m_calender;}
	public void setCalender( Calender calender){m_calender=calender;}
	public void setCalender(Calender.Type calender){m_calender.setCalender(calender);}
	public void setCalender( String calender){m_calender.setCalender(calender);}

	public Mazhab mazhab(){return this.m_mazhab;}
	public void setMazhab(Mazhab mazhab){this.m_mazhab=mazhab;}
	public void setMazhab(Mazhab.Type mazhab){this.m_mazhab.setMazhab(mazhab);}
	public void setMazhab(String mazhab){this.m_mazhab.setMazhab(mazhab);}

	public Season season(){return this.m_season;}
	public void setSeason(Season season){this.m_season=season;}
	public void setSeason(Season.Type season){this.m_season.setSeason(season);}
	public void setSeason( String season){this.m_season.setSeason(season);}

	public Coordinate coordinate(){return this.m_coordinate;}
	public void setCoordinate( Coordinate coordinate){this.m_coordinate=coordinate;}
	public void setCoordinate(double lot,double lat,int zone){
        this.m_coordinate.longitude=lot;
        this.m_coordinate.latitude=lat;
        this.m_coordinate.zone=zone;
    }

	public double longitude(){return this.m_coordinate.longitude;}
	public double latitude(){return this.m_coordinate.latitude;}
	public double zone() {return this.m_coordinate.zone;}

	public Date date(){return this.m_date;}
	public int day(){return this.m_date.day;}
	public int month(){return this.m_date.month;}
	public int year(){return this.m_date.year;}
	public void setDay(int day){this.m_date.day=day;}
	public void setMonth(int month){this.m_date.month=month;}
	public void setYear(int year){this.m_date.year=year;}
	public void setDate(int day,int month,int year){
        this.m_date.day=day;
        this.m_date.month=month;
        this.m_date.year=year;
    }
	public void setDate(Date date){this.m_date = date;}

	public void setData(Coordinate co,Date da,Calender ca,Mazhab ma,Season se){
        this.m_coordinate = co;
        this.m_date = da;
        this.m_calender=ca;
        this.m_mazhab=ma;
        this.m_season=se;
    }
	public void setData(double lot,double lat,int zone,int day,int month,int year, Calender ca, Mazhab ma, Season se){
        setCoordinate(lot,lat,zone);
        setDate(day,month,year);
        setCalender(ca);
        setMazhab(ma);
        setSeason(se);

    }
	public void setData(double lot,double lat,int zone,int day,int month,int year, String calender, String mazhab, String season){
        setCoordinate(lot,lat,zone);
        setDate(day,month,year);
        setCalender(calender);
        setMazhab(mazhab);
        setSeason(season);
    }

        


	// just used in calculate() function.
	protected double equation(double alt){
            return RadToDeg*Math.acos((Math.sin(alt*DegToRad)-Math.sin(dec*DegToRad)*Math.sin(m_coordinate.latitude*DegToRad))/(Math.cos(dec*DegToRad)*Math.cos(m_coordinate.latitude*DegToRad)));
        }

	protected double removeDublication(double var){
            if (var > 360) {
            var/=360;
            var-=(int)var;
            var*=360;
        }
        return var;
        }
	protected double abs(double var){
        
            if( var < 0 )
                return -var;
            else
                return var;
        }

/*    public static void main(String[] args){
        PrayerTime prayerTime = new PrayerTime(46.7825,24.6505,3,14,6,2011);
    // calculate prayer times.
        prayerTime.setMazhab("Hanafi");
        prayerTime.calculate();
        System.out.println(prayerTime.asrTime().text());


    }*/
}