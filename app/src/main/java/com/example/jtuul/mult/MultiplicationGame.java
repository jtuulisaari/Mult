package com.example.jtuul.mult;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;

/**
 * Created by jtuul on 12/2/2017.
 * Tama luokka pitää sisällään kaikki pelin logiikkaan tarvittavat muuttujat ja metodit.
 *
 * Uudeksi paikaksi arvotaan aina alas tai oikea,
 * Paitsi jos edellisellä kierroksella tultiin uuteen ruutuun ja se ei mennyt target-nopeudella
 * Tällöin valitaan se ruutu, joka on lähinnä target aikaa
 *
 * TODO: Pystyisikö suoritusta nopeuttamaan, uusi thread? Petrin kanssa?
 * TODO: Tee aloitusruutuaktiviteetti ja siihen nappulta jne. (sama layout)
 * TODO: Jostakin syystä voi vielä tulla oikea vastaus kahteen kertaan. Lisäksi täysin random-vastauksen voisi poistaa.
 * TODO: Menee edelleen väärän vastauksen seurauksena ainakin kulmittain liian pitkälle TAI jo suoritettuun ruutuun. Ainakin jälkimmäinen pitää korjata.
 * TODO: Edelliseen liittyen: tee uudestaan getAccessablePositions-menettely. Tee tästä matriisi, jossa boolean arvot, tee sen jälkeen myös crawl siten, että testataan, ettei päädy tällaiseen, MUTTA jos ei muita vaihtoehtoja niin pitää siirtyä taas arvontaan (esim. nurkassa siten, ettei crawl ole mahdollinen.
 * TODO: Huomattu Joelin puhelimella, ettei esim. 1:5 * 1:10 toimi (epäsymmetrinen tapaus).
 * TODO: Muuta samalla pistelaskua site, ettei virheen jälkeen voi saada enää pisteitä lähtien siitä, kuinka paljon aika paranee... Tätä varten tarvitaan kuten R:ssä toinen matriisi, josta pisteet?
 * TODO: Tee uusi activity, high score?
 *
 * PLAN: tee toinen matriisi, jossa minimiaika, jos tämä alittaa targetTimen niin tämä ei ole "accessable"; lisää sen jälkeen menettely random-valintaan ja pistelaskuun.
 */

public class MultiplicationGame {
    // Pitaisikö rakentaa testiluokka, jossa tätä tehdään?
    int x; int x0, x1, xLen;
    int y; int y0, y1, yLen;

    public Integer answer;
    final public int initialAnswerTime = 10 * 1000; // Annettava millisekunteissa, jotta vältytään double lukujen vertaillulta!
    private boolean alreadyOnNew = false;
    // int[][] answerTimeMatrix = new int[10][10];
    public int[][] answerTimeMatrix;
    public int[][] lowestAnswerTimeMatrix;
    private long answerStartTime = 0;
    private double answerTime;
    public double answerTargetTime = 5 * 1000; // Mikä on vastaukselle tavoite aika
    SoundPlayer sp;
    public double points = 0;
    public double previousPoints = 0;

    public MultiplicationGame(int x0, int x1, int y0, int y1) { // Konstruktori

        this.x0 = x0; this.x1 = x1; this.y0 = y0; this.y1 = y1;
        this.x = x0; this.y = y0;
        this.setxAndyLen();
        answerTimeMatrix = new int[xLen][yLen];
        lowestAnswerTimeMatrix = new int[xLen][yLen];
        this.initializeMatrix(answerTimeMatrix, initialAnswerTime);
        this.initializeMatrix(lowestAnswerTimeMatrix, initialAnswerTime);
        // int[][] relMatrix = new int[xLen][yLen];
        // this.initializeMatrix(relMatrix, 0);
        int xx = 1;
    }

    private int test(int x, int y) { // Taman voi myohemmin poistaa
        return x * y;
    }

    public Integer[] getAnswerOptions() {

        Integer[] wrongAnswers = new Integer[11];
        Integer[] answersShown = new Integer[4];

        // Tee ensin kymmenen vaihtoehtoa, joista vain yksi oikein
        Double rnd1 = Math.floor(random() * this.x1*this.y1);
        Double rnd2 = Math.floor(random() * 10 - 5);
        wrongAnswers[0] = rnd1.intValue();
        wrongAnswers[1] = rnd1.intValue() + this.x * this.y;
        wrongAnswers[2] = rnd2.intValue() + this.x * this.y;
        wrongAnswers[3] = this.x * this.y + this.y;
        wrongAnswers[4] = this.x * this.y - this.y;
        wrongAnswers[5] = this.x * this.y + this.x;
        wrongAnswers[6] = this.x * this.y - this.x;
        wrongAnswers[7] = this.x * this.y + 1;
        wrongAnswers[8] = this.x * this.y - 1;
        wrongAnswers[9] = this.x * this.y + 2;
        wrongAnswers[10] = this.x * this.y - 2;

        answersShown[3] = x * y; // Varmistetaan, ettei tule oikea vastaus tuplana (kts. identical())
        for (int i = 0; i <= 3; ++i) {
            answersShown[i] = wrongAnswers[this.randomWithRange(0, 10)];
            boolean cond = true;
            int n = 0; int addOn = 0;
            while ((answersShown[i] < x0*y0 | answersShown[i] > x1*y1 | cond) & x1*y1-x0*y0 > 2) {
                n = n+1;
                Double tmp = Math.floor(random() * 10 - 5);
                if(n > 10) addOn = tmp.intValue(); // Varmistetaan, että löytyy sopiva väärä vastaus
                answersShown[i] = wrongAnswers[this.randomWithRange(0, 9)] + addOn;
                cond = this.identical(answersShown);
                if(n > 100)
                    cond = true;
            }
        }
        answersShown[this.randomWithRange(0, 3)] = x * y; // Correct answer
        return answersShown;
    }

    private int randomWithRange(int min, int max) { // https://stackoverflow.com/questions/7961788/math-random-explained
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    private void setx1y1AnswerTime() {
        if(this.answerTimeMatrix[0][0] >= this.initialAnswerTime) this.answerTimeMatrix[0][0] = this.answerTimeMatrix[0][0] * this.initialAnswerTime;
    }

    private List getAccessablePositions(String direction) {
        // Tän voisi tehdä indexOf avulla siten, että tehdään matriisi, jossa ao. ehto ja sen jälkeen etsitään listaan kuten alempana ehdon täyttävät indeksit
        // xLen = this.setxAndyLen("x");
        // yLen = this.setxAndyLen("y");
        List<Integer> ret = new ArrayList<Integer>();

        for (int i = 0; i < xLen; i++) {
            for (int k = 0; k < yLen; k++) {
                if(this.answerTimeMatrix[i][k] > this.answerTargetTime & this.answerTimeMatrix[i][k] < this.initialAnswerTime) {
                    if(direction.equals("x")) ret.add(i);
                    if(direction.equals("y")) ret.add(k);
                }
            }
        }
        return ret;
    }

    private void catchFun() {
        int sxxx = 1; // Tähän myöhemmin error catch
    }

    private void setSmallestNewAsxAndy() {
        // Etsitään pienimmän tekemättömän (uuden) kertolaskun indeksi
        // xLen = this.setxAndyLen("x");
        // yLen = this.setxAndyLen("y");
        List<Integer> posInd = new ArrayList<Integer>();

        int current = x1*y1; // Aloitetaan suurimmasta mahdollisesta
        for (int i = 0; i < xLen; i++) {
            for (int k = 0; k < yLen; k++) {
                if (this.answerTimeMatrix[i][k] == this.initialAnswerTime & (k * i) < current) { // xxx tässä pitää vielä valita näistä pienin
                    current = k * i; // Vrt. if-lauseen sisällä oleva ehto; k*i "koko" ei mene loop-järjestyksessä
                    posInd.add(i);
                    posInd.add(k);
                }
            }
        }

        if (posInd.size() > 0) {
            this.x = (int) posInd.get(posInd.size() - 2) + x0;
            this.y = (int) posInd.get(posInd.size() - 1) + y0;
            if(this.x > x1 | this.x < x0) this.catchFun();
            if(this.y > y1 | this.y < y0) this.catchFun();
            this.alreadyOnNew = true; // Arvottu paikka ei koskaan ole uusi
        } else {
            this.catchFun(); // Tähän myöhemmin pelin lopetus, kaikki laskut on suoritettu alle targetTimen
        }
    }

    // ---> setRandomxAndy AND ---> crawlIt.
    private void setRandomxAndy() {
        // this.sp.playSound(3);
        List xList = this.getAccessablePositions("x");
        List yList = this.getAccessablePositions("y");
        if(xList.size() == 0 | yList.size() == 0) { // Jos ei onnistu löytää yhtään jo laskettua laskua, joka on vielä yli targetin, niin valitaan lasku, joka on pienin vielä suorittamaton
            this.setSmallestNewAsxAndy();
        } else {
            int xPos = this.randomWithRange(0, xList.size()-1);
            int yPos = this.randomWithRange(0, yList.size()-1);

            if((int) xList.get(xPos) + x0 == this.x  & (int) yList.get(yPos) + y0 == this.y) this.catchFun();
            this.x = (int) xList.get(xPos) + x0;
            this.y = (int) yList.get(yPos) + y0;
            if(this.x > x1 | this.x < x0) this.catchFun();
            if(this.y > y1 | this.y < y0) this.catchFun();
            this.alreadyOnNew = false; // Arvottu paikka ei koskaan ole uusi
        }
    }

    // ---> setRandomxAndy
    private void setCrawlxAndyByIndex(int index, int up, int down, int left, int right) { // Tämä pois
        boolean cond = true;
        int xx = this.x;
        int yy = this.y;
        int n = 0;
        while (cond) {
            n = n + 1;
            switch (index) { // Asetetaan uusi arvo, joka vastaa matalinta arvoa (joka suurempi kuin target value)
                case 0:
                    this.setRandomxAndy();
                    break;
                case 1:
                    this.x = up+x0;
                    break;
                case 2:
                    this.x = down+x0;
                    break;
                case 3:
                    this.y = left+y0;
                    break;
                case 4:
                    this.y = right+y0;
                    break;
            }
            if(this.y != yy | this.x != xx) cond = false; // Näin pitäisi aina käydä?
            if(cond) {
                int zz = 1; // Näin ei ikinä pitäisi käydä?
            }
            if(this.xLen == 1 & this.yLen == 1) cond = false; // Vain yksi solu, erikoistapaus..
            index = this.randomWithRange(0, 4);
        }
    }

    private int getxPos() { return this.x - x0; }
    private int getyPos() { return this.y - y0; }

    private int howManyDirections() { // Kuinka moneen suuntaan voidaan mennä
        int dirCount = 2;
        if(getxPos() > 0 & getxPos() < (x1-1)) dirCount = dirCount + 1;
        if(getyPos() > 0 & getxPos() < (y1-1)) dirCount = dirCount + 1;
        return dirCount;
    }

    private void setCrawlxy(List indLargest, int up, int down, int left, int right) {
        int ind = this.randomWithRange(0, indLargest.size()-1);
        int dir = (int) indLargest.get(ind);

        switch (dir) { // Asetetaan uusi arvo, joka vastaa korkeinta arvoa neljästä suunnasta
            case 0:
                this.x = up + this.x;
                break;
            case 1:
                this.x = down + this.x;
                break;
            case 2:
                this.y = left + this.y;
                break;
            case 3:
                this.y = right + this.y;
                break;
        }
        // if(this.xLen == 1 & this.yLen == 1) cond = false; // <--------- EI TARVITSE TEHDÄ MITÄÄN
    }

    private void crawlIt() {

        int up = Math.max(-1 , 0); // int up = Math.max(getxPos() -1 , 0);
        int down = Math.max(1 , 0); // int down = Math.min(getxPos() + 1, xLen - 1);
        int left = Math.max(0, -1);  // int left = Math.max(0, getyPos() - 1);
        int right = Math.max(0, 1);  // int right = Math.min(yLen - 1, getyPos() + 1);

        // Eri suunnat kentällä
        int[] moveVector = new int[4];
        int thisX = getxPos()+x0;
        int thisY = getyPos()+y0;
        moveVector[0] = this.answerTimeMatrix[up+this.x][thisY]*up; // Jos up == 0 (kuten ylärivillä), niin tämä saa 0-arvon, eikä tule "largest-listaan"
        moveVector[1] = this.answerTimeMatrix[down+this.x][thisY]*down;
        moveVector[2] = this.answerTimeMatrix[thisX][left+this.y]*left;
        moveVector[3] = this.answerTimeMatrix[thisX][right+this.y]*right;

        List indLargest = this.getIndecesOfLargest(moveVector); // Etsitään missä suunnassa on suurin arvo (voi olla vielä suorittamaton ruutu)

        if ((indLargest.size() > 0) & (moveVector[(int) indLargest.get(0)] > this.answerTargetTime)) { // Löytyi crawl-positio, pitää olla pienempi kuin answerTargetTime
            this.setCrawlxy(indLargest, up, down, left, right);
            this.alreadyOnNew = true;

        } else {
            this.setRandomxAndy();
        }
    }



/*            this.setCrawlxAndyByIndex((int) indLargest.get(movePos), up, down, left, right); // Arvotaan paikka, jos useampia vaihtoehtoja

        } else { // Ei löytynyt mitään uutta kertolaskuparia ---> etsitään parhaiten aikaisemmin osattu
            List indSmallest = this.getIndecesOfSmallest(moveVector);
            if(indSmallest.size() == 0) this.catchFun();
            int movePos = this.getRandomInt(0, indSmallest.size()-1);
            this.setCrawlxAndyByIndex(movePos, up, down, left, right);
        }

        if(this.x > x1 | this.x < x0) this.catchFun();
        if(this.y > y1 | this.y < y0) this.catchFun();

        this.alreadyOnNew = this.onNew();
    }
 */

    // ---> setRandomxAndy AND ---> crawlIt
    private void setNewxAndy() {
        if(this.alreadyOnNew) { // Oltiin edellisellä kierroksella jo uuden päällä
            if (this.answerTime > this.answerTargetTime) { // ja aikaa kului liikaa -> arvotaan
                this.setRandomxAndy();
            } else {
                this.crawlIt(); // Crawl voi päätyä uuteen
            }
        } else {
            this.crawlIt(); // Crawl voi päätyä uuteen
        }
        if(this.x > x1 | this.x < x0) this.catchFun();
        if(this.y > y1 | this.y < y0) this.catchFun();

    }

    private boolean onNew() {
        return this.answerTimeMatrix[this.x-x0][this.y-y0] == this.initialAnswerTime;
        // return isOnNew;
    }

    private List getIndecesOfSmallest(int[] array ) {
        List<Integer> smallest = new ArrayList<Integer>();
        if ( array == null || array.length == 0 ) return smallest; // null or empty

        smallest.add(0); // @0 index; Pienin indeksi ehdotus on ensin 0, testataan sen jälkeen löytyykö pienempää?
        for ( int i = 1; i < array.length; i++ ) {
            if ( array[i] <= array[smallest.get(smallest.size()-1)] & array[i] > this.answerTargetTime) smallest.add(i); // Mennään pienimpään, jos ei uutta vieressä, ei kuitenkaan jo suoritettuun
        }
        return smallest; // position of the first largest found
    }

    private List getIndecesOfLargest(int[] array ) {
        List<Integer> largest = new ArrayList<Integer>();
        if ( array == null || array.length == 0 ) return largest; // null or empty

        largest.add(0); // @0 index; Suurin indeksi ehdotus on ensin 0, testataan sen jälkeen löytyykö pienempää?
        for ( int i = 1; i < array.length; i++ ) {
            // int largestValue = this.lowestAnswerTimeMatrix [arrayPos[largest.get(largest.size()-1)][0]][arrayPos[largest.get(largest.size()-1)][1]];
            if ( array[i] >= array[largest.size()-1] ) largest.add(i); // Mennään pienimpään, jos ei uutta vieressä, ei kuitenkaan jo suoritettuun
        }
        int biggest = array[largest.get(largest.size()-1)];
        return indexOfAll(biggest, array); // Palauttaa kaikki suurimmat indeksit
    }

    private static ArrayList<Integer> indexOfAll(int biggest, int[] array){
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < array.length; i++)
            if(biggest == array[i])
                indexList.add(i);
        return indexList;
    }

    public static <T> boolean contains(final T[] array, final T v) {
        if (v == null) {
            for (final T e : array)
                if (e == null)
                    return true;
        } else {
            for (final T e : array)
                if (e == v || v.equals(e))
                    return true;
        }

        return false;
    }

    public String getxTimesYStr() { // Taman pitaa olla public
        return String.valueOf(this.x) + " x " + String.valueOf(this.y);
    }

    private boolean identical(Integer[] a) {
        boolean ret = false;
        if (a.length != 1) {
            for (int i = 0; i < a.length; i++) {
                for (int k = i + 1; k < a.length; k++) {
                    if (a[i] != null & a[k] != null) {
                        if (a[i].equals(a[k])) {
                            ret = true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private void initializeMatrix(int[][] matrix, int value) {
        for (int i = 0; i < matrix.length; i++) {
            for (int k = 0; k < matrix[0].length; k++) {
                matrix[i][k] = (int) value;
            }
        }
    }

    public Integer getCorrectAnswer() {
        return this.x * this.y;
    }

    public void evaluateAnswer() {
        long answerEndTime = System.currentTimeMillis();
        this.answerTime = (answerEndTime - this.answerStartTime);
        if(this.answer.equals(this.getCorrectAnswer())) {
            this.evaluateCorrectAnswer();
        } else { // Vaara vastaus
            this.evaluateWrongAnswer();
        }
        this.setPoints();
        this.setLowestAnswerTimeMatrix(); // Käyttää this.answerTime-muuttujaa, johon ylempänä sijoitettu adjusteerattu aika, esim. virheellinen vastaus -> aikaa kasvatettu
        this.checkEndCondition();
    }
    private void setLowestAnswerTimeMatrix() {
        this.lowestAnswerTimeMatrix[this.x-x0][this.y-y0] = Math.min(this.lowestAnswerTimeMatrix[this.x-x0][this.y-y0], (int) this.answerTime);
    }
    private void evaluateCorrectAnswer() {
        int prevAnswerTime = this.answerTimeMatrix[this.x-x0][this.y-y0];
        this.answerTime = (int) Math.min((this.answerTime + prevAnswerTime) / 2, this.initialAnswerTime);
        this.answerTimeMatrix[this.x-x0][this.y-y0] = (int) this.answerTime;
        if(this.answerTime > this.answerTargetTime) this.sp.playSound(1);
        if(this.answerTime <= this.answerTargetTime) this.sp.playSound(3);
    }
    private void evaluateWrongAnswer() {
        this.sp.playSound(2);
        this.answerTime = this.answerTime + this.initialAnswerTime;
        this.answerTime = (int) Math.min(this.answerTime, this.initialAnswerTime);
        this.answerTimeMatrix[this.x-x0][this.y-y0] = (int) this.answerTime;
    }

    private void setPoints() {
        // if(this.answer.equals(this.getCorrectAnswer())) { // Lisää pisteitä vain jos vastaus on oikea
            this.previousPoints = this.points;
            // Pisteitä saa siitä paljonko paransi edellistä noopeinta aikaa, heikompi aika ei kuitenkaan vähennä pisteitä
            double lowestAnswerTime = this.lowestAnswerTimeMatrix[this.x-x0][this.y-y0];
            double timeDiff =  Math.max(0, (lowestAnswerTime - this.answerTime)); // Nolla tarvitaan kun seuraavaksi nostetaan toiseen potenssiin.
            double mult = 100 - Math.abs(49 - this.y*this.x)*2;
            this.points = this.points +  Math.pow(timeDiff, 2)  / Math.pow(this.initialAnswerTime, 2) * mult; // 100 pistettä on maksimi --> 10000 koko pelistä <--- tähän voisi laittaa vielä vaikeuskertoimen mukaisen kertoimen 100 tilalle?
            this.points = Math.max(this.points, this.previousPoints);
            if(this.answerTime <= this.answerTargetTime) this.points = this.points + 10; // Muualle täytyy tehdä lisäys, että tän voi saada vain kerran
        // }
    }

    public void setxAndyLen() {
        this.xLen = x1-x0+1;
        this.yLen = y1-y0+1;
    }

    public void newRound(boolean notFirstRound) {
        this.answerStartTime = System.currentTimeMillis();
        if(notFirstRound != true) { this.x = x0; this.y = y0; }
        if(notFirstRound == true) { this.setNewxAndy(); }
    }

    public void checkEndCondition() {
        List<Integer> ret = new ArrayList<Integer>();
        boolean endIt = true;
        for (int i = 0; i < xLen; i++) {
            for (int k = 0; k < yLen; k++) {
                if(this.answerTimeMatrix[i][k] > this.answerTargetTime) { // Ovatko kaikki vastaukset tavoitetta nopeammat?
                    endIt = false;
                    break;
                }
            }
        }
        if(endIt) {
            sp.playSound(4);
            this.initializeMatrix(answerTimeMatrix, initialAnswerTime);
        }
    }

    public void setSoundPlayer(Activity act) {
        sp = new SoundPlayer(act);
    }

}