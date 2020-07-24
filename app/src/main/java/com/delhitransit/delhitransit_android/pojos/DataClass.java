package com.delhitransit.delhitransit_android.pojos;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class DataClass extends AsyncTask<String, Integer, PolylineOptions> {

    TaskCompleteCallback taskCallback;

    public DataClass(Context context) {
        this.taskCallback = (TaskCompleteCallback) context;
    }

    public String[] getArray() {
        return data2.split(",");
    }

    public ArrayList<Points> makePoints() {

        //Log.e("TAG", "makePoints: called");
        String[] arr = getArray();
        ArrayList<Points> points = new ArrayList<>();
        Points single = new Points();
        for (int i = 0; i < arr.length; i++) {
            if (i % 3 == 1) {
                single.lat = Double.parseDouble(arr[i]);
            }
            if (i % 3 == 2) {
                single.lon = Double.parseDouble(arr[i]);
                points.add(single);
                single = new Points();
            }
        }

        //Log.e("TAG", "makePoints: " + points.toString());
        return points;
    }

    public PolylineOptions makePoly() {
        //Log.e("TAG", "makePoly: called");
        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<Points> points = makePoints();
        ArrayList<LatLng> resultPoints = new ArrayList<>();
        for (Points p : points) {
            resultPoints.add(new LatLng(p.lat, p.lon));
        }
        polylineOptions.addAll(resultPoints);
        polylineOptions.width(20);
        polylineOptions.color(Color.RED);
        return polylineOptions;
    }

    /*public void executeData() {
        PolylineOptions polylineOptions = makePoly();
        Log.e("TAG", "execute: called");

    }*/

    @Override
    protected PolylineOptions doInBackground(String... strings) {
        return makePoly();
    }

    @Override
    protected void onPostExecute(PolylineOptions polylineOptions) {
        taskCallback.onTaskDone(polylineOptions, makePoints().get(0), makePoints().get(makePoints().size() - 1));
    }

    static public class Points {
        public Double lon, lat;

        @NonNull
        @Override
        public String toString() {
            return "lat :" + lat + " lon :" + lon;
        }
    }


    String data = "0,28.6251975188353,77.1108082069294,0" +
            "0,28.62522,77.110815,1" +
            "0,28.62528,77.110831,2" +
            "0,28.62548300000001,77.110835,3" +
            "0,28.625705,77.110839,4" +
            "0,28.625940000000003,77.110844,5" +
            "0,28.626574,77.11085600000001,6" +
            "0,28.626793,77.11086,7" +
            "0,28.627,77.110864,8" +
            "0,28.62735300000001,77.110874,9" +
            "0,28.627621,77.11088199999998,10" +
            "0,28.6279942244605,77.1108931597108,11" +
            "0,28.628223,77.1109,12" +
            "0,28.628544,77.110908,13" +
            "0,28.628773,77.110917,14" +
            "0,28.62966,77.110941,15" +
            "0,28.630494,77.11096500000002,16" +
            "0,28.630763,77.11097600000002,17" +
            "0,28.63123,77.111046,18" +
            "0,28.631426,77.111133,19" +
            "0,28.631588,77.11125799999998,20" +
            "0,28.631738,77.111369,21" +
            "0,28.631762,77.111378,22" +
            "0,28.631784000000003,77.111395,23" +
            "0,28.631802,77.111417,24" +
            "0,28.631815000000003,77.11144499999997,25" +
            "0,28.631821,77.111475,26" +
            "0,28.631822,77.111506,27" +
            "0,28.631816,77.111537,28" +
            "0,28.631804,77.111564,29" +
            "0,28.631787,77.111588,30" +
            "0,28.631765,77.11160500000003,31" +
            "0,28.631741,77.111616,32" +
            "0,28.631712,77.111628,33" +
            "0,28.631694,77.111636,34" +
            "0,28.631674,77.111649,35" +
            "0,28.631619,77.11171800000002,36" +
            "0,28.631436072609,77.1119563092562,37" +
            "0,28.631292,77.112144,38" +
            "0,28.631237,77.112259,39" +
            "0,28.631156,77.112443,40" +
            "0,28.630979,77.113099,41" +
            "0,28.63096,77.113297,42" +
            "0,28.630954,77.113721,43" +
            "0,28.630936726345897,77.1148990722338,44" +
            "0,28.630929,77.115426,45" +
            "0,28.63092,77.11627800000002,46" +
            "0,28.630913,77.11653000000003,47" +
            "0,28.63091,77.11688000000002,48" +
            "0,28.63090200000001,77.11724699999998,49" +
            "0,28.630898,77.11740400000002,50" +
            "0,28.63089,77.117728,51" +
            "0,28.630881,77.118145,52" +
            "0,28.630872,77.118496,53" +
            "0,28.630863,77.11885699999998,54" +
            "0,28.630859,77.119015,55" +
            "0,28.630853,77.11927299999998,56" +
            "0,28.63085,77.11952099999998,57" +
            "0,28.6308487465842,77.1195991777527,58" +
            "0,28.630847,77.119708,59" +
            "0,28.630839,77.120308,60" +
            "0,28.63083300000001,77.12080999999998,61" +
            "0,28.630832,77.120903,62" +
            "0,28.63083,77.121063,63" +
            "0,28.630828,77.121325,64" +
            "0,28.630825,77.121579,65" +
            "0,28.630823,77.121829,66" +
            "0,28.63078,77.122106,67" +
            "0,28.630726,77.12228499999998,68" +
            "0,28.630642,77.122485,69" +
            "0,28.630564,77.12259,70" +
            "0,28.63046,77.12273,71" +
            "0,28.63004,77.123378,72" +
            "0,28.629802,77.123683,73" +
            "0,28.6305967099194,77.1243672238883,74" +
            "0,28.63080900000001,77.12455,75" +
            "0,28.631849,77.125445,76" +
            "0,28.63278,77.126246,77" +
            "0,28.632973,77.12640999999998,78" +
            "0,28.633953,77.12725400000002,79" +
            "0,28.634398,77.12763199999998,80" +
            "0,28.6345455100807,77.1277351924865,81" +
            "0,28.634854,77.127951,82" +
            "0,28.635305,77.128146,83" +
            "0,28.635914,77.12818399999998,84" +
            "0,28.636281,77.128211,85" +
            "0,28.636486,77.1283,86" +
            "0,28.636661,77.12843000000002,87" +
            "0,28.6367137242407,77.12848372854891,88" +
            "0,28.63687100000001,77.128644,89" +
            "0,28.637029,77.128863,90" +
            "0,28.637354,77.12984399999998,91" +
            "0,28.637426,77.130054,92" +
            "0,28.637497,77.130264,93" +
            "0,28.637606,77.130675,94" +
            "0,28.637902,77.13152600000002,95" +
            "0,28.638037,77.131828,96" +
            "0,28.63816,77.13210699999998,97" +
            "0,28.638293,77.132402,98" +
            "0,28.638686,77.13303,99" +
            "0,28.639002,77.133394,100" +
            "0,28.639089,77.133489,101" +
            "0,28.639291605009106,77.1337077744916,102" +
            "0,28.639302,77.133719,103" +
            "0,28.639568,77.13399799999998,104" +
            "0,28.640027000000003,77.134491,105" +
            "0,28.640347,77.134792,106" +
            "0,28.640691,77.135111,107" +
            "0,28.641168,77.135561,108" +
            "0,28.641397,77.135775,109" +
            "0,28.641655,77.135992,110";

    static public String data2 = "0,28.6251975188353,77.1108082069294,0" +
            "0,28.62522,77.110815,1" +
            "0,28.62528,77.110831,2" +
            "0,28.62548300000001,77.110835,3" +
            "0,28.625705,77.110839,4" +
            "0,28.625940000000003,77.110844,5" +
            "0,28.626574,77.11085600000001,6" +
            "0,28.626793,77.11086,7" +
            "0,28.627,77.110864,8" +
            "0,28.62735300000001,77.110874,9" +
            "0,28.627621,77.11088199999998,10" +
            "0,28.6279942244605,77.1108931597108,11" +
            "0,28.628223,77.1109,12" +
            "0,28.628544,77.110908,13" +
            "0,28.628773,77.110917,14" +
            "0,28.62966,77.110941,15" +
            "0,28.630494,77.11096500000002,16" +
            "0,28.630763,77.11097600000002,17" +
            "0,28.63123,77.111046,18" +
            "0,28.631426,77.111133,19" +
            "0,28.631588,77.11125799999998,20" +
            "0,28.631738,77.111369,21" +
            "0,28.631762,77.111378,22" +
            "0,28.631784000000003,77.111395,23" +
            "0,28.631802,77.111417,24" +
            "0,28.631815000000003,77.11144499999997,25" +
            "0,28.631821,77.111475,26" +
            "0,28.631822,77.111506,27" +
            "0,28.631816,77.111537,28" +
            "0,28.631804,77.111564,29" +
            "0,28.631787,77.111588,30" +
            "0,28.631765,77.11160500000003,31" +
            "0,28.631741,77.111616,32" +
            "0,28.631712,77.111628,33" +
            "0,28.631694,77.111636,34" +
            "0,28.631674,77.111649,35" +
            "0,28.631619,77.11171800000002,36" +
            "0,28.631436072609,77.1119563092562,37" +
            "0,28.631292,77.112144,38" +
            "0,28.631237,77.112259,39" +
            "0,28.631156,77.112443,40" +
            "0,28.630979,77.113099,41" +
            "0,28.63096,77.113297,42" +
            "0,28.630954,77.113721,43" +
            "0,28.630936726345897,77.1148990722338,44" +
            "0,28.630929,77.115426,45" +
            "0,28.63092,77.11627800000002,46" +
            "0,28.630913,77.11653000000003,47" +
            "0,28.63091,77.11688000000002,48" +
            "0,28.63090200000001,77.11724699999998,49" +
            "0,28.630898,77.11740400000002,50" +
            "0,28.63089,77.117728,51" +
            "0,28.630881,77.118145,52" +
            "0,28.630872,77.118496,53" +
            "0,28.630863,77.11885699999998,54" +
            "0,28.630859,77.119015,55" +
            "0,28.630853,77.11927299999998,56" +
            "0,28.63085,77.11952099999998,57" +
            "0,28.6308487465842,77.1195991777527,58" +
            "0,28.630847,77.119708,59" +
            "0,28.630839,77.120308,60" +
            "0,28.63083300000001,77.12080999999998,61" +
            "0,28.630832,77.120903,62" +
            "0,28.63083,77.121063,63" +
            "0,28.630828,77.121325,64" +
            "0,28.630825,77.121579,65" +
            "0,28.630823,77.121829,66" +
            "0,28.63078,77.122106,67" +
            "0,28.630726,77.12228499999998,68" +
            "0,28.630642,77.122485,69" +
            "0,28.630564,77.12259,70" +
            "0,28.63046,77.12273,71" +
            "0,28.63004,77.123378,72" +
            "0,28.629802,77.123683,73" +
            "0,28.6305967099194,77.1243672238883,74" +
            "0,28.63080900000001,77.12455,75" +
            "0,28.631849,77.125445,76" +
            "0,28.63278,77.126246,77" +
            "0,28.632973,77.12640999999998,78" +
            "0,28.633953,77.12725400000002,79" +
            "0,28.634398,77.12763199999998,80" +
            "0,28.6345455100807,77.1277351924865,81" +
            "0,28.634854,77.127951,82" +
            "0,28.635305,77.128146,83" +
            "0,28.635914,77.12818399999998,84" +
            "0,28.636281,77.128211,85" +
            "0,28.636486,77.1283,86" +
            "0,28.636661,77.12843000000002,87" +
            "0,28.6367137242407,77.12848372854891,88" +
            "0,28.63687100000001,77.128644,89" +
            "0,28.637029,77.128863,90" +
            "0,28.637354,77.12984399999998,91" +
            "0,28.637426,77.130054,92" +
            "0,28.637497,77.130264,93" +
            "0,28.637606,77.130675,94" +
            "0,28.637902,77.13152600000002,95" +
            "0,28.638037,77.131828,96" +
            "0,28.63816,77.13210699999998,97" +
            "0,28.638293,77.132402,98" +
            "0,28.638686,77.13303,99" +
            "0,28.639002,77.133394,100" +
            "0,28.639089,77.133489,101" +
            "0,28.639291605009106,77.1337077744916,102" +
            "0,28.639302,77.133719,103" +
            "0,28.639568,77.13399799999998,104" +
            "0,28.640027000000003,77.134491,105" +
            "0,28.640347,77.134792,106" +
            "0,28.640691,77.135111,107" +
            "0,28.641168,77.135561,108" +
            "0,28.641397,77.135775,109" +
            "0,28.641655,77.135992,110" +
            "0,28.6418,77.136114,111" +
            "0,28.642343,77.13656800000003,112" +
            "0,28.64265300000001,77.13683,113" +
            "0,28.643116,77.137222,114" +
            "0,28.643304,77.137383,115" +
            "0,28.643987,77.137962,116" +
            "0,28.644122,77.138079,117" +
            "0,28.645083000000003,77.138915,118" +
            "0,28.645466,77.13931,119" +
            "0,28.645576,77.139453,120" +
            "0,28.645875,77.139843,121" +
            "0,28.645962,77.13996999999998,122" +
            "0,28.646392,77.140585,123" +
            "0,28.6466467946834,77.14094788883929,124" +
            "0,28.646788,77.141149,125" +
            "0,28.647321,77.141891,126" +
            "0,28.647425,77.142037,127" +
            "0,28.647586,77.14217099999998,128" +
            "0,28.648341,77.14277299999998,129" +
            "0,28.649471,77.143644,130" +
            "0,28.65007,77.144104,131" +
            "0,28.65027000000001,77.14373499999998,132" +
            "0,28.65057,77.143182,133" +
            "0,28.65053300000001,77.142862,134" +
            "0,28.650522,77.14275699999997,135" +
            "0,28.650472,77.142332,136" +
            "0,28.650722,77.14192299999998,137" +
            "0,28.650818,77.141736,138" +
            "0,28.651213,77.141121,139" +
            "0,28.651334988480397,77.1409213818491,140" +
            "0,28.651378,77.140851,141" +
            "0,28.651619,77.140457,142" +
            "0,28.651773,77.14020500000002,143" +
            "0,28.652137,77.13961,144" +
            "0,28.652509,77.13900100000002,145" +
            "0,28.652488,77.138986,146" +
            "0,28.652466,77.138966,147" +
            "0,28.652447,77.138942,148" +
            "0,28.65243,77.13891600000002,149" +
            "0,28.652416,77.138887,150" +
            "0,28.652404,77.138856,151" +
            "0,28.652396000000003,77.138823,152" +
            "0,28.652392,77.13879,153" +
            "0,28.652391,77.138756,154" +
            "0,28.652393,77.138723,155" +
            "0,28.652398,77.138689,156" +
            "0,28.652407,77.138657,157" +
            "0,28.652426,77.138612,158" +
            "0,28.652442,77.13858499999998,159" +
            "0,28.652461,77.138561,160" +
            "0,28.652482,77.138539,161" +
            "0,28.652505,77.138521,162" +
            "0,28.65253,77.13850699999998,163" +
            "0,28.652556,77.138497,164" +
            "0,28.652583,77.138491,165" +
            "0,28.652611,77.138489,166" +
            "0,28.652638,77.138491,167" +
            "0,28.652665,77.138497,168" +
            "0,28.652691,77.13850699999998,169" +
            "0,28.652716,77.138521,170" +
            "0,28.652739,77.138539,171" +
            "0,28.653281,77.13765,172" +
            "0,28.653382,77.137485,173" +
            "0,28.65384,77.137165,174" +
            "0,28.65406500000001,77.137008,175" +
            "0,28.654101,77.13698199999997,176" +
            "0,28.654222,77.136905,177" +
            "0,28.654287,77.136864,178" +
            "0,28.654573,77.136684,179" +
            "0,28.654626,77.136652,180" +
            "0,28.6546697013491,77.13673026442709,181" +
            "0,28.654889,77.137123,182" +
            "0,28.655154,77.137613,183" +
            "0,28.655392,77.138046,184" +
            "0,28.655602,77.13843,185" +
            "0,28.65583,77.13884300000002,186" +
            "0,28.656078000000004,77.13929300000002,187" +
            "0,28.656185,77.139492,188" +
            "0,28.656344,77.139792,189" +
            "0,28.656579,77.140253,190" +
            "0,28.656635,77.140375,191" +
            "0,28.656833,77.14072,192" +
            "0,28.656906,77.140849,193" +
            "0,28.657101,77.141191,194" +
            "0,28.65717,77.141315,195" +
            "0,28.657351,77.141638,196" +
            "0,28.657425,77.141769,197" +
            "0,28.657511,77.141922,198" +
            "0,28.657624,77.142123,199" +
            "0,28.657696,77.142251,200" +
            "0,28.657883,77.142588,201" +
            "0,28.658046,77.142883,202" +
            "0,28.658149,77.143069,203" +
            "0,28.658497,77.143703,204" +
            "0,28.6587907193616,77.14422585177041,205" +
            "0,28.658879,77.14438299999998,206" +
            "0,28.659218,77.145073,207" +
            "0,28.659357,77.145331,208" +
            "0,28.65949,77.145578,209" +
            "0,28.659798,77.146196,210" +
            "0,28.660174,77.146867,211" +
            "0,28.660407,77.14731,212" +
            "0,28.6604665266815,77.1474265082021,213" +
            "0,28.660524,77.147539,214" +
            "0,28.660647,77.147837,215" +
            "0,28.6608,77.148483,216" +
            "0,28.660905,77.148928,217" +
            "0,28.660965,77.149183,218" +
            "0,28.661035,77.14948199999998,219" +
            "0,28.661078000000003,77.14965500000002,220" +
            "0,28.661171000000003,77.14999399999998,221" +
            "0,28.661205,77.150112,222" +
            "0,28.661384,77.150531,223" +
            "0,28.661449,77.150687,224" +
            "0,28.661597,77.150997,225" +
            "0,28.661719,77.151255,226" +
            "0,28.661815,77.151457,227" +
            "0,28.661911,77.151661,228" +
            "0,28.66195,77.151742,229" +
            "0,28.662024,77.151898,230" +
            "0,28.662318,77.152551,231" +
            "0,28.662522,77.153023,232" +
            "0,28.66261,77.15323599999998,233" +
            "0,28.6627344039752,77.1535279512473,234" +
            "0,28.662907,77.153933,235" +
            "0,28.663091,77.15436600000002,236" +
            "0,28.663399,77.155076,237" +
            "0,28.66362,77.155608,238" +
            "0,28.663879,77.156233,239" +
            "0,28.664178000000003,77.15689300000003,240" +
            "0,28.664552,77.15778399999998,241" +
            "0,28.664748,77.158287,242" +
            "0,28.664919,77.15878599999998,243" +
            "0,28.665248,77.15974,244" +
            "0,28.665316959990395,77.1599494735675,245" +
            "0,28.665381,77.16014399999997,246" +
            "0,28.665519,77.160566,247" +
            "0,28.665626,77.160891,248" +
            "0,28.665759,77.16129699999998,249" +
            "0,28.66583,77.161521,250" +
            "0,28.66601,77.162085,251" +
            "0,28.666090000000004,77.162309,252" +
            "0,28.666445,77.163327,253" +
            "0,28.666485,77.163413,254" +
            "0,28.66651,77.163436,255" +
            "0,28.666544,77.163464,256" +
            "0,28.666598,77.163503,257" +
            "0,28.666626,77.163516,258" +
            "0,28.666651,77.163536,259" +
            "0,28.666673,77.16356,260" +
            "0,28.66669000000001,77.163589,261" +
            "0,28.666703,77.16362099999998,262" +
            "0,28.66671,77.163655,263" +
            "0,28.666711,77.16369,264" +
            "0,28.667152,77.163931,265" +
            "0,28.66756,77.164139,266" +
            "0,28.6679865370877,77.16431552967641,267" +
            "0,28.668036,77.16433599999998,268" +
            "0,28.66818,77.16438199999997,269" +
            "0,28.668333,77.164379,270" +
            "0,28.668528,77.164354,271" +
            "0,28.66861,77.164358,272" +
            "0,28.668635,77.164362,273" +
            "0,28.668658,77.164374,274" +
            "0,28.668687,77.164366,275" +
            "0,28.668713,77.16436,276" +
            "0,28.668741,77.16435899999998,277" +
            "0,28.668777,77.164362,278" +
            "0,28.668812,77.16436800000002,279" +
            "0,28.668855,77.164388,280" +
            "0,28.668896000000004,77.16441,281" +
            "0,28.66892600000001,77.164436,282" +
            "0,28.668953,77.164466,283" +
            "0,28.668988,77.164516,284" +
            "0,28.669006,77.164556,285" +
            "0,28.669018,77.164586,286" +
            "0,28.669027000000003,77.164625,287" +
            "0,28.669319,77.16489200000002,288" +
            "0,28.669477,77.164991,289" +
            "0,28.669609,77.16506899999997,290" +
            "0,28.669837,77.165161,291" +
            "0,28.670076,77.16528100000002,292" +
            "0,28.670257,77.165447,293" +
            "0,28.670369,77.165537,294" +
            "0,28.670595,77.16582700000002,295" +
            "0,28.67083,77.166397,296" +
            "0,28.671109,77.166866,297" +
            "0,28.671181,77.166996,298" +
            "0,28.671317639102696,77.1672451656533,299" +
            "0,28.672218,77.168887,300" +
            "0,28.672352000000004,77.169197,301" +
            "0,28.672454,77.169457,302" +
            "0,28.672511,77.169689,303" +
            "0,28.672525,77.16975,304" +
            "0,28.67255,77.169892,305" +
            "0,28.672567,77.170058,306" +
            "0,28.672575,77.170284,307" +
            "0,28.672573,77.17053,308" +
            "0,28.672558,77.17076800000002,309" +
            "0,28.672542,77.17095400000002,310" +
            "0,28.672513,77.171152,311" +
            "0,28.672453000000004,77.17138299999998,312" +
            "0,28.672378,77.171615,313" +
            "0,28.672267,77.171902,314" +
            "0,28.672156,77.172151,315" +
            "0,28.67203,77.172451,316" +
            "0,28.671854,77.172872,317" +
            "0,28.6717120875848,77.1732051489972,318" +
            "0,28.67169,77.17325699999998,319" +
            "0,28.671625,77.173472,320" +
            "0,28.671583,77.173674,321" +
            "0,28.671514,77.174279,322" +
            "0,28.671426,77.17482,323" +
            "0,28.671315000000003,77.175399,324" +
            "0,28.671237,77.17577800000002,325" +
            "0,28.671126,77.176317,326" +
            "0,28.670821000000004,77.177787,327" +
            "0,28.670668,77.178557,328" +
            "0,28.670572,77.179047,329" +
            "0,28.670548,77.17916,330" +
            "0,28.670413,77.17984,331" +
            "0,28.670285,77.180488,332" +
            "0,28.670231,77.18075400000002,333" +
            "0,28.670205,77.180891,334" +
            "0,28.670024,77.182498,335" +
            "0,28.669981,77.183397,336" +
            "0,28.670152,77.183485,337" +
            "0,28.670682,77.183757,338" +
            "0,28.670849,77.183878,339" +
            "0,28.671102618644,77.184143147384,340" +
            "0,28.671157,77.1842,341" +
            "0,28.671401,77.184498,342" +
            "0,28.671874,77.185117,343" +
            "0,28.672536,77.185982,344" +
            "0,28.672756,77.18627,345" +
            "0,28.67295,77.186524,346" +
            "0,28.6729593886365,77.18653627861829,347" +
            "0,28.673278000000003,77.186953,348" +
            "0,28.673629,77.187412,349" +
            "0,28.673665000000003,77.18746,350" +
            "0,28.673787,77.18764399999998,351" +
            "0,28.674888,77.188397,352" +
            "0,28.675578,77.18883000000002,353" +
            "0,28.675624,77.188857,354" +
            "0,28.676136,77.18914699999998,355" +
            "0,28.676316,77.18918000000002,356" +
            "0,28.676749,77.189064,357" +
            "0,28.6769972255581,77.1889247671338,358" +
            "0,28.677143,77.188843,359" +
            "0,28.677337,77.188675,360" +
            "0,28.67757000000001,77.188474,361" +
            "0,28.677613,77.188435,362" +
            "0,28.677640000000004,77.188489,363" +
            "0,28.677825,77.188869,364" +
            "0,28.677845,77.188911,365" +
            "0,28.678069,77.18964799999998,366" +
            "0,28.678315,77.19045799999998,367" +
            "0,28.678618,77.191489,368" +
            "0,28.678803,77.191989,369" +
            "0,28.679037,77.192806,370" +
            "0,28.679187,77.193405,371" +
            "0,28.67927,77.193714,372" +
            "0,28.679334000000004,77.19395,373" +
            "0,28.679359,77.194039,374" +
            "0,28.679374,77.194037,375" +
            "0,28.679413,77.194036,376" +
            "0,28.679451,77.194037,377" +
            "0,28.67949,77.194041,378" +
            "0,28.679528,77.194048,379" +
            "0,28.679566,77.194057,380" +
            "0,28.679603000000004,77.194069,381" +
            "0,28.67964000000001,77.194084,382" +
            "0,28.679676,77.194101,383" +
            "0,28.67971,77.194121,384" +
            "0,28.679744,77.19414300000003,385" +
            "0,28.679776,77.19416700000002,386" +
            "0,28.679807,77.194194,387" +
            "0,28.679836,77.194223,388" +
            "0,28.679864,77.194253,389" +
            "0,28.679890000000004,77.194286,390" +
            "0,28.679914,77.19431999999998,391" +
            "0,28.679936,77.194356,392" +
            "0,28.679956,77.19439399999997,393" +
            "0,28.679974,77.19443299999998,394" +
            "0,28.679990000000004,77.194473,395" +
            "0,28.680004,77.194514,396" +
            "0,28.680015,77.194556,397" +
            "0,28.680024,77.194598,398" +
            "0,28.68003,77.194641,399" +
            "0,28.680035,77.19468499999998,400" +
            "0,28.680036,77.19472900000002,401" +
            "0,28.680036,77.194772,402" +
            "0,28.680033,77.194816,403" +
            "0,28.68002700000001,77.194859,404" +
            "0,28.68002000000001,77.194902,405" +
            "0,28.680009,77.19494499999998,406" +
            "0,28.679997,77.194986,407" +
            "0,28.679982,77.195027,408" +
            "0,28.67996500000001,77.195066,409" +
            "0,28.679946,77.195104,410" +
            "0,28.679925,77.195141,411" +
            "0,28.679902,77.195176,412" +
            "0,28.679877,77.19520899999998,413" +
            "0,28.67985,77.19524100000002,414" +
            "0,28.679822,77.19527099999998,415" +
            "0,28.679792,77.195299,416" +
            "0,28.679788,77.195302,417" +
            "0,28.67997,77.19558599999998,418" +
            "0,28.680107,77.1958,419" +
            "0,28.6803646602977,77.1962240930615,420" +
            "0,28.680686,77.196753,421" +
            "0,28.681303000000003,77.19775200000002,422" +
            "0,28.681477,77.198141,423" +
            "0,28.681590000000003,77.19839499999998,424" +
            "0,28.681614,77.19845,425" +
            "0,28.681762,77.19868100000002,426" +
            "0,28.681888,77.198876,427" +
            "0,28.682185,77.199336,428" +
            "0,28.682479,77.199792,429" +
            "0,28.682767,77.20024000000002,430" +
            "0,28.683048,77.20067399999998,431" +
            "0,28.68307,77.200709,432" +
            "0,28.683319,77.20109599999998,433" +
            "0,28.683605,77.201544,434" +
            "0,28.684173261507894,77.202390553961,435" +
            "0,28.684481,77.202849,436" +
            "0,28.684495,77.20284000000002,437" +
            "0,28.684522,77.20283,438" +
            "0,28.684549,77.202825,439" +
            "0,28.684595,77.20283,440" +
            "0,28.684622,77.20284000000002,441" +
            "0,28.684646,77.202855,442" +
            "0,28.684667,77.202875,443" +
            "0,28.684684000000004,77.202899,444" +
            "0,28.684697,77.202926,445" +
            "0,28.684705,77.202956,446" +
            "0,28.684708,77.202986,447" +
            "0,28.684706,77.203016,448" +
            "0,28.684699,77.203046,449" +
            "0,28.684687,77.203073,450" +
            "0,28.68467,77.203098,451" +
            "0,28.68465,77.203119,452" +
            "0,28.684626,77.203135,453" +
            "0,28.684602,77.203144,454" +
            "0,28.6847096672957,77.2037778536054,455" +
            "0,28.684859000000003,77.204657,456" +
            "0,28.684925,77.204836,457" +
            "0,28.685097,77.204976,458" +
            "0,28.685235,77.205048,459" +
            "0,28.68553,77.20518,460" +
            "0,28.686475918984197,77.2056188995567,461" +
            "0,28.686627,77.205689,462" +
            "0,28.687088,77.205903,463" +
            "0,28.688433000000003,77.206571,464" +
            "0,28.688893,77.206799,465" +
            "0,28.6890595273139,77.2068815981575,466" +
            "0,28.689143,77.206923,467" +
            "0,28.689441,77.207071,468" +
            "0,28.68952800000001,77.207114,469" +
            "0,28.690028,77.207362,470" +
            "0,28.690737,77.207714,471" +
            "0,28.691758,77.208221,472" +
            "0,28.691806,77.208244,473" +
            "0,28.691845,77.20829,474" +
            "0,28.691919,77.208318,475" +
            "0,28.692152,77.208432,476" +
            "0,28.692454,77.20858,477" +
            "0,28.692868,77.208782,478" +
            "0,28.693321,77.20900300000002,479" +
            "0,28.69402100000001,77.209346,480" +
            "0,28.69415200000001,77.20940999999998,481" +
            "0,28.694849895614,77.20975079311279,482" +
            "0,28.694965000000003,77.209807,483" +
            "0,28.695362,77.210001,484" +
            "0,28.695567,77.210126,485" +
            "0,28.696345,77.210482,486" +
            "0,28.696619,77.210613,487" +
            "0,28.696668,77.21064100000002,488" +
            "0,28.696386,77.21137900000002,489" +
            "0,28.696221,77.21181700000002,490" +
            "0,28.696093,77.21214499999998,491" +
            "0,28.6960557270357,77.21223425964129,492" +
            "0,28.695675,77.213146,493" +
            "0,28.695571,77.213421,494" +
            "0,28.694821,77.215446,495" +
            "0,28.694738,77.215671,496" +
            "0,28.694693,77.215789,497" +
            "0,28.695181,77.21601899999997,498" +
            "0,28.6957603923002,77.2162947025742,499" +
            "0,28.696154,77.216482,500" +
            "0,28.696235,77.216521,501" +
            "0,28.697162,77.216962,502" +
            "0,28.697715,77.217225,503" +
            "0,28.698126,77.217421,504" +
            "0,28.698731,77.217709,505" +
            "0,28.699012,77.217843,506" +
            "0,28.699264000000003,77.217963,507" +
            "0,28.699448,77.218051,508" +
            "0,28.699646,77.21814499999998,509" +
            "0,28.699833,77.218234,510" +
            "0,28.700446000000003,77.218537,511" +
            "0,28.700850566507892,77.218736369911,512" +
            "0,28.701071,77.218845,513" +
            "0,28.702341,77.219466,514" +
            "0,28.702598,77.219587,515" +
            "0,28.703125,77.21983399999998,516" +
            "0,28.703532122020306,77.2200252857535,517" +
            "0,28.703921,77.220208,518" +
            "0,28.705197,77.220807,519" +
            "0,28.7059603896553,77.221180877122,520" +
            "0,28.70632,77.221357,521" +
            "0,28.706459,77.221425,522" +
            "0,28.707133,77.221755,523" +
            "0,28.707282,77.221828,524" +
            "0,28.70735,77.22203,525" +
            "0,28.707302,77.222319,526" +
            "0,28.70719,77.22261400000002,527" +
            "0,28.707121546697397,77.2227878528183,528" +
            "0,28.706951,77.223221,529" +
            "0,28.706698,77.22391,530" +
            "0,28.707115,77.22412800000002,531" +
            "0,28.707437,77.22429699999998,532" +
            "0,28.707744,77.224497,533" +
            "0,28.70810800000001,77.224733,534" +
            "0,28.708521,77.225039,535" +
            "0,28.708807,77.225401,536" +
            "0,28.70888900000001,77.225573,537" +
            "0,28.709005,77.226235,538" +
            "0,28.7090053714457,77.2262388875914,539" +
            "0,28.709082,77.227041,540" +
            "0,28.709744,77.226979,541" +
            "0,28.709711,77.226601,542" +
            "0,28.709688,77.226093,543" +
            "0,28.709685,77.22541600000002,544" +
            "0,28.7097,77.22513199999999,545" +
            "0,28.709702000000004,77.225027,546" +
            "0,28.709702000000004,77.22498,547" +
            "0,28.709705,77.224936,548" +
            "0,28.709725,77.224873,549" +
            "0,28.709781,77.22478199999998,550" +
            "0,28.709894,77.224621,551" +
            "0,28.710431,77.22393100000002,552" +
            "0,28.711516,77.222501,553";

}

